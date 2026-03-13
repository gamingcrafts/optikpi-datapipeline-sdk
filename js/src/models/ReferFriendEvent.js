/**
 * Refer Friend Event Model
 * Represents refer friend events for the Data Pipeline API
 */
class ReferFriendEvent {
  constructor(data = {}) {
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.event_category = data.event_category || 'Refer Friend';
    this.event_name = data.event_name;
    this.event_id = data.event_id;
    this.event_time = data.event_time;
    this.referral_code_used = data.referral_code_used;
    this.successful_referral_confirmation = data.successful_referral_confirmation;
    this.reward_type = data.reward_type;
    this.reward_claimed_status = data.reward_claimed_status;
    this.referee_user_id = data.referee_user_id;
    this.referee_registration_date = data.referee_registration_date;
    this.referee_first_deposit = data.referee_first_deposit;
  }

  /**
   * Validates the refer friend event data
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

    // Event category validation
    if (this.event_category && this.event_category !== 'Refer Friend') {
      errors.push('event_category must be "Refer Friend" for refer friend events');
    }

    // Date format validation
    if (this.event_time && !this.isValidDateTime(this.event_time)) {
      errors.push('event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)');
    }

    if (this.referee_registration_date && !this.isValidDateTime(this.referee_registration_date)) {
      errors.push('referee_registration_date must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)');
    }

    // Boolean validation
    if (this.successful_referral_confirmation !== null && this.successful_referral_confirmation !== undefined) {
      if (typeof this.successful_referral_confirmation !== 'boolean') {
        errors.push('successful_referral_confirmation must be a boolean');
      }
    }

    // Reward type validation
    const validRewardTypes = ['bonus', 'cash', 'points', 'free_spins', 'other'];
    if (this.reward_type && !validRewardTypes.includes(this.reward_type)) {
      errors.push(`reward_type must be one of: ${validRewardTypes.join(', ')}`);
    }

    // Reward claimed status validation
    const validClaimedStatuses = ['pending', 'claimed', 'expired', 'cancelled'];
    if (this.reward_claimed_status && !validClaimedStatuses.includes(this.reward_claimed_status)) {
      errors.push(`reward_claimed_status must be one of: ${validClaimedStatuses.join(', ')}`);
    }

    // First deposit validation (must be non-negative if provided)
    if (this.referee_first_deposit !== null && this.referee_first_deposit !== undefined) {
      if (typeof this.referee_first_deposit !== 'number' || this.referee_first_deposit < 0) {
        errors.push('referee_first_deposit must be a non-negative number');
      }
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
   * Creates a ReferFriendEvent instance from plain object
   * @param {Object} data - Plain object data
   * @returns {ReferFriendEvent} New ReferFriendEvent instance
   */
  static fromObject(data) {
    return new ReferFriendEvent(data);
  }
}

module.exports = ReferFriendEvent;

