"""
Data Pipeline API Client
Main client class for interacting with the Optikpi Data Pipeline API
"""
import time
import json
from typing import Dict, Any, List, Union, Optional
from datetime import datetime
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from utils.crypto import generate_hmac_signature


class DataPipelineClient:
    """
    Data Pipeline API Client
    
    Main client class for interacting with the Optikpi Data Pipeline API.
    Handles authentication, retries, and all API endpoints.
    """
    
    def __init__(self, config: Optional[Dict[str, Any]] = None):
        """
        Initialize the Data Pipeline Client
        
        Args:
            config: Configuration dictionary with:
                - baseURL: API base URL (required)
                - authToken: Authentication token (required)
                - accountId: Account ID (required)
                - workspaceId: Workspace ID (required)
                - timeout: Request timeout in seconds (default: 30)
                - retries: Number of retries (default: 3)
                - retryDelay: Retry delay in seconds (default: 1)
        """
        if config is None:
            config = {}
            
        self.config = {
            'baseURL': config.get('baseURL', 'https://demo.optikpi.com/apigw/ingest'),
            'authToken': config.get('authToken'),
            'accountId': config.get('accountId'),
            'workspaceId': config.get('workspaceId'),
            'timeout': config.get('timeout', 30),
            'retries': config.get('retries', 3),
            'retryDelay': config.get('retryDelay', 1),
            **config
        }
        
        self.validate_config()
        self.setup_session()
    
    def validate_config(self) -> None:
        """
        Validates the client configuration
        
        Raises:
            ValueError: If required configuration is missing
        """
        auth_token = self.config.get('authToken')
        account_id = self.config.get('accountId')
        workspace_id = self.config.get('workspaceId')
        
        if not auth_token:
            raise ValueError('authToken is required')
        if not account_id:
            raise ValueError('accountId is required')
        if not workspace_id:
            raise ValueError('workspaceId is required')
    
    def setup_session(self) -> None:
        """
        Sets up the requests session with default configuration and retry logic
        """
        self.session = requests.Session()
        
        # Set up retry strategy
        retry_strategy = Retry(
            total=self.config['retries'],
            backoff_factor=self.config['retryDelay'],
            status_forcelist=[500, 502, 503, 504],
            allowed_methods=["POST", "GET"]
        )
        
        adapter = HTTPAdapter(max_retries=retry_strategy)
        self.session.mount("http://", adapter)
        self.session.mount("https://", adapter)
        
        # Set default headers
        self.session.headers.update({
            'Content-Type': 'application/json',
            'User-Agent': 'Optikpi-DataPipeline-SDK-Python/1.0.0'
        })
    
    def _add_auth_headers(self, data: Any) -> Dict[str, str]:
        """
        Generate authentication headers including HMAC signature
        
        Args:
            data: Request data to sign
            
        Returns:
            Dictionary of authentication headers
        """
        hmac_signature = generate_hmac_signature(
            data,
            self.config['authToken'],
            self.config['accountId'],
            self.config['workspaceId']
        )
        
        return {
            'x-optikpi-token': self.config['authToken'],
            'x-optikpi-account-id': self.config['accountId'],
            'x-optikpi-workspace-id': self.config['workspaceId'],
            'x-hmac-signature': hmac_signature,
            'x-hmac-algorithm': 'sha256'
        }
    
    def _make_request(self, method: str, endpoint: str, data: Any = None) -> Dict[str, Any]:
        """
        Make an HTTP request to the API
        
        Args:
            method: HTTP method (GET, POST, etc.)
            endpoint: API endpoint path
            data: Request data
            
        Returns:
            Response dictionary with success status and data
        """
        url = f"{self.config['baseURL']}{endpoint}"
        
        try:
            headers = {}
            request_body = None
            
            if method != 'GET' and data is not None:
                # CRITICAL FIX: Generate signature from data object first
                headers = self._add_auth_headers(data)
                
                # Then convert to JSON string for the request body
                # Use same format as signature: separators=(',', ':')
                request_body = json.dumps(data, separators=(',', ':'))
            
            # Make the request with pre-serialized JSON string
            response = self.session.request(
                method=method,
                url=url,
                data=request_body,  # Send as string, not json=data
                headers=headers,
                timeout=self.config['timeout']
            )
            
            # Parse response
            try:
                response_data = response.json()
            except ValueError:
                response_data = {'message': response.text} if response.text else {}
            
            if response.ok:
                return {
                    'success': True,
                    'status': response.status_code,
                    'data': response_data,
                    'timestamp': datetime.now().isoformat()
                }
            else:
                return {
                    'success': False,
                    'status': response.status_code,
                    'error': response_data.get('message', response.text),
                    'data': response_data,
                    'timestamp': datetime.now().isoformat()
                }
        
        except requests.exceptions.Timeout:
            return {
                'success': False,
                'error': f'Request timeout after {self.config["timeout"]} seconds',
                'status': 0,
                'timestamp': datetime.now().isoformat()
            }
        except requests.exceptions.ConnectionError as e:
            return {
                'success': False,
                'error': f'Connection error: {str(e)}',
                'status': 0,
                'timestamp': datetime.now().isoformat()
            }
        except Exception as e:
            return {
                'success': False,
                'error': str(e),
                'status': 0,
                'timestamp': datetime.now().isoformat()
            }
    
    def send_customer_profile(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends customer profile data
        
        Args:
            data: Customer profile data or list of profiles
            
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/customers', data)
    
    def send_account_event(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends account event data
        
        Args:
            data: Account event data or list of events
            
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/events/account', data)
    
    def send_deposit_event(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends deposit event data
        
        Args:
            data: Deposit event data or list of events
            
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/events/deposit', data)
    
    def send_withdraw_event(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends withdrawal event data
        
        Args:
            data: Withdrawal event data or list of events
            
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/events/withdraw', data)
    
    def send_gaming_activity_event(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends gaming activity event data
        
        Args:
            data: Gaming activity event data or list of events
            
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/events/gaming-activity', data)

    def send_extended_attributes(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        send Extended Attributes data
        
        Args:
            data: send Extended Attributes data or list of events
            
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/extattributes', data)
    
    def send_wallet_balance_event(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends wallet balance event data
    
        Args:
            data: Wallet balance event data or list of events
        
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/events/wallet-balance', data)


    def send_refer_friend_event(self, data: Union[Dict[str, Any], List[Dict[str, Any]]]) -> Dict[str, Any]:
        """
        Sends refer friend event data
    
        Args:
            data: Refer friend event data or list of events
        
        Returns:
            API response dictionary
        """
        return self._make_request('POST', '/events/refer-friend', data)

    
    def send_batch(self, batch_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Sends multiple events in batch
        
        Args:
            batch_data: Dictionary containing different event types:
                - customers: List of customer profiles
                - accountEvents: List of account events
                - depositEvents: List of deposit events
                - withdrawEvents: List of withdraw events
                - gamingEvents: List of gaming activity events
                - walletBalanceEvents: List of wallet balance events
                - referFriendEvents: List of refer friend events
                - customerExtEvents: List of customer extended attributes events
            
        Returns:
            Batch response results dictionary
        """
        results = {}
        
        # Send each type of data
        if 'customers' in batch_data:
            results['customers'] = self.send_customer_profile(batch_data['customers'])
        
        if 'accountEvents' in batch_data:
            results['accountEvents'] = self.send_account_event(batch_data['accountEvents'])
        
        if 'depositEvents' in batch_data:
            results['depositEvents'] = self.send_deposit_event(batch_data['depositEvents'])
        
        if 'withdrawEvents' in batch_data:
            results['withdrawEvents'] = self.send_withdraw_event(batch_data['withdrawEvents'])
        
        if 'gamingEvents' in batch_data:
            results['gamingEvents'] = self.send_gaming_activity_event(batch_data['gamingEvents'])
        
        if 'walletBalanceEvents' in batch_data:
             results['walletBalanceEvents'] = self.send_wallet_balance_event(batch_data['walletBalanceEvents'])

        if 'referFriendEvents' in batch_data:
            results['referFriendEvents'] = self.send_refer_friend_event(batch_data['referFriendEvents'])

        if 'extendedAttributes' in batch_data:
            results['extendedAttributes'] = self.send_extended_attributes(batch_data['extendedAttributes'])
    
        


        return {
            'success': True,
            'results': results,
            'timestamp': datetime.now().isoformat()
        }
    
    def update_config(self, new_config: Dict[str, Any]) -> None:
        """
        Updates client configuration
        
        Args:
            new_config: Dictionary with new configuration options
        """
        self.config.update(new_config)
        self.validate_config()
        self.setup_session()
    
    
    def get_config(self) -> Dict[str, Any]:
        """
        Gets current configuration
        
        Returns:
            Current configuration (without sensitive data)
        """
        safe_config = self.config.copy()
        auth_token = safe_config.get('authToken', '')
        if auth_token:
            safe_config['authToken'] = f"{auth_token[:8]}..."
        return safe_config
    
    def __enter__(self):
        """Context manager entry"""
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit"""
        self.session.close()
    
    def close(self):
        """Close the session"""
        self.session.close()