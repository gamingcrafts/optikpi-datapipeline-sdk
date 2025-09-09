package com.optikpi.examples;

import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.ValidationResult;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Example: Test Customer Profile Endpoint
 * Demonstrates how to send customer profile data to the Optikpi Data Pipeline API
 */
public class TestCustomerEndpoint {
    
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
        
        System.out.println("=== Optikpi Data Pipeline SDK - Customer Profile Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();
        
        // Test 1: Single customer profile
        System.out.println("Test 1: Sending single customer profile...");
        testSingleCustomerProfile(sdk);
        
        // Test 2: Multiple customer profiles
        System.out.println("\nTest 2: Sending multiple customer profiles...");
        testMultipleCustomerProfiles(sdk);
        
        // Test 3: Customer profile with validation
        System.out.println("\nTest 3: Customer profile validation...");
        testCustomerProfileValidation();
        
        // Test 4: Health check
        System.out.println("\nTest 4: Health check...");
        testHealthCheck(sdk);
    }
    
    private static void testSingleCustomerProfile(OptikpiDataPipelineSDK sdk) {
        try {
            // Create a customer profile
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
            
            // Send the customer profile
            var response = sdk.sendCustomerProfile(customer);
            
            if (response.isSuccess()) {
                System.out.println("✅ Customer profile sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send customer profile");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testMultipleCustomerProfiles(OptikpiDataPipelineSDK sdk) {
        try {
            // Create multiple customer profiles
            CustomerProfile customer1 = new CustomerProfile();
            customer1.setAccountId("acc_12345");
            customer1.setWorkspaceId("ws_67890");
            customer1.setUserId("user_001");
            customer1.setUsername("john_doe");
            customer1.setEmail("john.doe@example.com");
            customer1.setFullName("John Doe");
            customer1.setAccountStatus("Active");
            customer1.setCreationTimestamp(Instant.now().toString());
            
            CustomerProfile customer2 = new CustomerProfile();
            customer2.setAccountId("acc_12346");
            customer2.setWorkspaceId("ws_67890");
            customer2.setUserId("user_002");
            customer2.setUsername("jane_smith");
            customer2.setEmail("jane.smith@example.com");
            customer2.setFullName("Jane Smith");
            customer2.setAccountStatus("Active");
            customer2.setCreationTimestamp(Instant.now().toString());
            
            // Send multiple profiles
            var response = sdk.sendCustomerProfile(Arrays.asList(customer1, customer2));
            
            if (response.isSuccess()) {
                System.out.println("✅ Multiple customer profiles sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send multiple customer profiles");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testCustomerProfileValidation() {
        try {
            // Test valid customer profile
            CustomerProfile validCustomer = new CustomerProfile();
            validCustomer.setAccountId("acc_12345");
            validCustomer.setWorkspaceId("ws_67890");
            validCustomer.setUserId("user_001");
            validCustomer.setUsername("john_doe");
            validCustomer.setEmail("john.doe@example.com");
            validCustomer.setAccountStatus("Active");
            validCustomer.setVipStatus("Regular");
            
            ValidationResult validResult = validCustomer.validate();
            System.out.println("Valid customer profile: " + validResult.isValid());
            if (!validResult.isValid()) {
                System.out.println("Errors: " + validResult.getErrors());
            }
            
            // Test invalid customer profile
            CustomerProfile invalidCustomer = new CustomerProfile();
            invalidCustomer.setAccountId(""); // Invalid: empty account ID
            invalidCustomer.setEmail("invalid-email"); // Invalid: bad email format
            invalidCustomer.setGender("Invalid"); // Invalid: not in enum
            invalidCustomer.setAccountStatus("InvalidStatus"); // Invalid: not in enum
            
            ValidationResult invalidResult = invalidCustomer.validate();
            System.out.println("Invalid customer profile: " + invalidResult.isValid());
            if (!invalidResult.isValid()) {
                System.out.println("Errors: " + invalidResult.getErrors());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testHealthCheck(OptikpiDataPipelineSDK sdk) {
        try {
            var response = sdk.healthCheck();
            
            if (response.isSuccess()) {
                System.out.println("✅ Health check successful!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Health check failed");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
