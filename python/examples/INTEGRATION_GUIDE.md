# Data Pipeline API Python Integration Guide

## Overview

This guide provides comprehensive instructions for third-party developers to integrate with the **Data Pipeline API** using Python. The API enables secure, real-time ingestion of customer profiles and event data into our enterprise data pipeline infrastructure.

> **💡 SDK Integration Available**: For easier integration, we now provide the **Optikpi Data Pipeline SDK for Python** that handles all authentication, HMAC signature generation, and API communication automatically. See the [Quick Start](#quick-start) section for SDK usage examples.

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
cd ~/optikpi_datapipeline-sdk-main/optikpi-datapipeline-sdk/python
poetry install
```

### 3. Test Your Connection
```python
from index import OptikpiDataPipelineSDK

sdk = OptikpiDataPipelineSDK({
    'authToken': 'your-auth-token',
    'accountId': 'your-account-id',
    'workspaceId': 'your-workspace-id',
    'baseURL': 'https://your-api-gateway-url/apigw/ingest'
})

# Health check
health = sdk.health_check()
print('API Status:', 'Healthy' if health['success'] else 'Unhealthy')
```

### 4. Send Your First Data
```python
# Simple customer profile
customer_data = {
    'account_id': 'your-account-id',
    'workspace_id': 'your-workspace-id',
    'user_id': 'user123',
    'username': 'john_doe',
    'email': 'john@example.com',
    # ... other required fields
}

result = sdk.send_customer_profile(customer_data)
if result['success']:
    print('✅ Customer profile sent successfully!')
    print('Response:', result['data'])
else:
    print('❌ Failed to send customer profile:', result['error'])
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
| **Salt** | `account_id + workspace_id` (from headers) | `688743ad724a144fc1e051d967bed03cc39204069f2f0366` |
| **Info** | Context string | `"hmac-signing"` |

#### Security Benefits
- 🔒 **Request Integrity**: Ensures data hasn't been tampered with
- 🔒 **Replay Protection**: Each request has a unique signature
- 🔒 **Context Binding**: Keys are bound to specific account/workspace
- 🔒 **Cryptographic Strength**: Uses standardized HKDF algorithm

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
| `x-optikpi-account-id` | string | Your account identifier | `688743ad724a144fc1e051d9` |
| `x-optikpi-workspace-id` | string | Your workspace identifier | `67bed03cc39204069f2f0366` |

### Response Format
All successful responses follow this structure:
```json
{
  "message": "Success description",
  "count": 1
}
```

## 📋 Data Models

### Customer Profile
<details>
<summary><strong>📊 Customer Profile Schema</strong></summary>

**Endpoint**: `POST /customers`

**Description**: Complete customer profile information including personal details, preferences, and account settings.

**Required Fields**: 25 fields
**Optional Fields**: 15 fields

```python
customer_data = {
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
    "acquisition_source": "string (optional)",
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
</details>

### Account Event
```python
account_event = {
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
    "metadata": "dict (optional)"
}
```

### Deposit Event
```python
deposit_event = {
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
    "metadata": "dict (optional)"
}
```

### Withdraw Event
```python
withdraw_event = {
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
    "metadata": "dict (optional)"
}
```

### Gaming Activity Event
```python
gaming_event = {
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
    "metadata": "dict (optional)"
}
```

## Integration Examples

### Prerequisites
Before making API calls, you need:
1. Your authentication token
2. Account ID and Workspace ID
3. The Optikpi Data Pipeline SDK installed

### SDK Setup
```python
from index import OptikpiDataPipelineSDK

sdk = OptikpiDataPipelineSDK({
    'authToken': 'your-auth-token',
    'accountId': 'your-account-id',
    'workspaceId': 'your-workspace-id',
    'baseURL': 'https://your-api-gateway-url/apigw/ingest'
})
```

### SDK vs Manual Integration
| Feature | Manual (requests) | SDK |
|---------|------------------|-----|
| **Setup** | Complex HMAC/HKDF implementation | Simple initialization |
| **Authentication** | Manual header management | Automatic |
| **Error Handling** | Basic HTTP status codes | Rich error objects |
| **Retry Logic** | Manual implementation | Built-in retry mechanism |
| **Data Validation** | Manual validation | Built-in validation |
| **Maintenance** | High (crypto code) | Low (SDK updates) |

### Example 1: Push Single Customer Profile (Using SDK)

> **📝 Note**: This example uses the Optikpi Data Pipeline SDK for simplified integration. The SDK automatically handles all authentication and HMAC signature generation.

```python
#!/usr/bin/env python3
"""
Send Customer Profile Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

# Load environment variables
load_dotenv()

# Initialize SDK
sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Customer data
customer_data = {
    "account_id": os.getenv('ACCOUNT_ID'),
    "workspace_id": os.getenv('WORKSPACE_ID'),
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
}

# Send customer profile using SDK
try:
    result = sdk.send_customer_profile(customer_data)
    
    if result['success']:
        print('✅ Customer profile sent successfully!')
        print(f"Response: {result['data']}")
        print(f"HTTP Status: {result['status']}")
    else:
        print(f"❌ Failed to send customer profile: {result['error']}")
        print(f"HTTP Status: {result['status']}")
except Exception as error:
    print(f'❌ SDK Error: {str(error)}')
```

**Expected Response:**
```json
{
  "message": "1 customer profile(s) processed successfully",
  "count": 1
}
```

### Example 2: Push Multiple Customer Profiles (Batch)

```python
#!/usr/bin/env python3
"""
Send Multiple Customer Profiles Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

# Load environment variables
load_dotenv()

# Initialize SDK
sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Multiple customers data
customers_data = [
    {
        "account_id": os.getenv('ACCOUNT_ID'),
        "workspace_id": os.getenv('WORKSPACE_ID'),
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
        "account_id": os.getenv('ACCOUNT_ID'),
        "workspace_id": os.getenv('WORKSPACE_ID'),
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
]

# Send multiple profiles
try:
    result = sdk.send_customer_profile(customers_data)
    
    if result['success']:
        print(f"✅ {len(customers_data)} customer profiles sent successfully!")
        print(f"Response: {result['data']}")
    else:
        print(f"❌ Failed: {result['error']}")
except Exception as error:
    print(f'❌ Error: {str(error)}')
```

### Example 3: Push Account Event

```python
#!/usr/bin/env python3
"""
Send Account Event Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Account event data
account_event = {
    "account_id": os.getenv('ACCOUNT_ID'),
    "workspace_id": os.getenv('WORKSPACE_ID'),
    "user_id": "user123456",
    "event_category": "Account",
    "event_name": "Player Registration",
    "event_id": "evt_123456789",
    "event_time": "2024-01-15T10:30:00Z",
    "device": "desktop",
    "ip_address": "192.168.1.100",
    "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
    "status": "verified",
    "metadata": {
        "registration_source": "website",
        "referral_code": "REF789"
    }
}

# Send account event
try:
    result = sdk.send_account_event(account_event)
    
    if result['success']:
        print('✅ Account event sent successfully!')
        print(f"Response: {result['data']}")
    else:
        print(f"❌ Failed: {result['error']}")
except Exception as error:
    print(f'❌ Error: {str(error)}')
```

### Example 4: Push Deposit Event

```python
#!/usr/bin/env python3
"""
Send Deposit Event Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Deposit event data
deposit_event = {
    "account_id": os.getenv('ACCOUNT_ID'),
    "workspace_id": os.getenv('WORKSPACE_ID'),
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
}

# Send deposit event
try:
    result = sdk.send_deposit_event(deposit_event)
    
    if result['success']:
        print('✅ Deposit event sent successfully!')
        print(f"Response: {result['data']}")
    else:
        print(f"❌ Failed: {result['error']}")
except Exception as error:
    print(f'❌ Error: {str(error)}')
```

### Example 5: Push Withdraw Event

```python
#!/usr/bin/env python3
"""
Send Withdraw Event Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Withdraw event data
withdraw_event = {
    "account_id": os.getenv('ACCOUNT_ID'),
    "workspace_id": os.getenv('WORKSPACE_ID'),
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
}

# Send withdraw event
try:
    result = sdk.send_withdraw_event(withdraw_event)
    
    if result['success']:
        print('✅ Withdraw event sent successfully!')
        print(f"Response: {result['data']}")
    else:
        print(f"❌ Failed: {result['error']}")
except Exception as error:
    print(f'❌ Error: {str(error)}')
```

### Example 6: Push Gaming Activity Event

```python
#!/usr/bin/env python3
"""
Send Gaming Activity Event Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Gaming activity event data
gaming_event = {
    "account_id": os.getenv('ACCOUNT_ID'),
    "workspace_id": os.getenv('WORKSPACE_ID'),
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
}

# Send gaming event
try:
    result = sdk.send_gaming_activity_event(gaming_event)
    
    if result['success']:
        print('✅ Gaming event sent successfully!')
        print(f"Response: {result['data']}")
    else:
        print(f"❌ Failed: {result['error']}")
except Exception as error:
    print(f'❌ Error: {str(error)}')
```

### Example 7: Health Check

```python
#!/usr/bin/env python3
"""
Health Check Example
"""
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Health check
try:
    result = sdk.health_check()
    
    if result['success']:
        print('✅ API is healthy!')
        print(f"Response: {result['data']}")
    else:
        print(f"❌ API health check failed: {result['error']}")
except Exception as error:
    print(f'❌ Error: {str(error)}')
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

```python
import time
from index import OptikpiDataPipelineSDK

def retry_request(sdk, data, max_attempts=3, base_delay=1):
    """Retry function with exponential backoff"""
    
    for attempt in range(1, max_attempts + 1):
        print(f"Attempt {attempt} of {max_attempts}")
        
        try:
            result = sdk.send_customer_profile(data)
            
            if result['success']:
                print(f"✅ Success: {result['data']}")
                return result
            elif result['status'] == 429:
                delay = base_delay * (2 ** (attempt - 1))
                print(f"⚠️  Rate limited. Waiting {delay}s before retry...")
                time.sleep(delay)
            else:
                print(f"❌ Error {result['status']}: {result['error']}")
                return result
        
        except Exception as error:
            print(f"❌ Exception: {str(error)}")
            if attempt < max_attempts:
                delay = base_delay * (2 ** (attempt - 1))
                print(f"Waiting {delay}s before retry...")
                time.sleep(delay)
    
    print("Max retry attempts reached")
    return {'success': False, 'error': 'Max retries exceeded'}
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
- Use environment variables for credentials
- Never commit credentials to version control
- Rotate tokens regularly

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

For technical support an