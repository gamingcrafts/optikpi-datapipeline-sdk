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
    'customer': {
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
    },
    'account': {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "event_category": "Account",
        "event_name": "Player Registration",
        "event_id": "evt_123456789",
        "event_time": "2024-01-15T10:30:00Z",
        "device": "desktop",
        "status": "verified",
        "affiliate_id": "aff_123",
        "partner_id": "partner_456",
        "campaign_code": "CAMPAIGN_001",
        "reason": "Registration completed successfully"
    },
    'deposit': {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "event_category": "Deposit",
        "event_name": "Successful Deposit",
        "event_id": "evt_dep_987654321",
        "event_time": "2024-01-15T14:45:00Z",
        "amount": 500.00,
        "payment_method": "bank",
        "transaction_id": "txn_123456789",
        "payment_provider_id": "provider123",
        "payment_provider_name": "Bank Transfer"
    },
    'withdraw': {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "event_category": "Withdraw",
        "event_name": "Successful Withdrawal",
        "event_id": "evt_with_123456789",
        "event_time": "2024-01-15T15:30:00Z",
        "amount": 300.00,
        "payment_method": "bank",
        "transaction_id": "txn_123456790"
    },
    'gaming': {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "user123456",
        "event_category": "Gaming Activity",
        "event_name": "Play Casino Game",
        "event_id": "evt_gaming_123456789",
        "event_time": "2024-01-15T16:00:00Z",
        "wager_amount": 50.00,
        "win_amount": 75.00,
        "game_id": "game_123",
        "game_title": "Blackjack",
        "provider": "Provider A"
    }
}


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


def health_check():
    """
    Perform a health check on the API
    
    Returns:
        Health check result dictionary
    """
    try:
        print('\n🏥 Performing Health Check...')
        
        start_time = time.time()
        result = sdk.health_check()
        end_time = time.time()
        response_time = int((end_time - start_time) * 1000)
        
        if result['success']:
            print('✅ Health Check - SUCCESS')
            print(f"   Status: {result['status']}")
            print(f"   Response Time: {response_time}ms")
            print(f"   Response: {json.dumps(result['data'], indent=2)}")
        else:
            print('❌ Health Check - FAILED')
            print(f"   Error: {result.get('error')}")
            print(f"   Status: {result.get('status')}")
        
        return result
    
    except Exception as error:
        print('❌ Health Check - FAILED')
        print(f"   Error: {str(error)}")
        raise


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
    
    endpoints = [
        {
            'name': 'Customer Profile',
            'endpoint': '/customers',
            'data': TEST_DATA['customer'],
            'method': 'customer'
        },
        {
            'name': 'Account Event',
            'endpoint': '/events/account',
            'data': TEST_DATA['account'],
            'method': 'account'
        },
        {
            'name': 'Deposit Event',
            'endpoint': '/events/deposit',
            'data': TEST_DATA['deposit'],
            'method': 'deposit'
        },
        {
            'name': 'Withdrawal Event',
            'endpoint': '/events/withdraw',
            'data': TEST_DATA['withdraw'],
            'method': 'withdraw'
        },
        {
            'name': 'Gaming Activity',
            'endpoint': '/events/gaming-activity',
            'data': TEST_DATA['gaming'],
            'method': 'gaming'
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
        # Health check first
        health_check()
        
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