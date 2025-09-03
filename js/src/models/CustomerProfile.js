/**
 * Customer Profile Model
 * Represents a customer profile for the Data Pipeline API
 */
class CustomerProfile {
  constructor(data = {}) {
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.username = data.username;
    this.full_name = data.full_name;
    this.first_name = data.first_name;
    this.last_name = data.last_name;
    this.date_of_birth = data.date_of_birth;
    this.email = data.email;
    this.phone_number = data.phone_number;
    this.gender = data.gender;
    this.country = data.country;
    this.city = data.city;
    this.language = data.language;
    this.currency = data.currency;
    this.marketing_email_preference = data.marketing_email_preference;
    this.notifications_preference = data.notifications_preference;
    this.subscription = data.subscription;
    this.privacy_settings = data.privacy_settings;
    this.deposit_limits = data.deposit_limits;
    this.loss_limits = data.loss_limits;
    this.wagering_limits = data.wagering_limits;
    this.session_time_limits = data.session_time_limits;
    this.cooling_off_period = data.cooling_off_period;
    this.self_exclusion_period = data.self_exclusion_period;
    this.reality_checks_notification = data.reality_checks_notification;
    this.account_status = data.account_status;
    this.vip_status = data.vip_status;
    this.loyalty_program_tiers = data.loyalty_program_tiers;
    this.bonus_abuser = data.bonus_abuser;
    this.financial_risk_level = data.financial_risk_level;
    this.acquisition_source = data.acquisition_source;
    this.partner_id = data.partner_id;
    this.affliate_id = data.affliate_id;
    this.referral_link_code = data.referral_link_code;
    this.referral_limit_reached = data.referral_limit_reached;
    this.creation_timestamp = data.creation_timestamp;
    this.phone_verification = data.phone_verification;
    this.email_verification = data.email_verification;
    this.bank_verification = data.bank_verification;
    this.iddoc_verification = data.iddoc_verification;
    this.cooling_off_expiry_date = data.cooling_off_expiry_date;
    this.self_exclusion_expiry_date = data.self_exclusion_expiry_date;
    this.risk_score_level = data.risk_score_level;
    this.marketing_sms_preference = data.marketing_sms_preference;
    this.custom_data = data.custom_data;
    this.self_exclusion_by = data.self_exclusion_by;
    this.self_exclusion_by_type = data.self_exclusion_by_type;
    this.self_exclusion_check_time = data.self_exclusion_check_time;
    this.self_exclusion_created_time = data.self_exclusion_created_time;
    this.closed_time = data.closed_time;
    this.real_money_enabled = data.real_money_enabled;
    this.push_token = data.push_token;
  }

  /**
   * Validates the customer profile data
   * @returns {Object} Validation result with isValid boolean and errors array
   */
  validate() {
    const errors = [];

    // Required fields
    if (!this.account_id) errors.push('account_id is required');
    if (!this.workspace_id) errors.push('workspace_id is required');
    if (!this.user_id) errors.push('user_id is required');
    if (!this.username) errors.push('username is required');
    if (!this.email) errors.push('email is required');

    // Email format validation
    if (this.email && !this.isValidEmail(this.email)) {
      errors.push('email must be a valid email address');
    }

    // Date format validation
    if (this.date_of_birth && !this.isValidDate(this.date_of_birth)) {
      errors.push('date_of_birth must be in YYYY-MM-DD format');
    }

    // Enum validations
    if (this.gender && !['Male', 'Female', 'Other'].includes(this.gender)) {
      errors.push('gender must be one of: Male, Female, Other');
    }

    if (this.account_status && !['Active', 'Inactive', 'Suspended', 'Closed'].includes(this.account_status)) {
      errors.push('account_status must be one of: Active, Inactive, Suspended, Closed');
    }

    if (this.vip_status && !['Regular', 'Silver', 'Gold', 'Platinum', 'Diamond'].includes(this.vip_status)) {
      errors.push('vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  /**
   * Validates email format
   * @param {string} email - Email to validate
   * @returns {boolean} True if email is valid
   */
  isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Validates date format
   * @param {string} date - Date to validate
   * @returns {boolean} True if date is valid
   */
  isValidDate(date) {
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(date)) return false;
    
    const d = new Date(date);
    return d instanceof Date && !isNaN(d) && d.toISOString().slice(0, 10) === date;
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
   * Creates a CustomerProfile instance from plain object
   * @param {Object} data - Plain object data
   * @returns {CustomerProfile} New CustomerProfile instance
   */
  static fromObject(data) {
    return new CustomerProfile(data);
  }
}

module.exports = CustomerProfile;

