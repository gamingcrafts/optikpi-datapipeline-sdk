# Optikpi Data Pipeline API JavaScript SDK

[![npm version](https://badge.fury.io/js/%40optikpi%2Fdatapipeline-sdk.svg)](https://badge.fury.io/js/%40optikpi%2Fdatapipeline-sdk)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Node.js Version](https://img.shields.io/badge/node-%3E%3D14.0.0-brightgreen.svg)](https://nodejs.org/)

A comprehensive JavaScript SDK for integrating with the Optikpi Data Pipeline API. This SDK provides secure authentication, data validation, and easy-to-use methods for sending customer profiles and event data.

## 🚀 Features

- **🔐 Secure Authentication**: HMAC-based authentication with HKDF key derivation
- **📊 Data Models**: Comprehensive data models with built-in validation
- **🔄 Automatic Retries**: Configurable retry logic for failed requests
- **📝 Type Safety**: Full TypeScript support with comprehensive JSDoc
- **🌐 Multi-Platform**: Works in Node.js and modern browsers
- **⚡ High Performance**: Optimized for high-throughput data ingestion
- **🛡️ Error Handling**: Robust error handling with detailed error messages

## 📦 Installation

### NPM
```bash
npm install 
```

## 🎯 Quick Start

### 1. Initialize the SDK

```javascript
const OptikpiDataPipelineSDK = require('../src/index');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your-auth-token',
  accountId: 'your-account-id',
  workspaceId: 'your-workspace-id',
  baseURL: 'https://your-api-gateway-url/apigw/ingest'
});
```

### 2. Send Customer Profile

```javascript
const customerData = new CustomerProfile({
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  username: 'john_doe',
  email: 'john.doe@example.com',
  full_name: 'John Doe',
  country: 'United States',
  currency: 'USD'
});
const validation = customerData.validate();
if (!validation.isValid) {
  console.error('❌ Validation errors:', validation.errors);
  process.exit(1);
}
console.log('✅ Customer event validated successfully!');

try {
  const result = await sdk.sendCustomerProfile(customerData);
  if (result.success) {
    console.log('Customer profile sent successfully:', result.data);
  } else {
    console.error('Failed to send customer profile:', result.error);
  }
} catch (error) {
  console.error('Error sending customer profile:', error);
}
```

### 3. Send Event Data

```javascript
const deposit=new DepositEvent({
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_name: 'Successful Deposit',
  event_id: 'evt_dep_123',
  event_time: new Date().toISOString(),
  amount: 100.00,
  payment_method: 'bank',
  transaction_id: 'txn_123'
});
// Validate the account event
const validation = deposit.validate();
if (!validation.isValid) {
  console.error('❌ Validation errors:', validation.errors);
  process.exit(1);
}
console.log('✅ Deposit event validated successfully!');

const result = await sdk.sendDepositEvent(depositEvent);
```

### 4. Batch Operations

```javascript
const batchData = {
  customers: [customer1, customer2],
  depositEvents: [deposit1, deposit2],
  gamingEvents: [gaming1, gaming2]
};

const batchResult = await sdk.sendBatch(batchData);
```

## 📚 API Reference

### Constructor Options

| Option | Type | Required | Default | Description |
|--------|------|----------|---------|-------------|
| `authToken` | string | ✅ | - | Your authentication token |
| `accountId` | string | ✅ | - | Your account ID |
| `workspaceId` | string | ✅ | - | Your workspace ID |
| `baseURL` | string | ❌ | `https://your-api-gateway-url/apigw/ingest` | API base URL |
| `timeout` | number | ❌ | `30000` | Request timeout in milliseconds |
| `retries` | number | ❌ | `3` | Number of retry attempts |
| `retryDelay` | number | ❌ | `1000` | Delay between retries in milliseconds |

### Methods

#### `healthCheck()`
Performs a health check on the API.

```javascript
const health = await sdk.healthCheck();
console.log('API Status:', health.success ? 'Healthy' : 'Unhealthy');
```

#### `sendCustomerProfile(data)`
Sends customer profile data to the API.

```javascript
const result = await sdk.sendCustomerProfile(customerData);
```

#### `sendAccountEvent(data)`
Sends account-related events.

```javascript
const result = await sdk.sendAccountEvent(accountEvent);
```

#### `sendDepositEvent(data)`
Sends deposit-related events.

```javascript
const result = await sdk.sendDepositEvent(depositEvent);
```

#### `sendWithdrawEvent(data)`
Sends withdrawal-related events.

```javascript
const result = await sdk.sendWithdrawEvent(withdrawEvent);
```

#### `sendGamingActivityEvent(data)`
Sends gaming activity events.

```javascript
const result = await sdk.sendGamingActivityEvent(gamingEvent);
```
#### `sendWalletBalanceEvent(data)`
Sends gaming activity events.

```javascript
const result = await sdk.sendWalletBalanceEvent(walletBalanceEvent);
```

#### `sendReferFriendEvent(data)`
Sends gaming activity events.

```javascript
const result = await sdk.sendReferFriendEvent(referFrientEvent);
```

#### `sendBatch(batchData)`
Sends multiple types of data in a single batch operation.

```javascript
const result = await sdk.sendBatch({
  customers: [customer1, customer2],
  depositEvents: [deposit1, deposit2],
  gamingEvents: [gaming1, gaming2]
});
```

## 🏗️ Data Models

The SDK includes comprehensive data models with built-in validation:

### CustomerProfile
```javascript
const { CustomerProfile } =  require('../src/models');

const customer = new CustomerProfile({
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  email: 'user@example.com'
});

const validation = customer.validate();
if (!validation.isValid) {
  console.error('Validation errors:', validation.errors);
}
```

### Event Models
```javascript
const { AccountEvent, DepositEvent, GamingActivityEvent } =  require('../src/models');

const accountEvent = new AccountEvent({
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_name: 'Player Registration',
  event_id: 'evt_123',
  event_time: new Date().toISOString()
});
```

## 🔐 Authentication

The SDK automatically handles authentication using HMAC signatures with HKDF key derivation. All requests are automatically signed with the appropriate headers:

- `x-optikpi-token`: Your authentication token
- `x-optikpi-account-id`: Your account ID
- `x-optikpi-workspace-id`: Your workspace ID
- `x-hmac-signature`: HMAC signature of the request body
- `x-hmac-algorithm`: HMAC algorithm (default: sha256)

## 🛠️ Development

### Building the SDK

```bash
# Install dependencies
npm install

# Build for production
npm run build

# Build for development with watch mode
npm run dev
```

### Running Tests

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test -- --coverage
```

### Code Quality

```bash
# Lint code
npm run lint

# Format code
npm run format
```

## 📋 Examples

### Complete Integration Example

```javascript
const {OptikpiDataPipelineSDK} = require('../src/index');
const {CustomerProfile, DepositEvent } =  require('../src/models');

class CustomerDataService {
  constructor() {
    this.sdk = new OptikpiDataPipelineSDK({
      authToken: process.env.OPTIKPI_AUTH_TOKEN,
      accountId: process.env.OPTIKPI_ACCOUNT_ID,
      workspaceId: process.env.OPTIKPI_WORKSPACE_ID,
      baseURL: process.env.OPTIKPI_API_URL
    });
  }

  async registerCustomer(customerData) {
    try {
      // Create and validate customer profile
      const customer = new CustomerProfile(customerData);
      const validation = customer.validate();
      
      if (!validation.isValid) {
        throw new Error(`Validation failed: ${validation.errors.join(', ')}`);
      }

      // Send customer profile
      const result = await this.sdk.sendCustomerProfile(customer.toJSON());
      
      if (result.success) {
        console.log('Customer registered successfully');
        return result.data;
      } else {
        throw new Error(`Failed to register customer: ${result.error}`);
      }
    } catch (error) {
      console.error('Error registering customer:', error);
      throw error;
    }
  }

  async recordDeposit(userId, amount, paymentMethod, transactionId) {
    try {
      const depositEvent = new DepositEvent({
        account_id: this.sdk.getConfig().accountId,
        workspace_id: this.sdk.getConfig().workspaceId,
        user_id: userId,
        event_name: 'Successful Deposit',
        event_id: `evt_dep_${Date.now()}`,
        event_time: new Date().toISOString(),
        amount: amount,
        payment_method: paymentMethod,
        transaction_id: transactionId
      });
      // Validate the account event
      const validation = deposit.validate();
      if (!validation.isValid) {
        console.error('❌ Validation errors:', validation.errors);
        process.exit(1);
      }
      console.log('✅ Deposit event validated successfully!');

      const result = await this.sdk.sendDepositEvent(depositEvent.toJSON());
      
      if (result.success) {
        console.log('Deposit recorded successfully');
        return result.data;
      } else {
        throw new Error(`Failed to record deposit: ${result.error}`);
      }
    } catch (error) {
      console.error('Error recording deposit:', error);
      throw error;
    }
  }
}

// Usage
const service = new CustomerDataService();

// Register a new customer
await service.registerCustomer({
  user_id: 'user123',
  email: 'user@example.com',
  full_name: 'John Doe',
  country: 'United States'
});

// Record a deposit
await service.recordDeposit('user123', 100.00, 'bank', 'txn_123');
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [https://docs.optikpi.com](https://docs.optikpi.com)
- **Issues**: [GitHub Issues](https://github.com/optikpi/datapipeline-sdk-js/issues)
- **Email**: support@optikpi.com

## 🔗 Links

- [Optikpi Website](https://optikpi.com)
- [API Documentation](https://docs.optikpi.com/api)
- [Integration Guide](https://docs.optikpi.com/integration)

