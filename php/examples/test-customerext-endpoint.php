<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\CustomerExtEvent;

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

// Create Customer Extension Event - Format 1: Object (will be auto-converted to JSON string)
$customerExtObject = new CustomerExtEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'opti789',
    'list_name' => 'BINGO_PREFERENCES',
    'ext_data' => [
        'Email' => 'True',
        'SMS' => 'True',
        'PushNotifications' => 'False'
    ]
]);

// Create Customer Extension Event - Format 2: JSON string (legacy format)
$customerExtString = new CustomerExtEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'opti456',
    'list_name' => 'GAMING_PREFERENCES',
    'ext_data' => json_encode([
        'Email' => 'True',
        'SMS' => 'True',
        'PushNotifications' => 'True'
    ])
]);

// SDK handles HMAC authentication automatically

// Test customer extension endpoint with both formats
function testCustomerExtEndpoint($sdk, $customerExtObject, $customerExtString, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    try {
        echo "👤 Testing Customer Extension Events Endpoint - Both Formats\n";
        echo "============================================================\n";
        
        echo "Configuration:\n";
        echo "API Base URL: $API_BASE_URL\n";
        echo "Account ID: $ACCOUNT_ID\n";
        echo "Workspace ID: $WORKSPACE_ID\n";
        echo "Auth Token: " . substr($AUTH_TOKEN, 0, 8) . "...\n";
        
        // Validate Format 1
        echo "\n📋 Validating Format 1: Object Format\n";
        echo "=====================================\n";
        $validation1 = $customerExtObject->validate();
        if (!$validation1['isValid']) {
            echo "❌ Validation errors:\n";
            foreach ($validation1['errors'] as $error) {
                echo "  - $error\n";
            }
            return;
        }
        echo "✅ Format 1 validated successfully!\n";
        
        // Test Format 1: Object format
        echo "\n📋 Testing Format 1: Object Format (auto-converted to JSON string)\n";
        echo "================================================================\n";
        echo "Extended Attributes Data (Object): " . json_encode($customerExtObject->toArray(), JSON_PRETTY_PRINT) . "\n";
        
        $startTime1 = microtime(true) * 1000;
        // Use toAPIFormat() to ensure proper format for API
        $result1 = $sdk->sendExtendedAttributes($customerExtObject->toAPIFormat());
        $endTime1 = microtime(true) * 1000;
        
        if ($result1['success']) {
            echo "\n✅ Format 1 Success!\n";
            echo "HTTP Status: " . $result1['status'] . "\n";
            echo "Response Time: " . round($endTime1 - $startTime1) . "ms\n";
            echo "SDK Success: " . ($result1['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result1['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ Format 1 API Error!\n";
            echo "HTTP Status: " . ($result1['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime1 - $startTime1) . "ms\n";
            echo "SDK Success: " . ($result1['success'] ? 'true' : 'false') . "\n";
            echo "Error: " . ($result1['error'] ?? 'Unknown error') . "\n";
            echo "Error Data: " . json_encode($result1['data'], JSON_PRETTY_PRINT) . "\n";
        }
        
        // Wait a moment between tests
        usleep(1000000); // 1 second
        
        // Validate Format 2
        echo "\n📋 Validating Format 2: JSON String Format\n";
        echo "==========================================\n";
        $validation2 = $customerExtString->validate();
        if (!$validation2['isValid']) {
            echo "❌ Validation errors:\n";
            foreach ($validation2['errors'] as $error) {
                echo "  - $error\n";
            }
            return;
        }
        echo "✅ Format 2 validated successfully!\n";
        
        // Test Format 2: JSON string format
        echo "\n📋 Testing Format 2: JSON String Format (legacy)\n";
        echo "===============================================\n";
        echo "Extended Attributes Data (String): " . json_encode($customerExtString->toArray(), JSON_PRETTY_PRINT) . "\n";
        
        $startTime2 = microtime(true) * 1000;
        $result2 = $sdk->sendExtendedAttributes($customerExtString->toArray());
        $endTime2 = microtime(true) * 1000;
        
        if ($result2['success']) {
            echo "\n✅ Format 2 Success!\n";
            echo "HTTP Status: " . $result2['status'] . "\n";
            echo "Response Time: " . round($endTime2 - $startTime2) . "ms\n";
            echo "SDK Success: " . ($result2['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result2['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ Format 2 API Error!\n";
            echo "HTTP Status: " . ($result2['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime2 - $startTime2) . "ms\n";
            echo "SDK Success: " . ($result2['success'] ? 'true' : 'false') . "\n";
            echo "Error: " . ($result2['error'] ?? 'Unknown error') . "\n";
            echo "Error Data: " . json_encode($result2['data'], JSON_PRETTY_PRINT) . "\n";
        }
        
        // Summary
        echo "\n📊 Test Summary\n";
        echo "================\n";
        echo "Format 1 (Object): " . ($result1['success'] ? '✅ PASSED' : '❌ FAILED') . "\n";
        echo "Format 2 (String): " . ($result2['success'] ? '✅ PASSED' : '❌ FAILED') . "\n";
        
    } catch (Exception $error) {
        echo "\n❌ SDK Error occurred!\n";
        echo "============================================\n";
        echo "Error: " . $error->getMessage() . "\n";
        echo "Stack: " . $error->getTraceAsString() . "\n";
    }
}

// Run the test
if (php_sapi_name() === 'cli') {
    testCustomerExtEndpoint($sdk, $customerExtObject, $customerExtString, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);
}
