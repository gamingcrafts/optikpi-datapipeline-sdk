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

// Valid base data for comparison
const VALID_CUSTOMER = {
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

// Test cases for validation failures
const VALIDATION_TEST_CASES = {
  // Customer validation failures
  customer: {
    "missing_required_fields": {
      name: "Missing Required Fields",
      description: "Customer data missing essential required fields",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        // Missing: user_id, username, full_name, email, etc.
        "gender": "Male"
      }
    },
    "invalid_email_format": {
      name: "Invalid Email Format",
      description: "Customer data with invalid email format",
      data: {
        ...VALID_CUSTOMER,
        "email": "invalid-email-format"
      }
    },
    "invalid_gender_enum": {
      name: "Invalid Gender Enum",
      description: "Customer data with invalid gender value",
      data: {
        ...VALID_CUSTOMER,
        "gender": "Other" // Should be "Male", "Female", or null
      }
    },
    "invalid_date_format": {
      name: "Invalid Date Format",
      description: "Customer data with invalid date format",
      data: {
        ...VALID_CUSTOMER,
        "date_of_birth": "15-01-1990" // Should be YYYY-MM-DD
      }
    },
    "invalid_phone_format": {
      name: "Invalid Phone Format",
      description: "Customer data with invalid phone number format",
      data: {
        ...VALID_CUSTOMER,
        "phone_number": "1234567890" // Missing + prefix
      }
    },
    "invalid_account_status": {
      name: "Invalid Account Status",
      description: "Customer data with invalid account status",
      data: {
        ...VALID_CUSTOMER,
        "account_status": "Suspended" // Should be "Active", "Inactive", "Blocked"
      }
    },
    "invalid_verification_status": {
      name: "Invalid Verification Status",
      description: "Customer data with invalid verification status",
      data: {
        ...VALID_CUSTOMER,
        "email_verification": "Pending" // Should be "Verified", "NotVerified"
      }
    },
    "invalid_financial_risk_level": {
      name: "Invalid Financial Risk Level",
      description: "Customer data with invalid financial risk level",
      data: {
        ...VALID_CUSTOMER,
        "financial_risk_level": 1.5 // Should be between 0 and 1
      }
    },
    "invalid_limits_type": {
      name: "Invalid Limits Type",
      description: "Customer data with invalid deposit limits type",
      data: {
        ...VALID_CUSTOMER,
        "deposit_limits": "1000" // Should be number, not string
      }
    },
    "header_account_id_mismatch": {
      name: "Header Account ID Mismatch",
      description: "Account ID in header doesn't match body",
      data: {
        ...VALID_CUSTOMER,
        "account_id": "different-account-id"
      }
    },
    "header_workspace_id_mismatch": {
      name: "Header Workspace ID Mismatch",
      description: "Workspace ID in header doesn't match body",
      data: {
        ...VALID_CUSTOMER,
        "workspace_id": "different-workspace-id"
      }
    }
  },

  // Account event validation failures
  account: {
    "missing_required_fields": {
      name: "Missing Required Fields",
      description: "Account event missing essential required fields",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        // Missing: user_id, event_category, event_name, event_id, event_time
        "device": "desktop"
      }
    },
    "invalid_event_name": {
      name: "Invalid Event Name",
      description: "Account event with invalid event name",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Account",
        "event_name": "Invalid Event", // Should be from enum
        "event_id": "evt_123456789",
        "event_time": "2024-01-15T10:30:00Z",
        "device": "desktop",
        "status": "verified"
      }
    },
    "invalid_device_type": {
      name: "Invalid Device Type",
      description: "Account event with invalid device type",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Account",
        "event_name": "Player Registration",
        "event_id": "evt_123456789",
        "event_time": "2024-01-15T10:30:00Z",
        "device": "smartwatch", // Should be "desktop", "mobile", "tablet"
        "status": "verified"
      }
    },
    "invalid_status": {
      name: "Invalid Status",
      description: "Account event with invalid status",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Account",
        "event_name": "Player Registration",
        "event_id": "evt_123456789",
        "event_time": "2024-01-15T10:30:00Z",
        "device": "desktop",
        "status": "pending" // Should be "verified", "unverified"
      }
    },
    "invalid_event_time_format": {
      name: "Invalid Event Time Format",
      description: "Account event with invalid event time format",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Account",
        "event_name": "Player Registration",
        "event_id": "evt_123456789",
        "event_time": "2024-01-15 10:30:00", // Should be ISO 8601
        "device": "desktop",
        "status": "verified"
      }
    }
  },

  // Deposit event validation failures
  deposit: {
    "missing_required_fields": {
      name: "Missing Required Fields",
      description: "Deposit event missing essential required fields",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        // Missing: user_id, event_category, event_name, event_id, event_time, amount
        "payment_method": "bank"
      }
    },
    "invalid_amount_type": {
      name: "Invalid Amount Type",
      description: "Deposit event with invalid amount type",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Deposit",
        "event_name": "Successful Deposit",
        "event_id": "evt_dep_987654321",
        "event_time": "2024-01-15T14:45:00Z",
        "amount": "500.00", // Should be number, not string
        "payment_method": "bank",
        "transaction_id": "txn_123456789"
      }
    },
    "negative_amount": {
      name: "Negative Amount",
      description: "Deposit event with negative amount",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Deposit",
        "event_name": "Successful Deposit",
        "event_id": "evt_dep_987654321",
        "event_time": "2024-01-15T14:45:00Z",
        "amount": -500.00, // Should be positive
        "payment_method": "bank",
        "transaction_id": "txn_123456789"
      }
    },
    "invalid_payment_method": {
      name: "Invalid Payment Method",
      description: "Deposit event with invalid payment method",
      data: {
        "account_id": "68911b7ad58ad825ec00c5ef",
        "workspace_id": "68911b7ad58ad825ec00c5ef",
        "user_id": "user123456",
        "event_category": "Deposit",
        "event_name": "Successful Deposit",
        "event_id": "evt_dep_987654321",
        "event_time": "2024-01-15T14:45:00Z",
        "amount": 500.00,
        "payment_method": "cryptocurrency", // Should be from enum
        "transaction_id": "txn_123456789"
      }
    }
  }
};

// HKDF implementation
function deriveKey(authToken, accountId, workspaceId, info, algorithm = 'sha256', length = 32) {
  const ikmBuffer = Buffer.from(authToken, 'utf8');
  const saltBuffer = Buffer.from(accountId + workspaceId, 'utf8');
  const infoBuffer = Buffer.from(info, 'utf8');

  const derivedKey = crypto.hkdfSync(
    algorithm,
    saltBuffer,
    ikmBuffer,
    infoBuffer,
    length
  );

  return derivedKey;
}

// Generate HMAC signature using HKDF
function generateHmacSignature(data, authToken, accountId, workspaceId, algorithm = 'sha256') {
  try {
    const info = 'hmac-signing';
    const derivedKey = deriveKey(authToken, accountId, workspaceId, info, algorithm);
    
    const hmac = crypto.createHmac(algorithm, derivedKey);
    hmac.update(JSON.stringify(data), 'utf8');
    const signature = hmac.digest('hex');
    
    return signature;
  } catch (error) {
    console.error('Error generating HMAC signature:', error);
    throw error;
  }
}

// Make API request with headers
async function makeApiRequest(endpoint, data, customHeaders = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  const hmacSignature = generateHmacSignature(data, AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID);
  
  const headers = {
    'Content-Type': 'application/json',
    'x-optikpi-token': AUTH_TOKEN,
    'x-optikpi-account-id': ACCOUNT_ID,
    'x-optikpi-workspace-id': WORKSPACE_ID,
    'x-hmac-signature': hmacSignature,
    'x-hmac-algorithm': 'sha256',
    ...customHeaders
  };

  console.log(`\n🌐 Making request to: ${url}`);
  console.log(`📋 Headers:`, JSON.stringify(headers, null, 2));
  console.log(`📄 Request Body:`, JSON.stringify(data, null, 2));

  const startTime = Date.now();
  const response = await axios.post(url, data, { headers });
  const endTime = Date.now();

  return {
    status: response.status,
    data: response.data,
    responseTime: endTime - startTime
  };
}

// Test validation failures
async function testValidationFailures() {
  console.log('🚨 Testing API Validation Failures');
  console.log('==================================');
  console.log(`API Base URL: ${API_BASE_URL}`);
  console.log(`Account ID: ${ACCOUNT_ID}`);
  console.log(`Workspace ID: ${WORKSPACE_ID}`);

  const results = [];

  // Test customer validation failures
  console.log('\n👤 Testing Customer Validation Failures');
  console.log('=======================================');
  
  for (const [testKey, testCase] of Object.entries(VALIDATION_TEST_CASES.customer)) {
    try {
      console.log(`\n🔍 Testing: ${testCase.name}`);
      console.log(`📝 Description: ${testCase.description}`);
      console.log('─'.repeat(60));
      
      const result = await makeApiRequest('/customers', testCase.data);
      
      // If we get here, the validation didn't fail as expected
      console.log(`⚠️  UNEXPECTED SUCCESS: ${testCase.name}`);
      console.log(`   Status: ${result.status}`);
      console.log(`   Response:`, JSON.stringify(result.data, null, 2));
      
      results.push({
        testKey,
        endpoint: 'customer',
        testName: testCase.name,
        expected: 'FAILURE',
        actual: 'SUCCESS',
        status: result.status,
        data: result.data
      });

    } catch (error) {
      if (error.response) {
        console.log(`✅ EXPECTED FAILURE: ${testCase.name}`);
        console.log(`   HTTP Status: ${error.response.status}`);
        console.log(`   Error Response:`, JSON.stringify(error.response.data, null, 2));
        
        results.push({
          testKey,
          endpoint: 'customer',
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'FAILURE',
          httpStatus: error.response.status,
          error: error.response.data
        });
      } else {
        console.log(`❌ UNEXPECTED ERROR: ${testCase.name}`);
        console.log(`   Error: ${error.message}`);
        
        results.push({
          testKey,
          endpoint: 'customer',
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'ERROR',
          error: error.message
        });
      }
    }
  }

  // Test account validation failures
  console.log('\n📊 Testing Account Event Validation Failures');
  console.log('============================================');
  
  for (const [testKey, testCase] of Object.entries(VALIDATION_TEST_CASES.account)) {
    try {
      console.log(`\n🔍 Testing: ${testCase.name}`);
      console.log(`📝 Description: ${testCase.description}`);
      console.log('─'.repeat(60));
      
      const result = await makeApiRequest('/events/account', testCase.data);
      
      console.log(`⚠️  UNEXPECTED SUCCESS: ${testCase.name}`);
      console.log(`   Status: ${result.status}`);
      console.log(`   Response:`, JSON.stringify(result.data, null, 2));
      
      results.push({
        testKey,
        endpoint: 'account',
        testName: testCase.name,
        expected: 'FAILURE',
        actual: 'SUCCESS',
        status: result.status,
        data: result.data
      });

    } catch (error) {
      if (error.response) {
        console.log(`✅ EXPECTED FAILURE: ${testCase.name}`);
        console.log(`   HTTP Status: ${error.response.status}`);
        console.log(`   Error Response:`, JSON.stringify(error.response.data, null, 2));
        
        results.push({
          testKey,
          endpoint: 'account',
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'FAILURE',
          httpStatus: error.response.status,
          error: error.response.data
        });
      } else {
        console.log(`❌ UNEXPECTED ERROR: ${testCase.name}`);
        console.log(`   Error: ${error.message}`);
        
        results.push({
          testKey,
          endpoint: 'account',
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'ERROR',
          error: error.message
        });
      }
    }
  }

  // Test deposit validation failures
  console.log('\n💰 Testing Deposit Event Validation Failures');
  console.log('============================================');
  
  for (const [testKey, testCase] of Object.entries(VALIDATION_TEST_CASES.deposit)) {
    try {
      console.log(`\n🔍 Testing: ${testCase.name}`);
      console.log(`📝 Description: ${testCase.description}`);
      console.log('─'.repeat(60));
      
      const result = await makeApiRequest('/events/deposit', testCase.data);
      
      console.log(`⚠️  UNEXPECTED SUCCESS: ${testCase.name}`);
      console.log(`   Status: ${result.status}`);
      console.log(`   Response:`, JSON.stringify(result.data, null, 2));
      
      results.push({
        testKey,
        endpoint: 'deposit',
        testName: testCase.name,
        expected: 'FAILURE',
        actual: 'SUCCESS',
        status: result.status,
        data: result.data
      });

    } catch (error) {
      if (error.response) {
        console.log(`✅ EXPECTED FAILURE: ${testCase.name}`);
        console.log(`   HTTP Status: ${error.response.status}`);
        console.log(`   Error Response:`, JSON.stringify(error.response.data, null, 2));
        
        results.push({
          testKey,
          endpoint: 'deposit',
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'FAILURE',
          httpStatus: error.response.status,
          error: error.response.data
        });
      } else {
        console.log(`❌ UNEXPECTED ERROR: ${testCase.name}`);
        console.log(`   Error: ${error.message}`);
        
        results.push({
          testKey,
          endpoint: 'deposit',
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'ERROR',
          error: error.message
        });
      }
    }
  }

  // Summary
  console.log('\n📊 Validation Test Summary');
  console.log('==========================');
  
  const expectedFailures = results.filter(r => r.expected === 'FAILURE' && r.actual === 'FAILURE').length;
  const unexpectedSuccesses = results.filter(r => r.expected === 'FAILURE' && r.actual === 'SUCCESS').length;
  const unexpectedErrors = results.filter(r => r.actual === 'ERROR').length;
  const totalTests = results.length;
  
  console.log(`✅ Expected Failures: ${expectedFailures}`);
  console.log(`⚠️  Unexpected Successes: ${unexpectedSuccesses}`);
  console.log(`❌ Unexpected Errors: ${unexpectedErrors}`);
  console.log(`📈 Total Tests: ${totalTests}`);
  console.log(`🎯 Validation Effectiveness: ${((expectedFailures / totalTests) * 100).toFixed(1)}%`);
  
  if (unexpectedSuccesses > 0) {
    console.log('\n⚠️  Tests that should have failed but succeeded:');
    results.filter(r => r.expected === 'FAILURE' && r.actual === 'SUCCESS').forEach(result => {
      console.log(`   - ${result.testName} (${result.endpoint})`);
    });
  }

  if (unexpectedErrors > 0) {
    console.log('\n❌ Tests with unexpected errors:');
    results.filter(r => r.actual === 'ERROR').forEach(result => {
      console.log(`   - ${result.testName} (${result.endpoint}): ${result.error}`);
    });
  }

  return results;
}

// Test authentication failures
async function testAuthenticationFailures() {
  console.log('\n🔐 Testing Authentication Failures');
  console.log('===================================');
  
  const authTestCases = [
    {
      name: "Missing Auth Token",
      description: "Request without x-optikpi-token header",
      headers: {
        'Content-Type': 'application/json',
        'x-optikpi-account-id': ACCOUNT_ID,
        'x-optikpi-workspace-id': WORKSPACE_ID,
        'x-hmac-signature': 'dummy-signature',
        'x-hmac-algorithm': 'sha256'
      },
      data: VALID_CUSTOMER
    },
    {
      name: "Invalid Auth Token",
      description: "Request with invalid auth token",
      headers: {
        'Content-Type': 'application/json',
        'x-optikpi-token': 'invalid-token',
        'x-optikpi-account-id': ACCOUNT_ID,
        'x-optikpi-workspace-id': WORKSPACE_ID,
        'x-hmac-signature': 'dummy-signature',
        'x-hmac-algorithm': 'sha256'
      },
      data: VALID_CUSTOMER
    },
    {
      name: "Missing Account ID Header",
      description: "Request without x-optikpi-account-id header",
      headers: {
        'Content-Type': 'application/json',
        'x-optikpi-token': AUTH_TOKEN,
        'x-optikpi-workspace-id': WORKSPACE_ID,
        'x-hmac-signature': 'dummy-signature',
        'x-hmac-algorithm': 'sha256'
      },
      data: VALID_CUSTOMER
    },
    {
      name: "Missing Workspace ID Header",
      description: "Request without x-optikpi-workspace-id header",
      headers: {
        'Content-Type': 'application/json',
        'x-optikpi-token': AUTH_TOKEN,
        'x-optikpi-account-id': ACCOUNT_ID,
        'x-hmac-signature': 'dummy-signature',
        'x-hmac-algorithm': 'sha256'
      },
      data: VALID_CUSTOMER
    },
    {
      name: "Invalid HMAC Signature",
      description: "Request with invalid HMAC signature",
      headers: {
        'Content-Type': 'application/json',
        'x-optikpi-token': AUTH_TOKEN,
        'x-optikpi-account-id': ACCOUNT_ID,
        'x-optikpi-workspace-id': WORKSPACE_ID,
        'x-hmac-signature': 'invalid-signature',
        'x-hmac-algorithm': 'sha256'
      },
      data: VALID_CUSTOMER
    }
  ];

  const results = [];

  for (const testCase of authTestCases) {
    try {
      console.log(`\n🔍 Testing: ${testCase.name}`);
      console.log(`📝 Description: ${testCase.description}`);
      console.log('─'.repeat(60));
      
      const url = `${API_BASE_URL}/customers`;
      console.log(`🌐 Making request to: ${url}`);
      console.log(`📋 Headers:`, JSON.stringify(testCase.headers, null, 2));

      const startTime = Date.now();
      const response = await axios.post(url, testCase.data, { headers: testCase.headers });
      const endTime = Date.now();

      console.log(`⚠️  UNEXPECTED SUCCESS: ${testCase.name}`);
      console.log(`   Status: ${response.status}`);
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
      
      results.push({
        testName: testCase.name,
        expected: 'FAILURE',
        actual: 'SUCCESS',
        status: response.status,
        data: response.data
      });

    } catch (error) {
      if (error.response) {
        console.log(`✅ EXPECTED FAILURE: ${testCase.name}`);
        console.log(`   HTTP Status: ${error.response.status}`);
        console.log(`   Error Response:`, JSON.stringify(error.response.data, null, 2));
        
        results.push({
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'FAILURE',
          httpStatus: error.response.status,
          error: error.response.data
        });
      } else {
        console.log(`❌ UNEXPECTED ERROR: ${testCase.name}`);
        console.log(`   Error: ${error.message}`);
        
        results.push({
          testName: testCase.name,
          expected: 'FAILURE',
          actual: 'ERROR',
          error: error.message
        });
      }
    }
  }

  return results;
}

// Run all failure tests
async function runFailureTests() {
  try {
    console.log('🚨 Starting Validation and Authentication Failure Tests');
    console.log('========================================================');
    
    // Test validation failures
    const validationResults = await testValidationFailures();
    
    // Test authentication failures
    const authResults = await testAuthenticationFailures();
    
    console.log('\n🎉 All failure tests completed!');
    return {
      validation: validationResults,
      authentication: authResults
    };
  } catch (error) {
    console.error('\n💥 Test execution failed:', error.message);
    throw error;
  }
}

// Run if this file is executed directly
if (require.main === module) {
  runFailureTests().catch(console.error);
}

module.exports = {
  testValidationFailures,
  testAuthenticationFailures,
  runFailureTests,
  VALIDATION_TEST_CASES,
  VALID_CUSTOMER
}; 