# Changelog

All notable changes to the Optikpi Data Pipeline SDK for Java will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-03

### Added
- Initial release of the Optikpi Data Pipeline SDK for Java
- Complete Java port of the JavaScript SDK functionality
- Support for all data models:
  - CustomerProfile
  - AccountEvent
  - DepositEvent
  - WithdrawEvent
  - GamingActivityEvent
- HMAC-SHA256 authentication with HKDF key derivation
- Comprehensive data validation using Jakarta Validation
- Automatic retry logic with exponential backoff
- Batch operations support
- Health check functionality
- Extensive logging with SLF4J
- Complete Maven build configuration
- Comprehensive examples and documentation
- Type-safe API design
- Error handling with detailed response objects

### Features
- **Authentication**: Secure HMAC-based authentication
- **Validation**: Built-in data validation for all models
- **Retry Logic**: Automatic retry with configurable parameters
- **Batch Operations**: Send multiple events in single request
- **Type Safety**: Full Java type safety throughout
- **Logging**: Comprehensive logging for debugging
- **Examples**: Complete example applications
- **Documentation**: Extensive README and API documentation

### Dependencies
- Java 11+
- OkHttp 4.12.0
- Jackson 2.16.0
- Jakarta Validation 3.0.2
- SLF4J 2.0.9
- Hibernate Validator 8.0.1.Final

### Examples
- TestCustomerEndpoint: Customer profile operations
- TestAllEndpoints: All API endpoints testing
- TestAccountEndpoint: Account event operations
- TestDepositEndpoint: Deposit event handling
- TestWithdrawEndpoint: Withdrawal event processing
- TestGamingEndpoint: Gaming activity tracking

### Documentation
- Complete README with quick start guide
- API documentation with examples
- Configuration options documentation
- Error handling guide
- Troubleshooting section
