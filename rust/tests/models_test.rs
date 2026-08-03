use optikpi_datapipeline_sdk::models::{AccountEvent, CustomerExtEvent, DepositEvent, SystemEvent};
use serde_json::json;

#[test]
fn deposit_event_requires_positive_amount() {
    let event = DepositEvent::new(
        "acc-1",
        "ws-1",
        "user-1",
        "Successful Deposit",
        "evt-1",
        "2026-01-15T10:30:00Z",
        -10.0,
        "credit_card",
        "txn-1",
    );
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("amount")));
}

#[test]
fn deposit_event_valid_passes() {
    let event = DepositEvent::new(
        "acc-1",
        "ws-1",
        "user-1",
        "Successful Deposit",
        "evt-1",
        "2026-01-15T10:30:00Z",
        100.0,
        "credit_card",
        "txn-1",
    );
    assert!(event.validate().is_valid());
}

#[test]
fn account_event_rejects_malformed_timestamp() {
    let event = AccountEvent::new("acc-1", "ws-1", "user-1", "Login", "evt-1", "not-a-date");
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("event_time")));
}

#[test]
fn account_event_serializes_without_none_fields() {
    let event = AccountEvent::new("acc-1", "ws-1", "user-1", "Login", "evt-1", "2026-01-15T10:30:00Z");
    let value = serde_json::to_value(&event).unwrap();
    assert!(value.get("device").is_none());
    assert_eq!(value.get("event_category").unwrap(), "Account");
}

#[test]
fn system_event_requires_event_data() {
    let event = SystemEvent::new("acc-1", "ws-1", "Campaign Trigger", "evt-1", "2026-01-15T10:30:00Z", serde_json::Value::Null);
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("event_data")));
}

#[test]
fn system_event_accepts_object_payload() {
    let event = SystemEvent::new(
        "acc-1",
        "ws-1",
        "Campaign Trigger",
        "evt-1",
        "2026-01-15T10:30:00Z",
        json!({"campaign_id": "camp_001", "action": "start"}),
    );
    assert!(event.validate().is_valid());
}

#[test]
fn customer_ext_event_serializes_object_as_json_string() {
    let event = CustomerExtEvent::new("acc-1", "ws-1", "user-1", "BINGO_PREFERENCES", json!({"Email": true}));
    assert!(event.validate().is_valid());
    let value = serde_json::to_value(&event).unwrap();
    assert_eq!(value.get("ext_data").unwrap(), &json!(r#"{"Email":true}"#));
}
