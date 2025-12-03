"""
Account Event Model
Represents account-related events for the Data Pipeline API
"""
from typing import Optional, Dict, Any, List
from datetime import datetime
from dataclasses import dataclass, field, asdict


@dataclass
class AccountEvent:
    """
    Account Event Model
    
    Represents account-related events for the Data Pipeline API.
    
    Attributes:
        account_id: Account identifier
        workspace_id: Workspace identifier
        user_id: User identifier
        event_category: Event category (default: 'Account')
        event_name: Name of the event
        event_id: Unique event identifier
        event_time: Event timestamp in ISO 8601 format
        device: Device type (desktop, mobile, tablet, app)
        status: Event status (verified, pending, failed, completed)
        affiliate_id: Affiliate identifier
        partner_id: Partner identifier
        campaign_code: Campaign code
        reason: Event reason/description
    """
    account_id: Optional[str] = None
    workspace_id: Optional[str] = None
    user_id: Optional[str] = None
    event_category: str = 'Account'
    event_name: Optional[str] = None
    event_id: Optional[str] = None
    event_time: Optional[str] = None
    device: Optional[str] = None
    status: Optional[str] = None
    affiliate_id: Optional[str] = None
    partner_id: Optional[str] = None
    campaign_code: Optional[str] = None
    reason: Optional[str] = None
    
    # Valid values
    VALID_EVENT_NAMES = [
        'Player Registration',
        'Account Verification',
        'Password Change',
        'Email Update',
        'Phone Update',
        'Account Suspension',
        'Account Reactivation',
        'Profile Update',
        'Login',
        'Logout'
    ]
    
    VALID_STATUSES = ['verified', 'pending', 'failed', 'completed']
    VALID_DEVICES = ['desktop', 'mobile', 'tablet', 'app']
    
    def validate(self) -> Dict[str, Any]:
        """
        Validates the account event data
        
        Returns:
            Dictionary with 'isValid' boolean and 'errors' list
        """
        errors = []
        
        # Required fields
        if not self.account_id:
            errors.append('account_id is required')
        if not self.workspace_id:
            errors.append('workspace_id is required')
        if not self.user_id:
            errors.append('user_id is required')
        if not self.event_name:
            errors.append('event_name is required')
        if not self.event_id:
            errors.append('event_id is required')
        if not self.event_time:
            errors.append('event_time is required')
        
        # Event category validation
        if self.event_category and self.event_category != 'Account':
            errors.append('event_category must be "Account" for account events')
        
        # Event name validation
        if self.event_name and self.event_name not in self.VALID_EVENT_NAMES:
            errors.append(f'event_name must be one of: {", ".join(self.VALID_EVENT_NAMES)}')
        
        # Status validation
        if self.status and self.status not in self.VALID_STATUSES:
            errors.append(f'status must be one of: {", ".join(self.VALID_STATUSES)}')
        
        # Device validation
        if self.device and self.device not in self.VALID_DEVICES:
            errors.append(f'device must be one of: {", ".join(self.VALID_DEVICES)}')
        
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
            datetime.fromisoformat(date_time.replace('Z', '+00:00'))
            return True
        except (ValueError, AttributeError):
            return False
    
    def to_json(self) -> Dict[str, Any]:
        """
        Converts the model to a dictionary
        
        Returns:
            Dictionary representation with non-None values
        """
        return {k: v for k, v in asdict(self).items() if v is not None and not k.startswith('VALID_')}
    
    def to_dict(self) -> Dict[str, Any]:
        """Alias for to_json()"""
        return self.to_json()
    
    @classmethod
    def from_object(cls, data: Dict[str, Any]) -> 'AccountEvent':
        """
        Creates an AccountEvent instance from a dictionary
        
        Args:
            data: Dictionary containing event data
            
        Returns:
            New AccountEvent instance
        """
        return cls(**data)
    
    def __repr__(self) -> str:
        """String representation"""
        return f"AccountEvent(event_id={self.event_id}, event_name={self.event_name}, user_id={self.user_id})"