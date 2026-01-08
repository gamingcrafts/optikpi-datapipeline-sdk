<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\ReferFriendEvent;

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

// Refer Friend event data
$referFriend = new ReferFriendEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
    'event_category' => 'Refer Friend',
    'event_name' => 'Referral Successful',
    'event_id' => 'evt_rf_987654321',
    'event_time' => '2024-01-15T14:45:00Z',
    'referral_code_used' => 'REF123456',
    'successful_referral_confirmation' => true,
    'reward_type' => 'bonus',
    'reward_claimed_status' => 'claimed',
    'referee_user_id' => 'user789012',
    'referee_registration_date' => '2024-01-15T10:30:00Z',
    'referee_first_deposit' => 100.00
]);

// Validate the referFriend event
$validation = $referFriend->validate();
if (!$validation['isValid']) {
    echo "❌ Validation errors:\n";
    foreach ($validation['errors'] as $error) {
        echo "  - $error\n";
    }
    exit(1);
}

echo "✅ referFriend event validated successfully!\n";

// Test refer friend endpoint
function testReferFriendEndpoint($sdk, $referFriend, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    try {
        echo "🚀 Testing Refer Friend Events Endpoint\n";
        echo "========================================\n";
        
        echo "Configuration:\n";
        echo "API Base URL: $API_BASE_URL\n";
        echo "Account ID: $ACCOUNT_ID\n";
        echo "Workspace ID: $WORKSPACE_ID\n";
        echo "Auth Token: " . substr($AUTH_TOKEN, 0, 8) . "...\n";
        
        echo "\nMaking API request using SDK...\n";
        echo "Refer Friend Event Data: " . json_encode($referFriend->toArray(), JSON_PRETTY_PRINT) . "\n";
        
        // Make the API call using SDK
        $startTime = microtime(true) * 1000;
        $result = $sdk->sendReferFriendEvent($referFriend);
        $endTime = microtime(true) * 1000;
        
        if ($result['success']) {
            echo "\n✅ Success!\n";
            echo "========================================\n";
            echo "HTTP Status: " . $result['status'] . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ API Error!\n";
            echo "========================================\n";
            echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Error Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        }
        
    } catch (Exception $error) {
        echo "\n❌ Error occurred!\n";
        echo "========================================\n";
        echo "Error: " . $error->getMessage() . "\n";
        echo "Stack: " . $error->getTraceAsString() . "\n";
    }
}

// Run the test
if (php_sapi_name() === 'cli') {
    testReferFriendEndpoint($sdk, $referFriend, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);
}
