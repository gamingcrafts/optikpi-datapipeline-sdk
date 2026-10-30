const { validateStorage, checkType } = require('../models/bigQueryValidation');
const {
  AccountEvent,
  DepositEvent,
  WithdrawEvent,
  GamingActivityEvent,
  WalletBalanceEvent,
  ReferFriendEvent,
  CustomerProfile,
  CustomerExtEvent
} = require('../models');

describe('checkType', () => {
  // Measured against the warehouse (dev probe 2026-10-29): [type, value, stored].
  // Two deliberate departures, marked: values the warehouse stores as a wrong value are rejected.
  const cases = [
    ['STRING', 'x', true],
    ['STRING', '', true],
    ['STRING', 15466, false],
    ['STRING', 10.5, false],
    ['STRING', true, false],
    ['STRING', { a: 1 }, false],
    ['STRING', [1], false],

    ['NUMERIC', 10, true],
    ['NUMERIC', 10.5, true],
    ['NUMERIC', '10.5', true],
    ['NUMERIC', '-10', true],
    ['NUMERIC', '1e5', true],
    ['NUMERIC', 1e5, true],
    ['NUMERIC', ' 10', true],
    ['NUMERIC', '10 ', true],
    ['NUMERIC', '+10', true],
    ['NUMERIC', '.5', true],
    ['NUMERIC', '10.', true],
    ['NUMERIC', '', false],
    ['NUMERIC', true, false],
    ['NUMERIC', '1,000', false],
    ['NUMERIC', 'abc', false], // deliberate: the warehouse stores garbage (-0.000018583)
    ['NUMERIC', Infinity, false],

    ['INTEGER', 3, true],
    ['INTEGER', '3', true],
    ['INTEGER', 3.0, true],
    ['INTEGER', '+3', true],
    ['INTEGER', 3.5, false],
    ['INTEGER', '3.5', false],
    ['INTEGER', '', false],
    ['INTEGER', true, false],
    ['INTEGER', ' 3', false],
    ['INTEGER', '3e2', false],

    ['BOOLEAN', true, true],
    ['BOOLEAN', false, true],
    ['BOOLEAN', 'true', true],
    ['BOOLEAN', 'TRUE', true],
    ['BOOLEAN', 'True', true],
    ['BOOLEAN', 'false', true],
    ['BOOLEAN', 1, true],
    ['BOOLEAN', 0, true],
    ['BOOLEAN', '1', true],
    ['BOOLEAN', 'yes', false], // deliberate: the warehouse stores it as true

    ['TIMESTAMP', '2026-10-29T12:32:13.562Z', true],
    ['TIMESTAMP', '2026-10-29 12:32:13', true],
    ['TIMESTAMP', '2026-10-29 12:32:13 UTC', true],
    ['TIMESTAMP', '2026-10-29T12:32:13 UTC', true],
    ['TIMESTAMP', '2026-10-29T12:32:13+05:30', true],
    ['TIMESTAMP', '2026-10-29 12:30', true],
    ['TIMESTAMP', '2026-10-29T12:32:13.123456789Z', true],
    ['TIMESTAMP', 1761741133562000, true],
    ['TIMESTAMP', 1761741133, true],
    ['TIMESTAMP', '1761741133562000', true],
    ['TIMESTAMP', '1', true],
    ['TIMESTAMP', '2026-10-29 12:32:13 Asia/Kolkata', false],
    ['TIMESTAMP', '2026-10-29 12:32:13 America/Argentina/Buenos_Aires', false],
    ['TIMESTAMP', '2026-10-29T12:32:13+0530', false],
    ['TIMESTAMP', '2026-10-29T12:32:13+05', false],
    ['TIMESTAMP', '2026-10-29', false],
    ['TIMESTAMP', '2026-1-5 1:2:3', false],
    ['TIMESTAMP', '20261029T123213Z', false],
    ['TIMESTAMP', 'Oct 29 2026', false],
    ['TIMESTAMP', '', false],
    ['TIMESTAMP', '   ', false],
    ['TIMESTAMP', '2026-02-30 00:00:00', false],
    ['TIMESTAMP', '2026-10-29 24:00:00', false],
    ['TIMESTAMP', '2026-10-29 12:32:13 UTC+5', false],
    ['TIMESTAMP', '2026-10-29T12:32:13z', false],
    ['TIMESTAMP', '2026-10-29t12:32:13Z', false],
    ['TIMESTAMP', true, false],

    ['DATE', '1990-05-17', true],
    ['DATE', '1990-5-7', true],
    ['DATE', 7000, true],
    ['DATE', '7000', true],
    ['DATE', '1990-02-30', false],
    ['DATE', '17/05/1990', false],
    ['DATE', '', false],
    ['DATE', '1990-05-17T00:00:00Z', false],
    ['DATE', '1990-05-17 00:00:00', false],

    // Objects and arrays are serialised to a JSON string by the API before storage.
    ['JSON', { a: 1 }, true],
    ['JSON', [1, 2], true],
    ['JSON', '{"a":1}', true],
    ['JSON', '[1,2]', true],
    ['JSON', '"str"', true],
    ['JSON', 'null', true],
    ['JSON', 'bad{', false],
    ['JSON', '', false],
    ['JSON', 5, false],
    ['JSON', true, false],

    // Not measured; chosen so every SDK agrees (see spec).
    ['TIMESTAMP', '0001-01-01T00:00:00Z', true],
    ['TIMESTAMP', '0099-12-31 23:59:59', true],
    ['TIMESTAMP', '0000-01-01T00:00:00Z', false],
    ['TIMESTAMP', '2026-10-29T12:32:13+05:99', false],
    ['TIMESTAMP', '2026-10-29T12:32:13+19:00', false],
    ['TIMESTAMP', '2026-10-29T12:32:13-18:00', true],
    ['DATE', '0001-01-01', true],
    ['DATE', '0000-01-01', false],
    ['NUMERIC', '\u00a010', false],
    ['NUMERIC', '\t10\n', true]
  ];

  test.each(cases)('%s accepts %p: %p', (type, value, accepted) => {
    expect(checkType('f', value, type) === null).toBe(accepted);
  });
});

describe('validateStorage', () => {
  const now = Date.parse('2026-10-29T00:00:00Z');
  const rules = { required: ['a', 'b'], types: { a: 'STRING', n: 'NUMERIC', t: 'TIMESTAMP' }, partition: 't' };

  test.each([
    ['missing', {}],
    ['null', { a: null, b: null }],
    ['empty string', { a: '', b: '' }]
  ])('required fields that are %s are rejected', (_, record) => {
    const errors = validateStorage(record, rules, now);
    expect(errors).toEqual(expect.arrayContaining(['a is required', 'b is required']));
  });

  test('null and absent optional fields are accepted', () => {
    expect(validateStorage({ a: 'x', b: 'y', n: null }, rules, now)).toEqual([]);
  });

  test.each([
    ['2016-10-28T00:00:00Z', false],
    ['2016-11-01T00:00:00Z', true],
    ['2027-10-01T00:00:00Z', true],
    ['2027-11-30T00:00:00Z', false]
  ])('partition value %s accepted: %p', (t, accepted) => {
    const errors = validateStorage({ a: 'x', b: 'y', t }, rules, now);
    expect(errors.length === 0).toBe(accepted);
  });
});

describe('models enforce the stored-record rules', () => {
  const event = {
    account_id: 'acc',
    workspace_id: 'ws',
    user_id: '1',
    event_id: 'e1',
    event_name: 'Something',
    event_time: new Date().toISOString()
  };

  const models = [
    [AccountEvent, 'Account', {}],
    [DepositEvent, 'Deposit', { amount: 10, payment_method: 'card', transaction_id: 't1' }],
    [WithdrawEvent, 'Withdraw', { amount: 10, payment_method: 'card', transaction_id: 't1' }],
    [GamingActivityEvent, 'Gaming Activity', { game_id: '15466', game_title: 'Aviator' }],
    [WalletBalanceEvent, 'Wallet Balance', { currency: 'INR', wallet_type: 'main' }],
    [ReferFriendEvent, 'Refer Friend', {}]
  ];

  test.each(models)('%p rejects a missing event_category and does not fill one in', (Model, category, extra) => {
    const model = new Model({ ...event, ...extra });
    const result = model.validate();
    expect(result.isValid).toBe(false);
    expect(result.errors).toContain('event_category is required');
    expect(model.toJSON().event_category).toBeUndefined();
  });

  test.each(models)('%p accepts a complete record', (Model, category, extra) => {
    const result = new Model({ ...event, event_category: category, ...extra }).validate();
    expect(result).toEqual({ isValid: true, errors: [] });
  });

  test('a field the model does not declare is still type-checked', () => {
    // payment_provider_id is a stored withdraw column that WithdrawEvent does not declare.
    const result = new WithdrawEvent({
      ...event,
      event_category: 'Withdraw',
      amount: 10,
      payment_method: 'card',
      transaction_id: 't1',
      payment_provider_id: 42
    }).validate();
    expect(result.errors).toContain('payment_provider_id must be a string');
  });

  test('a declared field deleted after construction counts as missing', () => {
    const model = new WalletBalanceEvent({ ...event, event_category: 'Wallet Balance', currency: 'INR', wallet_type: 'main' });
    delete model.event_category;
    expect(model.validate().errors).toContain('event_category is required');
    expect(JSON.parse(JSON.stringify(model)).event_category).toBeUndefined();
  });

  test('a declared field cleared after construction counts as missing', () => {
    const model = new WalletBalanceEvent({ ...event, event_category: 'Wallet Balance', currency: 'INR', wallet_type: 'main' });
    model.event_category = undefined;
    expect(model.validate().errors).toContain('event_category is required');
    expect(model.toJSON().event_category).toBeUndefined();
  });

  test('a numeric game_id is rejected', () => {
    const result = new GamingActivityEvent({
      ...event,
      event_category: 'Gaming Activity',
      game_id: 15466,
      game_title: 'Aviator'
    }).validate();
    expect(result.errors).toContain('game_id must be a string');
  });

  test('a wallet payload is rejected for its missing event_category', () => {
    const result = new WalletBalanceEvent({
      workspace_id: 'ws_example_001',
      blocked_amount: 100.0,
      account_id: 'acc_example_001',
      current_cash_balance: 3065.5,
      event_id: 'da49a497-a41f-4e37-b7b2-e10ad0c0ed4d',
      currency: 'INR',
      wallet_type: 'main',
      user_id: '1',
      current_bonus_balance: 20.0,
      event_time: new Date().toISOString(),
      event_name: 'Balance Update',
      current_total_balance: 3085.5
    }).validate();
    expect(result.errors).toEqual(['event_category is required']);
  });

  test('a customer with an empty-string timestamp is rejected', () => {
    const result = new CustomerProfile({
      account_id: 'acc',
      workspace_id: 'ws',
      user_id: '1',
      username: 'u',
      email: 'u@example.com',
      creation_timestamp: new Date().toISOString(),
      self_exclusion_created_time: ''
    }).validate();
    expect(result.errors).toContain('self_exclusion_created_time must be a valid ISO 8601 date-time');
  });

  test('customer ext data may be an object or a JSON string', () => {
    const base = { account_id: 'acc', workspace_id: 'ws', user_id: '1', list_name: 'l' };
    expect(new CustomerExtEvent({ ...base, ext_data: { a: 1 } }).validate().isValid).toBe(true);
    expect(new CustomerExtEvent({ ...base, ext_data: '{"a":1}' }).validate().isValid).toBe(true);
  });
});
