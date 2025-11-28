require('dotenv').config();
const OptikpiDataPipelineSDK = require('../src/index');
const { ReferFriendEvent } = require('../src/models');

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

// Refer Friend event data
const referFriend =new ReferFriendEvent({
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "user123456",
  "event_category": "Refer Friend",
  "event_name": "Referral Successful",
  "event_id": "evt_rf_987654321",
  "event_time": "2024-01-15T14:45:00Z",
  "referral_code_used": "REF123456",
  "successful_referral_confirmation": true,
  "reward_type": "bonus",
  "reward_claimed_status": "claimed",
  "referee_user_id": "user789012",
  "referee_registration_date": "2024-01-15T10:30:00Z",
  "referee_first_deposit": 100.00
});

// Validate the referFriend event
const validation = referFriend.validate();
if (!validation.isValid) {
  console.error('❌ Validation errors:', validation.errors);
  process.exit(1);
}

console.log('✅ referFriend event validated successfully!');


// Test refer friend endpoint
async function testReferFriendEndpoint() {
  try {
    console.log('🚀 Testing Refer Friend Events Endpoint');
    console.log('========================================');
    
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);
    
    console.log('\nMaking API request using SDK...');
    console.log('Refer Friend Event Data:', JSON.stringify(referFriend, null, 2));
    
    // Make the API call using SDK
    const startTime = Date.now();
    const result = await sdk.sendReferFriendEvent(referFriend);
    const endTime = Date.now();
    
    if (result.success) {
      console.log('\n✅ Success!');
      console.log('========================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`Success: ${result.success}`);
      console.log('Response Data:', JSON.stringify(result.data, null, 2));
    } else {
      console.log('\n❌ API Error!');
      console.log('========================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`Success: ${result.success}`);
      console.log('Error Data:', JSON.stringify(result.data, null, 2));
    }
    
  } catch (error) {
    console.error('\n❌ Error occurred!');
    console.error('========================================');
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
  testReferFriendEndpoint();
}

module.exports = {
  testReferFriendEndpoint,
  referFriend,
  sdk
};

