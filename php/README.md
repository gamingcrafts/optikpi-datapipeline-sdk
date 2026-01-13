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

## Setup

- **Update the env file** with your Optikpi credentials:
```bash
   # Edit the '.env' file with your actual credentials
 ```
 ```
    API_BASE_URL=https://your-api-gateway-url/apigw/ingest
    AUTH_TOKEN=your_actual_auth_token
    ACCOUNT_ID=your_actual_account_id
    WORKSPACE_ID=your_actual_workspace_id
```
## Running Examples
- Before executing the file, please change your current path to:
cd ../php/examples

### Test Customer Profile Endpoint

```bash
php test-customer-endpoint.php
```

### Test Customer Extended Attribute Endpoint
```bash
php test-customerext-endpoint.php
```

### Test All Endpoints
```bash
php test-all-endpoints.php
```

### Test Account Events
```bash
php test-account-endpoint.php
```

### Test Deposit Events
```bash
php test-deposit-endpoint.php
```

### Test Withdrawal Events
```bash
php test-withdraw-endpoint.php
```

### Test Gaming Activity Events
```bash
php test-gaming-endpoint.php
```

### Test ReferFriend Events
```bash
php test-refer-friend-endpoint.php
```

### Test WalletBalance Events
```bash
php test-wallet-balance-endpoint.php
```
### Test BatchData Operations
```bash
php test-batch-operations.php
```

## Example Code

### Basic Usage

```php
<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\CustomerProfile;

$dotenv = parse_ini_file(__DIR__ . '/.env');


// Initialize SDK
$sdk = new OptikpiDataPipelineSDK([
    'authToken' => $AUTH_TOKEN,
    'accountId' => $ACCOUNT_ID,
    'workspaceId' => $WORKSPACE_ID,
    'baseURL' => $API_BASE_URL
]);

// Customer data
$customer = new CustomerProfile([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'field04prod',
    'username' => 'john_doe',
    'full_name' => 'John Doe',
    'first_name' => 'John',
    'last_name' => 'Doe',
    'date_of_birth' => '1990-01-15',
    'email' => 'john.doe@example.com',
    'phone_number' => '+1234567890',
    'gender' => 'Male',
    'country' => 'United States',
    'city' => 'New York',
    'language' => 'en',
    'currency' => 'USD',
    'marketing_email_preference' => 'Opt-in',
    'notifications_preference' => 'Opt-in',
    'subscription' => 'Subscribed',
    'privacy_settings' => 'private',
    'deposit_limits' => 1000.00,
    'loss_limits' => 500.00,
    'wagering_limits' => 2000.00,
    'session_time_limits' => 120,
    'reality_checks_notification' => 'daily',
    'account_status' => 'Active',
    'vip_status' => 'Regular',
    'loyalty_program_tiers' => 'Bronze',
    'bonus_abuser' => 'Not flagged',
    'financial_risk_level' => 0.3,
    'acquisition_source' => 'Google Ads',
    'partner_id' => 'partner123',
    'referral_link_code' => 'REF789',
    'referral_limit_reached' => 'Not Reached',
    'creation_timestamp' => '2024-01-15T10:30:00Z',
    'phone_verification' => 'Verified',
    'email_verification' => 'Verified',
    'bank_verification' => 'NotVerified',
    'iddoc_verification' => 'Verified',
    'cooling_off_expiry_date' => '2024-12-31T23:59:59Z',
    'self_exclusion_expiry_date' => '2025-01-31T23:59:59Z',
    'risk_score_level' => 'low',
    'marketing_sms_preference' => 'Opt-in',
    'custom_data' => [
        'favorite_game' => 'slots',
        'newsletter_signup' => true
    ],
    'self_exclusion_by' => 'player',
    'self_exclusion_by_type' => 'voluntary',
    'self_exclusion_check_time' => '2024-01-15T10:30:00Z',
    'self_exclusion_created_time' => '2024-01-01T00:00:00Z',
    'closed_time' => null,
    'real_money_enabled' => 'true',
    'push_token' => 'push_token_abc123',
    'android_push_token' => 'android_push_token_xyz456',
    'ios_push_token' => 'ios_push_token_def789',
    'windows_push_token' => 'windows_push_token_ghi012',
    'mac_dmg_push_token' => 'mac_push_token_jkl345'
]);

$validation = $customer->validate();
if (!$validation['isValid']) {
    echo "❌ Validation errors:\n";
    foreach ($validation['errors'] as $error) {
        echo "  - $error\n";
    }
    exit(1);
}
echo "✅ Customer event validated successfully!\n";

// Send customer profile
 if ($result['success']) {
            echo "\n✅ Success!\n";
            echo "============================\n";
            echo "HTTP Status: " . $result['status'] . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Response Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        } else {
            echo "\n❌ API Error!\n";
            echo "============================\n";
            echo "HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
            echo "Response Time: " . round($endTime - $startTime) . "ms\n";
            echo "SDK Success: " . ($result['success'] ? 'true' : 'false') . "\n";
            echo "Error Data: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
        }
```
### Batch Operations
```php
$batchData = [
            'customers' => [createSampleCustomer($accountId, $workspaceId)],
            'extendedAttributes' => [
                createSampleExtendedAttributesMapFormat($accountId, $workspaceId),
                createSampleExtendedAttributesStringFormat($accountId, $workspaceId)
            ],
            'accountEvents' => [createSampleAccountEvent($accountId, $workspaceId)],
            'depositEvents' => [createSampleDepositEvent($accountId, $workspaceId)],
            'withdrawEvents' => [createSampleWithdrawEvent($accountId, $workspaceId)],
            'gamingEvents' => [createSampleGamingActivityEvent($accountId, $workspaceId)],
            'referFriendEvents' => [createSampleReferFriendEvent($accountId, $workspaceId)],
            'walletBalanceEvents' => [createSampleWalletBalanceEvent($accountId, $workspaceId)]
        ];
$response = $sdk->sendBatch($batchData);
```


## Troubleshooting

```bash
### Common Issues

1. Missing credentials: Ensure all required environment variables are set
2. Network issues: Check your internet connection and firewall settings
3. Invalid data: Use the validation methods to check your data before sending
4. Authentication errors: Verify your auth token, account ID, and workspace ID
```
