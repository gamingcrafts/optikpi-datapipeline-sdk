// Test setup file for Jest
// This file runs before each test

// Mock crypto module for testing
jest.mock('crypto', () => ({
  hkdfSync: jest.fn(() => Buffer.from('test-derived-key-32-bytes-long')),
  createHmac: jest.fn(() => ({
    update: jest.fn().mockReturnThis(),
    digest: jest.fn(() => 'test-hmac-signature')
  }))
}));

// Mock axios for testing
jest.mock('axios', () => ({
  create: jest.fn(() => ({
    interceptors: {
      request: {
        use: jest.fn()
      },
      response: {
        use: jest.fn()
      }
    },
    get: jest.fn(),
    post: jest.fn()
  }))
}));

// Global test timeout
jest.setTimeout(10000);

// Console logging for tests
global.console = {
  ...console,
  log: jest.fn(),
  debug: jest.fn(),
  info: jest.fn(),
  warn: jest.fn(),
  error: jest.fn()
};

