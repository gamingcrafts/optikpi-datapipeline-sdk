use serde::{Deserialize, Serialize};

use super::{is_valid_datetime, ok, ValidationResult};


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
            event_category: "Withdraw".to_string(),
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
        }
        if self.transaction_id.is_empty() {
            errors.push("transaction_id is required".to_string());
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        ok(errors)
    }
}
