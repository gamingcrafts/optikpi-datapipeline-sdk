from dataclasses import dataclass, field, asdict
from typing import Optional, Dict, Any, List
import re
from datetime import datetime

from .bigquery_validation import merge_storage_errors, model_from_raw

# Fields and types the warehouse accepts for this record; anything else is rejected there.
STORAGE_RULES = {
    "required": ["workspace_id", "user_id", "event_id", "event_time", "event_category", "event_name"],
    "types": {
        "workspace_id": "STRING",
        "user_id": "STRING",
        "event_id": "STRING",
        "event_time": "TIMESTAMP",
        "event_category": "STRING",
        "event_name": "STRING",
        "free_spin_id": "STRING",
        "num_spins_played": "INTEGER",
        "game_id": "STRING",
        "game_title": "STRING",
        "game_theme": "STRING",
        "provider": "STRING",
        "win_amount": "NUMERIC",
        "remaining_spins": "INTEGER",
        "bet_value_per_spin": "NUMERIC",
        "wagering_requirements_met": "BOOLEAN",
        "free_spin_expiry_date": "TIMESTAMP",
        "campaign_id": "STRING",
        "campaign_name": "STRING",
        "rtp": "NUMERIC",
        "jackpot_amount": "NUMERIC",
        "game_category": "STRING",
        "winning_bet_amount": "NUMERIC",
        "jackpot_type": "STRING",
        "wager_amount": "NUMERIC",
        "loss_amount": "NUMERIC",
        "volatility": "STRING",
        "min_bet": "NUMERIC",
        "max_bet": "NUMERIC",
        "number_of_reels": "INTEGER",
        "number_of_paylines": "INTEGER",
        "feature_types": "STRING",
        "game_release_date": "TIMESTAMP",
        "live_dealer_availability": "BOOLEAN",
        "side_bets_availability": "BOOLEAN",
        "multiplayer_option": "BOOLEAN",
        "auto_play": "BOOLEAN",
        "poker_variant": "STRING",
        "tournament_name": "STRING",
        "buy_in_amount": "NUMERIC",
        "table_type": "STRING",
        "stakes_level": "STRING",
        "number_of_players": "INTEGER",
        "game_duration": "INTEGER",
        "hand_volume": "INTEGER",
        "player_position": "STRING",
        "final_hand": "STRING",
        "rake_contribution": "NUMERIC",
        "multi_tabling_indicator": "BOOLEAN",
        "session_result": "STRING",
        "vip_status": "STRING",
        "blind_level": "STRING",
        "rebuy_and_addon_info": "STRING",
        "sport_type": "STRING",
        "betting_market": "STRING",
        "odds": "NUMERIC",
        "live_betting_availability": "BOOLEAN",
        "result": "STRING",
        "bet_status": "STRING",
        "betting_channel": "STRING",
        "bonus_type": "STRING",
        "bonus_amount": "NUMERIC",
        "bonus_code": "STRING",
        "free_spin_start_date": "TIMESTAMP",
        "num_spins_awarded": "INTEGER",
        "parent_game_category": "STRING",
        "currency": "STRING",
        "money_type": "STRING",
        "transaction_type": "STRING"
    },
    "partition": "event_time"
}


@dataclass
class GamingActivityEvent:
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_category: Optional[str] = None
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

        # The record as sent, so validation also covers fields this model does not declare.
        merge_storage_errors(errors, self, STORAGE_RULES)

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
        return model_from_raw(cls, data)

    def __repr__(self) -> str:
        return f"GamingActivityEvent(event_id={self.event_id}, event_name={self.event_name}, user_id={self.user_id})"
