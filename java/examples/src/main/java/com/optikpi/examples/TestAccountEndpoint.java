package com.optikpi.examples;

import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.ValidationResult;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Instant;
import java.util.Arrays;

/**
 * Example: Test Account Event Endpoint
 * Demonstrates how to send account event data to the Optikpi Data Pipeline API
 */
public class TestAccountEndpoint {
    
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
        
        System.out.println("=== Optikpi Data Pipeline SDK - Account Event Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();
        
        // Test 1: Login event
        System.out.println("Test 1: Sending login event...");
        testLoginEvent(sdk);
        
        // Test 2: Account verification event
        System.out.println("\nTest 2: Sending account verification event...");
        testAccountVerificationEvent(sdk);
        
        // Test 3: Multiple account events
        System.out.println("\nTest 3: Sending multiple account events...");
        testMultipleAccountEvents(sdk);
        
        // Test 4: Account event validation
        System.out.println("\nTest 4: Account event validation...");
        testAccountEventValidation();
    }
    
    private static void testLoginEvent(OptikpiDataPipelineSDK sdk) {
        try {
            AccountEvent event = new AccountEvent();
            event.setAccountId("acc_12345");
            event.setWorkspaceId("ws_67890");
            event.setUserId("user_001");
            event.setEventName("Login");
            event.setEventId("evt_login_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setDevice("desktop");
            event.setStatus("completed");
            event.setAffiliateId("aff_001");
            event.setPartnerId("partner_001");
            
            var response = sdk.sendAccountEvent(event);
            
            if (response.isSuccess()) {
                System.out.println("✅ Login event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send login event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testAccountVerificationEvent(OptikpiDataPipelineSDK sdk) {
        try {
            AccountEvent event = new AccountEvent();
            event.setAccountId("acc_12345");
            event.setWorkspaceId("ws_67890");
            event.setUserId("user_001");
            event.setEventName("Account Verification");
            event.setEventId("evt_verification_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setDevice("mobile");
            event.setStatus("verified");
            event.setReason("Email verification completed");
            
            var response = sdk.sendAccountEvent(event);
            
            if (response.isSuccess()) {
                System.out.println("✅ Account verification event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send account verification event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testMultipleAccountEvents(OptikpiDataPipelineSDK sdk) {
        try {
            // Create multiple account events
            AccountEvent loginEvent = new AccountEvent();
            loginEvent.setAccountId("acc_12345");
            loginEvent.setWorkspaceId("ws_67890");
            loginEvent.setUserId("user_001");
            loginEvent.setEventName("Login");
            loginEvent.setEventId("evt_login_" + System.currentTimeMillis());
            loginEvent.setEventTime(Instant.now().toString());
            loginEvent.setDevice("desktop");
            loginEvent.setStatus("completed");
            
            AccountEvent logoutEvent = new AccountEvent();
            logoutEvent.setAccountId("acc_12345");
            logoutEvent.setWorkspaceId("ws_67890");
            logoutEvent.setUserId("user_001");
            logoutEvent.setEventName("Logout");
            logoutEvent.setEventId("evt_logout_" + System.currentTimeMillis());
            logoutEvent.setEventTime(Instant.now().toString());
            logoutEvent.setDevice("desktop");
            logoutEvent.setStatus("completed");
            
            AccountEvent passwordChangeEvent = new AccountEvent();
            passwordChangeEvent.setAccountId("acc_12345");
            passwordChangeEvent.setWorkspaceId("ws_67890");
            passwordChangeEvent.setUserId("user_001");
            passwordChangeEvent.setEventName("Password Change");
            passwordChangeEvent.setEventId("evt_password_" + System.currentTimeMillis());
            passwordChangeEvent.setEventTime(Instant.now().toString());
            passwordChangeEvent.setDevice("mobile");
            passwordChangeEvent.setStatus("completed");
            
            // Send multiple events
            var response = sdk.sendAccountEvent(Arrays.asList(loginEvent, logoutEvent, passwordChangeEvent));
            
            if (response.isSuccess()) {
                System.out.println("✅ Multiple account events sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send multiple account events");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testAccountEventValidation() {
        try {
            // Test valid account event
            AccountEvent validEvent = new AccountEvent();
            validEvent.setAccountId("acc_12345");
            validEvent.setWorkspaceId("ws_67890");
            validEvent.setUserId("user_001");
            validEvent.setEventName("Login");
            validEvent.setEventId("evt_001");
            validEvent.setEventTime(Instant.now().toString());
            validEvent.setDevice("desktop");
            validEvent.setStatus("completed");
            
            ValidationResult validResult = validEvent.validate();
            System.out.println("Valid account event: " + validResult.isValid());
            if (!validResult.isValid()) {
                System.out.println("Errors: " + validResult.getErrors());
            }
            
            // Test invalid account event
            AccountEvent invalidEvent = new AccountEvent();
            invalidEvent.setAccountId(""); // Invalid: empty account ID
            invalidEvent.setEventName("Invalid Event"); // Invalid: not in enum
            invalidEvent.setDevice("invalid_device"); // Invalid: not in enum
            invalidEvent.setStatus("invalid_status"); // Invalid: not in enum
            
            ValidationResult invalidResult = invalidEvent.validate();
            System.out.println("Invalid account event: " + invalidResult.isValid());
            if (!invalidResult.isValid()) {
                System.out.println("Errors: " + invalidResult.getErrors());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
