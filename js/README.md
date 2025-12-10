# Optikpi Data Pipeline API JavaScript SDK

This directory contains example applications demonstrating how to use the Optikpi Data Pipeline SDK for Javascript.

## Prerequisites

- Node.js 18 or higher
- npm 8 or higher
- Valid Optikpi API credentials

## Setup

- **Update the env file** with your Optikpi credentials:
```bash
   # Edit the '.env' file with your actual credentials
 ```
 ```
    API_BASE_URL=https://your-api-gateway-url/apigw/ingest
    AUTH_TOKEN=your_actual_auth_token
    ACCOUNT_ID=your_actual_account_id
    WORKSPACE_ID=your_actual_workspace_id
```
## Running Examples
- Before executing the file, please change your current path to:
cd ../js/examples

### Test Customer Profile Endpoint

```bash
npm run test:customer (or) node test-customer-endpoint.js
```

### Test Customer Extended Attribute Endpoint
```bash
npm run test:customerExt (or) node test-customerext-endpoint.js
```

### Test All Endpoints
```bash
npm run test:all (or) node test-all-endpoints.js
```

### Test Account Events
```bash
npm run test:account (or) node test-account-endpoint.js
```

### Test Deposit Events
```bash
npm run test:deposit (or) node test-deposit-endpoint.js
```

### Test Withdrawal Events
```bash
npm run test:withdraw (or) node test-withdraw-endpoint.js
```

### Test Gaming Activity Events
```bash
npm run test:gaming (or) node test-gaming-endpoint.js
```

### Test ReferFriend Events
```bash
npm run test:referFriend (or) node test-refer-friend-endpoint.js
```

### Test WalletBalance Events
```bash
npm run test:walletBalance (or) node test-wallet-balance-endpoint.js
```
### Test BatchData Operations
```bash
npm run test:batchData (or) node test-batch-operations.js
```

## Example Code

### Basic Usage

```js
require('dotenv').config();
const OptikpiDataPipelineSDK = require('../src/index');
const { CustomerProfile } = require('../src/models');


// Initialize SDK
const sdk = new OptikpiDataPipelineSDK({
  authToken: AUTH_TOKEN,
  accountId: ACCOUNT_ID,
  workspaceId: WORKSPACE_ID,
  baseURL: API_BASE_URL
});

// Customer data
const customer = new CustomerProfile({
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "user123456",
  "username": "john_doe",
  "full_name": "John Doe",
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "1990-01-15",
  "email": "john.doe@example.com",
  "phone_number": "+1234567890",
  "gender": "Male",
  "country": "United States",
  "city": "New York",
  "language": "en",
  "currency": "USD",
  "marketing_email_preference": "Opt-in",
  "notifications_preference": "Opt-in",
  "subscription": "Subscribed",
  "privacy_settings": "private",
  "deposit_limits": 1000.00,
  "loss_limits": 500.00,
  "wagering_limits": 2000.00,
  "session_time_limits": 120,
  "cooling_off_period": 7,
  "self_exclusion_period": 30,
  "reality_checks_notification": "daily",
  "account_status": "Active",
  "vip_status": "Regular",
  "loyalty_program_tiers": "Bronze",
  "bonus_abuser": "Not flagged",
  "financial_risk_level": 0.3,
  "acquisition_source": "Google Ads",
  "partner_id": "partner123",
  "affliate_id": "affiliate456",
  "referral_link_code": "REF789",
  "referral_limit_reached": "Not Reached",
  "creation_timestamp": "2024-01-15T10:30:00Z",
  "phone_verification": "Verified",
  "email_verification": "Verified",
  "bank_verification": "NotVerified",
  "iddoc_verification": "Verified"
});

const validation = customer.validate();
if (!validation.isValid) {
  console.error('❌ Validation errors:', validation.errors);
  process.exit(1);
}
console.log('✅ Customer event validated successfully!');

// Send customer profile
if (result.success) {
      console.log('\n✅ Success!');
      console.log('============================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Response Data:', JSON.stringify(result.data, null, 2));
    } else {
      console.log('\n❌ API Error!');
      console.log('============================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Error Data:', JSON.stringify(result.data, null, 2));
    }
```
### Batch Operations
```javascript
const batchData = {
  customers: [createSampleCustomer(accountId, workspaceId)],
  extendedAttributes: [
    createSampleExtendedAttributesMapFormat(accountId, workspaceId),
    createSampleExtendedAttributesStringFormat(accountId, workspaceId)
  ],
  accountEvents: [createSampleAccountEvent(accountId, workspaceId)],
  depositEvents: [createSampleDepositEvent(accountId, workspaceId)],
  withdrawEvents: [createSampleWithdrawEvent(accountId, workspaceId)],
  gamingEvents: [createSampleGamingActivityEvent(accountId, workspaceId)],
  referFriendEvents: [createSampleReferFriendEvent(accountId, workspaceId)],
  walletBalanceEvents: [createSampleWalletBalanceEvent(accountId, workspaceId)]
};
const batchResult = await sdk.sendBatch(batchData);
```


## Troubleshooting

```bash
### Common Issues

1. Missing credentials: Ensure all required environment variables are set
2. Network issues: Check your internet connection and firewall settings
3. Invalid data: Use the validation methods to check your data before sending
4. Authentication errors: Verify your auth token, account ID, and workspace ID
```