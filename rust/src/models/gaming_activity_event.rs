use serde::{Deserialize, Serialize};

use super::{is_valid_datetime, ok, ValidationResult};

/// Gaming activity event: game plays, wins, losses, and session data across
/// slots, sports betting, and poker.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct GamingActivityEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub event_category: String,
    pub event_name: String,
    pub event_id: String,
    pub event_time: String,
    pub game_id: String,
    pub game_title: String,

    #[serde(skip_serializing_if = "Option::is_none")]
    pub wager_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub win_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub loss_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub provider: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bonus_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub free_spin_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub jackpot_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub num_spins_played: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub game_theme: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub remaining_spins: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bet_value_per_spin: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub wagering_requirements_met: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub free_spin_expiry_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub campaign_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub campaign_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub rtp: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub game_category: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub winning_bet_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub jackpot_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub volatility: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub min_bet: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub max_bet: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub number_of_reels: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub number_of_paylines: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub feature_types: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub game_release_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub live_dealer_availability: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub side_bets_availability: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub multiplayer_option: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub auto_play: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub poker_variant: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub tournament_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub buy_in_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub table_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub stakes_level: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub number_of_players: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub game_duration: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub hand_volume: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub player_position: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub final_hand: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub rake_contribution: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub multi_tabling_indicator: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub session_result: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub vip_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub blind_level: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub rebuy_and_addon_info: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub sport_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub betting_market: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub odds: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub live_betting_availability: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub result: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bet_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub betting_channel: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bonus_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bonus_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub free_spin_start_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub num_spins_awarded: Option<i64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bonus_code: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub parent_game_category: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub currency: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub money_type: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub transaction_type: Option<String>,
}

impl GamingActivityEvent {
    #[allow(clippy::too_many_arguments)]
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
        game_id: impl Into<String>,
        game_title: impl Into<String>,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            event_category: "Gaming Activity".to_string(),
            event_name: event_name.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            game_id: game_id.into(),
            game_title: game_title.into(),
            wager_amount: None,
            win_amount: None,
            loss_amount: None,
            provider: None,
            bonus_id: None,
            free_spin_id: None,
            jackpot_amount: None,
            num_spins_played: None,
            game_theme: None,
            remaining_spins: None,
            bet_value_per_spin: None,
            wagering_requirements_met: None,
            free_spin_expiry_date: None,
            campaign_id: None,
            campaign_name: None,
            rtp: None,
            game_category: None,
            winning_bet_amount: None,
            jackpot_type: None,
            volatility: None,
            min_bet: None,
            max_bet: None,
            number_of_reels: None,
            number_of_paylines: None,
            feature_types: None,
            game_release_date: None,
            live_dealer_availability: None,
            side_bets_availability: None,
            multiplayer_option: None,
            auto_play: None,
            poker_variant: None,
            tournament_name: None,
            buy_in_amount: None,
            table_type: None,
            stakes_level: None,
            number_of_players: None,
            game_duration: None,
            hand_volume: None,
            player_position: None,
            final_hand: None,
            rake_contribution: None,
            multi_tabling_indicator: None,
            session_result: None,
            vip_status: None,
            blind_level: None,
            rebuy_and_addon_info: None,
            sport_type: None,
            betting_market: None,
            odds: None,
            live_betting_availability: None,
            result: None,
            bet_status: None,
            betting_channel: None,
            bonus_type: None,
            bonus_amount: None,
            free_spin_start_date: None,
            num_spins_awarded: None,
            bonus_code: None,
            parent_game_category: None,
            currency: None,
            money_type: None,
            transaction_type: None,
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
        if self.event_name.is_empty() {
            errors.push("event_name is required".to_string());
        }
        if self.event_id.is_empty() {
            errors.push("event_id is required".to_string());
        }
        if self.event_time.is_empty() {
            errors.push("event_time is required".to_string());
        }
        if self.game_id.is_empty() {
            errors.push("game_id is required".to_string());
        }
        if self.game_title.is_empty() {
            errors.push("game_title is required".to_string());
        }
        if self.event_category != "Gaming Activity" {
            errors.push(r#"event_category must be "Gaming Activity" for gaming events"#.to_string());
        }
        if let Some(v) = self.wager_amount {
            if v < 0.0 {
                errors.push("wager_amount must be a non-negative number".to_string());
            }
        }
        if let Some(v) = self.win_amount {
            if v < 0.0 {
                errors.push("win_amount must be a non-negative number".to_string());
            }
        }
        if let Some(currency) = &self.currency {
            if !is_iso_currency(currency) {
                errors.push("currency must be a valid 3-letter ISO currency code".to_string());
            }
        }
        if !self.event_time.is_empty() && !is_valid_datetime(&self.event_time) {
            errors.push("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)".to_string());
        }

        ok(errors)
    }
}

fn is_iso_currency(s: &str) -> bool {
    s.len() == 3 && s.chars().all(|c| c.is_ascii_uppercase())
}
