from dataclasses import dataclass, field, asdict
from typing import Optional, Dict, Any, List
import re
from datetime import datetime


@dataclass
class WithdrawEvent:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_category: str = "Withdraw"
    event_name: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    amount: Optional[float] = None
    payment_method: Optional[str] = None
    transaction_id: Optional[str] = None
    failure_reason: Optional[str] = None

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
        if self.amount is None:
            errors.append("amount is required")
        if not self.payment_method:
            errors.append("payment_method is required")
        if not self.transaction_id:
            errors.append("transaction_id is required")

        # Event category validation
        if self.event_category and self.event_category != "Withdraw":
            errors.append('event_category must be "Withdraw" for withdraw events')

        # Event name validation
        valid_event_names = [
            "Successful Withdrawal",
            "Failed Withdrawal",
            "Pending Withdrawal",
            "Withdrawal Cancelled",
            "Withdrawal Rejected"
        ]
        if self.event_name and self.event_name not in valid_event_names:
            errors.append(f"event_name must be one of: {', '.join(valid_event_names)}")

        # Amount validation
        if self.amount is not None and self.amount <= 0:
            errors.append("amount must be a positive number")

        # Payment method validation
        valid_payment_methods = [
            "bank", "credit_card", "debit_card", "e_wallet",
            "crypto", "paypal", "skrill", "neteller"
        ]
        if self.payment_method and self.payment_method not in valid_payment_methods:
            errors.append(f"payment_method must be one of: {', '.join(valid_payment_methods)}")


        # Date format validation
        if self.event_time and not self.is_valid_datetime(self.event_time):
            errors.append("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)")

        return {"isValid": len(errors) == 0, "errors": errors}

    @staticmethod
    def is_valid_datetime(date_time: str) -> bool:
        try:
            datetime.fromisoformat(date_time.replace("Z", "+00:00"))
            return True
        except ValueError:
            return False

    # -------------------------------
    # Utility methods
    # -------------------------------
    def to_dict(self) -> Dict[str, Any]:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> "WithdrawEvent":
        return cls(**data)

    def __repr__(self) -> str:
        return f"WithdrawEvent(event_id={self.event_id}, event_name={self.event_name}, user_id={self.user_id})"
