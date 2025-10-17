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
- **Package**: `@optikpi/datapipeline-sdk`
- **Directory**: [`js/`](js/)
- **Documentation**: [JavaScript SDK README](js/README.md)

### Java SDK
- **Platform**: Java 11+
- **Package**: `com.optikpi:datapipeline-sdk`
- **Directory**: [`java/`](java/)
- **Documentation**: [Java SDK README](java/README.md)

### Python SDK
- **Platform**: Python 3.8+
- **Package**: `optikpi-datapipeline-sdk`
- **Directory**: [`python/`](python/)
- **Documentation**: [Python SDK README](python/README.md)

## 🚀 Quick Start

### JavaScript

```bash
npm install @optikpi/datapipeline-sdk
```

```javascript
const { OptikpiDataPipelineSDK } = require('@optikpi/datapipeline-sdk');

const sdk = new OptikpiDataPipelineSDK({
  authToken: 'your_auth_token',
  accountId: 'your_account_id',
  workspaceId: 'your_workspace_id'
});

// Send customer profile
const response = await sdk.sendCustomerProfile({
  account_id: 'acc_12345',
  workspace_id: 'ws_67890',
  user_id: 'user_001',
  username: 'john_doe',
  email: 'john.doe@example.com',
  full_name: 'John Doe',
  account_status: 'Active'
});
```

### Java

```xml
<dependency>
    <groupId>com.optikpi</groupId>
    <artifactId>datapipeline-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.model.CustomerProfile;

ClientConfig config = new ClientConfig(
    "your_auth_token",
    "your_account_id", 
    "your_workspace_id"
);

OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

CustomerProfile customer = new CustomerProfile();
customer.setAccountId("acc_12345");
customer.setWorkspaceId("ws_67890");
customer.setUserId("user_001");
customer.setUsername("john_doe");
customer.setEmail("john.doe@example.com");
customer.setFullName("John Doe");
customer.setAccountStatus("Active");

var response = sdk.sendCustomerProfile(customer);
```

### Python

```bash
# Local SDK installation
cd python
poetry install
```

```python
from index import OptikpiDataPipelineSDK

sdk = OptikpiDataPipelineSDK({
    'authToken': 'your_auth_token',
    'accountId': 'your_account_id',
    'workspaceId': 'your_workspace_id'
})

# Send customer profile
response = sdk.send_customer_profile({
    'account_id': 'acc_12345',
    'workspace_id': 'ws_67890',
    'user_id': 'user_001',
    'username': 'john_doe',
    'email': 'john.doe@example.com',
    'full_name': 'John Doe',
    'account_status': 'Active'
})
```

## 📊 Supported Data Types

All SDKs support the following data models:

- **Customer Profiles**: User account information and preferences
- **Account Events**: Login, logout, verification, profile updates
- **Deposit Events**: Financial deposit transactions
- **Withdrawal Events**: Financial withdrawal transactions
- **Gaming Activity Events**: Game sessions, bets, wins, losses

## 🔧 Configuration

### Environment Variables

```bash
# Required
OPTIKPI_AUTH_TOKEN=your_auth_token
OPTIKPI_ACCOUNT_ID=your_account_id
OPTIKPI_WORKSPACE_ID=your_workspace_id

# Optional
OPTIKPI_BASE_URL=https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest
OPTIKPI_TIMEOUT=30000
OPTIKPI_RETRIES=3
```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `baseUrl` | String | `https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest` | API base URL |
| `timeout` | Number | `30000` | Request timeout in milliseconds |
| `retries` | Number | `3` | Number of retry attempts |
| `retryDelay` | Number | `1000` | Delay between retries in milliseconds |

## 📚 Examples

### JavaScript Examples

```bash
cd js/examples
npm install
npm test
```

### Java Examples

```bash
cd java/examples
mvn clean compile
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestCustomerEndpoint"
```

### Python Examples

```bash
cd python
poetry install
poetry run python examples/test-customer-endpoint.py
```

## 🛠️ Development

### Prerequisites

- Node.js 14+ (for JavaScript SDK)
- Java 11+ (for Java SDK)
- Python 3.8+ (for Python SDK)
- Maven 3.6+ (for Java SDK)
- Poetry (for Python SDK)

### Building

```bash
# Build both SDKs
npm run build

# Build JavaScript SDK only
npm run build:js

# Build Java SDK only
npm run build:java

# Build Python SDK only
cd python && poetry build
```

### Testing

```bash
# Test both SDKs
npm test

# Test JavaScript SDK only
npm run test:js

# Test Java SDK only
npm run test:java

# Test Python SDK only
cd python && poetry run pytest
```

### Running Examples

```bash
# JavaScript examples
npm run examples:js

# Java examples
npm run examples:java

# Python examples
cd python && poetry run python examples/test-all-endpoints.py
```

## 🔒 Security

All SDKs implement secure authentication using:
- HMAC-SHA256 signatures
- HKDF key derivation
- Secure header transmission
- Input validation and sanitization

## 📖 Documentation

- [JavaScript SDK Documentation](js/README.md)
- [Java SDK Documentation](java/README.md)
- [Python SDK Documentation](python/README.md)
- [API Reference](https://github.com/gamingcrafts/optikpi-datapipeline-sdk)
- [Examples](examples/)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- 📚 [Documentation](https://github.com/gamingcrafts/optikpi-datapipeline-sdk)
- 🐛 [Report Issues](https://github.com/gamingcrafts/optikpi-datapipeline-sdk/issues)
- 💬 [Discussions](https://github.com/gamingcrafts/optikpi-datapipeline-sdk/discussions)
- 📧 [Email Support](mailto:support@optikpi.com)

## 📈 Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and updates.

## 🏗️ Architecture

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

## 🌟 Features Comparison

| Feature | JavaScript | Java | Python |
|---------|------------|------|--------|
| Type Safety | TypeScript support | Full type safety | Type hints |
| Validation | Built-in | Jakarta Validation | Built-in |
| HTTP Client | Axios | OkHttp | Requests |
| JSON Processing | Native | Jackson | Native |
| Logging | Console/Logger | SLF4J | Logging module |
| Testing | Jest | JUnit 5 | pytest |
| Package Manager | npm | Maven | Poetry |
| Browser Support | ✅ | ❌ | ❌ |
| Node.js Support | ✅ | ❌ | ❌ |
| JVM Support | ❌ | ✅ | ❌ |
| Linux/Unix Support | ✅ | ✅ | ✅ |
| Windows Support | ✅ | ✅ | ✅ |
| macOS Support | ✅ | ✅ | ✅ |

---

Made with ❤️ by the Optikpi team