# Optikpi API Python Client Examples

This folder contains Python examples for integrating with the Optikpi API using the official Optikpi Data Pipeline SDK. The SDK handles all authentication, HMAC signature generation, and API communication automatically.

## Setup

1. **Install dependencies:**
   ```bash
   poetry install
   ```

2. **Configure your credentials:**
   
   **Option A: Environment Variables (Recommended)**
   ```bash
   # Copy the example file
   cp env.example .env
   
   # Edit .env with your actual values
   API_BASE_URL=https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest
   AUTH_TOKEN=your-auth-token-here
   ACCOUNT_ID=your-account-id
   WORKSPACE_ID=your-workspace-id
   ```
   
   **Option B: Direct Configuration**
   Edit the configuration variables in the test files:
   ```python
   API_BASE_URL = "https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
   AUTH_TOKEN = "your-auth-token-here"
   ACCOUNT_ID = "your-account-id"
   WORKSPACE_ID = "your-workspace-id"
   ```
   
   > **💡 Tip**: Using environment variables is more secure and follows best practices for production deployments.

## Available Tests

### Customer Endpoint Test
```bash
poetry run python examples/test-customer-endpoint.py
```

### Account Events Test
```bash
poetry run python examples/test-account-endpoint.py
```

### All Endpoints Test
```bash
poetry run python examples/test-all-endpoints.py
```

### Validation Failure Tests
```bash
# Comprehensive validation failure tests
poetry run python examples/test-validation-failures.py

# Simple focused validation failure tests
poetry run python examples/test-simple-failures.py
```

### Debug HMAC Signature
```bash
poetry run python examples/debug-hmac.py
```

## Features

- ✅ **Official SDK Integration** - Uses the official Optikpi Data Pipeline SDK
- ✅ **Automatic Authentication** - SDK handles HMAC signature generation and HKDF key derivation
- ✅ **Error Handling** - Comprehensive error reporting with SDK response format
- ✅ **Response Logging** - Detailed request/response information
- ✅ **Modular Design** - Clean, simple examples for different endpoints
- ✅ **Validation Testing** - Comprehensive failure scenario testing
- ✅ **Easy Integration** - Simple SDK initialization and method calls

## SDK Usage

### SDK Initialization
```python
from index import OptikpiDataPipelineSDK

sdk = OptikpiDataPipelineSDK({
    'authToken': 'your-auth-token',
    'accountId': 'your-account-id',
    'workspaceId': 'your-workspace-id',
    'baseURL': 'https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest'
})
```

### Available Methods
- `sdk.send_customer_profile(data)` - Send customer profile data
- `sdk.send_account_event(data)` - Send account events
- `sdk.send_deposit_event(data)` - Send deposit events
- `sdk.send_withdraw_event(data)` - Send withdrawal events
- `sdk.send_gaming_activity_event(data)` - Send gaming activity events
- `sdk.health_check()` - Check API health status
- `sdk.send_batch(batch_data)` - Send multiple data types in batch

## Example Output

```
🚀 Testing Customer Endpoint
============================
Configuration:
API Base URL: https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest
Account ID: 688743ad724a144fc1e051d9
Workspace ID: 67bed03cc39204069f2f0366
Auth Token: 055da5b3...

Making API request using SDK...
Customer Data: {
  "account_id": "688743ad724a144fc1e051d9",
  "workspace_id": "67bed03cc39204069f2f0366",
  "user_id": "user123456",
  "email": "john.doe@example.com",
  ...
}

✅ Success!
============================
HTTP Status: 200
Response Time: 245ms
SDK Success: True
Response Data: {
  "message": "1 customer profile(s) processed successfully",
  "count": 1
}
```

## Security Notes

- The SDK automatically handles HMAC signature generation using HKDF for key derivation
- All requests are authenticated using the proper headers (`x-optikpi-token`, `x-optikpi-account-id`, `x-optikpi-workspace-id`, `x-hmac-signature`)
- The SDK implementation matches the server-side validation logic exactly
- No need to manually implement cryptographic functions - the SDK handles everything securely

## Troubleshooting

If you encounter errors:

### Common Issues

**1. "Invalid HMAC signature" error:**
- ✅ **Fixed!** The Python SDK now correctly matches the JavaScript implementation
- The issue was with JSON serialization and HKDF parameter ordering
- If you still see this error, ensure you're using the latest SDK version

**2. Import errors:**
```bash
# Make sure you're in the correct directory
cd ~/optikpi_datapipeline-sdk-main/optikpi-datapipeline-sdk/python

# Install dependencies
poetry install

# Run tests with poetry
poetry run python examples/test-customer-endpoint.py
```

**3. Environment variables not loaded:**
```bash
# Verify .env file exists
ls -la .env

# Check environment variables are set
poetry run python -c "import os; from dotenv import load_dotenv; load_dotenv(); print(os.getenv('AUTH_TOKEN'))"
```

**4. Connection errors:**
- Verify your credentials are correct in the SDK initialization
- Ensure the API endpoint is accessible
- Check your network connection and firewall settings

**5. API validation errors:**
- Review the SDK response for specific error details
- Check the test-validation-failures.py output to see which validations pass/fail
- The SDK handles all authentication automatically - no manual HMAC implementation needed

## Environment Variables

The examples can be configured using environment variables for better security and flexibility:

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `API_BASE_URL` | API Gateway URL | Yes | `https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest` |
| `AUTH_TOKEN` | Authentication token | Yes | Demo token (not for production) |
| `ACCOUNT_ID` | Your account identifier | Yes | Demo account ID |
| `WORKSPACE_ID` | Your workspace identifier | Yes | Demo workspace ID |

### Setting Environment Variables

**Linux/macOS:**
```bash
export API_BASE_URL="https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
export AUTH_TOKEN="your-auth-token"
export ACCOUNT_ID="your-account-id"
export WORKSPACE_ID="your-workspace-id"
```

**Windows:**
```cmd
set API_BASE_URL=https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest
set AUTH_TOKEN=your-auth-token
set ACCOUNT_ID=your-account-id
set WORKSPACE_ID=your-workspace-id
```

**Using .env file (Recommended):**
```bash
# Copy the example file
cp env.example .env

# Edit .env with your values
nano .env  # or use your preferred editor

# The SDK automatically loads .env files using python-dotenv
poetry run python examples/test-customer-endpoint.py
```

## Project Structure

```
python/
├── examples/               # Example scripts
│   ├── test-customer-endpoint.py
│   ├── test-account-endpoint.py
│   ├── test-all-endpoints.py
│   ├── test-simple-failures.py
│   ├── test-validation-failures.py
│   └── debug-hmac.py
├── src/python/            # SDK source code
│   ├── index.py           # Main SDK class
│   ├── core/              # Core functionality
│   │   └── DataPipelineClient.py
│   ├── models/            # Data models
│   │   ├── CustomerProfile.py
│   │   ├── AccountEvent.py
│   │   ├── DepositEvent.py
│   │   ├── WithdrawEvent.py
│   │   └── GamingActivityEvent.py
│   └── utils/             # Utility functions
│       └── crypto.py      # HMAC/HKDF implementation
├── .env                   # Environment variables (create this)
├── pyproject.toml         # Poetry configuration
└── README.md             # This file
```

## Dependencies

- `requests` - HTTP library for API calls
- `python-dotenv` - Environment variable management
- `cryptography` - Optional: for advanced cryptographic operations

Install all dependencies with:
```bash
poetry install
```

## Development

### Running Tests
```bash
# Run all validation tests
poetry run python examples/test-validation-failures.py

# Debug HMAC signature generation
poetry run python examples/debug-hmac.py

# Test specific endpoint
poetry run python examples/test-customer-endpoint.py
```

### Adding New Tests
1. Create a new test file in `examples/`
2. Import the SDK: `from index import OptikpiDataPipelineSDK`
3. Initialize with your credentials
4. Use SDK methods to interact with the API

### Code Style
The SDK follows Python best practices:
- PEP 8 style guidelines
- Type hints for better IDE support
- Comprehensive docstrings
- Error handling with try/except blocks

## API Validation Notes

Based on comprehensive testing, the API has the following validation behavior:

✅ **Working Validations:**
- Required field validation
- Email format validation
- Date format validation (YYYY-MM-DD)
- Data type validation (string vs number)

⚠️ **Known API Limitations:**
- Some enum values are not strictly validated
- Negative amounts are accepted (should be rejected)
- Some phone format variations are accepted
- ID mismatch causes 500 error instead of 400

These limitations are on the API server side, not in the Python SDK. The SDK correctly handles all requests and responses.

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review example scripts for proper usage
3. Run debug-hmac.py to verify signature generation
4. Check API documentation for data format requirements

## License

MIT