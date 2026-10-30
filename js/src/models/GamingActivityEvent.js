/**
 * Gaming Activity Event Model
 * Represents gaming activity events for the Data Pipeline API
 */
const { validateStorage, storageRecord } = require('./bigQueryValidation');

// Fields and types the warehouse accepts for this record; anything else is rejected there.
const STORAGE_RULES = {
  required: ['workspace_id', 'user_id', 'event_id', 'event_time', 'event_category', 'event_name'],
  types: {
    workspace_id: 'STRING',
    user_id: 'STRING',
    event_id: 'STRING',
    event_time: 'TIMESTAMP',
    event_category: 'STRING',
    event_name: 'STRING',
    free_spin_id: 'STRING',
    num_spins_played: 'INTEGER',
    game_id: 'STRING',
    game_title: 'STRING',
    game_theme: 'STRING',
    provider: 'STRING',
    win_amount: 'NUMERIC',
    remaining_spins: 'INTEGER',
    bet_value_per_spin: 'NUMERIC',
    wagering_requirements_met: 'BOOLEAN',
    free_spin_expiry_date: 'TIMESTAMP',
    campaign_id: 'STRING',
    campaign_name: 'STRING',
    rtp: 'NUMERIC',
    jackpot_amount: 'NUMERIC',
    game_category: 'STRING',
    winning_bet_amount: 'NUMERIC',
    jackpot_type: 'STRING',
    wager_amount: 'NUMERIC',
    loss_amount: 'NUMERIC',
    volatility: 'STRING',
    min_bet: 'NUMERIC',
    max_bet: 'NUMERIC',
    number_of_reels: 'INTEGER',
    number_of_paylines: 'INTEGER',
    feature_types: 'STRING',
    game_release_date: 'TIMESTAMP',
    live_dealer_availability: 'BOOLEAN',
    side_bets_availability: 'BOOLEAN',
    multiplayer_option: 'BOOLEAN',
    auto_play: 'BOOLEAN',
    poker_variant: 'STRING',
    tournament_name: 'STRING',
    buy_in_amount: 'NUMERIC',
    table_type: 'STRING',
    stakes_level: 'STRING',
    number_of_players: 'INTEGER',
    game_duration: 'INTEGER',
    hand_volume: 'INTEGER',
    player_position: 'STRING',
    final_hand: 'STRING',
    rake_contribution: 'NUMERIC',
    multi_tabling_indicator: 'BOOLEAN',
    session_result: 'STRING',
    vip_status: 'STRING',
    blind_level: 'STRING',
    rebuy_and_addon_info: 'STRING',
    sport_type: 'STRING',
    betting_market: 'STRING',
    odds: 'NUMERIC',
    live_betting_availability: 'BOOLEAN',
    result: 'STRING',
    bet_status: 'STRING',
    betting_channel: 'STRING',
    bonus_type: 'STRING',
    bonus_amount: 'NUMERIC',
    bonus_code: 'STRING',
    free_spin_start_date: 'TIMESTAMP',
    num_spins_awarded: 'INTEGER',
    parent_game_category: 'STRING',
    currency: 'STRING',
    money_type: 'STRING',
    transaction_type: 'STRING'
  },
  partition: 'event_time'
};

class GamingActivityEvent {
  constructor(data = {}) {
    // The record as sent, so validation also covers fields this model does not declare.
    Object.defineProperty(this, '_input', { value: data, enumerable: false });
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.event_category = data.event_category;
    this.event_name = data.event_name;
    this.event_id = data.event_id;
    this.event_time = data.event_time;
    this.wager_amount = data.wager_amount;
    this.win_amount = data.win_amount;
    this.game_id = data.game_id;
    this.game_title = data.game_title;
    this.provider = data.provider;
    this.currency = data.currency;
    this.loss_amount = data.loss_amount;
    this.bonus_id = data.bonus_id;
    this.free_spin_id = data.free_spin_id;
    this.jackpot_amount = data.jackpot_amount;
    this.tournament_name = data.tournament_name;
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

    for (const error of validateStorage(storageRecord(this, this._input), STORAGE_RULES)) {
      if (!errors.includes(error)) errors.push(error);
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

