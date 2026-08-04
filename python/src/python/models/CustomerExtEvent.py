"""
Customer Extended Attributes Event Model
Represents customer extended attributes event data for the Data Pipeline API
"""
import re
import json

class CustomerExtEvent:
    def __init__(self, **data):
        self.account_id = data.get("account_id")
        self.workspace_id = data.get("workspace_id")
        self.user_id = data.get("user_id")
        self.list_name = data.get("list_name")
        self.ext_data = data.get("ext_data")

    # ----------------------------------------------------------
    # Validation
    # ----------------------------------------------------------
    def validate(self):
        errors = []

        required = ["account_id","workspace_id", "user_id", "list_name", "ext_data"]

        for field in required:
            if not getattr(self, field, None):
                errors.append(f"{field} is required")

        # Validate user_id format
        if self.user_id and not isinstance(self.user_id, str):
            errors.append("user_id must be a string")

        # Validate list_name format (alphanumeric, underscores, hyphens)
        if self.list_name and not re.match(r'^[A-Za-z0-9_-]+$', self.list_name):
            errors.append("list_name must contain only alphanumeric characters, underscores, and hyphens")

        # Validate ext_data type
        if self.ext_data:
            # JSON string allowed
            if isinstance(self.ext_data, str):
                try:
                    json.loads(self.ext_data)
                except Exception:
                    errors.append("ext_data must be a valid JSON string or object")

            # Object allowed
            elif not isinstance(self.ext_data, dict):
                errors.append("ext_data must be an object or JSON string")

        return {
            "isValid": len(errors) == 0,
            "errors": errors
        }

    # ----------------------------------------------------------
    # Normalize to API expected structure
    # ----------------------------------------------------------
    def to_dict(self):
        result = {
            "account_id": self.account_id,
            "workspace_id": self.workspace_id,
            "user_id": self.user_id,
            "list_name": self.list_name,
        }

        # Convert ext_data object → JSON string
        if isinstance(self.ext_data, dict):
            import json
            result["ext_data"] = json.dumps(self.ext_data)
        else:
            result["ext_data"] = self.ext_data

        # Drop nulls
        return {k: v for k, v in result.items() if v is not None}

    @classmethod
    def from_object(cls, data):
        return cls(**data)
