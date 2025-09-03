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

// Customer data
const CUSTOMER_DATA = {
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "user123456",
  "username": "john_doe",
  "full_name": "John Doe",
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "1990-01-15",
  "email": "john.doe@example.com",
  "phone_number": "+1234567890",
  "gender": "Male",
  "country": "United States",
  "city": "New York",
  "language": "en",
  "currency": "USD",
  "marketing_email_preference": "Opt-in",
  "notifications_preference": "Opt-in",
  "subscription": "Subscribed",
  "privacy_settings": "private",
  "deposit_limits": 1000.00,
  "loss_limits": 500.00,
  "wagering_limits": 2000.00,
  "session_time_limits": 120,
  "cooling_off_period": 7,
  "self_exclusion_period": 30,
  "reality_checks_notification": "daily",
  "account_status": "Active",
  "vip_status": "Regular",
  "loyalty_program_tiers": "Bronze",
  "bonus_abuser": "Not flagged",
  "financial_risk_level": 0.3,
  "acquisition_source": "Google Ads",
  "partner_id": "partner123",
  "affliate_id": "affiliate456",
  "referral_link_code": "REF789",
  "referral_limit_reached": "Not Reached",
  "creation_timestamp": "2024-01-15T10:30:00Z",
  "phone_verification": "Verified",
  "email_verification": "Verified",
  "bank_verification": "NotVerified",
  "iddoc_verification": "Verified"
};

// SDK handles HMAC authentication automatically

// Test customer endpoint
async function testCustomerEndpoint() {
  try {
    console.log('🚀 Testing Customer Endpoint');
    console.log('============================');
    
    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);
    
    console.log('\nMaking API request using SDK...');
    console.log('Customer Data:', JSON.stringify(CUSTOMER_DATA, null, 2));
    
    // Make the API call using SDK
    const startTime = Date.now();
    const result = await sdk.sendCustomerProfile(CUSTOMER_DATA);
    const endTime = Date.now();
    
    if (result.success) {
      console.log('\n✅ Success!');
      console.log('============================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Response Data:', JSON.stringify(result.data, null, 2));
    } else {
      console.log('\n❌ API Error!');
      console.log('============================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Error Data:', JSON.stringify(result.data, null, 2));
    }
    
  } catch (error) {
    console.error('\n❌ SDK Error occurred!');
    console.error('============================');
    console.error('Error:', error.message);
    console.error('Stack:', error.stack);
  }
}

// Run the test
if (require.main === module) {
  testCustomerEndpoint();
}

module.exports = {
  testCustomerEndpoint,
  CUSTOMER_DATA,
  sdk
}; 