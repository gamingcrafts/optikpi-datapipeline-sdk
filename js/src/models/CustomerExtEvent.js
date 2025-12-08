/**
 * Customer Extension Event Model
 * Represents customer extended attributes events for the Data Pipeline API
 */
class CustomerExtEvent {
  constructor(data = {}) {
    this.account_id = data.account_id;
    this.workspace_id = data.workspace_id;
    this.user_id = data.user_id;
    this.list_name = data.list_name;
    this.ext_data = data.ext_data;
  }

  /**
   * Validates the customer extension event data
   * @returns {Object} Validation result with isValid boolean and errors array
   */
  validate() {
    const errors = [];

    // Required fields
    if (!this.account_id) errors.push('account_id is required');
    if (!this.workspace_id) errors.push('workspace_id is required');
    if (!this.user_id) errors.push('user_id is required');
    if (!this.list_name) errors.push('list_name is required');
    if (!this.ext_data) errors.push('ext_data is required');

    // Validate ext_data format
    if (this.ext_data) {
      // Check if ext_data is an object or a valid JSON string
      if (typeof this.ext_data === 'string') {
        try {
          JSON.parse(this.ext_data);
        } catch (e) {
          errors.push('ext_data must be a valid JSON string or object');
        }
      } else if (typeof this.ext_data !== 'object' || Array.isArray(this.ext_data)) {
        errors.push('ext_data must be an object or JSON string');
      }
    }

    // Validate list_name format (alphanumeric, underscores, hyphens)
    if (this.list_name && !/^[A-Za-z0-9_-]+$/.test(this.list_name)) {
      errors.push('list_name must contain only alphanumeric characters, underscores, and hyphens');
    }

    // Validate user_id format
    if (this.user_id && typeof this.user_id !== 'string') {
      errors.push('user_id must be a string');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  /**
   * Ensures ext_data is in the correct format for API submission
   * Converts object to JSON string if needed
   * @returns {Object} Event object with properly formatted ext_data
   */
  toAPIFormat() {
    const formatted = { ...this.toJSON() };
    
    // Convert ext_data to JSON string if it's an object
    if (typeof formatted.ext_data === 'object' && !Array.isArray(formatted.ext_data)) {
      formatted.ext_data = JSON.stringify(formatted.ext_data);
    }
    
    return formatted;
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
   * Creates a CustomerExtEvent instance from plain object
   * @param {Object} data - Plain object data
   * @returns {CustomerExtEvent} New CustomerExtEvent instance
   */
  static fromObject(data) {
    return new CustomerExtEvent(data);
  }

  /**
   * Helper method to create a customer extension event with object ext_data
   * @param {Object} params - Event parameters
   * @param {string} params.account_id - Account ID
   * @param {string} params.workspace_id - Workspace ID
   * @param {string} params.user_id - User ID
   * @param {string} params.list_name - List name for extended attributes
   * @param {Object} params.ext_data - Extended attributes as object
   * @returns {CustomerExtEvent} New CustomerExtEvent instance
   */
  static createWithObject(params) {
    return new CustomerExtEvent({
      account_id: params.account_id,
      workspace_id: params.workspace_id,
      user_id: params.user_id,
      list_name: params.list_name,
      ext_data: params.ext_data // Object format
    });
  }

  /**
   * Helper method to create a customer extension event with JSON string ext_data
   * @param {Object} params - Event parameters
   * @param {string} params.account_id - Account ID
   * @param {string} params.workspace_id - Workspace ID
   * @param {string} params.user_id - User ID
   * @param {string} params.list_name - List name for extended attributes
   * @param {string} params.ext_data - Extended attributes as JSON string
   * @returns {CustomerExtEvent} New CustomerExtEvent instance
   */
  static createWithString(params) {
    return new CustomerExtEvent({
      account_id: params.account_id,
      workspace_id: params.workspace_id,
      user_id: params.user_id,
      list_name: params.list_name,
      ext_data: params.ext_data // JSON string format
    });
  }
}

module.exports = CustomerExtEvent;