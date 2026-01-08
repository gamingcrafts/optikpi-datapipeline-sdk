<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\CustomerProfile;
use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\DepositEvent;
use Optikpi\DataPipeline\Models\WithdrawEvent;
use Optikpi\DataPipeline\Models\GamingActivityEvent;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;
use Optikpi\DataPipeline\Models\ReferFriendEvent;
use Optikpi\DataPipeline\Models\CustomerExtEvent;

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

// Create sample data
$customer = new CustomerProfile([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
    'username' => 'john_doe',
    'email' => 'john.doe@example.com',
    'account_status' => 'Active'
]);

$accountEvent = new AccountEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
    'event_name' => 'Login',
    'event_id' => 'event_' . time(),
    'event_time' => date('c')
]);

$depositEvent = new DepositEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
    'event_name' => 'Successful Deposit',
    'event_id' => 'deposit_' . time(),
    'event_time' => date('c'),
    'amount' => 100.00,
    'payment_method' => 'credit_card',
    'transaction_id' => 'txn_' . time()
]);

$batchData = [
    'customers' => [$customer->toArray()],
    'accountEvents' => [$accountEvent->toArray()],
    'depositEvents' => [$depositEvent->toArray()]
];

echo "Sending batch operations...\n";
$startTime = microtime(true);
$result = $sdk->sendBatch($batchData);
$endTime = microtime(true);

if ($result['success']) {
    echo "\n✅ Batch Success!\n";
    echo "============================\n";
    echo "Response Time: " . round(($endTime - $startTime) * 1000) . "ms\n";
    echo "Results: " . json_encode($result['results'], JSON_PRETTY_PRINT) . "\n";
} else {
    echo "\n❌ Batch Error!\n";
    echo "============================\n";
    echo "Error: " . json_encode($result, JSON_PRETTY_PRINT) . "\n";
}

