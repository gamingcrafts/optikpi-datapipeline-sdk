#!/usr/bin/env python3
"""
Test Customer Endpoint
Demonstrates how to send customer profile data to the Optikpi Data Pipeline API
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
from models.CustomerProfile import CustomerProfile


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

# Customer data
customer = CustomerProfile(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="cust_py_1",
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
validation = customer.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Customer event validated successfully!")


# SDK handles HMAC authentication automatically


def test_customer_endpoint():
    """
    Test customer endpoint
    
    Sends customer profile data to the Optikpi Data Pipeline API
    and displays the response
    """
    try:
        print('🚀 Testing Customer Endpoint')
        print('============================')
        
        print('Configuration:')
        print(f'API Base URL: {API_BASE_URL}')
        print(f'Account ID: {ACCOUNT_ID}')
        print(f'Workspace ID: {WORKSPACE_ID}')
        print(f'Auth Token: {AUTH_TOKEN[:8]}...')
        
        print('\nMaking API request using SDK...')
        customer_dict = customer.to_dict()
        print(f'Customer Event Data: {json.dumps(customer_dict, indent=2)}')
        
        # Make the API call using SDK
        start_time = time.time()
        result = sdk.send_customer_profile(customer_dict)
        end_time = time.time()
        
        response_time = int((end_time - start_time) * 1000)
        
        if result['success']:
            print('\n✅ Success!')
            print('============================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Response Data: {json.dumps(result['data'], indent=2)}")
        else:
            print('\n❌ API Error!')
            print('============================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Error Data: {json.dumps(result.get('data', result.get('error')), indent=2)}")
    
    except Exception as error:
        print('\n❌ SDK Error occurred!', file=sys.stderr)
        print('============================', file=sys.stderr)
        print(f'Error: {str(error)}', file=sys.stderr)
        import traceback
        print(f'Stack: {traceback.format_exc()}', file=sys.stderr)


# Run the test
if __name__ == "__main__":
    test_customer_endpoint()