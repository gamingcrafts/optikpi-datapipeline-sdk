<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\SystemEvent;

// Load environment variables
$dotenv = parse_ini_file(__DIR__ . '/.env');

$API_BASE_URL = $dotenv['API_BASE_URL'] ?? getenv('API_BASE_URL');
$AUTH_TOKEN = $dotenv['AUTH_TOKEN'] ?? getenv('AUTH_TOKEN');
$ACCOUNT_ID = $dotenv['ACCOUNT_ID'] ?? getenv('ACCOUNT_ID');
$WORKSPACE_ID = $dotenv['WORKSPACE_ID'] ?? getenv('WORKSPACE_ID');

// Validate required environment variables
if (empty($AUTH_TOKEN) || empty($ACCOUNT_ID) || empty($WORKSPACE_ID)) {
    echo "❌ Error: Missing required environment variables!\n";
    echo "   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID\n";
    echo "   fill your credentials in .env file\n";
    exit(1);
}

// Initialize SDK
$sdk = new OptikpiDataPipelineSDK([
    'authToken' => $AUTH_TOKEN,
    'accountId' => $ACCOUNT_ID,
    'workspaceId' => $WORKSPACE_ID,
    'baseURL' => $API_BASE_URL
]);

// Test 1: event_data as object (array)
echo "📋 Test 1: event_data as object\n";
testSystemEventWithObject($sdk, $ACCOUNT_ID, $WORKSPACE_ID);

echo "\n------------------------------------------------\n\n";

// Test 2: event_data as JSON string
echo "📋 Test 2: event_data as JSON string\n";
testSystemEventWithJsonString($sdk, $ACCOUNT_ID, $WORKSPACE_ID);

function testSystemEventWithObject($sdk, $accountId, $workspaceId) {
    $event = new SystemEvent([  
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'event_category' => 'SystemEvent',
        'event_name' => 'Campaign Trigger',
        'event_id' => 'evt_sys_obj_' . time(),
        'event_time' => "2026-03-02T09:45:00Z",
        'event_data' => [
            'campaign_id' => 'camp_002',
            'action' => 'start',
            'segment' => 'vip'
        ]
    ]);

    $validation = $event->validate();
    if (!$validation['isValid']) {
        echo "❌ Validation failed!\n";
        print_r($validation['errors']);
        return;
    }

    echo "✅ System event object validated successfully!\n";
    executeRequest($sdk, $event);
}

function testSystemEventWithJsonString($sdk, $accountId, $workspaceId) {
    $event = new SystemEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'event_category' => 'SystemEvent',
        'event_name' => 'Manual Action',
        'event_id' => 'evt_sys_str_' . (time() + 1),
        'event_time' => "2026-03-02T09:45:00Z",
        'event_data' => '{"action":"start","target":"user_list_1","payload":{}}'
    ]);

    $validation = $event->validate();
    if (!$validation['isValid']) {
        echo "❌ Validation failed!\n";
        print_r($validation['errors']);
        return;
    }

    echo "✅ System event with JSON string validated successfully!\n";
    executeRequest($sdk, $event);
}

function executeRequest($sdk, $event) {
    try {
        echo "📋 Payload:\n";
        echo json_encode($event->toArray(), JSON_PRETTY_PRINT) . "\n";

        $startTime = microtime(true) * 1000;
        $result = $sdk->sendSystemEvent($event);
        $endTime = microtime(true) * 1000;

        echo "⏱ Response Time: " . round($endTime - $startTime) . "ms\n";
        echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";

        if ($result['success']) {
            echo "✅ SUCCESS!\n";
            echo "Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "❌ FAILED!\n";
            echo "Error: " . json_encode($result['data'] ?? $result['error'], JSON_PRETTY_PRINT) . "\n";
        }
    } catch (Exception $e) {
        echo "❌ Request execution failed: " . $e->getMessage() . "\n";
    }
}
?>
