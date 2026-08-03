use serde::{Deserialize, Serialize};

use super::{is_valid_datetime, ok, ValidationResult};

const VALID_REWARD_TYPES: [&str; 5] = ["bonus", "cash", "points", "free_spins", "other"];
const VALID_CLAIM_STATUSES: [&str; 4] = ["pending", "claimed", "expired", "cancelled"];

/// Refer friend event: a referral program activity or reward.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ReferFriendEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referral_code_used: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub successful_referral_confirmation: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub reward_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub reward_claimed_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referee_user_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referee_registration_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referee_first_deposit: Option<f64>,
}

impl ReferFriendEvent {
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
            event_category: "Refer Friend".to_string(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            referral_code_used: None,
            successful_referral_confirmation: None,
            reward_type: None,
            reward_claimed_status: None,
            referee_user_id: None,
            referee_registration_date: None,
            referee_first_deposit: None,
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
        if self.event_category != "Refer Friend" {
            errors.push(r#"event_category must be "Refer Friend" for refer friend events"#.to_string());
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }
        if let Some(date) = &self.referee_registration_date {
            if !is_valid_datetime(date) {
                errors.push("referee_registration_date must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
            }
        }
        if let Some(reward_type) = &self.reward_type {
            if !VALID_REWARD_TYPES.contains(&reward_type.as_str()) {
                errors.push(format!("reward_type must be one of: {}", VALID_REWARD_TYPES.join(", ")));
            }
        }
        if let Some(status) = &self.reward_claimed_status {
            if !VALID_CLAIM_STATUSES.contains(&status.as_str()) {
                errors.push(format!(
                    "reward_claimed_status must be one of: {}",
                    VALID_CLAIM_STATUSES.join(", ")
                ));
            }
        }
        if let Some(deposit) = self.referee_first_deposit {
            if deposit < 0.0 {
                errors.push("referee_first_deposit must be a non-negative number".to_string());
            }
        }

        ok(errors)
    }
}
