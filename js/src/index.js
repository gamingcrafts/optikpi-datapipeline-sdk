/**
 * Optikpi Data Pipeline API JavaScript SDK
 * 
 * A comprehensive JavaScript SDK for integrating with the Optikpi Data Pipeline API.
 * Provides secure authentication, data validation, and easy-to-use methods for
 * sending customer profiles and event data.
 * 
 * @version 1.0.0
 * @author Optikpi
 * @license MIT
 */

const DataPipelineClient = require('./core/DataPipelineClient');
const {
  CustomerProfile,
  AccountEvent,
  DepositEvent,
  WithdrawEvent,
  GamingActivityEvent
} = require('./models');
const {
  deriveKey,
  generateHmacSignature,
  validateHmacSignature
} = require('./utils/crypto');

// Main SDK class
class OptikpiDataPipelineSDK {
  constructor(config = {}) {
    this.client = new DataPipelineClient(config);
  }

  // Delegate all client methods
  async healthCheck() {
    return this.client.healthCheck();
  }

  async sendCustomerProfile(data) {
    return this.client.sendCustomerProfile(data);
  }

  async sendAccountEvent(data) {
    return this.client.sendAccountEvent(data);
  }

  async sendDepositEvent(data) {
    return this.client.sendDepositEvent(data);
  }

  async sendWithdrawEvent(data) {
    return this.client.sendWithdrawEvent(data);
  }

  async sendGamingActivityEvent(data) {
    return this.client.sendGamingActivityEvent(data);
  }

  async sendBatch(batchData) {
    return this.client.sendBatch(batchData);
  }

  updateConfig(newConfig) {
    this.client.updateConfig(newConfig);
  }

  getConfig() {
    return this.client.getConfig();
  }
}

// Export the main SDK class
module.exports = OptikpiDataPipelineSDK;

// Export individual components for advanced usage
module.exports.DataPipelineClient = DataPipelineClient;
module.exports.CustomerProfile = CustomerProfile;
module.exports.AccountEvent = AccountEvent;
module.exports.DepositEvent = DepositEvent;
module.exports.WithdrawEvent = WithdrawEvent;
module.exports.GamingActivityEvent = GamingActivityEvent;
module.exports.crypto = {
  deriveKey,
  generateHmacSignature,
  validateHmacSignature
};

// Export default instance creator for convenience
module.exports.createClient = (config) => new OptikpiDataPipelineSDK(config);

