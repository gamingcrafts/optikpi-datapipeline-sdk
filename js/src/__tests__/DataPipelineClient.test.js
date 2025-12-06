const DataPipelineClient = require('../core/DataPipelineClient');

describe('DataPipelineClient', () => {
  let client;
  let mockAxios;

  beforeEach(() => {
    // Reset mocks
    jest.clearAllMocks();
    
    // Create client with test config
    client = new DataPipelineClient({
      authToken: 'test-token',
      accountId: 'test-account',
      workspaceId: 'test-workspace',
      baseURL: 'https://test.api.com'
    });

    // Get mock axios instance
    mockAxios = client.axios;
  });

  describe('Constructor', () => {
    it('should create client with valid config', () => {
      expect(client.config.authToken).toBe('test-token');
      expect(client.config.accountId).toBe('test-account');
      expect(client.config.workspaceId).toBe('test-workspace');
      expect(client.config.baseURL).toBe('https://test.api.com');
    });

    it('should throw error for missing authToken', () => {
      expect(() => {
        new DataPipelineClient({
          accountId: 'test-account',
          workspaceId: 'test-workspace'
        });
      }).toThrow('authToken is required');
    });

    it('should throw error for missing accountId', () => {
      expect(() => {
        new DataPipelineClient({
          authToken: 'test-token',
          workspaceId: 'test-workspace'
        });
      }).toThrow('accountId is required');
    });

    it('should throw error for missing workspaceId', () => {
      expect(() => {
        new DataPipelineClient({
          authToken: 'test-token',
          accountId: 'test-account'
        });
      }).toThrow('workspaceId is required');
    });

    it('should use default values for optional config', () => {
      const client = new DataPipelineClient({
        authToken: 'test-token',
        accountId: 'test-account',
        workspaceId: 'test-workspace'
      });

      expect(client.config.timeout).toBe(30000);
      expect(client.config.retries).toBe(3);
      expect(client.config.retryDelay).toBe(1000);
    });
  });

  describe('sendCustomerProfile', () => {
    it('should send customer profile successfully', async () => {
      const customerData = {
        account_id: 'test-account',
        workspace_id: 'test-workspace',
        user_id: 'user123',
        email: 'test@example.com'
      };

      const mockResponse = {
        status: 200,
        data: { message: 'Customer profile sent successfully' }
      };

      mockAxios.post.mockResolvedValue(mockResponse);

      const result = await client.sendCustomerProfile(customerData);

      expect(result.success).toBe(true);
      expect(result.status).toBe(200);
      expect(result.data).toEqual(mockResponse.data);
      expect(mockAxios.post).toHaveBeenCalledWith('/customers', customerData);
    });

    it('should handle API errors gracefully', async () => {
      const customerData = { invalid: 'data' };
      const mockError = {
        message: 'Validation failed',
        response: {
          status: 400,
          data: { errors: ['Invalid data format'] }
        }
      };

      mockAxios.post.mockRejectedValue(mockError);

      const result = await client.sendCustomerProfile(customerData);

      expect(result.success).toBe(false);
      expect(result.status).toBe(400);
      expect(result.data).toEqual(mockError.response.data);
    });
  });

  describe('sendAccountEvent', () => {
    it('should send account event successfully', async () => {
      const eventData = {
        account_id: 'test-account',
        workspace_id: 'test-workspace',
        user_id: 'user123',
        event_name: 'Player Registration',
        event_id: 'evt_123',
        event_time: '2024-01-01T00:00:00Z'
      };

      const mockResponse = {
        status: 200,
        data: { message: 'Account event sent successfully' }
      };

      mockAxios.post.mockResolvedValue(mockResponse);

      const result = await client.sendAccountEvent(eventData);

      expect(result.success).toBe(true);
      expect(result.status).toBe(200);
      expect(result.data).toEqual(mockResponse.data);
      expect(mockAxios.post).toHaveBeenCalledWith('/events/account', eventData);
    });
  });

  describe('sendDepositEvent', () => {
    it('should send deposit event successfully', async () => {
      const eventData = {
        account_id: 'test-account',
        workspace_id: 'test-workspace',
        user_id: 'user123',
        event_name: 'Successful Deposit',
        event_id: 'evt_dep_123',
        event_time: '2024-01-01T00:00:00Z',
        amount: 100.00,
        payment_method: 'bank',
        transaction_id: 'txn_123'
      };

      const mockResponse = {
        status: 200,
        data: { message: 'Deposit event sent successfully' }
      };

      mockAxios.post.mockResolvedValue(mockResponse);

      const result = await client.sendDepositEvent(eventData);

      expect(result.success).toBe(true);
      expect(result.status).toBe(200);
      expect(result.data).toEqual(mockResponse.data);
      expect(mockAxios.post).toHaveBeenCalledWith('/events/deposit', eventData);
    });
  });

  describe('sendWithdrawEvent', () => {
    it('should send withdraw event successfully', async () => {
      const eventData = {
        account_id: 'test-account',
        workspace_id: 'test-workspace',
        user_id: 'user123',
        event_name: 'Successful Withdrawal',
        event_id: 'evt_with_123',
        event_time: '2024-01-01T00:00:00Z',
        amount: 50.00,
        payment_method: 'bank',
        transaction_id: 'txn_456'
      };

      const mockResponse = {
        status: 200,
        data: { message: 'Withdraw event sent successfully' }
      };

      mockAxios.post.mockResolvedValue(mockResponse);

      const result = await client.sendWithdrawEvent(eventData);

      expect(result.success).toBe(true);
      expect(result.status).toBe(200);
      expect(result.data).toEqual(mockResponse.data);
      expect(mockAxios.post).toHaveBeenCalledWith('/events/withdraw', eventData);
    });
  });

  describe('sendGamingActivityEvent', () => {
    it('should send gaming activity event successfully', async () => {
      const eventData = {
        account_id: 'test-account',
        workspace_id: 'test-workspace',
        user_id: 'user123',
        event_name: 'Play Casino Game',
        event_id: 'evt_gaming_123',
        event_time: '2024-01-01T00:00:00Z',
        game_id: 'game_123',
        game_title: 'Blackjack'
      };

      const mockResponse = {
        status: 200,
        data: { message: 'Gaming activity event sent successfully' }
      };

      mockAxios.post.mockResolvedValue(mockResponse);

      const result = await client.sendGamingActivityEvent(eventData);

      expect(result.success).toBe(true);
      expect(result.status).toBe(200);
      expect(result.data).toEqual(mockResponse.data);
      expect(mockAxios.post).toHaveBeenCalledWith('/events/gaming-activity', eventData);
    });
  });

  describe('sendBatch', () => {
    it('should send batch data successfully', async () => {
      const batchData = {
        customers: [{ user_id: 'user1', email: 'user1@example.com' }],
        accountEvents: [{ user_id: 'user1', event_name: 'Login' }],
        depositEvents: [{ user_id: 'user1', amount: 100, payment_method: 'bank' }]
      };

      // Mock all endpoints
      mockAxios.post
        .mockResolvedValueOnce({ status: 200, data: { message: 'Customers sent' } })
        .mockResolvedValueOnce({ status: 200, data: { message: 'Account events sent' } })
        .mockResolvedValueOnce({ status: 200, data: { message: 'Deposit events sent' } });

      const result = await client.sendBatch(batchData);

      expect(result.success).toBe(true);
      expect(result.results.customers.success).toBe(true);
      expect(result.results.accountEvents.success).toBe(true);
      expect(result.results.depositEvents.success).toBe(true);
    });
  });

  describe('updateConfig', () => {
    it('should update configuration successfully', () => {
      const newConfig = {
        timeout: 60000,
        retries: 5
      };

      client.updateConfig(newConfig);

      expect(client.config.timeout).toBe(60000);
      expect(client.config.retries).toBe(5);
      expect(client.config.authToken).toBe('test-token'); // Should remain unchanged
    });
  });

  describe('getConfig', () => {
    it('should return safe configuration without sensitive data', () => {
      const config = client.getConfig();

      expect(config.authToken).toBe('test-tok...');
      expect(config.accountId).toBe('test-account');
      expect(config.workspaceId).toBe('test-workspace');
      expect(config.baseURL).toBe('https://test.api.com');
    });
  });
});

