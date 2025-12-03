#!/usr/bin/env python3
"""
Test Simple Failures - Comprehensive Version
Tests API validation failures and error handling with extensive test cases
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
from utils.crypto import generate_hmac_signature

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
    'retries': 0
})

# Valid base data for comparison
VALID_CUSTOMER = {
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

# Test cases for validation failures
VALIDATION_TEST_CASES = {
    # Customer validation failures
    "customer": {
        "missing_required_fields": {
            "name": "Missing Required Fields",
            "description": "Customer data missing essential required fields",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "gender": "Male"
            }
        },
        "invalid_email_format": {
            "name": "Invalid Email Format",
            "description": "Customer data with invalid email format",
            "data": {**VALID_CUSTOMER, "email": "invalid-email-format"}
        },
        "invalid_gender_enum": {
            "name": "Invalid Gender Enum",
            "description": "Customer data with invalid gender value",
            "data": {**VALID_CUSTOMER, "gender": "Other"}
        },
        "invalid_date_format": {
            "name": "Invalid Date Format",
            "description": "Customer data with invalid date format",
            "data": {**VALID_CUSTOMER, "date_of_birth": "15-01-1990"}
        },
        "invalid_phone_format": {
            "name": "Invalid Phone Format",
            "description": "Customer data with invalid phone number format",
            "data": {**VALID_CUSTOMER, "phone_number": "1234567890"}
        },
        "invalid_account_status": {
            "name": "Invalid Account Status",
            "description": "Customer data with invalid account status",
            "data": {**VALID_CUSTOMER, "account_status": "Suspended"}
        },
        "invalid_verification_status": {
            "name": "Invalid Verification Status",
            "description": "Customer data with invalid verification status",
            "data": {**VALID_CUSTOMER, "email_verification": "Pending"}
        },
        "invalid_financial_risk_level": {
            "name": "Invalid Financial Risk Level",
            "description": "Customer data with invalid financial risk level",
            "data": {**VALID_CUSTOMER, "financial_risk_level": 1.5}
        },
        "invalid_limits_type": {
            "name": "Invalid Limits Type",
            "description": "Customer data with invalid deposit limits type",
            "data": {**VALID_CUSTOMER, "deposit_limits": "1000"}
        },
        "header_account_id_mismatch": {
            "name": "Header Account ID Mismatch",
            "description": "Account ID in header doesn't match body",
            "data": {**VALID_CUSTOMER, "account_id": "different-account-id"}
        },
        "header_workspace_id_mismatch": {
            "name": "Header Workspace ID Mismatch",
            "description": "Workspace ID in header doesn't match body",
            "data": {**VALID_CUSTOMER, "workspace_id": "different-workspace-id"}
        }
    },
    
    # Account event validation failures
    "account": {
        "missing_required_fields": {
            "name": "Missing Required Fields",
            "description": "Account event missing essential required fields",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "device": "desktop"
            }
        },
        "invalid_event_name": {
            "name": "Invalid Event Name",
            "description": "Account event with invalid event name",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Account",
                "event_name": "Invalid Event",
                "event_id": "evt_123456789",
                "event_time": "2024-01-15T10:30:00Z",
                "device": "desktop",
                "status": "verified"
            }
        },
        "invalid_device_type": {
            "name": "Invalid Device Type",
            "description": "Account event with invalid device type",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Account",
                "event_name": "Player Registration",
                "event_id": "evt_123456789",
                "event_time": "2024-01-15T10:30:00Z",
                "device": "smartwatch",
                "status": "verified"
            }
        },
        "invalid_status": {
            "name": "Invalid Status",
            "description": "Account event with invalid status",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Account",
                "event_name": "Player Registration",
                "event_id": "evt_123456789",
                "event_time": "2024-01-15T10:30:00Z",
                "device": "desktop",
                "status": "pending"
            }
        },
        "invalid_event_time_format": {
            "name": "Invalid Event Time Format",
            "description": "Account event with invalid event time format",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Account",
                "event_name": "Player Registration",
                "event_id": "evt_123456789",
                "event_time": "2024-01-15 10:30:00",
                "device": "desktop",
                "status": "verified"
            }
        }
    },
    
    # Deposit event validation failures
    "deposit": {
        "missing_required_fields": {
            "name": "Missing Required Fields",
            "description": "Deposit event missing essential required fields",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "payment_method": "bank"
            }
        },
        "invalid_amount_type": {
            "name": "Invalid Amount Type",
            "description": "Deposit event with invalid amount type",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Deposit",
                "event_name": "Successful Deposit",
                "event_id": "evt_dep_987654321",
                "event_time": "2024-01-15T14:45:00Z",
                "amount": "500.00",
                "payment_method": "bank",
                "transaction_id": "txn_123456789"
            }
        },
        "negative_amount": {
            "name": "Negative Amount",
            "description": "Deposit event with negative amount",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Deposit",
                "event_name": "Successful Deposit",
                "event_id": "evt_dep_987654321",
                "event_time": "2024-01-15T14:45:00Z",
                "amount": -500.00,
                "payment_method": "bank",
                "transaction_id": "txn_123456789"
            }
        },
        "invalid_payment_method": {
            "name": "Invalid Payment Method",
            "description": "Deposit event with invalid payment method",
            "data": {
                "account_id": ACCOUNT_ID,
                "workspace_id": WORKSPACE_ID,
                "user_id": "user123456",
                "event_category": "Deposit",
                "event_name": "Successful Deposit",
                "event_id": "evt_dep_987654321",
                "event_time": "2024-01-15T14:45:00Z",
                "amount": 500.00,
                "payment_method": "cryptocurrency",
                "transaction_id": "txn_123456789"
            }
        }
    }
}


def make_sdk_request(endpoint, data, method='customer'):
    """Make an API request using the SDK"""
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


def test_validation_failures():
    """Test validation failures for all endpoints"""
    print('🚨 Testing API Validation Failures')
    print('==================================')
    print(f'API Base URL: {API_BASE_URL}')
    print(f'Account ID: {ACCOUNT_ID}')
    print(f'Workspace ID: {WORKSPACE_ID}')
    
    results = []
    
    # Test customer validation failures
    print('\n👤 Testing Customer Validation Failures')
    print('=======================================')
    
    for test_key, test_case in VALIDATION_TEST_CASES['customer'].items():
        print(f"\n🔍 Testing: {test_case['name']}")
        print(f"📝 Description: {test_case['description']}")
        print('─' * 60)
        
        result = make_sdk_request('/customers', test_case['data'], 'customer')
        
        if result['success']:
            print(f"⚠️  UNEXPECTED SUCCESS: {test_case['name']}")
            print(f"   Status: {result['status']}")
            print(f"   Response: {json.dumps(result['data'], indent=2)}")
            
            results.append({
                'test_key': test_key,
                'endpoint': 'customer',
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'SUCCESS',
                'status': result['status'],
                'data': result['data']
            })
        else:
            status = result.get('status', 0)
            if status == 500 or status == 0:
                print(f"⚠️  API ERROR: {test_case['name']}")
                print(f"   Status: {status}")
                print(f"   Error: {result['error']}")
            else:
                print(f"✅ EXPECTED FAILURE: {test_case['name']}")
                print(f"   HTTP Status: {status}")
                print(f"   Error: {result['error']}")
            
            results.append({
                'test_key': test_key,
                'endpoint': 'customer',
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'FAILURE',
                'http_status': status,
                'error': result['error']
            })
    
    # Test account validation failures
    print('\n📊 Testing Account Event Validation Failures')
    print('============================================')
    
    for test_key, test_case in VALIDATION_TEST_CASES['account'].items():
        print(f"\n🔍 Testing: {test_case['name']}")
        print(f"📝 Description: {test_case['description']}")
        print('─' * 60)
        
        result = make_sdk_request('/events/account', test_case['data'], 'account')
        
        if result['success']:
            print(f"⚠️  UNEXPECTED SUCCESS: {test_case['name']}")
            print(f"   Status: {result['status']}")
            print(f"   Response: {json.dumps(result['data'], indent=2)}")
            
            results.append({
                'test_key': test_key,
                'endpoint': 'account',
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'SUCCESS',
                'status': result['status'],
                'data': result['data']
            })
        else:
            status = result.get('status', 0)
            if status == 500 or status == 0:
                print(f"⚠️  API ERROR: {test_case['name']}")
                print(f"   Status: {status}")
                print(f"   Error: {result['error']}")
            else:
                print(f"✅ EXPECTED FAILURE: {test_case['name']}")
                print(f"   HTTP Status: {status}")
                print(f"   Error: {result['error']}")
            
            results.append({
                'test_key': test_key,
                'endpoint': 'account',
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'FAILURE',
                'http_status': status,
                'error': result['error']
            })
    
    # Test deposit validation failures
    print('\n💰 Testing Deposit Event Validation Failures')
    print('============================================')
    
    for test_key, test_case in VALIDATION_TEST_CASES['deposit'].items():
        print(f"\n🔍 Testing: {test_case['name']}")
        print(f"📝 Description: {test_case['description']}")
        print('─' * 60)
        
        result = make_sdk_request('/events/deposit', test_case['data'], 'deposit')
        
        if result['success']:
            print(f"⚠️  UNEXPECTED SUCCESS: {test_case['name']}")
            print(f"   Status: {result['status']}")
            print(f"   Response: {json.dumps(result['data'], indent=2)}")
            
            results.append({
                'test_key': test_key,
                'endpoint': 'deposit',
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'SUCCESS',
                'status': result['status'],
                'data': result['data']
            })
        else:
            status = result.get('status', 0)
            if status == 500 or status == 0:
                print(f"⚠️  API ERROR: {test_case['name']}")
                print(f"   Status: {status}")
                print(f"   Error: {result['error']}")
            else:
                print(f"✅ EXPECTED FAILURE: {test_case['name']}")
                print(f"   HTTP Status: {status}")
                print(f"   Error: {result['error']}")
            
            results.append({
                'test_key': test_key,
                'endpoint': 'deposit',
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'FAILURE',
                'http_status': status,
                'error': result['error']
            })
    
    # Summary
    print('\n📊 Validation Test Summary')
    print('==========================')
    
    expected_failures = len([r for r in results if r['expected'] == 'FAILURE' and r['actual'] == 'FAILURE'])
    unexpected_successes = len([r for r in results if r['expected'] == 'FAILURE' and r['actual'] == 'SUCCESS'])
    total_tests = len(results)
    
    print(f"✅ Expected Failures: {expected_failures}")
    print(f"⚠️  Unexpected Successes: {unexpected_successes}")
    print(f"📈 Total Tests: {total_tests}")
    print(f"🎯 Validation Effectiveness: {((expected_failures / total_tests) * 100):.1f}%")
    
    if unexpected_successes > 0:
        print('\n⚠️  Tests that should have failed but succeeded:')
        for result in [r for r in results if r['expected'] == 'FAILURE' and r['actual'] == 'SUCCESS']:
            print(f"   - {result['test_name']} ({result['endpoint']})")
    
    return results


def test_authentication_failures():
    """Test authentication failures"""
    print('\n🔐 Testing Authentication Failures')
    print('===================================')
    
    auth_test_cases = [
        {
            "name": "Missing Auth Token",
            "description": "Request without x-optikpi-token header",
            "headers": {
                'Content-Type': 'application/json',
                'x-optikpi-account-id': ACCOUNT_ID,
                'x-optikpi-workspace-id': WORKSPACE_ID,
                'x-hmac-signature': 'dummy-signature',
                'x-hmac-algorithm': 'sha256'
            }
        },
        {
            "name": "Invalid Auth Token",
            "description": "Request with invalid auth token",
            "headers": {
                'Content-Type': 'application/json',
                'x-optikpi-token': 'invalid-token',
                'x-optikpi-account-id': ACCOUNT_ID,
                'x-optikpi-workspace-id': WORKSPACE_ID,
                'x-hmac-signature': 'dummy-signature',
                'x-hmac-algorithm': 'sha256'
            }
        },
        {
            "name": "Missing Account ID Header",
            "description": "Request without x-optikpi-account-id header",
            "headers": {
                'Content-Type': 'application/json',
                'x-optikpi-token': AUTH_TOKEN,
                'x-optikpi-workspace-id': WORKSPACE_ID,
                'x-hmac-signature': 'dummy-signature',
                'x-hmac-algorithm': 'sha256'
            }
        },
        {
            "name": "Missing Workspace ID Header",
            "description": "Request without x-optikpi-workspace-id header",
            "headers": {
                'Content-Type': 'application/json',
                'x-optikpi-token': AUTH_TOKEN,
                'x-optikpi-account-id': ACCOUNT_ID,
                'x-hmac-signature': 'dummy-signature',
                'x-hmac-algorithm': 'sha256'
            }
        },
        {
            "name": "Invalid HMAC Signature",
            "description": "Request with invalid HMAC signature",
            "headers": {
                'Content-Type': 'application/json',
                'x-optikpi-token': AUTH_TOKEN,
                'x-optikpi-account-id': ACCOUNT_ID,
                'x-optikpi-workspace-id': WORKSPACE_ID,
                'x-hmac-signature': 'invalid-signature',
                'x-hmac-algorithm': 'sha256'
            }
        }
    ]
    
    results = []
    
    for test_case in auth_test_cases:
        print(f"\n🔍 Testing: {test_case['name']}")
        print(f"📝 Description: {test_case['description']}")
        print('─' * 60)
        
        url = f"{API_BASE_URL}/customers"
        print(f"🌐 Making request to: {url}")
        print(f"📋 Headers: {json.dumps(test_case['headers'], indent=2)}")
        
        try:
            response = requests.post(url, json=VALID_CUSTOMER, headers=test_case['headers'], timeout=10)
            
            if response.status_code in [401, 403]:
                print(f"✅ EXPECTED FAILURE: {test_case['name']}")
                print(f"   Status: {response.status_code}")
                try:
                    print(f"   Error: {json.dumps(response.json(), indent=2)}")
                except:
                    print(f"   Error: {response.text}")
                
                results.append({
                    'test_name': test_case['name'],
                    'expected': 'FAILURE',
                    'actual': 'FAILURE',
                    'http_status': response.status_code,
                    'error': response.json() if response.text else None
                })
            else:
                print(f"⚠️  UNEXPECTED SUCCESS: {test_case['name']}")
                print(f"   Status: {response.status_code}")
                print(f"   Response: {json.dumps(response.json(), indent=2)}")
                
                results.append({
                    'test_name': test_case['name'],
                    'expected': 'FAILURE',
                    'actual': 'SUCCESS',
                    'status': response.status_code,
                    'data': response.json()
                })
        
        except requests.exceptions.RequestException as error:
            print(f"❌ UNEXPECTED ERROR: {test_case['name']}")
            print(f"   Error: {str(error)}")
            
            results.append({
                'test_name': test_case['name'],
                'expected': 'FAILURE',
                'actual': 'ERROR',
                'error': str(error)
            })
    
    return results


def run_failure_tests():
    """Run all failure tests"""
    try:
        print('🚨 Starting Validation and Authentication Failure Tests')
        print('========================================================')
        
        # Test validation failures
        validation_results = test_validation_failures()
        
        # Test authentication failures
        auth_results = test_authentication_failures()
        
        print('\n🎉 All failure tests completed!')
        
        return {
            'validation': validation_results,
            'authentication': auth_results
        }
    except Exception as error:
        print(f'\n💥 Test execution failed: {str(error)}', file=sys.stderr)
        raise


# Run if this file is executed directly
if __name__ == "__main__":
    run_failure_tests()