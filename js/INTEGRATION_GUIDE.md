# Data Pipeline API Integration Guide

## Overview

This guide provides comprehensive instructions for third-party developers to integrate with the **Optikpi Data Pipeline API**. The API enables secure, real-time ingestion of customer profiles and event data into our enterprise data pipeline infrastructure.

### 🚀 Key Features
- **Real-time Data Ingestion**: Push customer and event data instantly
- **Batch Processing**: Support for bulk data uploads (up to 500 records)
- **Enterprise Security**: Dual-layer authentication with HMAC signatures
- **High Availability**: 99.9% uptime SLA
- **Scalable Architecture**: Built on AWS serverless infrastructure

### 📊 Data Types Supported
- **Customer Profiles**: Complete user information and preferences
- **Account Events**: Registration, verification, and account changes
- **Financial Events**: Deposits, withdrawals, and transactions
- **Gaming Activity**: Game plays, wins, losses, and session data

## Table of Contents

- [🔐 Authentication](#authentication)
- [🌐 API Endpoints](#api-endpoints)
- [📋 Data Models](#data-models)
- [🚀 Quick Start](#quick-start)
- [📚 SDK Integration Guide](#sdk-integration-guide)
- [⚠️ Error Handling](#error-handling)
- [✅ Best Practices](#best-practices)
- [⚡ Rate Limits](#rate-limits)
- [📞 Support](#support)

---

## 🔐 Authentication

The Data Pipeline API uses a **dual-layer security model** to ensure data integrity and prevent unauthorized access. The official SDK handles all authentication automatically - you don't need to implement any crypto code.

### How Authentication Works
1. **Token Authentication**: Your authentication token is sent in the `x-optikpi-token` header
2. **HMAC Signature**: Each request is signed using HMAC with HKDF key derivation
3. **Automatic Handling**: The SDK generates all required headers and signatures

### What You Need
- **Authentication Token**: Contact your account manager to obtain this
- **Account ID & Workspace ID**: Provided by your account manager
- **API Gateway URL**: The endpoint for your environment

> **💡 SDK Advantage**: All authentication complexity is handled automatically by the SDK. You just provide your credentials during initialization.

## 🌐 API Endpoints

### Base URL
```
https://your-api-gateway-url/apigw/ingest
```

### Available Endpoints

| Endpoint | Method | Description | Rate Limit |
|----------|--------|-------------|------------|
| `/customers` | POST | Push customer profile data | 1000 req/sec |
| `/events/account` | POST | Push account events | 1000 req/sec |
| `/events/deposit` | POST | Push deposit events | 1000 req/sec |
| `/events/withdraw` | POST | Push withdrawal events | 1000 req/sec |
| `/events/gaming-activity` | POST | Push gaming activity events | 1000 req/sec |
| `/datapipeline/health` | GET | Health check endpoint | No limit |

### Required Headers
| Header | Type | Description | Example |
|--------|------|-------------|---------|
| `x-optikpi-account-id` | string | Your account identifier | `68911b7ad58ad825ec00c5ef` |
| `x-optikpi-workspace-id` | string | Your workspace identifier | `68911b7ad825ec00c5ef` |

### Response Format
All successful responses follow this structure:
```json
{
  "message": "Success description",
  "recordIds": ["record-id-1", "record-id-2"],
  "count": 2
}
```

## 📋 Data Models

### Customer Profile
<details>
<summary><strong>📊 Customer Profile Schema</strong></summary>

**Endpoint**: `POST /customers/{account_id}/{workspace_id}`

**Description**: Complete customer profile information including personal details, preferences, and account settings.

**Required Fields**: 25 fields
**Optional Fields**: 15 fields
```json
{
  "account_id": "string (required)",
  "workspace_id": "string (required)",
  "user_id": "string (required)",
  "username": "string (required)",
  "full_name": "string (required)",
  "first_name": "string (optional)",
  "last_name": "string (optional)",
  "date_of_birth": "string (optional, YYYY-MM-DD)",
  "email": "string (required, email format)",
  "phone_number": "string (required)",
  "gender": "string (optional, Male/Female)",
  "country": "string (optional)",
  "city": "string (optional)",
  "language": "string (optional)",
  "currency": "string (required)",
  "marketing_email_preference": "string (optional, Opt-in/Opt-out)",
  "notifications_preference": "string (optional, Opt-in/Opt-out)",
  "subscription": "string (required, Subscribed/Unsubscribed)",
  "privacy_settings": "string (optional, public/private)",
  "deposit_limits": "number (optional)",
  "loss_limits": "number (optional)",
  "wagering_limits": "number (optional)",
  "session_time_limits": "number (optional)",
  "cooling_off_period": "integer (optional)",
  "self_exclusion_period": "integer (optional)",
  "reality_checks_notification": "string (optional, daily/weekly/monthly)",
  "account_status": "string (optional, Active/Locked/InActive/Frozen)",
  "vip_status": "string (optional, Regular/VIP)",
  "loyalty_program_tiers": "string (optional, Gold/Silver/Bronze)",
  "bonus_abuser": "string (optional, Flagged as a bonus abuser/Not flagged)",
  "financial_risk_level": "number (optional, 0-1)",
  "acquisition_source": "string (optional, Google Ads/Facebook Ads/Twitter Ads)",
  "partner_id": "string (optional)",
  "affliate_id": "string (optional)",
  "referral_link_code": "string (optional)",
  "referral_limit_reached": "string (optional, Reached/Not Reached)",
  "creation_timestamp": "string (optional, ISO 8601)",
  "phone_verification": "string (optional, Verified/NotVerified)",
  "email_verification": "string (optional, Verified/NotVerified)",
  "bank_verification": "string (optional, Verified/NotVerified)",
  "iddoc_verification": "string (optional, Verified/NotVerified)"
}
```

### Account Event
```json
{
  "account_id": "string (required)",
  "workspace_id": "string (required)",
  "user_id": "string (required)",
  "event_category": "string (required)",
  "event_name": "string (required)",
  "event_id": "string (required)",
  "event_time": "string (required, ISO 8601)",
  "device": "string (optional, desktop/mobile/tablet)",
  "ip_address": "string (optional)",
  "user_agent": "string (optional)",
  "status": "string (optional, verified/failed)",
  "metadata": "object (optional)"
}
```

### Deposit Event
```json
{
  "account_id": "string (required)",
  "workspace_id": "string (required)",
  "user_id": "string (required)",
  "event_category": "string (required)",
  "event_name": "string (required)",
  "event_id": "string (required)",
  "event_time": "string (required, ISO 8601)",
  "amount": "number (required)",
  "currency": "string (optional)",
  "payment_method": "string (optional, bank/credit_card)",
  "transaction_id": "string (optional)",
  "status": "string (optional)",
  "metadata": "object (optional)"
}
```

### Withdraw Event
```json
{
  "account_id": "string (required)",
  "workspace_id": "string (required)",
  "user_id": "string (required)",
  "event_category": "string (required)",
  "event_name": "string (required)",
  "event_id": "string (required)",
  "event_time": "string (required, ISO 8601)",
  "amount": "number (optional)",
  "currency": "string (optional)",
  "payment_method": "string (optional, bank)",
  "transaction_id": "string (optional)",
  "status": "string (optional)",
  "metadata": "object (optional)"
}
```

### Gaming Activity Event
```json
{
  "account_id": "string (required)",
  "workspace_id": "string (required)",
  "user_id": "string (required)",
  "event_category": "string (required)",
  "event_name": "string (required)",
  "event_id": "string (required)",
  "event_time": "string (required, ISO 8601)",
  "wager_amount": "number (optional)",
  "win_amount": "number (optional)",
  "game_title": "string (optional)",
  "game_category": "string (optional)",
  "session_duration": "number (optional)",
  "metadata": "object (optional)"
}
```

## 🚀 Quick Start

Get started with the Optikpi Data Pipeline API in just 5 minutes using our official SDK.

### Prerequisites
Before you begin, you'll need:
1. Your authentication token
2. Account ID and Workspace ID
3. API Gateway URL
4. Node.js environment

---

## 📚 SDK Integration Guide

The official Optikpi Data Pipeline SDK makes integration simple and secure. All authentication, validation, and error handling is handled automatically.

### Installation
```bash
npm install @optikpi/datapipeline-sdk
```

### 1. Initialize the SDK
```javascript
const { OptikpiDataPipelineSDK } = require('@optikpi/datapipeline-sdk');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your-auth-token',
  accountId: 'your-account-id',
  workspaceId: 'your-workspace-id',
  baseURL: 'https://your-api-gateway-url/apigw/ingest'
});
```

### 2. Send Customer Profile
```javascript
const customerData = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  username: 'john_doe',
  full_name: 'John Doe',
  email: 'john.doe@example.com',
  phone_number: '+1234567890',
  currency: 'USD',
  subscription: 'Subscribed',
  deposit_limits: 1000.00,
  loss_limits: 500.00,
  wagering_limits: 2000.00,
  session_time_limits: 120,
  cooling_off_period: 7,
  self_exclusion_period: 30,
  account_status: 'Active',
  vip_status: 'Regular',
  loyalty_program_tiers: 'Bronze',
  financial_risk_level: 0.3,
  referral_link_code: 'REF789',
  creation_timestamp: new Date().toISOString(),
  phone_verification: 'Verified',
  email_verification: 'Verified',
  bank_verification: 'NotVerified',
  iddoc_verification: 'Verified'
};

try {
  const result = await sdk.sendCustomerProfile(customerData);
  
  if (result.success) {
    console.log('✅ Customer profile sent successfully!');
    console.log('Response:', result.data);
  } else {
    console.error('❌ Failed to send customer profile:', result.error);
  }
} catch (error) {
  console.error('❌ Error:', error.message);
}
```

### 3. Send Events
```javascript
// Account Event
const accountEvent = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_category: 'Account',
  event_name: 'Player Registration',
  event_id: 'evt_123456789',
  event_time: new Date().toISOString(),
  device: 'desktop',
  ip_address: '192.168.1.100',
  status: 'verified'
};

const accountResult = await sdk.sendAccountEvent(accountEvent);

// Deposit Event
const depositEvent = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_category: 'Deposit',
  event_name: 'Successful Deposit',
  event_id: 'evt_dep_987654321',
  event_time: new Date().toISOString(),
  amount: 500.00,
  currency: 'USD',
  payment_method: 'bank',
  transaction_id: 'txn_123456789',
  status: 'completed'
};

const depositResult = await sdk.sendDepositEvent(depositEvent);

// Gaming Activity Event
const gamingEvent = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_category: 'Gaming Activity',
  event_name: 'Play Casino Game',
  event_id: 'evt_game_789123456',
  event_time: new Date().toISOString(),
  wager_amount: 10.00,
  win_amount: 25.00,
  game_title: 'Mega Fortune Slots',
  game_category: 'Slots',
  session_duration: 45
};

const gamingResult = await sdk.sendGamingActivityEvent(gamingEvent);
```

### 4. Health Check
```javascript
const health = await sdk.healthCheck();
console.log('API Status:', health.success ? 'Healthy' : 'Unhealthy');
```

### 5. Batch Operations
```javascript
const batchData = {
  customers: [customer1, customer2],
  depositEvents: [deposit1, deposit2],
  gamingEvents: [gaming1, gaming2]
};

const batchResult = await sdk.sendBatch(batchData);
```

### SDK Benefits
- **🔐 Automatic Authentication**: Handles HMAC signature generation
- **✅ Built-in Validation**: Validates data before sending
- **🔄 Retry Logic**: Automatic retry with exponential backoff
- **🛡️ Error Handling**: Rich error objects with detailed information
- **📝 Type Safety**: Full TypeScript support
- **⚡ High Performance**: Optimized for high-throughput data ingestion

For complete SDK documentation, see the [README.md](README.md) file.

---

## ⚠️ Error Handling

### Common HTTP Status Codes

| Status Code | Description | Action |
|-------------|-------------|--------|
| 200 | Success | Request processed successfully |
| 400 | Bad Request | Check request body format and required fields |
| 401 | Unauthorized | Verify authentication token and HMAC signature |
| 403 | Forbidden | Token may be expired or invalid |
| 404 | Not Found | Check API endpoint URL |
| 429 | Too Many Requests | Implement exponential backoff |
| 500 | Internal Server Error | Retry request after delay |

### Error Response Format

```json
{
  "error": "Bad Request",
  "message": "Validation failed: account_id is required",
  "details": {
    "field": "account_id",
    "issue": "missing required field"
  }
}
```

### Retry Strategy Implementation

```javascript
class RetryHandler {
  constructor(maxRetries = 3, baseDelay = 1000) {
    this.maxRetries = maxRetries;
    this.baseDelay = baseDelay;
  }

  async executeWithRetry(operation) {
    let lastError;
    
    for (let attempt = 1; attempt <= this.maxRetries; attempt++) {
      try {
        const result = await operation();
        
        if (result.success) {
          return result;
        }
        
        // Don't retry on client errors (4xx)
        if (result.status >= 400 && result.status < 500) {
          return result;
        }
        
        lastError = result;
        
        if (attempt < this.maxRetries) {
          const delay = this.baseDelay * Math.pow(2, attempt - 1);
          console.log(`Attempt ${attempt} failed, retrying in ${delay}ms...`);
          await this.delay(delay);
        }
      } catch (error) {
        lastError = {
          success: false,
          error: error.message,
          status: 0
        };
        
        if (attempt < this.maxRetries) {
          const delay = this.baseDelay * Math.pow(2, attempt - 1);
          console.log(`Attempt ${attempt} failed with error, retrying in ${delay}ms...`);
          await this.delay(delay);
        }
      }
    }
    
    return lastError;
  }

  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}
```

## ✅ Best Practices

### 1. Data Validation
- Always validate data before sending to API
- Ensure required fields are present
- Use proper data types (numbers, strings, dates)
- Implement client-side validation for better user experience

### 2. Batch Processing
- Use batch endpoints for multiple records
- Keep batch sizes reasonable (max 500 records)
- Implement proper error handling for partial failures
- Consider rate limits when batching

### 3. Error Handling
- Implement exponential backoff for retries
- Log all API responses for debugging
- Handle rate limiting gracefully
- Monitor error rates and set up alerts

### 4. Security
- Keep authentication tokens secure
- Rotate tokens regularly
- Use HTTPS for all API calls
- Never log sensitive data

### 5. Monitoring
- Monitor API response times
- Track success/failure rates
- Set up alerts for high error rates
- Implement health checks

### 6. Performance
- Use connection pooling for HTTP clients
- Implement request queuing for high volume
- Consider async processing for non-critical data
- Cache frequently used data

## ⚡ Rate Limits

- **Requests per second**: 1000
- **Batch size limit**: 500 records per request
- **Rate limit window**: 1 minute
- **Rate limit response**: 429 Too Many Requests

### Rate Limit Handling
```javascript
class RateLimitHandler {
  constructor() {
    this.requests = [];
    this.maxRequests = 1000;
    this.windowMs = 60000; // 1 minute
  }

  async waitIfNeeded() {
    const now = Date.now();
    
    // Remove requests outside the window
    this.requests = this.requests.filter(time => now - time < this.windowMs);
    
    if (this.requests.length >= this.maxRequests) {
      const oldestRequest = Math.min(...this.requests);
      const waitTime = this.windowMs - (now - oldestRequest);
      
      if (waitTime > 0) {
        console.log(`Rate limit reached, waiting ${waitTime}ms...`);
        await new Promise(resolve => setTimeout(resolve, waitTime));
      }
    }
    
    this.requests.push(now);
  }
}
```

## 📞 Support

For technical support and questions:
- **Email**: api-support@optikpi.com
- **Documentation**: https://docs.optikpi.com/api
- **Status page**: https://status.optikpi.com
- **GitHub Issues**: https://github.com/optikpi/datapipeline-sdk/issues

## 🚀 Next Steps

1. **Choose Integration Method**: Decide between manual integration or SDK
2. **Get Credentials**: Contact your account manager for API credentials
3. **Start Development**: Use the examples in this guide as templates
4. **Test Integration**: Use the health check endpoint to verify connectivity
5. **Go Live**: Deploy your integration to production

## 📚 Additional Resources

- [API Reference Documentation](https://docs.optikpi.com/api)
- [SDK Documentation](README.md)
- [Java SDK Guide](../java/README.md)
- [Integration Examples](../examples/)
- [Changelog](CHANGELOG.md)

---

*This integration guide provides everything you need to successfully integrate with the Optikpi Data Pipeline API. Choose the approach that best fits your technology stack and requirements.*