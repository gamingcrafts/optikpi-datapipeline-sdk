#!/usr/bin/env python3
"""
Test Customer Extended Attributes Endpoint
"""

import os
import sys
import json
import time
from pathlib import Path
from dotenv import load_dotenv

sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from index import OptikpiDataPipelineSDK
from models.CustomerExtEvent import CustomerExtEvent

load_dotenv()

API_BASE_URL = os.getenv("API_BASE_URL")
AUTH_TOKEN = os.getenv("AUTH_TOKEN")
ACCOUNT_ID = os.getenv("ACCOUNT_ID")
WORKSPACE_ID = os.getenv("WORKSPACE_ID")

if not AUTH_TOKEN or not ACCOUNT_ID or not WORKSPACE_ID:
    print("❌ Missing environment variables!")
    sys.exit(1)

sdk = OptikpiDataPipelineSDK({
    "authToken": AUTH_TOKEN,
    "accountId": ACCOUNT_ID,
    "workspaceId": WORKSPACE_ID,
    "baseURL": API_BASE_URL
})

extattr = CustomerExtEvent(
    workspace_id=WORKSPACE_ID,
    user_id="1custext_py_1",
    list_name="BINGO_PREFERENCES",
    ext_data={
        "Email": "True",
        "SMS": "True",
        "PushNotifications": "False"
    }
)

validation = extattr.validate()
if not validation["isValid"]:
    print("❌ Validation failed:", validation["errors"])
    sys.exit(1)

print("✅ Validation passed")

def test_customerext_endpoint():
    print("\n🚀 Testing Customer Extended Attributes Endpoint")

    payload = extattr.to_dict()
    print("📋 Data:", json.dumps(payload, indent=2))

    start = time.time()
    result = sdk.send_extended_attributes(payload)
    end = time.time()

    rt = int((end - start) * 1000)

    if result["success"]:
        print("\n✅ Success!")
        print(f"Status: {result['status']}")
        print(f"Response Time: {rt}ms")
        print(f"Response: {json.dumps(result['data'], indent=2)}")
    else:
        print("\n❌ API Error!")
        print(f"Status: {result['status']}")
        print(f"Response Time: {rt}ms")
        print(f"Error: {json.dumps(result['data'], indent=2)}")

if __name__ == "__main__":
    test_customerext_endpoint()
