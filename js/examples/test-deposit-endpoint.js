require('dotenv').config();
const OptikpiDataPipelineSDK = require('@optikpi/datapipeline-sdk').default;

// Configuration - Read from environment variables
const API_BASE_URL = process.env.API_BASE_URL || "https://demo.optikpi.com/apigw/ingest";
const AUTH_TOKEN = process.env.AUTH_TOKEN;
const ACCOUNT_ID = process.env.ACCOUNT_ID;
const WORKSPACE_ID = process.env.WORKSPACE_ID;

// Validate required environment variables
if (!AUTH_TOKEN || !ACCOUNT_ID || !WORKSPACE_ID) {
  console.error('❌ Error: Missing required environment variables!');
  console.error('   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID');
  console.error('   Copy env.example to .env and fill in your values');
  process.exit(1);
}

// Initialize SDK
const sdk = new OptikpiDataPipelineSDK({
  authToken: AUTH_TOKEN,
  accountId: ACCOUNT_ID,
  workspaceId: WORKSPACE_ID,
  baseURL: API_BASE_URL
});

// Deposit event data
const DEPOSIT_EVENT = {
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
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
};

// Test deposit endpoint
async function testDepositEndpoint() {
  try {
    console.log('🚀 Testing Deposit Events Endpoint');
    console.log('==================================');
    
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);
    
    console.log('\nMaking API request using SDK...');
    console.log('Deposit Event Data:', JSON.stringify(DEPOSIT_EVENT, null, 2));
    
    // Make the API call using SDK
    const startTime = Date.now();
    const result = await sdk.sendDepositEvent(DEPOSIT_EVENT);
    const endTime = Date.now();
    
    if (result.success) {
      console.log('\n✅ Success!');
      console.log('==================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Response Data:', JSON.stringify(result.data, null, 2));
    } else {
      console.log('\n❌ API Error!');
      console.log('==================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Error Data:', JSON.stringify(result.data, null, 2));
    }
    
  } catch (error) {
    console.error('\n❌ SDK Error occurred!');
    console.error('==================================');
    console.error('Error:', error.message);
    console.error('Stack:', error.stack);
  }
}

// Run the test
if (require.main === module) {
  testDepositEndpoint();
}

module.exports = {
  testDepositEndpoint,
  DEPOSIT_EVENT,
  sdk
}; 