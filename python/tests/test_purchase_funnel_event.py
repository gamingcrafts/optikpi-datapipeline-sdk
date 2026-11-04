import copy
import json
import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path
from unittest.mock import Mock, patch

import pytest

sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from core.DataPipelineClient import DataPipelineClient
from models.PurchaseFunnelEvent import FAILURE_REASONS, FUNNEL_STAGES, PurchaseFunnelEvent

# Shared with the API's tests: the client and the server must agree on every shape.
SHAPES = json.loads(
    (Path(__file__).parent.parent.parent / "testdata" / "purchase-funnel-shapes.json").read_text(encoding="utf-8")
)
NOW_MS = datetime(2026, 11, 4, 10, 0, 0, tzinfo=timezone.utc).timestamp() * 1000


def build_record(case):
    record = copy.deepcopy(SHAPES["full"] if case.get("from") == "full" else SHAPES["base"])
    for field in case.get("unset", []):
        record.pop(field, None)
    record.update(copy.deepcopy(case.get("set", {})))
    return record


@pytest.mark.parametrize("case", SHAPES["cases"], ids=[case["name"] for case in SHAPES["cases"]])
def test_validate_matches_shared_shapes(case):
    result = PurchaseFunnelEvent.from_object(build_record(case)).validate(now_ms=NOW_MS)
    assert result["isValid"] is case["valid"], result["errors"]
    if not case["valid"]:
        assert any(case["errorField"] in message for message in result["errors"]), result["errors"]


def test_window_defaults_to_now():
    recent = (datetime.now(timezone.utc) - timedelta(minutes=1)).strftime("%Y-%m-%dT%H:%M:%SZ")
    assert PurchaseFunnelEvent.from_object({**SHAPES["base"], "event_time": recent}).validate()["isValid"] is True
    old = (datetime.now(timezone.utc) - timedelta(days=11 * 365)).strftime("%Y-%m-%dT%H:%M:%SZ")
    assert PurchaseFunnelEvent.from_object({**SHAPES["base"], "event_time": old}).validate()["isValid"] is False


def test_event_category_is_not_filled_in():
    record = dict(SHAPES["base"])
    del record["event_category"]
    assert "event_category" not in PurchaseFunnelEvent.from_object(record).to_dict()


def test_constants_in_contract_order():
    assert FUNNEL_STAGES == (
        "start", "browse", "configure", "cart", "checkout", "payment", "payment_failed", "complete",
        "failed", "cancelled", "exit", "stalled", "abandoned",
    )
    assert FAILURE_REASONS == (
        "payment_declined", "insufficient_funds", "provider_timeout", "limit_reached", "kyc_required",
        "geo_blocked", "draw_closed", "technical_error", "user_cancelled",
    )


@pytest.fixture
def client():
    return DataPipelineClient({
        "authToken": "test-token",
        "accountId": "acc_example_001",
        "workspaceId": "ws_example_001",
        "baseURL": "https://test.api.com",
    })


def ok_response():
    response = Mock()
    response.ok = True
    response.status_code = 200
    response.json.return_value = {"message": "ok"}
    return response


def test_send_purchase_funnel_event_posts_to_endpoint(client):
    with patch.object(client.session, "request", return_value=ok_response()) as request:
        result = client.send_purchase_funnel_event(SHAPES["base"])
    assert result["success"] is True
    assert request.call_args.kwargs["method"] == "POST"
    assert request.call_args.kwargs["url"] == "https://test.api.com/events/purchase-funnel"


def test_send_batch_sends_purchase_funnel_events(client):
    with patch.object(client.session, "request", return_value=ok_response()) as request:
        result = client.send_batch({"purchaseFunnelEvents": [SHAPES["base"]]})
    assert request.call_count == 1
    assert request.call_args.kwargs["url"] == "https://test.api.com/events/purchase-funnel"
    assert result["results"]["purchaseFunnelEvents"]["success"] is True
