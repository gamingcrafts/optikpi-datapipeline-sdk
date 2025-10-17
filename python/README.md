# Optikpi Data Pipeline API Python SDK

[![PyPI version](https://badge.fury.io/py/optikpi-datapipeline-sdk.svg)](https://badge.fury.io/py/optikpi-datapipeline-sdk)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Python Version](https://img.shields.io/badge/python-%3E%3D3.8-brightgreen.svg)](https://www.python.org/)

A comprehensive Python SDK for integrating with the Optikpi Data Pipeline API. This SDK provides secure authentication, data validation, and easy-to-use methods for sending customer profiles and event data.

## 🚀 Features

- **🔐 Secure Authentication**: HMAC-based authentication with HKDF key derivation
- **📊 Data Models**: Comprehensive data models with built-in validation
- **🔄 Automatic Retries**: Configurable retry logic for failed requests
- **📝 Type Safety**: Full type hints with comprehensive docstrings
- **🌐 Cross-Platform**: Works on Linux, macOS, and Windows
- **⚡ High Performance**: Optimized for high-throughput data ingestion
- **🛡️ Error Handling**: Robust error handling with detailed error messages

## 📦 Installation

This SDK is included in the project and does not require separate installation. Simply use it directly from the project directory.

### Setup

```bash
# Navigate to the Python SDK directory
cd ~/optikpi_datapipeline-sdk-main/optikpi-datapipeline-sdk/python

# Install dependencies
poetry install

# Activate the virtual environment (optional)
poetry shell
```

### Using the SDK

```bash
# Run examples directly
poetry run python examples/test-customer-endpoint.py

# Or activate shell and run
poetry shell
python examples/test-customer-endpoint.py
```

## 🎯 Quick Start

### 1. Initialize the SDK

```python
import sys
from pathlib import Path

# Add SDK to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK

sdk = OptikpiDataPipelineSDK({
    'authToken': 'your-auth-token',
    'accountId': 'your-account-id',
    'workspaceId': 'your-workspace-id',
    'baseURL':'https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest'
})
```

### 2. Send Customer Profile

```python
customer_data = {
    'account_id': 'your-account-id',
    'workspace_id': 'your-workspace-id',
    'user_id': 'user123',
    'username': 'john_doe',
    'email': 'john.doe@example.com',
    'full_name': 'John Doe',
    'country': 'United States',
    'currency': 'USD',
    'subscription': 'Subscribed',
    'deposit_limits': 1000.00,
    'loss_limits': 500.00,
    'wagering_limits': 2000.00,
    'session_time_limits': 120,
    'cooling_off_period': 7,
    'self_exclusion_period': 30,
    'account_status': 'Active',
    'vip_status': 'Regular',
    'loyalty_program_tiers': 'Bronze',
    'financial_risk_level': 0.3,
    'referral_link_code': 'REF789',
    'creation_timestamp': '2024-01-15T10:30:00Z',
    'phone_verification': 'Verified',
    'email_verification': 'Verified',
    'bank_verification': 'NotVerified',
    'iddoc_verification': 'Verified'
}

try:
    result = sdk.send_customer_profile(customer_data)
    if result['success']:
        print('Customer profile sent successfully:', result['data'])
    else:
        print('Failed to send customer profile:', result['error'])
except Exception as error:
    print('Error sending customer profile:', str(error))
```

### 3. Send Event Data

```python
deposit_event = {
    'account_id': 'your-account-id',
    'workspace_id': 'your-workspace-id',
    'user_id': 'user123',
    'event_category': 'Deposit',
    'event_name': 'Successful Deposit',
    'event_id': 'evt_dep_123',
    'event_time': '2024-01-15T14:45:00Z',
    'amount': 100.00,
    'payment_method': 'bank',
    'transaction_id': 'txn_123'
}

result = sdk.send_deposit_event(deposit_event)
```

### 4. Batch Operations

```python
batch_data = {
    'customers': [customer1, customer2],
    'depositEvents': [deposit1, deposit2],
    'gamingEvents': [gaming1, gaming2]
}

batch_result = sdk.send_batch(batch_data)
```

## 📚 API Reference

### Constructor Options

| Option | Type | Required | Default | Description |
|--------|------|----------|---------|-------------|
| `authToken` | str | ✅ | - | Your authentication token |
| `accountId` | str | ✅ | - | Your account ID |
| `workspaceId` | str | ✅ | - | Your workspace ID |
| `baseURL` | str | ❌ | `https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest` | API base URL |
| `timeout` | int | ❌ | `30` | Request timeout in seconds |
| `retries` | int | ❌ | `3` | Number of retry attempts |
| `retryDelay` | int | ❌ | `1` | Delay between retries in seconds |

### Methods

#### `health_check()`
Performs a health check on the API.

```python
health = sdk.health_check()
print('API Status:', 'Healthy' if health['success'] else 'Unhealthy')
```

#### `send_customer_profile(data)`
Sends customer profile data to the API.

```python
result = sdk.send_customer_profile(customer_data)
```

#### `send_account_event(data)`
Sends account-related events.

```python
result = sdk.send_account_event(account_event)
```

#### `send_deposit_event(data)`
Sends deposit-related events.

```python
result = sdk.send_deposit_event(deposit_event)
```

#### `send_withdraw_event(data)`
Sends withdrawal-related events.

```python
result = sdk.send_withdraw_event(withdraw_event)
```

#### `send_gaming_activity_event(data)`
Sends gaming activity events.

```python
result = sdk.send_gaming_activity_event(gaming_event)
```

#### `send_extended_attributes(data)`
Sends extended attributes data.

```python
result = sdk.send_extended_attributes(extended_data)
```

#### `send_batch(batch_data)`
Sends multiple types of data in a single batch operation.

```python
result = sdk.send_batch({
    'customers': [customer1, customer2],
    'depositEvents': [deposit1, deposit2],
    'gamingEvents': [gaming1, gaming2]
})
```

## 🏗️ Data Models

The SDK includes comprehensive data models with built-in validation:

### CustomerProfile
```python
from models.CustomerProfile import CustomerProfile

customer = CustomerProfile({
    'account_id': 'your-account-id',
    'workspace_id': 'your-workspace-id',
    'user_id': 'user123',
    'email': 'user@example.com',
    # ... other fields
})

validation = customer.validate()
if not validation['is_valid']:
    print('Validation errors:', validation['errors'])
```

### Event Models
```python
from models.AccountEvent import AccountEvent
from models.DepositEvent import DepositEvent
from models.GamingActivityEvent import GamingActivityEvent

account_event = AccountEvent({
    'account_id': 'your-account-id',
    'workspace_id': 'your-workspace-id',
    'user_id': 'user123',
    'event_name': 'Player Registration',
    'event_id': 'evt_123',
    'event_time': '2024-01-15T10:30:00Z'
})
```

## 🔐 Authentication

The SDK automatically handles authentication using HMAC signatures with HKDF key derivation. All requests are automatically signed with the appropriate headers:

- `x-optikpi-token`: Your authentication token
- `x-optikpi-account-id`: Your account ID
- `x-optikpi-workspace-id`: Your workspace ID
- `x-hmac-signature`: HMAC signature of the request body
- `x-hmac-algorithm`: HMAC algorithm (default: sha256)

## 🛠️ Development

### Setting Up Development Environment

```bash
# Clone the repository
git clone https://github.com/optikpi/datapipeline-sdk-python.git
cd datapipeline-sdk-python

# Install dependencies
poetry install

# Activate virtual environment
poetry shell
```

### Running Tests

```bash
# Run all tests
poetry run pytest

# Run tests with coverage
poetry run pytest --cov=src --cov-report=html

# Run specific test file
poetry run pytest tests/test_client.py

# Run with verbose output
poetry run pytest -v
```

### Code Quality

```bash
# Format code with black
poetry run black src/

# Lint code with flake8
poetry run flake8 src/

# Type checking with mypy
poetry run mypy src/

# Run all quality checks
poetry run black src/ && poetry run flake8 src/ && poetry run mypy src/
```

## 📋 Examples

### Complete Integration Example

```python
import os
from dotenv import load_dotenv
from index import OptikpiDataPipelineSDK
from models.CustomerProfile import CustomerProfile
from models.DepositEvent import DepositEvent

class CustomerDataService:
    def __init__(self):
        load_dotenv()
        
        self.sdk = OptikpiDataPipelineSDK({
            'authToken': os.getenv('OPTIKPI_AUTH_TOKEN'),
            'accountId': os.getenv('OPTIKPI_ACCOUNT_ID'),
            'workspaceId': os.getenv('OPTIKPI_WORKSPACE_ID'),
            'baseURL': os.getenv('OPTIKPI_API_URL')
        })
    
    def register_customer(self, customer_data):
        """Register a new customer"""
        try:
            # Create and validate customer profile
            customer = CustomerProfile(customer_data)
            validation = customer.validate()
            
            if not validation['is_valid']:
                raise ValueError(f"Validation failed: {', '.join(validation['errors'])}")
            
            # Send customer profile
            result = self.sdk.send_customer_profile(customer.to_dict())
            
            if result['success']:
                print('Customer registered successfully')
                return result['data']
            else:
                raise Exception(f"Failed to register customer: {result['error']}")
        
        except Exception as error:
            print(f'Error registering customer: {str(error)}')
            raise
    
    def record_deposit(self, user_id, amount, payment_method, transaction_id):
        """Record a customer deposit"""
        try:
            config = self.sdk.get_config()
            
            deposit_event = DepositEvent({
                'account_id': config['accountId'],
                'workspace_id': config['workspaceId'],
                'user_id': user_id,
                'event_category': 'Deposit',
                'event_name': 'Successful Deposit',
                'event_id': f"evt_dep_{int(time.time())}",
                'event_time': datetime.now().isoformat() + 'Z',
                'amount': amount,
                'payment_method': payment_method,
                'transaction_id': transaction_id
            })
            
            result = self.sdk.send_deposit_event(deposit_event.to_dict())
            
            if result['success']:
                print('Deposit recorded successfully')
                return result['data']
            else:
                raise Exception(f"Failed to record deposit: {result['error']}")
        
        except Exception as error:
            print(f'Error recording deposit: {str(error)}')
            raise


# Usage
service = CustomerDataService()

# Register a new customer
service.register_customer({
    'user_id': 'user123',
    'email': 'user@example.com',
    'full_name': 'John Doe',
    'country': 'United States',
    'currency': 'USD',
    # ... other required fields
})

# Record a deposit
service.record_deposit('user123', 100.00, 'bank', 'txn_123')
```

### Environment Variables Setup

Create a `.env` file:

```bash
OPTIKPI_AUTH_TOKEN=your-auth-token-here
OPTIKPI_ACCOUNT_ID=your-account-id
OPTIKPI_WORKSPACE_ID=your-workspace-id
OPTIKPI_API_URL=https://your-api-gateway-url/apigw/ingest
```

### Error Handling Example

```python
from index import OptikpiDataPipelineSDK
import time

def send_with_retry(sdk, data, max_retries=3):
    """Send data with custom retry logic"""
    
    for attempt in range(1, max_retries + 1):
        try:
            print(f"Attempt {attempt} of {max_retries}")
            
            result = sdk.send_customer_profile(data)
            
            if result['success']:
                print(f"✅ Success: {result['data']}")
                return result
            elif result['status'] == 429:
                # Rate limited
                delay = 2 ** attempt  # Exponential backoff
                print(f"⚠️  Rate limited. Waiting {delay}s...")
                time.sleep(delay)
            else:
                print(f"❌ Error {result['status']}: {result['error']}")
                return result
        
        except Exception as error:
            print(f"❌ Exception: {str(error)}")
            if attempt < max_retries:
                delay = 2 ** attempt
                print(f"Waiting {delay}s before retry...")
                time.sleep(delay)
    
    return {'success': False, 'error': 'Max retries exceeded'}


# Usage
sdk = OptikpiDataPipelineSDK({
    'authToken': 'your-token',
    'accountId': 'your-account',
    'workspaceId': 'your-workspace'
})

result = send_with_retry(sdk, customer_data)
```

## 🐛 Troubleshooting

### Common Issues

**1. ImportError: No module named 'index'**
```bash
# Make sure you're in the correct directory
cd ~/optikpi_datapipeline-sdk-main/optikpi-datapipeline-sdk/python

# Run with poetry
poetry run python examples/test-customer-endpoint.py

# Or add the path in your script
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))
```

**2. "Invalid HMAC signature" Error**
- ✅ This has been fixed in version 1.0.0
- The Python SDK now correctly matches the JavaScript implementation
- Verify your AUTH_TOKEN is correct

**3. Connection Timeout**
```python
# Increase timeout in config
sdk = OptikpiDataPipelineSDK({
    'authToken': 'your-token',
    'accountId': 'your-account',
    'workspaceId': 'your-workspace',
    'timeout': 60  # Increase to 60 seconds
})
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Workflow

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests (`poetry run pytest`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: [https://docs.optikpi.com](https://docs.optikpi.com)
- **Issues**: [GitHub Issues](https://github.com/optikpi/datapipeline-sdk-python/issues)
- **Email**: support@optikpi.com
- **Python Package**: [PyPI](https://pypi.org/project/optikpi-datapipeline-sdk/)

## 📊 Performance

The SDK is optimized for high-throughput scenarios:

- **Throughput**: Up to 1000 requests/second
- **Batch Size**: Up to 500 records per request
- **Memory**: Low memory footprint with streaming support
- **CPU**: Minimal CPU usage with efficient HMAC implementation

## 🔗 Links

- [Optikpi Website](https://optikpi.com)
- [API Documentation](https://docs.optikpi.com/api)
- [Integration Guide](https://docs.optikpi.com/integration)
- [Python Examples](./examples/)
- [Change Log](CHANGELOG.md)

## 📈 Roadmap

- [ ] Async/await support with `asyncio`
- [ ] WebSocket streaming support
- [ ] Enhanced data validation with Pydantic
- [ ] CLI tool for testing
- [ ] Docker container examples
- [ ] More comprehensive examples

## ⭐ Star History

If you find this SDK useful, please consider giving it a star on GitHub!

[![Star History Chart](https://api.star-history.com/svg?repos=optikpi/datapipeline-sdk-python&type=Date)](https://star-history.com/#optikpi/datapipeline-sdk-python&Date)