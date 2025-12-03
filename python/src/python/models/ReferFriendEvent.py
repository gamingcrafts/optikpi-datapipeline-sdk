from dataclasses import dataclass, asdict
from typing import Optional, Dict, Any, List
from datetime import datetime
import re


@dataclass
class ReferFriendEvent:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_category: str = "Refer Friend"
    event_name: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    referral_code_used: Optional[str] = None
    successful_referral_confirmation: Optional[bool] = None
    reward_type: Optional[str] = None
    reward_claimed_status: Optional[str] = None
    referee_user_id: Optional[str] = None
    referee_registration_date: Optional[str] = None
    referee_first_deposit: Optional[float] = None

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
        if self.event_category and self.event_category != "Refer Friend":
            errors.append('event_category must be "Refer Friend" for refer friend events')

        # Date validations
        for field in ["event_time", "referee_registration_date"]:
            dt = getattr(self, field)
            if dt and not self.is_valid_datetime(dt):
                errors.append(
                    f"{field} must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)"
                )

        # Boolean validation
        if (
            self.successful_referral_confirmation is not None
            and not isinstance(self.successful_referral_confirmation, bool)
        ):
            errors.append("successful_referral_confirmation must be boolean")

        # Reward type validation
        valid_reward_types = ["bonus", "cash", "points", "free_spins", "other"]
        if self.reward_type and self.reward_type not in valid_reward_types:
            errors.append(
                "reward_type must be one of: " + ", ".join(valid_reward_types)
            )

        # Reward claimed status validation
        valid_claimed_status = ["pending", "claimed", "expired", "cancelled"]
        if (
            self.reward_claimed_status
            and self.reward_claimed_status not in valid_claimed_status
        ):
            errors.append(
                "reward_claimed_status must be one of: "
                + ", ".join(valid_claimed_status)
            )

        # Non-negative first deposit validation
        if self.referee_first_deposit is not None:
            if not isinstance(self.referee_first_deposit, (int, float)) or self.referee_first_deposit < 0:
                errors.append("referee_first_deposit must be a non-negative number")

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

    def to_dict(self) -> Dict[str, Any]:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> "ReferFriendEvent":
        return cls(**data)

    def __repr__(self) -> str:
        return (
            f"ReferFriendEvent(event_id={self.event_id}, "
            f"event_name={self.event_name}, user_id={self.user_id})"
        )
