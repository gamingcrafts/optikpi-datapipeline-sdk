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
    user_id: 'user123456',
    username: 'john_doe',
    full_name: 'John Doe',
    first_name: 'John',
    last_name: 'Doe',
    date_of_birth: '1990-01-15',
    email: 'john.doe@example.com',
    phone_number: '+1234567890',
    gender: 'Male',
    country: 'United States',
    city: 'New York',
    language: 'en',
    currency: 'USD',
    
    // Preferences
    marketing_email_preference: true,
    notifications_preference: true,
    subscription: 'Subscribed',
    
    // Risk & Responsible gaming
    deposit_limits: { limit: 1000 },
    loss_limits: { limit: 500 },
    wagering_limits: { limit: 2000 },
    session_time_limits: { limit: 120 },
    cooling_off_period: '7',
    self_exclusion_period: '30',
    reality_checks_notification: true,
    
    // Account status
    account_status: 'Active',
    vip_status: 'Regular',
    loyalty_program_tiers: { tier: 'Bronze' },
    bonus_abuser: false,
    financial_risk_level: '0.3',
    
    // Acquisition / Partner fields
    acquisition_source: 'Google Ads',
    partner_id: 'partner123',
    affiliate_id: 'affiliate456',
    
    // Referral data
    referral_link_code: 'REF789',
    referral_limit_reached: false,
    
    // Verification & timestamps
    creation_timestamp: '2024-01-15T10:30:00Z',
    phone_verification: true,
    email_verification: true,
    bank_verification: false,
    iddoc_verification: true,
    
    // Privacy
    privacy_settings: { profile_visibility: 'private' }
  });
}

/**
 * Create sample extended attributes event - Map format
 */
function createSampleExtendedAttributesMapFormat(accountId, workspaceId) {
  return new CustomerExtEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: 'batch_ext_001',
    list_name: 'BINGO_PREFERENCES',
    ext_data: {
      Email: 'True',
      SMS: 'True',
      PushNotifications: 'False'
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
    user_id: 'batch_ext_002',
    list_name: 'GAMING_PREFERENCES',
    ext_data: JSON.stringify({
      Email: 'True',
      SMS: 'True',
      PushNotifications: 'True'
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
    user_id: 'user_001',
    event_name: 'Player Registration',
    event_id: `evt_login_${Date.now()}`,
    event_time: new Date().toISOString(),
    device: 'desktop',
    status: 'completed',
    affiliate_id: 'aff_001',
    partner_id: 'partner_001',
    campaign_code: 'CAMPAIGN_001',
    reason: 'Registration completed successfully'
  });
}

/**
 * Create sample deposit event
 */
function createSampleDepositEvent(accountId, workspaceId) {
  return new DepositEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: 'batch_004',
    event_category: 'Deposit',
    event_name: 'Successful Deposit',
    event_id: `evt_${Date.now()}`,
    event_time: new Date().toISOString(),
    amount: 100.00,
    currency: 'USD',
    payment_method: 'credit_card',
    transaction_id: `txn_${Date.now()}`,
    status: 'success',
    device: 'mobile'
  });
}

/**
 * Create sample withdraw event
 */
function createSampleWithdrawEvent(accountId, workspaceId) {
  return new WithdrawEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: 'batch_005',
    event_name: 'Successful Withdrawal',
    event_id: `evt_${Date.now()}`,
    event_time: new Date().toISOString(),
    amount: 50.00,
    currency: 'USD',
    payment_method: 'bank',
    transaction_id: `txn_${Date.now()}`,
    status: 'success',
    device: 'desktop'
  });
}

/**
 * Create sample gaming activity event
 */
function createSampleGamingActivityEvent(accountId, workspaceId) {
  return new GamingActivityEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: 'batch_006',
    event_name: 'Play Casino Game',
    event_id: `evt_${Date.now()}`,
    event_time: new Date().toISOString(),
    game_id: 'game_001',
    game_title: 'Mega Slots',
    provider: 'ProviderXYZ',
    game_type: 'slots',
    wager_amount: 5.00,
    currency: 'USD',
    device: 'mobile',
    session_id: `session_${Date.now()}`
  });
}

/**
 * Create sample refer friend event
 */
function createSampleReferFriendEvent(accountId, workspaceId) {
  return new ReferFriendEvent({
    account_id: accountId,
    workspace_id: workspaceId,
    user_id: 'batch_007',
    event_name: 'Referral Successful',
    event_id: `evt_rf_${Date.now()}`,
    event_time: new Date().toISOString(),
    referral_code_used: 'REF123456',
    successful_referral_confirmation: true,
    reward_type: 'bonus',
    reward_claimed_status: 'claimed',
    referee_user_id: 'user789012',
    referee_registration_date: '2024-01-15T10:30:00Z',
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
    user_id: 'batch_008',
    event_name: 'Balance Update',
    event_id: `evt_wb_${Date.now()}`,
    event_time: new Date().toISOString(),
    wallet_type: 'main',
    currency: 'USD',
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

function PrintData(data){
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