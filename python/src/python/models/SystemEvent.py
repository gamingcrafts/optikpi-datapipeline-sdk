"""
System Event Model
Represents system-related events for the Data Pipeline API
"""
from typing import Optional, Dict, Any, Union, List
from datetime import datetime
from dataclasses import dataclass, field, asdict


@dataclass
class SystemEvent:
    """
    System Event Model
    
    Represents system-related events for the Data Pipeline API.
    
    Attributes:
        account_id: Account identifier
        workspace_id: Workspace identifier
        event_category: Event category (default: 'SystemEvent')
        event_name: Name of the event
        event_id: Unique event identifier
        event_time: Event timestamp in ISO 8601 format
        event_data: Event data (can be a JSON string or an object/dict)
    """
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    event_category: str = 'SystemEvent'
    event_name: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    event_data: Optional[Union[str, Dict[str, Any]]] = None
    
    def validate(self) -> Dict[str, Any]:
        """
        Validates the system event data
        
        Returns:
            Dictionary with 'isValid' boolean and 'errors' list
        """
        errors = []
        
        # Required fields
        if not self.account_id:
            errors.append('account_id is required')
        if not self.workspace_id:
            errors.append('workspace_id is required')
        if not self.event_name:
            errors.append('event_name is required')
        if not self.event_id:
            errors.append('event_id is required')
        if not self.event_time:
            errors.append('event_time is required')
        
        # event_data validation
        if self.event_data is None:
            errors.append('event_data is required')
        elif not isinstance(self.event_data, (str, dict)):
            errors.append('event_data must be a string or an object')
        
        # Event category validation
        if self.event_category and self.event_category != 'SystemEvent':
            errors.append('event_category must be "SystemEvent" for system events')
        
        # Date format validation
        if self.event_time and not self.is_valid_datetime(self.event_time):
            errors.append('event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)')
        
        return {
            'isValid': len(errors) == 0,
            'errors': errors
        }
    
    def is_valid_datetime(self, date_time: str) -> bool:
        """
        Validates ISO 8601 datetime format
        
        Args:
            date_time: DateTime string to validate
            
        Returns:
            True if datetime is valid
        """
        try:
            # Handle potential 'Z' suffix manually if python version < 3.11 with limited fromisoformat support
            # But replace('Z', '+00:00') covers most cases
            datetime.fromisoformat(date_time.replace('Z', '+00:00'))
            return True
        except (ValueError, AttributeError, TypeError):
            return False
    
    def to_json(self) -> Dict[str, Any]:
        """
        Converts the model to a dictionary
        
        Returns:
            Dictionary representation with non-None values
        """
        return {k: v for k, v in asdict(self).items() if v is not None}
    
    def to_dict(self) -> Dict[str, Any]:
        """Alias for to_json()"""
        return self.to_json()
    
    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> 'SystemEvent':
        """
        Creates an SystemEvent instance from a dictionary
        
        Args:
            data: Dictionary containing event data
            
        Returns:
            New SystemEvent instance
        """
        return cls(**data)
    
    def __repr__(self) -> str:
        """String representation"""
        return f"SystemEvent(event_id={self.event_id}, event_name={self.event_name})"
