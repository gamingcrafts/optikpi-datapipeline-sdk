<?php

require_once __DIR__ . '/../vendor/autoload.php';

use Optikpi\DataPipeline\OptikpiDataPipelineSDK;
use Optikpi\DataPipeline\Models\CustomerProfile;
use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\DepositEvent;
use Optikpi\DataPipeline\Models\WithdrawEvent;
use Optikpi\DataPipeline\Models\GamingActivityEvent;
use Optikpi\DataPipeline\Models\ReferFriendEvent;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;
use Optikpi\DataPipeline\Models\CustomerExtEvent;

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

/**
 * Create sample customer profile
 */
function createSampleCustomer($accountId, $workspaceId) {
    return new CustomerProfile([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'js_field04prod',
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
}

/**
 * Create sample extended attributes event - Map format
 */
function createSampleExtendedAttributesMapFormat($accountId, $workspaceId) {
    return new CustomerExtEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user0112',
        'list_name' => 'BINGO_PREFERENCES',
        'ext_data' => [
            'Email' => 'True',
            'SMS' => 'True',
            'PushNotifications' => 'False'
        ]
    ]);
}

/**
 * Create sample extended attributes event - String format
 */
function createSampleExtendedAttributesStringFormat($accountId, $workspaceId) {
    return new CustomerExtEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user0113',
        'list_name' => 'GAMING_PREFERENCES',
        'ext_data' => json_encode([
            'Email' => 'True',
            'SMS' => 'True',
            'PushNotifications' => 'True'
        ])
    ]);
}

/**
 * Create sample account event
 */
function createSampleAccountEvent($accountId, $workspaceId) {
    return new AccountEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user0114',
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
}

/**
 * Create sample deposit event
 */
function createSampleDepositEvent($accountId, $workspaceId) {
    return new DepositEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user123456',
        'event_category' => 'Deposit',
        'event_name' => 'Successful Deposit',
        'event_id' => 'evt_dep_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'amount' => 500.00,
        'payment_method' => 'bank',
        'transaction_id' => 'txn_123456789',
        'payment_provider_id' => 'provider123',
        'payment_provider_name' => 'Chase Bank',
        'failure_reason' => null
    ]);
}

/**
 * Create sample withdraw event
 */
function createSampleWithdrawEvent($accountId, $workspaceId) {
    return new WithdrawEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user123456',
        'event_category' => 'Withdraw',
        'event_name' => 'Successful Withdrawal',
        'event_id' => 'evt_wd_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'amount' => 250.00,
        'payment_method' => 'bank',
        'transaction_id' => 'txn_wd_123456789',
        'failure_reason' => null
    ]);
}

/**
 * Create sample gaming activity event
 */
function createSampleGamingActivityEvent($accountId, $workspaceId) {
    return new GamingActivityEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user123411',
        'event_category' => 'Gaming Activity',
        'event_name' => 'Play Casino Game',
        'event_id' => 'evt_' . (time() * 1000),
        'event_time' => date('c'),
        'wager_amount' => 10.00,
        'win_amount' => 25.00,
        'loss_amount' => 0.00,
        'game_id' => 'game_001',
        'game_title' => 'Mega Fortune Slots',
        'provider' => 'ProviderXYZ',
        'bonus_id' => 'bonus_12345',
        'free_spin_id' => 'freespin_67890',
        'num_spins_played' => 50,
        'game_theme' => 'Egyptian',
        'remaining_spins' => 10,
        'bet_value_per_spin' => 0.50,
        'wagering_requirements_met' => true,
        'free_spin_expiry_date' => '2024-12-31T23:59:59Z',
        'campaign_id' => 'camp_summer2024',
        'campaign_name' => 'Summer Bonanza',
        'rtp' => 96.5,
        'game_category' => 'slots',
        'winning_bet_amount' => 25.00,
        'jackpot_type' => 'progressive',
        'volatility' => 'high',
        'min_bet' => 0.10,
        'max_bet' => 100.00,
        'number_of_reels' => 5,
        'number_of_paylines' => 20,
        'feature_types' => 'wild,scatter,freespins',
        'game_release_date' => '2023-01-15T00:00:00Z',
        'live_dealer_availability' => false,
        'side_bets_availability' => true,
        'multiplayer_option' => false,
        'auto_play' => true,
        'poker_variant' => 'texas_holdem',
        'tournament_name' => 'Weekend Warriors',
        'buy_in_amount' => 50.00,
        'table_type' => 'cash_game',
        'stakes_level' => 'medium',
        'number_of_players' => 6,
        'game_duration' => 45,
        'hand_volume' => 120,
        'player_position' => 'button',
        'final_hand' => 'royal_flush',
        'rake_contribution' => 2.50,
        'multi_tabling_indicator' => false,
        'session_result' => 'win',
        'vip_status' => 'gold',
        'blind_level' => '50/100',
        'rebuy_and_addon_info' => '1 rebuy, 1 addon',
        'sport_type' => 'football',
        'betting_market' => 'match_winner',
        'odds' => 2.50,
        'live_betting_availability' => true,
        'result' => 'won',
        'bet_status' => 'settled',
        'betting_channel' => 'online',
        'bonus_type' => 'welcome_bonus',
        'bonus_amount' => 100.00,
        'free_spin_start_date' => '2024-01-01T00:00:00Z',
        'num_spins_awarded' => 20,
        'bonus_code' => 'WELCOME100',
        'parent_game_category' => 'casino',
        'currency' => 'USD',
        'money_type' => 'real',
        'transaction_type' => 'bet'
    ]);
}

/**
 * Create sample refer friend event
 */
function createSampleReferFriendEvent($accountId, $workspaceId) {
    return new ReferFriendEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user0118',
        'event_category' => 'Refer Friend',
        'event_name' => 'Referral Successful',
        'event_id' => 'evt_rf_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'referral_code_used' => 'REF123456',
        'successful_referral_confirmation' => true,
        'reward_type' => 'bonus',
        'reward_claimed_status' => 'claimed',
        'referee_user_id' => 'user789012',
        'referee_registration_date' => '2024-01-15T10:30:00Z',
        'referee_first_deposit' => 100.00
    ]);
}

/**
 * Create sample wallet balance event
 */
function createSampleWalletBalanceEvent($accountId, $workspaceId) {
    return new WalletBalanceEvent([
        'account_id' => $accountId,
        'workspace_id' => $workspaceId,
        'user_id' => 'user0119',
        'event_category' => 'Wallet Balance',
        'event_name' => 'Balance Update',
        'event_id' => 'evt_wb_987654321',
        'event_time' => '2024-01-15T14:45:00Z',
        'wallet_type' => 'main',
        'currency' => 'USD',
        'current_cash_balance' => 1250.50,
        'current_bonus_balance' => 100.00,
        'current_total_balance' => 1350.50,
        'blocked_amount' => 50.00
    ]);
}

/**
 * Print validation result
 */
function printValidationResult($result, $eventName) {
    if (!$result['isValid']) {
        echo "❌ Invalid $eventName:\n";
        echo "Errors: " . json_encode($result['errors']) . "\n";
    } else {
        echo "✅ Valid $eventName\n";
    }
}

function printData($data) {
    $className = get_class($data);
    echo "\n🔍 Testing: $className\n";
    echo str_repeat('─', 50) . "\n";
    echo "📋 Data: " . json_encode($data->toArray(), JSON_PRETTY_PRINT) . "\n";
}

/**
 * Validate batch data
 */
function validateBatchData($batch) {
    echo "\n=== Validating BatchData contents ===\n\n";

    if (isset($batch['customers']) && $batch['customers'] !== null) {
        foreach ($batch['customers'] as $c) {
            if ($c instanceof CustomerProfile) {
                $result = $c->validate();
                printValidationResult($result, 'CustomerProfile (Batch)');
                printData($c);
            }
        }
    }

    if (isset($batch['extendedAttributes']) && $batch['extendedAttributes'] !== null) {
        foreach ($batch['extendedAttributes'] as $ea) {
            if ($ea instanceof CustomerExtEvent) {
                $result = $ea->validate();
                printValidationResult($result, 'ExtendedAttributes (Batch)');
                printData($ea);
            }
        }
    }

    if (isset($batch['accountEvents']) && $batch['accountEvents'] !== null) {
        foreach ($batch['accountEvents'] as $a) {
            if ($a instanceof AccountEvent) {
                $result = $a->validate();
                printValidationResult($result, 'AccountEvent (Batch)');
                printData($a);
            }
        }
    }

    if (isset($batch['depositEvents']) && $batch['depositEvents'] !== null) {
        foreach ($batch['depositEvents'] as $d) {
            if ($d instanceof DepositEvent) {
                $result = $d->validate();
                printValidationResult($result, 'DepositEvent (Batch)');
                printData($d);
            }
        }
    }

    if (isset($batch['withdrawEvents']) && $batch['withdrawEvents'] !== null) {
        foreach ($batch['withdrawEvents'] as $w) {
            if ($w instanceof WithdrawEvent) {
                $result = $w->validate();
                printValidationResult($result, 'WithdrawEvent (Batch)');
                printData($w);
            }
        }
    }

    if (isset($batch['gamingEvents']) && $batch['gamingEvents'] !== null) {
        foreach ($batch['gamingEvents'] as $g) {
            if ($g instanceof GamingActivityEvent) {
                $result = $g->validate();
                printValidationResult($result, 'GamingActivityEvent (Batch)');
                printData($g);
            }
        }
    }

    if (isset($batch['referFriendEvents']) && $batch['referFriendEvents'] !== null) {
        foreach ($batch['referFriendEvents'] as $r) {
            if ($r instanceof ReferFriendEvent) {
                $result = $r->validate();
                printValidationResult($result, 'ReferFriendEvent (Batch)');
                printData($r);
            }
        }
    }

    if (isset($batch['walletBalanceEvents']) && $batch['walletBalanceEvents'] !== null) {
        foreach ($batch['walletBalanceEvents'] as $wb) {
            if ($wb instanceof WalletBalanceEvent) {
                $result = $wb->validate();
                printValidationResult($result, 'WalletBalanceEvent (Batch)');
                printData($wb);
            }
        }
    }
}

/**
 * Test batch operations
 */
function testBatchOperations($sdk, $accountId, $workspaceId) {
    echo "\n=== Batch Operations ===\n\n";

    try {
        // Create batch data object using camelCase (matching SDK expectations)
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

        // Validate batch data
        validateBatchData($batchData);

        echo "\n📦 Batch Data Summary:\n";
        echo "   Customers: " . (isset($batchData['customers']) ? count($batchData['customers']) : 0) . "\n";
        echo "   Extended Attributes: " . (isset($batchData['extendedAttributes']) ? count($batchData['extendedAttributes']) : 0) . "\n";
        echo "   Account Events: " . (isset($batchData['accountEvents']) ? count($batchData['accountEvents']) : 0) . "\n";
        echo "   Deposit Events: " . (isset($batchData['depositEvents']) ? count($batchData['depositEvents']) : 0) . "\n";
        echo "   Withdraw Events: " . (isset($batchData['withdrawEvents']) ? count($batchData['withdrawEvents']) : 0) . "\n";
        echo "   Gaming Events: " . (isset($batchData['gamingEvents']) ? count($batchData['gamingEvents']) : 0) . "\n";
        echo "   Refer Friend Events: " . (isset($batchData['referFriendEvents']) ? count($batchData['referFriendEvents']) : 0) . "\n";
        echo "   Wallet Balance Events: " . (isset($batchData['walletBalanceEvents']) ? count($batchData['walletBalanceEvents']) : 0) . "\n";

        echo "\n📤 Sending batch request to API...\n\n";
        
        // Debug: Log what we're actually sending
        echo "🔍 Request Payload Preview:\n";
        $payloadPreview = [
            'customers' => isset($batchData['customers']) ? count($batchData['customers']) : 0,
            'extendedAttributes' => isset($batchData['extendedAttributes']) ? count($batchData['extendedAttributes']) : 0,
            'accountEvents' => isset($batchData['accountEvents']) ? count($batchData['accountEvents']) : 0,
            'depositEvents' => isset($batchData['depositEvents']) ? count($batchData['depositEvents']) : 0,
            'withdrawEvents' => isset($batchData['withdrawEvents']) ? count($batchData['withdrawEvents']) : 0,
            'gamingEvents' => isset($batchData['gamingEvents']) ? count($batchData['gamingEvents']) : 0,
            'referFriendEvents' => isset($batchData['referFriendEvents']) ? count($batchData['referFriendEvents']) : 0,
            'walletBalanceEvents' => isset($batchData['walletBalanceEvents']) ? count($batchData['walletBalanceEvents']) : 0
        ];
        echo json_encode($payloadPreview, JSON_PRETTY_PRINT) . "\n\n";
        
        // Send batch request
        $response = $sdk->sendBatch($batchData);

        echo "\n=== API Response ===\n\n";
        echo "📊 Full Response Object:\n";
        echo json_encode($response, JSON_PRETTY_PRINT) . "\n";
        echo "\n" . str_repeat('─', 50) . "\n";

        if ($response['success']) {
            echo "\n✅ Batch operation completed successfully!\n";
            echo "⏰ Timestamp: " . ($response['timestamp'] ?? date('c')) . "\n";

            // Handle different response structures - Check for 'results' property
            if (isset($response['results'])) {
                echo "\n📋 Detailed Results:\n\n";

                // Check each event type with safe property access
                $checkResult = function($key, $displayName) use ($response) {
                    if (isset($response['results'][$key]) && $response['results'][$key] !== null) {
                        $result = $response['results'][$key];
                        echo "\n$displayName:\n";
                        
                        // Try different possible response formats
                        if (is_array($result)) {
                            if (isset($result['success'])) {
                                echo "  " . ($result['success'] ? '✅' : '❌') . " Result: " . ($result['success'] ? 'Success' : 'Failed') . "\n";
                            }
                            if (isset($result['status'])) {
                                echo "  📊 HTTP Status: " . $result['status'] . "\n";
                            }
                            if (isset($result['error'])) {
                                echo "  🔴 Error: " . $result['error'] . "\n";
                            }
                            if (isset($result['message'])) {
                                echo "  💬 Message: " . $result['message'] . "\n";
                            }
                            if (isset($result['data']['message'])) {
                                echo "  💬 Data Message: " . $result['data']['message'] . "\n";
                            }
                            if (isset($result['data']['validRecordsCount'])) {
                                echo "  ✅ Valid Records: " . $result['data']['validRecordsCount'] . "/" . $result['data']['totalRecordsCount'] . "\n";
                            }
                            if (isset($result['data']['validationErrors']) && is_array($result['data']['validationErrors']) && count($result['data']['validationErrors']) > 0) {
                                echo "  ⚠️  Validation Errors:\n";
                                foreach ($result['data']['validationErrors'] as $err) {
                                    echo "    Record " . $err['recordIndex'] . ":\n";
                                    foreach ($err['errors'] as $e) {
                                        echo "      - Field: " . $e['field'] . "\n";
                                        echo "        Message: " . $e['message'] . "\n";
                                        echo "        Value: " . json_encode($e['value']) . "\n";
                                    }
                                }
                            }
                            if (isset($result['count'])) {
                                echo "  🔢 Count: " . $result['count'] . "\n";
                            }
                        } else if (is_bool($result)) {
                            echo "  " . ($result ? '✅' : '❌') . " Result: " . ($result ? 'Success' : 'Failed') . "\n";
                        } else if (is_string($result)) {
                            echo "  📝 Result: $result\n";
                        }
                    }
                };

                $checkResult('customers', '👤 Customer Profiles');
                $checkResult('extendedAttributes', '🔧 Extended Attributes');
                $checkResult('accountEvents', '📝 Account Events');
                $checkResult('depositEvents', '💰 Deposit Events');
                $checkResult('withdrawEvents', '💸 Withdraw Events');
                $checkResult('gamingEvents', '🎮 Gaming Events');
                $checkResult('referFriendEvents', '👥 Refer Friend Events');
                $checkResult('walletBalanceEvents', '💳 Wallet Balance Events');

                // Check for missing results
                $expectedKeys = [
                    'customers', 
                    'extendedAttributes', 
                    'accountEvents', 
                    'depositEvents', 
                    'withdrawEvents', 
                    'gamingEvents', 
                    'referFriendEvents', 
                    'walletBalanceEvents'
                ];
                
                $receivedKeys = array_keys($response['results']);
                $missingKeys = array_filter($expectedKeys, function($key) use ($receivedKeys) {
                    return !in_array($key, $receivedKeys);
                });
                
                if (count($missingKeys) > 0) {
                    echo "\n⚠️  Missing Results:\n";
                    foreach ($missingKeys as $key) {
                        echo "   - $key: No response received\n";
                    }
                }
                
                echo "\n📊 Summary: " . count($receivedKeys) . "/" . count($expectedKeys) . " event types processed\n";

            } else if (isset($response['data'])) {
                echo "\n📋 Detailed Results (from data property):\n\n";
                echo json_encode($response['data'], JSON_PRETTY_PRINT) . "\n";
            } else {
                echo "\n⚠️  Warning: No results or data property in response\n";
            }

        } else {
            echo "\n❌ Batch operation failed\n";
            echo "📈 HTTP Status: " . ($response['status'] ?? 'N/A') . "\n";
            
            if (isset($response['error'])) {
                echo "\n🔴 Error Details:\n";
                echo json_encode($response['error'], JSON_PRETTY_PRINT) . "\n";
            }
            
            if (isset($response['data'])) {
                echo "\n📦 Response Data:\n";
                echo json_encode($response['data'], JSON_PRETTY_PRINT) . "\n";
            }
        }

    } catch (Exception $error) {
        echo "\n❌ Batch operation failed with exception:\n";
        echo "Error Message: " . $error->getMessage() . "\n";
        echo "Error Stack: " . $error->getTraceAsString() . "\n";
    }

    echo "\n" . str_repeat('=', 50) . "\n\n";
}

// Run the test
if (php_sapi_name() === 'cli') {
    echo "\n" . str_repeat('=', 50) . "\n";
    echo "🚀 Optikpi Data Pipeline SDK - Batch Operations Test\n";
    echo str_repeat('=', 50) . "\n";
    echo "\n📍 Configuration:\n";
    echo "   Base URL: " . ($API_BASE_URL ?: 'Not set') . "\n";
    echo "   Account ID: $ACCOUNT_ID\n";
    echo "   Workspace ID: $WORKSPACE_ID\n";
    echo "   Auth Token: " . ($AUTH_TOKEN ? substr($AUTH_TOKEN, 0, 8) . '...' : 'Not set') . "\n";
    
    testBatchOperations($sdk, $ACCOUNT_ID, $WORKSPACE_ID);
}
