use optikpi_datapipeline_sdk::models::WithdrawEvent;

fn valid_withdraw() -> WithdrawEvent {
    WithdrawEvent::new(
        "acc-1", "ws-1", "user-1", "Successful Withdrawal", "evt-1",
        "2026-01-15T10:30:00Z", 200.0, "bank", "txn-1",
    )
}

#[test]
fn withdraw_event_valid_passes() {
    assert!(valid_withdraw().validate().is_valid());
}

#[test]
fn withdraw_event_negative_amount_fails() {
    let event = WithdrawEvent::new(
        "acc-1", "ws-1", "user-1", "Withdrawal", "evt-1",
        "2026-01-15T10:30:00Z", -50.0, "bank", "txn-1",
    );
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("amount")));
}

#[test]
fn withdraw_event_zero_amount_fails() {
    let event = WithdrawEvent::new(
        "acc-1", "ws-1", "user-1", "Withdrawal", "evt-1",
        "2026-01-15T10:30:00Z", 0.0, "bank", "txn-1",
    );
    assert!(!event.validate().is_valid());
}

#[test]
fn withdraw_event_empty_payment_method_is_optional() {
    let event = WithdrawEvent::new(
        "acc-1", "ws-1", "user-1", "Withdrawal", "evt-1",
        "2026-01-15T10:30:00Z", 100.0, "", "txn-1",
    );
    assert!(event.validate().is_valid());
}

#[test]
fn withdraw_event_any_payment_method_passes() {
    for method in ["bank", "credit_card", "debit_card", "e_wallet", "crypto", "paypal", "skrill", "neteller", "cheque"] {
        let event = WithdrawEvent::new(
            "acc-1", "ws-1", "user-1", "Withdrawal", "evt-1",
            "2026-01-15T10:30:00Z", 100.0, method, "txn-1",
        );
        assert!(event.validate().is_valid(), "expected valid for payment_method={method}");
    }
}

#[test]
fn withdraw_event_invalid_timestamp_fails() {
    let event = WithdrawEvent::new(
        "acc-1", "ws-1", "user-1", "Withdrawal", "evt-1",
        "not-a-date", 100.0, "bank", "txn-1",
    );
    let result = event.validate();
    assert!(!result.is_valid());
    assert!(result.errors.iter().any(|e| e.contains("event_time")));
}

#[test]
fn withdraw_event_sets_correct_event_category() {
    let value = serde_json::to_value(&valid_withdraw()).unwrap();
    assert_eq!(value.get("event_category").unwrap(), "Withdraw");
}

#[test]
fn withdraw_event_optional_failure_reason_serializes() {
    let mut event = valid_withdraw();
    event.failure_reason = Some("Insufficient funds".to_string());
    let value = serde_json::to_value(&event).unwrap();
    assert_eq!(value.get("failure_reason").unwrap(), "Insufficient funds");
}

#[test]
fn withdraw_event_none_failure_reason_omitted_from_json() {
    let value = serde_json::to_value(&valid_withdraw()).unwrap();
    assert!(value.get("failure_reason").is_none());
}
