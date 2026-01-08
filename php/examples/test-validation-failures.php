<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Utils\Crypto;

// Load environment variables
$dotenv = parse_ini_file(__DIR__ . '/../.env');

$API_BASE_URL = $dotenv['API_BASE_URL'] ?? getenv('API_BASE_URL');
$AUTH_TOKEN = $dotenv['AUTH_TOKEN'] ?? getenv('AUTH_TOKEN');
$ACCOUNT_ID = $dotenv['ACCOUNT_ID'] ?? getenv('ACCOUNT_ID');
$WORKSPACE_ID = $dotenv['WORKSPACE_ID'] ?? getenv('WORKSPACE_ID');

// Validate required environment variables
if (empty($AUTH_TOKEN) || empty($ACCOUNT_ID) || empty($WORKSPACE_ID)) {
    echo "❌ Error: Missing required environment variables!\n";
    echo "   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID\n";
    echo "   Copy env.example to .env and fill in your values\n";
    exit(1);
}

// Initialize SDK
$sdk = new OptikpiDataPipelineSDK([
    'authToken' => $AUTH_TOKEN,
    'accountId' => $ACCOUNT_ID,
    'workspaceId' => $WORKSPACE_ID,
    'baseURL' => $API_BASE_URL
]);

// Valid base data for comparison
$VALID_CUSTOMER = [
    'account_id' => $ACCOUNT_ID,
    'workspace_id' => $WORKSPACE_ID,
    'user_id' => 'user123456',
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
    'iddoc_verification' => 'Verified'
];

// Test cases for validation failures
$VALIDATION_TEST_CASES = [
    // Customer validation failures
    'customer' => [
        'missing_required_fields' => [
            'name' => 'Missing Required Fields',
            'description' => 'Customer data missing essential required fields',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                // Missing: user_id, username, full_name, email, etc.
                'gender' => 'Male'
            ]
        ],
        'invalid_email_format' => [
            'name' => 'Invalid Email Format',
            'description' => 'Customer data with invalid email format',
            'data' => array_merge($VALID_CUSTOMER, [
                'email' => 'invalid-email-format'
            ])
        ],
        'invalid_gender_enum' => [
            'name' => 'Invalid Gender Enum',
            'description' => 'Customer data with invalid gender value',
            'data' => array_merge($VALID_CUSTOMER, [
                'gender' => 'Other' // Should be "Male", "Female", or null
            ])
        ],
        'invalid_date_format' => [
            'name' => 'Invalid Date Format',
            'description' => 'Customer data with invalid date format',
            'data' => array_merge($VALID_CUSTOMER, [
                'date_of_birth' => '15-01-1990' // Should be YYYY-MM-DD
            ])
        ],
        'invalid_account_status' => [
            'name' => 'Invalid Account Status',
            'description' => 'Customer data with invalid account status',
            'data' => array_merge($VALID_CUSTOMER, [
                'account_status' => 'Suspended' // Should be "Active", "Inactive", "Blocked"
            ])
        ]
    ],

    // Account event validation failures
    'account' => [
        'missing_required_fields' => [
            'name' => 'Missing Required Fields',
            'description' => 'Account event missing essential required fields',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                // Missing: user_id, event_category, event_name, event_id, event_time
                'device' => 'desktop'
            ]
        ],
        'invalid_event_name' => [
            'name' => 'Invalid Event Name',
            'description' => 'Account event with invalid event name',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                'user_id' => 'user123456',
                'event_category' => 'Account',
                'event_name' => 'Invalid Event', // Should be from enum
                'event_id' => 'evt_123456789',
                'event_time' => '2024-01-15T10:30:00Z',
                'device' => 'desktop',
                'status' => 'verified'
            ]
        ],
        'invalid_device_type' => [
            'name' => 'Invalid Device Type',
            'description' => 'Account event with invalid device type',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                'user_id' => 'user123456',
                'event_category' => 'Account',
                'event_name' => 'Player Registration',
                'event_id' => 'evt_123456789',
                'event_time' => '2024-01-15T10:30:00Z',
                'device' => 'smartwatch', // Should be "desktop", "mobile", "tablet"
                'status' => 'verified'
            ]
        ],
        'invalid_event_time_format' => [
            'name' => 'Invalid Event Time Format',
            'description' => 'Account event with invalid event time format',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                'user_id' => 'user123456',
                'event_category' => 'Account',
                'event_name' => 'Player Registration',
                'event_id' => 'evt_123456789',
                'event_time' => '2024-01-15 10:30:00', // Should be ISO 8601
                'device' => 'desktop',
                'status' => 'verified'
            ]
        ]
    ],

    // Deposit event validation failures
    'deposit' => [
        'missing_required_fields' => [
            'name' => 'Missing Required Fields',
            'description' => 'Deposit event missing essential required fields',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                // Missing: user_id, event_category, event_name, event_id, event_time, amount
                'payment_method' => 'bank'
            ]
        ],
        'invalid_amount_type' => [
            'name' => 'Invalid Amount Type',
            'description' => 'Deposit event with invalid amount type',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                'user_id' => 'user123456',
                'event_category' => 'Deposit',
                'event_name' => 'Successful Deposit',
                'event_id' => 'evt_dep_987654321',
                'event_time' => '2024-01-15T14:45:00Z',
                'amount' => '500.00', // Should be number, not string
                'payment_method' => 'bank',
                'transaction_id' => 'txn_123456789'
            ]
        ],
        'negative_amount' => [
            'name' => 'Negative Amount',
            'description' => 'Deposit event with negative amount',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                'user_id' => 'user123456',
                'event_category' => 'Deposit',
                'event_name' => 'Successful Deposit',
                'event_id' => 'evt_dep_987654321',
                'event_time' => '2024-01-15T14:45:00Z',
                'amount' => -500.00, // Should be positive
                'payment_method' => 'bank',
                'transaction_id' => 'txn_123456789'
            ]
        ],
        'invalid_payment_method' => [
            'name' => 'Invalid Payment Method',
            'description' => 'Deposit event with invalid payment method',
            'data' => [
                'account_id' => '68911b7ad58ad825ec00c5ef',
                'workspace_id' => '68911b7ad58ad825ec00c5ef',
                'user_id' => 'user123456',
                'event_category' => 'Deposit',
                'event_name' => 'Successful Deposit',
                'event_id' => 'evt_dep_987654321',
                'event_time' => '2024-01-15T14:45:00Z',
                'amount' => 500.00,
                'payment_method' => 'cryptocurrency', // Should be from enum
                'transaction_id' => 'txn_123456789'
            ]
        ]
    ]
];

// Make API request with headers
function makeApiRequest($sdk, $endpoint, $data) {
    try {
        // Determine method based on endpoint
        $method = 'customer';
        if (strpos($endpoint, '/events/account') !== false) {
            $method = 'account';
        } elseif (strpos($endpoint, '/events/deposit') !== false) {
            $method = 'deposit';
        }
        
        $result = null;
        switch ($method) {
            case 'customer':
                $result = $sdk->sendCustomerProfile($data);
                break;
            case 'account':
                $result = $sdk->sendAccountEvent($data);
                break;
            case 'deposit':
                $result = $sdk->sendDepositEvent($data);
                break;
        }
        
        return [
            'status' => $result['status'] ?? null,
            'data' => $result['data'] ?? null,
            'responseTime' => 0
        ];
    } catch (Exception $error) {
        return [
            'status' => null,
            'data' => null,
            'error' => $error->getMessage()
        ];
    }
}

// Test validation failures
function testValidationFailures($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $VALIDATION_TEST_CASES) {
    echo "🚨 Testing API Validation Failures\n";
    echo "==================================\n";
    echo "API Base URL: $API_BASE_URL\n";
    echo "Account ID: $ACCOUNT_ID\n";
    echo "Workspace ID: $WORKSPACE_ID\n";

    $results = [];

    // Test customer validation failures
    echo "\n👤 Testing Customer Validation Failures\n";
    echo "=======================================\n";
    
    foreach ($VALIDATION_TEST_CASES['customer'] as $testKey => $testCase) {
        try {
            echo "\n🔍 Testing: {$testCase['name']}\n";
            echo "📝 Description: {$testCase['description']}\n";
            echo str_repeat('─', 60) . "\n";
            
            $result = makeApiRequest($sdk, '/customers', $testCase['data']);
            
            // If we get here, the validation didn't fail as expected
            if (isset($result['status']) && $result['status'] >= 200 && $result['status'] < 300) {
                echo "⚠️  UNEXPECTED SUCCESS: {$testCase['name']}\n";
                echo "   Status: {$result['status']}\n";
                echo "   Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
                
                $results[] = [
                    'testKey' => $testKey,
                    'endpoint' => 'customer',
                    'testName' => $testCase['name'],
                    'expected' => 'FAILURE',
                    'actual' => 'SUCCESS',
                    'status' => $result['status'],
                    'data' => $result['data']
                ];
            } else {
                echo "✅ EXPECTED FAILURE: {$testCase['name']}\n";
                echo "   HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
                echo "   Error Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
                
                $results[] = [
                    'testKey' => $testKey,
                    'endpoint' => 'customer',
                    'testName' => $testCase['name'],
                    'expected' => 'FAILURE',
                    'actual' => 'FAILURE',
                    'httpStatus' => $result['status'],
                    'error' => $result['data']
                ];
            }

        } catch (Exception $error) {
            echo "❌ UNEXPECTED ERROR: {$testCase['name']}\n";
            echo "   Error: {$error->getMessage()}\n";
            
            $results[] = [
                'testKey' => $testKey,
                'endpoint' => 'customer',
                'testName' => $testCase['name'],
                'expected' => 'FAILURE',
                'actual' => 'ERROR',
                'error' => $error->getMessage()
            ];
        }
    }

    // Test account validation failures
    echo "\n📊 Testing Account Event Validation Failures\n";
    echo "============================================\n";
    
    foreach ($VALIDATION_TEST_CASES['account'] as $testKey => $testCase) {
        try {
            echo "\n🔍 Testing: {$testCase['name']}\n";
            echo "📝 Description: {$testCase['description']}\n";
            echo str_repeat('─', 60) . "\n";
            
            $result = makeApiRequest($sdk, '/events/account', $testCase['data']);
            
            if (isset($result['status']) && $result['status'] >= 200 && $result['status'] < 300) {
                echo "⚠️  UNEXPECTED SUCCESS: {$testCase['name']}\n";
                echo "   Status: {$result['status']}\n";
                echo "   Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
                
                $results[] = [
                    'testKey' => $testKey,
                    'endpoint' => 'account',
                    'testName' => $testCase['name'],
                    'expected' => 'FAILURE',
                    'actual' => 'SUCCESS',
                    'status' => $result['status'],
                    'data' => $result['data']
                ];
            } else {
                echo "✅ EXPECTED FAILURE: {$testCase['name']}\n";
                echo "   HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
                echo "   Error Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
                
                $results[] = [
                    'testKey' => $testKey,
                    'endpoint' => 'account',
                    'testName' => $testCase['name'],
                    'expected' => 'FAILURE',
                    'actual' => 'FAILURE',
                    'httpStatus' => $result['status'],
                    'error' => $result['data']
                ];
            }

        } catch (Exception $error) {
            echo "❌ UNEXPECTED ERROR: {$testCase['name']}\n";
            echo "   Error: {$error->getMessage()}\n";
            
            $results[] = [
                'testKey' => $testKey,
                'endpoint' => 'account',
                'testName' => $testCase['name'],
                'expected' => 'FAILURE',
                'actual' => 'ERROR',
                'error' => $error->getMessage()
            ];
        }
    }

    // Test deposit validation failures
    echo "\n💰 Testing Deposit Event Validation Failures\n";
    echo "============================================\n";
    
    foreach ($VALIDATION_TEST_CASES['deposit'] as $testKey => $testCase) {
        try {
            echo "\n🔍 Testing: {$testCase['name']}\n";
            echo "📝 Description: {$testCase['description']}\n";
            echo str_repeat('─', 60) . "\n";
            
            $result = makeApiRequest($sdk, '/events/deposit', $testCase['data']);
            
            if (isset($result['status']) && $result['status'] >= 200 && $result['status'] < 300) {
                echo "⚠️  UNEXPECTED SUCCESS: {$testCase['name']}\n";
                echo "   Status: {$result['status']}\n";
                echo "   Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
                
                $results[] = [
                    'testKey' => $testKey,
                    'endpoint' => 'deposit',
                    'testName' => $testCase['name'],
                    'expected' => 'FAILURE',
                    'actual' => 'SUCCESS',
                    'status' => $result['status'],
                    'data' => $result['data']
                ];
            } else {
                echo "✅ EXPECTED FAILURE: {$testCase['name']}\n";
                echo "   HTTP Status: " . ($result['status'] ?? 'N/A') . "\n";
                echo "   Error Response: " . json_encode($result['data'], JSON_PRETTY_PRINT) . "\n";
                
                $results[] = [
                    'testKey' => $testKey,
                    'endpoint' => 'deposit',
                    'testName' => $testCase['name'],
                    'expected' => 'FAILURE',
                    'actual' => 'FAILURE',
                    'httpStatus' => $result['status'],
                    'error' => $result['data']
                ];
            }

        } catch (Exception $error) {
            echo "❌ UNEXPECTED ERROR: {$testCase['name']}\n";
            echo "   Error: {$error->getMessage()}\n";
            
            $results[] = [
                'testKey' => $testKey,
                'endpoint' => 'deposit',
                'testName' => $testCase['name'],
                'expected' => 'FAILURE',
                'actual' => 'ERROR',
                'error' => $error->getMessage()
            ];
        }
    }

    // Summary
    echo "\n📊 Validation Test Summary\n";
    echo "==========================\n";
    
    $expectedFailures = count(array_filter($results, function($r) {
        return $r['expected'] === 'FAILURE' && $r['actual'] === 'FAILURE';
    }));
    $unexpectedSuccesses = count(array_filter($results, function($r) {
        return $r['expected'] === 'FAILURE' && $r['actual'] === 'SUCCESS';
    }));
    $unexpectedErrors = count(array_filter($results, function($r) {
        return $r['actual'] === 'ERROR';
    }));
    $totalTests = count($results);
    
    echo "✅ Expected Failures: $expectedFailures\n";
    echo "⚠️  Unexpected Successes: $unexpectedSuccesses\n";
    echo "❌ Unexpected Errors: $unexpectedErrors\n";
    echo "📈 Total Tests: $totalTests\n";
    echo "🎯 Validation Effectiveness: " . round(($expectedFailures / $totalTests) * 100, 1) . "%\n";
    
    if ($unexpectedSuccesses > 0) {
        echo "\n⚠️  Tests that should have failed but succeeded:\n";
        foreach (array_filter($results, function($r) {
            return $r['expected'] === 'FAILURE' && $r['actual'] === 'SUCCESS';
        }) as $result) {
            echo "   - {$result['testName']} ({$result['endpoint']})\n";
        }
    }

    if ($unexpectedErrors > 0) {
        echo "\n❌ Tests with unexpected errors:\n";
        foreach (array_filter($results, function($r) {
            return $r['actual'] === 'ERROR';
        }) as $result) {
            echo "   - {$result['testName']} ({$result['endpoint']}): {$result['error']}\n";
        }
    }

    return $results;
}

// Test authentication failures
function testAuthenticationFailures($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN, $VALID_CUSTOMER) {
    echo "\n🔐 Testing Authentication Failures\n";
    echo "===================================\n";
    
    // Note: SDK automatically handles authentication, so these tests are limited
    // In a real scenario, you would need direct HTTP client access to test missing headers
    echo "⚠️  Note: SDK automatically adds authentication headers\n";
    echo "   These tests would require direct HTTP client access\n";
    
    return [];
}

// Run all failure tests
function runFailureTests($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN, $VALIDATION_TEST_CASES, $VALID_CUSTOMER) {
    try {
        echo "🚨 Starting Validation and Authentication Failure Tests\n";
        echo "========================================================\n";
        
        // Test validation failures
        $validationResults = testValidationFailures($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $VALIDATION_TEST_CASES);
        
        // Test authentication failures
        $authResults = testAuthenticationFailures($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN, $VALID_CUSTOMER);
        
        echo "\n🎉 All failure tests completed!\n";
        return [
            'validation' => $validationResults,
            'authentication' => $authResults
        ];
    } catch (Exception $error) {
        echo "\n💥 Test execution failed: {$error->getMessage()}\n";
        throw $error;
    }
}

// Run if this file is executed directly
if (php_sapi_name() === 'cli') {
    runFailureTests($sdk, $API_BASE_URL, $ACCOUNT_ID, $WORKSPACE_ID, $AUTH_TOKEN, $VALIDATION_TEST_CASES, $VALID_CUSTOMER);
}
