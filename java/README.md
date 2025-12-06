# Optikpi Data Pipeline API Java SDK

This directory contains example applications demonstrating how to use the Optikpi Data Pipeline SDK for Java.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Valid Optikpi API credentials

## Setup

- **Update the env file** with your Optikpi credentials:
```bash
   # Edit the '.env' file with your actual credentials
 ```
 ```
    API_BASE_URL=https://your-api-gateway-url/apigw/ingest
    AUTH_TOKEN=your_actual_auth_token
    ACCOUNT_ID=your_actual_account_id
    WORKSPACE_ID=your_actual_workspace_id
```

## Running Examples
- Before executing the file, please change your current path to:
cd ../java/examples

### Give execute permission
```bash
chmod +x run.sh
```
 ### Now run it! Test without arguments (shows help):
 ```bash
 ./run.sh
 ```
## Run Specific Classes
### Test Customer Profile Endpoint
```bash
./run.sh TestCustomerEndpoint
```

### Test Customer Extended Attribute Endpoint
```bash
./run.sh TestCustomerExtEndpoint
```

### Test All Endpoints
```bash
./run.sh TestAllEndpoints
```

### Test Account Events
```bash
./run.sh TestAccountEndpoint
```

### Test Deposit Events
```bash
./run.sh TestDepositEndpoint
```

### Test Withdrawal Events
```bash
./run.sh TestWithdrawEndpoint
```

### Test Gaming Activity Events
```bash
./run.sh TestGamingEndpoint
```

### Test ReferFriend Events
```bash
./run.sh TestReferFriendEndpoint
```

### Test WalletBalance Events
```bash
./run.sh TestWalletBalanceEndpoint
```

### Test TestBatchOperations
```bash
./run.sh TestBatchOperations
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

//validation
ValidationResult result = customer.validate();
if (!result.isValid()) {
    System.out.println("Validation errors: " + result.getErrors());
}

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

## Troubleshooting

```bash
### Common Issues

1. Missing credentials: Ensure all required environment variables are set
2. Network issues: Check your internet connection and firewall settings
3. Invalid data: Use the validation methods to check your data before sending
4. Authentication errors: Verify your auth token, account ID, and workspace ID
```
