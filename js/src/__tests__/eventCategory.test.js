const {
  AccountEvent,
  DepositEvent,
  WithdrawEvent,
  GamingActivityEvent,
  WalletBalanceEvent,
  ReferFriendEvent
} = require('../models');

const base = {
  account_id: 'acc_example_001',
  workspace_id: 'ws_example_001',
  user_id: '1',
  event_id: 'e1',
  event_name: 'Something',
  event_time: '2026-01-15T10:30:00Z'
};

describe('event_category is required', () => {
  const models = [
    [AccountEvent, 'Account'],
    [DepositEvent, 'Deposit'],
    [WithdrawEvent, 'Withdraw'],
    [GamingActivityEvent, 'Gaming Activity'],
    [WalletBalanceEvent, 'Wallet Balance'],
    [ReferFriendEvent, 'Refer Friend']
  ];

  test.each(models)('%p rejects a missing category and does not fill one in', (Model) => {
    const model = new Model(base);
    expect(model.validate().errors).toContain('event_category is required');
    expect(model.toJSON().event_category).toBeUndefined();
  });

  test.each(models)('%p accepts its own category', (Model, category) => {
    expect(new Model({ ...base, event_category: category }).validate().errors).not.toContain('event_category is required');
  });

  test.each(models)('%p still rejects a wrong category', (Model) => {
    expect(new Model({ ...base, event_category: 'Other' }).validate().isValid).toBe(false);
  });
});
