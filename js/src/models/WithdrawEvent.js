/**
 * Withdraw Event Model
 * Represents withdrawal-related events for the Data Pipeline API
 */
class WithdrawEvent {
  constructor(data = {}) {
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.event_category = data.event_category || 'Withdraw';
    this.event_name = data.event_name;
    this.event_id = data.event_id;
    this.event_time = data.event_time;
    this.amount = data.amount;
    this.payment_method = data.payment_method;
    this.transaction_id = data.transaction_id;
    this.status = data.status;
    this.currency = data.currency;
    this.fees = data.fees;
    this.net_amount = data.net_amount;
    this.withdrawal_reason = data.withdrawal_reason;
    this.processing_time = data.processing_time;
    this.failure_reason = data.failure_reason;
  }

  /**
   * Validates the withdraw event data
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
    if (!this.amount) errors.push('amount is required');
    if (!this.payment_method) errors.push('payment_method is required');
    if (!this.transaction_id) errors.push('transaction_id is required');

    // Event category validation
    if (this.event_category && this.event_category !== 'Withdraw') {
      errors.push('event_category must be "Withdraw" for withdraw events');
    }

    // Event name validation
    const validEventNames = [
      'Successful Withdrawal',
      'Failed Withdrawal',
      'Pending Withdrawal',
      'Withdrawal Cancelled',
      'Withdrawal Rejected'
    ];

    if (this.event_name && !validEventNames.includes(this.event_name)) {
      errors.push(`event_name must be one of: ${validEventNames.join(', ')}`);
    }

    // Amount validation
    if (this.amount && (typeof this.amount !== 'number' || this.amount <= 0)) {
      errors.push('amount must be a positive number');
    }

    // Payment method validation
    const validPaymentMethods = [
      'bank',
      'credit_card',
      'debit_card',
      'e_wallet',
      'crypto',
      'paypal',
      'skrill',
      'neteller'
    ];

    if (this.payment_method && !validPaymentMethods.includes(this.payment_method)) {
      errors.push(`payment_method must be one of: ${validPaymentMethods.join(', ')}`);
    }

    // Status validation
    if (this.status && !['success', 'pending', 'failed', 'cancelled', 'rejected'].includes(this.status)) {
      errors.push('status must be one of: success, pending, failed, cancelled, rejected');
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
   * Creates a WithdrawEvent instance from plain object
   * @param {Object} data - Plain object data
   * @returns {WithdrawEvent} New WithdrawEvent instance
   */
  static fromObject(data) {
    return new WithdrawEvent(data);
  }
}

module.exports = WithdrawEvent;

