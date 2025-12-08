#!/usr/bin/env python3
"""
Test Withdraw Endpoint
Demonstrates how to send withdraw event data to the Optikpi Data Pipeline API
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
from models.WithdrawEvent import WithdrawEvent


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

# Withdraw event data
withdraw = WithdrawEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="wd_py_1",
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
    net_amount=248.50,
    withdrawal_reason="Cash out winnings",
    processing_time="2024-01-15T10:30:00Z",
    failure_reason=None
)

validation = withdraw.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Withdraw event validated successfully!")	


def test_withdraw_endpoint():
    """
    Test withdraw endpoint
    
    Sends withdraw event data to the Optikpi Data Pipeline API
    and displays the response
    """
    try:
        print('🚀 Testing Withdraw Events Endpoint')
        print('==================================')
        
        print('Configuration:')
        print(f'API Base URL: {API_BASE_URL}')
        print(f'Account ID: {ACCOUNT_ID}')
        print(f'Workspace ID: {WORKSPACE_ID}')
        print(f'Auth Token: {AUTH_TOKEN[:8]}...')
        
        print('\nMaking API request using SDK...')
        withdraw_dict = withdraw.to_dict()
        print(f'Customer Event Data: {json.dumps(withdraw_dict, indent=2)}')
        
        # Make the API call using SDK
        start_time = time.time()
        result = sdk.send_withdraw_event(withdraw_dict)
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
    test_withdraw_endpoint()