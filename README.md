# Optikpi Data Pipeline SDK

[![Maven Central](https://img.shields.io/maven-central/v/com.optikpi/datapipeline-sdk.svg)](https://maven.apache.org/)
[![NPM Version](https://img.shields.io/npm/v/@optikpi/datapipeline-sdk.svg)](https://www.npmjs.com/package/@optikpi/datapipeline-sdk)
[![PyPI version](https://badge.fury.io/py/optikpi-datapipeline-sdk.svg)](https://badge.fury.io/py/optikpi-datapipeline-sdk)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Official SDKs for the Optikpi Data Pipeline API. This repository contains JavaScript, Java, and Python implementations, providing comprehensive, type-safe ways to integrate with Optikpi's data pipeline services for gaming and financial data processing.

## 🚀 Features

- **Multi-Language Support**: JavaScript (Node.js/Browser), Java, and Python implementations
- **Easy Integration**: Simple, intuitive API design across all languages
- **Secure Authentication**: HMAC-based authentication with HKDF key derivation
- **Data Validation**: Built-in validation for all data models
- **Retry Logic**: Automatic retry with exponential backoff
- **Batch Operations**: Send multiple events in a single request
- **Type Safety**: Full type safety with comprehensive models
- **Comprehensive Logging**: Detailed logging for debugging and monitoring
- **Well Tested**: Extensive test coverage and examples

## 📦 Available SDKs

### JavaScript SDK
- **Platform**: Node.js and Browser
- **Directory**: [`js/`](js/)
- **Documentation**: [JavaScript SDK README](js/README.md)

### Java SDK
- **Platform**: Java 11+
- **Directory**: [`java/`](java/)
- **Documentation**: [Java SDK README](java/README.md)

### Python SDK
- **Platform**: Python 3.8+
- **Directory**: [`python/`](python/)
- **Documentation**: [Python SDK README](python/README.md)


## 📊 Supported Data Types

All SDKs support the following data models:

- **Customer Profiles**: User account information and preferences
- **Account Events**: Login, logout, verification, profile updates
- **Deposit Events**: Financial deposit transactions
- **Withdrawal Events**: Financial withdrawal transactions
- **Gaming Activity Events**: Game sessions, bets, wins, losses
- **ReferFriend Events**: User referral activities and referral rewards
- **WalletBalance Events**: Wallet balance snapshots and balance update notifications

## 🔒 Security

All SDKs implement secure authentication using:
- HMAC-SHA256 signatures
- HKDF key derivation
- Secure header transmission
- Input validation and sanitization


## 🏗️ Folder Structure

```
optikpi-datapipeline-sdk/
├── js/                    # JavaScript SDK
│   ├── src/              # Source code
│   ├── examples/         # Example applications
│   ├── dist/             # Built files
│   └── README.md         # JavaScript documentation
├── java/                 # Java SDK
│   ├── src/              # Source code
│   ├── examples/         # Example applications
│   ├── target/           # Built files
│   └── README.md         # Java documentation
├── python/               # Python SDK
│   ├── src/              # Source code
│   ├── examples/         # Example applications
│   ├── tests/            # Unit tests
│   └── README.md         # Python documentation
├── package.json          # Root package configuration
└── README.md             # This file
```

Made with ❤️ by the Optikpi team