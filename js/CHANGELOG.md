# Changelog

All notable changes to the Optikpi Data Pipeline API JavaScript SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial SDK development
- Core DataPipelineClient class
- Data models with validation
- HMAC authentication with HKDF
- Automatic retry logic
- Comprehensive error handling
- Batch operations support
- Health check functionality

## [1.0.0] - 2024-01-15

### Added
- **Initial Release** 🎉
- Complete JavaScript SDK for Optikpi Data Pipeline API
- Support for all API endpoints:
  - Customer Profile ingestion
  - Account Events
  - Deposit Events
  - Withdrawal Events
  - Gaming Activity Events
- Secure HMAC authentication using HKDF key derivation
- Comprehensive data validation
- Automatic retry mechanism for failed requests
- Batch processing capabilities
- Health check endpoint
- Full TypeScript support
- Browser and Node.js compatibility
- Comprehensive test coverage
- JSDoc documentation
- Rollup build system for multiple output formats

### Features
- **Authentication**: Automatic HMAC signature generation and validation
- **Data Models**: Structured data models with built-in validation rules
- **Error Handling**: Robust error handling with detailed error messages
- **Retry Logic**: Configurable retry mechanism for network failures
- **Batch Operations**: Support for sending multiple data types in parallel
- **Configuration**: Flexible configuration options with validation
- **Logging**: Comprehensive request/response logging
- **Performance**: Optimized for high-throughput data ingestion

### Technical Details
- **Dependencies**: Axios for HTTP requests, Node.js crypto for HMAC
- **Build System**: Rollup with Babel for ES5+ compatibility
- **Testing**: Jest with comprehensive test coverage
- **Code Quality**: ESLint and Prettier for code formatting
- **Documentation**: Comprehensive README and API documentation
- **License**: MIT License for open source usage

### Supported Platforms
- Node.js 14.0.0+
- Modern browsers (ES6+)
- CommonJS and ES modules
- UMD for browser usage

### Installation
```bash
npm install
```

### Quick Start
```javascript
const OptikpiDataPipelineSDK = require('../src/index');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your-auth-token',
  accountId: 'your-account-id',
  workspaceId: 'your-workspace-id'
});

// Send customer profile
const result = await sdk.sendCustomerProfile(customerData);
```

---

## Version History

- **1.0.0**: Initial release with full API support
- **Unreleased**: Development and testing phase

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute to this project.

## Support

For support and questions:
- Documentation: [https://docs.optikpi.com](https://docs.optikpi.com)
- Issues: [GitHub Issues](https://github.com/optikpi/datapipeline-sdk-js/issues)
- Email: support@optikpi.com

