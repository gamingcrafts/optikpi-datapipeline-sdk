import os
import sys
import json
import time
from datetime import datetime
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
API_BASE_URL = os.getenv('API_BASE_URL')
AUTH_TOKEN = os.getenv('AUTH_TOKEN')
ACCOUNT_ID = os.getenv('ACCOUNT_ID')
WORKSPACE_ID = os.getenv('WORKSPACE_ID')

# Validate required environment variables
if not all([AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID]):
    print('❌ Error: Missing required environment variables!')
    print('   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID')
    print('   Copy env.example to .env and fill in your values')
    sys.exit(1)

# Initialize SDK
sdk = OptikpiDataPipelineSDK({
    'authToken': AUTH_TOKEN,
    'accountId': ACCOUNT_ID,
    'workspaceId': WORKSPACE_ID,
    'baseURL': API_BASE_URL
})


def create_sample_customer(account_id, workspace_id):
    """Create sample customer profile"""
    return CustomerProfile(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="py_field01",
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
        reality_checks_notification="daily",
        account_status="Active",
        vip_status="Regular",
        loyalty_program_tiers="Bronze",
        bonus_abuser="Not flagged",
        financial_risk_level=0.3,
        acquisition_source="Google Ads",
        partner_id="partner123",
        referral_link_code="REF789",
        referral_limit_reached="Not Reached",
        creation_timestamp="2024-01-15T10:30:00Z",
        phone_verification="Verified",
        email_verification="Verified",
        bank_verification="NotVerified",
        iddoc_verification="Verified",
        cooling_off_expiry_date="2024-12-31T23:59:59Z",
        self_exclusion_expiry_date="2025-01-31T23:59:59Z",
        risk_score_level="0.2",
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
        real_money_enabled="True",
        push_token="push_token_abc123",
        android_push_token="android_push_token_xyz456",
        ios_push_token="ios_push_token_def789",
        windows_push_token="windows_push_token_ghi012",
        mac_dmg_push_token="mac_push_token_jkl345"
    )


def create_sample_extended_attributes_map_format(account_id, workspace_id):
    """Create sample extended attributes event - Map format"""
    return CustomerExtEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="user1112",
        list_name="BINGO_PREFERENCES",
        ext_data={
            "Email": "True",
            "SMS": "True",
            "PushNotifications": "False"
        }
    )


def create_sample_extended_attributes_string_format(account_id, workspace_id):
    """Create sample extended attributes event - String format"""
    return CustomerExtEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="user1113",
        list_name="GAMING_PREFERENCES",
        ext_data=json.dumps({
            "Email": "True",
            "SMS": "True",
            "PushNotifications": "True"
        })
    )


def create_sample_account_event(account_id, workspace_id):
    """Create sample account event"""
    return AccountEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="user1114",
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
    )


def create_sample_deposit_event(account_id, workspace_id):
    """Create sample deposit event"""
    return DepositEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="1dep_py_1",
        event_category="Deposit",
        event_name="Successful Deposit",
        event_id="evt_dep_987654321",
        event_time="2024-01-15T14:45:00Z",
        amount=500.00,
        payment_method="bank",
        transaction_id="txn_123456789",
        payment_provider_id="provider123",
        payment_provider_name="Chase Bank",
        failure_reason=None,
    )


def create_sample_withdraw_event(account_id, workspace_id):
    """Create sample withdraw event"""
    return WithdrawEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="2wd_py_1",
        event_category="Withdraw",
        event_name="Successful Withdrawal",
        event_id="evt_wd_987654321",
        event_time="2024-01-15T14:45:00Z",
        amount=250.00,
        payment_method="bank",
        transaction_id="txn_wd_123456789",
        failure_reason=None
    )


def create_sample_gaming_activity_event(account_id, workspace_id):
    """Create sample gaming activity event"""
    return GamingActivityEvent(
        account_id=ACCOUNT_ID,
        workspace_id=WORKSPACE_ID,
        user_id="1game_py_1",
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


def create_sample_refer_friend_event(account_id, workspace_id):
    """Create sample refer friend event"""
    return ReferFriendEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="user1118",
        event_category="Refer Friend",
        event_name="Referral Successful",
        event_id="evt_rf_987654321",
        event_time="2024-01-15T14:45:00Z",
        referral_code_used="REF123456",
        successful_referral_confirmation=True,
        reward_type="bonus",
        reward_claimed_status="claimed",
        referee_user_id="user789012",
        referee_registration_date="2024-01-15T10:30:00Z",
        referee_first_deposit=100.00
    )


def create_sample_wallet_balance_event(account_id, workspace_id):
    """Create sample wallet balance event"""
    return WalletBalanceEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="user1119",
        event_category="Wallet Balance",
        event_name="Balance Update",
        event_id="evt_wb_987654321",
        event_time="2024-01-15T14:45:00Z",
        wallet_type="main",
        currency="USD",
        current_cash_balance=1250.50,
        current_bonus_balance=100.00,
        current_total_balance=1350.50,
        blocked_amount=50.00
    )


def print_validation_result(result, event_name):
    """Print validation result"""
    # Handle both dictionary and object responses
    if isinstance(result, dict):
        is_valid = result.get('is_valid', result.get('isValid', True))
        errors = result.get('errors', [])
    else:
        is_valid = getattr(result, 'is_valid', getattr(result, 'isValid', True))
        errors = getattr(result, 'errors', [])
    
    if not is_valid:
        print(f"❌ Invalid {event_name}:")
        print(f"Errors: {json.dumps(errors)}")
    else:
        print(f"✅ Valid {event_name}")


def print_data(data):
    """Print data object"""
    class_name = data.__class__.__name__
    print(f"\n🔍 Testing: {class_name}")
    print('─' * 50)
    
    # Try different methods to get dictionary representation
    try:
        if hasattr(data, 'to_dict'):
            data_dict = data.to_dict()
        elif hasattr(data, '__dict__'):
            data_dict = data.__dict__
        else:
            data_dict = str(data)
        print(f"📋 Data: {json.dumps(data_dict, indent=2, default=str)}")
    except Exception as e:
        print(f"📋 Data: {data}")


def validate_batch_data(batch):
    """Validate batch data"""
    print('\n=== Validating BatchData contents ===\n')
    
    if batch.get('customers'):
        for c in batch['customers']:
            if isinstance(c, CustomerProfile):
                result = c.validate()
                print_validation_result(result, 'CustomerProfile (Batch)')
                print_data(c)
    
    if batch.get('extended_attributes'):
        for ea in batch['extended_attributes']:
            if isinstance(ea, CustomerExtEvent):
                result = ea.validate()
                print_validation_result(result, 'ExtendedAttributes (Batch)')
                print_data(ea)
    
    if batch.get('account_events'):
        for a in batch['account_events']:
            if isinstance(a, AccountEvent):
                result = a.validate()
                print_validation_result(result, 'AccountEvent (Batch)')
                print_data(a)
    
    if batch.get('deposit_events'):
        for d in batch['deposit_events']:
            if isinstance(d, DepositEvent):
                result = d.validate()
                print_validation_result(result, 'DepositEvent (Batch)')
                print_data(d)
    
    if batch.get('withdraw_events'):
        for w in batch['withdraw_events']:
            if isinstance(w, WithdrawEvent):
                result = w.validate()
                print_validation_result(result, 'WithdrawEvent (Batch)')
                print_data(w)
    
    if batch.get('gaming_events'):
        for g in batch['gaming_events']:
            if isinstance(g, GamingActivityEvent):
                result = g.validate()
                print_validation_result(result, 'GamingActivityEvent (Batch)')
                print_data(g)
    
    if batch.get('refer_friend_events'):
        for r in batch['refer_friend_events']:
            if isinstance(r, ReferFriendEvent):
                result = r.validate()
                print_validation_result(result, 'ReferFriendEvent (Batch)')
                print_data(r)
    
    if batch.get('wallet_balance_events'):
        for wb in batch['wallet_balance_events']:
            if isinstance(wb, WalletBalanceEvent):
                result = wb.validate()
                print_validation_result(result, 'WalletBalanceEvent (Batch)')
                print_data(wb)


def test_batch_operations(account_id, workspace_id):
    """Test batch operations"""
    print('\n=== Batch Operations ===\n')
    
    try:
        # Create batch data object using camelCase (matching SDK expectations)
        batch_data = {
    'customers': [
        create_sample_customer(account_id, workspace_id).to_dict()
    ],
    'extendedAttributes': [
        create_sample_extended_attributes_map_format(account_id, workspace_id).to_dict(),
        create_sample_extended_attributes_string_format(account_id, workspace_id).to_dict()
    ],
    'accountEvents': [
        create_sample_account_event(account_id, workspace_id).to_dict()
    ],
    'depositEvents': [
        create_sample_deposit_event(account_id, workspace_id).to_dict()
    ],
    'withdrawEvents': [
        create_sample_withdraw_event(account_id, workspace_id).to_dict()
    ],
    'gamingEvents': [
        create_sample_gaming_activity_event(account_id, workspace_id).to_dict()
    ],
    'referFriendEvents': [
        create_sample_refer_friend_event(account_id, workspace_id).to_dict()
    ],
    'walletBalanceEvents': [
        create_sample_wallet_balance_event(account_id, workspace_id).to_dict()
    ]
}

        
        # Validate batch data
        validate_batch_data({
            'customers': batch_data['customers'],
            'extended_attributes': batch_data['extendedAttributes'],
            'account_events': batch_data['accountEvents'],
            'deposit_events': batch_data['depositEvents'],
            'withdraw_events': batch_data['withdrawEvents'],
            'gaming_events': batch_data['gamingEvents'],
            'refer_friend_events': batch_data['referFriendEvents'],
            'wallet_balance_events': batch_data['walletBalanceEvents']
        })
        
        print('\n📦 Batch Data Summary:')
        print(f"   Customers: {len(batch_data.get('customers', []))}")
        print(f"   Extended Attributes: {len(batch_data.get('extendedAttributes', []))}")
        print(f"   Account Events: {len(batch_data.get('accountEvents', []))}")
        print(f"   Deposit Events: {len(batch_data.get('depositEvents', []))}")
        print(f"   Withdraw Events: {len(batch_data.get('withdrawEvents', []))}")
        print(f"   Gaming Events: {len(batch_data.get('gamingEvents', []))}")
        print(f"   Refer Friend Events: {len(batch_data.get('referFriendEvents', []))}")
        print(f"   Wallet Balance Events: {len(batch_data.get('walletBalanceEvents', []))}")
        
        print('\n📤 Sending batch request to API...\n')
        
        # Debug: Log what we're actually sending
        print('🔍 Request Payload Preview:')
        payload_preview = {
            'customers': len(batch_data.get('customers', [])),
            'extendedAttributes': len(batch_data.get('extendedAttributes', [])),
            'accountEvents': len(batch_data.get('accountEvents', [])),
            'depositEvents': len(batch_data.get('depositEvents', [])),
            'withdrawEvents': len(batch_data.get('withdrawEvents', [])),
            'gamingEvents': len(batch_data.get('gamingEvents', [])),
            'referFriendEvents': len(batch_data.get('referFriendEvents', [])),
            'walletBalanceEvents': len(batch_data.get('walletBalanceEvents', []))
        }
        print(json.dumps(payload_preview, indent=2))
        print()
        
        # Send batch request
        response = sdk.send_batch(batch_data)
        
        print('\n=== API Response ===\n')
        print('📊 Full Response Object:')
        print(json.dumps(response, indent=2, default=str))
        print('\n' + '─' * 50)
        
        if response.get('success'):
            print('\n✅ Batch operation completed successfully!')
            print(f"⏰ Timestamp: {response.get('timestamp', datetime.now().isoformat())}")
            
            # Handle different response structures - Check for 'results' property
            if response.get('results'):
                print('\n📋 Detailed Results:\n')
                
                def check_result(key, display_name):
                    """Check and display result for a specific key"""
                    if key in response['results'] and response['results'][key] is not None:
                        result = response['results'][key]
                        print(f"\n{display_name}:")
                        
                        # Try different possible response formats
                        if isinstance(result, dict):
                            if 'success' in result:
                                status = '✅' if result['success'] else '❌'
                                print(f"  {status} Result: {'Success' if result['success'] else 'Failed'}")
                            if 'status' in result:
                                print(f"  📊 HTTP Status: {result['status']}")
                            if 'error' in result:
                                print(f"  🔴 Error: {result['error']}")
                            if 'message' in result:
                                print(f"  💬 Message: {result['message']}")
                            if 'data' in result and result['data']:
                                if 'message' in result['data']:
                                    print(f"  💬 Data Message: {result['data']['message']}")
                                if 'validRecordsCount' in result['data']:
                                    print(f"  ✅ Valid Records: {result['data']['validRecordsCount']}/{result['data']['totalRecordsCount']}")
                                if 'validationErrors' in result['data'] and result['data']['validationErrors']:
                                    print(f"  ⚠️  Validation Errors:")
                                    for err in result['data']['validationErrors']:
                                        print(f"    Record {err['recordIndex']}:")
                                        for e in err['errors']:
                                            print(f"      - Field: {e['field']}")
                                            print(f"        Message: {e['message']}")
                                            print(f"        Value: {json.dumps(e['value'])}")
                            if 'count' in result:
                                print(f"  🔢 Count: {result['count']}")
                        elif isinstance(result, bool):
                            status = '✅' if result else '❌'
                            print(f"  {status} Result: {'Success' if result else 'Failed'}")
                        elif isinstance(result, str):
                            print(f"  📝 Result: {result}")
                
                check_result('customers', '👤 Customer Profiles')
                check_result('extendedAttributes', '🔧 Extended Attributes')
                check_result('accountEvents', '📝 Account Events')
                check_result('depositEvents', '💰 Deposit Events')
                check_result('withdrawEvents', '💸 Withdraw Events')
                check_result('gamingEvents', '🎮 Gaming Events')
                check_result('referFriendEvents', '👥 Refer Friend Events')
                check_result('walletBalanceEvents', '💳 Wallet Balance Events')
                
                # Check for missing results
                expected_keys = [
                    'customers', 
                    'extendedAttributes', 
                    'accountEvents', 
                    'depositEvents', 
                    'withdrawEvents', 
                    'gamingEvents', 
                    'referFriendEvents', 
                    'walletBalanceEvents'
                ]
                
                received_keys = list(response['results'].keys())
                missing_keys = [key for key in expected_keys if key not in received_keys]
                
                if missing_keys:
                    print('\n⚠️  Missing Results:')
                    for key in missing_keys:
                        print(f"   - {key}: No response received")
                
                print(f"\n📊 Summary: {len(received_keys)}/{len(expected_keys)} event types processed")
            
            elif response.get('data'):
                print('\n📋 Detailed Results (from data property):\n')
                print(json.dumps(response['data'], indent=2, default=str))
            else:
                print('\n⚠️  Warning: No results or data property in response')
        
        else:
            print('\n❌ Batch operation failed')
            print(f"📈 HTTP Status: {response.get('status')}")
            
            if response.get('error'):
                print('\n🔴 Error Details:')
                print(json.dumps(response['error'], indent=2, default=str))
            
            if response.get('data'):
                print('\n📦 Response Data:')
                print(json.dumps(response['data'], indent=2, default=str))
    
    except Exception as error:
        print('\n❌ Batch operation failed with exception:')
        print(f"Error Message: {str(error)}")
        import traceback
        traceback.print_exc()
        
        if hasattr(error, 'response'):
            print('\n📡 HTTP Response Details:')
            print(f"Status: {error.response.status_code}")
            try:
                print(f"Data: {json.dumps(error.response.json(), indent=2)}")
            except:
                print(f"Data: {error.response.text}")
    
    print('\n' + '=' * 50 + '\n')


# Run the test
if __name__ == '__main__':
    print('\n' + '=' * 50)
    print('🚀 Optikpi Data Pipeline SDK - Batch Operations Test')
    print('=' * 50)
    print(f"\n📍 Configuration:")
    print(f"   Base URL: {API_BASE_URL or 'Not set'}")
    print(f"   Account ID: {ACCOUNT_ID}")
    print(f"   Workspace ID: {WORKSPACE_ID}")
    print(f"   Auth Token: {AUTH_TOKEN[:8] + '...' if AUTH_TOKEN else 'Not set'}")
    
    test_batch_operations(ACCOUNT_ID, WORKSPACE_ID)