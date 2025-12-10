require('dotenv').config();
const OptikpiDataPipelineSDK = require('../src/index');
const {
  CustomerProfile,
  AccountEvent,
  DepositEvent,
  WithdrawEvent,
  GamingActivityEvent,
  ReferFriendEvent,
  WalletBalanceEvent,
  CustomerExtEvent
} = require('../src/models');

// Configuration - Read from environment variables
const API_BASE_URL = process.env.API_BASE_URL;
const AUTH_TOKEN = process.env.AUTH_TOKEN;
const ACCOUNT_ID = process.env.ACCOUNT_ID;
const WORKSPACE_ID = process.env.WORKSPACE_ID;

// Validate required environment variables
if (!AUTH_TOKEN || !ACCOUNT_ID || !WORKSPACE_ID) {
  console.error('❌ Error: Missing required environment variables!');
  console.error('   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID');
  console.error('   Copy env.example to .env and fill in your values');
  process.exit(1);
}

// Initialize SDK
const sdk = new OptikpiDataPipelineSDK({
  authToken: AUTH_TOKEN,
  accountId: ACCOUNT_ID,
  workspaceId: WORKSPACE_ID,
  baseURL: API_BASE_URL
});

/**
 * Create sample customer profile
 */
function createSampleCustomer(accountId, workspaceId) {
  return new CustomerProfile({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0111",
    username: "john_doe",
    full_name: "John Doe",
    first_name: "John",
    last_name: "Doe",
    date_of_birth: "1990-01-15",
    email: "john.doe@example.com",
    phone_number: "+1234567890",
    gender: "Male",
    country: "United States",
    city: "New York",
    language: "en",
    currency: "USD",
    marketing_email_preference: "Opt-in",
    notifications_preference: "Opt-in",
    subscription: "Subscribed",
    privacy_settings: "private",
    deposit_limits: 1000.00,
    loss_limits: 500.00,
    wagering_limits: 2000.00,
    session_time_limits: 120,
    cooling_off_period: 7,
    self_exclusion_period: 30,
    reality_checks_notification: "daily",
    account_status: "Active",
    vip_status: "Regular",
    loyalty_program_tiers: "Bronze",
    bonus_abuser: "Not flagged",
    financial_risk_level: 0.3,
    acquisition_source: "Google Ads",
    partner_id: "partner123",
    affliate_id: "affiliate456",
    referral_link_code: "REF789",
    referral_limit_reached: "Not Reached",
    creation_timestamp: "2024-01-15T10:30:00Z",
    phone_verification: "Verified",
    email_verification: "Verified",
    bank_verification: "NotVerified",
    iddoc_verification: "Verified",
    cooling_off_expiry_date: "2024-12-31T23:59:59Z",
    self_exclusion_expiry_date: "2025-01-31T23:59:59Z",
    risk_score_level: "low",
    marketing_sms_preference: "Opt-in",
    custom_data: {
      favorite_game: "slots",
      newsletter_signup: true
    },
    self_exclusion_by: "player",
    self_exclusion_by_type: "voluntary",
    self_exclusion_check_time: "2024-01-15T10:30:00Z",
    self_exclusion_created_time: "2024-01-01T00:00:00Z",
    closed_time: null,
    real_money_enabled: "true",  // Changed from boolean to string
    push_token: "push_token_abc123"
  });
}

/**
 * Create sample extended attributes event - Map format
 */
function createSampleExtendedAttributesMapFormat(accountId, workspaceId) {
  return new CustomerExtEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0112",
    list_name: "BINGO_PREFERENCES",
    ext_data: {
      Email: "True",
      SMS: "True",
      PushNotifications: "False"
    }
  });
}

/**
 * Create sample extended attributes event - String format
 */
function createSampleExtendedAttributesStringFormat(accountId, workspaceId) {
  return new CustomerExtEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0113",
    list_name: "GAMING_PREFERENCES",
    ext_data: JSON.stringify({
      Email: "True",
      SMS: "True",
      PushNotifications: "True"
    })
  });
}

/**
 * Create sample account event
 */
function createSampleAccountEvent(accountId, workspaceId) {
  return new AccountEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0114",
    event_category: "Account",
    event_name: "Player Registration",
    event_id: "evt_123456789",
    event_time: "2024-01-15T10:30:00Z",
    device: "desktop",
    status: "verified",
    affiliate_id: "aff_123",
    partner_id: "partner_456",
    campaign_code: "CAMPAIGN_001",
    reason: "Registration completed successfully"
  });
}

/**
 * Create sample deposit event
 */
function createSampleDepositEvent(accountId, workspaceId) {
  return new DepositEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0115",
    event_category: "Deposit",
    event_name: "Successful Deposit",
    event_id: "evt_dep_987654321",
    event_time: "2024-01-15T14:45:00Z",
    amount: 500.00,
    currency: "USD",
    payment_method: "bank",
    transaction_id: "txn_123456789",
    payment_provider_id: "provider123",
    payment_provider_name: "Chase Bank",
    status: "success",
    fees: 2.50,
    net_amount: 497.50,
    failure_reason: null,
    metadata: {
      bank_name: "Chase Bank",
      account_last4: "1234"
    }
  });
}

/**
 * Create sample withdraw event
 */
function createSampleWithdrawEvent(accountId, workspaceId) {
  return new WithdrawEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0116",
    event_category: "Withdraw",
    event_name: "Successful Withdrawal",
    event_id: "evt_wd_987654321",
    event_time: "2024-01-15T14:45:00Z",
    amount: 250.00,
    currency: "USD",
    payment_method: "bank",
    transaction_id: "txn_wd_123456789",
    status: "success",
    device: "desktop",
    affiliate_id: "aff_123456",
    partner_id: "partner_789",
    campaign_code: "SUMMER2024",
    reason: "User requested withdrawal",
    fees: 1.50,
    net_amount: 248.50,
    withdrawal_reason: "Cash out winnings",
    processing_time: "2024-01-15T10:30:00Z",
    failure_reason: null
  });
}

/**
 * Create sample gaming activity event
 */
function createSampleGamingActivityEvent(accountId, workspaceId) {
  return new GamingActivityEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0117",
    event_category: "Gaming Activity",
    event_name: "Play Casino Game",
    event_id: "evt_" + Date.now(),
    event_time: new Date().toISOString(),
    wager_amount: 10.00,
    win_amount: 25.00,
    loss_amount: 0.00,
    game_id: "game_001",
    game_title: "Mega Fortune Slots",
    provider: "ProviderXYZ",
    bonus_id: "bonus_12345",
    free_spin_id: "freespin_67890",
    jackpot_amount: 1000.00,
    num_spins_played: 50,
    game_theme: "Egyptian",
    remaining_spins: 10,
    bet_value_per_spin: 0.50,
    wagering_requirements_met: true,
    free_spin_expiry_date: "2024-12-31T23:59:59Z",
    campaign_id: "camp_summer2024",
    campaign_name: "Summer Bonanza",
    rtp: 96.5,
    game_category: "slots",
    winning_bet_amount: 25.00,
    jackpot_type: "progressive",
    volatility: "high",
    min_bet: 0.10,
    max_bet: 100.00,
    number_of_reels: 5,
    number_of_paylines: 20,
    feature_types: "wild,scatter,freespins",
    game_release_date: "2023-01-15T00:00:00Z",
    live_dealer_availability: false,
    side_bets_availability: true,
    multiplayer_option: false,
    auto_play: true,
    poker_variant: "texas_holdem",
    tournament_name: "Weekend Warriors",
    buy_in_amount: 50.00,
    table_type: "cash_game",
    stakes_level: "medium",
    number_of_players: 6,
    game_duration: 45,
    hand_volume: 120,
    player_position: "button",
    final_hand: "royal_flush",
    rake_contribution: 2.50,
    multi_tabling_indicator: false,
    session_result: "win",
    vip_status: "gold",
    blind_level: "50/100",
    rebuy_and_addon_info: "1 rebuy, 1 addon",
    sport_type: "football",
    betting_market: "match_winner",
    odds: 2.50,
    live_betting_availability: true,
    result: "won",
    bet_status: "settled",
    betting_channel: "online",
    bonus_type: "welcome_bonus",
    bonus_amount: 100.00,
    free_spin_start_date: "2024-01-01T00:00:00Z",
    num_spins_awarded: 20,
    bonus_code: "WELCOME100",
    parent_game_category: "casino",
    currency: "USD",
    money_type: "real",
    transaction_type: "bet"
  });
}

/**
 * Create sample refer friend event
 */
function createSampleReferFriendEvent(accountId, workspaceId) {
  return new ReferFriendEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0118",
    event_category: "Refer Friend",
    event_name: "Referral Successful",
    event_id: "evt_rf_987654321",
    event_time: "2024-01-15T14:45:00Z",
    referral_code_used: "REF123456",
    successful_referral_confirmation: true,
    reward_type: "bonus",
    reward_claimed_status: "claimed",
    referee_user_id: "user789012",
    referee_registration_date: "2024-01-15T10:30:00Z",
    referee_first_deposit: 100.00
  });
}

/**
 * Create sample wallet balance event
 */
function createSampleWalletBalanceEvent(accountId, workspaceId) {
  return new WalletBalanceEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: "user0119",
    event_category: "Wallet Balance",
    event_name: "Balance Update",
    event_id: "evt_wb_987654321",
    event_time: "2024-01-15T14:45:00Z",
    wallet_type: "main",
    currency: "USD",
    current_cash_balance: 1250.50,
    current_bonus_balance: 100.00,
    current_total_balance: 1350.50,
    blocked_amount: 50.00
  });
}

/**
 * Print validation result
 */
function printValidationResult(result, eventName) {
  if (!result.isValid) {
    console.log(`❌ Invalid ${eventName}:`);
    console.log(`Errors: ${JSON.stringify(result.errors)}`);
  } else {
    console.log(`✅ Valid ${eventName}`);
  }
}

function PrintData(data) {
  const className = data.constructor?.name;
  console.log(`\n🔍 Testing: ${className}`);
  console.log('─'.repeat(50));
  console.log(`📋 Data:`, JSON.stringify(data, null, 2));
}

/**
 * Validate batch data
 */
function validateBatchData(batch) {
  console.log('\n=== Validating BatchData contents ===\n');

  if (batch.customers != null) {
    for (const c of batch.customers) {
      if (c instanceof CustomerProfile) {
        const result = c.validate();
        printValidationResult(result, 'CustomerProfile (Batch)');
        PrintData(c);
      }
    }
  }

  if (batch.extendedAttributes != null) {
    for (const ea of batch.extendedAttributes) {
      if (ea instanceof CustomerExtEvent) {
        const result = ea.validate();
        printValidationResult(result, 'ExtendedAttributes (Batch)');
        PrintData(ea);
      }
    }
  }

  if (batch.accountEvents != null) {
    for (const a of batch.accountEvents) {
      if (a instanceof AccountEvent) {
        const result = a.validate();
        printValidationResult(result, 'AccountEvent (Batch)');
        PrintData(a);
      }
    }
  }

  if (batch.depositEvents != null) {
    for (const d of batch.depositEvents) {
      if (d instanceof DepositEvent) {
        const result = d.validate();
        printValidationResult(result, 'DepositEvent (Batch)');
        PrintData(d);
      }
    }
  }

  if (batch.withdrawEvents != null) {
    for (const w of batch.withdrawEvents) {
      if (w instanceof WithdrawEvent) {
        const result = w.validate();
        printValidationResult(result, 'WithdrawEvent (Batch)');
        PrintData(w);
      }
    }
  }

  if (batch.gamingEvents != null) {
    for (const g of batch.gamingEvents) {
      if (g instanceof GamingActivityEvent) {
        const result = g.validate();
        printValidationResult(result, 'GamingActivityEvent (Batch)');
        PrintData(g);
      }
    }
  }

  if (batch.referFriendEvents != null) {
    for (const r of batch.referFriendEvents) {
      if (r instanceof ReferFriendEvent) {
        const result = r.validate();
        printValidationResult(result, 'ReferFriendEvent (Batch)');
        PrintData(r);
      }
    }
  }

  if (batch.walletBalanceEvents != null) {
    for (const wb of batch.walletBalanceEvents) {
      if (wb instanceof WalletBalanceEvent) {
        const result = wb.validate();
        printValidationResult(result, 'WalletBalanceEvent (Batch)');
        PrintData(wb);
      }
    }
  }
}

/**
 * Test batch operations
 */
async function testBatchOperations(accountId, workspaceId) {
  console.log('\n=== Batch Operations ===\n');

  try {
    // Create batch data object using camelCase (matching SDK expectations)
    const batchData = {
      customers: [createSampleCustomer(accountId, workspaceId)],
      extendedAttributes: [
        createSampleExtendedAttributesMapFormat(accountId, workspaceId),
        createSampleExtendedAttributesStringFormat(accountId, workspaceId)
      ],
      accountEvents: [createSampleAccountEvent(accountId, workspaceId)],
      depositEvents: [createSampleDepositEvent(accountId, workspaceId)],
      withdrawEvents: [createSampleWithdrawEvent(accountId, workspaceId)],
      gamingEvents: [createSampleGamingActivityEvent(accountId, workspaceId)],
      referFriendEvents: [createSampleReferFriendEvent(accountId, workspaceId)],
      walletBalanceEvents: [createSampleWalletBalanceEvent(accountId, workspaceId)]
    };

    // Validate batch data
    validateBatchData(batchData);

    console.log('\n📦 Batch Data Summary:');
    console.log(`   Customers: ${batchData.customers?.length || 0}`);
    console.log(`   Extended Attributes: ${batchData.extendedAttributes?.length || 0}`);
    console.log(`   Account Events: ${batchData.accountEvents?.length || 0}`);
    console.log(`   Deposit Events: ${batchData.depositEvents?.length || 0}`);
    console.log(`   Withdraw Events: ${batchData.withdrawEvents?.length || 0}`);
    console.log(`   Gaming Events: ${batchData.gamingEvents?.length || 0}`);
    console.log(`   Refer Friend Events: ${batchData.referFriendEvents?.length || 0}`);
    console.log(`   Wallet Balance Events: ${batchData.walletBalanceEvents?.length || 0}`);

    console.log('\n📤 Sending batch request to API...\n');
    
    // Debug: Log what we're actually sending
    console.log('🔍 Request Payload Preview:');
    const payloadPreview = {
      customers: batchData.customers?.length || 0,
      extendedAttributes: batchData.extendedAttributes?.length || 0,
      accountEvents: batchData.accountEvents?.length || 0,
      depositEvents: batchData.depositEvents?.length || 0,
      withdrawEvents: batchData.withdrawEvents?.length || 0,
      gamingEvents: batchData.gamingEvents?.length || 0,
      referFriendEvents: batchData.referFriendEvents?.length || 0,
      walletBalanceEvents: batchData.walletBalanceEvents?.length || 0
    };
    console.log(JSON.stringify(payloadPreview, null, 2));
    console.log('\n');
    
    // Send batch request
    const response = await sdk.sendBatch(batchData);

    console.log('\n=== API Response ===\n');
    console.log('📊 Full Response Object:');
    console.log(JSON.stringify(response, null, 2));
    console.log('\n' + '─'.repeat(50));

    if (response.success) {
      console.log('\n✅ Batch operation completed successfully!');
      console.log(`⏰ Timestamp: ${response.timestamp || new Date().toISOString()}`);


      // Handle different response structures - Check for 'results' property
      if (response.results) {
        console.log('\n📋 Detailed Results:\n');

        // Check each event type with safe property access
        const checkResult = (key, displayName) => {
          if (response.results[key] !== undefined && response.results[key] !== null) {
            const result = response.results[key];
            console.log(`\n${displayName}:`);
            
            // Try different possible response formats
            if (typeof result === 'object') {
              if (result.success !== undefined) {
                console.log(`  ${result.success ? '✅' : '❌'} Result: ${result.success ? 'Success' : 'Failed'}`);
              }
              if (result.status !== undefined) {
                console.log(`  📊 HTTP Status: ${result.status}`);
              }
              if (result.error !== undefined) {
                console.log(`  🔴 Error: ${result.error}`);
              }
              if (result.message !== undefined) {
                console.log(`  💬 Message: ${result.message}`);
              }
              if (result.data && result.data.message !== undefined) {
                console.log(`  💬 Data Message: ${result.data.message}`);
              }
              if (result.data && result.data.validRecordsCount !== undefined) {
                console.log(`  ✅ Valid Records: ${result.data.validRecordsCount}/${result.data.totalRecordsCount}`);
              }
              if (result.data && result.data.validationErrors && result.data.validationErrors.length > 0) {
                console.log(`  ⚠️  Validation Errors:`);
                result.data.validationErrors.forEach((err, idx) => {
                  console.log(`    Record ${err.recordIndex}:`);
                  err.errors.forEach(e => {
                    console.log(`      - Field: ${e.field}`);
                    console.log(`        Message: ${e.message}`);
                    console.log(`        Value: ${JSON.stringify(e.value)}`);
                  });
                });
              }
              if (result.count !== undefined) {
                console.log(`  🔢 Count: ${result.count}`);
              }
            } else if (typeof result === 'boolean') {
              console.log(`  ${result ? '✅' : '❌'} Result: ${result ? 'Success' : 'Failed'}`);
            } else if (typeof result === 'string') {
              console.log(`  📝 Result: ${result}`);
            }
          }
        };

        checkResult('customers', '👤 Customer Profiles');
        checkResult('extendedAttributes', '🔧 Extended Attributes');
        checkResult('accountEvents', '📝 Account Events');
        checkResult('depositEvents', '💰 Deposit Events');
        checkResult('withdrawEvents', '💸 Withdraw Events');
        checkResult('gamingEvents', '🎮 Gaming Events');
        checkResult('referFriendEvents', '👥 Refer Friend Events');
        checkResult('walletBalanceEvents', '💳 Wallet Balance Events');

        // Check for missing results
        const expectedKeys = [
          'customers', 
          'extendedAttributes', 
          'accountEvents', 
          'depositEvents', 
          'withdrawEvents', 
          'gamingEvents', 
          'referFriendEvents', 
          'walletBalanceEvents'
        ];
        
        const receivedKeys = Object.keys(response.results);
        const missingKeys = expectedKeys.filter(key => !receivedKeys.includes(key));
        
        if (missingKeys.length > 0) {
          console.log('\n⚠️  Missing Results:');
          missingKeys.forEach(key => {
            console.log(`   - ${key}: No response received`);
          });
        }
        
        console.log(`\n📊 Summary: ${receivedKeys.length}/${expectedKeys.length} event types processed`);

      } else if (response.data) {
        console.log('\n📋 Detailed Results (from data property):\n');
        console.log(JSON.stringify(response.data, null, 2));
      } else {
        console.log('\n⚠️  Warning: No results or data property in response');
      }

    } else {
      console.log('\n❌ Batch operation failed');
      console.log(`📈 HTTP Status: ${response.status}`);
      
      if (response.error) {
        console.log('\n🔴 Error Details:');
        console.log(JSON.stringify(response.error, null, 2));
      }
      
      if (response.data) {
        console.log('\n📦 Response Data:');
        console.log(JSON.stringify(response.data, null, 2));
      }
    }

  } catch (error) {
    console.error('\n❌ Batch operation failed with exception:');
    console.error('Error Message:', error.message);
    console.error('Error Stack:', error.stack);
    
    if (error.response) {
      console.error('\n📡 HTTP Response Details:');
      console.error('Status:', error.response.status);
      console.error('Data:', JSON.stringify(error.response.data, null, 2));
    }
  }

  console.log('\n' + '='.repeat(50) + '\n');
}

// Run the test
if (require.main === module) {
  console.log('\n' + '='.repeat(50));
  console.log('🚀 Optikpi Data Pipeline SDK - Batch Operations Test');
  console.log('='.repeat(50));
  console.log(`\n📍 Configuration:`);
  console.log(`   Base URL: ${API_BASE_URL || 'Not set'}`);
  console.log(`   Account ID: ${ACCOUNT_ID}`);
  console.log(`   Workspace ID: ${WORKSPACE_ID}`);
  console.log(`   Auth Token: ${AUTH_TOKEN ? AUTH_TOKEN.substring(0, 8) + '...' : 'Not set'}`);
  
  testBatchOperations(ACCOUNT_ID, WORKSPACE_ID);
}

module.exports = {
  testBatchOperations,
  createSampleCustomer,
  createSampleExtendedAttributesMapFormat,
  createSampleExtendedAttributesStringFormat,
  createSampleAccountEvent,
  createSampleDepositEvent,
  createSampleWithdrawEvent,
  createSampleGamingActivityEvent,
  createSampleReferFriendEvent,
  createSampleWalletBalanceEvent,
  validateBatchData,
  printValidationResult,
  sdk
};