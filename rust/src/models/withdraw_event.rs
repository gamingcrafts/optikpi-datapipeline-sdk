use serde::{Deserialize, Serialize};

use super::big_query_validation::{append_storage_errors, Col, StorageRules};
use super::{is_valid_datetime, ok, ValidationResult};

const VALID_PAYMENT_METHODS: [&str; 8] = [
    "bank",
    "credit_card",
    "debit_card",
    "e_wallet",
    "crypto",
    "paypal",
    "skrill",
    "neteller",
];

// Fields and types the warehouse accepts for this record; anything else is rejected there.
const STORAGE_RULES: StorageRules = StorageRules {
    required: &["workspace_id", "user_id", "event_id", "event_time", "event_category", "event_name", "amount"],
    types: &[
        ("workspace_id", Col::String),
        ("user_id", Col::String),
        ("event_id", Col::String),
        ("event_time", Col::Timestamp),
        ("event_category", Col::String),
        ("event_name", Col::String),
        ("amount", Col::Numeric),
        ("payment_method", Col::String),
        ("transaction_id", Col::String),
        ("failure_reason", Col::String),
        ("payment_provider_id", Col::String),
        ("payment_provider_name", Col::String),
    ],
    partition: Some("event_time"),
};

/// Withdraw event: a financial withdrawal transaction.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct WithdrawEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    /// Required; the SDK never fills it in. Must be `"Withdraw"`.
    #[serde(default)]
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    pub amount: f64,
    pub payment_method: String,
    pub transaction_id: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub failure_reason: Option<String>,
}

impl WithdrawEvent {
    #[allow(clippy::too_many_arguments)]
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        event_category: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
        amount: f64,
        payment_method: impl Into<String>,
        transaction_id: impl Into<String>,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            event_category: event_category.into(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            amount,
            payment_method: payment_method.into(),
            transaction_id: transaction_id.into(),
            failure_reason: None,
        }
    }

    pub fn validate(&self) -> ValidationResult {
        let mut errors = Vec::new();

        if self.account_id.is_empty() {
            errors.push("account_id is required".to_string());
        }
        if self.workspace_id.is_empty() {
            errors.push("workspace_id is required".to_string());
        }
        if self.user_id.is_empty() {
            errors.push("user_id is required".to_string());
        }
        if self.event_name.is_empty() {
            errors.push("event_name is required".to_string());
        }
        if self.event_id.is_empty() {
            errors.push("event_id is required".to_string());
        }
        if self.event_time.is_empty() {
            errors.push("event_time is required".to_string());
        }
        if !self.event_category.is_empty() && self.event_category != "Withdraw" {
            errors.push(r#"event_category must be "Withdraw" for withdraw events"#.to_string());
        }
        if self.amount <= 0.0 {
            errors.push("amount must be a positive number".to_string());
        }
        if self.payment_method.is_empty() {
            errors.push("payment_method is required".to_string());
        } else if !VALID_PAYMENT_METHODS.contains(&self.payment_method.as_str()) {
            errors.push(format!(
                "payment_method must be one of: {}",
                VALID_PAYMENT_METHODS.join(", ")
            ));
        }
        if self.transaction_id.is_empty() {
            errors.push("transaction_id is required".to_string());
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        append_storage_errors(&mut errors, self, &STORAGE_RULES);

        ok(errors)
    }
}
