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

