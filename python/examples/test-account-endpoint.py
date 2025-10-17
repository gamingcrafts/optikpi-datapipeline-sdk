#!/usr/bin/env python3
"""
Test Account Events Endpoint
Demonstrates how to send account event data to the Optikpi Data Pipeline API
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


def main():
    """Main test function"""
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
    
    # Account event data
    ACCOUNT_EVENT = {
        "account_id": ACCOUNT_ID,
        "workspace_id": WORKSPACE_ID,
        "user_id": "chandru123",
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
    }
    
    # Test account endpoint
    test_account_endpoint(sdk, ACCOUNT_EVENT, API_BASE_URL, ACCOUNT_ID, WORKSPACE_ID, AUTH_TOKEN)


def test_account_endpoint(sdk, account_event, api_base_url, account_id, workspace_id, auth_token):
    """
    Test account endpoint
    
    Args:
        sdk: OptikpiDataPipelineSDK instance
        account_event: Account event data dictionary
        api_base_url: API base URL
        account_id: Account ID
        workspace_id: Workspace ID
        auth_token: Authentication token
    """
    try:
        print('🚀 Testing Account Events Endpoint')
        print('==================================')
        
        print('Configuration:')
        print(f'API Base URL: {api_base_url}')
        print(f'Account ID: {account_id}')
        print(f'Workspace ID: {workspace_id}')
        print(f'Auth Token: {auth_token[:8]}...')
        
        print('\nMaking API request using SDK...')
        print(f'Account Event Data: {json.dumps(account_event, indent=2)}')
        
        # Make the API call using SDK
        start_time = time.time()
        result = sdk.send_account_event(account_event)
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
    main()