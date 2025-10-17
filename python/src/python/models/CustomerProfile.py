import re
from datetime import datetime
from typing import Any, Dict, List, Optional, Union


class CustomerProfile:
    """
    Customer Profile Model
    Represents a customer profile for the Data Pipeline API
    """

    def __init__(self, data: Optional[Dict[str, Any]] = None) -> None:
        if data is None:
            data = {}

        # Core identifiers
        self.account_id: Optional[str] = data.get("account_id")
        self.workspace_id: Optional[str] = data.get("workspace_id")
        self.user_id: Optional[str] = data.get("user_id")
        self.username: Optional[str] = data.get("username")

        # Personal info
        self.full_name: Optional[str] = data.get("full_name")
        self.first_name: Optional[str] = data.get("first_name")
        self.last_name: Optional[str] = data.get("last_name")
        self.date_of_birth: Optional[str] = data.get("date_of_birth")
        self.email: Optional[str] = data.get("email")
        self.phone_number: Optional[str] = data.get("phone_number")
        self.gender: Optional[str] = data.get("gender")

        # Location & preferences
        self.country: Optional[str] = data.get("country")
        self.city: Optional[str] = data.get("city")
        self.language: Optional[str] = data.get("language")
        self.currency: Optional[str] = data.get("currency")
        self.marketing_email_preference: Optional[bool] = data.get("marketing_email_preference")
        self.marketing_sms_preference: Optional[bool] = data.get("marketing_sms_preference")
        self.notifications_preference: Optional[bool] = data.get("notifications_preference")

        # Subscription & privacy
        self.subscription: Optional[str] = data.get("subscription")
        self.privacy_settings: Optional[Dict[str, Any]] = data.get("privacy_settings")

        # Responsible gaming limits
        self.deposit_limits: Optional[Dict[str, Any]] = data.get("deposit_limits")
        self.loss_limits: Optional[Dict[str, Any]] = data.get("loss_limits")
        self.wagering_limits: Optional[Dict[str, Any]] = data.get("wagering_limits")
        self.session_time_limits: Optional[Dict[str, Any]] = data.get("session_time_limits")
        self.cooling_off_period: Optional[int] = data.get("cooling_off_period")
        self.self_exclusion_period: Optional[int] = data.get("self_exclusion_period")
        self.reality_checks_notification: Optional[bool] = data.get("reality_checks_notification")

        # Account status
        self.account_status: Optional[str] = data.get("account_status")
        self.vip_status: Optional[str] = data.get("vip_status")
        self.loyalty_program_tiers: Optional[List[str]] = data.get("loyalty_program_tiers")
        self.bonus_abuser: Optional[bool] = data.get("bonus_abuser")
        self.financial_risk_level: Optional[str] = data.get("financial_risk_level")
        self.risk_score_level: Optional[str] = data.get("risk_score_level")

        # Acquisition
        self.acquisition_source: Optional[str] = data.get("acquisition_source")
        self.partner_id: Optional[str] = data.get("partner_id")
        self.affliate_id: Optional[str] = data.get("affliate_id")  # Note: typo preserved from JS
        self.referral_link_code: Optional[str] = data.get("referral_link_code")
        self.referral_limit_reached: Optional[bool] = data.get("referral_limit_reached")

        # Timestamps
        self.creation_timestamp: Optional[Union[str, int, float]] = data.get("creation_timestamp")
        self.cooling_off_expiry_date: Optional[str] = data.get("cooling_off_expiry_date")
        self.self_exclusion_expiry_date: Optional[str] = data.get("self_exclusion_expiry_date")
        self.self_exclusion_check_time: Optional[str] = data.get("self_exclusion_check_time")
        self.self_exclusion_created_time: Optional[str] = data.get("self_exclusion_created_time")
        self.closed_time: Optional[str] = data.get("closed_time")

        # Verification status
        self.phone_verification: Optional[bool] = data.get("phone_verification")
        self.email_verification: Optional[bool] = data.get("email_verification")
        self.bank_verification: Optional[bool] = data.get("bank_verification")
        self.iddoc_verification: Optional[bool] = data.get("iddoc_verification")

        # Other
        self.real_money_enabled: Optional[bool] = data.get("real_money_enabled")
        self.push_token: Optional[str] = data.get("push_token")
        self.custom_data: Optional[Dict[str, Any]] = data.get("custom_data")
        self.self_exclusion_by: Optional[str] = data.get("self_exclusion_by")
        self.self_exclusion_by_type: Optional[str] = data.get("self_exclusion_by_type")

    def validate(self) -> Dict[str, Union[bool, List[str]]]:
        """
        Validates the customer profile data

        Returns:
            dict: Validation result with 'is_valid' boolean and 'errors' list
        """
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

        # Email format validation
        if self.email and not self._is_valid_email(self.email):
            errors.append("email must be a valid email address")

        # Date format validation (YYYY-MM-DD)
        if self.date_of_birth and not self._is_valid_date(self.date_of_birth):
            errors.append("date_of_birth must be in YYYY-MM-DD format")

        # Enum validations
        if self.gender and self.gender not in {"Male", "Female", "Other"}:
            errors.append("gender must be one of: Male, Female, Other")

        if self.account_status and self.account_status not in {"Active", "Inactive", "Suspended", "Closed"}:
            errors.append("account_status must be one of: Active, Inactive, Suspended, Closed")

        if self.vip_status and self.vip_status not in {"Regular", "Silver", "Gold", "Platinum", "Diamond"}:
            errors.append("vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond")

        return {
            "is_valid": len(errors) == 0,
            "errors": errors
        }

    def _is_valid_email(self, email: str) -> bool:
        """
        Validates email format

        Args:
            email (str): Email to validate

        Returns:
            bool: True if email is valid
        """
        email_regex = r"^[^\s@]+@[^\s@]+\.[^\s@]+$"
        return bool(re.match(email_regex, email))

    def _is_valid_date(self, date_str: str) -> bool:
        """
        Validates date format (YYYY-MM-DD)

        Args:
            date_str (str): Date string to validate

        Returns:
            bool: True if date is valid and in correct format
        """
        date_regex = r"^\d{4}-\d{2}-\d{2}$"
        if not re.match(date_regex, date_str):
            return False

        try:
            parsed_date = datetime.strptime(date_str, "%Y-%m-%d")
            # Ensure round-trip consistency (e.g., 2023-02-30 → invalid)
            return parsed_date.strftime("%Y-%m-%d") == date_str
        except ValueError:
            return False

    def to_dict(self) -> Dict[str, Any]:
        """
        Converts the model to a plain dictionary (excludes None/undefined-like values)

        Returns:
            dict: Dictionary representation of the customer profile
        """
        result = {}
        for key, value in self.__dict__.items():
            if value is not None:
                result[key] = value
        return result

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> "CustomerProfile":
        """
        Creates a CustomerProfile instance from a plain dictionary

        Args:
            data (dict): Dictionary containing customer profile data

        Returns:
            CustomerProfile: New CustomerProfile instance
        """
        return cls(data)