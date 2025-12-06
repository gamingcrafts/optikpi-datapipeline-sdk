# Optikpi Data Pipeline API Python SDK

This directory contains example applications demonstrating how to use the Optikpi Data Pipeline SDK for Python.

## Prerequisites

- Python 3.9 or higher
- Poetry 1.6 or higher
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
  cd ../python/examples

### Test Customer Profile Endpoint

```bash
poetry run python test-customer-endpoint.py
```

### Test Customer Extended Attribute Endpoint

```bash
poetry run python test-customerext-endpoint.py
```

### Test All Endpoints

```bash
poetry run python test-all-endpoints.py
```

### Test Account Events

```bash
poetry run python test-account-endpoint.py
```

### Test Deposit Events

```bash
poetry run python test-deposit-endpoint.py
```

### Test Withdrawal Events

```bash
poetry run python test-withdraw-endpoint.py
```

### Test Gaming Activity Events

```bash
poetry run python test-gaming-endpoint.py
```

### Test ReferFriend Events

```bash
poetry run python test-refer-friend-endpoint.py
```

### Test WalletBalance Events

```bash
poetry run python test-wallet-balance-endpoint.py
```
### Test BatchData Operations

```bash
poetry run python test-batch-operations.py
```

## Example Code

### Basic Usage

```python
import os
import sys
import json
import time
from pathlib import Path
from dotenv import load_dotenv

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK
from models.CustomerProfile import CustomerProfile


# Initialize SDK
sdk = OptikpiDataPipelineSDK({
    'authToken': AUTH_TOKEN,
    'accountId': ACCOUNT_ID,
    'workspaceId': WORKSPACE_ID,
    'baseURL': API_BASE_URL
})

# Customer data
customer = CustomerProfile(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="user123456",
    username="john_doe",
    full_name="John Doe",
    first_name="John",
    last_name="Doe",
    date_of_birth="1990-01-15",
    email="john.doe@example.com",
    phone_number="+1234567890",
    gender="Male",
    country="United States",
    city="New York",
    language="en",
    currency="USD",
    marketing_email_preference="Opt-in",
    notifications_preference="Opt-in",
    subscription="Subscribed",
    privacy_settings="private",
    deposit_limits=1000.00,
    loss_limits=500.00,
    wagering_limits=2000.00,
    session_time_limits=120,
    cooling_off_period=7,
    self_exclusion_period=30,
    reality_checks_notification="daily",
    account_status="Active",
    vip_status="Regular",
    loyalty_program_tiers="Bronze",
    bonus_abuser="Not flagged",
    financial_risk_level=0.3,
    acquisition_source="Google Ads",
    partner_id="partner123",
    affliate_id="affiliate456",
    referral_link_code="REF789",
    referral_limit_reached="Not Reached",
    creation_timestamp="2024-01-15T10:30:00Z",
    phone_verification="Verified",
    email_verification="Verified",
    bank_verification="NotVerified",
    iddoc_verification="Verified"
)
validation = customer.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Customer event validated successfully!")

customer_dict = customer.to_dict()
result = sdk.send_customer_profile(customer_dict)

    if result['success']:
        print('\n✅ Success!')
        print('============================')
        print(f"HTTP Status: {result['status']}")
        print(f"Response Time: {response_time}ms")
        print(f"SDK Success: {result['success']}")
        print(f"Response Data: {json.dumps(result['data'], indent=2)}")
    else:
        print('\n❌ API Error!')
        print('============================')
        print(f"HTTP Status: {result['status']}")
        print(f"Response Time: {response_time}ms")
        print(f"SDK Success: {result['success']}")
        print(f"Error Data: {json.dumps(result.get('data', result.get('error')), indent=2)}")
```

### Batch Operations

```python
batch_data = {
    "customers": [customer1, customer2],
    "depositEvents": [deposit1, deposit2],
    "gamingEvents": [gaming1, gaming2],
    "walletBalanceEvents": [wallet_balance1, wallet_balance2],
    "referFriendEvents": [refer_friend1, refer_friend2],
    "extendedAttributes": [ext_attr1, ext_attr2]
}
batchResult = sdk.send_batch(batch_data)
```

## Troubleshooting

```bash
### Common Issues

1. Missing credentials: Ensure all required environment variables are set
2. Network issues: Check your internet connection and firewall settings
3. Invalid data: Use the validation methods to check your data before sending
4. Authentication errors: Verify your auth token, account ID, and workspace ID
```
