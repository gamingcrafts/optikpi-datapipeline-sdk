"""
Unit tests for DataPipelineClient
"""
import pytest
from unittest.mock import Mock, patch
import sys
from pathlib import Path

# Add src to path
sys.path.insert(0, str(Path(__file__).parent.parent / "src" / "python"))

from core.DataPipelineClient import DataPipelineClient


class TestDataPipelineClient:
    """Test suite for DataPipelineClient"""
    
    @pytest.fixture
    def client(self):
        """Create a test client instance"""
        return DataPipelineClient({
            'authToken': 'test-token',
            'accountId': 'test-account',
            'workspaceId': 'test-workspace',
            'baseURL': 'https://test.api.com'
        })
    
    # Constructor Tests
    class TestConstructor:
        """Tests for client constructor"""
        
        def test_create_client_with_valid_config(self):
            """Should create client with valid config"""
            client = DataPipelineClient({
                'authToken': 'test-token',
                'accountId': 'test-account',
                'workspaceId': 'test-workspace',
                'baseURL': 'https://test.api.com'
            })
            
            assert client.config['authToken'] == 'test-token'
            assert client.config['accountId'] == 'test-account'
            assert client.config['workspaceId'] == 'test-workspace'
            assert client.config['baseURL'] == 'https://test.api.com'
        
        def test_missing_auth_token_raises_error(self):
            """Should throw error for missing authToken"""
            with pytest.raises(ValueError, match='authToken is required'):
                DataPipelineClient({
                    'accountId': 'test-account',
                    'workspaceId': 'test-workspace'
                })
        
        def test_missing_account_id_raises_error(self):
            """Should throw error for missing accountId"""
            with pytest.raises(ValueError, match='accountId is required'):
                DataPipelineClient({
                    'authToken': 'test-token',
                    'workspaceId': 'test-workspace'
                })
        
        def test_missing_workspace_id_raises_error(self):
            """Should throw error for missing workspaceId"""
            with pytest.raises(ValueError, match='workspaceId is required'):
                DataPipelineClient({
                    'authToken': 'test-token',
                    'accountId': 'test-account'
                })
        
        def test_default_values_for_optional_config(self):
            """Should use default values for optional config"""
            client = DataPipelineClient({
                'authToken': 'test-token',
                'accountId': 'test-account',
                'workspaceId': 'test-workspace'
            })
            
            assert client.config['timeout'] == 30
            assert client.config['retries'] == 3
            assert client.config['retryDelay'] == 1
    
    # Health Check Tests
    class TestHealthCheck:
        """Tests for health check endpoint"""
        
        def test_health_check_success(self, client):
            """Should return success response for healthy API"""
            mock_response = Mock()
            mock_response.ok = True
            mock_response.status_code = 200
            mock_response.json.return_value = {
                'status': 'healthy',
                'timestamp': '2024-01-01T00:00:00Z'
            }
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.health_check()
            
            assert result['success'] is True
            assert result['status'] == 200
            assert result['data']['status'] == 'healthy'
        
        def test_health_check_failure(self, client):
            """Should return error response for failed health check"""
            with patch.object(client.session, 'request', side_effect=Exception('Network error')):
                result = client.health_check()
            
            assert result['success'] is False
            assert 'Network error' in result['error']
    
    # Customer Profile Tests
    class TestSendCustomerProfile:
        """Tests for sending customer profiles"""
        
        def test_send_customer_profile_success(self, client):
            """Should send customer profile successfully"""
            customer_data = {
                'account_id': 'test-account',
                'workspace_id': 'test-workspace',
                'user_id': 'user123',
                'email': 'test@example.com'
            }
            
            mock_response = Mock()
            mock_response.ok = True
            mock_response.status_code = 200
            mock_response.json.return_value = {
                'message': 'Customer profile sent successfully'
            }
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.send_customer_profile(customer_data)
            
            assert result['success'] is True
            assert result['status'] == 200
            assert result['data']['message'] == 'Customer profile sent successfully'
        
        def test_send_customer_profile_api_error(self, client):
            """Should handle API errors gracefully"""
            customer_data = {'invalid': 'data'}
            
            mock_response = Mock()
            mock_response.ok = False
            mock_response.status_code = 400
            mock_response.json.return_value = {
                'errors': ['Invalid data format']
            }
            mock_response.text = 'Bad Request'
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.send_customer_profile(customer_data)
            
            assert result['success'] is False
            assert result['status'] == 400
            assert 'errors' in result['data']
    
    # Account Event Tests
    class TestSendAccountEvent:
        """Tests for sending account events"""
        
        def test_send_account_event_success(self, client):
            """Should send account event successfully"""
            event_data = {
                'account_id': 'test-account',
                'workspace_id': 'test-workspace',
                'user_id': 'user123',
                'event_name': 'Player Registration',
                'event_id': 'evt_123',
                'event_time': '2024-01-01T00:00:00Z'
            }
            
            mock_response = Mock()
            mock_response.ok = True
            mock_response.status_code = 200
            mock_response.json.return_value = {
                'message': 'Account event sent successfully'
            }
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.send_account_event(event_data)
            
            assert result['success'] is True
            assert result['status'] == 200
            assert result['data']['message'] == 'Account event sent successfully'
    
    # Deposit Event Tests
    class TestSendDepositEvent:
        """Tests for sending deposit events"""
        
        def test_send_deposit_event_success(self, client):
            """Should send deposit event successfully"""
            event_data = {
                'account_id': 'test-account',
                'workspace_id': 'test-workspace',
                'user_id': 'user123',
                'event_name': 'Successful Deposit',
                'event_id': 'evt_dep_123',
                'event_time': '2024-01-01T00:00:00Z',
                'amount': 100.00,
                'payment_method': 'bank',
                'transaction_id': 'txn_123'
            }
            
            mock_response = Mock()
            mock_response.ok = True
            mock_response.status_code = 200
            mock_response.json.return_value = {
                'message': 'Deposit event sent successfully'
            }
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.send_deposit_event(event_data)
            
            assert result['success'] is True
            assert result['status'] == 200
            assert result['data']['message'] == 'Deposit event sent successfully'
    
    # Withdraw Event Tests
    class TestSendWithdrawEvent:
        """Tests for sending withdraw events"""
        
        def test_send_withdraw_event_success(self, client):
            """Should send withdraw event successfully"""
            event_data = {
                'account_id': 'test-account',
                'workspace_id': 'test-workspace',
                'user_id': 'user123',
                'event_name': 'Successful Withdrawal',
                'event_id': 'evt_with_123',
                'event_time': '2024-01-01T00:00:00Z',
                'amount': 50.00,
                'payment_method': 'bank',
                'transaction_id': 'txn_456'
            }
            
            mock_response = Mock()
            mock_response.ok = True
            mock_response.status_code = 200
            mock_response.json.return_value = {
                'message': 'Withdraw event sent successfully'
            }
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.send_withdraw_event(event_data)
            
            assert result['success'] is True
            assert result['status'] == 200
            assert result['data']['message'] == 'Withdraw event sent successfully'
    
    # Gaming Activity Event Tests
    class TestSendGamingActivityEvent:
        """Tests for sending gaming activity events"""
        
        def test_send_gaming_activity_event_success(self, client):
            """Should send gaming activity event successfully"""
            event_data = {
                'account_id': 'test-account',
                'workspace_id': 'test-workspace',
                'user_id': 'user123',
                'event_name': 'Play Casino Game',
                'event_id': 'evt_gaming_123',
                'event_time': '2024-01-01T00:00:00Z',
                'game_id': 'game_123',
                'game_title': 'Blackjack'
            }
            
            mock_response = Mock()
            mock_response.ok = True
            mock_response.status_code = 200
            mock_response.json.return_value = {
                'message': 'Gaming activity event sent successfully'
            }
            
            with patch.object(client.session, 'request', return_value=mock_response):
                result = client.send_gaming_activity_event(event_data)
            
            assert result['success'] is True
            assert result['status'] == 200
            assert result['data']['message'] == 'Gaming activity event sent successfully'
    
    # Batch Tests
    class TestSendBatch:
        """Tests for sending batch data"""
        
        def test_send_batch_success(self, client):
            """Should send batch data successfully"""
            batch_data = {
                'customers': [{'user_id': 'user1', 'email': 'user1@example.com'}],
                'accountEvents': [{'user_id': 'user1', 'event_name': 'Login'}],
                'depositEvents': [{'user_id': 'user1', 'amount': 100, 'payment_method': 'bank'}]
            }
            
            # Mock responses for each endpoint
            mock_response1 = Mock()
            mock_response1.ok = True
            mock_response1.status_code = 200
            mock_response1.json.return_value = {'message': 'Customers sent'}
            
            mock_response2 = Mock()
            mock_response2.ok = True
            mock_response2.status_code = 200
            mock_response2.json.return_value = {'message': 'Account events sent'}
            
            mock_response3 = Mock()
            mock_response3.ok = True
            mock_response3.status_code = 200
            mock_response3.json.return_value = {'message': 'Deposit events sent'}
            
            with patch.object(client.session, 'request', side_effect=[mock_response1, mock_response2, mock_response3]):
                result = client.send_batch(batch_data)
            
            assert result['success'] is True
            assert result['results']['customers']['success'] is True
            assert result['results']['accountEvents']['success'] is True
            assert result['results']['depositEvents']['success'] is True
    
    # Config Tests
    class TestConfig:
        """Tests for configuration methods"""
        
        def test_update_config_success(self, client):
            """Should update configuration successfully"""
            new_config = {
                'timeout': 60,
                'retries': 5
            }
            
            client.update_config(new_config)
            
            assert client.config['timeout'] == 60
            assert client.config['retries'] == 5
            assert client.config['authToken'] == 'test-token'  # Should remain unchanged
        
        def test_get_config_returns_safe_config(self, client):
            """Should return safe configuration without sensitive data"""
            config = client.get_config()
            
            assert config['authToken'] == 'test-tok...'
            assert config['accountId'] == 'test-account'
            assert config['workspaceId'] == 'test-workspace'
            assert config['baseURL'] == 'https://test.api.com'
    
    # Error Handling Tests
    class TestErrorHandling:
        """Tests for error handling"""
        
        def test_timeout_error(self, client):
            """Should handle timeout errors"""
            import requests
            
            with patch.object(client.session, 'request', side_effect=requests.exceptions.Timeout('Request timeout')):
                result = client.send_customer_profile({'test': 'data'})
            
            assert result['success'] is False
            assert 'timeout' in result['error'].lower()
        
        def test_connection_error(self, client):
            """Should handle connection errors"""
            import requests
            
            with patch.object(client.session, 'request', side_effect=requests.exceptions.ConnectionError('Connection failed')):
                result = client.send_customer_profile({'test': 'data'})
            
            assert result['success'] is False
            assert 'connection' in result['error'].lower()


# Run tests with pytest
if __name__ == '__main__':
    pytest.main([__file__, '-v'])