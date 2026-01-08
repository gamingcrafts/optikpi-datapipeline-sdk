<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\CustomerProfile;

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
    'authToken' => $AUTH_TOKEN,
    'accountId' => $ACCOUNT_ID,
    'workspaceId' => $WORKSPACE_ID,
    'baseURL' => $API_BASE_URL
]);

// Customer data
$customer = new CustomerProfile([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'js_field04prod',
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
    'iddoc_verification' => 'Verified',
    'cooling_off_expiry_date' => '2024-12-31T23:59:59Z',
    'self_exclusion_expiry_date' => '2025-01-31T23:59:59Z',
    'risk_score_level' => 'low',
    'marketing_sms_preference' => 'Opt-in',
    'custom_data' => [
        'favorite_game' => 'slots',
        'newsletter_signup' => true
    ],
    'self_exclusion_by' => 'player',
    'self_exclusion_by_type' => 'voluntary',
    'self_exclusion_check_time' => '2024-01-15T10:30:00Z',
    'self_exclusion_created_time' => '2024-01-01T00:00:00Z',
    'closed_time' => null,
    'real_money_enabled' => 'true',
    'push_token' => 'push_token_abc123',
    'android_push_token' => 'android_push_token_xyz456',
    'ios_push_token' => 'ios_push_token_def789',
    'windows_push_token' => 'windows_push_token_ghi012',
    'mac_dmg_push_token' => 'mac_push_token_jkl345'
]);

$validation = $customer->validate();
if (!$validation['isValid']) {
    echo "❌ Validation errors:\n";
    foreach ($validation['errors'] as $error) {
        echo "  - $error\n";
    }
    exit(1);
}
echo "✅ Customer event validated successfully!\n";

// SDK handles HMAC authentication automatically

// Test customer endpoint
function testCustomerEndpoint($sdk, $customer, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    try {
        echo "🚀 Testing Customer Endpoint\n";
        echo "============================\n";

        echo "Configuration:\n";
        echo "API Base URL: $API_BASE_URL\n";
        echo "Account ID: $ACCOUNT_ID\n";
        echo "Workspace ID: $WORKSPACE_ID\n";
        echo "Auth Token: " . substr($AUTH_TOKEN, 0, 8) . "...\n";

        echo "\nMaking API request using SDK...\n";
        echo "Customer Data: " . json_encode($customer->toArray(), JSON_PRETTY_PRINT) . "\n";

        // Make the API call using SDK
        $startTime = microtime(true) * 1000;
        $result = $sdk->sendCustomerProfile($customer);
        $endTime = microtime(true) * 1000;

        if ($result['success']) {
            echo "\n✅ Success!\n";
            echo "============================\n";
            echo "HTTP Status: " . $result['status'] . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ API Error!\n";
            echo "============================\n";
            echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Error Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        }

    } catch (Exception $error) {
        echo "\n❌ SDK Error occurred!\n";
        echo "============================\n";
        echo "Error: " . $error->getMessage() . "\n";
        echo "Stack: " . $error->getTraceAsString() . "\n";
    }
}

// Run the test
if (php_sapi_name() === 'cli') {
    testCustomerEndpoint($sdk, $customer, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);
}

