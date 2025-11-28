require('dotenv').config();
const OptikpiDataPipelineSDK = require('../src/index');
const { WalletBalanceEvent } = require('../src/models');

// Configuration - Read from environment variables
const API_BASE_URL = process.env.API_BASE_URL;
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

// Wallet Balance event data
const walletBalance =new WalletBalanceEvent({
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "user123456",
  "event_category": "Wallet Balance",
  "event_name": "Balance Update",
  "event_id": "evt_wb_987654321",
  "event_time": "2024-01-15T14:45:00Z",
  "wallet_type": "main",
  "currency": "USD",
  "current_cash_balance": 1250.50,
  "current_bonus_balance": 100.00,
  "current_total_balance": 1350.50,
  "blocked_amount": 50.00
});

// Test wallet balance endpoint
async function testWalletBalanceEndpoint() {
  try {
    console.log('🚀 Testing Wallet Balance Events Endpoint');
    console.log('==========================================');
    
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);
    
    console.log('\nMaking API request using SDK...');
    console.log('Wallet Balance Event Data:', JSON.stringify(walletBalance, null, 2));
    
    // Make the API call using SDK
    const startTime = Date.now();
    const result = await sdk.sendWalletBalanceEvent(walletBalance);
    const endTime = Date.now();
    
    if (result.success) {
      console.log('\n✅ Success!');
      console.log('==========================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`Success: ${result.success}`);
      console.log('Response Data:', JSON.stringify(result.data, null, 2));
    } else {
      console.log('\n❌ API Error!');
      console.log('==========================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`Success: ${result.success}`);
      console.log('Error Data:', JSON.stringify(result.data, null, 2));
    }
    
  } catch (error) {
    console.error('\n❌ Error occurred!');
    console.error('==========================================');
    if (error.response) {
      console.error(`HTTP Status: ${error.response.status}`);
      console.error('Error Response:', JSON.stringify(error.response.data, null, 2));
    } else {
      console.error('Error:', error.message);
      console.error('Stack:', error.stack);
    }
  }
}

// Run the test
if (require.main === module) {
  testWalletBalanceEndpoint();
}

module.exports = {
  testWalletBalanceEndpoint,
  walletBalance,
  sdk
};

