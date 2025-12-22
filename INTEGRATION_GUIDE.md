# Optikpi Data Pipeline API Integration Guide v1.0.1

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
- **Wallet Balance**: Wallet balance updates and tracking
- **Refer Friend**: Referral program events and rewards

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
| `/customers` | POST | Push customer profile data | 50 req/sec |
| `/events/account` | POST | Push account events | 250 req/sec |
| `/events/deposit` | POST | Push deposit events | 250 req/sec |
| `/events/withdraw` | POST | Push withdrawal events | 250 req/sec |
| `/events/gaming-activity` | POST | Push gaming activity events | 250 req/sec |
| `/events/wallet-balance` | POST | Push wallet balance events | 250 req/sec |
| `/events/refer-friend` | POST | Push refer friend events | 250 req/sec |
| `/extattributes` | POST | Push extended attributes data | 50 req/sec |
| `/datapipeline/health` | GET | Health check endpoint | No limit |

### Required Headers
| Header | Type | Description | Example |
|--------|------|-------------|---------|
| `x-optikpi-account-id` | string | Your account identifier | `68911b7ad58ad825ec00c5ef` |
| `x-optikpi-workspace-id` | string | Your workspace identifier | `68911b7ad825ec00c5ef` |

### Successful Response Format
```json
{
  "message": "Success description",
  "recordIds": ["record-id-1", "record-id-2"],
  "count": 2
}
```

## 📋 Data Models

### 📊 Customer Profile Schema
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| username | String | No | User's username |
| full_name | String | No | User's full name |
| first_name | String | No | User's first name |
| last_name | String | No | User's last name |
| date_of_birth | String | No | User's date of birth (date format) |
| email | String | No | User's email address (email format) |
| phone_number | String | No | User's phone number |
| gender | String | No | User's gender |
| country | String | No | User's country |
| city | String | No | User's city |
| language | String | No | User's preferred language |
| currency | String | No | User's preferred currency |
| phone_verification | String | No | Phone verification status |
| email_verification | String | No | Email verification status |
| bank_verification | String | No | Bank verification status |
| iddoc_verification | String | No | ID document verification status |
| marketing_email_preference | String | No | Marketing email preference |
| notifications_preference | String | No | Notifications preference |
| subscription | String | No | Subscription status (Subscribed\|Unsubscribed) |
| privacy_settings | String | No | Privacy settings |
| deposit_limits | Number | No | Deposit limits |
| loss_limits | Number | No | Loss limits |
| wagering_limits | Number | No | Wagering limits |
| session_time_limits | Number | No | Session time limits |
| cooling_off_expiry_date | String | No | Cooling off expiry date (date-time format). Example: `"2024-01-15T10:30:00Z"`
| self_exclusion_expiry_date | String | No | Self exclusion expiry date (date-time format). Example: `"2024-01-15T10:30:00Z"`
| reality_checks_notification | String | No | Reality checks notification frequency |
| vip_status | String | No | VIP status |
| loyalty_program_tiers | String | No | Loyalty program tier |
| account_status | String | No | Account status |
| bonus_abuser | String | No | Bonus abuser status |
| financial_risk_level | Number | No | Financial risk level |
| acquisition_source | String | No | Acquisition source |
| partner_id | String | No | Partner identifier |
| referral_link_code | String | No | Referral link code |
| referral_limit_reached | String | No | Referral limit status |
| creation_timestamp | String | Yes | Account creation timestamp (date-time format). Example: `"2024-01-15T10:30:00Z"`
| risk_score_level | String | No | Risk score level |
| marketing_sms_preference | String | No | Marketing SMS preference |
| custom_data | Object/String | No | Custom data in JSON format. Examples: `{"email":true,"sms":false}` or `"{\"email\":true,\"sms\":true}"`
| self_exclusion_by | String | No | Self exclusion initiated by |
| self_exclusion_by_type | String | No | Self exclusion by type |
| self_exclusion_check_time | String | No | Self exclusion check time (date-time format). Example: `"2024-01-15T10:30:00Z"`
| self_exclusion_created_time | String | No | Self exclusion created time (date-time format). Example: `"2024-01-15T10:30:00Z"`
| closed_time | String | No | Account closed time (date-time format). Example: `"2024-01-15T10:30:00Z"`
| real_money_enabled | String | No | Real money enabled status |
| push_token | String | No | Push notification token |
| andriod_push_token | String | No | Android Push notification token |
| ios_push_token | String | No | IOS Push notification token |
| windows_push_token | String | No | Windows Push notification token |
| mac_dmg_push_token | String | No | MAC Push notification token |

### Account Event
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| event_category | String | Yes | Category of the event (Account) |
| event_name | String | Yes | Name of the account event |
| event_id | String | Yes | Unique event identifier |
| event_time | String | Yes | Timestamp when the event occurred (date-time format). Example: "2024-01-15T10:30:00Z" |
| affiliate_id | String | No | Affiliate identifier |
| partner_id | String | No | Partner identifier |
| device | String | No | Device type used |
| campaign_code | String | No | Campaign code |
| status | String | No | Verification status |
| reason | String | No | Reason for failure or additional information |

### Deposit Event
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| event_category | String | Yes | Category of the event (Deposit) |
| event_name | String | Yes | Name of the deposit event (Successful Deposit\|First-Time Deposit\|Second-Time Deposit\|Third-Time Deposit\|Failed Deposit) |
| event_id | String | Yes | Unique event identifier |
| event_time | String | Yes | Timestamp when the event occurred (date-time format). Example: "2024-01-15T10:30:00Z" |
| payment_method | String | No | Payment method used |
| transaction_id | String | No | Transaction identifier |
| amount | Number | Yes | Deposit amount |
| payment_provider_id | String | No | Payment provider identifier |
| payment_provider_name | String | No | Payment provider name |
| failure_reason | String | No | Reason for deposit failure |

### Withdraw Event
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| event_category | String | Yes | Category of the event (Withdraw) |
| event_name | String | Yes | Name of the withdrawal event (Successful Withdrawal\|Failed Withdrawal\|Pending Withdrawal\|Withdrawal Reversal) |
| event_id | String | Yes | Unique event identifier |
| event_time | String | Yes | Timestamp when the event occurred (date-time format). Example: "2024-01-15T10:30:00Z" |
| amount | Number | Yes | Withdrawal amount |
| payment_method | String | No | Payment method used |
| transaction_id | String | No | Transaction identifier |
| failure_reason | String | No | Reason for withdrawal failure |

### Gaming Activity Event
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| event_category | String | Yes | Category of the event (Gaming Activity) |
| event_name | String | Yes | Name of the gaming activity event |
| event_id | String | Yes | Unique event identifier |
| event_time | String | Yes | Timestamp when the event occurred (date-time format). Example: "2024-01-15T10:30:00Z" |
| wager_amount | Number | No | Amount wagered |
| win_amount | Number | No | Amount won |
| loss_amount | Number | No | Amount lost |
| game_id | String | No | Game identifier |
| game_title | String | No | Title of the game |
| provider | String | No | Game provider |
| bonus_id | String | No | Bonus identifier |
| free_spin_id | String | No | Free spin identifier |
| jackpot_amount | Number | No | Jackpot amount |
| num_spins_played | Integer | No | Number of spins played |
| game_theme | String | No | Theme of the game |
| remaining_spins | Integer | No | Number of remaining spins |
| bet_value_per_spin | Number | No | Bet value per spin |
| wagering_requirements_met | Boolean | No | Whether wagering requirements are met |
| free_spin_expiry_date | String | No | Free spin expiry date (date-time format). Example: `"2024-01-15T10:30:00Z"`
| campaign_id | String | No | Campaign identifier |
| campaign_name | String | No | Campaign name |
| rtp | Number | No | Return to Player percentage |
| game_category | String | No | Category of the game |
| winning_bet_amount | Number | No | Amount of winning bet |
| jackpot_type | String | No | Type of jackpot |
| volatility | String | No | Game volatility level |
| min_bet | Number | No | Minimum bet amount |
| max_bet | Number | No | Maximum bet amount |
| number_of_reels | Integer | No | Number of reels in the game |
| number_of_paylines | Integer | No | Number of paylines in the game |
| feature_types | String | No | Types of features available in the game |
| game_release_date | String | No | Game release date (date-time format). Example: `"2024-01-15T10:30:00Z"`
| live_dealer_availability | Boolean | No | Whether live dealer is available |
| side_bets_availability | Boolean | No | Whether side bets are available |
| multiplayer_option | Boolean | No | Whether multiplayer option is available |
| auto_play | Boolean | No | Whether auto play is available |
| poker_variant | String | No | Poker variant type |
| tournament_name | String | No | Name of the tournament |
| buy_in_amount | Number | No | Buy-in amount for tournament |
| table_type | String | No | Type of table |
| stakes_level | String | No | Stakes level |
| number_of_players | Integer | No | Number of players |
| game_duration | Integer | No | Duration of the game in minutes |
| hand_volume | Integer | No | Volume of hands played |
| player_position | String | No | Position of the player |
| final_hand | String | No | Final hand result |
| rake_contribution | Number | No | Rake contribution amount |
| multi_tabling_indicator | Boolean | No | Whether multi-tabling is active |
| session_result | String | No | Result of the session |
| vip_status | String | No | VIP status of the player |
| blind_level | String | No | Blind level in poker |
| rebuy_and_addon_info | String | No | Rebuy and addon information |
| sport_type | String | No | Type of sport for betting |
| betting_market | String | No | Betting market type |
| odds | Number | No | Betting odds |
| live_betting_availability | Boolean | No | Whether live betting is available |
| result | String | No | Result of the bet/game |
| bet_status | String | No | Status of the bet |
| betting_channel | String | No | Channel used for betting |
| bonus_type | String | No | Type of bonus |
| bonus_amount | Number | No | Amount of bonus |
| free_spin_start_date | String | No | Free spin start date (date-time format). Example: `"2024-01-15T10:30:00Z"`
| num_spins_awarded | Integer | No | Number of spins awarded |
| bonus_code | String | No | Bonus code used |
| parent_game_category | String | No | Parent game category |
| currency | String | No | Currency used |
| money_type | String | No | Type of money (real/virtual) |
| transaction_type | String | No | Type of transaction |

### Wallet Balance Event
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| event_category | String | Yes | Category of the event (Wallet Balance) |
| event_name | String | Yes | Name of the wallet balance event |
| event_id | String | Yes | Unique event identifier |
| event_time | String | Yes | Timestamp when the event occurred (date-time format). Example: "2024-01-15T10:30:00Z" |
| wallet_type | String | No | Type of wallet |
| currency | String | No | Currency of the wallet balance |
| current_cash_balance | Number | No | Current cash balance in the wallet |
| current_bonus_balance | Number | No | Current bonus balance in the wallet |
| current_total_balance | Number | No | Current total balance (cash + bonus) in the wallet |
| blocked_amount | Number | No | Amount blocked in the wallet |

### Refer Friend Event
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| account_id | String | Yes | Account identifier - one account can have multiple workspaces |
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| event_category | String | Yes | Category of the event (Refer Friend) |
| event_name | String | Yes | Name of the refer friend event |
| event_id | String | Yes | Unique event identifier |
| event_time | String | Yes | Timestamp when the event occurred (date-time format). Example: "2024-01-15T10:30:00Z" |
| referral_code_used | String | No | Referral code that was used |
| successful_referral_confirmation | Boolean | No | Whether the referral was successfully confirmed |
| reward_type | String | No | Type of reward (e.g., bonus, cash, points) |
| reward_claimed_status | String | No | Status of reward claim (e.g., pending, claimed, expired) |
| referee_user_id | String | No | User ID of the person who was referred |
| referee_registration_date | String | No | Date when the referee registered (date-time format). Example: "2024-01-15T10:30:00Z" |
| referee_first_deposit | Number | No | First deposit amount made by the referee |

### Extended Attributes
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| workspace_id | String | Yes | Workspace identifier - belongs to an account |
| user_id | String | Yes | Unique user identifier |
| list_name | String | Yes | Name of the list or category for the extended attributes |
| ext_data | Object/String | Yes | Extended attributes data in JSON format. Examples: `{"email":true,"sms":false}` or `"{\"email\":true,\"sms\":true}"`


> **📅 Timestamp Format**: All date-time fields use ISO 8601 format with UTC timezone (e.g., `"2024-01-15T10:30:00Z"`). The `Z` suffix indicates UTC timezone.

## 🚀 Quick Start

Get started with the Optikpi Data Pipeline API in just 5 minutes using our official SDK.

### Prerequisites

Before you begin, you'll need:

1. Your authentication token
2. Account ID and Workspace ID
3. API Gateway URL
4. Node.js environment

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
  event_name: 'Slot Game Play',
  event_id: 'evt_game_789123456',
  event_time: new Date().toISOString(),
  wager_amount: 10.00,
  win_amount: 25.00,
  game_title: 'Mega Fortune Slots',
  game_category: 'Slots',
  session_duration: 45
};
const gamingResult = await sdk.sendGamingActivityEvent(gamingEvent);

// Wallet Balance Event
const walletBalanceEvent = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_category: 'Wallet Balance',
  event_name: 'Balance Update',
  event_id: 'evt_wallet_123456789',
  event_time: new Date().toISOString(),
  wallet_type: 'main',
  currency: 'USD',
  current_cash_balance: 1000.00,
  current_bonus_balance: 50.00,
  current_total_balance: 1050.00,
  blocked_amount: 0.00
};
const walletBalanceResult = await sdk.sendWalletBalanceEvent(walletBalanceEvent);

// Refer Friend Event
const referFriendEvent = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 'user123',
  event_category: 'Refer Friend',
  event_name: 'Referral Confirmed',
  event_id: 'evt_refer_987654321',
  event_time: new Date().toISOString(),
  referral_code_used: 'REF123',
  successful_referral_confirmation: true,
  reward_type: 'bonus',
  reward_claimed_status: 'claimed',
  referee_user_id: 'user456',
  referee_registration_date: new Date().toISOString(),
  referee_first_deposit: 100.00
};
const referFriendResult = await sdk.sendReferFriendEvent(referFriendEvent);
```

### 4. Customer Extension Attributes
```javascript
// Format 1: Object (auto-converted to JSON string)
const extAttributesEvent = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 't345345',
  list_name: 'BINGO_PREFERENCES',
  ext_data: {
    "Email": "True",
    "SMS": "True"
  }
};

// Format 2: JSON string (also supported)
const extAttributesEventString = {
  account_id: 'your-account-id',
  workspace_id: 'your-workspace-id',
  user_id: 't345345',
  list_name: 'BINGO_PREFERENCES',
  ext_data: '{\"Email\":\"True\",\"SMS\":\"True\"}'
};

const extAttributesResult = await sdk.sendExtendedAttributes(extAttributesEvent);
```

### 5. Health Check
```javascript
const health = await sdk.healthCheck();
console.log('API Status:', health.success ? 'Healthy' : 'Unhealthy');
```

### 6. Batch Operations
```javascript
const batchData = {
  customers: [customer1, customer2],
  depositEvents: [deposit1, deposit2],
  gamingEvents: [gaming1, gaming2],
  walletBalanceEvents: [walletBalance1, walletBalance2],
  referFriendEvents: [referFriend1, referFriend2],
  extendedAttributes: [extAttr1, extAttr2]
};
const batchResult = await sdk.sendBatch(batchData);
```

### SDK Benefits
- 🔐 **Automatic Authentication**: Handles HMAC signature generation
- ✅ **Built-in Validation**: Validates data before sending
- 🔄 **Retry Logic**: Automatic retry with exponential backoff
- 🛡 **Error Handling**: Rich error objects with detailed information
- 📝 **Type Safety**: Full TypeScript support
- ⚡ **High Performance**: Optimized for high-throughput data ingestion

For complete SDK documentation, see the README.md file.

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

- **Customer endpoints**: 50 requests per second
- **Event endpoints**: 250 requests per second
- **Batch size limit**: 500 records per request
- **Rate limit window**: 1 minute
- **Rate limit response**: 429 Too Many Requests

### Rate Limit Handling
```javascript
class RateLimitHandler {
  constructor(endpointType = 'events') {
    this.requests = [];
    this.maxRequests = endpointType === 'customers' ? 50 : 250;
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
- **GitHub Issues**: https://github.com/optikpi/datapipeline-sdk/issues

## 🚀 Next Steps
1. **Choose Integration Method**: Decide between manual integration or SDK
2. **Get Credentials**: Contact your account manager for API credentials
3. **Start Development**: Use the examples in this guide as templates
4. **Test Integration**: Use the health check endpoint to verify connectivity
5. **Go Live**: Deploy your integration to production

## 📚 Additional Resources
- **SDK Documentation**: README.md
- **Java SDK Guide**: ../java/README.md
- **Integration Examples**: ../examples/
- **Changelog**: CHANGELOG.md

This integration guide provides everything you need to successfully integrate with the Optikpi Data Pipeline API. Choose the approach that best fits your technology stack and requirements.
