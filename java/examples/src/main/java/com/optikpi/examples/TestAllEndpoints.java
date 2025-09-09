package com.optikpi.examples;

import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.model.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Example: Test All Endpoints
 * Demonstrates how to use all available endpoints in the Optikpi Data Pipeline SDK
 */
public class TestAllEndpoints {
    
    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename("env")
                .load();
        
        // Get configuration from environment variables
        String authToken = dotenv.get("OPTIKPI_AUTH_TOKEN");
        String accountId = dotenv.get("OPTIKPI_ACCOUNT_ID");
        String workspaceId = dotenv.get("OPTIKPI_WORKSPACE_ID");
        String baseUrl = dotenv.get("OPTIKPI_BASE_URL", "https://demo.optikpi.com/apigw/ingest");
        
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
        testCustomerProfile(sdk);
        testAccountEvent(sdk);
        testDepositEvent(sdk);
        testWithdrawEvent(sdk);
        testGamingActivityEvent(sdk);
        testBatchOperations(sdk);
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
    
    private static void testCustomerProfile(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Customer Profile ===");
        try {
            CustomerProfile customer = createSampleCustomer();
            var response = sdk.sendCustomerProfile(customer);
            printResponse("Customer Profile", response);
        } catch (Exception e) {
            System.err.println("❌ Customer profile failed: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testAccountEvent(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Account Event ===");
        try {
            AccountEvent event = createSampleAccountEvent();
            var response = sdk.sendAccountEvent(event);
            printResponse("Account Event", response);
        } catch (Exception e) {
            System.err.println("❌ Account event failed: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testDepositEvent(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Deposit Event ===");
        try {
            DepositEvent event = createSampleDepositEvent();
            var response = sdk.sendDepositEvent(event);
            printResponse("Deposit Event", response);
        } catch (Exception e) {
            System.err.println("❌ Deposit event failed: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testWithdrawEvent(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Withdraw Event ===");
        try {
            WithdrawEvent event = createSampleWithdrawEvent();
            var response = sdk.sendWithdrawEvent(event);
            printResponse("Withdraw Event", response);
        } catch (Exception e) {
            System.err.println("❌ Withdraw event failed: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testGamingActivityEvent(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Gaming Activity Event ===");
        try {
            GamingActivityEvent event = createSampleGamingActivityEvent();
            var response = sdk.sendGamingActivityEvent(event);
            printResponse("Gaming Activity Event", response);
        } catch (Exception e) {
            System.err.println("❌ Gaming activity event failed: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testBatchOperations(OptikpiDataPipelineSDK sdk) {
        System.out.println("=== Batch Operations ===");
        try {
            BatchData batchData = new BatchData();
            batchData.setCustomers(Arrays.asList(createSampleCustomer()));
            batchData.setAccountEvents(Arrays.asList(createSampleAccountEvent()));
            batchData.setDepositEvents(Arrays.asList(createSampleDepositEvent()));
            batchData.setWithdrawEvents(Arrays.asList(createSampleWithdrawEvent()));
            batchData.setGamingEvents(Arrays.asList(createSampleGamingActivityEvent()));
            
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
            } else {
                System.out.println("❌ Batch operation failed");
            }
        } catch (Exception e) {
            System.err.println("❌ Batch operation failed: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static CustomerProfile createSampleCustomer() {
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
        customer.setCreationTimestamp(Instant.now().toString());
        
        // Add some custom data
        Map<String, Object> customData = new HashMap<>();
        customData.put("preferred_game_category", "slots");
        customData.put("marketing_source", "google_ads");
        customer.setCustomData(customData);
        
        return customer;
    }
    
    private static AccountEvent createSampleAccountEvent() {
        AccountEvent event = new AccountEvent();
        event.setAccountId("acc_12345");
        event.setWorkspaceId("ws_67890");
        event.setUserId("user_001");
        event.setEventName("Login");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setDevice("desktop");
        event.setStatus("completed");
        return event;
    }
    
    private static DepositEvent createSampleDepositEvent() {
        DepositEvent event = new DepositEvent();
        event.setAccountId("acc_12345");
        event.setWorkspaceId("ws_67890");
        event.setUserId("user_001");
        event.setEventName("Deposit Completed");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("credit_card");
        event.setTransactionId("txn_" + System.currentTimeMillis());
        event.setStatus("completed");
        event.setDevice("mobile");
        return event;
    }
    
    private static WithdrawEvent createSampleWithdrawEvent() {
        WithdrawEvent event = new WithdrawEvent();
        event.setAccountId("acc_12345");
        event.setWorkspaceId("ws_67890");
        event.setUserId("user_001");
        event.setEventName("Withdrawal Completed");
        event.setEventId("evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("50.00"));
        event.setCurrency("USD");
        event.setWithdrawalMethod("bank_transfer");
        event.setTransactionId("txn_" + System.currentTimeMillis());
        event.setStatus("completed");
        event.setDevice("desktop");
        return event;
    }
    
    private static GamingActivityEvent createSampleGamingActivityEvent() {
        GamingActivityEvent event = new GamingActivityEvent();
        event.setAccountId("acc_12345");
        event.setWorkspaceId("ws_67890");
        event.setUserId("user_001");
        event.setEventName("Game Started");
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
}
