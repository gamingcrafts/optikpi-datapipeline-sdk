use optikpi_datapipeline_sdk::models::DepositEvent;

#[test]
fn deposit_event_valid_passes() {
    let event = DepositEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Deposit", "evt-1",
        "2026-01-15T10:30:00Z", 100.0, "credit_card", "txn-1",
    );
    assert!(event.validate().is_valid());
}

#[test]
fn deposit_event_requires_positive_amount() {
    let event = DepositEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Deposit", "evt-1",
        "2026-01-15T10:30:00Z", -10.0, "credit_card", "txn-1",
    );
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("amount")));
}

#[test]
fn deposit_event_zero_amount_fails() {
    let event = DepositEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Deposit", "evt-1",
        "2026-01-15T10:30:00Z", 0.0, "credit_card", "txn-1",
    );
    assert!(!event.validate().is_valid());
}

#[test]
fn deposit_event_empty_payment_method_is_optional() {
    let event = DepositEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Deposit", "evt-1",
        "2026-01-15T10:30:00Z", 50.0, "", "txn-1",
    );
    assert!(event.validate().is_valid());
}

#[test]
fn deposit_event_invalid_timestamp_fails() {
    let event = DepositEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Deposit", "evt-1",
        "15-01-2026", 50.0, "bank", "txn-1",
    );
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("event_time")));
}

#[test]
fn deposit_event_sets_correct_event_category() {
    let event = DepositEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Deposit", "evt-1",
        "2026-01-15T10:30:00Z", 100.0, "bank", "txn-1",
    );
    let value = serde_json::to_value(&event).unwrap();
    assert_eq!(value.get("event_category").unwrap(), "Deposit");
}
