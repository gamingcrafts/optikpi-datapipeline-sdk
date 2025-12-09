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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123456",
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
    real_money_enabled: true,
    push_token: "push_token_abc123"

  });
}

/**
 * Create sample extended attributes event - Map format
 */
function createSampleExtendedAttributesMapFormat(accountId, workspaceId) {
  return new CustomerExtEvent({
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "opti789",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "opti456",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123456",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123456",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123456",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123411",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123456",
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
    account_id: ACCOUNT_ID,
    workspace_id: WORKSPACE_ID,
    user_id: "user123456",
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
    console.log(`✅ Valid ${eventName}: ${result.isValid}`);
  }
}

function PrintData(data) {
  const className = data.constructor?.name;
  console.log(`🔍 Testing : ${className}`);
  console.log('─'.repeat(50));
  console.log(`📋 Data:`, JSON.stringify(data, null, 2));
}

/**
 * Validate batch data
 */
function validateBatchData(batch) {
  console.log('=== Validating BatchData contents ===');

  if (batch.customers != null) {
    for (const c of batch.customers) {
      if (c instanceof CustomerProfile) {
        const result = c.validate();
        printValidationResult(result, 'CustomerProfile (Batch)');
        PrintData(c);
      }
    }
  }

  if (batch.extended_attributes != null) {
    for (const ea of batch.extended_attributes) {
      if (ea instanceof CustomerExtEvent) {
        const result = ea.validate();
        printValidationResult(result, 'ExtendedAttributes (Batch)');
        PrintData(ea);
      }
    }
  }

  if (batch.account_events != null) {
    for (const a of batch.account_events) {
      if (a instanceof AccountEvent) {
        const result = a.validate();
        printValidationResult(result, 'AccountEvent (Batch)');
        PrintData(a);
      }
    }
  }

  if (batch.deposit_events != null) {
    for (const d of batch.deposit_events) {
      if (d instanceof DepositEvent) {
        const result = d.validate();
        printValidationResult(result, 'DepositEvent (Batch)');
        PrintData(d);
      }
    }
  }

  if (batch.withdraw_events != null) {
    for (const w of batch.withdraw_events) {
      if (w instanceof WithdrawEvent) {
        const result = w.validate();
        printValidationResult(result, 'WithdrawEvent (Batch)');
        PrintData(w);
      }
    }
  }

  if (batch.gaming_events != null) {
    for (const g of batch.gaming_events) {
      if (g instanceof GamingActivityEvent) {
        const result = g.validate();
        printValidationResult(result, 'GamingActivityEvent (Batch)');
        PrintData(g);
      }
    }
  }

  if (batch.refer_friend_events != null) {
    for (const r of batch.refer_friend_events) {
      if (r instanceof ReferFriendEvent) {
        const result = r.validate();
        printValidationResult(result, 'ReferFriendEvent (Batch)');
        PrintData(r);
      }
    }
  }

  if (batch.wallet_balance_events != null) {
    for (const wb of batch.wallet_balance_events) {
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
async function testBatchOperations(sdk, accountId, workspaceId) {
  console.log('=== Batch Operations ===');

  try {
    // Create batch data object (plain JavaScript object, no BatchData class)
    const batchData = {
      customers: [createSampleCustomer(accountId, workspaceId)],
      extended_attributes: [
        createSampleExtendedAttributesMapFormat(accountId, workspaceId),
        createSampleExtendedAttributesStringFormat(accountId, workspaceId)
      ],
      account_events: [createSampleAccountEvent(accountId, workspaceId)],
      deposit_events: [createSampleDepositEvent(accountId, workspaceId)],
      withdraw_events: [createSampleWithdrawEvent(accountId, workspaceId)],
      gaming_events: [createSampleGamingActivityEvent(accountId, workspaceId)],
      refer_friend_events: [createSampleReferFriendEvent(accountId, workspaceId)],
      wallet_balance_events: [createSampleWalletBalanceEvent(accountId, workspaceId)]
    };

    // Validate batch data
    validateBatchData(batchData);

    // Send batch request
    const response = await sdk.sendBatch(batchData);

    if (response.success) {
      console.log('✅ Batch operation completed successfully!');
      console.log(`Timestamp: ${response.timestamp || new Date().toISOString()}`);

      if (response.data) {
        if (response.data.customers != null) {
          console.log(`Customer profiles: ${response.data.customers.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.extended_attributes != null) {
          console.log(`Extended Attributes: ${response.data.extended_attributes.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.account_events != null) {
          console.log(`Account events: ${response.data.account_events.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.deposit_events != null) {
          console.log(`Deposit events: ${response.data.deposit_events.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.withdraw_events != null) {
          console.log(`Withdraw events: ${response.data.withdraw_events.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.gaming_events != null) {
          console.log(`Gaming events: ${response.data.gaming_events.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.refer_friend_events != null) {
          console.log(`Refer Friend events: ${response.data.refer_friend_events.success ? 'Success' : 'Failed'}`);
        }
        if (response.data.wallet_balance_events != null) {
          console.log(`Wallet Balance events: ${response.data.wallet_balance_events.success ? 'Success' : 'Failed'}`);
        }
      }
    } else {
      console.log('❌ Batch operation failed');
    }

  } catch (error) {
    console.error('❌ Batch operation failed:', error.message);
    console.error(error.stack);
  }

  console.log();
}

// Run the test
if (require.main === module) {
  console.log('=== Optikpi Data Pipeline SDK - Batch Operations Test ===');
  console.log(`Base URL: ${API_BASE_URL}`);
  console.log(`Account ID: ${ACCOUNT_ID}`);
  console.log(`Workspace ID: ${WORKSPACE_ID}`);
  console.log();

  testBatchOperations(sdk, ACCOUNT_ID, WORKSPACE_ID);
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