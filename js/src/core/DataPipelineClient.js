const axios = require('axios');
const { generateHmacSignature } = require('../utils/crypto');

/**
 * Data Pipeline API Client
 * Main client class for interacting with the Optikpi Data Pipeline API
 */
class DataPipelineClient {
  constructor(config = {}) {
    this.config = {
      baseURL: config.baseURL || 'https://demo.optikpi.com/apigw/ingest',
      authToken: config.authToken,
      accountId: config.accountId,
      workspaceId: config.workspaceId,
      timeout: config.timeout || 30000,
      retries: config.retries || 3,
      retryDelay: config.retryDelay || 1000,
      ...config
    };

    this.validateConfig();
    this.setupAxiosInstance();
  }

  /**
   * Validates the client configuration
   * @throws {Error} If required configuration is missing
   */
  validateConfig() {
    const { authToken, accountId, workspaceId } = this.config;
    
    if (!authToken) {
      throw new Error('authToken is required');
    }
    if (!accountId) {
      throw new Error('accountId is required');
    }
    if (!workspaceId) {
      throw new Error('workspaceId is required');
    }
  }

  /**
   * Sets up the axios instance with default configuration
   */
  setupAxiosInstance() {
    this.axios = axios.create({
      baseURL: this.config.baseURL,
      timeout: this.config.timeout,
      headers: {
        'Content-Type': 'application/json',
        'User-Agent': 'Optikpi-DataPipeline-SDK/1.0.0'
      }
    });

    // Add request interceptor for authentication
    this.axios.interceptors.request.use(
      (config) => {
        if (config.data && config.method !== 'get') {
          const hmacSignature = generateHmacSignature(
            config.data,
            this.config.authToken,
            this.config.accountId,
            this.config.workspaceId
          );

          config.headers['x-optikpi-token'] = this.config.authToken;
          config.headers['x-optikpi-account-id'] = this.config.accountId;
          config.headers['x-optikpi-workspace-id'] = this.config.workspaceId;
          config.headers['x-hmac-signature'] = hmacSignature;
          config.headers['x-hmac-algorithm'] = 'sha256';
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Add response interceptor for error handling
    this.axios.interceptors.response.use(
      (response) => response,
      async (error) => {
        if (error.response && error.response.status >= 500 && this.config.retries > 0) {
          return this.retryRequest(error.config);
        }
        return Promise.reject(error);
      }
    );
  }

  /**
   * Retries a failed request
   * @param {Object} config - Axios request config
   * @returns {Promise} Axios response
   */
  async retryRequest(config, retryCount = 0) {
    if (retryCount >= this.config.retries) {
      throw new Error(`Request failed after ${this.config.retries} retries`);
    }

    await new Promise(resolve => setTimeout(resolve, this.config.retryDelay * (retryCount + 1)));
    
    try {
      return await this.axios.request(config);
    } catch (error) {
      if (error.response && error.response.status >= 500) {
        return this.retryRequest(config, retryCount + 1);
      }
      throw error;
    }
  }

  /**
   * Performs a health check on the API
   * @returns {Promise<Object>} Health check response
   */
  async healthCheck() {
    try {
      const response = await this.axios.get('/datapipeline/health');
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends customer profile data
   * @param {Object|Array} data - Customer profile data or array of profiles
   * @returns {Promise<Object>} API response
   */
  async sendCustomerProfile(data) {
    try {
      const response = await this.axios.post('/customers', data);
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        data: error.response?.data,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends account event data
   * @param {Object|Array} data - Account event data or array of events
   * @returns {Promise<Object>} API response
   */
  async sendAccountEvent(data) {
    try {
      const response = await this.axios.post('/events/account', data);
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        data: error.response?.data,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends deposit event data
   * @param {Object|Array} data - Deposit event data or array of events
   * @returns {Promise<Object>} API response
   */
  async sendDepositEvent(data) {
    try {
      const response = await this.axios.post('/events/deposit', data);
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        data: error.response?.data,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends withdrawal event data
   * @param {Object|Array} data - Withdrawal event data or array of events
   * @returns {Promise<Object>} API response
   */
  async sendWithdrawEvent(data) {
    try {
      const response = await this.axios.post('/events/withdraw', data);
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        data: error.response?.data,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends gaming activity event data
   * @param {Object|Array} data - Gaming activity event data or array of events
   * @returns {Promise<Object>} API response
   */
  async sendGamingActivityEvent(data) {
    try {
      const response = await this.axios.post('/events/gaming-activity', data);
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        data: error.response?.data,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends extended attributes data
   * @param {Object|Array} data - Extended attributes data or array of attributes
   * @returns {Promise<Object>} API response
   */
  async sendExtendedAttributes(data) {
    try {
      const response = await this.axios.post('/extattributes', data);
      return {
        success: true,
        status: response.status,
        data: response.data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: error.response?.status,
        data: error.response?.data,
        timestamp: new Date().toISOString()
      };
    }
  }

  /**
   * Sends multiple events in batch
   * @param {Object} batchData - Object containing different event types
   * @returns {Promise<Object>} Batch response results
   */
  async sendBatch(batchData) {
    const results = {};
    const promises = [];

    if (batchData.customers) {
      promises.push(
        this.sendCustomerProfile(batchData.customers)
          .then(result => { results.customers = result; })
      );
    }

    if (batchData.accountEvents) {
      promises.push(
        this.sendAccountEvent(batchData.accountEvents)
          .then(result => { results.accountEvents = result; })
      );
    }

    if (batchData.depositEvents) {
      promises.push(
        this.sendDepositEvent(batchData.depositEvents)
          .then(result => { results.depositEvents = result; })
      );
    }

    if (batchData.withdrawEvents) {
      promises.push(
        this.sendWithdrawEvent(batchData.withdrawEvents)
          .then(result => { results.withdrawEvents = result; })
      );
    }

    if (batchData.gamingEvents) {
      promises.push(
        this.sendGamingActivityEvent(batchData.gamingEvents)
          .then(result => { results.gamingEvents = result; })
      );
    }

    if (batchData.extendedAttributes) {
      promises.push(
        this.sendExtendedAttributes(batchData.extendedAttributes)
          .then(result => { results.extendedAttributes = result; })
      );
    }

    await Promise.all(promises);

    return {
      success: true,
      results,
      timestamp: new Date().toISOString()
    };
  }

  /**
   * Updates client configuration
   * @param {Object} newConfig - New configuration options
   */
  updateConfig(newConfig) {
    this.config = { ...this.config, ...newConfig };
    this.validateConfig();
    this.setupAxiosInstance();
  }

  /**
   * Gets current configuration
   * @returns {Object} Current configuration (without sensitive data)
   */
  getConfig() {
    const { authToken, ...safeConfig } = this.config;
    return {
      ...safeConfig,
      authToken: authToken ? `${authToken.substring(0, 8)}...` : undefined
    };
  }
}

module.exports = DataPipelineClient;

