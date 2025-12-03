#!/usr/bin/env python3
"""
Validate Account Schema
Validates account event data against JSON schema using jsonschema library
Equivalent to JavaScript AJV validation
"""
import json
import sys
from pathlib import Path

try:
    from jsonschema import validate, ValidationError, Draft7Validator
except ImportError:
    print("Error: jsonschema module not found")
    print("Install it with: pip install jsonschema")
    sys.exit(1)


# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

# Import test data from test-account-endpoint
from examples.test_account_endpoint import ACCOUNT_EVENT


def load_schema(schema_path):
    """
    Load JSON schema from file
    
    Args:
        schema_path: Path to schema file
    
    Returns:
        dict: Parsed schema
    """
    try:
        with open(schema_path, 'r', encoding='utf-8') as f:
            schema = json.load(f)
        return schema
    except FileNotFoundError:
        print(f"Warning: Schema file not found at {schema_path}")
        return None
    except json.JSONDecodeError as e:
        print(f"Warning: Invalid JSON in schema file: {e}")
        return None


def get_default_schema():
    """
    Return default account event schema
    Used when schema file cannot be loaded
    
    Returns:
        dict: Default schema
    """
    return {
        "type": "object",
        "properties": {
            "account_id": {"type": "string"},
            "workspace_id": {"type": "string"},
            "user_id": {"type": "string"},
            "event_category": {"type": "string"},
            "event_name": {
                "type": "string",
                "enum": [
                    "Player Registration",
                    "Player Login",
                    "Player Logout",
                    "Account Verification",
                    "Password Change",
                    "Account Update",
                    "Account Deletion",
                    "Account Suspension",
                    "Account Reactivation"
                ]
            },
            "event_id": {"type": "string"},
            "event_time": {"type": "string"},
            "device": {
                "type": "string",
                "enum": ["desktop", "mobile", "tablet", "unknown"]
            },
            "status": {
                "type": "string",
                "enum": ["pending", "completed", "failed", "verified", "suspended", "active", "inactive"]
            },
            "affiliate_id": {"type": "string"},
            "partner_id": {"type": "string"},
            "campaign_code": {"type": "string"},
            "reason": {"type": "string"}
        },
        "required": [
            "account_id",
            "workspace_id",
            "user_id",
            "event_name",
            "event_id",
            "event_time"
        ]
    }


def validate_account_data(account_data=None, account_schema=None):
    """
    Validate account event data against schema
    
    Args:
        account_data: Account event data to validate
        account_schema: Schema to validate against
    
    Returns:
        bool: True if valid, False otherwise
    """
    if account_data is None:
        account_data = ACCOUNT_EVENT
    
    # Try to load schema from file
    if account_schema is None:
        possible_paths = [
            Path(__file__).parent.parent / "api-to-s3" / "jsons" / "account-schema.json",
            Path(__file__).parent / "schemas" / "account-schema.json",
            Path(__file__).parent / "account-schema.json",
        ]
        
        for schema_path in possible_paths:
            if schema_path.exists():
                account_schema = load_schema(schema_path)
                if account_schema:
                    break
        
        # Use default schema if file not found
        if account_schema is None:
            account_schema = get_default_schema()
    
    print('Validating Account Event Data against Schema')
    print('=' * 50)
    
    print('\nTest Data:')
    print(json.dumps(account_data, indent=2))
    
    print('\nSchema Requirements:')
    if 'required' in account_schema:
        print(f'Required fields: {account_schema["required"]}')
    
    if 'properties' in account_schema:
        props = account_schema['properties']
        if 'event_name' in props and 'enum' in props['event_name']:
            print(f'Event names allowed: {props["event_name"]["enum"]}')
        if 'device' in props and 'enum' in props['device']:
            print(f'Device types allowed: {props["device"]["enum"]}')
        if 'status' in props and 'enum' in props['status']:
            print(f'Status values allowed: {props["status"]["enum"]}')
    
    print('\nValidation Results:')
    print('=' * 50)
    
    try:
        # Validate against schema
        validate(instance=account_data, schema=account_schema)
        
        print('\nPASS: Test data is valid according to schema!')
        
        # Additional checks
        print('\nData Analysis:')
        print(f'- Total fields: {len(account_data)}')
        
        if 'required' in account_schema:
            required_count = len(account_schema['required'])
            print(f'- Required fields present: {required_count}/{required_count}')
            
            # Check required fields
            missing_required = [
                field for field in account_schema['required']
                if field not in account_data
            ]
            
            if missing_required:
                print(f'Missing required fields: {", ".join(missing_required)}')
            else:
                print('All required fields are present')
        
        # Check enum values
        if 'properties' in account_schema:
            props = account_schema['properties']
            
            if 'event_name' in props and 'event_name' in account_data:
                event_name_valid = (
                    account_data['event_name'] in props['event_name'].get('enum', [])
                )
                print(f'Event name valid: {event_name_valid}')
            
            if 'device' in props and 'device' in account_data:
                device_valid = (
                    not account_data['device'] or
                    account_data['device'] in props['device'].get('enum', [])
                )
                print(f'Device type valid: {device_valid}')
            
            if 'status' in props and 'status' in account_data:
                status_valid = (
                    not account_data['status'] or
                    account_data['status'] in props['status'].get('enum', [])
                )
                print(f'Status valid: {status_valid}')
        
        return True
    
    except ValidationError as error:
        print('\nFAIL: Test data has validation errors!')
        print('\nValidation Errors:')
        
        # Get all validation errors
        validator = Draft7Validator(account_schema)
        errors = list(validator.iter_errors(account_data))
        
        for index, err in enumerate(errors, 1):
            path = '.'.join(str(p) for p in err.path) if err.path else 'root'
            print(f'{index}. {path}: {err.message}')
            if err.validator_value:
                print(f'   Details: {err.validator_value}')
        
        return False
    
    except Exception as e:
        print(f'\nError during validation: {str(e)}')
        return False


if __name__ == "__main__":
    # Load schema
    schema_path = Path(__file__).parent.parent / "api-to-s3" / "jsons" / "account-schema.json"
    
    if schema_path.exists():
        account_schema = load_schema(schema_path)
    else:
        account_schema = get_default_schema()
    
    # Validate data
    is_valid = validate_account_data(ACCOUNT_EVENT, account_schema)
    
    # Exit with appropriate code
    sys.exit(0 if is_valid else 1)