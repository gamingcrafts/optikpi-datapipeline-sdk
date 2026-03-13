/**
 * System Event Model
 * Represents system-related events for the Data Pipeline API
 */
class SystemEvent {
    constructor(data = {}) {
        this.account_id = data.account_id;
        this.workspace_id = data.workspace_id;
        this.event_category = data.event_category || 'SystemEvent';
        this.event_name = data.event_name;
        this.event_id = data.event_id;
        this.event_time = data.event_time;
        this.event_data = data.event_data;
    }

    /**
     * Validates the system event data
     * @returns {Object} Validation result with isValid boolean and errors array
     */
    validate() {
        const errors = [];

        // Required fields
        if (!this.account_id) errors.push('account_id is required');
        if (!this.workspace_id) errors.push('workspace_id is required');
        if (!this.event_name) errors.push('event_name is required');
        if (!this.event_id) errors.push('event_id is required');
        if (!this.event_time) errors.push('event_time is required');

        // event_data validation
        if (this.event_data === undefined || this.event_data === null) {
            errors.push('event_data is required');
        } else if (typeof this.event_data !== 'string' && (typeof this.event_data !== 'object' || Array.isArray(this.event_data))) {
            errors.push('event_data must be a string or an object');
        }

        // Event category validation
        if (this.event_category && this.event_category !== 'SystemEvent') {
            errors.push('event_category must be "SystemEvent" for system events');
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
     * Creates a SystemEvent instance from plain object
     * @param {Object} data - Plain object data
     * @returns {SystemEvent} New SystemEvent instance
     */
    static fromObject(data) {
        return new SystemEvent(data);
    }
}

module.exports = SystemEvent;
