use serde::{Deserialize, Serialize};
use serde_json::Value;

use super::{is_valid_datetime, ok, ValidationResult};

/// System event: back-office, operator, and system-level actions (campaign
/// triggers, manual operator actions, automated processes). `event_data` is
/// an arbitrary caller-defined JSON payload — either an object or a JSON
/// string are accepted by the API.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct SystemEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    pub event_data: Value,
}

impl SystemEvent {
    /// `event_category` defaults to `"SystemEvent"`; set it directly on the
    /// returned instance for a caller-defined category (e.g. `"BackOffice"`).
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
        event_data: Value,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            event_category: "SystemEvent".to_string(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            event_data,
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
        if self.event_category.is_empty() {
            errors.push("event_category is required".to_string());
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
        if self.event_data.is_null() {
            errors.push("event_data is required".to_string());
        } else if !(self.event_data.is_object() || self.event_data.is_string()) {
            errors.push("event_data must be a JSON object or a JSON string".to_string());
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        ok(errors)
    }
}
