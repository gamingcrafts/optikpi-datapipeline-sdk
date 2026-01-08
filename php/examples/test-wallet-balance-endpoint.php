<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;

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

// Wallet Balance event data
$walletBalance = new WalletBalanceEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
    'event_category' => 'Wallet Balance',
    'event_name' => 'Balance Update',
    'event_id' => 'evt_wb_987654321',
    'event_time' => '2024-01-15T14:45:00Z',
    'wallet_type' => 'main',
    'currency' => 'USD',
    'current_cash_balance' => 1250.50,
    'current_bonus_balance' => 100.00,
    'current_total_balance' => 1350.50,
    'blocked_amount' => 50.00
]);

// Test wallet balance endpoint
function testWalletBalanceEndpoint($sdk, $walletBalance, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    try {
        echo "🚀 Testing Wallet Balance Events Endpoint\n";
        echo "==========================================\n";
        
        echo "Configuration:\n";
        echo "API Base URL: $API_BASE_URL\n";
        echo "Account ID: $ACCOUNT_ID\n";
        echo "Workspace ID: $WORKSPACE_ID\n";
        echo "Auth Token: " . substr($AUTH_TOKEN, 0, 8) . "...\n";
        
        echo "\nMaking API request using SDK...\n";
        echo "Wallet Balance Event Data: " . json_encode($walletBalance->toArray(), JSON_PRETTY_PRINT) . "\n";
        
        // Make the API call using SDK
        $startTime = microtime(true) * 1000;
        $result = $sdk->sendWalletBalanceEvent($walletBalance);
        $endTime = microtime(true) * 1000;
        
        if ($result['success']) {
            echo "\n✅ Success!\n";
            echo "==========================================\n";
            echo "HTTP Status: " . $result['status'] . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ API Error!\n";
            echo "==========================================\n";
            echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Error Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        }
        
    } catch (Exception $error) {
        echo "\n❌ Error occurred!\n";
        echo "==========================================\n";
        echo "Error: " . $error->getMessage() . "\n";
        echo "Stack: " . $error->getTraceAsString() . "\n";
    }
}

// Run the test
if (php_sapi_name() === 'cli') {
    testWalletBalanceEndpoint($sdk, $walletBalance, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);
}
