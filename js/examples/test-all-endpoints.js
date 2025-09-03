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

// Test data for different endpoints
const TEST_DATA = {
  customer: {
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
  },
  account: {
    "account_id": ACCOUNT_ID,
    "workspace_id": WORKSPACE_ID,
    "user_id": "user123456",
    "event_category": "Account",
    "event_name": "Player Registration",
    "event_id": "evt_123456789",
    "event_time": "2024-01-15T10:30:00Z",
    "device": "desktop",
    "status": "verified",
    "affiliate_id": "aff_123",
    "partner_id": "partner_456",
    "campaign_code": "CAMPAIGN_001",
    "reason": "Registration completed successfully"
  },
  deposit: {
    "account_id": ACCOUNT_ID,
    "workspace_id": WORKSPACE_ID,
    "user_id": "user123456",
    "event_category": "Deposit",
    "event_name": "Successful Deposit",
    "event_id": "evt_dep_987654321",
    "event_time": "2024-01-15T14:45:00Z",
    "amount": 500.00,
    "payment_method": "bank",
    "transaction_id": "txn_123456789",
    "payment_provider_id": "provider123",
    "payment_provider_name": "Bank Transfer"
  },
  withdraw: {
    "account_id": ACCOUNT_ID,
    "workspace_id": WORKSPACE_ID,
    "user_id": "user123456",
    "event_category": "Withdraw",
    "event_name": "Successful Withdrawal",
    "event_id": "evt_with_123456789",
    "event_time": "2024-01-15T15:30:00Z",
    "amount": 300.00,
    "payment_method": "bank",
    "transaction_id": "txn_123456790"
  },
  gaming: {
    "account_id": ACCOUNT_ID,
    "workspace_id": WORKSPACE_ID,
    "user_id": "user123456",
    "event_category": "Gaming Activity",
    "event_name": "Play Casino Game",
    "event_id": "evt_gaming_123456789",
    "event_time": "2024-01-15T16:00:00Z",
    "wager_amount": 50.00,
    "win_amount": 75.00,
    "game_id": "game_123",
    "game_title": "Blackjack",
    "provider": "Provider A"
  }
};

// Make API request using SDK
async function makeApiRequest(endpoint, data, method) {
  console.log(`\n🌐 Making ${method} request to: ${endpoint}`);
  console.log(`📋 Data:`, JSON.stringify(data, null, 2));

  const startTime = Date.now();
  let result;
  
  try {
    switch (method) {
      case 'customer':
        result = await sdk.sendCustomerProfile(data);
        break;
      case 'account':
        result = await sdk.sendAccountEvent(data);
        break;
      case 'deposit':
        result = await sdk.sendDepositEvent(data);
        break;
      case 'withdraw':
        result = await sdk.sendWithdrawEvent(data);
        break;
      case 'gaming':
        result = await sdk.sendGamingActivityEvent(data);
        break;
      default:
        throw new Error(`Unknown method: ${method}`);
    }
    
    const endTime = Date.now();
    
    return {
      status: result.status || 200,
      data: result.data,
      responseTime: endTime - startTime,
      success: result.success
    };
  } catch (error) {
    const endTime = Date.now();
    return {
      status: error.response?.status || 500,
      data: error.response?.data || { error: error.message },
      responseTime: endTime - startTime,
      success: false,
      error: error
    };
  }
}

// Test all endpoints
async function testAllEndpoints() {
  console.log('🚀 Testing All API Endpoints with Header-Based Authentication');
  console.log('==========================================================');
  console.log(`API Base URL: ${API_BASE_URL}`);
  console.log(`Account ID: ${ACCOUNT_ID}`);
  console.log(`Workspace ID: ${WORKSPACE_ID}`);
  console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);

  const endpoints = [
    { name: 'Customer Profile', endpoint: '/customers', data: TEST_DATA.customer, method: 'customer' },
    { name: 'Account Event', endpoint: '/events/account', data: TEST_DATA.account, method: 'account' },
    { name: 'Deposit Event', endpoint: '/events/deposit', data: TEST_DATA.deposit, method: 'deposit' },
    { name: 'Withdrawal Event', endpoint: '/events/withdraw', data: TEST_DATA.withdraw, method: 'withdraw' },
    { name: 'Gaming Activity', endpoint: '/events/gaming-activity', data: TEST_DATA.gaming, method: 'gaming' }
  ];

  const results = [];

  for (const endpoint of endpoints) {
    try {
      console.log(`\n📡 Testing ${endpoint.name}...`);
      console.log('─'.repeat(50));
      
      const result = await makeApiRequest(endpoint.endpoint, endpoint.data, endpoint.method);
      
      console.log(`✅ ${endpoint.name} - SUCCESS`);
      console.log(`   Status: ${result.status}`);
      console.log(`   Response Time: ${result.responseTime}ms`);
      console.log(`   SDK Success: ${result.success}`);
      console.log(`   Response:`, JSON.stringify(result.data, null, 2));
      
      results.push({
        endpoint: endpoint.name,
        status: 'SUCCESS',
        httpStatus: result.status,
        responseTime: result.responseTime,
        data: result.data
      });

    } catch (error) {
      console.log(`❌ ${endpoint.name} - FAILED`);
      
      if (error.response) {
        console.log(`   HTTP Status: ${error.response.status}`);
        console.log(`   Error Response:`, JSON.stringify(error.response.data, null, 2));
        
        results.push({
          endpoint: endpoint.name,
          status: 'FAILED',
          httpStatus: error.response.status,
          error: error.response.data
        });
      } else {
        console.log(`   Error: ${error.message}`);
        
        results.push({
          endpoint: endpoint.name,
          status: 'FAILED',
          error: error.message
        });
      }
    }
  }

  // Summary
  console.log('\n📊 Test Summary');
  console.log('===============');
  
  const successful = results.filter(r => r.status === 'SUCCESS').length;
  const failed = results.filter(r => r.status === 'FAILED').length;
  
  console.log(`✅ Successful: ${successful}`);
  console.log(`❌ Failed: ${failed}`);
  console.log(`📈 Success Rate: ${((successful / results.length) * 100).toFixed(1)}%`);
  
  if (failed > 0) {
    console.log('\n❌ Failed Endpoints:');
    results.filter(r => r.status === 'FAILED').forEach(result => {
      console.log(`   - ${result.endpoint}: ${result.error?.message || result.error}`);
    });
  }

  return results;
}

// Health check using SDK
async function healthCheck() {
  try {
    console.log('\n🏥 Performing Health Check...');
    
    const startTime = Date.now();
    const result = await sdk.healthCheck();
    const endTime = Date.now();
    
    if (result.success) {
      console.log('✅ Health Check - SUCCESS');
      console.log(`   Status: ${result.status}`);
      console.log(`   Response Time: ${endTime - startTime}ms`);
      console.log(`   Response:`, JSON.stringify(result.data, null, 2));
    } else {
      console.log('❌ Health Check - FAILED');
      console.log(`   Error: ${result.error}`);
      console.log(`   Status: ${result.status}`);
    }
    
    return result;
  } catch (error) {
    console.log('❌ Health Check - FAILED');
    console.log(`   Error: ${error.message}`);
    throw error;
  }
}

// Run tests
async function runTests() {
  try {
    // Health check first
    await healthCheck();
    
    // Test all endpoints
    const results = await testAllEndpoints();
    
    console.log('\n🎉 All tests completed!');
    return results;
  } catch (error) {
    console.error('\n💥 Test execution failed:', error.message);
    throw error;
  }
}

// Run if this file is executed directly
if (require.main === module) {
  runTests().catch(console.error);
}

module.exports = {
  testAllEndpoints,
  healthCheck,
  makeApiRequest,
  TEST_DATA,
  sdk
}; 