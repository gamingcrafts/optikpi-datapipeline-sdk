require('dotenv').config();
const OptikpiDataPipelineSDK = require('@optikpi/datapipeline-sdk').default;
const { SystemEvent } = require('@optikpi/datapipeline-sdk')

/**
 * Example of how to send system events using the SDK
 */

// Configuration - Read from environment variables
const API_BASE_URL = process.env.API_BASE_URL;
const AUTH_TOKEN = process.env.AUTH_TOKEN;
const ACCOUNT_ID = process.env.ACCOUNT_ID;
const WORKSPACE_ID = process.env.WORKSPACE_ID;

// Validate required environment variables
if (!AUTH_TOKEN || !ACCOUNT_ID || !WORKSPACE_ID) {
  console.error('❌ Error: Missing required environment variables!');
  console.error('   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID');
  process.exit(1);
}

// Initialize SDK
const sdk = new OptikpiDataPipelineSDK({
  authToken: AUTH_TOKEN,
  accountId: ACCOUNT_ID,
  workspaceId: WORKSPACE_ID,
  baseURL: API_BASE_URL
});

// System event data - event_data as object
const SYSTEM_EVENT_OBJECT = new SystemEvent({
  account_id: ACCOUNT_ID,
  workspace_id: WORKSPACE_ID,
  event_category: 'SystemEvent',
  event_name: 'CampaignTrigger',
  event_id: `evt_sys_${Date.now()}`,
  event_time: new Date().toISOString(),
  event_data: {
    campaign_id: 'camp_001',
    action: 'start',
    segment: 'vip'
  }
});

// System event data - event_data as JSON string
const SYSTEM_EVENT_STRING = new SystemEvent({
  account_id: ACCOUNT_ID,
  workspace_id: WORKSPACE_ID,
  event_category: 'SystemEvent',
  event_name: 'ManualAction',
  event_id: `evt_sys_${Date.now() + 1}`,
  event_time: new Date().toISOString(),
  event_data: JSON.stringify({
    action: "notify",
    target: "user_list_1",
    payload: {}
  })
});

// Test system endpoint
async function testSystemEndpoint() {
  try {
    console.log('🚀 Testing System (Back Office) Endpoint');
    console.log('========================================');
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);

    // Test 1: event_data as object
    console.log('\n📋 Test 1: event_data as object');
    console.log('Payload:', JSON.stringify(SYSTEM_EVENT_OBJECT, null, 2));

    // Validate
    const val1 = SYSTEM_EVENT_OBJECT.validate();
    if (!val1.isValid) {
      console.error('❌ Test 1 Validation failed:', val1.errors);
      process.exit(1);
    }
    console.log('✅ System event validated successfully!');

    const startTime1 = Date.now();
    const result1 = await sdk.sendSystemEvent(SYSTEM_EVENT_OBJECT);
    const endTime1 = Date.now();

    if (result1.success) {
      console.log('\n✅ Test 1 Success!');
      console.log(`HTTP Status: ${result1.status}`);
      console.log(`Response Time: ${endTime1 - startTime1}ms`);
      console.log('Response:', JSON.stringify(result1.data, null, 2));
    } else {
      console.log('\n❌ Test 1 Failed!');
      console.log(`HTTP Status: ${result1.status || 'N/A'}`);
      console.log('Error:', result1.error);
      console.log('Data:', JSON.stringify(result1.data, null, 2));
    }

    // Wait 1.5s
    await new Promise(resolve => setTimeout(resolve, 1500));

    // Test 2: event_data as JSON string
    console.log('\n📋 Test 2: event_data as JSON string');
    console.log('Payload:', JSON.stringify(SYSTEM_EVENT_STRING, null, 2));

    // Validate
    const val2 = SYSTEM_EVENT_STRING.validate();
    if (!val2.isValid) {
      console.error('❌ Test 2 Validation failed:', val2.errors);
      process.exit(1);
    }
    console.log('✅ System event validated successfully!');

    const startTime2 = Date.now();
    const result2 = await sdk.sendSystemEvent(SYSTEM_EVENT_STRING);
    const endTime2 = Date.now();

    if (result2.success) {
      console.log('\n✅ Test 2 Success!');
      console.log(`HTTP Status: ${result2.status}`);
      console.log(`Response Time: ${endTime2 - startTime2}ms`);
      console.log('Response:', JSON.stringify(result2.data, null, 2));
    } else {
      console.log('\n❌ Test 2 Failed!');
      console.log(`HTTP Status: ${result2.status || 'N/A'}`);
      console.log('Error:', result2.error);
      console.log('Data:', JSON.stringify(result2.data, null, 2));
    }

    console.log('\n📊 Summary');
    console.log('==========');
    console.log(`Test 1 (object): ${result1.success ? '✅ PASSED' : '❌ FAILED'}`);
    console.log(`Test 2 (string):  ${result2.success ? '✅ PASSED' : '❌ FAILED'}`);
  } catch (error) {
    console.error('\n❌ Error occurred!');
    console.error('==================');
    console.error('Error:', error.message);
  }
}

if (require.main === module) {
  testSystemEndpoint();
}

module.exports = {
  testSystemEndpoint,
  SYSTEM_EVENT_OBJECT,
  SYSTEM_EVENT_STRING,
  sdk
};
