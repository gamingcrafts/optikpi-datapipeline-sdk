require('dotenv').config();
const OptikpiDataPipelineSDK = require('@optikpi/datapipeline-sdk').default;

// Configuration - Read from environment variables
const API_BASE_URL = process.env.API_BASE_URL ;
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

// Extended attributes event data - Format 1: Object (will be auto-converted to JSON string)
const EXTATTRIBUTES_EVENT_OBJECT = {
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "KLT345345",
  "list_name": "BINGO_PREFERENCES",
  "ext_data": {
    "Email": "True",
    "SMS": "True",
    "PushNotifications": "False"
  }
};

// Extended attributes event data - Format 2: JSON string (legacy format)
const EXTATTRIBUTES_EVENT_STRING = {
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "KLT345346",
  "list_name": "GAMING_PREFERENCES",
  "ext_data": "{\"Email\":\"True\",\"SMS\":\"True\",\"PushNotifications\":\"True\"}"
};

// SDK handles HMAC authentication automatically

// Test customer extension endpoint with both formats
async function testCustomerExtEndpoint() {
  try {
    console.log('👤 Testing Customer Extension Events Endpoint - Both Formats');
    console.log('============================================================');
    
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);
    
    // Test Format 1: Object format
    console.log('\n📋 Testing Format 1: Object Format (auto-converted to JSON string)');
    console.log('================================================================');
    console.log('Extended Attributes Data (Object):', JSON.stringify(EXTATTRIBUTES_EVENT_OBJECT, null, 2));
    
    const startTime1 = Date.now();
    const result1 = await sdk.sendExtendedAttributes(EXTATTRIBUTES_EVENT_OBJECT);
    const endTime1 = Date.now();
    
    if (result1.success) {
      console.log('\n✅ Format 1 Success!');
      console.log(`HTTP Status: ${result1.status}`);
      console.log(`Response Time: ${endTime1 - startTime1}ms`);
      console.log(`SDK Success: ${result1.success}`);
      console.log('Response Data:', JSON.stringify(result1.data, null, 2));
    } else {
      console.log('\n❌ Format 1 API Error!');
      console.log(`HTTP Status: ${result1.status}`);
      console.log(`Response Time: ${endTime1 - startTime1}ms`);
      console.log(`SDK Success: ${result1.success}`);
      console.log('Error:', result1.error);
      console.log('Error Data:', JSON.stringify(result1.data, null, 2));
    }
    
    // Wait a moment between tests
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Test Format 2: JSON string format
    console.log('\n📋 Testing Format 2: JSON String Format (legacy)');
    console.log('===============================================');
    console.log('Extended Attributes Data (String):', JSON.stringify(EXTATTRIBUTES_EVENT_STRING, null, 2));
    
    const startTime2 = Date.now();
    const result2 = await sdk.sendExtendedAttributes(EXTATTRIBUTES_EVENT_STRING);
    const endTime2 = Date.now();
    
    if (result2.success) {
      console.log('\n✅ Format 2 Success!');
      console.log(`HTTP Status: ${result2.status}`);
      console.log(`Response Time: ${endTime2 - startTime2}ms`);
      console.log(`SDK Success: ${result2.success}`);
      console.log('Response Data:', JSON.stringify(result2.data, null, 2));
    } else {
      console.log('\n❌ Format 2 API Error!');
      console.log(`HTTP Status: ${result2.status}`);
      console.log(`Response Time: ${endTime2 - startTime2}ms`);
      console.log(`SDK Success: ${result2.success}`);
      console.log('Error:', result2.error);
      console.log('Error Data:', JSON.stringify(result2.data, null, 2));
    }
    
    // Summary
    console.log('\n📊 Test Summary');
    console.log('================');
    console.log(`Format 1 (Object): ${result1.success ? '✅ PASSED' : '❌ FAILED'}`);
    console.log(`Format 2 (String): ${result2.success ? '✅ PASSED' : '❌ FAILED'}`);
    
  } catch (error) {
    console.error('\n❌ SDK Error occurred!');
    console.error('============================================');
    console.error('Error:', error.message);
    console.error('Stack:', error.stack);
  }
}

// Run the test
if (require.main === module) {
  testCustomerExtEndpoint();
}

module.exports = {
  testCustomerExtEndpoint,
  EXTATTRIBUTES_EVENT_OBJECT,
  EXTATTRIBUTES_EVENT_STRING,
  sdk
};
