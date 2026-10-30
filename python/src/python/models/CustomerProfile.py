from dataclasses import dataclass, field, asdict
from typing import Optional, Any, Dict, List
import re
from datetime import datetime

from .bigquery_validation import merge_storage_errors, model_from_raw

# Fields and types the warehouse accepts for this record; anything else is rejected there.
STORAGE_RULES = {
    "required": ["workspace_id", "user_id", "creation_timestamp"],
    "types": {
        "workspace_id": "STRING",
        "user_id": "STRING",
        "username": "STRING",
        "full_name": "STRING",
        "first_name": "STRING",
        "last_name": "STRING",
        "date_of_birth": "DATE",
        "email": "STRING",
        "phone_number": "STRING",
        "gender": "STRING",
        "country": "STRING",
        "city": "STRING",
        "language": "STRING",
        "currency": "STRING",
        "phone_verification": "STRING",
        "email_verification": "STRING",
        "bank_verification": "STRING",
        "iddoc_verification": "STRING",
        "marketing_email_preference": "STRING",
        "notifications_preference": "STRING",
        "subscription": "STRING",
        "privacy_settings": "STRING",
        "deposit_limits": "NUMERIC",
        "loss_limits": "NUMERIC",
        "wagering_limits": "NUMERIC",
        "session_time_limits": "NUMERIC",
        "cooling_off_expiry_date": "TIMESTAMP",
        "self_exclusion_expiry_date": "TIMESTAMP",
        "reality_checks_notification": "STRING",
        "vip_status": "STRING",
        "loyalty_program_tiers": "STRING",
        "account_status": "STRING",
        "bonus_abuser": "STRING",
        "financial_risk_level": "NUMERIC",
        "acquisition_source": "STRING",
        "partner_id": "STRING",
        "affiliate_id": "STRING",
        "utm_source": "STRING",
        "utm_campaign": "STRING",
        "utm_medium": "STRING",
        "referral_link_code": "STRING",
        "referral_limit_reached": "STRING",
        "creation_timestamp": "TIMESTAMP",
        "risk_score_level": "STRING",
        "marketing_sms_preference": "STRING",
        "custom_data": "JSON",
        "self_exclusion_by": "STRING",
        "self_exclusion_by_type": "STRING",
        "self_exclusion_check_time": "TIMESTAMP",
        "self_exclusion_created_time": "TIMESTAMP",
        "closed_time": "TIMESTAMP",
        "real_money_enabled": "STRING",
        "push_token": "STRING"
    },
    "partition": "creation_timestamp"
}


@dataclass
class CustomerProfile:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    username: Optional[str] = None
    full_name: Optional[str] = None
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    date_of_birth: Optional[str] = None
    email: Optional[str] = None
    phone_number: Optional[str] = None
    gender: Optional[str] = None
    country: Optional[str] = None
    city: Optional[str] = None
    language: Optional[str] = None
    currency: Optional[str] = None
    marketing_email_preference: Optional[str] = None
    notifications_preference: Optional[str] = None
    subscription: Optional[str] = None
    privacy_settings: Optional[str] = None
    deposit_limits: Optional[float] = None
    loss_limits: Optional[float] = None
    wagering_limits: Optional[float] = None
    session_time_limits: Optional[int] = None
    reality_checks_notification: Optional[str] = None
    account_status: Optional[str] = None
    vip_status: Optional[str] = None
    loyalty_program_tiers: Optional[str] = None
    bonus_abuser: Optional[str] = None
    financial_risk_level: Optional[float] = None
    acquisition_source: Optional[str] = None
    partner_id: Optional[str] = None
    affiliate_id: Optional[str] = None
    utm_source: Optional[str] = None
    utm_campaign: Optional[str] = None
    utm_medium: Optional[str] = None
    referral_link_code: Optional[str] = None
    referral_limit_reached: Optional[str] = None
    creation_timestamp: Optional[str] = None
    phone_verification: Optional[str] = None
    email_verification: Optional[str] = None
    bank_verification: Optional[str] = None
    iddoc_verification: Optional[str] = None
    cooling_off_expiry_date: Optional[str] = None
    self_exclusion_expiry_date: Optional[str] = None
    risk_score_level: Optional[str] = None
    marketing_sms_preference: Optional[str] = None
    custom_data: Optional[Dict[str, Any]] = field(default_factory=dict)
    self_exclusion_by: Optional[str] = None
    self_exclusion_by_type: Optional[str] = None
    self_exclusion_check_time: Optional[str] = None
    self_exclusion_created_time: Optional[str] = None
    closed_time: Optional[str] = None
    real_money_enabled: Optional[str] = None
    push_token: Optional[str] = None
    android_push_token: Optional[str] = None
    ios_push_token: Optional[str] = None    
    windows_push_token: Optional[str] = None
    mac_dmg_push_token: Optional[str] = None

    # -------------------------------
    # Validation methods
    # -------------------------------
    def validate(self) -> Dict[str, Any]:
        errors: List[str] = []

        # Required fields
        if not self.account_id:
            errors.append("account_id is required")
        if not self.workspace_id:
            errors.append("workspace_id is required")
        if not self.user_id:
            errors.append("user_id is required")
        if not self.username:
            errors.append("username is required")
        if not self.email:
            errors.append("email is required")
        if not self.creation_timestamp:
            errors.append("creation_timestamp is required")

        # Email validation
        if self.email and not self.is_valid_email(self.email):
            errors.append("email must be a valid email address")

        # Date of birth validation
        if self.date_of_birth and not self.is_valid_date(self.date_of_birth):
            errors.append("date_of_birth must be in YYYY-MM-DD format")

        # Enum validations
        if self.gender and self.gender not in ["Male", "Female", "Other"]:
            errors.append("gender must be one of: Male, Female, Other")

        if self.account_status and self.account_status not in ["Active", "Inactive", "Suspended", "Closed"]:
            errors.append("account_status must be one of: Active, Inactive, Suspended, Closed")

        if self.vip_status and self.vip_status not in ["Regular", "Silver", "Gold", "Platinum", "Diamond"]:
            errors.append("vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond")

        # The record as sent, so validation also covers fields this model does not declare.
        merge_storage_errors(errors, self, STORAGE_RULES)

        return {
            "isValid": len(errors) == 0,
            "errors": errors
        }

    @staticmethod
    def is_valid_email(email: str) -> bool:
        email_regex = r"^[^\s@]+@[^\s@]+\.[^\s@]+$"
        return re.match(email_regex, email) is not None

    @staticmethod
    def is_valid_date(date_str: str) -> bool:
        try:
            datetime.strptime(date_str, "%Y-%m-%d")
            return True
        except ValueError:
            return False

    # -------------------------------
    # Utility methods
    # -------------------------------
    def to_dict(self) -> Dict[str, Any]:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> "CustomerProfile":
        return model_from_raw(cls, data)

    def __repr__(self) -> str:
        return f"CustomerProfile(user_id={self.user_id}, username={self.username}, email={self.email})"
