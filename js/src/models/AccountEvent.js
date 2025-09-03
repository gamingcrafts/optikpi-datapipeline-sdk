/**
 * Account Event Model
 * Represents account-related events for the Data Pipeline API
 */
class AccountEvent {
  constructor(data = {}) {
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.event_category = data.event_category || 'Account';
    this.event_name = data.event_name;
    this.event_id = data.event_id;
    this.event_time = data.event_time;
    this.device = data.device;
    this.status = data.status;
    this.affiliate_id = data.affiliate_id;
    this.partner_id = data.partner_id;
    this.campaign_code = data.campaign_code;
    this.reason = data.reason;
  }

  /**
   * Validates the account event data
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
    if (this.event_category && this.event_category !== 'Account') {
      errors.push('event_category must be "Account" for account events');
    }

    // Event name validation
    const validEventNames = [
      'Player Registration',
      'Account Verification',
      'Password Change',
      'Email Update',
      'Phone Update',
      'Account Suspension',
      'Account Reactivation',
      'Profile Update',
      'Login',
      'Logout'
    ];

    if (this.event_name && !validEventNames.includes(this.event_name)) {
      errors.push(`event_name must be one of: ${validEventNames.join(', ')}`);
    }

    // Status validation
    if (this.status && !['verified', 'pending', 'failed', 'completed'].includes(this.status)) {
      errors.push('status must be one of: verified, pending, failed, completed');
    }

    // Device validation
    if (this.device && !['desktop', 'mobile', 'tablet', 'app'].includes(this.device)) {
      errors.push('device must be one of: desktop, mobile, tablet, app');
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
   * Creates an AccountEvent instance from plain object
   * @param {Object} data - Plain object data
   * @returns {AccountEvent} New AccountEvent instance
   */
  static fromObject(data) {
    return new AccountEvent(data);
  }
}

module.exports = AccountEvent;

