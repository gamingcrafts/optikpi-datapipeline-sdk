import sys
from pathlib import Path

import pytest

sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from models.AccountEvent import AccountEvent
from models.DepositEvent import DepositEvent
from models.WithdrawEvent import WithdrawEvent
from models.GamingActivityEvent import GamingActivityEvent
from models.WalletBalanceEvent import WalletBalanceEvent
from models.ReferFriendEvent import ReferFriendEvent

BASE = {
    "account_id": "acc_example_001",
    "workspace_id": "ws_example_001",
    "user_id": "1",
    "event_id": "e1",
    "event_name": "Something",
    "event_time": "2026-01-15T10:30:00Z",
}

MODELS = [
    (AccountEvent, "Account"),
    (DepositEvent, "Deposit"),
    (WithdrawEvent, "Withdraw"),
    (GamingActivityEvent, "Gaming Activity"),
    (WalletBalanceEvent, "Wallet Balance"),
    (ReferFriendEvent, "Refer Friend"),
]


@pytest.mark.parametrize("model,category", MODELS)
def test_missing_category_is_rejected_and_not_filled_in(model, category):
    event = model.from_object(dict(BASE))
    assert "event_category is required" in event.validate()["errors"]
    assert event.event_category is None


@pytest.mark.parametrize("model,category", MODELS)
def test_own_category_is_accepted(model, category):
    event = model.from_object({**BASE, "event_category": category})
    assert "event_category is required" not in event.validate()["errors"]


@pytest.mark.parametrize("model,category", MODELS)
def test_wrong_category_is_rejected(model, category):
    event = model.from_object({**BASE, "event_category": "Other"})
    assert event.validate()["isValid"] is False
