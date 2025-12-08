#!/usr/bin/env python3
"""
Test Gaming Endpoint
Demonstrates how to send gaming event data to the Optikpi Data Pipeline API
"""
import os
import sys
import json
import time
from pathlib import Path
from dotenv import load_dotenv

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK
from models.GamingActivityEvent import GamingActivityEvent

# Load environment variables
load_dotenv()

# Configuration - Read from environment variables
API_BASE_URL = os.getenv("API_BASE_URL")
AUTH_TOKEN = os.getenv("AUTH_TOKEN")
ACCOUNT_ID = os.getenv("ACCOUNT_ID")
WORKSPACE_ID = os.getenv("WORKSPACE_ID")

# Validate required environment variables
if not AUTH_TOKEN or not ACCOUNT_ID or not WORKSPACE_ID:
    print('❌ Error: Missing required environment variables!', file=sys.stderr)
    print('   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID', file=sys.stderr)
    print('   Copy env.example to .env and fill in your values', file=sys.stderr)
    sys.exit(1)

# Initialize SDK
sdk = OptikpiDataPipelineSDK({
    'authToken': AUTH_TOKEN,
    'accountId': ACCOUNT_ID,
    'workspaceId': WORKSPACE_ID,
    'baseURL': API_BASE_URL
})

# Gaming event data
gaming = GamingActivityEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="game_py_1",
    event_category="Gaming Activity",
    event_name="Play Casino Game",
    event_id="evt_" + str(int(time.time())),
    event_time="2024-01-15T10:30:00Z",
    wager_amount=10.00,
    win_amount=25.00,
    loss_amount=0.00,
    game_id="game_001",
    game_title="Mega Fortune Slots",
    provider="ProviderXYZ",
    bonus_id="bonus_12345",
    free_spin_id="freespin_67890",
    jackpot_amount=1000.00,
    num_spins_played=50,
    game_theme="Egyptian",
    remaining_spins=10,
    bet_value_per_spin=0.50,
    wagering_requirements_met=True,
    free_spin_expiry_date="2024-12-31T23:59:59Z",
    campaign_id="camp_summer2024",
    campaign_name="Summer Bonanza",
    rtp=96.5,
    game_category="slots",
    winning_bet_amount=25.00,
    jackpot_type="progressive",
    volatility="high",
    min_bet=0.10,
    max_bet=100.00,
    number_of_reels=5,
    number_of_paylines=20,
    feature_types="wild,scatter,freespins",
    game_release_date="2023-01-15T00:00:00Z",
    live_dealer_availability=False,
    side_bets_availability=True,
    multiplayer_option=False,
    auto_play=True,
    poker_variant="texas_holdem",
    tournament_name="Weekend Warriors",
    buy_in_amount=50.00,
    table_type="cash_game",
    stakes_level="medium",
    number_of_players=6,
    game_duration=45,
    hand_volume=120,
    player_position="button",
    final_hand="royal_flush",
    rake_contribution=2.50,
    multi_tabling_indicator=False,
    session_result="win",
    vip_status="gold",
    blind_level="50/100",
    rebuy_and_addon_info="1 rebuy, 1 addon",
    sport_type="football",
    betting_market="match_winner",
    odds=2.50,
    live_betting_availability=True,
    result="won",
    bet_status="settled",
    betting_channel="online",
    bonus_type="welcome_bonus",
    bonus_amount=100.00,
    free_spin_start_date="2024-01-01T00:00:00Z",
    num_spins_awarded=20,
    bonus_code="WELCOME100",
    parent_game_category="casino",
    currency="USD",
    money_type="real",
    transaction_type="bet"
)
validation = gaming.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Gaming event validated successfully!")

def test_gaming_endpoint():
    """
    Test gaming endpoint
    
    Sends gaming event data to the Optikpi Data Pipeline API
    and displays the response
    """
    try:
        print('🚀 Testing Gaming Events Endpoint')
        print('==================================')
        
        print('Configuration:')
        print(f'API Base URL: {API_BASE_URL}')
        print(f'Account ID: {ACCOUNT_ID}')
        print(f'Workspace ID: {WORKSPACE_ID}')
        print(f'Auth Token: {AUTH_TOKEN[:8]}...')
        
        print('\nMaking API request using SDK...')
        gaming_dict = gaming.to_dict()
        print(f'Customer Event Data: {json.dumps(gaming_dict, indent=2)}')
        
        # Make the API call using SDK
        start_time = time.time()
        result = sdk.send_gaming_activity_event(gaming_dict)
        end_time = time.time()
        
        response_time = int((end_time - start_time) * 1000)
        
        if result['success']:
            print('\n✅ Success!')
            print('==================================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Response Data: {json.dumps(result['data'], indent=2)}")
        else:
            print('\n❌ API Error!')
            print('==================================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Error Data: {json.dumps(result.get('data', result.get('error')), indent=2)}")
    
    except Exception as error:
        print('\n❌ SDK Error occurred!', file=sys.stderr)
        print('==================================', file=sys.stderr)
        print(f'Error: {str(error)}', file=sys.stderr)
        import traceback
        print(f'Stack: {traceback.format_exc()}', file=sys.stderr)


# Run the test
if __name__ == "__main__":
    test_gaming_endpoint()