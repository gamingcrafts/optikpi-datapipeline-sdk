import re
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional, Union


class GamingActivityEvent:
    """
    Gaming Activity Event Model
    Represents gaming activity events for the Data Pipeline API
    """

    def __init__(self, data: Optional[Dict[str, Any]] = None) -> None:
        if data is None:
            data = {}

        self.account_id: Optional[str] = data.get("account_id")
        self.workspace_id: Optional[str] = data.get("workspace_id")
        self.user_id: Optional[str] = data.get("user_id")
        self.event_category: str = data.get("event_category", "Gaming Activity")
        self.event_name: Optional[str] = data.get("event_name")
        self.event_id: Optional[str] = data.get("event_id")
        self.event_time: Optional[str] = data.get("event_time")
        self.wager_amount: Optional[Union[int, float]] = data.get("wager_amount")
        self.win_amount: Optional[Union[int, float]] = data.get("win_amount")
        self.game_id: Optional[str] = data.get("game_id")
        self.game_title: Optional[str] = data.get("game_title")
        self.provider: Optional[str] = data.get("provider")
        self.game_type: Optional[str] = data.get("game_type")
        self.session_id: Optional[str] = data.get("session_id")
        self.round_id: Optional[str] = data.get("round_id")
        self.device: Optional[str] = data.get("device")
        self.platform: Optional[str] = data.get("platform")
        self.currency: Optional[str] = data.get("currency")
        self.bet_type: Optional[str] = data.get("bet_type")
        self.payout_multiplier: Optional[Union[int, float]] = data.get("payout_multiplier")

    def validate(self) -> Dict[str, Union[bool, List[str]]]:
        """
        Validates the gaming activity event data

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
        if not self.game_id:
            errors.append("game_id is required")
        if not self.game_title:
            errors.append("game_title is required")

        # Event category validation
        if self.event_category != "Gaming Activity":
            errors.append('event_category must be "Gaming Activity" for gaming events')

        # Event name validation
        valid_event_names = {
            "Play Casino Game",
            "Game Win",
            "Game Loss",
            "Game Draw",
            "Bonus Game",
            "Free Spins",
            "Tournament Entry",
            "Tournament Win",
            "Progressive Jackpot",
            "Side Bet"
        }
        if self.event_name and self.event_name not in valid_event_names:
            errors.append(f"event_name must be one of: {', '.join(valid_event_names)}")

        # Wager amount validation (non-negative)
        if self.wager_amount is not None:
            if not isinstance(self.wager_amount, (int, float)) or self.wager_amount < 0:
                errors.append("wager_amount must be a non-negative number")

        # Win amount validation (non-negative)
        if self.win_amount is not None:
            if not isinstance(self.win_amount, (int, float)) or self.win_amount < 0:
                errors.append("win_amount must be a non-negative number")

        # Game type validation
        valid_game_types = {
            "slots",
            "table_games",
            "card_games",
            "live_casino",
            "bingo",
            "scratch_cards",
            "lottery",
            "sports_betting",
            "virtual_sports"
        }
        if self.game_type and self.game_type not in valid_game_types:
            errors.append(f"game_type must be one of: {', '.join(valid_game_types)}")

        # Device validation
        valid_devices = {"desktop", "mobile", "tablet", "app"}
        if self.device and self.device not in valid_devices:
            errors.append("device must be one of: desktop, mobile, tablet, app")

        # Platform validation
        valid_platforms = {
            "web",
            "ios",
            "android",
            "windows",
            "mac",
            "linux"
        }
        if self.platform and self.platform not in valid_platforms:
            errors.append(f"platform must be one of: {', '.join(valid_platforms)}")

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
            dict: Dictionary representation of the gaming activity event
        """
        result = {}
        for key, value in self.__dict__.items():
            if value is not None:
                result[key] = value
        return result

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> "GamingActivityEvent":
        """
        Creates a GamingActivityEvent instance from a plain dictionary

        Args:
            data (dict): Dictionary containing gaming activity event data

        Returns:
            GamingActivityEvent: New GamingActivityEvent instance
        """
        return cls(data)