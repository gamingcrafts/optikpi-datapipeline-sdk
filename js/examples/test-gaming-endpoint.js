require('dotenv').config();
const OptikpiDataPipelineSDK = require('@optikpi/datapipeline-sdk').default;
const { GamingActivityEvent } = require('@optikpi/datapipeline-sdk')

// Configuration - Read from environment variables
const API_BASE_URL = process.env.API_BASE_URL;
const AUTH_TOKEN = process.env.AUTH_TOKEN;
const ACCOUNT_ID = process.env.ACCOUNT_ID;
const WORKSPACE_ID = process.env.WORKSPACE_ID;

// Validate required environment variables
if (!AUTH_TOKEN || !ACCOUNT_ID || !WORKSPACE_ID) {
  console.error('❌ Error: Missing required environment variables!');
  console.error('   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID');
  console.error('   Copy env.example to .env and fill in your values');
  process.exit(1);
}

// Initialize SDK
const sdk = new OptikpiDataPipelineSDK({
  authToken: AUTH_TOKEN,
  accountId: ACCOUNT_ID,
  workspaceId: WORKSPACE_ID,
  baseURL: API_BASE_URL
});

// Gaming activity event data
const gaming = new GamingActivityEvent({
  "account_id": ACCOUNT_ID,
  "workspace_id": WORKSPACE_ID,
  "user_id": "user123411",
  "event_category": "Gaming Activity",
  "event_name": "Play Casino Game",
  "event_id": "evt_" + Date.now(),
  "event_time": new Date().toISOString(),
  "wager_amount": 10.00,
  "win_amount": 25.00,
  "loss_amount": 0.00,
  "game_id": "game_001",
  "game_title": "Mega Fortune Slots",
  "provider": "ProviderXYZ",
  "bonus_id": "bonus_12345",
  "free_spin_id": "freespin_67890",
  "jackpot_amount": 1000.00,
  "num_spins_played": 50,
  "game_theme": "Egyptian",
  "remaining_spins": 10,
  "bet_value_per_spin": 0.50,
  "wagering_requirements_met": true,
  "free_spin_expiry_date": "2024-12-31T23:59:59Z",
  "campaign_id": "camp_summer2024",
  "campaign_name": "Summer Bonanza",
  "rtp": 96.5,
  "game_category": "slots",
  "winning_bet_amount": 25.00,
  "jackpot_type": "progressive",
  "volatility": "high",
  "min_bet": 0.10,
  "max_bet": 100.00,
  "number_of_reels": 5,
  "number_of_paylines": 20,
  "feature_types": "wild,scatter,freespins",
  "game_release_date": "2023-01-15T00:00:00Z",
  "live_dealer_availability": false,
  "side_bets_availability": true,
  "multiplayer_option": false,
  "auto_play": true,
  "poker_variant": "texas_holdem",
  "tournament_name": "Weekend Warriors",
  "buy_in_amount": 50.00,
  "table_type": "cash_game",
  "stakes_level": "medium",
  "number_of_players": 6,
  "game_duration": 45,
  "hand_volume": 120,
  "player_position": "button",
  "final_hand": "royal_flush",
  "rake_contribution": 2.50,
  "multi_tabling_indicator": false,
  "session_result": "win",
  "vip_status": "gold",
  "blind_level": "50/100",
  "rebuy_and_addon_info": "1 rebuy, 1 addon",
  "sport_type": "football",
  "betting_market": "match_winner",
  "odds": 2.50,
  "live_betting_availability": true,
  "result": "won",
  "bet_status": "settled",
  "betting_channel": "online",
  "bonus_type": "welcome_bonus",
  "bonus_amount": 100.00,
  "free_spin_start_date": "2024-01-01T00:00:00Z",
  "num_spins_awarded": 20,
  "bonus_code": "WELCOME100",
  "parent_game_category": "casino",
  "currency": "USD",
  "money_type": "real",
  "transaction_type": "bet"
});

// Validate the account event
const validation = gaming.validate();
if (!validation.isValid) {
  console.error('❌ Validation errors:', validation.errors);
  process.exit(1);
}

console.log('✅ Gaming event validated successfully!');

// SDK handles HMAC authentication automatically

// Test gaming endpoint
async function testGamingEndpoint() {
  try {
    console.log('🎮 Testing Gaming Activity Events Endpoint');
    console.log('==========================================');

    console.log('Configuration:');
    console.log(`API Base URL: ${API_BASE_URL}`);
    console.log(`Account ID: ${ACCOUNT_ID}`);
    console.log(`Workspace ID: ${WORKSPACE_ID}`);
    console.log(`Auth Token: ${AUTH_TOKEN.substring(0, 8)}...`);

    console.log('\nMaking API request using SDK...');
    console.log('Gaming Event Data:', JSON.stringify(gaming, null, 2));

    // Make the API call using SDK
    const startTime = Date.now();
    const result = await sdk.sendGamingActivityEvent(gaming);
    const endTime = Date.now();

    if (result.success) {
      console.log('\n✅ Success!');
      console.log('==========================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Response Data:', JSON.stringify(result.data, null, 2));
    } else {
      console.log('\n❌ API Error!');
      console.log('==========================================');
      console.log(`HTTP Status: ${result.status}`);
      console.log(`Response Time: ${endTime - startTime}ms`);
      console.log(`SDK Success: ${result.success}`);
      console.log('Error Data:', JSON.stringify(result.data, null, 2));
    }

  } catch (error) {
    console.error('\n❌ SDK Error occurred!');
    console.error('==========================================');
    console.error('Error:', error.message);
    console.error('Stack:', error.stack);
  }
}

// Run the test
if (require.main === module) {
  testGamingEndpoint();
}

module.exports = {
  testGamingEndpoint,
  gaming,
  sdk
};
