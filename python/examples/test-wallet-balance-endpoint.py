#!/usr/bin/env python3
"""
Test Wallet Balance Endpoint
Demonstrates how to send wallet balance event data to the Optikpi Data Pipeline API
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
from models.WalletBalanceEvent import WalletBalanceEvent

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

# Wallet balance event data
wallet = WalletBalanceEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="1wallet_py_1",
    event_category="Wallet Balance",
    event_name="Balance Updated",
    event_id="evt_wb_1234",
    event_time="2024-01-15T11:30:00Z",
    currency="USD",
    current_cash_balance=500.00,
    current_bonus_balance=50.00,
    current_total_balance=550.00,
    blocked_amount=20.00
)

validation = wallet.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Wallet balance event validated successfully!")


def test_wallet_balance_endpoint():
    """
    Test wallet balance endpoint
    
    Sends wallet balance event data and displays the response
    """
    try:
        print('🚀 Testing Wallet Balance Events Endpoint')
        print('========================================')

        print('Configuration:')
        print(f'API Base URL: {API_BASE_URL}')
        print(f'Account ID: {ACCOUNT_ID}')
        print(f'Workspace ID: {WORKSPACE_ID}')
        print(f'Auth Token: {AUTH_TOKEN[:8]}...')

        print('\nMaking API request using SDK...')
        wallet_dict = wallet.to_dict()
        print(f'Wallet Event Data: {json.dumps(wallet_dict, indent=2)}')

        start_time = time.time()
        result = sdk.send_wallet_balance_event(wallet_dict)
        end_time = time.time()

        response_time = int((end_time - start_time) * 1000)

        if result['success']:
            print('\n✅ Success!')
            print('========================================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Response Data: {json.dumps(result['data'], indent=2)}")
        else:
            print('\n❌ API Error!')
            print('========================================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Error Data: {json.dumps(result.get('data', result.get('error')), indent=2)}")

    except Exception as error:
        print('\n❌ SDK Error occurred!', file=sys.stderr)
        print('========================================', file=sys.stderr)
        print(f'Error: {str(error)}', file=sys.stderr)
        import traceback
        print(f'Stack: {traceback.format_exc()}', file=sys.stderr)


# Run the test
if __name__ == "__main__":
    test_wallet_balance_endpoint()
