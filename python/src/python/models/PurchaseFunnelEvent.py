"""
Purchase Funnel Event Model

One step of a purchase attempt (a funnel session). Every event of one attempt shares a
funnel_session_id. Its meaning comes from funnel_stage (a fixed list); event_name is the
operator's own free text.
"""
from dataclasses import dataclass, asdict, fields
from typing import Optional, Dict, Any, List
from datetime import datetime, timezone
import math
import re
import time

# Allowed funnel_stage values, in funnel order.
FUNNEL_STAGES = (
    "start",
    "browse",
    "configure",
    "cart",
    "checkout",
    "payment",
    "payment_failed",
    "complete",
    "failed",
    "cancelled",
    "exit",
    "stalled",
    "abandoned",
)

# Allowed failure_reason values.
FAILURE_REASONS = (
    "payment_declined",
    "insufficient_funds",
    "provider_timeout",
    "limit_reached",
    "kyc_required",
    "geo_blocked",
    "draw_closed",
    "technical_error",
    "user_cancelled",
)

EVENT_CATEGORY = "Purchase Funnel"

REQUIRED_FIELDS = (
    "account_id",
    "workspace_id",
    "user_id",
    "event_category",
    "event_name",
    "event_id",
    "event_time",
    "funnel_session_id",
    "funnel_stage",
)

_MONEY_FIELDS = (
    "jackpot_amount", "unit_price", "item_amount", "cart_subtotal", "discount_amount",
    "bonus_used_amount", "fees_amount", "tax_amount", "cart_total", "order_total",
)
_COUNT_FIELDS = (
    "lines_count", "multi_draw_count", "item_quantity", "cart_item_count", "cart_quantity_total",
    "payment_attempt_number", "inactivity_sec",
)
_BOOLEAN_FIELDS = ("is_saved_method", "is_first_purchase", "is_resumed")
_DATETIME_FIELDS = ("event_time", "draw_date", "draw_close_time")

# Same window the event store accepts on its partition column.
_TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000
_ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000

_DATETIME_PATTERN = re.compile(
    r"^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2})(\.\d{1,9})?)?(Z|[+-]\d{2}:\d{2})?$"
)
_CURRENCY_PATTERN = re.compile(r"^[A-Z]{3}$")


def parse_datetime_ms(value: str) -> Optional[float]:
    """Milliseconds since the epoch for an ISO 8601 date-time string, or None when it is not one.
    A string without a zone is read as UTC."""
    match = _DATETIME_PATTERN.fullmatch(value)
    if not match:
        return None
    year, month, day, hour, minute = (int(part) for part in match.group(1, 2, 3, 4, 5))
    second = int(match.group(6) or 0)
    try:
        moment = datetime(year, month, day, hour, minute, second, tzinfo=timezone.utc)
    except ValueError:
        return None
    millis = moment.timestamp() * 1000
    zone = match.group(8)
    if zone and zone != "Z":
        offset_hours, offset_minutes = int(zone[1:3]), int(zone[4:6])
        if offset_hours > 18 or offset_minutes > 59:
            return None
        sign = -1 if zone[0] == "-" else 1
        millis -= sign * (offset_hours * 60 + offset_minutes) * 60 * 1000
    return millis


def _kind_error(field: str, value: Any) -> Optional[str]:
    """An error message, or None when the value has the field's kind."""
    if field in _MONEY_FIELDS:
        # bool is an int in Python; JSON true is not a number.
        ok = isinstance(value, (int, float)) and not isinstance(value, bool) and math.isfinite(value)
        return None if ok else f"{field} must be a finite number"
    if field in _COUNT_FIELDS:
        ok = (isinstance(value, int) and not isinstance(value, bool)) or (
            isinstance(value, float) and value.is_integer()
        )
        return None if ok else f"{field} must be an integer"
    if field in _BOOLEAN_FIELDS:
        return None if isinstance(value, bool) else f"{field} must be true or false"
    if field in _DATETIME_FIELDS:
        ok = isinstance(value, str) and parse_datetime_ms(value) is not None
        return None if ok else (
            f"{field} must be an ISO 8601 date-time (YYYY-MM-DDTHH:mm[:ss[.fff]][Z|+hh:mm])"
        )
    return None if isinstance(value, str) else f"{field} must be a string"


@dataclass
class PurchaseFunnelEvent:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    event_category: Optional[str] = None
    event_name: Optional[str] = None
    funnel_session_id: Optional[str] = None
    funnel_stage: Optional[str] = None
    # Item
    item_id: Optional[str] = None
    item_name: Optional[str] = None
    item_category: Optional[str] = None
    draw_id: Optional[str] = None
    draw_number: Optional[str] = None
    draw_date: Optional[str] = None
    draw_close_time: Optional[str] = None
    jackpot_amount: Optional[float] = None
    selection: Optional[str] = None
    selection_method: Optional[str] = None
    lines_count: Optional[int] = None
    multi_draw_count: Optional[int] = None
    unit_price: Optional[float] = None
    item_quantity: Optional[int] = None
    item_amount: Optional[float] = None
    # Cart snapshot
    cart_id: Optional[str] = None
    cart_item_count: Optional[int] = None
    cart_quantity_total: Optional[int] = None
    cart_items: Optional[str] = None
    cart_subtotal: Optional[float] = None
    discount_amount: Optional[float] = None
    bonus_used_amount: Optional[float] = None
    fees_amount: Optional[float] = None
    tax_amount: Optional[float] = None
    cart_total: Optional[float] = None
    coupon_code: Optional[str] = None
    currency: Optional[str] = None
    # Payment
    payment_method: Optional[str] = None
    payment_provider_id: Optional[str] = None
    payment_provider_name: Optional[str] = None
    is_saved_method: Optional[bool] = None
    funds_source: Optional[str] = None
    payment_attempt_number: Optional[int] = None
    payment_status: Optional[str] = None
    transaction_id: Optional[str] = None
    failure_reason: Optional[str] = None
    failure_code: Optional[str] = None
    failure_message: Optional[str] = None
    # Order
    order_id: Optional[str] = None
    ticket_ids: Optional[str] = None
    order_total: Optional[float] = None
    order_status: Optional[str] = None
    is_first_purchase: Optional[bool] = None
    # Context
    device: Optional[str] = None
    platform: Optional[str] = None
    app_version: Optional[str] = None
    page_url: Optional[str] = None
    referrer: Optional[str] = None
    utm_source: Optional[str] = None
    utm_medium: Optional[str] = None
    utm_campaign: Optional[str] = None
    campaign_id: Optional[str] = None
    affiliate_id: Optional[str] = None
    entry_point: Optional[str] = None
    is_resumed: Optional[bool] = None
    # Set by OptikPI on derived events
    abandoned_at_stage: Optional[str] = None
    inactivity_sec: Optional[int] = None

    FUNNEL_STAGES = FUNNEL_STAGES
    FAILURE_REASONS = FAILURE_REASONS

    # -------------------------------
    # Validation methods
    # -------------------------------
    def validate(self, now_ms: Optional[float] = None) -> Dict[str, Any]:
        """Validate the event. now_ms (milliseconds since the epoch) is for tests; it defaults to now."""
        errors: List[str] = []

        for field in fields(self):
            value = getattr(self, field.name)
            if value is None or value == "":
                if field.name in REQUIRED_FIELDS:
                    errors.append(f"{field.name} is required")
                    continue
                # An empty optional value is still checked: '' is a string, but not a number or a date-time.
                if value is None:
                    continue
            error = _kind_error(field.name, value)
            if error:
                errors.append(error)

        if isinstance(self.event_category, str) and self.event_category and self.event_category != EVENT_CATEGORY:
            errors.append(f'event_category must be "{EVENT_CATEGORY}" for purchase funnel events')

        if isinstance(self.funnel_stage, str) and self.funnel_stage and self.funnel_stage not in FUNNEL_STAGES:
            errors.append(f"funnel_stage must be one of: {', '.join(FUNNEL_STAGES)}")

        if isinstance(self.failure_reason, str) and self.failure_reason not in FAILURE_REASONS:
            errors.append(f"failure_reason must be one of: {', '.join(FAILURE_REASONS)}")

        if isinstance(self.currency, str) and not _CURRENCY_PATTERN.fullmatch(self.currency):
            errors.append("currency must be a valid 3-letter ISO currency code")

        if isinstance(self.event_time, str):
            event_ms = parse_datetime_ms(self.event_time)
            now = time.time() * 1000 if now_ms is None else now_ms
            if event_ms is not None and (event_ms < now - _TEN_YEARS_MS or event_ms > now + _ONE_YEAR_MS):
                errors.append("event_time must be within the last 10 years and no more than 1 year in the future")

        return {"isValid": len(errors) == 0, "errors": errors}

    # -------------------------------
    # Utility methods
    # -------------------------------
    def to_dict(self) -> Dict[str, Any]:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> "PurchaseFunnelEvent":
        """Build from a dict. Unknown keys are ignored, as the API accepts and later drops them."""
        known = {field.name for field in fields(cls)}
        return cls(**{key: value for key, value in data.items() if key in known})

    def __repr__(self) -> str:
        return (
            f"PurchaseFunnelEvent(event_id={self.event_id}, funnel_session_id={self.funnel_session_id}, "
            f"funnel_stage={self.funnel_stage}, user_id={self.user_id})"
        )
