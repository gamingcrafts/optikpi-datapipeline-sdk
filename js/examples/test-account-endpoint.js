require('dotenv').config();
const OptikpiDataPipelineSDK = require('@optikpi/datapipeline-sdk').default;
const { AccountEvent } = require('@optikpi/datapipeline-sdk')

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

// Account event data
const account = new AccountEvent({
  account_id: ACCOUNT_ID,
  workspace_id: WORKSPACE_ID,
  user_id: "user123456",
  event_category: "Account",
  event_name: "Player Registration",
  event_id: "evt_123456789",
  event_time: "2024-01-15T10:30:00Z",
  device: "desktop",
  status: "verified",
  affiliate_id: "aff_123",
  partner_id: "partner_456",
  campaign_code: "CAMPAIGN_001",
  reason: "Registration completed successfully"
});

// Validate the account event
const validation = account.validate();
if (!validation.isValid) {
  console.error('❌ Validation errors:', validation.errors);
  process.exit(1);
}
console.log('✅ Account event validated successfully!');

// Test account endpoint
async function testAccountEndpoint() {
  try {
    console.log('🚀 Testing Account Events Endpoint');
    console.log('==================================');
    
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);
    
    console.log('\nMaking API request using SDK...');
    console.log('Account Event Data:', JSON.stringify(account, null, 2));
    
    // Make the API call using SDK
    const startTime = Date.now();
    const result = await sdk.sendAccountEvent(account);
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
  testAccountEndpoint();
}

module.exports = {
  testAccountEndpoint,
  account,
  sdk
}; 