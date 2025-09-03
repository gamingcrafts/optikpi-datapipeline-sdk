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

// Make API request using SDK
async function makeRequest(endpoint, data, method = 'customer') {
  console.log(`\n🌐 Request: ${endpoint}`);
  console.log(`📄 Body:`, JSON.stringify(data, null, 2));

  try {
    let result;
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
    
    return { 
      success: result.success, 
      status: result.status, 
      data: result.data,
      error: result.error 
    };
  } catch (error) {
    return { 
      success: false, 
      error: error.message 
    };
  }
}

// Test 1: Missing required fields
async function testMissingRequiredFields() {
  console.log('\n🔍 Test 1: Missing Required Fields');
  console.log('==================================');
  
  const invalidCustomer = {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad58ad825ec00c5ef",
    // Missing: user_id, username, full_name, email, etc.
    "gender": "Male"
  };

  const result = await makeRequest('/customers', invalidCustomer);
  
  if (result.success) {
    console.log('❌ UNEXPECTED: Request succeeded when it should have failed');
    console.log('Response:', result.data);
  } else {
    console.log('✅ EXPECTED: Request failed with validation errors');
    console.log('Status:', result.status);
    console.log('Error:', result.error);
  }
}

// Test 2: Invalid email format
async function testInvalidEmail() {
  console.log('\n🔍 Test 2: Invalid Email Format');
  console.log('===============================');
  
  const invalidEmailCustomer = {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad58ad825ec00c5ef",
    "user_id": "user123456",
    "username": "john_doe",
    "full_name": "John Doe",
    "first_name": "John",
    "last_name": "Doe",
    "date_of_birth": "1990-01-15",
    "email": "invalid-email-format", // Invalid email
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

  const result = await makeRequest('/customers', invalidEmailCustomer);
  
  if (result.success) {
    console.log('❌ UNEXPECTED: Request succeeded with invalid email');
    console.log('Response:', result.data);
  } else {
    console.log('✅ EXPECTED: Request failed due to invalid email');
    console.log('Status:', result.status);
    console.log('Error:', result.error);
  }
}

// Test 3: Invalid enum values
async function testInvalidEnumValues() {
  console.log('\n🔍 Test 3: Invalid Enum Values');
  console.log('==============================');
  
  const invalidEnumCustomer = {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad58ad825ec00c5ef",
    "user_id": "user123456",
    "username": "john_doe",
    "full_name": "John Doe",
    "first_name": "John",
    "last_name": "Doe",
    "date_of_birth": "1990-01-15",
    "email": "john.doe@example.com",
    "phone_number": "+1234567890",
    "gender": "Other", // Invalid enum value
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
    "account_status": "Suspended", // Invalid enum value
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

  const result = await makeRequest('/customers', invalidEnumCustomer);
  
  if (result.success) {
    console.log('❌ UNEXPECTED: Request succeeded with invalid enum values');
    console.log('Response:', result.data);
  } else {
    console.log('✅ EXPECTED: Request failed due to invalid enum values');
    console.log('Status:', result.status);
    console.log('Error:', result.error);
  }
}

// Test 4: Header mismatch
async function testHeaderMismatch() {
  console.log('\n🔍 Test 4: Header vs Body Mismatch');
  console.log('==================================');
  
  const mismatchedCustomer = {
    "account_id": "different-account-id", // Different from header
    "workspace_id": "different-workspace-id", // Different from header
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

  const result = await makeRequest('/customers', mismatchedCustomer);
  
  if (result.success) {
    console.log('❌ UNEXPECTED: Request succeeded with header mismatch');
    console.log('Response:', result.data);
  } else {
    console.log('✅ EXPECTED: Request failed due to header mismatch');
    console.log('Status:', result.status);
    console.log('Error:', result.error);
  }
}

// Test 5: Missing authentication headers
async function testMissingAuthHeaders() {
  console.log('\n🔍 Test 5: Missing Authentication Headers');
  console.log('==========================================');
  
  const validCustomer = {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad58ad825ec00c5ef",
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

  // Request without auth token
  const url = `${API_BASE_URL}/customers`;
  const headers = {
    'Content-Type': 'application/json',
    // Missing: x-optikpi-token, x-optikpi-account-id, x-optikpi-workspace-id
    'x-hmac-signature': 'dummy-signature',
    'x-hmac-algorithm': 'sha256'
  };

  console.log(`\n🌐 Request: ${url}`);
  console.log(`📋 Headers:`, JSON.stringify(headers, null, 2));

  try {
    const response = await axios.post(url, validCustomer, { headers });
    console.log('❌ UNEXPECTED: Request succeeded without auth token');
    console.log('Response:', response.data);
  } catch (error) {
    if (error.response) {
      console.log('✅ EXPECTED: Request failed due to missing auth token');
      console.log('Status:', error.response.status);
      console.log('Error:', error.response.data);
    } else {
      console.log('❌ UNEXPECTED ERROR:', error.message);
    }
  }
}

// Test 6: Invalid deposit event
async function testInvalidDepositEvent() {
  console.log('\n🔍 Test 6: Invalid Deposit Event');
  console.log('=================================');
  
  const invalidDeposit = {
    "account_id": "68911b7ad58ad825ec00c5ef",
    "workspace_id": "68911b7ad58ad825ec00c5ef",
    "user_id": "user123456",
    "event_category": "Deposit",
    "event_name": "Successful Deposit",
    "event_id": "evt_dep_987654321",
    "event_time": "2024-01-15T14:45:00Z",
    "amount": -500.00, // Negative amount (invalid)
    "payment_method": "cryptocurrency", // Invalid payment method
    "transaction_id": "txn_123456789"
  };

  const result = await makeRequest('/events/deposit', invalidDeposit);
  
  if (result.success) {
    console.log('❌ UNEXPECTED: Request succeeded with invalid deposit data');
    console.log('Response:', result.data);
  } else {
    console.log('✅ EXPECTED: Request failed due to invalid deposit data');
    console.log('Status:', result.status);
    console.log('Error:', result.error);
  }
}

// Run all tests
async function runAllTests() {
  console.log('🚨 Testing API Validation Failures');
  console.log('==================================');
  console.log(`API Base URL: ${API_BASE_URL}`);
  console.log(`Account ID: ${ACCOUNT_ID}`);
  console.log(`Workspace ID: ${WORKSPACE_ID}`);

  await testMissingRequiredFields();
  await testInvalidEmail();
  await testInvalidEnumValues();
  await testHeaderMismatch();
  await testMissingAuthHeaders();
  await testInvalidDepositEvent();

  console.log('\n🎉 All validation failure tests completed!');
}

// Run if this file is executed directly
if (require.main === module) {
  runAllTests().catch(console.error);
}

module.exports = {
  runAllTests,
  testMissingRequiredFields,
  testInvalidEmail,
  testInvalidEnumValues,
  testHeaderMismatch,
  testMissingAuthHeaders,
  testInvalidDepositEvent
}; 