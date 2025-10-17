"""
Optikpi Data Pipeline SDK - Customer Extension Test
"""

import os
import sys
import json
import asyncio
from pathlib import Path
from datetime import datetime, timezone
from time import sleep

from dotenv import load_dotenv
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK

# Load environment variables from .env
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

# Extended attributes event data - Format 1: Object (auto-converted to JSON string)
EXTATTRIBUTES_EVENT_OBJECT = {
    "account_id": ACCOUNT_ID,
    "workspace_id": WORKSPACE_ID,
    "user_id": "KLT345345",
    "list_name": "BINGO_PREFERENCES",
    "ext_data": {
        "Email": "True",
        "SMS": "True",
        "PushNotifications": "False"
    }
}

# Extended attributes event data - Format 2: JSON string (legacy)
EXTATTRIBUTES_EVENT_STRING = {
    "account_id": ACCOUNT_ID,
    "workspace_id": WORKSPACE_ID,
    "user_id": "KLT345346",
    "list_name": "GAMING_PREFERENCES",
    "ext_data": json.dumps({
        "Email": "True",
        "SMS": "True",
        "PushNotifications": "True"
    })
}

# Test customer extension endpoint with both formats
async def test_customer_ext_endpoint():
    try:
        print("👤 Testing Customer Extension Events Endpoint - Both Formats")
        print("============================================================")

        print("Configuration:")
        print(f"API Base URL: {API_BASE_URL}")
        print(f"Account ID: {ACCOUNT_ID}")
        print(f"Workspace ID: {WORKSPACE_ID}")
        print(f"Auth Token: {AUTH_TOKEN[:8]}...")

        # Format 1: Object format
        print("\n📋 Testing Format 1: Object Format (auto-converted to JSON string)")
        print("================================================================")
        print("Extended Attributes Data (Object):", json.dumps(EXTATTRIBUTES_EVENT_OBJECT, indent=2))

        start_time1 = datetime.now(timezone.utc)
        result1 = sdk.send_extended_attributes(EXTATTRIBUTES_EVENT_OBJECT)
        end_time1 = datetime.now(timezone.utc)
        elapsed1 = int((end_time1 - start_time1).total_seconds() * 1000)

        if result1.get("success"):
            print("\n✅ Format 1 Success!")
            print(f"HTTP Status: {result1.get('status')}")
            print(f"Response Time: {elapsed1}ms")
            print(f"SDK Success: {result1.get('success')}")
            print("Response Data:", json.dumps(result1.get("data"), indent=2))
        else:
            print("\n❌ Format 1 API Error!")
            print(f"HTTP Status: {result1.get('status')}")
            print(f"Response Time: {elapsed1}ms")
            print(f"SDK Success: {result1.get('success')}")
            print("Error:", result1.get("error"))
            print("Error Data:", json.dumps(result1.get("data"), indent=2))

        # Wait 1 second between tests
        await asyncio.sleep(1)

        # Format 2: JSON string format
        print("\n📋 Testing Format 2: JSON String Format (legacy)")
        print("===============================================")
        print("Extended Attributes Data (String):", json.dumps(EXTATTRIBUTES_EVENT_STRING, indent=2))

        start_time2 = datetime.now(timezone.utc)
        result2 =sdk.send_extended_attributes(EXTATTRIBUTES_EVENT_STRING)
        end_time2 = datetime.now(timezone.utc)
        elapsed2 = int((end_time2 - start_time2).total_seconds() * 1000)

        if result2.get("success"):
            print("\n✅ Format 2 Success!")
            print(f"HTTP Status: {result2.get('status')}")
            print(f"Response Time: {elapsed2}ms")
            print(f"SDK Success: {result2.get('success')}")
            print("Response Data:", json.dumps(result2.get("data"), indent=2))
        else:
            print("\n❌ Format 2 API Error!")
            print(f"HTTP Status: {result2.get('status')}")
            print(f"Response Time: {elapsed2}ms")
            print(f"SDK Success: {result2.get('success')}")
            print("Error:", result2.get("error"))
            print("Error Data:", json.dumps(result2.get("data"), indent=2))

        # Summary
        print("\n📊 Test Summary")
        print("================")
        print(f"Format 1 (Object): {'✅ PASSED' if result1.get('success') else '❌ FAILED'}")
        print(f"Format 2 (String): {'✅ PASSED' if result2.get('success') else '❌ FAILED'}")

    except Exception as e:
        print("\n❌ SDK Error occurred!")
        print("============================================")
        print("Error:", str(e))
        import traceback
        traceback.print_exc()


# Run test if executed directly
if __name__ == "__main__":
    asyncio.run(test_customer_ext_endpoint())


# Expose for imports
__all__ = ["test_customer_ext_endpoint", "EXTATTRIBUTES_EVENT_OBJECT", "EXTATTRIBUTES_EVENT_STRING", "sdk"]
