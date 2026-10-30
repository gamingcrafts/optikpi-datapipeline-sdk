"""
Tests for the stored-record validation (mirrors js/src/__tests__/storageValidation.test.js)
"""
import math
import sys
from datetime import datetime, timezone
from pathlib import Path

import pytest

# Add src to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from models.bigquery_validation import check_type, validate_storage
from models.AccountEvent import AccountEvent
from models.DepositEvent import DepositEvent
from models.WithdrawEvent import WithdrawEvent
from models.GamingActivityEvent import GamingActivityEvent
from models.WalletBalanceEvent import WalletBalanceEvent
from models.ReferFriendEvent import ReferFriendEvent
from models.CustomerProfile import CustomerProfile
from models.CustomerExtEvent import CustomerExtEvent


def now_iso() -> str:
    return datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")


# Every STORED / REJECTED value measured on the dev BigQuery probe (spec, "Measured acceptance"), plus the
# deliberate rejections of junk BigQuery would store ("abc" as NUMERIC, "yes" as BOOLEAN). [type, value, accepted]
MEASURED_CASES = [
    # STRING
    ("STRING", "abc", True),
    ("STRING", "", True),
    ("STRING", 15466, False),
    ("STRING", 1.5, False),
    ("STRING", True, False),
    ("STRING", {}, False),
    ("STRING", [], False),
    # NUMERIC
    ("NUMERIC", 10, True),
    ("NUMERIC", 10.5, True),
    ("NUMERIC", -3, True),
    ("NUMERIC", "10.5", True),
    ("NUMERIC", "-10", True),
    ("NUMERIC", "1e5", True),
    ("NUMERIC", " 10", True),
    ("NUMERIC", "10 ", True),
    ("NUMERIC", "+10", True),
    ("NUMERIC", ".5", True),
    ("NUMERIC", "10.", True),
    ("NUMERIC", "abc", False),  # deliberate: BigQuery stores garbage
    ("NUMERIC", "", False),
    ("NUMERIC", True, False),
    ("NUMERIC", "1,000", False),
    ("NUMERIC", "\u00a010", False),  # NBSP: padding is ASCII whitespace only
    ("NUMERIC", "10\u2003", False),  # Unicode em space
    ("NUMERIC", "\t10\n", True),
    ("NUMERIC", 10**400, True),  # too big for a float: must not raise OverflowError
    ("NUMERIC", math.inf, False),
    ("NUMERIC", math.nan, False),
    # INTEGER
    ("INTEGER", 3, True),
    ("INTEGER", 3.0, True),
    ("INTEGER", "3", True),
    ("INTEGER", "+3", True),
    ("INTEGER", 3.5, False),
    ("INTEGER", "3.5", False),
    ("INTEGER", "", False),
    ("INTEGER", True, False),
    ("INTEGER", " 3", False),
    ("INTEGER", "3e2", False),
    ("INTEGER", 10**400, True),  # int64 range not measured, so no range rule
    # BOOLEAN
    ("BOOLEAN", True, True),
    ("BOOLEAN", False, True),
    ("BOOLEAN", "true", True),
    ("BOOLEAN", "TRUE", True),
    ("BOOLEAN", "True", True),
    ("BOOLEAN", "false", True),
    ("BOOLEAN", 1, True),
    ("BOOLEAN", 0, True),
    ("BOOLEAN", "1", True),
    ("BOOLEAN", "0", True),
    ("BOOLEAN", "yes", False),  # deliberate: BigQuery stores it as true
    ("BOOLEAN", 2, False),
    ("BOOLEAN", "", False),
    # TIMESTAMP
    ("TIMESTAMP", "2026-10-29T12:32:13.562Z", True),
    ("TIMESTAMP", "2026-10-29 12:32:13", True),
    ("TIMESTAMP", "2026-10-29 12:32:13 UTC", True),
    ("TIMESTAMP", "2026-10-29T12:32:13 UTC", True),
    ("TIMESTAMP", "2026-10-29T12:32:13+05:30", True),
    ("TIMESTAMP", "2026-10-29 12:30", True),
    ("TIMESTAMP", "2026-10-29T12:32:13.123456789Z", True),
    ("TIMESTAMP", 1761741133562000, True),
    ("TIMESTAMP", 1761741133, True),
    ("TIMESTAMP", "1761741133562000", True),
    ("TIMESTAMP", "2026-10-29", False),
    ("TIMESTAMP", "2026-10-29T12:32:13 Asia/Kolkata", False),
    ("TIMESTAMP", "2026-10-29T12:32:13+0530", False),
    ("TIMESTAMP", "2026-10-29T12:32:13+05", False),
    ("TIMESTAMP", "2026-1-5 1:2:3", False),
    ("TIMESTAMP", "20261029T123213Z", False),
    ("TIMESTAMP", "Oct 29 2026", False),
    ("TIMESTAMP", "", False),
    ("TIMESTAMP", "   ", False),
    ("TIMESTAMP", "2026-02-30T00:00:00Z", False),
    ("TIMESTAMP", "2026-10-29T24:00:00Z", False),
    ("TIMESTAMP", "2026-10-29T12:32:13 UTC+5", False),
    ("TIMESTAMP", "2026-10-29T12:32:13z", False),
    ("TIMESTAMP", "2026-10-29t12:32:13Z", False),
    ("TIMESTAMP", True, False),
    ("TIMESTAMP", 10**400, True),  # finite number; must not raise OverflowError
    # Not measured: chosen so all six SDKs agree (years 0001-9999, offsets up to +/-18:59)
    ("TIMESTAMP", "0001-01-01T00:00:00Z", True),
    ("TIMESTAMP", "0001-01-01T00:00:00+18:00", True),
    ("TIMESTAMP", "0099-12-31 23:59:59", True),
    ("TIMESTAMP", "9999-12-31T23:59:59-18:00", True),
    ("TIMESTAMP", "0000-01-01T00:00:00Z", False),
    ("TIMESTAMP", "2026-10-29T12:32:13+05:99", False),
    ("TIMESTAMP", "2026-10-29T12:32:13+19:00", False),
    ("TIMESTAMP", "2026-10-29T12:32:13-18:00", True),
    ("TIMESTAMP", "not a date", False),
    ("TIMESTAMP", {}, False),
    # DATE
    ("DATE", "1990-05-17", True),
    ("DATE", "1990-5-7", True),
    ("DATE", 7000, True),
    ("DATE", "7000", True),
    ("DATE", "1990-02-30", False),
    ("DATE", "17/05/1990", False),
    ("DATE", "", False),
    ("DATE", "1990-05-17T00:00:00Z", False),
    ("DATE", "1990-05-17 00:00:00", False),
    ("DATE", True, False),
    ("DATE", "0001-01-01", True),
    ("DATE", "0000-01-01", False),
    # JSON (raw objects/arrays are accepted: the API serialises them before storage)
    ("JSON", '{"a":1}', True),
    ("JSON", "[1,2]", True),
    ("JSON", '"str"', True),
    ("JSON", "null", True),
    ("JSON", {"a": 1}, True),
    ("JSON", [1, 2], True),
    ("JSON", "bad{", False),
    ("JSON", "", False),
    ("JSON", 5, False),
    ("JSON", True, False),
    ("JSON", "[" * 100000 + "]" * 100000, False),  # deeper than the parser handles: error, not RecursionError
]


@pytest.mark.parametrize("type_,value,accepted", MEASURED_CASES)
def test_check_type(type_, value, accepted):
    assert (check_type("f", value, type_) is None) is accepted


def test_check_type_never_raises_on_huge_or_deep_values():
    for type_ in ("STRING", "NUMERIC", "INTEGER", "BOOLEAN", "TIMESTAMP", "DATE", "JSON"):
        for value in (10**400, -10**400, "[" * 100000, "[" * 100000 + "]" * 100000):
            result = check_type("f", value, type_)
            assert result is None or isinstance(result, str)


class TestValidateStorage:
    NOW = datetime(2026, 10, 29, tzinfo=timezone.utc).timestamp() * 1000
    RULES = {"required": ["a", "b"], "types": {"a": "STRING", "n": "NUMERIC", "t": "TIMESTAMP"}, "partition": "t"}

    @pytest.mark.parametrize("record", [{}, {"a": None, "b": None}, {"a": "", "b": ""}], ids=["missing", "null", "empty string"])
    def test_required_fields_rejected(self, record):
        errors = validate_storage(record, self.RULES, self.NOW)
        assert "a is required" in errors
        assert "b is required" in errors

    def test_null_and_absent_optional_fields_accepted(self):
        assert validate_storage({"a": "x", "b": "y", "n": None}, self.RULES, self.NOW) == []

    @pytest.mark.parametrize("t,accepted", [
        ("2016-10-15T00:00:00Z", False),
        ("2016-11-15T00:00:00Z", True),
        ("2027-10-01T00:00:00Z", True),
        ("2027-12-01T00:00:00Z", False),
    ])
    def test_partition_window(self, t, accepted):
        errors = validate_storage({"a": "x", "b": "y", "t": t}, self.RULES, self.NOW)
        assert (len(errors) == 0) is accepted

    def test_partition_window_applies_the_offset(self):
        # The same wall-clock time at the window edge is inside at -02:00 (2 h later in UTC), outside at +02:00.
        edge = datetime.fromtimestamp((self.NOW - 10 * 365.25 * 24 * 60 * 60 * 1000) / 1000, timezone.utc)
        wall = edge.strftime("%Y-%m-%dT%H:%M:%S")
        assert validate_storage({"a": "x", "b": "y", "t": wall + "-02:00"}, self.RULES, self.NOW) == []
        assert validate_storage({"a": "x", "b": "y", "t": wall + "+02:00"}, self.RULES, self.NOW) != []

    @pytest.mark.parametrize("t", [1, 946684800, "946684800000000"])
    def test_numeric_timestamps_skip_the_partition_check(self, t):
        # The unit of an epoch number is guessed by BigQuery, so it cannot be checked against the window.
        assert validate_storage({"a": "x", "b": "y", "t": t}, self.RULES, self.NOW) == []


EVENT = {
    "account_id": "acc",
    "workspace_id": "ws",
    "user_id": "1",
    "event_id": "e1",
    "event_name": "Something",
}

MODELS = [
    (AccountEvent, "Account", {}),
    (DepositEvent, "Deposit", {"amount": 10, "payment_method": "credit_card", "transaction_id": "t1"}),
    (WithdrawEvent, "Withdraw", {"amount": 10, "payment_method": "credit_card", "transaction_id": "t1"}),
    (GamingActivityEvent, "Gaming Activity", {"game_id": "15466", "game_title": "Aviator"}),
    (WalletBalanceEvent, "Wallet Balance", {"currency": "INR", "wallet_type": "main"}),
    (ReferFriendEvent, "Refer Friend", {}),
]
MODEL_IDS = [m[0].__name__ for m in MODELS]


class TestModelsEnforceStoredRecordRules:
    @pytest.mark.parametrize("model,category,extra", MODELS, ids=MODEL_IDS)
    def test_missing_event_category_rejected_and_not_filled_in(self, model, category, extra):
        instance = model(**EVENT, event_time=now_iso(), **extra)
        result = instance.validate()
        assert result["isValid"] is False
        assert "event_category is required" in result["errors"]
        assert "event_category" not in instance.to_dict()

    @pytest.mark.parametrize("model,category,extra", MODELS, ids=MODEL_IDS)
    def test_missing_event_category_rejected_via_from_object(self, model, category, extra):
        result = model.from_object({**EVENT, "event_time": now_iso(), **extra}).validate()
        assert "event_category is required" in result["errors"]

    @pytest.mark.parametrize("model,category,extra", MODELS, ids=MODEL_IDS)
    def test_complete_record_accepted(self, model, category, extra):
        result = model(**EVENT, event_time=now_iso(), event_category=category, **extra).validate()
        assert result == {"isValid": True, "errors": []}

    def test_wrong_event_category_still_rejected(self):
        result = AccountEvent(**EVENT, event_time=now_iso(), event_category="Deposit").validate()
        assert result["errors"] == ['event_category must be "Account" for account events']

    def test_stored_column_type_is_checked(self):
        result = GamingActivityEvent(
            **EVENT, event_time=now_iso(), event_category="Gaming Activity",
            game_id="1", game_title="Aviator", game_category=7,
        ).validate()
        assert "game_category must be a string" in result["errors"]

    def test_clearing_event_category_after_construction_fails_validation(self):
        event = AccountEvent.from_object({**EVENT, "event_time": now_iso(), "event_category": "Account"})
        assert event.validate()["isValid"] is True
        event.event_category = None
        result = event.validate()
        assert result["errors"] == ["event_category is required"]
        assert "event_category" not in event.to_dict()

    def test_deleting_event_category_after_construction_fails_validation(self):
        event = AccountEvent.from_object({**EVENT, "event_time": now_iso(), "event_category": "Account"})
        del event.event_category
        assert event.validate()["errors"] == ["event_category is required"]
        assert "event_category" not in event.to_dict()

    def test_deleting_a_default_factory_field_leaves_it_missing(self):
        # custom_data has a default_factory, so there is no class default to fall back to after del.
        profile = CustomerProfile(
            account_id="acc", workspace_id="ws", user_id="1", username="u", email="u@example.com",
            creation_timestamp=now_iso(),
        )
        del profile.custom_data
        assert profile.validate() == {"isValid": True, "errors": []}

    def test_changing_a_declared_field_after_construction_uses_the_new_value(self):
        event = GamingActivityEvent.from_object({
            **EVENT, "event_time": now_iso(), "event_category": "Gaming Activity",
            "game_id": "1", "game_title": "Aviator",
        })
        event.game_id = 15466
        assert "game_id must be a string" in event.validate()["errors"]

    def test_field_the_model_does_not_declare_is_still_type_checked(self):
        # Fails if the raw-input merge is removed: payment_provider_id exists only in the raw input.
        result = WithdrawEvent.from_object({
            **EVENT, "event_time": now_iso(), "event_category": "Withdraw",
            "amount": 10, "payment_method": "credit_card", "transaction_id": "t1",
            "payment_provider_id": 42,
        }).validate()
        assert result["errors"] == ["payment_provider_id must be a string"]

    def test_raw_input_merge_is_what_catches_undeclared_fields(self):
        # Fails if the raw-input merge is removed: the same record without from_object has no raw input.
        record = {
            **EVENT, "event_time": now_iso(), "event_category": "Withdraw",
            "amount": 10, "payment_method": "credit_card", "transaction_id": "t1",
            "payment_provider_name": 7,
        }
        assert WithdrawEvent.from_object(record).validate()["errors"] == ["payment_provider_name must be a string"]
        declared_only = {k: v for k, v in record.items() if k != "payment_provider_name"}
        assert WithdrawEvent(**declared_only).validate()["isValid"] is True

    def test_numeric_game_id_rejected(self):
        result = GamingActivityEvent(
            **EVENT, event_time=now_iso(), event_category="Gaming Activity",
            game_id=15466, game_title="Aviator",
        ).validate()
        assert "game_id must be a string" in result["errors"]

    def test_event_time_outside_partition_window_rejected(self):
        result = AccountEvent(**EVENT, event_time="2012-01-01T00:00:00Z", event_category="Account").validate()
        assert result["errors"] == ["event_time must be within the last 10 years and no more than 1 year in the future"]

    def test_wallet_payload_rejected_for_missing_event_category(self):
        result = WalletBalanceEvent.from_object({
            "workspace_id": "ws_example_001",
            "blocked_amount": 100.0,
            "account_id": "acc_example_001",
            "current_cash_balance": 3065.5,
            "event_id": "da49a497-a41f-4e37-b7b2-e10ad0c0ed4d",
            "currency": "INR",
            "wallet_type": "main",
            "user_id": "1",
            "current_bonus_balance": 20.0,
            "event_time": now_iso(),
            "event_name": "Balance Update",
            "current_total_balance": 3085.5,
        }).validate()
        assert result["errors"] == ["event_category is required"]

    def test_customer_with_empty_string_timestamp_rejected(self):
        result = CustomerProfile(
            account_id="acc", workspace_id="ws", user_id="1", username="u", email="u@example.com",
            creation_timestamp=now_iso(), self_exclusion_created_time="",
        ).validate()
        assert "self_exclusion_created_time must be a valid ISO 8601 date-time" in result["errors"]

    def test_customer_creation_timestamp_outside_partition_window_rejected(self):
        result = CustomerProfile(
            account_id="acc", workspace_id="ws", user_id="1", username="u", email="u@example.com",
            creation_timestamp="2012-03-01T00:00:00Z",
        ).validate()
        assert result["errors"] == [
            "creation_timestamp must be within the last 10 years and no more than 1 year in the future"
        ]

    def test_customer_ext_data_may_be_object_or_json_string(self):
        base = {"account_id": "acc", "workspace_id": "ws", "user_id": "1", "list_name": "l"}
        assert CustomerExtEvent(**base, ext_data={"a": 1}).validate()["isValid"] is True
        assert CustomerExtEvent(**base, ext_data='{"a":1}').validate()["isValid"] is True
