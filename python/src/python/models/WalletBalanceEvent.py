from dataclasses import dataclass, asdict
from typing import Optional, Dict, Any, List
from datetime import datetime
import re


@dataclass
class WalletBalanceEvent:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_category: str = "Wallet Balance"
    event_name: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    wallet_type: Optional[str] = None
    currency: Optional[str] = None
    current_cash_balance: Optional[float] = None
    current_bonus_balance: Optional[float] = None
    current_total_balance: Optional[float] = None
    blocked_amount: Optional[float] = None

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
        if not self.event_name:
            errors.append("event_name is required")
        if not self.event_id:
            errors.append("event_id is required")
        if not self.event_time:
            errors.append("event_time is required")

        # Event category validation
        if self.event_category and self.event_category != "Wallet Balance":
            errors.append('event_category must be "Wallet Balance" for wallet events')

        # Date validation
        if self.event_time and not self.is_valid_datetime(self.event_time):
            errors.append("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)")

        # Currency validation
        if self.currency and not self.is_valid_currency(self.currency):
            errors.append("currency must be a valid 3-letter ISO currency code")

        # Non-negative balance validations
        for field in [
            "current_cash_balance",
            "current_bonus_balance",
            "current_total_balance",
            "blocked_amount",
        ]:
            value = getattr(self, field)
            if value is not None and (not isinstance(value, (int, float)) or value < 0):
                errors.append(f"{field} must be a non-negative number")

        return {"isValid": len(errors) == 0, "errors": errors}

    # -------------------------------
    # Utility methods
    # -------------------------------
    @staticmethod
    def is_valid_datetime(date_time: str) -> bool:
        try:
            datetime.fromisoformat(date_time.replace("Z", "+00:00"))
            return True
        except ValueError:
            return False

    @staticmethod
    def is_valid_currency(currency: str) -> bool:
        return re.match(r"^[A-Z]{3}$", currency) is not None

    def to_dict(self) -> Dict[str, Any]:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> "WalletBalanceEvent":
        return cls(**data)

    def __repr__(self) -> str:
        return (
            f"WalletBalanceEvent(event_id={self.event_id}, "
            f"event_name={self.event_name}, user_id={self.user_id})"
        )
