"""
Customer Extended Attributes Event Model
Represents customer extended attributes event data for the Data Pipeline API
"""

from .bigquery_validation import merge_storage_errors

# Fields and types the warehouse accepts for this record; anything else is rejected there.
STORAGE_RULES = {
    "required": ["workspace_id", "user_id"],
    "types": {
        "workspace_id": "STRING",
        "user_id": "STRING",
        "list_name": "STRING",
        "ext_data": "JSON"
    }
}


class CustomerExtEvent:
    def __init__(self, **data):
        # The record as sent, so validation also covers fields this model does not declare.
        self._input = data
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

        # Validate list_name format
        if self.list_name and not isinstance(self.list_name, str):
            errors.append("list_name must be a string")

        # Validate ext_data type
        if self.ext_data:
            # JSON string allowed
            if isinstance(self.ext_data, str):
                try:
                    import json
                    json.loads(self.ext_data)
                except Exception:
                    errors.append("ext_data string must contain valid JSON")

            # Object allowed
            elif not isinstance(self.ext_data, dict):
                errors.append("ext_data must be object or JSON string")

        # The record as sent, so validation also covers fields this model does not declare.
        merge_storage_errors(errors, self, STORAGE_RULES)

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
