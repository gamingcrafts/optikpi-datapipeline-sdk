#!/usr/bin/env python3
"""
Test System Events Endpoint
Demonstrates how to send system event data to the Optikpi Data Pipeline API
"""
import os
import sys
import json
import time
from pathlib import Path
from datetime import datetime
from dotenv import load_dotenv

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK
from models.SystemEvent import SystemEvent


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

def test_system_event_with_object(sdk, account_id, workspace_id):
    """Test system event with object (dict)"""
    print("📋 Test 1: event_data as object")
    event = SystemEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        event_category="SystemEvent",
        event_name="Campaign Trigger",
        event_id=f"evt_sys_obj_{int(time.time())}",
        event_time=datetime.now().isoformat() + "Z",
        event_data={
            "campaign_id": "camp_001",
            "action": "start",
            "segment": "vip"
        }
    )

    validation = event.validate()
    if not validation.get("isValid", False):
        print(f"❌ Validation failed! Errors: {validation.get('errors')}")
        return

    print("✅ System event object validated successfully!")
    execute_request(sdk, event)

def test_system_event_with_json_string(sdk, account_id, workspace_id):
    """Test system event with JSON string"""
    print("\n📋 Test 2: event_data as JSON string")
    event = SystemEvent(
        account_id=account_id,
        workspace_id=workspace_id,
        event_category="SystemEvent",
        event_name="Manual Action",
        event_id=f"evt_sys_str_{int(time.time()) + 1}",
        event_time=datetime.now().isoformat() + "Z",
        event_data='{"action":"notify","target":"user_list_1","payload":{}}'
    )

    validation = event.validate()
    if not validation.get("isValid", False):
        print(f"❌ Validation failed! Errors: {validation.get('errors')}")
        return

    print("✅ System event with JSON string validated successfully!")
    execute_request(sdk, event)

def execute_request(sdk, event):
    """Execute the API request and print results"""
    try:
        print('Making API request using SDK...')
        payload = event.to_dict()
        print(f'Payload: {json.dumps(payload, indent=2)}')
        
        start_time = time.time()
        result = sdk.send_system_event(payload)
        end_time = time.time()
        
        response_time = int((end_time - start_time) * 1000)
        
        print(f"⏱ Response Time: {response_time}ms")
        print(f"HTTP Status: {result['status']}")

        if result['success']:
            print('✅ SUCCESS!')
            print(f"Response: {json.dumps(result['data'], indent=2)}")
        else:
            print('❌ FAILED!')
            print(f"Error: {json.dumps(result.get('data', result.get('error')), indent=2)}")
    
    except Exception as error:
        print(f'\n❌ SDK Error: {str(error)}', file=sys.stderr)

# Run the tests
if __name__ == "__main__":
    print('🚀 Testing System (Back Office) Endpoint')
    print('========================================')
    print(f'📌 API Base URL: {API_BASE_URL}')
    print(f'👤 Account ID: {ACCOUNT_ID}')
    print(f'🏢 Workspace ID: {WORKSPACE_ID}')
    print()

    test_system_event_with_object(sdk, ACCOUNT_ID, WORKSPACE_ID)
    print("\n" + "-"*48 + "\n")
    test_system_event_with_json_string(sdk, ACCOUNT_ID, WORKSPACE_ID)

