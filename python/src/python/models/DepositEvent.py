import re
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional, Union


class DepositEvent:
    """
    Deposit Event Model
    Represents deposit-related events for the Data Pipeline API
    """

    def __init__(self, data: Optional[Dict[str, Any]] = None) -> None:
        if data is None:
            data = {}

        self.account_id: Optional[str] = data.get("account_id")
        self.workspace_id: Optional[str] = data.get("workspace_id")
        self.user_id: Optional[str] = data.get("user_id")
        self.event_category: str = data.get("event_category", "Deposit")
        self.event_name: Optional[str] = data.get("event_name")
        self.event_id: Optional[str] = data.get("event_id")
        self.event_time: Optional[str] = data.get("event_time")
        self.amount: Optional[Union[int, float]] = data.get("amount")
        self.payment_method: Optional[str] = data.get("payment_method")
        self.transaction_id: Optional[str] = data.get("transaction_id")
        self.payment_provider_id: Optional[str] = data.get("payment_provider_id")
        self.payment_provider_name: Optional[str] = data.get("payment_provider_name")
        self.status: Optional[str] = data.get("status")
        self.currency: Optional[str] = data.get("currency")
        self.fees: Optional[Union[int, float]] = data.get("fees")
        self.net_amount: Optional[Union[int, float]] = data.get("net_amount")

    def validate(self) -> Dict[str, Union[bool, List[str]]]:
        """
        Validates the deposit event data

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
        if not self.event_name:
            errors.append("event_name is required")
        if not self.event_id:
            errors.append("event_id is required")
        if not self.event_time:
            errors.append("event_time is required")
        if self.amount is None:
            errors.append("amount is required")
        if not self.payment_method:
            errors.append("payment_method is required")
        if not self.transaction_id:
            errors.append("transaction_id is required")

        # Event category validation
        if self.event_category != "Deposit":
            errors.append('event_category must be "Deposit" for deposit events')

        # Event name validation
        valid_event_names = {
            "Successful Deposit",
            "Failed Deposit",
            "Pending Deposit",
            "Deposit Cancelled",
            "Deposit Refunded"
        }
        if self.event_name and self.event_name not in valid_event_names:
            errors.append(f"event_name must be one of: {', '.join(valid_event_names)}")

        # Amount validation
        if self.amount is not None:
            if not isinstance(self.amount, (int, float)) or self.amount <= 0:
                errors.append("amount must be a positive number")

        # Payment method validation
        valid_payment_methods = {
            "bank",
            "credit_card",
            "debit_card",
            "e_wallet",
            "crypto",
            "paypal",
            "skrill",
            "neteller"
        }
        if self.payment_method and self.payment_method not in valid_payment_methods:
            errors.append(f"payment_method must be one of: {', '.join(valid_payment_methods)}")

        # Status validation
        valid_statuses = {"success", "pending", "failed", "cancelled", "refunded"}
        if self.status and self.status not in valid_statuses:
            errors.append("status must be one of: success, pending, failed, cancelled, refunded")

        # Currency validation
        if self.currency and not self._is_valid_currency(self.currency):
            errors.append("currency must be a valid 3-letter ISO currency code")

        # Datetime validation
        if self.event_time and not self._is_valid_datetime(self.event_time):
            errors.append("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ or YYYY-MM-DDTHH:mm:ss.sssZ)")

        return {
            "is_valid": len(errors) == 0,
            "errors": errors
        }

    def _is_valid_datetime(self, datetime_str: str) -> bool:
        """
        Validates ISO 8601 datetime format

        Args:
            datetime_str (str): DateTime string to validate

        Returns:
            bool: True if datetime is valid ISO 8601
        """
        if not isinstance(datetime_str, str):
            return False

        # Try common ISO 8601 formats
        iso_formats = [
            "%Y-%m-%dT%H:%M:%SZ",
            "%Y-%m-%dT%H:%M:%S.%fZ",
            "%Y-%m-%dT%H:%M:%S%z",
            "%Y-%m-%dT%H:%M:%S.%f%z",
            "%Y-%m-%dT%H:%M:%S",
            "%Y-%m-%dT%H:%M:%S.%f",
        ]

        for fmt in iso_formats:
            try:
                dt = datetime.strptime(datetime_str, fmt)
                # If no timezone info, assume UTC for consistency
                if dt.tzinfo is None:
                    dt = dt.replace(tzinfo=timezone.utc)
                return True
            except ValueError:
                continue

        # Fallback: try Python's fromisoformat (Python 3.7+)
        try:
            dt = datetime.fromisoformat(datetime_str.replace("Z", "+00:00"))
            if dt.tzinfo is None:
                dt = dt.replace(tzinfo=timezone.utc)
            return True
        except (ValueError, AttributeError):
            return False

    def _is_valid_currency(self, currency: str) -> bool:
        """
        Validates currency code format (3 uppercase letters)

        Args:
            currency (str): Currency code to validate

        Returns:
            bool: True if currency code is valid
        """
        currency_regex = r"^[A-Z]{3}$"
        return bool(re.match(currency_regex, currency))

    def to_dict(self) -> Dict[str, Any]:
        """
        Converts the model to a plain dictionary (excludes None values)

        Returns:
            dict: Dictionary representation of the deposit event
        """
        result = {}
        for key, value in self.__dict__.items():
            if value is not None:
                result[key] = value
        return result

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> "DepositEvent":
        """
        Creates a DepositEvent instance from a plain dictionary

        Args:
            data (dict): Dictionary containing deposit event data

        Returns:
            DepositEvent: New DepositEvent instance
        """
        return cls(data)