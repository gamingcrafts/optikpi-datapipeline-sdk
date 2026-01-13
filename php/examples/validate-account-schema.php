<?php

/**
 * Account Schema Validation Example
 * 
 * Note: This is a simplified version. For full JSON schema validation in PHP,
 * you would typically use a library like justinrainbow/json-schema.
 * This example demonstrates basic validation logic.
 */

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\Models\AccountEvent;

// Load environment variables
$dotenv = parse_ini_file(__DIR__ . '/.env');

$ACCOUNT_ID = $dotenv['ACCOUNT_ID'] ?? getenv('ACCOUNT_ID');
$WORKSPACE_ID = $dotenv['WORKSPACE_ID'] ?? getenv('WORKSPACE_ID');

// Create test account event
$ACCOUNT_EVENT = new AccountEvent([
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
    'event_category' => 'Account',
    'event_name' => 'Player Registration',
    'event_id' => 'evt_123456789',
    'event_time' => '2024-01-15T10:30:00Z',
    'device' => 'desktop',
    'status' => 'verified',
    'affiliate_id' => 'aff_123',
    'partner_id' => 'partner_456',
    'campaign_code' => 'CAMPAIGN_001',
    'reason' => 'Registration completed successfully'
]);

/**
 * Validate account data
 */
function validateAccountData($accountEvent) {
    echo "🔍 Validating Account Event Data against Schema\n";
    echo "==============================================\n";
    
    echo "\n📋 Test Data:\n";
    echo json_encode($accountEvent->toArray(), JSON_PRETTY_PRINT) . "\n";
    
    // Validate using the model's validate method
    $validation = $accountEvent->validate();
    
    echo "\n✅ Validation Results:\n";
    echo "==============================================\n";
    
    if ($validation['isValid']) {
        echo "🎉 PASS: Test data is valid according to schema!\n";
        
        // Additional checks
        echo "\n📊 Data Analysis:\n";
        $dataArray = $accountEvent->toArray();
        echo "- Total fields: " . count($dataArray) . "\n";
        
        // Check required fields
        $requiredFields = ['account_id', 'workspace_id', 'user_id', 'event_name', 'event_id', 'event_time'];
        $missingRequired = array_filter($requiredFields, function($field) use ($dataArray) {
            return !isset($dataArray[$field]) || empty($dataArray[$field]);
        });
        
        if (count($missingRequired) === 0) {
            echo "✅ All required fields are present\n";
        } else {
            echo "❌ Missing required fields: " . implode(', ', $missingRequired) . "\n";
        }
        
        // Check enum values
        $validEventNames = [
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
        ];
        
        $eventNameValid = in_array($accountEvent->event_name, $validEventNames);
        $deviceValid = empty($accountEvent->device) || in_array($accountEvent->device, ['desktop', 'mobile', 'tablet', 'app']);
        $statusValid = empty($accountEvent->status) || in_array($accountEvent->status, ['verified', 'pending', 'failed', 'completed']);
        
        echo "✅ Event name valid: " . ($eventNameValid ? 'true' : 'false') . "\n";
        echo "✅ Device type valid: " . ($deviceValid ? 'true' : 'false') . "\n";
        echo "✅ Status valid: " . ($statusValid ? 'true' : 'false') . "\n";
        
    } else {
        echo "❌ FAIL: Test data has validation errors!\n";
        echo "\n🚨 Validation Errors:\n";
        foreach ($validation['errors'] as $index => $error) {
            echo ($index + 1) . ". $error\n";
        }
    }
    
    return $validation['isValid'];
}

// Run validation
if (php_sapi_name() === 'cli') {
    validateAccountData($ACCOUNT_EVENT);
}
