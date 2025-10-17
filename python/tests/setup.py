"""
Pytest configuration and fixtures
Equivalent to Jest setup file (jest.setup.js)

This file runs before each test and configures:
- Global test fixtures
- Mock setup
- Test timeout
- Console mocking
"""
import sys
import pytest
from unittest.mock import Mock, patch, MagicMock
from io import StringIO
from pathlib import Path

# Add src to path for imports
sys.path.insert(0, str(Path(__file__).parent / "src" / "python"))


# =====================================================
# Global Test Configuration
# =====================================================

def pytest_configure(config):
    """
    Configure pytest
    Equivalent to jest.setTimeout() and global test setup
    """
    # Set global test timeout (10 seconds, equivalent to jest.setTimeout(10000))
    config.option.timeout = 10


# =====================================================
# Mock Setup - Crypto Module
# =====================================================

class MockCrypto:
    """
    Mock crypto module for testing
    Replaces Python's hashlib and cryptography modules
    """
    
    @staticmethod
    def hkdf_sync(*args, **kwargs):
        """Mock HKDF key derivation"""
        return b'test-derived-key-32-bytes-long'
    
    @staticmethod
    def create_hmac(*args, **kwargs):
        """Mock HMAC creation"""
        mock_hmac = Mock()
        mock_hmac.update = Mock(return_value=mock_hmac)
        mock_hmac.digest = Mock(return_value=b'test-hmac-signature')
        mock_hmac.hexdigest = Mock(return_value='test-hmac-signature')
        return mock_hmac


# =====================================================
# Mock Setup - Requests Module (Axios equivalent)
# =====================================================

class MockResponse:
    """Mock response object for requests"""
    def __init__(self):
        self.status_code = 200
        self.text = 'Success'
        self.ok = True
        self.headers = {}
    
    def json(self):
        """Return mock JSON response"""
        return {'status': 'success', 'data': {}}


class MockSession:
    """Mock requests.Session for testing"""
    def __init__(self):
        self.interceptors = {
            'request': Mock(),
            'response': Mock()
        }
        self.headers = {}
        self._request_mock = Mock(return_value=MockResponse())
        self._get_mock = Mock(return_value=MockResponse())
        self._post_mock = Mock(return_value=MockResponse())
    
    def request(self, *args, **kwargs):
        """Mock request method"""
        return self._request_mock(*args, **kwargs)
    
    def get(self, *args, **kwargs):
        """Mock GET request"""
        return self._get_mock(*args, **kwargs)
    
    def post(self, *args, **kwargs):
        """Mock POST request"""
        return self._post_mock(*args, **kwargs)
    
    def close(self):
        """Mock close method"""
        pass
    
    def mount(self, *args, **kwargs):
        """Mock mount method"""
        pass


# =====================================================
# Pytest Fixtures
# =====================================================

@pytest.fixture(autouse=True)
def mock_crypto(monkeypatch):
    """
    Mock the crypto module for all tests
    Equivalent to jest.mock('crypto', ...)
    Runs automatically before each test (autouse=True)
    """
    mock = MockCrypto()
    
    # Mock hashlib
    monkeypatch.setattr('hashlib.pbkdf2_hmac', lambda *args, **kwargs: b'test-derived-key-32-bytes-long')
    
    # Mock hmac
    mock_hmac_module = Mock()
    mock_hmac_module.new = Mock(return_value=mock.create_hmac())
    monkeypatch.setattr('hmac.new', mock_hmac_module.new)
    
    yield mock


@pytest.fixture(autouse=True)
def mock_requests(monkeypatch):
    """
    Mock the requests module for all tests
    Equivalent to jest.mock('axios', ...)
    Runs automatically before each test (autouse=True)
    """
    mock_session = MockSession()
    
    def mock_session_factory(*args, **kwargs):
        return mock_session
    
    # Patch requests.Session
    monkeypatch.setattr('requests.Session', mock_session_factory)
    
    yield mock_session


@pytest.fixture
def capture_logs(capsys):
    """
    Capture console output
    Equivalent to mocking console.log/debug/info/warn/error in Jest
    """
    logs = {
        'log': [],
        'debug': [],
        'info': [],
        'warn': [],
        'error': []
    }
    
    def mock_print(*args, **kwargs):
        captured = capsys.readouterr()
        level = kwargs.get('level', 'log')
        logs[level].append(args)
    
    yield logs


@pytest.fixture
def mock_console():
    """
    Mock console methods for testing
    Equivalent to global.console mock in Jest
    """
    console_mock = {
        'log': Mock(),
        'debug': Mock(),
        'info': Mock(),
        'warn': Mock(),
        'error': Mock()
    }
    
    return console_mock


# =====================================================
# Test Configuration Fixtures
# =====================================================

@pytest.fixture
def test_config():
    """
    Provide standard test configuration
    Used across multiple tests
    """
    return {
        'authToken': 'test-token',
        'accountId': 'test-account',
        'workspaceId': 'test-workspace',
        'baseURL': 'https://test.api.com',
        'timeout': 30,
        'retries': 3,
        'retryDelay': 1
    }


@pytest.fixture
def test_account_event():
    """Provide standard test account event"""
    return {
        'account_id': 'test-account',
        'workspace_id': 'test-workspace',
        'user_id': 'user123',
        'event_category': 'Account',
        'event_name': 'Player Registration',
        'event_id': 'evt_123456789',
        'event_time': '2024-01-15T10:30:00Z',
        'device': 'desktop',
        'status': 'verified',
        'affiliate_id': 'aff_123',
        'partner_id': 'partner_456',
        'campaign_code': 'CAMPAIGN_001',
        'reason': 'Registration completed successfully'
    }


@pytest.fixture
def test_customer_data():
    """Provide standard test customer data"""
    return {
        'account_id': 'test-account',
        'workspace_id': 'test-workspace',
        'user_id': 'user123',
        'email': 'test@example.com',
        'first_name': 'Test',
        'last_name': 'User',
        'phone': '+1234567890'
    }


@pytest.fixture
def test_deposit_event():
    """Provide standard test deposit event"""
    return {
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


@pytest.fixture
def test_withdraw_event():
    """Provide standard test withdraw event"""
    return {
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


@pytest.fixture
def test_gaming_event():
    """Provide standard test gaming activity event"""
    return {
        'account_id': 'test-account',
        'workspace_id': 'test-workspace',
        'user_id': 'user123',
        'event_name': 'Play Casino Game',
        'event_id': 'evt_gaming_123',
        'event_time': '2024-01-01T00:00:00Z',
        'game_id': 'game_123',
        'game_title': 'Blackjack'
    }


# =====================================================
# Pytest Hooks
# =====================================================

def pytest_collection_modifyitems(config, items):
    """
    Add markers to tests
    Configure test behavior
    """
    for item in items:
        # Add timeout marker if not already present
        if 'timeout' not in item.keywords:
            item.add_marker(pytest.mark.timeout(10))


@pytest.fixture(scope='function', autouse=True)
def reset_mocks():
    """
    Reset all mocks before each test
    Equivalent to jest.clearAllMocks() in beforeEach
    """
    yield
    # Cleanup after test
    from unittest.mock import _get_child_mock
    # Mocks are automatically reset due to fixture scope


# =====================================================
# Helper Functions for Tests
# =====================================================

def assert_mock_called_with(mock, *args, **kwargs):
    """
    Assert mock was called with specific arguments
    Wrapper around mock.assert_called_with()
    """
    mock.assert_called_with(*args, **kwargs)


def assert_mock_called_once_with(mock, *args, **kwargs):
    """
    Assert mock was called exactly once with specific arguments
    Wrapper around mock.assert_called_once_with()
    """
    mock.assert_called_once_with(*args, **kwargs)


def create_mock_response(status=200, data=None, ok=True, error=None):
    """
    Create a mock response object
    Helper for test setup
    """
    mock_response = Mock()
    mock_response.status_code = status
    mock_response.ok = ok
    mock_response.text = error or 'Success'
    mock_response.json.return_value = data or {'status': 'success'}
    mock_response.headers = {'content-type': 'application/json'}
    return mock_response


# =====================================================
# Test Markers
# =====================================================

def pytest_configure(config):
    """Register custom markers"""
    config.addinivalue_line(
        "markers", "integration: mark test as an integration test"
    )
    config.addinivalue_line(
        "markers", "unit: mark test as a unit test"
    )
    config.addinivalue_line(
        "markers", "slow: mark test as slow running"
    )