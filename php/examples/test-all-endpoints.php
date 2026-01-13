<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\CustomerProfile;
use Optikpi\DataPipeline\Models\DepositEvent;
use Optikpi\DataPipeline\Models\GamingActivityEvent;
use Optikpi\DataPipeline\Models\WithdrawEvent;
use Optikpi\DataPipeline\Models\CustomerExtEvent;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;
use Optikpi\DataPipeline\Models\ReferFriendEvent;

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

// Test data for different endpoints
$TEST_DATA = [
    'customer' => new CustomerProfile([
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
    ]),
    'account' => new AccountEvent([
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123456',
        'event_category' => 'Account',
        'event_name' => 'Player Registration',
        'event_id' => 'evt_123456789',
        'event_time' => '2024-01-15T10:30:00Z',
        'device' => 'desktop',
        'status' => 'verified',
        'affiliate_id' => 'aff_123',
        'partner_id' => 'partner_456',
        'campaign_code' => 'CAMPAIGN_001',
        'reason' => 'Registration completed successfully'
    ]),
    'deposit' => new DepositEvent([
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123456',
        'event_category' => 'Deposit',
        'event_name' => 'Successful Deposit',
        'event_id' => 'evt_dep_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'amount' => 500.00,
        'payment_method' => 'bank',
        'transaction_id' => 'txn_123456789',
        'payment_provider_id' => 'provider123',
        'payment_provider_name' => 'Chase Bank',
        'failure_reason' => null
    ]),
    'withdraw' => new WithdrawEvent([
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123456',
        'event_category' => 'Withdraw',
        'event_name' => 'Successful Withdrawal',
        'event_id' => 'evt_wd_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'amount' => 250.00,
        'payment_method' => 'bank',
        'transaction_id' => 'txn_wd_123456789',
        'failure_reason' => null
    ]),
    'gaming' => new GamingActivityEvent([
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'user123411',
        'event_category' => 'Gaming Activity',
        'event_name' => 'Play Casino Game',
        'event_id' => 'evt_' . (time() * 1000),
        'event_time' => date('c'),
        'wager_amount' => 10.00,
        'win_amount' => 25.00,
        'loss_amount' => 0.00,
        'game_id' => 'game_001',
        'game_title' => 'Mega Fortune Slots',
        'provider' => 'ProviderXYZ',
        'bonus_id' => 'bonus_12345',
        'free_spin_id' => 'freespin_67890',
        'jackpot_amount'=> 1000.00,
        'num_spins_played' => 50,
        'game_theme' => 'Egyptian',
        'remaining_spins' => 10,
        'bet_value_per_spin' => 0.50,
        'wagering_requirements_met' => true,
        'free_spin_expiry_date' => '2024-12-31T23:59:59Z',
        'campaign_id' => 'camp_summer2024',
        'campaign_name' => 'Summer Bonanza',
        'rtp' => 96.5,
        'game_category' => 'slots',
        'winning_bet_amount' => 25.00,
        'jackpot_type' => 'progressive',
        'volatility' => 'high',
        'min_bet' => 0.10,
        'max_bet' => 100.00,
        'number_of_reels' => 5,
        'number_of_paylines' => 20,
        'feature_types' => 'wild,scatter,freespins',
        'game_release_date' => '2023-01-15T00:00:00Z',
        'live_dealer_availability' => false,
        'side_bets_availability' => true,
        'multiplayer_option' => false,
        'auto_play' => true,
        'poker_variant' => 'texas_holdem',
        'tournament_name' => 'Weekend Warriors',
        'buy_in_amount' => 50.00,
        'table_type' => 'cash_game',
        'stakes_level' => 'medium',
        'number_of_players' => 6,
        'game_duration' => 45,
        'hand_volume' => 120,
        'player_position' => 'button',
        'final_hand' => 'royal_flush',
        'rake_contribution' => 2.50,
        'multi_tabling_indicator' => false,
        'session_result' => 'win',
        'vip_status' => 'gold',
        'blind_level' => '50/100',
        'rebuy_and_addon_info' => '1 rebuy, 1 addon',
        'sport_type' => 'football',
        'betting_market' => 'match_winner',
        'odds' => 2.50,
        'live_betting_availability' => true,
        'result' => 'won',
        'bet_status' => 'settled',
        'betting_channel' => 'online',
        'bonus_type' => 'welcome_bonus',
        'bonus_amount' => 100.00,
        'free_spin_start_date' => '2024-01-01T00:00:00Z',
        'num_spins_awarded' => 20,
        'bonus_code' => 'WELCOME100',
        'parent_game_category' => 'casino',
        'currency' => 'USD',
        'money_type' => 'real',
        'transaction_type' => 'bet'
    ]),
    'customerExt' => new CustomerExtEvent([
        'account_id' => $ACCOUNT_ID,
        'workspace_id' => $WORKSPACE_ID,
        'user_id' => 'opti789',
        'list_name' => 'BINGO_PREFERENCES',
        'ext_data' => [
            'Email' => 'True',
            'SMS' => 'True',
            'PushNotifications' => 'False'
        ]
    ]),
    'walletBalance' => new WalletBalanceEvent([
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
    ]),
    'referFriend' => new ReferFriendEvent([
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
    ])
];

$eventsToValidate = [
    ['key' => 'customer', 'label' => 'Customer'],
    ['key' => 'account', 'label' => 'Account'],
    ['key' => 'deposit', 'label' => 'Deposit'],
    ['key' => 'withdraw', 'label' => 'Withdraw'],
    ['key' => 'customerExt', 'label' => 'CustomerExt'],
    ['key' => 'gaming', 'label' => 'Gaming'],
    ['key' => 'walletBalance', 'label' => 'WalletBalance'],
    ['key' => 'referFriend', 'label' => 'ReferFriend']
];

foreach ($eventsToValidate as $event) {
    $validation = $TEST_DATA[$event['key']]->validate();
    if (!$validation['isValid']) {
        echo "❌ {$event['label']} validation errors:\n";
        foreach ($validation['errors'] as $error) {
            echo "  - $error\n";
        }
        exit(1);
    }
    echo "✅ {$event['label']} event validated successfully!\n";
}

// Make API request using SDK
function makeApiRequest($sdk, $endpoint, $data, $method) {
    echo "\n🌐 Making $method request to: $endpoint\n";
    echo "📋 Data: " . json_encode($data->toArray(), JSON_PRETTY_PRINT) . "\n";

    $startTime = microtime(true) * 1000;
    $result = null;

    try {
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
            case 'extattributes':
                $result = $sdk->sendExtendedAttributes($data);
                break;
            case 'walletBalance':
                $result = $sdk->sendWalletBalanceEvent($data);
                break;
            case 'referFriend':
                $result = $sdk->sendReferFriendEvent($data);
                break;
            default:
                throw new Exception("Unknown method: $method");
        }

        $endTime = microtime(true) * 1000;

        return [
            'status' => $result['status'] ?? 200,
            'data' => $result['data'] ?? null,
            'responseTime' => round($endTime - $startTime),
            'success' => $result['success'] ?? false
        ];
    } catch (Exception $error) {
        $endTime = microtime(true) * 1000;
        return [
            'status' => 500,
            'data' => ['error' => $error->getMessage()],
            'responseTime' => round($endTime - $startTime),
            'success' => false,
            'error' => $error
        ];
    }
}

// Test all endpoints
function testAllEndpoints($sdk, $TEST_DATA, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    echo "🚀 Testing All API Endpoints with Header-Based Authentication\n";
    echo "==========================================================\n";
    echo "API Base URL: $API_BASE_URL\n";
    echo "Account ID: $ACCOUNT_ID\n";
    echo "Workspace ID: $WORKSPACE_ID\n";
    echo "Auth Token: " . substr($AUTH_TOKEN, 0, 8) . "...\n";

    $endpoints = [
        ['name' => 'Customer Profile', 'endpoint' => '/customers', 'data' => $TEST_DATA['customer'], 'method' => 'customer'],
        ['name' => 'Account Event', 'endpoint' => '/events/account', 'data' => $TEST_DATA['account'], 'method' => 'account'],
        ['name' => 'Deposit Event', 'endpoint' => '/events/deposit', 'data' => $TEST_DATA['deposit'], 'method' => 'deposit'],
        ['name' => 'Withdrawal Event', 'endpoint' => '/events/withdraw', 'data' => $TEST_DATA['withdraw'], 'method' => 'withdraw'],
        ['name' => 'Gaming Activity', 'endpoint' => '/events/gaming-activity', 'data' => $TEST_DATA['gaming'], 'method' => 'gaming'],
        ['name' => 'Extended Attributes', 'endpoint' => '/extattributes', 'data' => $TEST_DATA['customerExt'], 'method' => 'extattributes'],
        ['name' => 'Wallet Balance', 'endpoint' => '/events/wallet-balance', 'data' => $TEST_DATA['walletBalance'], 'method' => 'walletBalance'],
        ['name' => 'Refer Friend', 'endpoint' => '/events/refer-friend', 'data' => $TEST_DATA['referFriend'], 'method' => 'referFriend']
    ];

    $results = [];

    foreach ($endpoints as $endpoint) {
        try {
            echo "\n📡 Testing {$endpoint['name']}...\n";
            echo str_repeat('─', 50) . "\n";

            $result = makeApiRequest($sdk, $endpoint['endpoint'], $endpoint['data'], $endpoint['method']);

            echo "✅ {$endpoint['name']} - SUCCESS\n";
            echo "   Status: {$result['status']}\n";
            echo "   Response Time: {$result['responseTime']}ms\n";
            echo "   SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "   Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";

            $results[] = [
                'endpoint' => $endpoint['name'],
                'status' => 'SUCCESS',
                'httpStatus' => $result['status'],
                'responseTime' => $result['responseTime'],
                'data' => $result['data']
            ];

        } catch (Exception $error) {
            echo "❌ {$endpoint['name']} - FAILED\n";
            echo "   Error: {$error->getMessage()}\n";

            $results[] = [
                'endpoint' => $endpoint['name'],
                'status' => 'FAILED',
                'error' => $error->getMessage()
            ];
        }
    }

    // Summary
    echo "\n📊 Test Summary\n";
    echo "===============\n";

    $successful = count(array_filter($results, function($r) { return $r['status'] === 'SUCCESS'; }));
    $failed = count(array_filter($results, function($r) { return $r['status'] === 'FAILED'; }));

    echo "✅ Successful: $successful\n";
    echo "❌ Failed: $failed\n";
    echo "📈 Success Rate: " . round(($successful / count($results)) * 100, 1) . "%\n";

    if ($failed > 0) {
        echo "\n❌ Failed Endpoints:\n";
        foreach (array_filter($results, function($r) { return $r['status'] === 'FAILED'; }) as $result) {
            echo "   - {$result['endpoint']}: " . ($result['error'] ?? 'Unknown error') . "\n";
        }
    }

    return $results;
}

// Run tests
function runTests($sdk, $TEST_DATA, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    try {
        // Test all endpoints
        $results = testAllEndpoints($sdk, $TEST_DATA, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);

        echo "\n🎉 All tests completed!\n";
        return $results;
    } catch (Exception $error) {
        echo "\n💥 Test execution failed: {$error->getMessage()}\n";
        throw $error;
    }
}

// Run if this file is executed directly
if (php_sapi_name() === 'cli') {
    runTests($sdk, $TEST_DATA, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);
}
