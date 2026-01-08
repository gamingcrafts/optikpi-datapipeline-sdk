<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;

// Load environment variables
$dotenv = parse_ini_file(__DIR__ . '/../.env');

$API_BASE_URL = $dotenv['API_BASE_URL'] ?? getenv('API_BASE_URL');
$AUTH_TOKEN = $dotenv['AUTH_TOKEN'] ?? getenv('AUTH_TOKEN');
$ACCOUNT_ID = $dotenv['ACCOUNT_ID'] ?? getenv('ACCOUNT_ID');
$WORKSPACE_ID = $dotenv['WORKSPACE_ID'] ?? getenv('WORKSPACE_ID');

// Validate required environment variables
if (empty($AUTH_TOKEN) || empty($ACCOUNT_ID) || empty($WORKSPACE_ID)) {
    echo "❌ Error: Missing required environment variables!\n";
    echo "   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID\n";
    echo "   Copy env.example to .env and fill in your values\n";
    exit(1);
}

// Initialize SDK
$sdk = new OptikpiDataPipelineSDK([
    'baseURL' => $API_BASE_URL,
    'authToken' => $AUTH_TOKEN,
    'accountId' => $ACCOUNT_ID,
    'workspaceId' => $WORKSPACE_ID
]);

// Make API request using SDK
function makeRequest($sdk, $endpoint, $data, $method = 'customer') {
    echo "\n🌐 Request: $endpoint\n";
    echo "📄 Body: " . json_encode($data, JSON_PRETTY_PRINT) . "\n";

    try {
        $result = null;
        switch ($method) {
            case 'customer':
                $result = $sdk->sendCustomerProfile($data);
                break;
            case 'account':
                $result = $sdk->sendAccountEvent($data);
                break;
            case 'deposit':
                $result = $sdk->sendDepositEvent($data);
                break;
            case 'withdraw':
                $result = $sdk->sendWithdrawEvent($data);
                break;
            case 'gaming':
                $result = $sdk->sendGamingActivityEvent($data);
                break;
            default:
                throw new Exception("Unknown method: $method");
        }
        
        return [
            'success' => $result['success'] ?? false,
            'status' => $result['status'] ?? null,
            'data' => $result['data'] ?? null,
            'error' => $result['error'] ?? null
        ];
    } catch (Exception $error) {
        return [
            'success' => false,
            'error' => $error->getMessage()
        ];
    }
}

// Test 1: Missing required fields
function testMissingRequiredFields($sdk) {
    echo "\n🔍 Test 1: Missing Required Fields\n";
    echo "==================================\n";
    
    $invalidCustomer = [
        'account_id' => '68911b7ad58ad825ec00c5ef',
        'workspace_id' => '68911b7ad58ad825ec00c5ef',
        // Missing: user_id, username, full_name, email, etc.
        'gender' => 'Male'
    ];

    $result = makeRequest($sdk, '/customers', $invalidCustomer);
    
    if ($result['success']) {
        echo "❌ UNEXPECTED: Request succeeded when it should have failed\n";
        echo "Response: " . json_encode($result['data']) . "\n";
    } else {
        echo "✅ EXPECTED: Request failed with validation errors\n";
        echo "Status: " . ($result['status'] ?? 'N/A') . "\n";
        echo "Error: " . ($result['error'] ?? 'Unknown error') . "\n";
    }
}

// Test 2: Invalid email format
function testInvalidEmail($sdk, $ACCOUNT_ID, $WORKSPACE_ID) {
    echo "\n🔍 Test 2: Invalid Email Format\n";
    echo "===============================\n";
    
    $invalidEmailCustomer = [
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123456',
        'username' => 'john_doe',
        'full_name' => 'John Doe',
        'first_name' => 'John',
        'last_name' => 'Doe',
        'date_of_birth' => '1990-01-15',
        'email' => 'invalid-email-format', // Invalid email
        'phone_number' => '+1234567890',
        'gender' => 'Male',
        'country' => 'United States',
        'city' => 'New York',
        'language' => 'en',
        'currency' => 'USD',
        'marketing_email_preference' => 'Opt-in',
        'notifications_preference' => 'Opt-in',
        'subscription' => 'Subscribed',
        'privacy_settings' => 'private',
        'deposit_limits' => 1000.00,
        'loss_limits' => 500.00,
        'wagering_limits' => 2000.00,
        'session_time_limits' => 120,
        'reality_checks_notification' => 'daily',
        'account_status' => 'Active',
        'vip_status' => 'Regular',
        'loyalty_program_tiers' => 'Bronze',
        'bonus_abuser' => 'Not flagged',
        'financial_risk_level' => 0.3,
        'acquisition_source' => 'Google Ads',
        'partner_id' => 'partner123',
        'referral_link_code' => 'REF789',
        'referral_limit_reached' => 'Not Reached',
        'creation_timestamp' => '2024-01-15T10:30:00Z',
        'phone_verification' => 'Verified',
        'email_verification' => 'Verified',
        'bank_verification' => 'NotVerified',
        'iddoc_verification' => 'Verified'
    ];

    $result = makeRequest($sdk, '/customers', $invalidEmailCustomer);
    
    if ($result['success']) {
        echo "❌ UNEXPECTED: Request succeeded with invalid email\n";
        echo "Response: " . json_encode($result['data']) . "\n";
    } else {
        echo "✅ EXPECTED: Request failed due to invalid email\n";
        echo "Status: " . ($result['status'] ?? 'N/A') . "\n";
        echo "Error: " . ($result['error'] ?? 'Unknown error') . "\n";
    }
}

// Test 3: Invalid enum values
function testInvalidEnumValues($sdk, $ACCOUNT_ID, $WORKSPACE_ID) {
    echo "\n🔍 Test 3: Invalid Enum Values\n";
    echo "==============================\n";
    
    $invalidEnumCustomer = [
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123456',
        'username' => 'john_doe',
        'full_name' => 'John Doe',
        'first_name' => 'John',
        'last_name' => 'Doe',
        'date_of_birth' => '1990-01-15',
        'email' => 'john.doe@example.com',
        'phone_number' => '+1234567890',
        'gender' => 'Other', // Invalid enum value
        'country' => 'United States',
        'city' => 'New York',
        'language' => 'en',
        'currency' => 'USD',
        'marketing_email_preference' => 'Opt-in',
        'notifications_preference' => 'Opt-in',
        'subscription' => 'Subscribed',
        'privacy_settings' => 'private',
        'deposit_limits' => 1000.00,
        'loss_limits' => 500.00,
        'wagering_limits' => 2000.00,
        'session_time_limits' => 120,
        'reality_checks_notification' => 'daily',
        'account_status' => 'Suspended', // Invalid enum value
        'vip_status' => 'Regular',
        'loyalty_program_tiers' => 'Bronze',
        'bonus_abuser' => 'Not flagged',
        'financial_risk_level' => 0.3,
        'acquisition_source' => 'Google Ads',
        'partner_id' => 'partner123',
        'referral_link_code' => 'REF789',
        'referral_limit_reached' => 'Not Reached',
        'creation_timestamp' => '2024-01-15T10:30:00Z',
        'phone_verification' => 'Verified',
        'email_verification' => 'Verified',
        'bank_verification' => 'NotVerified',
        'iddoc_verification' => 'Verified'
    ];

    $result = makeRequest($sdk, '/customers', $invalidEnumCustomer);
    
    if ($result['success']) {
        echo "❌ UNEXPECTED: Request succeeded with invalid enum values\n";
        echo "Response: " . json_encode($result['data']) . "\n";
    } else {
        echo "✅ EXPECTED: Request failed due to invalid enum values\n";
        echo "Status: " . ($result['status'] ?? 'N/A') . "\n";
        echo "Error: " . ($result['error'] ?? 'Unknown error') . "\n";
    }
}

// Test 4: Header mismatch
function testHeaderMismatch($sdk, $ACCOUNT_ID, $WORKSPACE_ID) {
    echo "\n🔍 Test 4: Header vs Body Mismatch\n";
    echo "==================================\n";
    
    $mismatchedCustomer = [
        'account_id' => 'different-account-id', // Different from header
        'workspace_id' => 'different-workspace-id', // Different from header
        'user_id' => 'user123456',
        'username' => 'john_doe',
        'full_name' => 'John Doe',
        'first_name' => 'John',
        'last_name' => 'Doe',
        'date_of_birth' => '1990-01-15',
        'email' => 'john.doe@example.com',
        'phone_number' => '+1234567890',
        'gender' => 'Male',
        'country' => 'United States',
        'city' => 'New York',
        'language' => 'en',
        'currency' => 'USD',
        'marketing_email_preference' => 'Opt-in',
        'notifications_preference' => 'Opt-in',
        'subscription' => 'Subscribed',
        'privacy_settings' => 'private',
        'deposit_limits' => 1000.00,
        'loss_limits' => 500.00,
        'wagering_limits' => 2000.00,
        'session_time_limits' => 120,
        'reality_checks_notification' => 'daily',
        'account_status' => 'Active',
        'vip_status' => 'Regular',
        'loyalty_program_tiers' => 'Bronze',
        'bonus_abuser' => 'Not flagged',
        'financial_risk_level' => 0.3,
        'acquisition_source' => 'Google Ads',
        'partner_id' => 'partner123',
        'referral_link_code' => 'REF789',
        'referral_limit_reached' => 'Not Reached',
        'creation_timestamp' => '2024-01-15T10:30:00Z',
        'phone_verification' => 'Verified',
        'email_verification' => 'Verified',
        'bank_verification' => 'NotVerified',
        'iddoc_verification' => 'Verified'
    ];

    $result = makeRequest($sdk, '/customers', $mismatchedCustomer);
    
    if ($result['success']) {
        echo "❌ UNEXPECTED: Request succeeded with header mismatch\n";
        echo "Response: " . json_encode($result['data']) . "\n";
    } else {
        echo "✅ EXPECTED: Request failed due to header mismatch\n";
        echo "Status: " . ($result['status'] ?? 'N/A') . "\n";
        echo "Error: " . ($result['error'] ?? 'Unknown error') . "\n";
    }
}

// Test 5: Missing authentication headers
function testMissingAuthHeaders($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID) {
    echo "\n🔍 Test 5: Missing Authentication Headers\n";
    echo "==========================================\n";
    
    $validCustomer = [
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123456',
        'username' => 'john_doe',
        'full_name' => 'John Doe',
        'first_name' => 'John',
        'last_name' => 'Doe',
        'date_of_birth' => '1990-01-15',
        'email' => 'john.doe@example.com',
        'phone_number' => '+1234567890',
        'gender' => 'Male',
        'country' => 'United States',
        'city' => 'New York',
        'language' => 'en',
        'currency' => 'USD',
        'marketing_email_preference' => 'Opt-in',
        'notifications_preference' => 'Opt-in',
        'subscription' => 'Subscribed',
        'privacy_settings' => 'private',
        'deposit_limits' => 1000.00,
        'loss_limits' => 500.00,
        'wagering_limits' => 2000.00,
        'session_time_limits' => 120,
        'reality_checks_notification' => 'daily',
        'account_status' => 'Active',
        'vip_status' => 'Regular',
        'loyalty_program_tiers' => 'Bronze',
        'bonus_abuser' => 'Not flagged',
        'financial_risk_level' => 0.3,
        'acquisition_source' => 'Google Ads',
        'partner_id' => 'partner123',
        'referral_link_code' => 'REF789',
        'referral_limit_reached' => 'Not Reached',
        'creation_timestamp' => '2024-01-15T10:30:00Z',
        'phone_verification' => 'Verified',
        'email_verification' => 'Verified',
        'bank_verification' => 'NotVerified',
        'iddoc_verification' => 'Verified'
    ];

    // Note: This test would require direct HTTP client access to test missing headers
    // SDK automatically adds headers, so this test is limited
    echo "⚠️  Note: SDK automatically adds authentication headers\n";
    echo "   This test would require direct HTTP client access\n";
}

// Test 6: Invalid deposit event
function testInvalidDepositEvent($sdk) {
    echo "\n🔍 Test 6: Invalid Deposit Event\n";
    echo "=================================\n";
    
    $invalidDeposit = [
        'account_id' => '68911b7ad58ad825ec00c5ef',
        'workspace_id' => '68911b7ad58ad825ec00c5ef',
        'user_id' => 'user123456',
        'event_category' => 'Deposit',
        'event_name' => 'Successful Deposit',
        'event_id' => 'evt_dep_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'amount' => -500.00, // Negative amount (invalid)
        'payment_method' => 'cryptocurrency', // Invalid payment method
        'transaction_id' => 'txn_123456789'
    ];

    $result = makeRequest($sdk, '/events/deposit', $invalidDeposit, 'deposit');
    
    if ($result['success']) {
        echo "❌ UNEXPECTED: Request succeeded with invalid deposit data\n";
        echo "Response: " . json_encode($result['data']) . "\n";
    } else {
        echo "✅ EXPECTED: Request failed due to invalid deposit data\n";
        echo "Status: " . ($result['status'] ?? 'N/A') . "\n";
        echo "Error: " . ($result['error'] ?? 'Unknown error') . "\n";
    }
}

// Run all tests
function runAllTests($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID) {
    echo "🚨 Testing API Validation Failures\n";
    echo "==================================\n";
    echo "API Base URL: $API_BASE_URL\n";
    echo "Account ID: $ACCOUNT_ID\n";
    echo "Workspace ID: $WORKSPACE_ID\n";

    testMissingRequiredFields($sdk);
    testInvalidEmail($sdk, $ACCOUNT_ID, $WORKSPACE_ID);
    testInvalidEnumValues($sdk, $ACCOUNT_ID, $WORKSPACE_ID);
    testHeaderMismatch($sdk, $ACCOUNT_ID, $WORKSPACE_ID);
    testMissingAuthHeaders($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID);
    testInvalidDepositEvent($sdk);

    echo "\n🎉 All validation failure tests completed!\n";
}

// Run if this file is executed directly
if (php_sapi_name() === 'cli') {
    runAllTests($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID);
}
