from dataclasses import dataclass, field, asdict
from typing import Optional, Dict, Any, List
import re
from datetime import datetime


@dataclass
class GamingActivityEvent:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_category: str = "Gaming Activity"
    event_name: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    wager_amount: Optional[float] = None
    win_amount: Optional[float] = None
    game_id: Optional[str] = None
    game_title: Optional[str] = None
    provider: Optional[str] = None
    currency: Optional[str] = None
    loss_amount: Optional[float] = None
    bonus_id: Optional[str] = None
    free_spin_id: Optional[str] = None
    jackpot_amount: Optional[float] = None
    num_spins_played: Optional[int] = None
    game_theme: Optional[str] = None
    remaining_spins: Optional[int] = None
    bet_value_per_spin: Optional[float] = None
    wagering_requirements_met: Optional[bool] = None
    free_spin_expiry_date: Optional[str] = None
    campaign_id: Optional[str] = None
    campaign_name: Optional[str] = None
    rtp: Optional[float] = None
    game_category: Optional[str] = None
    winning_bet_amount: Optional[float] = None
    jackpot_type: Optional[str] = None
    volatility: Optional[str] = None
    min_bet: Optional[float] = None
    max_bet: Optional[float] = None
    number_of_reels: Optional[int] = None
    number_of_paylines: Optional[int] = None
    feature_types: Optional[str] = None
    game_release_date: Optional[str] = None
    live_dealer_availability: Optional[bool] = None
    side_bets_availability: Optional[bool] = None
    multiplayer_option: Optional[bool] = None
    auto_play: Optional[bool] = None
    poker_variant: Optional[str] = None
    tournament_name: Optional[str] = None
    buy_in_amount: Optional[float] = None
    table_type: Optional[str] = None
    stakes_level: Optional[str] = None
    number_of_players: Optional[int] = None
    game_duration: Optional[int] = None
    hand_volume: Optional[int] = None
    player_position: Optional[str] = None
    final_hand: Optional[str] = None
    rake_contribution: Optional[float] = None
    multi_tabling_indicator: Optional[bool] = None
    session_result: Optional[str] = None
    vip_status: Optional[str] = None
    blind_level: Optional[str] = None
    rebuy_and_addon_info: Optional[str] = None
    sport_type: Optional[str] = None
    betting_market: Optional[str] = None
    odds: Optional[float] = None
    live_betting_availability: Optional[bool] = None
    result: Optional[str] = None
    bet_status: Optional[str] = None
    betting_channel: Optional[str] = None
    bonus_type: Optional[str] = None
    bonus_amount: Optional[float] = None
    free_spin_start_date: Optional[str] = None
    num_spins_awarded: Optional[int] = None
    bonus_code: Optional[str] = None
    parent_game_category: Optional[str] = None
    money_type: Optional[str] = None
    transaction_type: Optional[str] = None

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
        if not self.game_id:
            errors.append("game_id is required")
        if not self.game_title:
            errors.append("game_title is required")

        # Event category validation
        if self.event_category and self.event_category != "Gaming Activity":
            errors.append('event_category must be "Gaming Activity" for gaming events')

        # Event name validation
        valid_event_names = [
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
        ]
        if self.event_name and self.event_name not in valid_event_names:
            errors.append(f"event_name must be one of: {', '.join(valid_event_names)}")

        # Amount validations
        if self.wager_amount is not None and (self.wager_amount < 0):
            errors.append("wager_amount must be a non-negative number")
        if self.win_amount is not None and (self.win_amount < 0):
            errors.append("win_amount must be a non-negative number")

        # Currency validation
        if self.currency and not self.is_valid_currency(self.currency):
            errors.append("currency must be a valid 3-letter ISO currency code")

        # Date validation
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

    @staticmethod
    def is_valid_currency(currency: str) -> bool:
        return re.match(r"^[A-Z]{3}$", currency) is not None

    # -------------------------------
    # Utility methods
    # -------------------------------
    def to_dict(self) -> Dict[str, Any]:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> "GamingActivityEvent":
        return cls(**data)

    def __repr__(self) -> str:
        return f"GamingActivityEvent(event_id={self.event_id}, event_name={self.event_name}, user_id={self.user_id})"
