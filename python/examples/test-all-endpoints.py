#!/usr/bin/env python3
"""
Test All API Endpoints
Tests customer profiles, account events, deposit events, withdrawal events, and gaming activity
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
from models.AccountEvent import AccountEvent
from models.CustomerExtEvent import CustomerExtEvent
from models.CustomerProfile import CustomerProfile
from models.DepositEvent import DepositEvent
from models.GamingActivityEvent import GamingActivityEvent
from models.WithdrawEvent import WithdrawEvent
from models.WalletBalanceEvent import WalletBalanceEvent
from models.ReferFriendEvent import ReferFriendEvent


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

# Test data for different endpoints
TEST_DATA = {
    "customer": CustomerProfile(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="1allcust_py_2",
        username="john_doe",
        full_name="John Doe",
        first_name="John",
        last_name="Doe",
        date_of_birth="1990-01-15",
        email="john.doe@example.com",
        phone_number="+1234567890",
        gender="Male",
        country="United States",
        city="New York",
        language="en",
        currency="USD",
        marketing_email_preference="Opt-in",
        notifications_preference="Opt-in",
        subscription="Subscribed",
        privacy_settings="private",
        deposit_limits=1000.00,
        loss_limits=500.00,
        wagering_limits=2000.00,
        session_time_limits=120,
        cooling_off_period=7,
        self_exclusion_period=30,
        reality_checks_notification="daily",
        account_status="Active",
        vip_status="Regular",
        loyalty_program_tiers="Bronze",
        bonus_abuser="Not flagged",
        financial_risk_level=0.3,
        acquisition_source="Google Ads",
        partner_id="partner123",
        affliate_id="affiliate456",
        referral_link_code="REF789",
        referral_limit_reached="Not Reached",
        creation_timestamp="2024-01-15T10:30:00Z",
        phone_verification="Verified",
        email_verification="Verified",
        bank_verification="NotVerified",
        iddoc_verification="Verified",
        cooling_off_expiry_date="2024-12-31T23:59:59Z",
        self_exclusion_expiry_date="2025-01-31T23:59:59Z",
        risk_score_level="low",
        marketing_sms_preference="Opt-in",
        custom_data={
            "favorite_game": "slots",
            "newsletter_signup": True
        },
        self_exclusion_by="player",
        self_exclusion_by_type="voluntary",
        self_exclusion_check_time="2024-01-15T10:30:00Z",
        self_exclusion_created_time="2024-01-01T00:00:00Z",
        closed_time=None,
        real_money_enabled=True,
        push_token="push_token_abc123",
        android_push_token="android_push_token_xyz456",
        ios_push_token="ios_push_token_def789",
        windows_push_token="windows_push_token_ghi012",
        mac_dmg_push_token="mac_push_token_jkl345"
    ),
    "customerext": CustomerExtEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0008",
        list_name="BINGO_PREFERENCES",
        ext_data={
            "Email": "True",
            "SMS": "True",
             "PushNotifications": "False"
             }
    ),
    "account": AccountEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0002",
        event_category="Account",
        event_name="Player Registration",
        event_id="evt_123456789",
        event_time="2024-01-15T10:30:00Z",
        device="desktop",
        status="verified",
        affiliate_id="aff_123",
        partner_id="partner_456",
        campaign_code="CAMPAIGN_001",
        reason="Registration completed successfully"
    ),
    "deposit": DepositEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0003",
        event_category="Deposit",
        event_name="Successful Deposit",
        event_id="evt_dep_987654321",
        event_time="2024-01-15T14:45:00Z",
        amount=500.00,
        currency="USD",
        payment_method="bank",
        transaction_id="txn_123456789",
        payment_provider_id="provider123",
        payment_provider_name="Chase Bank",
        status="success",
        fees=2.50,
        net_amount=497.50,
        failure_reason=None,
        metadata={
        "bank_name": "Chase Bank",
        "account_last4": "1234"
        }
    ),
    "withdraw": WithdrawEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0004",
        event_category="Withdraw",
        event_name="Successful Withdrawal",
        event_id="evt_wd_987654321",
        event_time="2024-01-15T14:45:00Z",
        amount=250.00,
        currency="USD",
        payment_method="bank",
        transaction_id="txn_wd_123456789",
        status="success",
        fees=1.50,
        device="desktop",
        affiliate_id="aff_123456",
        partner_id="partner_789",
        campaign_code="SUMMER2024",
        reason="User requested withdrawal",
        net_amount=248.50,
        withdrawal_reason="Cash out winnings",
        processing_time="2024-01-15T10:30:00Z",
        failure_reason=None
    ),
    "gaming": GamingActivityEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0005",
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
    ),
    "wallet": WalletBalanceEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0006",
        event_category="Wallet Balance",
        event_name="Balance Updated",
        event_id="evt_wallet_123456789",
        event_time="2024-01-15T17:00:00Z",
        wallet_type="main",
        currency="USD",
        current_cash_balance=450.00,
        current_bonus_balance=100.00,
        current_total_balance=550.00,
        blocked_amount=20.00
    ),
    "referral": ReferFriendEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="optuser0007",
        event_category="Refer Friend",
        event_name="Referral Successful",
        event_id="evt_rf_123456789",
        event_time="2024-01-15T17:30:00Z",
        referral_code_used="REF123",
        successful_referral_confirmation=True,
        reward_type="bonus",
        reward_claimed_status="claimed",
        referee_user_id="user654321",
        referee_registration_date="2024-01-15T16:00:00Z",
        referee_first_deposit=100.00
    )

}
events_to_validate = [
    {"key": "customer", "label": "Customer"},
    {"key": "account", "label": "Account"},
    {"key": "deposit", "label": "Deposit"},
    {"key": "withdraw", "label": "Withdraw"},
    {"key": "gaming", "label": "Gaming"},
    {"key": "customerext", "label": "Customer Extension"},
    {"key": "wallet", "label": "Wallet Balance"},
    {"key": "referral", "label": "Refer Friend"}
]

for event in events_to_validate:
    key = event["key"]
    label = event["label"]
    
    validation = TEST_DATA[key].validate()
    
    if not validation["isValid"]:
        print(f"❌ {label} validation errors:", validation["errors"])
        exit(1)
    
    print(f"✅ {label} event validated successfully!")



def make_api_request(endpoint, data, method):
    """
    Make an API request using the SDK
    
    Args:
        endpoint: API endpoint path
        data: Request data
        method: SDK method to call (customer, account, deposit, withdraw, gaming)
    
    Returns:
        Dictionary with response status, data, and response time
    """
    print(f"\n🌐 Making {method.upper()} request to: {endpoint}")
    print(f"📋 Data: {json.dumps(data, indent=2)}")
    
    start_time = time.time()
    result = None
    
    try:
        if method == 'customer':
            result = sdk.send_customer_profile(data)
        elif method == 'account':
            result = sdk.send_account_event(data)
        elif method == 'deposit':
            result = sdk.send_deposit_event(data)
        elif method == 'withdraw':
            result = sdk.send_withdraw_event(data)
        elif method == 'gaming':
            result = sdk.send_gaming_activity_event(data)
        elif method == 'customerext':
            result = sdk.send_extended_attributes(data)
        elif method == 'wallet':
            result = sdk.send_wallet_balance_event(data)
        elif method == 'referral':
            result = sdk.send_refer_friend_event(data)    
        else:
            raise ValueError(f"Unknown method: {method}")
        
        end_time = time.time()
        response_time = int((end_time - start_time) * 1000)
        
        return {
            'status': result.get('status', 200),
            'data': result.get('data'),
            'response_time': response_time,
            'success': result.get('success', False)
        }
    
    except Exception as error:
        end_time = time.time()
        response_time = int((end_time - start_time) * 1000)
        
        return {
            'status': getattr(error.response, 'status_code', 500) if hasattr(error, 'response') else 500,
            'data': getattr(error.response, 'data', {'error': str(error)}) if hasattr(error, 'response') else {'error': str(error)},
            'response_time': response_time,
            'success': False,
            'error': error
        }


def test_all_endpoints():
    """
    Test all API endpoints
    
    Returns:
        List of test results
    """
    print('🚀 Testing All API Endpoints with Header-Based Authentication')
    print('==========================================================')
    print(f'API Base URL: {API_BASE_URL}')
    print(f'Account ID: {ACCOUNT_ID}')
    print(f'Workspace ID: {WORKSPACE_ID}')
    print(f'Auth Token: {AUTH_TOKEN[:8]}...')
    customer_dict = TEST_DATA['customer'].to_dict()
    account_dict = TEST_DATA['account'].to_dict()
    deposit_dict = TEST_DATA['deposit'].to_dict()
    withdraw_dict = TEST_DATA['withdraw'].to_dict()
    gaming_dict = TEST_DATA['gaming'].to_dict()
    extattr_dict = TEST_DATA['customerext'].to_dict()
    wallet_dict = TEST_DATA['wallet'].to_dict()
    referral_dict = TEST_DATA['referral'].to_dict()
    endpoints = [
        {
            'name': 'Customer Profile',
            'endpoint': '/customers',
            'data': customer_dict,
            'method': 'customer'
        },
        {
            'name': 'Account Event',
            'endpoint': '/events/account',
            'data': account_dict,
            'method': 'account'
        },
        {
            'name': 'Deposit Event',
            'endpoint': '/events/deposit',
            'data': deposit_dict,
            'method': 'deposit'
        },
        {
            'name': 'Withdrawal Event',
            'endpoint': '/events/withdraw',
            'data': withdraw_dict,
            'method': 'withdraw'
        },
        {
            'name': 'Gaming Activity',
            'endpoint': '/events/gaming-activity',
            'data': gaming_dict,
            'method': 'gaming'
        },
        {
            'name': 'Customer Extended Attributes',
            'endpoint': '/extattributes',
            'data': extattr_dict,
            'method': 'customerext'
        },
        {
            'name': 'Wallet Balance Event',
            'endpoint': '/events/wallet-balance',
            'data': wallet_dict,
            'method': 'wallet'
        },
        {
            'name': 'Refer Friend Event',
            'endpoint': '/events/refer-friend',
            'data': referral_dict,
            'method': 'referral'
        }
    ]
    
    results = []
    
    for endpoint in endpoints:
        try:
            print(f"\n📡 Testing {endpoint['name']}...")
            print('─' * 50)
            
            result = make_api_request(
                endpoint['endpoint'],
                endpoint['data'],
                endpoint['method']
            )
            
            print(f"✅ {endpoint['name']} - SUCCESS")
            print(f"   Status: {result['status']}")
            print(f"   Response Time: {result['response_time']}ms")
            print(f"   SDK Success: {result['success']}")
            print(f"   Response: {json.dumps(result['data'], indent=2)}")
            
            results.append({
                'endpoint': endpoint['name'],
                'status': 'SUCCESS',
                'http_status': result['status'],
                'response_time': result['response_time'],
                'data': result['data']
            })
        
        except Exception as error:
            print(f"❌ {endpoint['name']} - FAILED")
            
            if hasattr(error, 'response'):
                print(f"   HTTP Status: {error.response.status_code}")
                print(f"   Error Response: {json.dumps(error.response.data, indent=2)}")
                
                results.append({
                    'endpoint': endpoint['name'],
                    'status': 'FAILED',
                    'http_status': error.response.status_code,
                    'error': error.response.data
                })
            else:
                print(f"   Error: {str(error)}")
                
                results.append({
                    'endpoint': endpoint['name'],
                    'status': 'FAILED',
                    'error': str(error)
                })
    
    # Summary
    print('\n📊 Test Summary')
    print('===============')
    
    successful = len([r for r in results if r['status'] == 'SUCCESS'])
    failed = len([r for r in results if r['status'] == 'FAILED'])
    total = len(results)
    
    print(f"✅ Successful: {successful}")
    print(f"❌ Failed: {failed}")
    if total > 0:
        print(f"📈 Success Rate: {(successful / total) * 100:.1f}%")
    
    if failed > 0:
        print('\n❌ Failed Endpoints:')
        for result in results:
            if result['status'] == 'FAILED':
                error_msg = result.get('error', {}).get('message') if isinstance(result.get('error'), dict) else result.get('error')
                print(f"   - {result['endpoint']}: {error_msg}")
    
    return results


def run_tests():
    """
    Run all tests
    
    Returns:
        Test results
    """
    try:
        
        # Test all endpoints
        results = test_all_endpoints()
        
        print('\n🎉 All tests completed!')
        return results
    
    except Exception as error:
        print(f'\n💥 Test execution failed: {str(error)}', file=sys.stderr)
        raise


# Run if this file is executed directly
if __name__ == "__main__":
    try:
        run_tests()
    except Exception as e:
        sys.exit(1)