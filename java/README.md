# Optikpi Data Pipeline SDK - Java

[![Maven Central](https://img.shields.io/maven-central/v/com.optikpi/datapipeline-sdk.svg)](https://maven.apache.org/)
[![Java Version](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Official Java SDK for the Optikpi Data Pipeline API. This SDK provides a comprehensive, type-safe way to integrate with Optikpi's data pipeline services for gaming and financial data processing.

## Features

- 🚀 **Easy Integration**: Simple, intuitive API design
- 🔒 **Secure Authentication**: HMAC-based authentication with HKDF key derivation
- ✅ **Data Validation**: Built-in validation for all data models
- 🔄 **Retry Logic**: Automatic retry with exponential backoff
- 📦 **Batch Operations**: Send multiple events in a single request
- 🎯 **Type Safety**: Full Java type safety with comprehensive models
- 📊 **Comprehensive Logging**: Detailed logging for debugging and monitoring
- 🧪 **Well Tested**: Extensive test coverage and examples

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.optikpi</groupId>
    <artifactId>datapipeline-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```gradle
implementation 'com.optikpi:datapipeline-sdk:1.0.0'
```

## Quick Start

### 1. Basic Setup

```java
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.model.CustomerProfile;

// Create configuration
ClientConfig config = new ClientConfig(
    "your_auth_token",
    "your_account_id", 
    "your_workspace_id"
);

// Create SDK instance
OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
```

### 2. Send Customer Profile

```java
// Create customer profile
CustomerProfile customer = new CustomerProfile();
customer.setAccountId("acc_12345");
customer.setWorkspaceId("ws_67890");
customer.setUserId("user_001");
customer.setUsername("john_doe");
customer.setEmail("john.doe@example.com");
customer.setFullName("John Doe");
customer.setAccountStatus("Active");

// Send customer profile
var response = sdk.sendCustomerProfile(customer);
if (response.isSuccess()) {
    System.out.println("Customer profile sent successfully!");
} else {
    System.err.println("Failed: " + response.getError());
}
```

### 3. Send Events

```java
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.DepositEvent;
import java.math.BigDecimal;
import java.time.Instant;

// Account event
AccountEvent accountEvent = new AccountEvent();
accountEvent.setAccountId("acc_12345");
accountEvent.setWorkspaceId("ws_67890");
accountEvent.setUserId("user_001");
accountEvent.setEventName("Login");
accountEvent.setEventId("evt_001");
accountEvent.setEventTime(Instant.now().toString());
accountEvent.setDevice("desktop");

sdk.sendAccountEvent(accountEvent);

// Deposit event
DepositEvent depositEvent = new DepositEvent();
depositEvent.setAccountId("acc_12345");
depositEvent.setWorkspaceId("ws_67890");
depositEvent.setUserId("user_001");
depositEvent.setEventName("Deposit Completed");
depositEvent.setEventId("evt_002");
depositEvent.setEventTime(Instant.now().toString());
depositEvent.setAmount(new BigDecimal("100.00"));
depositEvent.setCurrency("USD");
depositEvent.setStatus("completed");

sdk.sendDepositEvent(depositEvent);
```

## Configuration

### Basic Configuration

```java
ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
```

### Advanced Configuration

```java
ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
config.setBaseUrl("https://api.optikpi.com/ingest");
config.setTimeout(60000); // 60 seconds
config.setRetries(5);
config.setRetryDelay(2000); // 2 seconds
```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `baseUrl` | String | `https://demo.optikpi.com/apigw/ingest` | API base URL |
| `timeout` | long | `30000` | Request timeout in milliseconds |
| `retries` | int | `3` | Number of retry attempts |
| `retryDelay` | long | `1000` | Delay between retries in milliseconds |

## Data Models

### Customer Profile

```java
CustomerProfile customer = new CustomerProfile();
customer.setAccountId("acc_12345");
customer.setWorkspaceId("ws_67890");
customer.setUserId("user_001");
customer.setUsername("john_doe");
customer.setEmail("john.doe@example.com");
customer.setFullName("John Doe");
customer.setFirstName("John");
customer.setLastName("Doe");
customer.setDateOfBirth("1990-01-15");
customer.setGender("Male");
customer.setCountry("US");
customer.setCity("New York");
customer.setLanguage("en");
customer.setCurrency("USD");
customer.setAccountStatus("Active");
customer.setVipStatus("Regular");
```

### Account Events

```java
AccountEvent event = new AccountEvent();
event.setAccountId("acc_12345");
event.setWorkspaceId("ws_67890");
event.setUserId("user_001");
event.setEventName("Login"); // Login, Logout, Account Verification, etc.
event.setEventId("evt_001");
event.setEventTime(Instant.now().toString());
event.setDevice("desktop"); // desktop, mobile, tablet, app
event.setStatus("completed"); // verified, pending, failed, completed
```

### Deposit Events

```java
DepositEvent event = new DepositEvent();
event.setAccountId("acc_12345");
event.setWorkspaceId("ws_67890");
event.setUserId("user_001");
event.setEventName("Deposit Completed");
event.setEventId("evt_002");
event.setEventTime(Instant.now().toString());
event.setAmount(new BigDecimal("100.00"));
event.setCurrency("USD");
event.setPaymentMethod("credit_card");
event.setTransactionId("txn_001");
event.setStatus("completed");
```

### Withdrawal Events

```java
WithdrawEvent event = new WithdrawEvent();
event.setAccountId("acc_12345");
event.setWorkspaceId("ws_67890");
event.setUserId("user_001");
event.setEventName("Withdrawal Completed");
event.setEventId("evt_003");
event.setEventTime(Instant.now().toString());
event.setAmount(new BigDecimal("50.00"));
event.setCurrency("USD");
event.setWithdrawalMethod("bank_transfer");
event.setTransactionId("txn_002");
event.setStatus("completed");
```

### Gaming Activity Events

```java
GamingActivityEvent event = new GamingActivityEvent();
event.setAccountId("acc_12345");
event.setWorkspaceId("ws_67890");
event.setUserId("user_001");
event.setEventName("Game Started");
event.setEventId("evt_004");
event.setEventTime(Instant.now().toString());
event.setGameId("game_001");
event.setGameName("Mega Slots");
event.setGameProvider("ProviderXYZ");
event.setGameCategory("slots");
event.setBetAmount(new BigDecimal("5.00"));
event.setCurrency("USD");
event.setDevice("mobile");
event.setSessionId("session_001");
```

## Batch Operations

Send multiple events in a single request:

```java
import com.optikpi.datapipeline.BatchData;
import java.util.Arrays;

BatchData batchData = new BatchData();
batchData.setCustomers(Arrays.asList(customer1, customer2));
batchData.setAccountEvents(Arrays.asList(accountEvent1, accountEvent2));
batchData.setDepositEvents(Arrays.asList(depositEvent1, depositEvent2));
batchData.setWithdrawEvents(Arrays.asList(withdrawEvent1, withdrawEvent2));
batchData.setGamingEvents(Arrays.asList(gamingEvent1, gamingEvent2));

var response = sdk.sendBatch(batchData);
if (response.isSuccess()) {
    System.out.println("Batch operation completed successfully!");
}
```

## Data Validation

All models include built-in validation:

```java
CustomerProfile customer = new CustomerProfile();
// ... set properties

ValidationResult result = customer.validate();
if (!result.isValid()) {
    System.out.println("Validation errors:");
    for (String error : result.getErrors()) {
        System.out.println("- " + error);
    }
}
```

## Error Handling

All SDK methods return `ApiResponse<T>` objects:

```java
var response = sdk.sendCustomerProfile(customer);
if (response.isSuccess()) {
    System.out.println("Success! Status: " + response.getStatus());
    System.out.println("Data: " + response.getData());
} else {
    System.err.println("Failed! Status: " + response.getStatus());
    System.err.println("Error: " + response.getError());
}
```

## Health Check

Check API connectivity:

```java
var response = sdk.healthCheck();
if (response.isSuccess()) {
    System.out.println("API is healthy!");
} else {
    System.err.println("API health check failed: " + response.getError());
}
```

## Logging

The SDK uses SLF4J for logging. Configure your logging framework:

### Logback Configuration

```xml
<configuration>
    <logger name="com.optikpi.datapipeline" level="DEBUG"/>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## Examples

See the [examples directory](examples/) for comprehensive usage examples:

- [TestCustomerEndpoint](examples/src/main/java/com/optikpi/examples/TestCustomerEndpoint.java)
- [TestAllEndpoints](examples/src/main/java/com/optikpi/examples/TestAllEndpoints.java)

Run examples:

```bash
cd examples
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestCustomerEndpoint"
```

## Requirements

- Java 11 or higher
- Maven 3.6+ or Gradle 6.0+
- Valid Optikpi API credentials

## Dependencies

- OkHttp 4.12.0 (HTTP client)
- Jackson 2.16.0 (JSON processing)
- Jakarta Validation 3.0.2 (Data validation)
- SLF4J 2.0.9 (Logging)

## Security

The SDK implements secure authentication using:
- HMAC-SHA256 signatures
- HKDF key derivation
- Secure header transmission

## Support

- 📚 [Documentation](https://github.com/gamingcrafts/optikpi-datapipeline-sdk)
- 🐛 [Report Issues](https://github.com/gamingcrafts/optikpi-datapipeline-sdk/issues)
- 💬 [Discussions](https://github.com/gamingcrafts/optikpi-datapipeline-sdk/discussions)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and updates.
