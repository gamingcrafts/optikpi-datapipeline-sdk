<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\GamingActivityEvent;

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

// Gaming activity event data
$gaming = new GamingActivityEvent([
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
]);

// Validate the gaming event
$validation = $gaming->validate();
if (!$validation['isValid']) {
    echo "❌ Validation errors:\n";
    foreach ($validation['errors'] as $error) {
        echo "  - $error\n";
    }
    exit(1);
}

echo "✅ Gaming event validated successfully!\n";

// SDK handles HMAC authentication automatically

// Test gaming endpoint
function testGamingEndpoint($sdk, $gaming, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN) {
    try {
        echo "🎮 Testing Gaming Activity Events Endpoint\n";
        echo "==========================================\n";

        echo "Configuration:\n";
        echo "API Base URL: $API_BASE_URL\n";
        echo "Account ID: $ACCOUNT_ID\n";
        echo "Workspace ID: $WORKSPACE_ID\n";
        echo "Auth Token: " . substr($AUTH_TOKEN, 0, 8) . "...\n";

        echo "\nMaking API request using SDK...\n";
        echo "Gaming Event Data: " . json_encode($gaming->toArray(), JSON_PRETTY_PRINT) . "\n";

        // Make the API call using SDK
        $startTime = microtime(true) * 1000;
        $result = $sdk->sendGamingActivityEvent($gaming);
        $endTime = microtime(true) * 1000;

        if ($result['success']) {
            echo "\n✅ Success!\n";
            echo "==========================================\n";
            echo "HTTP Status: " . $result['status'] . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ API Error!\n";
            echo "==========================================\n";
            echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Error Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        }

    } catch (Exception $error) {
        echo "\n❌ SDK Error occurred!\n";
        echo "==========================================\n";
        echo "Error: " . $error->getMessage() . "\n";
        echo "Stack: " . $error->getTraceAsString() . "\n";
    }
}

// Run the test
if (php_sapi_name() === 'cli') {
    testGamingEndpoint($sdk, $gaming, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN);
}
