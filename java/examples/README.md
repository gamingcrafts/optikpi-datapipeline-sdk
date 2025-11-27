# Optikpi Data Pipeline SDK - Java Examples

This directory contains example applications demonstrating how to use the Optikpi Data Pipeline SDK for Java.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Valid Optikpi API credentials

## Setup

1. **Clone the repository** (if not already done):
   ```bash
   git clone https://github.com/gamingcrafts/optikpi-datapipeline-sdk.git
   cd optikpi-datapipeline-sdk/java/examples
   ```

2. **Install the SDK**:
   ```bash
   cd ../../
   mvn clean install
   cd examples
   ```

3. **Configure environment variables**:
   ```bash
   cp env.example .env
   # Edit the '.env' file with your actual credentials
   ```

4. **Update the env file** with your Optikpi credentials:
   ```
   AUTH_TOKEN=your_actual_auth_token
   ACCOUNT_ID=your_actual_account_id
   WORKSPACE_ID=your_actual_workspace_id
   ```

## Running Examples

### Test Customer Profile Endpoint
```bash
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestCustomerEndpoint"
```

### Test All Endpoints
```bash
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestAllEndpoints"
```

### Test Account Events
```bash
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestAccountEndpoint"
```

### Test Deposit Events
```bash
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestDepositEndpoint"
```

### Test Withdrawal Events
```bash
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestWithdrawEndpoint"
```

### Test Gaming Activity Events
```bash
mvn exec:java -Dexec.mainClass="com.optikpi.examples.TestGamingEndpoint"
```

## Example Code

### Basic Usage

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

### Batch Operations

```java
import com.optikpi.datapipeline.BatchData;
import com.optikpi.datapipeline.model.*;

// Create batch data
BatchData batchData = new BatchData();
batchData.setCustomers(Arrays.asList(customer1, customer2));
batchData.setAccountEvents(Arrays.asList(accountEvent1, accountEvent2));
batchData.setDepositEvents(Arrays.asList(depositEvent1, depositEvent2));

// Send batch
var response = sdk.sendBatch(batchData);
if (response.isSuccess()) {
    System.out.println("Batch operation completed successfully!");
}
```

## Available Examples

- **TestCustomerEndpoint**: Demonstrates customer profile operations
- **TestAllEndpoints**: Tests all available API endpoints
- **TestAccountEndpoint**: Focuses on account event operations
- **TestDepositEndpoint**: Demonstrates deposit event handling
- **TestWithdrawEndpoint**: Shows withdrawal event processing
- **TestGamingEndpoint**: Illustrates gaming activity tracking

## Error Handling

All SDK methods return `ApiResponse<T>` objects that include:
- `isSuccess()`: Boolean indicating if the operation succeeded
- `getStatus()`: HTTP status code
- `getData()`: Response data (if successful)
- `getError()`: Error message (if failed)
- `getTimestamp()`: Operation timestamp

## Validation

The SDK includes built-in validation for all model classes:

```java
CustomerProfile customer = new CustomerProfile();
// ... set properties

ValidationResult result = customer.validate();
if (!result.isValid()) {
    System.out.println("Validation errors: " + result.getErrors());
}
```

## Configuration Options

The `ClientConfig` class supports various configuration options:

```java
ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
config.setBaseUrl("https://5800o195ia.execute-api.eu-west-1.amazonaws.com/apigw/ingest");
config.setTimeout(60000); // 60 seconds
config.setRetries(5);
config.setRetryDelay(2000); // 2 seconds
```

## Troubleshooting

### Common Issues

1. **Missing credentials**: Ensure all required environment variables are set
2. **Network issues**: Check your internet connection and firewall settings
3. **Invalid data**: Use the validation methods to check your data before sending
4. **Authentication errors**: Verify your auth token, account ID, and workspace ID

### Debug Mode

Enable debug logging by setting the log level in your `logback.xml`:

```xml
<logger name="com.optikpi.datapipeline" level="DEBUG"/>
```

## Support

For more information and support:
- Documentation: [https://github.com/gamingcrafts/optikpi-datapipeline-sdk](https://github.com/gamingcrafts/optikpi-datapipeline-sdk)
- Issues: [https://github.com/gamingcrafts/optikpi-datapipeline-sdk/issues](https://github.com/gamingcrafts/optikpi-datapipeline-sdk/issues)
