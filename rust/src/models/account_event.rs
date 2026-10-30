use serde::{Deserialize, Serialize};

use super::big_query_validation::{append_storage_errors, Col, StorageRules};
use super::{is_valid_datetime, ok, ValidationResult};

const VALID_STATUSES: [&str; 4] = ["verified", "pending", "failed", "completed"];
const VALID_DEVICES: [&str; 4] = ["desktop", "mobile", "tablet", "app"];

// Fields and types the warehouse accepts for this record; anything else is rejected there.
const STORAGE_RULES: StorageRules = StorageRules {
    required: &["workspace_id", "user_id", "event_id", "event_time", "event_category", "event_name"],
    types: &[
        ("workspace_id", Col::String),
        ("user_id", Col::String),
        ("event_id", Col::String),
        ("event_time", Col::Timestamp),
        ("event_category", Col::String),
        ("event_name", Col::String),
        ("device", Col::String),
        ("affiliate_id", Col::String),
        ("partner_id", Col::String),
        ("utm_source", Col::String),
        ("utm_campaign", Col::String),
        ("utm_medium", Col::String),
        ("status", Col::String),
        ("reason", Col::String),
        ("campaign_code", Col::String),
    ],
    partition: Some("event_time"),
};

/// Account event: registration, verification, and account changes.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct AccountEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    /// Required; the SDK never fills it in. Must be `"Account"`.
    #[serde(default)]
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub device: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub affiliate_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub partner_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub campaign_code: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_source: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_campaign: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_medium: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub reason: Option<String>,
}

impl AccountEvent {
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        event_category: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            event_category: event_category.into(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            device: None,
            status: None,
            affiliate_id: None,
            partner_id: None,
            campaign_code: None,
            utm_source: None,
            utm_campaign: None,
            utm_medium: None,
            reason: None,
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
        if !self.event_category.is_empty() && self.event_category != "Account" {
            errors.push(r#"event_category must be "Account" for account events"#.to_string());
        }
        if let Some(status) = &self.status {
            if !VALID_STATUSES.contains(&status.as_str()) {
                errors.push(format!("status must be one of: {}", VALID_STATUSES.join(", ")));
            }
        }
        if let Some(device) = &self.device {
            if !VALID_DEVICES.contains(&device.as_str()) {
                errors.push(format!("device must be one of: {}", VALID_DEVICES.join(", ")));
            }
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        append_storage_errors(&mut errors, self, &STORAGE_RULES);

        ok(errors)
    }
}
