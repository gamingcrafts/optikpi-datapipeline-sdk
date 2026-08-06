use serde::{Deserialize, Serialize};

use super::{is_valid_datetime, ok, ValidationResult};

/// Wallet balance event: a balance snapshot or update notification.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct WalletBalanceEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub wallet_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub currency: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub current_cash_balance: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub current_bonus_balance: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub current_total_balance: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub blocked_amount: Option<f64>,
}

impl WalletBalanceEvent {
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            event_category: "Wallet Balance".to_string(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            wallet_type: None,
            currency: None,
            current_cash_balance: None,
            current_bonus_balance: None,
            current_total_balance: None,
            blocked_amount: None,
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
        if !self.event_category.is_empty() && self.event_category != "Wallet Balance" {
            errors.push(r#"event_category must be "Wallet Balance" for wallet events"#.to_string());
        }
        if let Some(currency) = &self.currency {
            if !(currency.len() == 3 && currency.chars().all(|c| c.is_ascii_uppercase())) {
                errors.push("currency must be a valid 3-letter ISO currency code".to_string());
            }
        }
        for (name, value) in [
            ("current_cash_balance", self.current_cash_balance),
            ("current_bonus_balance", self.current_bonus_balance),
            ("current_total_balance", self.current_total_balance),
            ("blocked_amount", self.blocked_amount),
        ] {
            if let Some(v) = value {
                if v < 0.0 {
                    errors.push(format!("{name} must be a non-negative number"));
                }
            }
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        ok(errors)
    }
}
