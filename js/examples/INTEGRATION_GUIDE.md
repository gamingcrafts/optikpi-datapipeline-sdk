# Data Pipeline API Integration Guide

## Overview

This guide provides comprehensive instructions for third-party developers to integrate with the **Data Pipeline API**. The API enables secure, real-time ingestion of customer profiles and event data into our enterprise data pipeline infrastructure.

> **💡 SDK Integration Available**: For easier integration, we now provide the **Optikpi Data Pipeline SDK** that handles all authentication, HMAC signature generation, and API communication automatically. See the [Quick Start](#quick-start) section for SDK usage examples.

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
- [💻 Integration Examples](#integration-examples)
- [⚠️ Error Handling](#error-handling)
- [✅ Best Practices](#best-practices)
- [⚡ Rate Limits](#rate-limits)
- [📞 Support](#support)

---

## Quick Start

<details>
<summary><strong>🚀 Get Started in 5 Minutes</strong></summary>

### 1. Get Your Credentials
```bash
# Contact your account manager for:
# - API Gateway URL
# - Authentication Token
# - Account ID and Workspace ID
```

### 2. Install the SDK
```bash
npm install 
```

### 3. Test Your Connection
```javascript
const OptikpiDataPipelineSDK = require('../src/index');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your-auth-token',
  accountId: 'your-account-id',
  workspaceId: 'your-workspace-id',
  baseURL: 'https://your-api-gateway-url/apigw/ingest'
});

// Health check
const health = await sdk.healthCheck();
console.log('API Status:', health.success ? 'Healthy' : 'Unhealthy');
```

### 4. Send Your First Data
```javascript
// Simple customer profile
const customerData = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  username: 'john_doe',
  email: 'john@example.com',
  // ... other required fields
};

const result = await sdk.sendCustomerProfile(customerData);
if (result.success) {
  console.log('Customer profile sent successfully!');
} else {
  console.error('Failed to send customer profile:', result.error);
}
```

</details>

## 🔐 Authentication

The Data Pipeline API uses a **dual-layer security model** to ensure data integrity and prevent unauthorized access.

> **🚀 SDK Simplification**: The Optikpi Data Pipeline SDK automatically handles all authentication complexity, including HMAC signature generation and HKDF key derivation. You only need to provide your credentials during SDK initialization.

### 1. Token Authentication
All API requests require a valid authentication token in the header:

```http
x-optikpi-token: your-auth-token-here
```

> **Note**: Contact your account manager to obtain your authentication token.

### 2. HMAC Request Body Authentication
For enhanced security, all requests require HMAC signature validation using **HKDF** (HMAC-based Key Derivation Function).

#### Required Headers
| Header | Description | Example |
|--------|-------------|---------|
| `x-hmac-signature` | HMAC signature of the request body | `a1b2c3d4e5f6...` |
| `x-hmac-algorithm` | HMAC algorithm (default: sha256) | `sha256` |

#### HKDF Parameters
| Parameter | Description | Example |
|-----------|-------------|---------|
| **IKM** (Input Key Material) | Your auth token | `your-auth-token-here` |
| **Salt** | `account_id + workspace_id` (from headers) | `68911b7ad58ad825ec00c5ef68911b7ad825ec00c5ef` |
| **Info** | Context string | `"hmac-signing"` |

#### Security Benefits
- 🔒 **Request Integrity**: Ensures data hasn't been tampered with
- 🔒 **Replay Protection**: Each request has a unique signature
- 🔒 **Context Binding**: Keys are bound to specific account/workspace
- 🔒 **Cryptographic Strength**: Uses standardized HKDF algorithm

## 🌐 API Endpoints

### Base URL
```
https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest
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
  "count": 2,
  "streamName": "firehose-stream-name",
  "destination": "s3://bucket/path"
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
  "date_of_birth": "string (required, YYYY-MM-DD)",
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
  "deposit_limits": "number (required)",
  "loss_limits": "number (required)",
  "wagering_limits": "number (required)",
  "session_time_limits": "number (required)",
  "cooling_off_period": "integer (required)",
  "self_exclusion_period": "integer (required)",
  "reality_checks_notification": "string (optional, daily/weekly/monthly)",
  "account_status": "string (required, Active/Locked/InActive/Frozen)",
  "vip_status": "string (required, Regular/VIP)",
  "loyalty_program_tiers": "string (required, Gold/Silver/Bronze)",
  "bonus_abuser": "string (optional, Flagged as a bonus abuser/Not flagged)",
  "financial_risk_level": "number (required, 0-1)",
  "acquisition_source": "string (optional, Google Ads/Facebook Ads/Twitter Ads)",
  "partner_id": "string (optional)",
  "affliate_id": "string (optional)",
  "referral_link_code": "string (required)",
  "referral_limit_reached": "string (optional, Reached/Not Reached)",
  "creation_timestamp": "string (required, ISO 8601)",
  "phone_verification": "string (required, Verified/NotVerified)",
  "email_verification": "string (required, Verified/NotVerified)",
  "bank_verification": "string (required, Verified/NotVerified)",
  "iddoc_verification": "string (required, Verified/NotVerified)"
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

## Integration Examples

### Prerequisites
Before making API calls, you need:
1. Your authentication token
2. Account ID and Workspace ID
3. The Optikpi Data Pipeline SDK installed

### SDK Setup
```javascript
const OptikpiDataPipelineSDK = require('../src/index');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your-auth-token',
  accountId: 'your-account-id',
  workspaceId: 'your-workspace-id',
  baseURL: 'https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest'
});
```

### SDK vs Manual Integration
| Feature | Manual (curl) | SDK |
|---------|---------------|-----|
| **Setup** | Complex HMAC/HKDF implementation | Simple initialization |
| **Authentication** | Manual header management | Automatic |
| **Error Handling** | Basic HTTP status codes | Rich error objects |
| **Retry Logic** | Manual implementation | Built-in retry mechanism |
| **Data Validation** | Manual validation | Built-in validation |
| **Maintenance** | High (crypto code) | Low (SDK updates) |

### Example 1: Push Single Customer Profile (Using SDK)

> **📝 Note**: This example uses the Optikpi Data Pipeline SDK for simplified integration. The SDK automatically handles all authentication and HMAC signature generation.

```javascript
// Customer data
const customerData = {
  account_id: "68911b7ad58ad825ec00c5ef",
  workspace_id: "68911b7ad58ad825ec00c5ef",
  user_id: "user123456",
  username: "john_doe",
  full_name: "John Doe",
  first_name: "John",
  last_name: "Doe",
  date_of_birth: "1990-01-15",
  email: "john.doe@example.com",
  phone_number: "+1234567890",
  gender: "Male",
  country: "United States",
  city: "New York",
  language: "en",
  currency: "USD",
  marketing_email_preference: "Opt-in",
  notifications_preference: "Opt-in",
  subscription: "Subscribed",
  privacy_settings: "private",
  deposit_limits: 1000.00,
  loss_limits: 500.00,
  wagering_limits: 2000.00,
  session_time_limits: 120,
  cooling_off_period: 7,
  self_exclusion_period: 30,
  reality_checks_notification: "daily",
  account_status: "Active",
  vip_status: "Regular",
  loyalty_program_tiers: "Bronze",
  bonus_abuser: "Not flagged",
  financial_risk_level: 0.3,
  acquisition_source: "Google Ads",
  partner_id: "partner123",
  affliate_id: "affiliate456",
  referral_link_code: "REF789",
  referral_limit_reached: "Not Reached",
  creation_timestamp: "2024-01-15T10:30:00Z",
  phone_verification: "Verified",
  email_verification: "Verified",
  bank_verification: "NotVerified",
  iddoc_verification: "Verified"
};

// Send customer profile using SDK
try {
  const result = await sdk.sendCustomerProfile(customerData);
  
  if (result.success) {
    console.log('✅ Customer profile sent successfully!');
    console.log('Response:', result.data);
    console.log('HTTP Status:', result.status);
    console.log('Response Time:', result.responseTime, 'ms');
  } else {
    console.error('❌ Failed to send customer profile:', result.error);
    console.log('HTTP Status:', result.status);
  }
} catch (error) {
  console.error('❌ SDK Error:', error.message);
}
```

**Expected Response:**
```json
{
  "message": "1 customer profile(s) sent to Firehose successfully",
  "recordIds": ["record-id-123"],
  "count": 1,
  "streamName": "datapipeline-customers-dev-stream",
  "destination": "s3://datapipeline/customers/68911b7ad58ad825ec00c5ef/68911b7ad58ad825ec00c5ef"
}
```

### Example 2: Push Multiple Customer Profiles (Batch - Using SDK)

> **📝 Note**: This example uses the Optikpi Data Pipeline SDK for simplified integration. The SDK automatically handles all authentication and HMAC signature generation.

```javascript
#!/bin/bash

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"
ACCOUNT_ID="68911b7ad58ad825ec00c5ef"
WORKSPACE_ID="68911b7ad825ec00c5ef"

# Multiple customers data
CUSTOMERS_DATA='[
  {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad825ec00c5ef",
    "user_id": "user123456",
    "username": "john_doe",
    "full_name": "John Doe",
    "date_of_birth": "1990-01-15",
    "email": "john.doe@example.com",
    "phone_number": "+1234567890",
    "currency": "USD",
    "subscription": "Subscribed",
    "deposit_limits": 1000.00,
    "loss_limits": 500.00,
    "wagering_limits": 2000.00,
    "session_time_limits": 120,
    "cooling_off_period": 7,
    "self_exclusion_period": 30,
    "account_status": "Active",
    "vip_status": "Regular",
    "loyalty_program_tiers": "Bronze",
    "financial_risk_level": 0.3,
    "referral_link_code": "REF789",
    "creation_timestamp": "2024-01-15T10:30:00Z",
    "phone_verification": "Verified",
    "email_verification": "Verified",
    "bank_verification": "NotVerified",
    "iddoc_verification": "Verified"
  },
  {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad825ec00c5ef",
    "user_id": "user789012",
    "username": "jane_smith",
    "full_name": "Jane Smith",
    "date_of_birth": "1985-05-20",
    "email": "jane.smith@example.com",
    "phone_number": "+1987654321",
    "currency": "USD",
    "subscription": "Subscribed",
    "deposit_limits": 2000.00,
    "loss_limits": 1000.00,
    "wagering_limits": 5000.00,
    "session_time_limits": 180,
    "cooling_off_period": 14,
    "self_exclusion_period": 60,
    "account_status": "Active",
    "vip_status": "VIP",
    "loyalty_program_tiers": "Gold",
    "financial_risk_level": 0.1,
    "referral_link_code": "REF456",
    "creation_timestamp": "2024-01-16T14:20:00Z",
    "phone_verification": "Verified",
    "email_verification": "Verified",
    "bank_verification": "Verified",
    "iddoc_verification": "Verified"
  }
]'

# Generate HMAC signature
SALT="${ACCOUNT_ID}${WORKSPACE_ID}"
INFO="hmac-signing"
PRK=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -hmac "$SALT" | cut -d' ' -f2)
DERIVED_KEY=$(echo -n "${INFO}01" | openssl dgst -sha256 -hmac "$PRK" | cut -d' ' -f2)
HMAC_SIGNATURE=$(echo -n "$CUSTOMERS_DATA" | openssl dgst -sha256 -hmac "$DERIVED_KEY" | cut -d' ' -f2)

# Make API call
curl -X POST "${API_BASE_URL}/customers/${ACCOUNT_ID}/${WORKSPACE_ID}" \
  -H "Content-Type: application/json" \
  -H "x-optikpi-token: ${AUTH_TOKEN}" \
  -H "x-hmac-signature: ${HMAC_SIGNATURE}" \
  -H "x-hmac-algorithm: sha256" \
  -d "$CUSTOMERS_DATA" \
  -w "\nHTTP Status: %{http_code}\nResponse Time: %{time_total}s\n"
```

**Expected Response:**
```json
{
  "message": "2 customer profile(s) sent to Firehose successfully",
  "recordIds": ["record-id-123", "record-id-456"],
  "count": 2,
  "streamName": "datapipeline-customers-dev-stream",
  "destination": "s3://datapipeline/customers/68911b7ad58ad825ec00c5ef/68911b7ad825ec00c5ef"
}
```

### Example 3: Push Account Event

```bash
#!/bin/bash

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"
ACCOUNT_ID="68911b7ad58ad825ec00c5ef"
WORKSPACE_ID="68911b7ad825ec00c5ef"

# Account event data
ACCOUNT_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Account",
  "event_name": "Player Registration",
  "event_id": "evt_123456789",
  "event_time": "2024-01-15T10:30:00Z",
  "device": "desktop",
  "ip_address": "192.168.1.100",
  "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
  "status": "verified",
  "metadata": {
    "registration_source": "website",
    "referral_code": "REF789"
  }
}'

# Generate HMAC signature
SALT="${ACCOUNT_ID}${WORKSPACE_ID}"
INFO="hmac-signing"
PRK=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -hmac "$SALT" | cut -d' ' -f2)
DERIVED_KEY=$(echo -n "${INFO}01" | openssl dgst -sha256 -hmac "$PRK" | cut -d' ' -f2)
HMAC_SIGNATURE=$(echo -n "$ACCOUNT_EVENT" | openssl dgst -sha256 -hmac "$DERIVED_KEY" | cut -d' ' -f2)

# Make API call
curl -X POST "${API_BASE_URL}/events/account/${ACCOUNT_ID}/${WORKSPACE_ID}" \
  -H "Content-Type: application/json" \
  -H "x-optikpi-token: ${AUTH_TOKEN}" \
  -H "x-hmac-signature: ${HMAC_SIGNATURE}" \
  -H "x-hmac-algorithm: sha256" \
  -d "$ACCOUNT_EVENT" \
  -w "\nHTTP Status: %{http_code}\nResponse Time: %{time_total}s\n"
```

**Expected Response:**
```json
{
  "message": "1 account event(s) sent to Firehose successfully",
  "recordIds": ["record-id-789"],
  "count": 1,
  "streamName": "datapipeline-events-account-dev-stream",
  "destination": "s3://datapipeline/events/account/68911b7ad58ad825ec00c5ef/68911b7ad825ec00c5ef/yyyy/mm/dd"
}
```

### Example 4: Push Deposit Event

```bash
#!/bin/bash

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"
ACCOUNT_ID="68911b7ad58ad825ec00c5ef"
WORKSPACE_ID="68911b7ad825ec00c5ef"

# Deposit event data
DEPOSIT_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Deposit",
  "event_name": "Successful Deposit",
  "event_id": "evt_dep_987654321",
  "event_time": "2024-01-15T14:45:00Z",
  "amount": 500.00,
  "currency": "USD",
  "payment_method": "bank",
  "transaction_id": "txn_123456789",
  "status": "completed",
  "metadata": {
    "bank_name": "Chase Bank",
    "account_last4": "1234"
  }
}'

# Generate HMAC signature
SALT="${ACCOUNT_ID}${WORKSPACE_ID}"
INFO="hmac-signing"
PRK=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -hmac "$SALT" | cut -d' ' -f2)
DERIVED_KEY=$(echo -n "${INFO}01" | openssl dgst -sha256 -hmac "$PRK" | cut -d' ' -f2)
HMAC_SIGNATURE=$(echo -n "$DEPOSIT_EVENT" | openssl dgst -sha256 -hmac "$DERIVED_KEY" | cut -d' ' -f2)

# Make API call
curl -X POST "${API_BASE_URL}/events/deposit/${ACCOUNT_ID}/${WORKSPACE_ID}" \
  -H "Content-Type: application/json" \
  -H "x-optikpi-token: ${AUTH_TOKEN}" \
  -H "x-hmac-signature: ${HMAC_SIGNATURE}" \
  -H "x-hmac-algorithm: sha256" \
  -d "$DEPOSIT_EVENT" \
  -w "\nHTTP Status: %{http_code}\nResponse Time: %{time_total}s\n"
```

**Expected Response:**
```json
{
  "message": "1 deposit event(s) sent to Firehose successfully",
  "recordIds": ["record-id-dep-123"],
  "count": 1,
  "streamName": "datapipeline-events-deposit-dev-stream",
  "destination": "s3://datapipeline/events/deposit/68911b7ad58ad825ec00c5ef/68911b7ad825ec00c5ef/yyyy/mm/dd"
}
```

### Example 5: Push Withdraw Event

```bash
#!/bin/bash

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"
ACCOUNT_ID="68911b7ad58ad825ec00c5ef"
WORKSPACE_ID="68911b7ad825ec00c5ef"

# Withdraw event data
WITHDRAW_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Withdraw",
  "event_name": "Withdrawal Request",
  "event_id": "evt_wd_456789123",
  "event_time": "2024-01-15T16:20:00Z",
  "amount": 250.00,
  "currency": "USD",
  "payment_method": "bank",
  "transaction_id": "txn_wd_987654321",
  "status": "pending",
  "metadata": {
    "bank_name": "Wells Fargo",
    "account_last4": "5678",
    "processing_time": "2-3 business days"
  }
}'

# Generate HMAC signature
SALT="${ACCOUNT_ID}${WORKSPACE_ID}"
INFO="hmac-signing"
PRK=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -hmac "$SALT" | cut -d' ' -f2)
DERIVED_KEY=$(echo -n "${INFO}01" | openssl dgst -sha256 -hmac "$PRK" | cut -d' ' -f2)
HMAC_SIGNATURE=$(echo -n "$WITHDRAW_EVENT" | openssl dgst -sha256 -hmac "$DERIVED_KEY" | cut -d' ' -f2)

# Make API call
curl -X POST "${API_BASE_URL}/events/withdraw/${ACCOUNT_ID}/${WORKSPACE_ID}" \
  -H "Content-Type: application/json" \
  -H "x-optikpi-token: ${AUTH_TOKEN}" \
  -H "x-hmac-signature: ${HMAC_SIGNATURE}" \
  -H "x-hmac-algorithm: sha256" \
  -d "$WITHDRAW_EVENT" \
  -w "\nHTTP Status: %{http_code}\nResponse Time: %{time_total}s\n"
```

**Expected Response:**
```json
{
  "message": "1 withdraw event(s) sent to Firehose successfully",
  "recordIds": ["record-id-wd-456"],
  "count": 1,
  "streamName": "datapipeline-events-withdraw-dev-stream",
  "destination": "s3://datapipeline/events/withdraw/68911b7ad58ad825ec00c5ef/68911b7ad825ec00c5ef/yyyy/mm/dd"
}
```

### Example 6: Push Gaming Activity Event

```bash
#!/bin/bash

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"
ACCOUNT_ID="68911b7ad58ad825ec00c5ef"
WORKSPACE_ID="68911b7ad825ec00c5ef"

# Gaming activity event data
GAMING_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Gaming Activity",
  "event_name": "Slot Game Play",
  "event_id": "evt_game_789123456",
  "event_time": "2024-01-15T18:30:00Z",
  "wager_amount": 10.00,
  "win_amount": 25.00,
  "game_title": "Mega Fortune Slots",
  "game_category": "Slots",
  "session_duration": 45,
  "metadata": {
    "game_id": "mega_fortune_001",
    "provider": "NetEnt",
    "bet_lines": 20,
    "multiplier": 2.5
  }
}'

# Generate HMAC signature
SALT="${ACCOUNT_ID}${WORKSPACE_ID}"
INFO="hmac-signing"
PRK=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -hmac "$SALT" | cut -d' ' -f2)
DERIVED_KEY=$(echo -n "${INFO}01" | openssl dgst -sha256 -hmac "$PRK" | cut -d' ' -f2)
HMAC_SIGNATURE=$(echo -n "$GAMING_EVENT" | openssl dgst -sha256 -hmac "$DERIVED_KEY" | cut -d' ' -f2)

# Make API call
curl -X POST "${API_BASE_URL}/events/gaming-activity/${ACCOUNT_ID}/${WORKSPACE_ID}" \
  -H "Content-Type: application/json" \
  -H "x-optikpi-token: ${AUTH_TOKEN}" \
  -H "x-hmac-signature: ${HMAC_SIGNATURE}" \
  -H "x-hmac-algorithm: sha256" \
  -d "$GAMING_EVENT" \
  -w "\nHTTP Status: %{http_code}\nResponse Time: %{time_total}s\n"
```

**Expected Response:**
```json
{
  "message": "1 gaming activity event(s) sent to Firehose successfully",
  "recordIds": ["record-id-game-789"],
  "count": 1,
  "streamName": "datapipeline-events-gaming-activity-dev-stream",
  "destination": "s3://datapipeline/events/gaming-activity/68911b7ad58ad825ec00c5ef/68911b7ad825ec00c5ef/yyyy/mm/dd"
}
```

### Example 7: Health Check

```bash
#!/bin/bash

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"

# Health check (no HMAC required for GET requests)
curl -X GET "${API_BASE_URL}/datapipeline/health" \
  -H "x-optikpi-token: ${AUTH_TOKEN}" \
  -w "\nHTTP Status: %{http_code}\nResponse Time: %{time_total}s\n"
```

**Expected Response:**
```json
{
  "status": "healthy",
  "timestamp": "2024-01-15T10:30:00Z",
  "service": "data-pipeline-api"
}
```

## Error Handling

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

### Retry Strategy

```bash
#!/bin/bash

# Retry function with exponential backoff
retry_request() {
    local max_attempts=3
    local base_delay=1
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        echo "Attempt $attempt of $max_attempts"
        
        # Make the API call
        response=$(curl -s -w "%{http_code}" -X POST "${API_BASE_URL}/customers/${ACCOUNT_ID}/${WORKSPACE_ID}" \
          -H "Content-Type: application/json" \
          -H "x-optikpi-token: ${AUTH_TOKEN}" \
          -H "x-hmac-signature: ${HMAC_SIGNATURE}" \
          -H "x-hmac-algorithm: sha256" \
          -d "$CUSTOMER_DATA")
        
        http_code="${response: -3}"
        response_body="${response%???}"
        
        if [ "$http_code" -eq 200 ]; then
            echo "Success: $response_body"
            return 0
        elif [ "$http_code" -eq 429 ]; then
            delay=$((base_delay * 2 ** (attempt - 1)))
            echo "Rate limited. Waiting ${delay}s before retry..."
            sleep $delay
        else
            echo "Error $http_code: $response_body"
            return 1
        fi
        
        attempt=$((attempt + 1))
    done
    
    echo "Max retry attempts reached"
    return 1
}
```

## Best Practices

### 1. Data Validation
- Always validate data before sending to API
- Ensure required fields are present
- Use proper data types (numbers, strings, dates)

### 2. Batch Processing
- Use batch endpoints for multiple records
- Keep batch sizes reasonable (max 500 records)
- Implement proper error handling for partial failures

### 3. Error Handling
- Implement exponential backoff for retries
- Log all API responses for debugging
- Handle rate limiting gracefully

### 4. Security
- Keep authentication tokens secure
- Rotate tokens regularly
- Use HTTPS for all API calls

### 5. Monitoring
- Monitor API response times
- Track success/failure rates
- Set up alerts for high error rates

## Rate Limits

- **Requests per second**: 1000
- **Batch size limit**: 500 records per request
- **Rate limit window**: 1 minute
- **Rate limit response**: 429 Too Many Requests

## Support

For technical support and questions:
- Email: api-support@yourcompany.com
- Documentation: https://docs.yourcompany.com/api
- Status page: https://status.yourcompany.com

## Complete Integration Script

Here's a complete JavaScript script using the SDK that demonstrates all API endpoints:

> **💡 SDK Advantage**: This script is much simpler than the equivalent bash/curl version and automatically handles all authentication complexity.

```javascript
// Data Pipeline API Integration Script
// Complete example with all endpoints using SDK

const OptikpiDataPipelineSDK = require('../src/index');

# Configuration
API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
AUTH_TOKEN="your-auth-token-here"
ACCOUNT_ID="68911b7ad58ad825ec00c5ef"
WORKSPACE_ID="68911b7ad825ec00c5ef"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Data Pipeline API Integration Test${NC}"
echo "======================================"

# Function to generate HMAC signature
generate_hmac() {
    local data="$1"
    local salt="${ACCOUNT_ID}${WORKSPACE_ID}"
    local info="hmac-signing"
    
    local prk=$(echo -n "$AUTH_TOKEN" | openssl dgst -sha256 -hmac "$salt" | cut -d' ' -f2)
    local derived_key=$(echo -n "${info}01" | openssl dgst -sha256 -hmac "$prk" | cut -d' ' -f2)
    echo -n "$data" | openssl dgst -sha256 -hmac "$derived_key" | cut -d' ' -f2
}

# Function to make API call
make_api_call() {
    local endpoint="$1"
    local data="$2"
    local description="$3"
    
    echo -e "\n${YELLOW}Testing: $description${NC}"
    
    local hmac_signature=$(generate_hmac "$data")
    
    local response=$(curl -s -w "%{http_code}" -X POST "${API_BASE_URL}${endpoint}" \
      -H "Content-Type: application/json" \
      -H "x-optikpi-token: ${AUTH_TOKEN}" \
      -H "x-hmac-signature: ${hmac_signature}" \
      -H "x-hmac-algorithm: sha256" \
      -d "$data")
    
    local http_code="${response: -3}"
    local response_body="${response%???}"
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✅ Success${NC}"
        echo "Response: $response_body"
    else
        echo -e "${RED}❌ Error $http_code${NC}"
        echo "Response: $response_body"
    fi
}

# Test data
CUSTOMER_DATA='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "username": "john_doe",
  "full_name": "John Doe",
  "date_of_birth": "1990-01-15",
  "email": "john.doe@example.com",
  "phone_number": "+1234567890",
  "currency": "USD",
  "subscription": "Subscribed",
  "deposit_limits": 1000.00,
  "loss_limits": 500.00,
  "wagering_limits": 2000.00,
  "session_time_limits": 120,
  "cooling_off_period": 7,
  "self_exclusion_period": 30,
  "account_status": "Active",
  "vip_status": "Regular",
  "loyalty_program_tiers": "Bronze",
  "financial_risk_level": 0.3,
  "referral_link_code": "REF789",
  "creation_timestamp": "2024-01-15T10:30:00Z",
  "phone_verification": "Verified",
  "email_verification": "Verified",
  "bank_verification": "NotVerified",
  "iddoc_verification": "Verified"
}'

ACCOUNT_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Account",
  "event_name": "Player Registration",
  "event_id": "evt_123456789",
  "event_time": "2024-01-15T10:30:00Z"
}'

DEPOSIT_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Deposit",
  "event_name": "Successful Deposit",
  "event_id": "evt_dep_987654321",
  "event_time": "2024-01-15T14:45:00Z",
  "amount": 500.00,
  "currency": "USD"
}'

WITHDRAW_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Withdraw",
  "event_name": "Withdrawal Request",
  "event_id": "evt_wd_456789123",
  "event_time": "2024-01-15T16:20:00Z",
  "amount": 250.00,
  "currency": "USD"
}'

GAMING_EVENT='{
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad825ec00c5ef",
  "user_id": "user123456",
  "event_category": "Gaming Activity",
  "event_name": "Slot Game Play",
  "event_id": "evt_game_789123456",
  "event_time": "2024-01-15T18:30:00Z",
  "wager_amount": 10.00,
  "win_amount": 25.00,
  "game_title": "Mega Fortune Slots"
}'

# Run tests
make_api_call "/customers/${ACCOUNT_ID}/${WORKSPACE_ID}" "$CUSTOMER_DATA" "Customer Profile"
make_api_call "/events/account/${ACCOUNT_ID}/${WORKSPACE_ID}" "$ACCOUNT_EVENT" "Account Event"
make_api_call "/events/deposit/${ACCOUNT_ID}/${WORKSPACE_ID}" "$DEPOSIT_EVENT" "Deposit Event"
make_api_call "/events/withdraw/${ACCOUNT_ID}/${WORKSPACE_ID}" "$WITHDRAW_EVENT" "Withdraw Event"
make_api_call "/events/gaming-activity/${ACCOUNT_ID}/${WORKSPACE_ID}" "$GAMING_EVENT" "Gaming Activity Event"

# Health check
echo -e "\n${YELLOW}Testing: Health Check${NC}"
health_response=$(curl -s -w "%{http_code}" -X GET "${API_BASE_URL}/datapipeline/health" \
  -H "x-optikpi-token: ${AUTH_TOKEN}")

health_http_code="${health_response: -3}"
health_body="${health_response%???}"

if [ "$health_http_code" -eq 200 ]; then
    echo -e "${GREEN}✅ Health Check Success${NC}"
    echo "Response: $health_body"
else
    echo -e "${RED}❌ Health Check Failed $health_http_code${NC}"
    echo "Response: $health_body"
fi

console.log('\n🎉 Integration test completed!');
```

## 🚀 SDK Integration Benefits

By using the Optikpi Data Pipeline SDK instead of manual curl commands, you get:

### **Simplified Development**
- **No crypto implementation** - SDK handles HMAC/HKDF automatically
- **Clean, readable code** - Focus on business logic, not authentication
- **Type safety** - Better IDE support and error catching

### **Enhanced Reliability**
- **Built-in retry logic** - Automatic handling of network failures
- **Better error handling** - Rich error objects with detailed information
- **Request validation** - SDK validates data before sending

### **Maintenance Benefits**
- **Automatic updates** - SDK improvements without code changes
- **Security updates** - Cryptographic improvements handled automatically
- **Documentation** - Always up-to-date with latest API changes

### **Production Ready**
- **Logging support** - Built-in request/response logging
- **Performance monitoring** - Response time tracking
- **Batch operations** - Efficient handling of multiple records

## 📚 Next Steps

1. **Install the SDK**: `npm install`
2. **Review Examples**: Check the updated client examples in this folder
3. **Start Integrating**: Use the SDK examples as templates for your integration
4. **Get Support**: Contact our team for SDK-specific assistance

The SDK makes integration with the Optikpi Data Pipeline API significantly easier and more reliable than manual implementation.


