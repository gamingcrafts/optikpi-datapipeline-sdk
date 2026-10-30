//! Models reject records the data warehouse would refuse to store.

use std::time::{SystemTime, UNIX_EPOCH};

use optikpi_datapipeline_sdk::models::{
    AccountEvent, CustomerExtEvent, CustomerProfile, DepositEvent, GamingActivityEvent, ReferFriendEvent,
    ValidationResult, WalletBalanceEvent, WithdrawEvent,
};
use serde_json::json;

/// An ISO 8601 UTC timestamp `offset_days` from now.
fn iso_days_from_now(offset_days: i64) -> String {
    let secs = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs() as i64 + offset_days * 86_400;
    let days = secs.div_euclid(86_400);
    let rem = secs.rem_euclid(86_400);
    // Civil date from days since the epoch.
    let z = days + 719_468;
    let era = z.div_euclid(146_097);
    let doe = z - era * 146_097;
    let yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365;
    let doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
    let mp = (5 * doy + 2) / 153;
    let d = doy - (153 * mp + 2) / 5 + 1;
    let m = if mp < 10 { mp + 3 } else { mp - 9 };
    let y = yoe + era * 400 + i64::from(m <= 2);
    format!("{y:04}-{m:02}-{d:02}T{:02}:{:02}:{:02}Z", rem / 3600, rem % 3600 / 60, rem % 60)
}

fn now() -> String {
    iso_days_from_now(0)
}

/// Each event model with otherwise complete fields, with or without its event_category.
fn events(with_category: bool) -> Vec<(&'static str, ValidationResult, serde_json::Value)> {
    let t = now();
    let cat = |c: &str| if with_category { c.to_string() } else { String::new() };
    let account = AccountEvent::new("acc", "ws", "1", cat("Account"), "Something", "e1", &t);
    let deposit = DepositEvent::new("acc", "ws", "1", cat("Deposit"), "Something", "e1", &t, 10.0, "credit_card", "t1");
    let withdraw = WithdrawEvent::new("acc", "ws", "1", cat("Withdraw"), "Something", "e1", &t, 10.0, "credit_card", "t1");
    let gaming = GamingActivityEvent::new("acc", "ws", "1", cat("Gaming Activity"), "Something", "e1", &t, "15466", "Aviator");
    let mut wallet = WalletBalanceEvent::new("acc", "ws", "1", cat("Wallet Balance"), "Something", "e1", &t);
    wallet.currency = Some("INR".into());
    wallet.wallet_type = Some("main".into());
    let refer = ReferFriendEvent::new("acc", "ws", "1", cat("Refer Friend"), "Something", "e1", &t);
    vec![
        ("AccountEvent", account.validate(), serde_json::to_value(&account).unwrap()),
        ("DepositEvent", deposit.validate(), serde_json::to_value(&deposit).unwrap()),
        ("WithdrawEvent", withdraw.validate(), serde_json::to_value(&withdraw).unwrap()),
        ("GamingActivityEvent", gaming.validate(), serde_json::to_value(&gaming).unwrap()),
        ("WalletBalanceEvent", wallet.validate(), serde_json::to_value(&wallet).unwrap()),
        ("ReferFriendEvent", refer.validate(), serde_json::to_value(&refer).unwrap()),
    ]
}

#[test]
fn models_reject_a_missing_event_category_and_do_not_fill_one_in() {
    for (name, result, sent) in events(false) {
        assert!(!result.is_valid(), "{name}");
        assert_eq!(result.errors, vec!["event_category is required".to_string()], "{name}");
        assert_eq!(sent["event_category"], json!(""), "{name}");
    }
}

#[test]
fn models_accept_a_complete_record() {
    for (name, result, _) in events(true) {
        assert_eq!(result, ValidationResult::default(), "{name}");
    }
}

#[test]
fn a_wrong_event_category_is_still_rejected() {
    let event = AccountEvent::new("acc", "ws", "1", "Deposit", "Login", "e1", now());
    assert_eq!(
        event.validate().errors,
        vec![r#"event_category must be "Account" for account events"#.to_string()]
    );
}

#[test]
fn a_wallet_payload_is_rejected_for_its_missing_event_category() {
    let payload = json!({
        "workspace_id": "ws_example_001",
        "blocked_amount": 100.0,
        "account_id": "acc_example_001",
        "current_cash_balance": 3065.5,
        "event_id": "da49a497-a41f-4e37-b7b2-e10ad0c0ed4d",
        "currency": "INR",
        "wallet_type": "main",
        "user_id": "1",
        "current_bonus_balance": 20.0,
        "event_time": now(),
        "event_name": "Balance Update",
        "current_total_balance": 3085.5
    });
    let event: WalletBalanceEvent = serde_json::from_value(payload).unwrap();
    assert_eq!(event.validate().errors, vec!["event_category is required".to_string()]);
}

#[test]
fn a_numeric_game_id_cannot_be_built() {
    // The typed field rejects it at deserialization, before anything can be sent.
    let payload = json!({
        "account_id": "acc", "workspace_id": "ws", "user_id": "1", "event_category": "Gaming Activity",
        "event_name": "Something", "event_id": "e1", "event_time": now(), "game_id": 15466, "game_title": "Aviator"
    });
    assert!(serde_json::from_value::<GamingActivityEvent>(payload).is_err());
}

#[test]
fn an_event_time_outside_the_partition_window_is_rejected() {
    let message = "event_time must be within the last 10 years and no more than 1 year in the future".to_string();
    for (offset_days, accepted) in [(-3700, false), (-3600, true), (360, true), (370, false)] {
        let event = AccountEvent::new("acc", "ws", "1", "Account", "Login", "e1", iso_days_from_now(offset_days));
        let errors = event.validate().errors;
        assert_eq!(!errors.contains(&message), accepted, "{offset_days}: {errors:?}");
    }
}

fn customer() -> CustomerProfile {
    CustomerProfile::new("acc", "ws", "1", "u", "u@example.com", now())
}

#[test]
fn a_customer_with_an_empty_string_timestamp_is_rejected() {
    let mut profile = customer();
    profile.self_exclusion_created_time = Some(String::new());
    assert!(profile
        .validate()
        .errors
        .contains(&"self_exclusion_created_time must be a valid ISO 8601 date-time".to_string()));
}

#[test]
fn a_customer_with_an_impossible_date_of_birth_is_rejected() {
    let mut profile = customer();
    profile.date_of_birth = Some("1990-02-30".into());
    assert_eq!(
        profile.validate().errors,
        vec!["date_of_birth must be a date in YYYY-MM-DD format".to_string()]
    );
}

#[test]
fn customer_custom_data_must_be_an_object_an_array_or_a_json_string() {
    let mut profile = customer();
    for (value, accepted) in [(json!({"a": 1}), true), (json!(r#"{"a":1}"#), true), (json!([1]), true), (json!(5), false), (json!(""), false)] {
        profile.custom_data = Some(value.clone());
        let errors = profile.validate().errors;
        assert_eq!(errors.is_empty(), accepted, "{value}: {errors:?}");
        if !accepted {
            assert_eq!(errors, vec!["custom_data must be an object or a valid JSON string".to_string()]);
        }
    }
}

#[test]
fn a_customer_created_more_than_10_years_ago_is_rejected() {
    let profile = CustomerProfile::new("acc", "ws", "1", "u", "u@example.com", "2012-05-01T00:00:00Z");
    assert_eq!(
        profile.validate().errors,
        vec!["creation_timestamp must be within the last 10 years and no more than 1 year in the future".to_string()]
    );
}

#[test]
fn customer_ext_data_may_be_an_object_or_a_json_string() {
    let object = CustomerExtEvent::new("acc", "ws", "1", "l", json!({"a": 1}));
    let string = CustomerExtEvent::new("acc", "ws", "1", "l", json!(r#"{"a":1}"#));
    assert!(object.validate().is_valid());
    assert!(string.validate().is_valid());
}

#[test]
fn customer_ext_requires_workspace_and_user() {
    let event = CustomerExtEvent::new("acc", "", "", "l", json!({"a": 1}));
    let errors = event.validate().errors;
    assert!(errors.contains(&"workspace_id is required".to_string()));
    assert!(errors.contains(&"user_id is required".to_string()));
}
