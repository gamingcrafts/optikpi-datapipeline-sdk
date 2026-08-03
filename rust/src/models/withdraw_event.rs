use serde::{Deserialize, Serialize};

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

/// Withdraw event: a financial withdrawal transaction.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct WithdrawEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    pub amount: f64,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub payment_method: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub transaction_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub failure_reason: Option<String>,
}

impl WithdrawEvent {
    #[allow(clippy::too_many_arguments)]
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
        amount: f64,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            event_category: "Withdraw".to_string(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            amount,
            payment_method: None,
            transaction_id: None,
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
        if self.event_category != "Withdraw" {
            errors.push(r#"event_category must be "Withdraw" for withdraw events"#.to_string());
        }
        if self.amount <= 0.0 {
            errors.push("amount must be a positive number".to_string());
        }
        if let Some(method) = &self.payment_method {
            if !VALID_PAYMENT_METHODS.contains(&method.as_str()) {
                errors.push(format!(
                    "payment_method must be one of: {}",
                    VALID_PAYMENT_METHODS.join(", ")
                ));
            }
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        ok(errors)
    }
}
