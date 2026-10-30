"""
Validation of a record against what the warehouse stores for each column type.

A record the API accepts but the warehouse cannot store is lost without an error reaching the caller, so
every model checks its fields against these rules before anything is sent. The rules follow the BigQuery
acceptance measured on a dev probe (development/datapipeline-sdk/bq-validation-spec.md, "Final rules"): a value
BigQuery stores is accepted, except a few values it stores as junk ("abc" as NUMERIC, "yes" as BOOLEAN), which
are rejected on purpose.
"""
import json
import math
import re
import time
from dataclasses import fields as dataclass_fields, is_dataclass
from datetime import date, datetime, timezone
from typing import Any, Dict, List, Optional, Tuple

TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000
ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000

# re.ASCII so \d and \s match only ASCII digits / whitespace ([ \t\n\r\f\v], no NBSP or Unicode spaces);
# patterns are applied with fullmatch.
NUMERIC_STRING = re.compile(r"\s*[+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)?\s*", re.ASCII)
INTEGER_STRING = re.compile(r"[+-]?\d+", re.ASCII)
DIGITS = re.compile(r"\d+", re.ASCII)
DATE_STRING = re.compile(r"(\d{4})-(\d{1,2})-(\d{1,2})", re.ASCII)
TIMESTAMP_STRING = re.compile(
    r"(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(:(\d{2})(\.\d+)?)?(Z|[+-]\d{2}:\d{2}| UTC)?", re.ASCII
)

INVALID = (False, None)
EPOCH = datetime(1970, 1, 1, tzinfo=timezone.utc)


def _is_number(value: Any) -> bool:
    # bool is a subclass of int in Python, but true/false is not a number here.
    return isinstance(value, (int, float)) and not isinstance(value, bool)


def _is_finite(value: Any) -> bool:
    # Python ints are always finite; math.isfinite would raise OverflowError on one too big for a float.
    if isinstance(value, int):
        return True
    return math.isfinite(value)


def _is_integral(value: Any) -> bool:
    if not _is_number(value):
        return False
    return True if isinstance(value, int) else math.isfinite(value) and value.is_integer()


def _reject_constant(name: str) -> Any:
    # NaN / Infinity are not valid JSON.
    raise ValueError(f"invalid JSON constant {name}")


def parse_timestamp(value: Any) -> Tuple[bool, Optional[float]]:
    """
    Returns (valid, ms since the epoch). The time is None for a valid epoch number or digit string, whose unit
    BigQuery guesses, so it cannot be checked against the partition window.
    """
    if _is_number(value):
        return (True, None) if _is_finite(value) else INVALID
    if not isinstance(value, str):
        return INVALID
    if DIGITS.fullmatch(value):
        return True, None
    match = TIMESTAMP_STRING.fullmatch(value)
    if not match:
        return INVALID
    year, month, day, hour, minute = (int(match.group(i)) for i in range(1, 6))
    second = int(match.group(7)) if match.group(7) else 0
    fraction = float(match.group(8)) if match.group(8) else 0.0
    zone = match.group(9)
    if hour >= 24 or minute >= 60 or second >= 60:
        return INVALID
    offset_seconds = 0
    if zone and zone[0] in "+-":
        offset_hours, offset_minutes = int(zone[1:3]), int(zone[4:6])
        if offset_hours > 18 or offset_minutes > 59:
            return INVALID
        offset_seconds = (offset_hours * 3600 + offset_minutes * 60) * (1 if zone[0] == "+" else -1)
    try:
        # Years 0001-9999; year 0000 and impossible dates raise.
        parsed = datetime(year, month, day, hour, minute, second, tzinfo=timezone.utc)
    except ValueError:
        return INVALID
    # Plain arithmetic, so an offset near year 0001 / 9999 cannot overflow datetime.
    seconds = (parsed - EPOCH).total_seconds() - offset_seconds + fraction
    return True, seconds * 1000


def _is_real_date(value: Any) -> bool:
    if _is_integral(value):
        return True
    if not isinstance(value, str):
        return False
    if DIGITS.fullmatch(value):
        return True
    match = DATE_STRING.fullmatch(value)
    if not match:
        return False
    try:
        date(int(match.group(1)), int(match.group(2)), int(match.group(3)))
        return True
    except ValueError:
        return False


def _is_boolean(value: Any) -> bool:
    if isinstance(value, bool):
        return True
    if _is_number(value):
        return value in (0, 1)
    if isinstance(value, str):
        return value.lower() in ("true", "false") or value in ("0", "1")
    return False


def check_type(field: str, value: Any, type_: str) -> Optional[str]:
    """Returns an error message, or None when the value can be stored in a column of this type."""
    if type_ == "STRING":
        return None if isinstance(value, str) else f"{field} must be a string"
    if type_ == "NUMERIC":
        if _is_number(value):
            return None if _is_finite(value) else f"{field} must be a finite number"
        return None if isinstance(value, str) and NUMERIC_STRING.fullmatch(value) else f"{field} must be a number"
    if type_ == "INTEGER":
        if _is_number(value):
            return None if _is_integral(value) else f"{field} must be an integer"
        return None if isinstance(value, str) and INTEGER_STRING.fullmatch(value) else f"{field} must be an integer"
    if type_ == "BOOLEAN":
        return None if _is_boolean(value) else f"{field} must be true or false"
    if type_ == "TIMESTAMP":
        return None if parse_timestamp(value)[0] else f"{field} must be a valid ISO 8601 date-time"
    if type_ == "DATE":
        return None if _is_real_date(value) else f"{field} must be a date in YYYY-MM-DD format"
    if type_ == "JSON":
        # The API serialises objects and arrays to a JSON string before storing them.
        if isinstance(value, (dict, list)):
            return None
        if isinstance(value, str):
            try:
                json.loads(value, parse_constant=_reject_constant)
                return None
            except (ValueError, RecursionError):
                # RecursionError: nesting deeper than the parser can handle.
                return f"{field} must be an object or a valid JSON string"
        return f"{field} must be an object or a valid JSON string"
    return None


def validate_storage(record: Dict[str, Any], rules: Dict[str, Any], now: Optional[float] = None) -> List[str]:
    """
    Args:
        record: The values to check, as they will be sent
        rules: {"required": [...], "types": {field: type}, "partition": field (optional)}
        now: Current time in ms (for tests)

    Returns:
        Error messages; empty when the record can be stored
    """
    if now is None:
        now = time.time() * 1000
    errors: List[str] = []

    for field in rules["required"]:
        value = record.get(field)
        if value is None or value == "":
            errors.append(f"{field} is required")

    for field, type_ in rules["types"].items():
        value = record.get(field)
        if value is None:
            continue
        error = check_type(field, value, type_)
        if error:
            errors.append(error)

    # The table is partitioned on this column and only accepts writes within this window.
    partition = rules.get("partition")
    partition_value = record.get(partition) if partition else None
    if partition_value is not None:
        valid, ms = parse_timestamp(partition_value)
        if valid and ms is not None and (ms < now - TEN_YEARS_MS or ms > now + ONE_YEAR_MS):
            errors.append(f"{partition} must be within the last 10 years and no more than 1 year in the future")

    return errors


def _declared_values(model: Any) -> Dict[str, Any]:
    """The model's current value for every field it declares, None included."""
    if is_dataclass(model):
        # A deleted field (e.g. one with a default_factory, which has no class default) is just missing.
        return {f.name: getattr(model, f.name, None) for f in dataclass_fields(model)}
    return {k: v for k, v in vars(model).items() if not k.startswith("_")}


def merge_storage_errors(errors: List[str], model: Any, rules: Dict[str, Any]) -> None:
    """
    Appends the storage errors for the record as it will be stored, skipping duplicates. Declared fields always
    take the model's current value (so a field cleared after construction is missing); the raw input the model
    was built from contributes only the keys the model does not declare.
    """
    declared = _declared_values(model)
    raw = getattr(model, "_input", None) or {}
    record = {**{k: v for k, v in raw.items() if k not in declared}, **declared}
    for error in validate_storage(record, rules):
        if error not in errors:
            errors.append(error)


def model_from_raw(cls, data: Dict[str, Any]):
    """
    Builds a model from a raw record, keeping the record so validation also covers fields the model does
    not declare (the API checks the record as the caller sent it).
    """
    names = {f.name for f in dataclass_fields(cls)}
    instance = cls(**{k: v for k, v in data.items() if k in names})
    instance._input = dict(data)
    return instance
