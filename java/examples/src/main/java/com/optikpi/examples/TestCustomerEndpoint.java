package com.optikpi.examples;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Example: Test Customer Profile Endpoint Demonstrates how to send customer
 * profile data to the Optikpi Data Pipeline API
 */
public class TestCustomerEndpoint {

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

        System.out.println("=== Optikpi Data Pipeline SDK - Customer Profile Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();

        // Test 1: Single customer profile
        System.out.println("Test 1: Sending single customer profile...");
        testSingleCustomerProfile(sdk, accountId, workspaceId);

        // Test 2: Multiple customer profiles
        System.out.println("\nTest 2: Sending multiple customer profiles...");
        testMultipleCustomerProfiles(sdk, accountId, workspaceId);

        // Test 3: Health check
        System.out.println("\nTest 3: Health check...");
        testHealthCheck(sdk);
    }

    private static void testSingleCustomerProfile(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            // Create a customer profile
            CustomerProfile customer = new CustomerProfile();
            customer.setAccountId(accountId);
            customer.setWorkspaceId(workspaceId);
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

            ValidationResult validResult = customer.validate();
            if (!validResult.isValid()) {
                System.out.println("❌ Invalid customer event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            } else {
                System.out.println("✅ Valid customer event: " + validResult.isValid());
            }

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

    private static void testMultipleCustomerProfiles(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            // Create multiple customer profiles
            CustomerProfile customer1 = new CustomerProfile();
            customer1.setAccountId(accountId);
            customer1.setWorkspaceId(workspaceId);
            customer1.setUserId("user_001");
            customer1.setUsername("john_doe");
            customer1.setEmail("john.doe@example.com");
            customer1.setFullName("John Doe");
            customer1.setAccountStatus("Active");
            customer1.setCreationTimestamp(Instant.now().toString());

            CustomerProfile customer2 = new CustomerProfile();
            customer2.setAccountId(accountId);
            customer2.setWorkspaceId(workspaceId);
            customer2.setUserId("user_002");
            customer2.setUsername("jane_smith");
            customer2.setEmail("jane.smith@example.com");
            customer2.setFullName("Jane Smith");
            customer2.setAccountStatus("Active");
            customer2.setCreationTimestamp(Instant.now().toString());

            Map<String, CustomerProfile> validate = new LinkedHashMap<>();
            validate.put("customer1", customer1);
            validate.put("customer2", customer2);

            for (Map.Entry<String, CustomerProfile> entry : validate.entrySet()) {
                String eventName = entry.getKey();
                CustomerProfile event = entry.getValue();
                ValidationResult validResult = event.validate();

                if (!validResult.isValid()) {
                    System.out.println("❌ Invalid " + eventName);
                    System.out.println("Errors: " + validResult.getErrors());
                    return; // stops the loop after first invalid event
                } else {
                    System.out.println("✅ Valid " + eventName + " :"+validResult.isValid());
                }
            }

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
