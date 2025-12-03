package com.optikpi.examples;

import java.lang.ref.Reference;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.BatchData;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.GamingActivityEvent;
import com.optikpi.datapipeline.model.ValidationResult;
import com.optikpi.datapipeline.model.WalletBalanceEvent;
import com.optikpi.datapipeline.model.ReferFriendEvent;
import com.optikpi.datapipeline.model.WithdrawEvent;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Example: Test All Endpoints Demonstrates how to use all available endpoints
 * in the Optikpi Data Pipeline SDK
 */
public class TestAllEndpoints {

    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        // Get configuration from environment variables
        String authToken = dotenv.get("AUTH_TOKEN");
        String accountId = dotenv.get("ACCOUNT_ID");
        String workspaceId = dotenv.get("WORKSPACE_ID");
        String baseUrl = dotenv.get("API_BASE_URL");

        if (authToken == null || accountId == null || workspaceId == null) {
            System.err.println("Error: Missing required environment variables:");
            System.err.println("Please set OPTIKPI_AUTH_TOKEN, OPTIKPI_ACCOUNT_ID, and OPTIKPI_WORKSPACE_ID");
            System.exit(1);
        }

        // Create client configuration
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        config.setBaseUrl(baseUrl);

        // Create SDK instance
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

        System.out.println("=== Optikpi Data Pipeline SDK - All Endpoints Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();

        // Test all endpoints
        testHealthCheck(sdk);
        testCustomerProfile(sdk, accountId, workspaceId);
        testAccountEvent(sdk, accountId, workspaceId);
        testDepositEvent(sdk, accountId, workspaceId);
        testWithdrawEvent(sdk, accountId, workspaceId);
        testGamingActivityEvent(sdk, accountId, workspaceId);
        testReferFriendEvent(sdk, accountId, workspaceId);      
        testWalletBalanceEvent(sdk, accountId, workspaceId);
        testBatchOperations(sdk, accountId, workspaceId);
    }

    private static void testHealthCheck(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Health Check ===");
        try {
            var response = sdk.healthCheck();
            printResponse("Health Check", response);
        } catch (Exception e) {
            System.err.println("❌ Health check failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testCustomerProfile(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Customer Profile ===");
        try {
            CustomerProfile customer = createSampleCustomer(accountId, workspaceId);
            validateEvent(customer, "customer event");
            var response = sdk.sendCustomerProfile(customer);
            printResponse("Customer Profile", response);
        } catch (Exception e) {
            System.err.println("❌ Customer profile failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testAccountEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Account Event ===");
        try {
            AccountEvent event = createSampleAccountEvent(accountId, workspaceId);
            validateEvent(event, "Account event");
            var response = sdk.sendAccountEvent(event);
            printResponse("Account Event", response);
        } catch (Exception e) {
            System.err.println("❌ Account event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testDepositEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Deposit Event ===");
        try {
            DepositEvent event = createSampleDepositEvent(accountId, workspaceId);
            validateEvent(event, "Deposit event");
            var response = sdk.sendDepositEvent(event);
            printResponse("Deposit Event", response);
        } catch (Exception e) {
            System.err.println("❌ Deposit event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testWithdrawEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Withdraw Event ===");
        try {
            WithdrawEvent event = createSampleWithdrawEvent(accountId, workspaceId);
            validateEvent(event, "Withdraw event");
            var response = sdk.sendWithdrawEvent(event);
            printResponse("Withdraw Event", response);
        } catch (Exception e) {
            System.err.println("❌ Withdraw event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testGamingActivityEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Gaming Activity Event ===");
        try {
            GamingActivityEvent event = createSampleGamingActivityEvent(accountId, workspaceId);
            validateEvent(event, "Gaming event");
            var response = sdk.sendGamingActivityEvent(event);
            printResponse("Gaming Activity Event", response);
        } catch (Exception e) {
            System.err.println("❌ Gaming activity event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testReferFriendEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Refer Friend Event ===");
        try {
            ReferFriendEvent event = createSampleReferFriendEvent(accountId, workspaceId);
            validateEvent(event, "ReferFriend Event");
            var response = sdk.sendReferFriendEvent(event);
            printResponse("Refer Friend Event", response);
        } catch (Exception e) {
            System.err.println("❌ Refer Friend event failed: " + e.getMessage());
        }
        System.out.println();
    }

     private static void testWalletBalanceEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Wallet Balance Event ===");
        try {
            WalletBalanceEvent event = createSampleWalletBalanceEvent(accountId, workspaceId);
            validateEvent(event, "Wallet Balance Event");
            var response = sdk.sendWalletBalanceEvent(event);
            printResponse("Wallet Balance Event", response);
        } catch (Exception e) {
            System.err.println("❌ Wallet Balance event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testBatchOperations(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Batch Operations ===");
        try {
            BatchData batchData = new BatchData();
            batchData.setCustomers(Arrays.asList(createSampleCustomer(accountId, workspaceId)));
            batchData.setAccountEvents(Arrays.asList(createSampleAccountEvent(accountId, workspaceId)));
            batchData.setDepositEvents(Arrays.asList(createSampleDepositEvent(accountId, workspaceId)));
            batchData.setWithdrawEvents(Arrays.asList(createSampleWithdrawEvent(accountId, workspaceId)));
            batchData.setGamingEvents(Arrays.asList(createSampleGamingActivityEvent(accountId, workspaceId)));
            batchData.setReferFriendEvents(Arrays.asList(createSampleReferFriendEvent(accountId, workspaceId)));
            batchData.setWalletBalanceEvents(Arrays.asList(createSampleWalletBalanceEvent(accountId, workspaceId)));
            validateEvent(batchData, "BatchData");
            var response = sdk.sendBatch(batchData);

            if (response.isSuccess()) {
                System.out.println("✅ Batch operation completed successfully!");
                System.out.println("Timestamp: " + response.getTimestamp());

                if (response.getCustomers() != null) {
                    System.out.println("Customer profiles: " + (response.getCustomers().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getAccountEvents() != null) {
                    System.out.println("Account events: " + (response.getAccountEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getDepositEvents() != null) {
                    System.out.println("Deposit events: " + (response.getDepositEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getWithdrawEvents() != null) {
                    System.out.println("Withdraw events: " + (response.getWithdrawEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getGamingEvents() != null) {
                    System.out.println("Gaming events: " + (response.getGamingEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getReferFriendEvents() != null) {
                    System.out.println("Refer Friend events: " + (response.getReferFriendEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getWalletBalanceEvents() != null) {
                    System.out.println("Wallet Balance events: " + (response.getWalletBalanceEvents().isSuccess() ? "Success" : "Failed"));
                }
            } else {
                System.out.println("❌ Batch operation failed");
            }
        } catch (Exception e) {
            System.err.println("❌ Batch operation failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static CustomerProfile createSampleCustomer(String accountId, String workspaceId) {
        CustomerProfile customer = new CustomerProfile();
        customer.setAccountId(accountId);
        customer.setWorkspaceId(workspaceId);
        customer.setUserId("user_011");
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
        customer.setCreationTimestamp(Instant.now().toString());

        // Add some custom data
        Map<String, Object> customData = new HashMap<>();
        customData.put("preferred_game_category", "slots");
        customData.put("marketing_source", "google_ads");
        customer.setCustomData(customData);

        return customer;
    }

    private static AccountEvent createSampleAccountEvent(String accountId, String workspaceId) {
        AccountEvent event = new AccountEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("user_012");
        event.setEventName("Login");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setDevice("desktop");
        event.setStatus("completed");
        return event;
    }

    private static DepositEvent createSampleDepositEvent(String accountId, String workspaceId) {
        DepositEvent event = new DepositEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("user_013");
        event.setEventName("Successful Deposit");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("credit_card");
        event.setTransactionId("txn_" + System.currentTimeMillis());
        event.setStatus("success");
        event.setDevice("mobile");
        return event;
    }

    private static WithdrawEvent createSampleWithdrawEvent(String accountId, String workspaceId) {
        WithdrawEvent event = new WithdrawEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("user_014");
        event.setEventName("Successful Withdrawal");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("50.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("bank");
        event.setTransactionId("txn_" + System.currentTimeMillis());
        event.setStatus("success");
        event.setDevice("desktop");
        return event;
    }

    private static GamingActivityEvent createSampleGamingActivityEvent(String accountId, String workspaceId) {
        GamingActivityEvent event = new GamingActivityEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("user_015");
        event.setEventName("Play Casino Game");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setGameId("game_001");
        event.setGameName("Mega Slots");
        event.setGameProvider("ProviderXYZ");
        event.setGameCategory("slots");
        event.setBetAmount(new BigDecimal("5.00"));
        event.setCurrency("USD");
        event.setDevice("mobile");
        event.setSessionId("session_" + System.currentTimeMillis());
        return event;
    }
    private static ReferFriendEvent createSampleReferFriendEvent(String accountId, String workspaceId) {
     ReferFriendEvent event = new ReferFriendEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user_016");
            event.setEventName("Referral Successful");
            event.setEventId("evt_rf_987654321");
            event.setEventTime(Instant.now().toString());
            event.setReferralCodeUsed("REF123456");
            event.setSuccessfulReferralConfirmation(true);
            event.setRewardType("bonus");
            event.setRewardClaimedStatus("claimed");
            event.setRefereeUserId("user789012");
            event.setRefereeRegistrationDate("2024-01-15T10:30:00Z");
            event.setRefereeFirstDeposit(100.00);
            return event;
    }

    private static WalletBalanceEvent createSampleWalletBalanceEvent(String accountId, String workspaceId) {
      WalletBalanceEvent event = new WalletBalanceEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user_017");
            event.setEventName("Balance Update");
            event.setEventId("evt_wb_987654321");
            event.setEventTime(Instant.now().toString());
            event.setWalletType("main");
            event.setCurrency("USD");
            event.setCurrentCashBalance(new BigDecimal("1250.50"));
            event.setCurrentBonusBalance(new BigDecimal("100.00"));
            event.setCurrentTotalBalance(new BigDecimal("1350.50"));
            event.setBlockedAmount(new BigDecimal("50.00"));
            return event;
    }

    private static void printResponse(String operation, com.optikpi.datapipeline.ApiResponse<Object> response) {
        if (response.isSuccess()) {
            System.out.println("✅ " + operation + " successful!");
            System.out.println("Status: " + response.getStatus());
            System.out.println("Response: " + response.getData());
        } else {
            System.out.println("❌ " + operation + " failed");
            System.out.println("Error: " + response.getError());
            System.out.println("Status: " + response.getStatus());
        }
    }

    // ✅ Universal validation method for all event types (including BatchData)
    private static void validateEvent(Object event, String eventName) {
        if (event instanceof CustomerProfile) {
            ValidationResult result = ((CustomerProfile) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof AccountEvent) {
            ValidationResult result = ((AccountEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof DepositEvent) {
            ValidationResult result = ((DepositEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof WithdrawEvent) {
            ValidationResult result = ((WithdrawEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof GamingActivityEvent) {
            ValidationResult result = ((GamingActivityEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof ReferFriendEvent) {
            ValidationResult result = ((ReferFriendEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof WalletBalanceEvent) {
            ValidationResult result = ((WalletBalanceEvent) event).validate();
            printValidationResult(result, eventName);   
        } else if (event instanceof BatchData) {
            System.out.println("=== Validating BatchData contents ===");

            BatchData batch = (BatchData) event;

            if (batch.getCustomers() != null) {
                for (Object c : batch.getCustomers()) {
                    validateEvent(c, "CustomerProfile (Batch)");
                }
            }
            if (batch.getAccountEvents() != null) {
                for (Object a : batch.getAccountEvents()) {
                    validateEvent(a, "AccountEvent (Batch)");
                }
            }
            if (batch.getDepositEvents() != null) {
                for (Object d : batch.getDepositEvents()) {
                    validateEvent(d, "DepositEvent (Batch)");
                }
            }
            if (batch.getWithdrawEvents() != null) {
                for (Object w : batch.getWithdrawEvents()) {
                    validateEvent(w, "WithdrawEvent (Batch)");
                }
            }
            if (batch.getGamingEvents() != null) {
                for (Object g : batch.getGamingEvents()) {
                    validateEvent(g, "GamingActivityEvent (Batch)");
                }
            }
            if (batch.getReferFriendEvents() != null) {
                for (Object g : batch.getReferFriendEvents()) {
                    validateEvent(g, "ReferFriendEvent (Batch)");
                }
            }
            if (batch.getWalletBalanceEvents() != null) {
                for (Object g : batch.getWalletBalanceEvents()) {
                    validateEvent(g, "WalletBalanceEvent (Batch)");
                }
            }
            

        } else {
            System.out.println("⚠️ Unknown event type: " + eventName);
        }
    }

    private static void printValidationResult(ValidationResult result, String eventName) {
        if (!result.isValid()) {
            System.out.println("❌ Invalid " + eventName + ":");
            System.out.println("Errors: " + result.getErrors());
        } else {
            System.out.println("✅ Valid " + eventName + ": " + result.isValid());
        }
    }

}
