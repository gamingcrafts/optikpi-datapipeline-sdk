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
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_cust1',
        username='john_doe',
        full_name='John Doe',
        first_name='John',
        last_name='Doe',
        date_of_birth='1990-01-15',
        email='john.doe@example.com',
        phone_number='+1234567890',
        gender='Male',
        country='United States',
        city='New York',
        language='en',
        currency='USD',
        
        # Preferences
        marketing_email_preference=True,
        notifications_preference=True,
        subscription='Subscribed',
        
        # Risk & Responsible gaming
        deposit_limits={'limit': 1000},
        loss_limits={'limit': 500},
        wagering_limits={'limit': 2000},
        session_time_limits={'limit': 120},
        cooling_off_period='7',
        self_exclusion_period='30',
        reality_checks_notification=True,
        
        # Account status
        account_status='Active',
        vip_status='Regular',
        loyalty_program_tiers={'tier': 'Bronze'},
        bonus_abuser=False,
        financial_risk_level='0.3',
        
        # Acquisition / Partner fields
        acquisition_source='Google Ads',
        partner_id='partner123',
        affliate_id='affiliate456',
        
        # Referral data
        referral_link_code='REF789',
        referral_limit_reached=False,
        
        # Verification & timestamps
        creation_timestamp='2024-01-15T10:30:00Z',
        phone_verification=True,
        email_verification=True,
        bank_verification=False,
        iddoc_verification=True,
        
        # Privacy
        privacy_settings={'profile_visibility': 'private'}
    )


def create_sample_extended_attributes_map_format(account_id, workspace_id):
    """Create sample extended attributes event - Map format"""
    return CustomerExtEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_ext1',
        list_name='BINGO_PREFERENCES',
        ext_data={
            'Email': 'True',
            'SMS': 'True',
            'PushNotifications': 'False'
        }
    )


def create_sample_extended_attributes_string_format(account_id, workspace_id):
    """Create sample extended attributes event - String format"""
    return CustomerExtEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_ext2',
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
        user_id='batch_py_acc1',
        event_name='Player Registration',
        event_id=f'evt_login_{int(datetime.now().timestamp() * 1000)}',
        event_time=datetime.now().isoformat(),
        device='desktop',
        status='completed',
        affiliate_id='aff_001',
        partner_id='partner_001',
        campaign_code='CAMPAIGN_001',
        reason='Registration completed successfully'
    )


def create_sample_deposit_event(account_id, workspace_id):
    """Create sample deposit event"""
    return DepositEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_dep1',
        event_category='Deposit',
        event_name='Successful Deposit',
        event_id=f'evt_{int(datetime.now().timestamp() * 1000)}',
        event_time=datetime.now().isoformat(),
        amount=100.00,
        currency='USD',
        payment_method='credit_card',
        transaction_id=f'txn_{int(datetime.now().timestamp() * 1000)}',
        status='success'
    )


def create_sample_withdraw_event(account_id, workspace_id):
    """Create sample withdraw event"""
    return WithdrawEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_wd1',
        event_name='Successful Withdrawal',
        event_id=f'evt_{int(datetime.now().timestamp() * 1000)}',
        event_time=datetime.now().isoformat(),
        amount=50.00,
        currency='USD',
        payment_method='bank',
        transaction_id=f'txn_{int(datetime.now().timestamp() * 1000)}',
        status='success'
    )


def create_sample_gaming_activity_event(account_id, workspace_id):
    """Create sample gaming activity event"""
    return GamingActivityEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_game1',
        event_name='Play Casino Game',
        event_id=f'evt_{int(datetime.now().timestamp() * 1000)}',
        event_time=datetime.now().isoformat(),
        game_id='game_001',
        game_title='Mega Slots',
        provider='ProviderXYZ',
        game_type='slots',
        wager_amount=5.00,
        currency='USD',
        device='mobile',
        session_id=f'session_{int(datetime.now().timestamp() * 1000)}'
    )


def create_sample_refer_friend_event(account_id, workspace_id):
    """Create sample refer friend event"""
    return ReferFriendEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_ref1',
        event_name='Referral Successful',
        event_id=f'evt_rf_{int(datetime.now().timestamp() * 1000)}',
        event_time=datetime.now().isoformat(),
        referral_code_used='REF123456',
        successful_referral_confirmation=True,
        reward_type='bonus',
        reward_claimed_status='claimed',
        referee_user_id='user789012',
        referee_registration_date='2024-01-15T10:30:00Z',
        referee_first_deposit=100.00
    )


def create_sample_wallet_balance_event(account_id, workspace_id):
    """Create sample wallet balance event"""
    return WalletBalanceEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        user_id='batch_py_wb1',
        event_name='Balance Update',
        event_id=f'evt_wb_{int(datetime.now().timestamp() * 1000)}',
        event_time=datetime.now().isoformat(),
        wallet_type='main',
        currency='USD',
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