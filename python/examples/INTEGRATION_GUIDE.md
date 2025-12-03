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
- **Wallet Balance Events**: Cash, bonus, total, and blocked amounts
- **Refer Friend Events**: Referral tracking with rewards and conversion status

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
customer = CustomerProfile(
    account_id='your-account-id',
    workspace_id='your-workspace-id',
    user_id="user123456",
    username="john_doe",
    email="john.doe@example.com",
    # ... other required fields
)
validation = customer.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Customer event validated successfully!")


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
| `/events/wallet-balance` | POST | Push wallet balance events | 1000 req/sec |
| `/events/refer-friend` | POST | Push refer-a-friend events | 1000 req/sec |
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
customer = CustomerProfile(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    username="string (required)",
    full_name="string (required)",
    first_name="string (optional)",
    last_name="string (optional)",
    date_of_birth="YYYY-MM-DD",
    email="example@email.com",
    phone_number="+1234567890",
    gender="Male",
    country="India",
    city="Chennai",
    language="English",
    currency="INR",
    marketing_email_preference="Opt-in",
    notifications_preference="Opt-in",
    subscription="Subscribed",
    privacy_settings="public",
    deposit_limits=1000,
    loss_limits=500,
    wagering_limits=1500,
    session_time_limits=120,
    cooling_off_period=7,
    self_exclusion_period=30,
    reality_checks_notification="weekly",
    account_status="Active",
    vip_status="Regular",
    loyalty_program_tiers="Gold",
    bonus_abuser="Not flagged",
    financial_risk_level=0.3,
    acquisition_source="Google Ads",
    partner_id="partner123",
    affliate_id="affiliate456",
    referral_link_code="ref123",
    referral_limit_reached="Not Reached",
    creation_timestamp="2024-01-15T10:30:00Z",
    phone_verification="Verified",
    email_verification="Verified",
    bank_verification="Verified",
    iddoc_verification="Verified"
)
```
</details>

### Account Event
```python
account = AccountEvent(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    event_category="string (required)",
    event_name="string (required)",
    event_id="string (required)",
    event_time="2024-01-15T10:30:00Z",  # ISO 8601 format
    device="desktop",  # optional: desktop/mobile/tablet
    ip_address="192.168.1.1",  # optional
    user_agent="Mozilla/5.0",  # optional
    status="verified",  # optional: verified/failed
    metadata={"key1": "value1", "key2": "value2"}  # optional dictionary
)
```

### Deposit Event
```python
deposit = DepositEvent(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    event_category="string (required)",
    event_name="string (required)",
    event_id="string (required)",
    event_time="2024-01-15T10:30:00Z",  # ISO 8601 format
    amount=100.0,  # required number
    currency="USD",  # optional
    payment_method="bank",  # optional: bank/credit_card
    transaction_id="txn_123456",  # optional
    status="completed",  # optional
    metadata={"note": "First deposit"}  # optional dictionary
)
```

### Withdraw Event
```python
withdraw = WithdrawEvent(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    event_category="string (required)",
    event_name="string (required)",
    event_id="string (required)",
    event_time="2024-01-15T12:00:00Z",  # ISO 8601 format
    amount=50.0,  # optional number
    currency="USD",  # optional
    payment_method="bank",  # optional
    transaction_id="txn_654321",  # optional
    status="completed",  # optional
    metadata={"note": "First withdrawal"}  # optional dictionary
)
```

### Gaming Activity Event
```python
gaming = GamingActivityEvent(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    event_category="string (required)",
    event_name="string (required)",
    event_id="string (required)",
    event_time="2024-01-15T12:00:00Z",  # ISO 8601 format
    wager_amount=100.0,  # optional number
    win_amount=150.0,  # optional number
    game_title="Poker",  # optional
    game_category="Card",  # optional
    session_duration=3600,  # optional, in seconds
    metadata={"note": "Evening session"}  # optional dictionary
)
```
### Wallet Balance Event
```python
wallet = WalletBalanceEvent(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    event_category="Wallet Balance",
    event_name="Balance Updated",
    event_id="evt_wallet_001",
    event_time="2024-01-15T14:45:00Z",  # ISO 8601 format

    currency="USD",  # optional
    
    current_cash_balance=500.00,   # required number
    current_bonus_balance=100.00,  # optional number
    current_total_balance=600.00,  # required number
    blocked_amount=20.00,          # optional number
    metadata={"source": "deposit"} # optional dictionary
)
```

### Refer Friend Event
```python
referral = ReferFriendEvent(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    event_category="Refer Friend",
    event_name="Referral Successful",
    event_id="evt_rf_001",
    event_time="2024-01-15T15:00:00Z",  # ISO 8601 format

    referral_code_used="REF123",                # required string
    referee_user_id="user789",                  # required string
    successful_referral_confirmation=True,      # required bool
    reward_type="bonus",                        # optional
    reward_amount=50.00,                        # optional
    reward_claimed_status="claimed",            # optional: claimed/pending/expired
    metadata={"campaign": "HOLIDAY_REFER"}      # optional dictionary
)
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
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
from index import OptikpiDataPipelineSDK
from models.CustomerProfile import CustomerProfile

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
customer = CustomerProfile(
    account_id="string (required)",
    workspace_id="string (required)",
    user_id="string (required)",
    username="string (required)",
    full_name="string (required)",
    first_name="string (optional)",
    last_name="string (optional)",
    date_of_birth="YYYY-MM-DD",
    email="example@email.com",
    phone_number="+1234567890",
    gender="Male",
    country="India",
    city="Chennai",
    language="English",
    currency="INR",
    marketing_email_preference="Opt-in",
    notifications_preference="Opt-in",
    subscription="Subscribed",
    privacy_settings="public",
    deposit_limits=1000,
    loss_limits=500,
    wagering_limits=1500,
    session_time_limits=120,
    cooling_off_period=7,
    self_exclusion_period=30,
    reality_checks_notification="weekly",
    account_status="Active",
    vip_status="Regular",
    loyalty_program_tiers="Gold",
    bonus_abuser="Not flagged",
    financial_risk_level=0.3,
    acquisition_source="Google Ads",
    partner_id="partner123",
    affliate_id="affiliate456",
    referral_link_code="ref123",
    referral_limit_reached="Not Reached",
    creation_timestamp="2024-01-15T10:30:00Z",
    phone_verification="Verified",
    email_verification="Verified",
    bank_verification="Verified",
    iddoc_verification="Verified"
)
validation = customer.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Customer event validated successfully!")
# Send customer profile using SDK
try:
    result = sdk.send_customer_profile(customer)
    
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
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
from index import OptikpiDataPipelineSDK
import json
from models.CustomerProfile import CustomerProfile

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
CUSTOMERS_DATA = '''
[
  {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad58ad825ec00c5ef",
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
    "workspace_id": "68911b7ad58ad825ec00c5ef",
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
'''

# Parse JSON to Python list
customers = json.loads(CUSTOMERS_DATA)

# Validate each customer
for i, data in enumerate(customers, start=1):
    print(f"\n🔍 Validating Customer #{i}: {data['username']}")
    customer = CustomerProfile(**data)
    validation = customer.validate()

    if not validation["isValid"]:
        print(f"❌ Validation errors for {data['username']}: {validation['errors']}")
        sys.exit(1)
    else:
        print(f"✅ Customer {data['username']} validated successfully!")

    # Send multiple profiles
    try: 
        customer_dict = customer.to_dict()
        result = sdk.send_customer_profile(customer_dict)
        
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
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
from index import OptikpiDataPipelineSDK
from models.AccountEvent import AccountEvent

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Account event data
account = AccountEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="chandru123",
    event_category="Account",
    event_name="Player Registration",
    event_id="evt_123456789",
    event_time="2024-01-15T10:30:00Z",
    device="desktop",
    status="verified",
    affiliate_id="aff_123",
    partner_id="partner_456",
    campaign_code="CAMPAIGN_001",
    reason="Registration completed successfully"
)

validation = account.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Account event validated successfully!")

# Send account event
try:
    result = sdk.send_account_event(account)
    
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
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
from index import OptikpiDataPipelineSDK
from models.DepositEvent import DepositEvent

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Deposit event data
deposit = DepositEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="user123456",
    event_category="Deposit",
    event_name="Successful Deposit",
    event_id="evt_dep_987654321",
    event_time="2024-01-15T14:45:00Z",
    amount=500.00,
    currency="USD",
    payment_method="bank",
    transaction_id="txn_123456789",
    status="success",
    metadata={
        "bank_name": "Chase Bank",
        "account_last4": "1234"
    }
)

validation = deposit.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Deposit event validated successfully!")

# Send deposit event
try:
    result = sdk.send_deposit_event(deposit)
    
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
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
from index import OptikpiDataPipelineSDK
from models.WithdrawEvent import WithdrawEvent

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Withdraw event data
withdraw = WithdrawEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="user123456",
    event_category="Withdraw",
    event_name="Successful Withdrawal",
    event_id="evt_wd_987654321",
    event_time="2024-01-15T14:45:00Z",
    amount=250.00,
    currency="USD",
    payment_method="bank",
    transaction_id="txn_wd_123456789",
    status="success",
    withdrawal_reason=None  # Make sure your class uses 'withdrawal_reason' instead of 'failure_reason'
)

validation = withdraw.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Withdraw event validated successfully!")

# Send withdraw event
try:
    result = sdk.send_withdraw_event(withdraw)
    
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
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
from index import OptikpiDataPipelineSDK
from models.GamingActivityEvent import GamingActivityEvent

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

# Gaming event data
gaming = GamingActivityEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="user123411",
    event_category="Gaming Activity",
    event_name="Play Casino Game",
    event_id="1234",
    event_time="2024-01-15T10:30:00Z",
    game_id= "123",
    game_title= "poker"
)
validation = gaming.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Gaming event validated successfully!")

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
### Example 8: Push Wallet Balance Event
```python
#!/usr/bin/env python3
"""
Send Wallet Balance Event Example
"""
import os
from dotenv import load_dotenv
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK
from models.WalletBalanceEvent import WalletBalanceEvent

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

wallet = WalletBalanceEvent(
    account_id=os.getenv('ACCOUNT_ID'),
    workspace_id=os.getenv('WORKSPACE_ID'),
    user_id="user123",
    event_category="Wallet Balance",
    event_name="Balance Updated",
    event_id="evt_wallet_987654321",
    event_time="2024-01-15T10:30:00Z",
    currency="USD",
    current_cash_balance=450.00,
    current_bonus_balance=50.00,
    current_total_balance=500.00,
    blocked_amount=0.00
)

validation = wallet.validate()
if not validation["isValid"]:
    print("❌ Validation errors:", validation["errors"])
    sys.exit(1)

print("✅ Wallet event validated successfully!")

try:
    result = sdk.send_wallet_balance_event(wallet)
    if result['success']:
        print("✅ Wallet balance event sent successfully!")
        print("Response:", result['data'])
    else:
        print("❌ Failed:", result['error'])
except Exception as error:
    print("❌ Error:", str(error))
```

### Push Refer Friend Event
```python
#!/usr/bin/env python3
"""
Send Refer Friend Event Example
"""
import os
from dotenv import load_dotenv
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK
from models.ReferFriendEvent import ReferFriendEvent

load_dotenv()

sdk = OptikpiDataPipelineSDK({
    'authToken': os.getenv('AUTH_TOKEN'),
    'accountId': os.getenv('ACCOUNT_ID'),
    'workspaceId': os.getenv('WORKSPACE_ID'),
    'baseURL': os.getenv('API_BASE_URL')
})

referral = ReferFriendEvent(
    account_id=os.getenv('ACCOUNT_ID'),
    workspace_id=os.getenv('WORKSPACE_ID'),
    user_id="user123",
    event_category="Refer Friend",
    event_name="Referral Successful",
    event_id="evt_rf_1234",
    event_time="2024-01-15T10:45:00Z",
    referral_code_used="REFER50",
    referee_user_id="user789",
    successful_referral_confirmation=True,
    reward_type="bonus",
    reward_amount=100.00,
    reward_claimed_status="pending"
)

validation = referral.validate()
if not validation["isValid"]:
    print("❌ Validation errors:", validation["errors"])
    sys.exit(1)

print("✅ Referral event validated successfully!")

try:
    result = sdk.send_refer_friend_event(referral)
    if result['success']:
        print("✅ Refer friend event sent successfully!")
        print("Response:", result['data'])
    else:
        print("❌ Failed:", result['error'])
except Exception as error:
    print("❌ Error:", str(error))
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