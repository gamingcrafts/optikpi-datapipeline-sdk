/**
 * Gaming Activity Event Model
 * Represents gaming activity events for the Data Pipeline API
 */
class GamingActivityEvent {
  constructor(data = {}) {
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.event_category = data.event_category || 'Gaming Activity';
    this.event_name = data.event_name;
    this.event_id = data.event_id;
    this.event_time = data.event_time;
    this.wager_amount = data.wager_amount;
    this.win_amount = data.win_amount;
    this.game_id = data.game_id;
    this.game_title = data.game_title;
    this.provider = data.provider;
    this.game_type = data.game_type;
    this.session_id = data.session_id;
    this.round_id = data.round_id;
    this.device = data.device;
    this.platform = data.platform;
    this.currency = data.currency;
    this.bet_type = data.bet_type;
    this.payout_multiplier = data.payout_multiplier;
    this.loss_amount = data.loss_amount;
    this.bonus_id = data.bonus_id;
    this.free_spin_id = data.free_spin_id;
    this.num_spins_played = data.num_spins_played;
    this.game_theme = data.game_theme;
    this.remaining_spins = data.remaining_spins;
    this.bet_value_per_spin = data.bet_value_per_spin;
    this.wagering_requirements_met = data.wagering_requirements_met;
    this.free_spin_expiry_date = data.free_spin_expiry_date;
    this.campaign_id = data.campaign_id;
    this.campaign_name = data.campaign_name;
    this.rtp = data.rtp;
    this.game_category = data.game_category;
    this.winning_bet_amount = data.winning_bet_amount;
    this.jackpot_type = data.jackpot_type;
    this.volatility = data.volatility;
    this.min_bet = data.min_bet;
    this.max_bet = data.max_bet;
    this.number_of_reels = data.number_of_reels;
    this.number_of_paylines = data.number_of_paylines;
    this.feature_types = data.feature_types;
    this.game_release_date = data.game_release_date;
    this.live_dealer_availability = data.live_dealer_availability;
    this.side_bets_availability = data.side_bets_availability;
    this.multiplayer_option = data.multiplayer_option;
    this.auto_play = data.auto_play;
    this.poker_variant = data.poker_variant;
    this.buy_in_amount = data.buy_in_amount;
    this.table_type = data.table_type;
    this.stakes_level = data.stakes_level;
    this.number_of_players = data.number_of_players;
    this.game_duration = data.game_duration;
    this.hand_volume = data.hand_volume;
    this.player_position = data.player_position;
    this.final_hand = data.final_hand;
    this.rake_contribution = data.rake_contribution;
    this.multi_tabling_indicator = data.multi_tabling_indicator;
    this.session_result = data.session_result;
    this.vip_status = data.vip_status;
    this.blind_level = data.blind_level;
    this.rebuy_and_addon_info = data.rebuy_and_addon_info;
    this.sport_type = data.sport_type;
    this.betting_market = data.betting_market;
    this.odds = data.odds;
    this.live_betting_availability = data.live_betting_availability;
    this.result = data.result;
    this.bet_status = data.bet_status;
    this.betting_channel = data.betting_channel;
    this.bonus_type = data.bonus_type;
    this.bonus_amount = data.bonus_amount;
    this.free_spin_start_date = data.free_spin_start_date;
    this.num_spins_awarded = data.num_spins_awarded;
    this.bonus_code = data.bonus_code;
    this.parent_game_category = data.parent_game_category;
    this.money_type = data.money_type;
    this.transaction_type = data.transaction_type;
  }

  /**
   * Validates the gaming activity event data
   * @returns {Object} Validation result with isValid boolean and errors array
   */
  validate() {
    const errors = [];

    // Required fields
    if (!this.account_id) errors.push('account_id is required');
    if (!this.workspace_id) errors.push('workspace_id is required');
    if (!this.user_id) errors.push('user_id is required');
    if (!this.event_name) errors.push('event_name is required');
    if (!this.event_id) errors.push('event_id is required');
    if (!this.event_time) errors.push('event_time is required');
    if (!this.game_id) errors.push('game_id is required');
    if (!this.game_title) errors.push('game_title is required');

    // Event category validation
    if (this.event_category && this.event_category !== 'Gaming Activity') {
      errors.push('event_category must be "Gaming Activity" for gaming events');
    }

    // Event name validation
    const validEventNames = [
      'Play Casino Game',
      'Game Win',
      'Game Loss',
      'Game Draw',
      'Bonus Game',
      'Free Spins',
      'Tournament Entry',
      'Tournament Win',
      'Progressive Jackpot',
      'Side Bet'
    ];

    if (this.event_name && !validEventNames.includes(this.event_name)) {
      errors.push(`event_name must be one of: ${validEventNames.join(', ')}`);
    }

    // Amount validations
    if (this.wager_amount && (typeof this.wager_amount !== 'number' || this.wager_amount < 0)) {
      errors.push('wager_amount must be a non-negative number');
    }

    if (this.win_amount && (typeof this.win_amount !== 'number' || this.win_amount < 0)) {
      errors.push('win_amount must be a non-negative number');
    }

    // Game type validation
    const validGameTypes = [
      'slots',
      'table_games',
      'card_games',
      'live_casino',
      'bingo',
      'scratch_cards',
      'lottery',
      'sports_betting',
      'virtual_sports'
    ];

    if (this.game_type && !validGameTypes.includes(this.game_type)) {
      errors.push(`game_type must be one of: ${validGameTypes.join(', ')}`);
    }

    // Device validation
    if (this.device && !['desktop', 'mobile', 'tablet', 'app'].includes(this.device)) {
      errors.push('device must be one of: desktop, mobile, tablet, app');
    }

    // Platform validation
    const validPlatforms = [
      'web',
      'ios',
      'android',
      'windows',
      'mac',
      'linux'
    ];

    if (this.platform && !validPlatforms.includes(this.platform)) {
      errors.push(`platform must be one of: ${validPlatforms.join(', ')}`);
    }

    // Currency validation
    if (this.currency && !this.isValidCurrency(this.currency)) {
      errors.push('currency must be a valid 3-letter ISO currency code');
    }

    // Date format validation
    if (this.event_time && !this.isValidDateTime(this.event_time)) {
      errors.push('event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  /**
   * Validates ISO 8601 datetime format
   * @param {string} dateTime - DateTime string to validate
   * @returns {boolean} True if datetime is valid
   */
  isValidDateTime(dateTime) {
    const date = new Date(dateTime);
    return date instanceof Date && !isNaN(date);
  }

  /**
   * Validates currency code format
   * @param {string} currency - Currency code to validate
   * @returns {boolean} True if currency code is valid
   */
  isValidCurrency(currency) {
    const currencyRegex = /^[A-Z]{3}$/;
    return currencyRegex.test(currency);
  }

  /**
   * Converts the model to a plain object
   * @returns {Object} Plain object representation
   */
  toJSON() {
    const obj = {};
    Object.keys(this).forEach(key => {
      if (this[key] !== undefined) {
        obj[key] = this[key];
      }
    });
    return obj;
  }

  /**
   * Creates a GamingActivityEvent instance from plain object
   * @param {Object} data - Plain object data
   * @returns {GamingActivityEvent} New GamingActivityEvent instance
   */
  static fromObject(data) {
    return new GamingActivityEvent(data);
  }
}

module.exports = GamingActivityEvent;

