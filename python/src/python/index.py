"""
Optikpi Data Pipeline API Python SDK

A comprehensive Python SDK for integrating with the Optikpi Data Pipeline API.
Provides secure authentication, data validation, and easy-to-use methods for
sending customer profiles and event data.

Version: 1.0.0
Author: Optikpi
License: MIT
"""

from core.DataPipelineClient import DataPipelineClient
from models.CustomerProfile import CustomerProfile
from models.AccountEvent import AccountEvent
from models.DepositEvent import DepositEvent
from models.WithdrawEvent import WithdrawEvent
from models.GamingActivityEvent import GamingActivityEvent
from utils import crypto

__version__ = "1.0.0"
__author__ = "Optikpi"
__license__ = "MIT"


class OptikpiDataPipelineSDK:
    """
    Main SDK class for Optikpi Data Pipeline API
    
    This class provides a simplified interface to the Data Pipeline API,
    delegating all operations to the underlying DataPipelineClient.
    
    Example:
        >>> from optikpi_datapipeline import OptikpiDataPipelineSDK
        >>> sdk = OptikpiDataPipelineSDK({
        ...     'authToken': 'your-token',
        ...     'accountId': 'your-account',
        ...     'workspaceId': 'your-workspace'
        ... })
        >>> response = sdk.send_account_event(event_data)
    """
    
    def __init__(self, config=None):
        """
        Initialize the SDK
        
        Args:
            config: Configuration dictionary with authToken, accountId, workspaceId, etc.
        """
        if config is None:
            config = {}
        self.client = DataPipelineClient(config)
    
    def health_check(self):
        """
        Perform a health check on the API
        
        Returns:
            Response dictionary with success status and data
        """
        return self.client.health_check()
    
    def send_customer_profile(self, data):
        """
        Send customer profile data
        
        Args:
            data: Customer profile data (dict or list of dicts)
            
        Returns:
            Response dictionary with success status and data
        """
        return self.client.send_customer_profile(data)
    
    def send_account_event(self, data):
        """
        Send account event data
        
        Args:
            data: Account event data (dict or list of dicts)
            
        Returns:
            Response dictionary with success status and data
        """
        return self.client.send_account_event(data)
    
    def send_deposit_event(self, data):
        """
        Send deposit event data
        
        Args:
            data: Deposit event data (dict or list of dicts)
            
        Returns:
            Response dictionary with success status and data
        """
        return self.client.send_deposit_event(data)
    
    def send_withdraw_event(self, data):
        """
        Send withdrawal event data
        
        Args:
            data: Withdrawal event data (dict or list of dicts)
            
        Returns:
            Response dictionary with success status and data
        """
        return self.client.send_withdraw_event(data)
    
    def send_gaming_activity_event(self, data):
        """
        Send gaming activity event data
        
        Args:
            data: Gaming activity event data (dict or list of dicts)
            
        Returns:
            Response dictionary with success status and data
        """
        return self.client.send_gaming_activity_event(data)
    
    def send_extended_attributes(self, data):
        """
        Send Extended Attributes data
        
        Args:
            data: Extended Attributes data (dict or list of dicts)
            
        Returns:
            Response dictionary with success status and data
        """
        return self.client.send_extended_attributes(data)
    
    def send_batch(self, batch_data):
        """
        Send multiple events in batch
        
        Args:
            batch_data: Dictionary containing different event types
                       (customers, accountEvents, depositEvents, etc.)
            
        Returns:
            Batch response results dictionary
        """
        return self.client.send_batch(batch_data)
    
    def update_config(self, new_config):
        """
        Update client configuration
        
        Args:
            new_config: Dictionary with new configuration options
        """
        self.client.update_config(new_config)
    
    def get_config(self):
        """
        Get current configuration
        
        Returns:
            Current configuration dictionary (without sensitive data)
        """
        return self.client.get_config()


def create_client(config):
    """
    Create a new SDK instance
    
    Convenience function for creating SDK instances.
    
    Args:
        config: Configuration dictionary
        
    Returns:
        New OptikpiDataPipelineSDK instance
    """
    return OptikpiDataPipelineSDK(config)


# Export all public classes and functions
__all__ = [
    'OptikpiDataPipelineSDK',
    'DataPipelineClient',
    'CustomerProfile',
    'AccountEvent',
    'DepositEvent',
    'WithdrawEvent',
    'GamingActivityEvent',
    'crypto',
    'create_client',
]