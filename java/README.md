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
cd ../java/
 ```bash
mvn clean install
```
- change path to:
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
customer.setAccountId(accountId);
customer.setWorkspaceId(workspaceId);
customer.setUserId("java_field01");
customer.setUsername("john_doe");
customer.setFullName("John Doe");
customer.setFirstName("John");
customer.setLastName("Doe");
customer.setDateOfBirth("1990-01-15");
customer.setEmail("john.doe@example.com");
customer.setPhoneNumber("+1234567890");
customer.setGender("Male");
customer.setCountry("United States");
customer.setCity("New York");
customer.setLanguage("en");
customer.setCurrency("USD");
customer.setPhoneVerification("Verified");
customer.setEmailVerification("Verified");
customer.setBankVerification("NotVerified");
customer.setIddocVerification("Verified");
customer.setMarketingEmailPreference("Opt-in");
customer.setNotificationsPreference("Opt-in");
customer.setSubscription("Subscribed");
customer.setPrivacySettings("private");
customer.setDepositLimits(1000.00);
customer.setLossLimits(500.00);
customer.setWageringLimits(2000.00);
customer.setSessionTimeLimits(120);
customer.setCoolingOffExpiryDate("2024-12-31T23:59:59Z");
customer.setSelfExclusionExpiryDate("2025-01-31T23:59:59Z");
customer.setRealityChecksNotification("daily");
customer.setVipStatus("Regular");
customer.setLoyaltyProgramTiers("Bronze");
customer.setAccountStatus("Active");
customer.setBonusAbuser("Not flagged");
customer.setFinancialRiskLevel(0.3);
customer.setAcquisitionSource("Google Ads");
customer.setPartnerId("partner123");
customer.setReferralLinkCode("REF789");
customer.setReferralLimitReached("Not Reached");
customer.setCreationTimestamp("2024-01-15T10:30:00Z");
customer.setRiskScoreLevel("low");
customer.setMarketingSmsPreference("Opt-in");
// Custom data as Map (same in both)
Map<String, Object> customData = new HashMap<>();
customData.put("favorite_game", "slots");
customData.put("newsletter_signup", true);
customer.setCustomData(customData);
customer.setSelfExclusionBy("player");
customer.setSelfExclusionByType("voluntary");
customer.setSelfExclusionCheckTime("2024-01-15T10:30:00Z");
customer.setSelfExclusionCreatedTime("2024-01-01T00:00:00Z");
customer.setClosedTime(null);
customer.setRealMoneyEnabled("true");
customer.setPushToken("push_token_abc123");
customer.setAndroidPushToken("android_push_token_xyz456");
customer.setIosPushToken("ios_push_token_def789");
customer.setWindowsPushToken("windows_push_token_ghi012");  
customer.setMacDmgPushToken("mac_push_token_jkl345");

//validation
ValidationResult result = customer.validate();
if (!result.isValid()) {
    System.out.println("Validation errors: " + result.getErrors());
}
System.out.println("✅ Customer event validated successfully!");
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
