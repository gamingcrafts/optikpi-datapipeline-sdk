#!/usr/bin/env python3
"""
Test Refer Friend Endpoint
Demonstrates how to send refer friend event data to the Optikpi Data Pipeline API
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

# Refer friend event data
ref_event = ReferFriendEvent(
    account_id=ACCOUNT_ID,
    workspace_id=WORKSPACE_ID,
    user_id="ref_py_1",
    event_category="Refer Friend",
    event_name="Referral Successful",
    event_id="evt_rf_987654",
    event_time="2024-01-15T15:45:00Z",
    referral_code_used="REF123456",
    successful_referral_confirmation=True,
    reward_type="bonus",
    reward_claimed_status="claimed",
    referee_user_id="user789012",
    referee_registration_date="2024-01-15T10:30:00Z",
    referee_first_deposit=100.00
)

validation = ref_event.validate()

if not validation.get("isValid", False):
    print("❌ Validation errors:", validation.get("errors", []))
    sys.exit(1)

print("✅ Refer friend event validated successfully!")


def test_refer_friend_endpoint():
    """
    Test refer friend endpoint
    
    Sends refer friend event data and displays the response
    """
    try:
        print('🚀 Testing Refer Friend Events Endpoint')
        print('=======================================')

        print('Configuration:')
        print(f'API Base URL: {API_BASE_URL}')
        print(f'Account ID: {ACCOUNT_ID}')
        print(f'Workspace ID: {WORKSPACE_ID}')
        print(f'Auth Token: {AUTH_TOKEN[:8]}...')

        print('\nMaking API request using SDK...')
        ref_dict = ref_event.to_dict()
        print(f'Refer Friend Event Data: {json.dumps(ref_dict, indent=2)}')

        start_time = time.time()
        result = sdk.send_refer_friend_event(ref_dict)
        end_time = time.time()

        response_time = int((end_time - start_time) * 1000)

        if result['success']:
            print('\n✅ Success!')
            print('=======================================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Response Data: {json.dumps(result['data'], indent=2)}")
        else:
            print('\n❌ API Error!')
            print('=======================================')
            print(f"HTTP Status: {result['status']}")
            print(f"Response Time: {response_time}ms")
            print(f"SDK Success: {result['success']}")
            print(f"Error Data: {json.dumps(result.get('data', result.get('error')), indent=2)}")

    except Exception as error:
        print('\n❌ SDK Error occurred!', file=sys.stderr)
        print('=======================================')
        print(f'Error: {str(error)}', file=sys.stderr)
        import traceback
        print(f'Stack: {traceback.format_exc()}', file=sys.stderr)


# Run the test
if __name__ == "__main__":
    test_refer_friend_endpoint()
