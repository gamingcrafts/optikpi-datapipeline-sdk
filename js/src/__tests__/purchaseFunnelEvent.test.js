const fs = require('fs');
const path = require('path');
const DataPipelineClient = require('../core/DataPipelineClient');
const SDK = require('../index');
const { PurchaseFunnelEvent, FUNNEL_STAGES, FAILURE_REASONS } = require('../models');

// Shared with the API's tests: the client and the server must agree on every shape.
const shapes = JSON.parse(
  fs.readFileSync(path.join(__dirname, '..', '..', '..', 'testdata', 'purchase-funnel-shapes.json'), 'utf8')
);
const NOW = Date.parse('2026-11-04T10:00:00Z');

function buildRecord(testCase) {
  const record = { ...(testCase.from === 'full' ? shapes.full : shapes.base) };
  (testCase.unset || []).forEach(field => { delete record[field]; });
  Object.assign(record, testCase.set || {});
  return record;
}

describe('PurchaseFunnelEvent.validate over the shared shapes', () => {
  test.each(shapes.cases.map(c => [c.name, c]))('%s', (_name, testCase) => {
    const { isValid, errors } = new PurchaseFunnelEvent(buildRecord(testCase)).validate(NOW);
    expect({ isValid, errors: isValid ? errors : [] }).toEqual({ isValid: testCase.valid, errors: [] });
    if (!testCase.valid) {
      expect(errors.some(message => message.includes(testCase.errorField))).toBe(true);
    }
  });

  test('without a time argument the window is measured from now', () => {
    const recent = new Date(Date.now() - 60 * 1000).toISOString();
    expect(new PurchaseFunnelEvent({ ...shapes.base, event_time: recent }).validate().isValid).toBe(true);
    const old = new Date(Date.now() - 11 * 365 * 24 * 60 * 60 * 1000).toISOString();
    expect(new PurchaseFunnelEvent({ ...shapes.base, event_time: old }).validate().isValid).toBe(false);
  });

  test('does not fill in event_category', () => {
    const withoutCategory = { ...shapes.base };
    delete withoutCategory.event_category;
    expect(new PurchaseFunnelEvent(withoutCategory).toJSON().event_category).toBeUndefined();
  });
});

describe('Purchase Funnel constants', () => {
  test('FUNNEL_STAGES and FAILURE_REASONS are exported in contract order', () => {
    expect(FUNNEL_STAGES).toEqual([
      'start', 'browse', 'configure', 'cart', 'checkout', 'payment', 'payment_failed', 'complete',
      'failed', 'cancelled', 'exit', 'stalled', 'abandoned'
    ]);
    expect(FAILURE_REASONS).toEqual([
      'payment_declined', 'insufficient_funds', 'provider_timeout', 'limit_reached', 'kyc_required',
      'geo_blocked', 'draw_closed', 'technical_error', 'user_cancelled'
    ]);
    expect(SDK.FUNNEL_STAGES).toBe(FUNNEL_STAGES);
    expect(SDK.FAILURE_REASONS).toBe(FAILURE_REASONS);
    expect(SDK.PurchaseFunnelEvent).toBe(PurchaseFunnelEvent);
  });
});

describe('sendPurchaseFunnelEvent', () => {
  let client;

  beforeEach(() => {
    jest.clearAllMocks();
    client = new DataPipelineClient({
      authToken: 'test-token',
      accountId: 'acc_example_001',
      workspaceId: 'ws_example_001',
      baseURL: 'https://test.api.com'
    });
  });

  test('posts to /events/purchase-funnel', async () => {
    client.axios.post.mockResolvedValue({ status: 200, data: { message: 'ok' } });
    const event = new PurchaseFunnelEvent(shapes.base);

    const result = await client.sendPurchaseFunnelEvent(event);

    expect(result.success).toBe(true);
    expect(client.axios.post).toHaveBeenCalledWith('/events/purchase-funnel', event);
  });

  test('reports API errors without throwing', async () => {
    client.axios.post.mockRejectedValue({ message: 'Bad Request', response: { status: 400, data: { errors: ['x'] } } });

    const result = await client.sendPurchaseFunnelEvent([shapes.base]);

    expect(result).toMatchObject({ success: false, status: 400, data: { errors: ['x'] } });
  });

  test('sendBatch sends purchaseFunnelEvents to /events/purchase-funnel', async () => {
    client.axios.post.mockResolvedValue({ status: 200, data: { message: 'ok' } });

    const result = await client.sendBatch({ purchaseFunnelEvents: [shapes.base] });

    expect(client.axios.post).toHaveBeenCalledTimes(1);
    expect(client.axios.post).toHaveBeenCalledWith('/events/purchase-funnel', [shapes.base]);
    expect(result.results.purchaseFunnelEvents.success).toBe(true);
  });

  test('the SDK class delegates to the client', async () => {
    const sdk = new SDK({ authToken: 'test-token', accountId: 'acc_example_001', workspaceId: 'ws_example_001' });
    sdk.client.axios.post.mockResolvedValue({ status: 200, data: {} });

    await sdk.sendPurchaseFunnelEvent(shapes.base);

    expect(sdk.client.axios.post).toHaveBeenCalledWith('/events/purchase-funnel', shapes.base);
  });
});
