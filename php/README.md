# Optikpi Data Pipeline API PHP SDK

This directory contains the PHP SDK for the Optikpi Data Pipeline API.

## Prerequisites

- PHP 7.4 or higher
- Composer
- Valid Optikpi API credentials
- Required PHP extensions:
  - `ext-json`
  - `ext-curl`
  - `ext-openssl`

## Installation

### Using Composer

```bash
composer require optikpi/datapipeline-sdk
```

### Manual Installation

1. Clone or download this repository
2. Navigate to the `php` directory
3. Install dependencies:

```bash
composer install
```

## Setup

Create a `.env` file in the `php` directory with your Optikpi credentials:

```ini
API_BASE_URL=https://your-api-gateway-url/apigw/ingest
AUTH_TOKEN=your_actual_auth_token
ACCOUNT_ID=your_actual_account_id
WORKSPACE_ID=your_actual_workspace_id
```

## Usage

### Basic Usage

```php
<?php

require_once 'vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\CustomerProfile;

// Initialize SDK
$sdk = new OptikpiDataPipelineSDK([
    'baseURL' => 'https://your-api-gateway-url/apigw/ingest',
    'authToken' => 'your_auth_token',
    'accountId' => 'your_account_id',
    'workspaceId' => 'your_workspace_id'
]);

// Create customer profile
$customer = new CustomerProfile([
    'account_id' => 'your_account_id',
    'workspace_id' => 'your_workspace_id',
    'user_id' => 'user123456',
    'username' => 'john_doe',
    'email' => 'john.doe@example.com',
    'account_status' => 'Active'
]);

// Validate before sending
$validation = $customer->validate();
if (!$validation['isValid']) {
    foreach ($validation['errors'] as $error) {
        echo "Error: $error\n";
    }
    exit(1);
}

// Send customer profile
$result = $sdk->sendCustomerProfile($customer);

if ($result['success']) {
    echo "Success! Status: " . $result['status'] . "\n";
    echo "Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
} else {
    echo "Error: " . $result['error'] . "\n";
}
```

### Sending Events

```php
use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\DepositEvent;

// Account Event
$accountEvent = new AccountEvent([
    'account_id' => 'your_account_id',
    'workspace_id' => 'your_workspace_id',
    'user_id' => 'user123456',
    'event_name' => 'Login',
    'event_id' => 'event_' . time(),
    'event_time' => date('c')
]);

$result = $sdk->sendAccountEvent($accountEvent);

// Deposit Event
$depositEvent = new DepositEvent([
    'account_id' => 'your_account_id',
    'workspace_id' => 'your_workspace_id',
    'user_id' => 'user123456',
    'event_name' => 'Successful Deposit',
    'event_id' => 'deposit_' . time(),
    'event_time' => date('c'),
    'amount' => 100.00,
    'payment_method' => 'credit_card',
    'transaction_id' => 'txn_' . time()
]);

$result = $sdk->sendDepositEvent($depositEvent);
```

### Batch Operations

```php
$batchData = [
    'customers' => [$customer->toArray()],
    'accountEvents' => [$accountEvent->toArray()],
    'depositEvents' => [$depositEvent->toArray()],
    'withdrawEvents' => [$withdrawEvent->toArray()],
    'gamingEvents' => [$gamingEvent->toArray()],
    'walletBalanceEvents' => [$walletBalanceEvent->toArray()],
    'referFriendEvents' => [$referFriendEvent->toArray()],
    'extendedAttributes' => [$extendedAttributes->toArray()]
];

$result = $sdk->sendBatch($batchData);
```

## Available Models

- `CustomerProfile` - Customer profile information
- `AccountEvent` - Account-related events (login, logout, etc.)
- `DepositEvent` - Deposit transactions
- `WithdrawEvent` - Withdrawal transactions
- `GamingActivityEvent` - Gaming activity events
- `WalletBalanceEvent` - Wallet balance snapshots
- `ReferFriendEvent` - Referral activities
- `CustomerExtEvent` - Extended customer attributes

## Running Examples

Navigate to the `examples` directory:

```bash
cd examples
```

### Test Customer Profile Endpoint

```bash
php test-customer-endpoint.php
```

### Test Batch Operations

```bash
php test-batch-operations.php
```

## API Methods

### OptikpiDataPipelineSDK

- `sendCustomerProfile($data)` - Send customer profile data
- `sendAccountEvent($data)` - Send account event data
- `sendDepositEvent($data)` - Send deposit event data
- `sendWithdrawEvent($data)` - Send withdrawal event data
- `sendGamingActivityEvent($data)` - Send gaming activity event data
- `sendWalletBalanceEvent($data)` - Send wallet balance event data
- `sendReferFriendEvent($data)` - Send refer friend event data
- `sendExtendedAttributes($data)` - Send extended attributes data
- `sendBatch($batchData)` - Send multiple events in batch
- `updateConfig($newConfig)` - Update client configuration
- `getConfig()` - Get current configuration

## Configuration Options

- `baseURL` (required) - API base URL
- `authToken` (required) - Authentication token
- `accountId` (required) - Account ID
- `workspaceId` (required) - Workspace ID
- `timeout` (optional) - Request timeout in milliseconds (default: 30000)
- `retries` (optional) - Number of retry attempts (default: 3)
- `retryDelay` (optional) - Delay between retries in milliseconds (default: 1000)

## Error Handling

All SDK methods return an array with the following structure:

```php
[
    'success' => true|false,
    'status' => 200, // HTTP status code
    'data' => [...], // Response data
    'error' => 'Error message', // Only present if success is false
    'timestamp' => '2024-01-15T10:30:00Z'
]
```

## Security

The SDK implements secure authentication using:
- HMAC-SHA256 signatures
- HKDF key derivation
- Secure header transmission
- Input validation and sanitization

## Troubleshooting

### Common Issues

1. **Missing credentials**: Ensure all required environment variables are set
2. **Network issues**: Check your internet connection and firewall settings
3. **Invalid data**: Use the validation methods to check your data before sending
4. **Authentication errors**: Verify your auth token, account ID, and workspace ID
5. **PHP extensions**: Ensure required PHP extensions are installed and enabled

### Debugging

Enable verbose error reporting:

```php
error_reporting(E_ALL);
ini_set('display_errors', 1);
```

Check the response structure:

```php
$result = $sdk->sendCustomerProfile($customer);
var_dump($result);
```

## License

MIT

## Support

For issues and questions, please visit: https://github.com/gamingcrafts/optikpi-datapipeline-sdk/issues

