use serde::ser::{Serialize, SerializeStruct, Serializer};
use serde::Deserialize;
use serde_json::Value;

use super::{ok, ValidationResult};

/// Extended attributes: arbitrary custom key-value data attached to a user
/// under a named list (e.g. `"BINGO_PREFERENCES"`). `ext_data` accepts a
/// JSON object or a JSON string on the wire; it is always sent to the API
/// as a JSON string.
#[derive(Debug, Clone, Deserialize)]
pub struct CustomerExtEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub list_name: String,
    pub ext_data: Value,
}

impl CustomerExtEvent {
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        list_name: impl Into<String>,
        ext_data: Value,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            list_name: list_name.into(),
            ext_data,
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
        if self.list_name.is_empty() {
            errors.push("list_name is required".to_string());
        } else if !self.list_name.chars().all(|c| c.is_ascii_alphanumeric() || c == '_' || c == '-') {
            errors.push("list_name must contain only alphanumeric characters, underscores, and hyphens".to_string());
        }
        match &self.ext_data {
            Value::Object(_) => {}
            Value::String(s) if serde_json::from_str::<Value>(s).is_ok() => {}
            Value::String(_) => errors.push("ext_data string must contain valid JSON".to_string()),
            Value::Null => errors.push("ext_data is required".to_string()),
            _ => errors.push("ext_data must be a JSON object or a JSON string".to_string()),
        }

        ok(errors)
    }
}

impl Serialize for CustomerExtEvent {
    fn serialize<S: Serializer>(&self, serializer: S) -> Result<S::Ok, S::Error> {
        let ext_data = match &self.ext_data {
            Value::String(s) => s.clone(),
            other => other.to_string(),
        };
        let mut state = serializer.serialize_struct("CustomerExtEvent", 5)?;
        state.serialize_field("account_id", &self.account_id)?;
        state.serialize_field("workspace_id", &self.workspace_id)?;
        state.serialize_field("user_id", &self.user_id)?;
        state.serialize_field("list_name", &self.list_name)?;
        state.serialize_field("ext_data", &ext_data)?;
        state.end()
    }
}
