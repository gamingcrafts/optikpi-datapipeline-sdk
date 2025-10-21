# Optikpi API JavaScript Client Examples

This folder contains JavaScript examples for integrating with the Optikpi API using the official Optikpi Data Pipeline SDK. The SDK handles all authentication, HMAC signature generation, and API communication automatically.

## Setup

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Configure your credentials:**
   
   **Option A: Environment Variables (Recommended)**
   ```bash
   # Copy the example file
   cp env.example .env
   
   # Edit .env with your actual values
   API_BASE_URL=https://your-api-gateway-url/apigw/ingest
   AUTH_TOKEN=your-auth-token-here
   ACCOUNT_ID=your-account-id
   WORKSPACE_ID=your-workspace-id
   ```
   
   **Option B: Direct Configuration**
   Edit the configuration variables in the test files:
   ```javascript
   const API_BASE_URL = "https://demo.optikpi.com/apigw/ingest";
   const AUTH_TOKEN = "your-auth-token-here";
   const ACCOUNT_ID = "your-account-id";
   const WORKSPACE_ID = "your-workspace-id";
   ```
   
   > **💡 Tip**: Using environment variables is more secure and follows best practices for production deployments.

## Available Tests

### Customer Endpoint Test
```bash
npm test
# or
node test-customer-endpoint.js
```

### All Endpoints Test
```bash
npm run test:all        # Test all endpoints in sequence
```

### Individual Endpoint Tests
```bash
npm run test:account    # Account events
npm run test:deposit    # Deposit events  
npm run test:withdraw   # Withdrawal events
npm run test:gaming     # Gaming activity events
```

### Validation Failure Tests
```bash
npm run test:failures         # Comprehensive validation failure tests
npm run test:simple-failures  # Simple focused validation failure tests
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
```javascript
const OptikpiDataPipelineSDK = require('../src/index');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your-auth-token',
  accountId: 'your-account-id',
  workspaceId: 'your-workspace-id',
  baseURL: 'https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest'
});
```

### Available Methods
- `sdk.sendCustomerProfile(data)` - Send customer profile data
- `sdk.sendAccountEvent(data)` - Send account events
- `sdk.sendDepositEvent(data)` - Send deposit events
- `sdk.sendWithdrawEvent(data)` - Send withdrawal events
- `sdk.sendGamingActivityEvent(data)` - Send gaming activity events
- `sdk.healthCheck()` - Check API health status
- `sdk.sendBatch(batchData)` - Send multiple data types in batch

## Example Output

```
🚀 Testing Customer Endpoint
============================
Configuration:
API Base URL: "https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest"
Account ID: 68911b7ad58ad825ec00c5ef
Workspace ID: 68911b7ad58ad825ec00c5ef
Auth Token: 0f07901b...

Making API request using SDK...
Customer Data: {
  "account_id": "68911b7ad58ad825ec00c5ef",
  "workspace_id": "68911b7ad58ad825ec00c5ef",
  "user_id": "user123456",
  "email": "john.doe@example.com",
  ...
}

✅ Success!
============================
HTTP Status: 200
Response Time: 245ms
SDK Success: true
Response Data: {
  "message": "1 customer profile(s) sent to Firehose successfully",
  "recordIds": ["record-id-123"],
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
1. Verify your credentials are correct in the SDK initialization
2. Ensure the API endpoint is accessible
3. Check that the SDK is properly installed (`npm install`)
4. Review the SDK response for specific error details
5. The SDK handles all authentication automatically - no manual HMAC implementation needed

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

**Using .env file:**
```bash
# Copy the example file
cp env.example .env

# Edit .env with your values
# Then run with dotenv (install: npm install dotenv)
node -r dotenv/config test-customer-endpoint.js
```

## Dependencies
- `dotenv` - Optional: for loading .env files (install with `npm install dotenv`) 