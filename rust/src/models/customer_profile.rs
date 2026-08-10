use serde::{Deserialize, Serialize};
use serde_json::Value;

use super::{is_valid_date, is_valid_email, ok, ValidationResult};

/// Customer profile: full user account information and preferences.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CustomerProfile {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub username: String,
    pub email: String,
    pub creation_timestamp: String,

    #[serde(skip_serializing_if = "Option::is_none")]
    pub full_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub first_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub last_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub date_of_birth: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub phone_number: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub gender: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub country: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub city: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub language: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub currency: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub phone_verification: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub email_verification: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bank_verification: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub iddoc_verification: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub marketing_email_preference: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub notifications_preference: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub subscription: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub privacy_settings: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub deposit_limits: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub loss_limits: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub wagering_limits: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub session_time_limits: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cooling_off_expiry_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub self_exclusion_expiry_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub reality_checks_notification: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub vip_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub loyalty_program_tiers: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub account_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bonus_abuser: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub financial_risk_level: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub acquisition_source: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub partner_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub affiliate_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_source: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_campaign: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_medium: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referral_link_code: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referral_limit_reached: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub risk_score_level: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub marketing_sms_preference: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub custom_data: Option<Value>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub self_exclusion_by: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub self_exclusion_by_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub self_exclusion_check_time: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub self_exclusion_created_time: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub closed_time: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub real_money_enabled: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub push_token: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub android_push_token: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub ios_push_token: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub windows_push_token: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub mac_dmg_push_token: Option<String>,
}

impl CustomerProfile {
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        username: impl Into<String>,
        email: impl Into<String>,
        creation_timestamp: impl Into<String>,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            username: username.into(),
            email: email.into(),
            creation_timestamp: creation_timestamp.into(),
            full_name: None,
            first_name: None,
            last_name: None,
            date_of_birth: None,
            phone_number: None,
            gender: None,
            country: None,
            city: None,
            language: None,
            currency: None,
            phone_verification: None,
            email_verification: None,
            bank_verification: None,
            iddoc_verification: None,
            marketing_email_preference: None,
            notifications_preference: None,
            subscription: None,
            privacy_settings: None,
            deposit_limits: None,
            loss_limits: None,
            wagering_limits: None,
            session_time_limits: None,
            cooling_off_expiry_date: None,
            self_exclusion_expiry_date: None,
            reality_checks_notification: None,
            vip_status: None,
            loyalty_program_tiers: None,
            account_status: None,
            bonus_abuser: None,
            financial_risk_level: None,
            acquisition_source: None,
            partner_id: None,
            affiliate_id: None,
            utm_source: None,
            utm_campaign: None,
            utm_medium: None,
            referral_link_code: None,
            referral_limit_reached: None,
            risk_score_level: None,
            marketing_sms_preference: None,
            custom_data: None,
            self_exclusion_by: None,
            self_exclusion_by_type: None,
            self_exclusion_check_time: None,
            self_exclusion_created_time: None,
            closed_time: None,
            real_money_enabled: None,
            push_token: None,
            android_push_token: None,
            ios_push_token: None,
            windows_push_token: None,
            mac_dmg_push_token: None,
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
        if self.username.is_empty() {
            errors.push("username is required".to_string());
        }
        if self.email.is_empty() {
            errors.push("email is required".to_string());
        } else if !is_valid_email(&self.email) {
            errors.push("email must be a valid email address".to_string());
        }
        if self.creation_timestamp.is_empty() {
            errors.push("creation_timestamp is required".to_string());
        }
        if let Some(dob) = &self.date_of_birth {
            if !is_valid_date(dob) {
                errors.push("date_of_birth must be in YYYY-MM-DD format".to_string());
            }
        }
        if let Some(gender) = &self.gender {
            if !["Male", "Female", "Other"].contains(&gender.as_str()) {
                errors.push("gender must be one of: Male, Female, Other".to_string());
            }
        }
        ok(errors)
    }
}
