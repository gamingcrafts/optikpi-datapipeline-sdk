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

// Initialize SDK
$sdk = new OptikpiDataPipelineSDK([
    'baseURL' => $API_BASE_URL,
    'authToken' => $AUTH_TOKEN,
    'accountId' => $ACCOUNT_ID,
    'workspaceId' => $WORKSPACE_ID
]);

// Customer data
$customer = new CustomerProfile([
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

$startTime = microtime(true);
$result = $sdk->sendCustomerProfile($customer);
$endTime = microtime(true);

if ($result['success']) {
    echo "\n✅ Success!\n";
    echo "============================\n";
    echo "HTTP Status: " . $result['status'] . "\n";
    echo "Response Time: " . round(($endTime - $startTime) * 1000) . "ms\n";
    echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
    echo "Response Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
} else {
    echo "\n❌ API Error!\n";
    echo "============================\n";
    echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
    echo "Response Time: " . round(($endTime - $startTime) * 1000) . "ms\n";
    echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
    echo "Error: " . ($result['error'] ?? 'Unknown error') . "\n";
    echo "Error Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
}

