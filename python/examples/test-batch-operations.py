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
        user_id="2batchcust_py_1",
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
        risk_score_level=0.2,
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
        push_token="push_token_abc123"
    )


def create_sample_extended_attributes_map_format(account_id, workspace_id):
    """Create sample extended attributes event - Map format"""
    return CustomerExtEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="2batchce1",
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
        user_id='2batch_py_ext2',
        list_name='GAMING_PREFERENCES',
        ext_data=json.dumps({
            'Email': 'True',
            'SMS': 'True',
            'PushNotifications': 'True'
        })
    )


def create_sample_account_event(account_id, workspace_id):
    """Create sample account event"""
    return AccountEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="2batchacc1",
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
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="2batch_dep_py_1",
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
    )


def create_sample_withdraw_event(account_id, workspace_id):
    """Create sample withdraw event"""
    return WithdrawEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="2batch_wd_py_1",
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
    )


def create_sample_gaming_activity_event(account_id, workspace_id):
    """Create sample gaming activity event"""
    return GamingActivityEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="2batchgame_py_1",
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
        user_id="2batchrf1",
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


def create_sample_wallet_balance_event(account_id, workspace_id):
    """Create sample wallet balance event"""
    return WalletBalanceEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id="2batchwb1",
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
        print(f"✅ Valid {event_name}: {is_valid}")


def print_data(data):
    """Print data with class name"""
    class_name = data.__class__.__name__
    print(f"🔍 Testing : {class_name}")
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
        print(f"Note: Could not serialize to JSON: {e}")


def validate_batch_data(batch):
    """Validate batch data"""
    print('=== Validating BatchData contents ===')
    
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


def test_batch_operations(sdk, account_id, workspace_id):
    """Test batch operations"""
    print('=== Batch Operations ===')
    
    try:
        # Create batch data object (plain dictionary)
        batch_data = {
            'customers': [create_sample_customer(account_id, workspace_id)],
            'extended_attributes': [
                create_sample_extended_attributes_map_format(account_id, workspace_id),
                create_sample_extended_attributes_string_format(account_id, workspace_id)
            ],
            'account_events': [create_sample_account_event(account_id, workspace_id)],
            'deposit_events': [create_sample_deposit_event(account_id, workspace_id)],
            'withdraw_events': [create_sample_withdraw_event(account_id, workspace_id)],
            'gaming_events': [create_sample_gaming_activity_event(account_id, workspace_id)],
            'refer_friend_events': [create_sample_refer_friend_event(account_id, workspace_id)],
            'wallet_balance_events': [create_sample_wallet_balance_event(account_id, workspace_id)]
        }
        
        # Validate batch data
        validate_batch_data(batch_data)
        
        # Send batch request
        response = sdk.send_batch(batch_data)
        
        if response.get('success'):
            print('✅ Batch operation completed successfully!')
            print(f"Timestamp: {response.get('timestamp', datetime.now().isoformat())}")
            
            if response.get('data'):
                data = response['data']
                if data.get('customers'):
                    print(f"Customer profiles: {'Success' if data['customers'].get('success') else 'Failed'}")
                if data.get('extended_attributes'):
                    print(f"Extended Attributes: {'Success' if data['extended_attributes'].get('success') else 'Failed'}")
                if data.get('account_events'):
                    print(f"Account events: {'Success' if data['account_events'].get('success') else 'Failed'}")
                if data.get('deposit_events'):
                    print(f"Deposit events: {'Success' if data['deposit_events'].get('success') else 'Failed'}")
                if data.get('withdraw_events'):
                    print(f"Withdraw events: {'Success' if data['withdraw_events'].get('success') else 'Failed'}")
                if data.get('gaming_events'):
                    print(f"Gaming events: {'Success' if data['gaming_events'].get('success') else 'Failed'}")
                if data.get('refer_friend_events'):
                    print(f"Refer Friend events: {'Success' if data['refer_friend_events'].get('success') else 'Failed'}")
                if data.get('wallet_balance_events'):
                    print(f"Wallet Balance events: {'Success' if data['wallet_balance_events'].get('success') else 'Failed'}")
        else:
            print('❌ Batch operation failed')
    
    except Exception as error:
        print(f'❌ Batch operation failed: {str(error)}')
        import traceback
        traceback.print_exc()
    
    print()


# Run the test
if __name__ == '__main__':
    print('=== Optikpi Data Pipeline SDK - Batch Operations Test ===')
    print(f'Base URL: {API_BASE_URL}')
    print(f'Account ID: {ACCOUNT_ID}')
    print(f'Workspace ID: {WORKSPACE_ID}')
    print()
    
    # Run the test (no async needed)
    test_batch_operations(sdk, ACCOUNT_ID, WORKSPACE_ID)