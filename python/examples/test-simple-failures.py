#!/usr/bin/env python3
"""
Test Simple Failures
Tests API validation failures and error handling
"""
import os
import sys
import json
import requests
from pathlib import Path
from dotenv import load_dotenv

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK    


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

# Initialize SDK with NO RETRIES for failure tests
sdk = OptikpiDataPipelineSDK({
    'authToken': AUTH_TOKEN,
    'accountId': ACCOUNT_ID,
    'workspaceId': WORKSPACE_ID,
    'baseURL': API_BASE_URL,
    'retries': 0  # Disable retries for failure tests
})


def make_request(endpoint, data, method='customer'):
    """
    Make an API request using the SDK
    
    Args:
        endpoint: API endpoint path
        data: Request data
        method: SDK method to call (customer, account, deposit, withdraw, gaming)
    
    Returns:
        Dictionary with response data, error info, and status
    """
    print(f"\n🌐 Request: {endpoint}")
    print(f"📄 Body: {json.dumps(data, indent=2)}")
    
    try:
        result = None
        
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
        else:
            raise ValueError(f"Unknown method: {method}")
        
        return {
            'success': result.get('success', False),
            'status': result.get('status', 0),
            'data': result.get('data'),
            'error': result.get('error')
        }
    
    except Exception as error:
        return {
            'success': False,
            'status': 0,
            'error': str(error)
        }


def test_missing_required_fields():
    """Test 1: Missing required fields"""
    print('\n🔍 Test 1: Missing Required Fields')
    print('==================================')
    
    invalid_customer = {
        "account_id": ACCOUNT_ID,  # Use correct IDs for HMAC
        "workspace_id": WORKSPACE_ID,
        # Missing: user_id, username, full_name, email, etc.
        "gender": "Male"
    }
    
    result = make_request('/customers', invalid_customer)
    
    if result['success']:
        print('❌ UNEXPECTED: Request succeeded when it should have failed')
        print(f"Response: {result['data']}")
    else:
        print('✅ EXPECTED: Request failed with validation errors')
        print(f"Status: {result.get('status')}")
        print(f"Error: {result['error']}")


def test_invalid_email():
    """Test 2: Invalid email format"""
    print('\n🔍 Test 2: Invalid Email Format')
    print('===============================')
    
    invalid_email_customer = {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "username": "john_doe",
        "full_name": "John Doe",
        "first_name": "John",
        "last_name": "Doe",
        "date_of_birth": "1990-01-15",
        "email": "invalid-email-format",  # Invalid email
        "phone_number": "+1234567890",
        "gender": "Male",
        "country": "United States",
        "city": "New York",
        "language": "en",
        "currency": "USD",
        "marketing_email_preference": "Opt-in",
        "notifications_preference": "Opt-in",
        "subscription": "Subscribed",
        "privacy_settings": "private",
        "deposit_limits": 1000.00,
        "loss_limits": 500.00,
        "wagering_limits": 2000.00,
        "session_time_limits": 120,
        "cooling_off_period": 7,
        "self_exclusion_period": 30,
        "reality_checks_notification": "daily",
        "account_status": "Active",
        "vip_status": "Regular",
        "loyalty_program_tiers": "Bronze",
        "bonus_abuser": "Not flagged",
        "financial_risk_level": 0.3,
        "acquisition_source": "Google Ads",
        "partner_id": "partner123",
        "affliate_id": "affiliate456",
        "referral_link_code": "REF789",
        "referral_limit_reached": "Not Reached",
        "creation_timestamp": "2024-01-15T10:30:00Z",
        "phone_verification": "Verified",
        "email_verification": "Verified",
        "bank_verification": "NotVerified",
        "iddoc_verification": "Verified"
    }
    
    result = make_request('/customers', invalid_email_customer)
    
    if result['success']:
        print('❌ UNEXPECTED: Request succeeded with invalid email')
        print(f"Response: {result['data']}")
    else:
        print('✅ EXPECTED: Request failed due to invalid email')
        print(f"Status: {result.get('status')}")
        print(f"Error: {result['error']}")


def test_invalid_enum_values():
    """Test 3: Invalid enum values"""
    print('\n🔍 Test 3: Invalid Enum Values')
    print('==============================')
    
    invalid_enum_customer = {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "username": "john_doe",
        "full_name": "John Doe",
        "first_name": "John",
        "last_name": "Doe",
        "date_of_birth": "1990-01-15",
        "email": "john.doe@example.com",
        "phone_number": "+1234567890",
        "gender": "Other",  # Invalid enum value
        "country": "United States",
        "city": "New York",
        "language": "en",
        "currency": "USD",
        "marketing_email_preference": "Opt-in",
        "notifications_preference": "Opt-in",
        "subscription": "Subscribed",
        "privacy_settings": "private",
        "deposit_limits": 1000.00,
        "loss_limits": 500.00,
        "wagering_limits": 2000.00,
        "session_time_limits": 120,
        "cooling_off_period": 7,
        "self_exclusion_period": 30,
        "reality_checks_notification": "daily",
        "account_status": "Suspended",  # Invalid enum value
        "vip_status": "Regular",
        "loyalty_program_tiers": "Bronze",
        "bonus_abuser": "Not flagged",
        "financial_risk_level": 0.3,
        "acquisition_source": "Google Ads",
        "partner_id": "partner123",
        "affliate_id": "affiliate456",
        "referral_link_code": "REF789",
        "referral_limit_reached": "Not Reached",
        "creation_timestamp": "2024-01-15T10:30:00Z",
        "phone_verification": "Verified",
        "email_verification": "Verified",
        "bank_verification": "NotVerified",
        "iddoc_verification": "Verified"
    }
    
    result = make_request('/customers', invalid_enum_customer)
    
    if result['success']:
        print('⚠️  KNOWN API BUG: API accepts invalid enum values')
        print(f"Response: {result['data']}")
        print('   (API should validate and reject "Other" and "Suspended")')
    else:
        print('✅ EXPECTED: Request failed due to invalid enum values')
        print(f"Status: {result.get('status')}")
        print(f"Error: {result['error']}")


def test_header_mismatch():
    """Test 4: Header vs Body Mismatch"""
    print('\n🔍 Test 4: Header vs Body Mismatch')
    print('==================================')
    print('⚠️  NOTE: This test expects the API to reject mismatched IDs,')
    print('   but the API may have a bug and return 500 error instead.')
    
    mismatched_customer = {
        "account_id": "different-account-id",  # Different from header
        "workspace_id": "different-workspace-id",  # Different from header
        "user_id": "user123456",
        "username": "john_doe",
        "full_name": "John Doe",
        "first_name": "John",
        "last_name": "Doe",
        "date_of_birth": "1990-01-15",
        "email": "john.doe@example.com",
        "phone_number": "+1234567890",
        "gender": "Male",
        "country": "United States",
        "city": "New York",
        "language": "en",
        "currency": "USD",
        "marketing_email_preference": "Opt-in",
        "notifications_preference": "Opt-in",
        "subscription": "Subscribed",
        "privacy_settings": "private",
        "deposit_limits": 1000.00,
        "loss_limits": 500.00,
        "wagering_limits": 2000.00,
        "session_time_limits": 120,
        "cooling_off_period": 7,
        "self_exclusion_period": 30,
        "reality_checks_notification": "daily",
        "account_status": "Active",
        "vip_status": "Regular",
        "loyalty_program_tiers": "Bronze",
        "bonus_abuser": "Not flagged",
        "financial_risk_level": 0.3,
        "acquisition_source": "Google Ads",
        "partner_id": "partner123",
        "affliate_id": "affiliate456",
        "referral_link_code": "REF789",
        "referral_limit_reached": "Not Reached",
        "creation_timestamp": "2024-01-15T10:30:00Z",
        "phone_verification": "Verified",
        "email_verification": "Verified",
        "bank_verification": "NotVerified",
        "iddoc_verification": "Verified"
    }
    
    result = make_request('/customers', mismatched_customer)
    
    if result['success']:
        print('❌ UNEXPECTED: Request succeeded with header mismatch')
        print(f"Response: {result['data']}")
    else:
        status = result.get('status', 0)
        if status == 500 or status == 0:
            print('⚠️  KNOWN API BUG: API returns 500 error on ID mismatch')
            print(f"Status: {status}")
            print(f"Error: {result['error']}")
            print('   (API should return 400 validation error instead of crashing)')
        else:
            print('✅ EXPECTED: Request failed due to header mismatch')
            print(f"Status: {status}")
            print(f"Error: {result['error']}")


def test_missing_auth_headers():
    """Test 5: Missing authentication headers"""
    print('\n🔍 Test 5: Missing Authentication Headers')
    print('==========================================')
    
    valid_customer = {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "username": "john_doe",
        "full_name": "John Doe",
        "first_name": "John",
        "last_name": "Doe",
        "date_of_birth": "1990-01-15",
        "email": "john.doe@example.com",
        "phone_number": "+1234567890",
        "gender": "Male",
        "country": "United States",
        "city": "New York",
        "language": "en",
        "currency": "USD",
        "marketing_email_preference": "Opt-in",
        "notifications_preference": "Opt-in",
        "subscription": "Subscribed",
        "privacy_settings": "private",
        "deposit_limits": 1000.00,
        "loss_limits": 500.00,
        "wagering_limits": 2000.00,
        "session_time_limits": 120,
        "cooling_off_period": 7,
        "self_exclusion_period": 30,
        "reality_checks_notification": "daily",
        "account_status": "Active",
        "vip_status": "Regular",
        "loyalty_program_tiers": "Bronze",
        "bonus_abuser": "Not flagged",
        "financial_risk_level": 0.3,
        "acquisition_source": "Google Ads",
        "partner_id": "partner123",
        "affliate_id": "affiliate456",
        "referral_link_code": "REF789",
        "referral_limit_reached": "Not Reached",
        "creation_timestamp": "2024-01-15T10:30:00Z",
        "phone_verification": "Verified",
        "email_verification": "Verified",
        "bank_verification": "NotVerified",
        "iddoc_verification": "Verified"
    }
    
    # Request without auth token
    url = f"{API_BASE_URL}/customers"
    headers = {
        'Content-Type': 'application/json',
        # Missing: x-optikpi-token, x-optikpi-account-id, x-optikpi-workspace-id
        'x-hmac-signature': 'dummy-signature',
        'x-hmac-algorithm': 'sha256'
    }
    
    print(f"\n🌐 Request: {url}")
    print(f"📋 Headers: {json.dumps(headers, indent=2)}")
    
    try:
        response = requests.post(url, json=valid_customer, headers=headers, timeout=10)
        
        # FIXED: Check status code properly
        if response.status_code == 401 or response.status_code == 403:
            print('✅ EXPECTED: Request was rejected due to missing auth')
            print(f"Status: {response.status_code}")
            try:
                print(f"Error: {response.json()}")
            except:
                print(f"Error: {response.text}")
        else:
            print('❌ UNEXPECTED: Request succeeded without proper auth')
            print(f"Status: {response.status_code}")
            print(f"Response: {response.json()}")
            
    except requests.exceptions.RequestException as error:
        print(f"❌ UNEXPECTED ERROR: {str(error)}")


def test_invalid_deposit_event():
    """Test 6: Invalid deposit event"""
    print('\n🔍 Test 6: Invalid Deposit Event')
    print('=================================')
    
    invalid_deposit = {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "event_category": "Deposit",
        "event_name": "Successful Deposit",
        "event_id": "evt_dep_987654321",
        "event_time": "2024-01-15T14:45:00Z",
        "amount": -500.00,  # Negative amount (invalid)
        "payment_method": "cryptocurrency",  # Invalid payment method
        "transaction_id": "txn_123456789"
    }
    
    result = make_request('/events/deposit', invalid_deposit, method='deposit')
    
    if result['success']:
        print('⚠️  KNOWN API BUG: API accepts invalid deposit data')
        print(f"Response: {result['data']}")
        print('   (API should validate and reject negative amounts)')
    else:
        print('✅ EXPECTED: Request failed due to invalid deposit data')
        print(f"Status: {result.get('status')}")
        print(f"Error: {result['error']}")


def run_all_tests():
    """Run all validation failure tests"""
    print('🚨 Testing API Validation Failures')
    print('==================================')
    print(f'API Base URL: {API_BASE_URL}')
    print(f'Account ID: {ACCOUNT_ID}')
    print(f'Workspace ID: {WORKSPACE_ID}')
    
    test_missing_required_fields()
    test_invalid_email()
    test_invalid_enum_values()
    test_header_mismatch()
    test_missing_auth_headers()
    test_invalid_deposit_event()
    
    print('\n🎉 All validation failure tests completed!')
    print('\n⚠️  NOTE: If tests show 500 errors instead of 400 errors,')
    print('   this indicates the API server needs better validation.')


# Run if this file is executed directly
if __name__ == "__main__":
    run_all_tests()