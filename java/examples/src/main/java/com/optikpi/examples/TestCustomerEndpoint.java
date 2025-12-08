package com.optikpi.examples;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Example: Test Customer Profile Endpoint Demonstrates how to send customer
 * profile data to the Optikpi Data Pipeline API
 */
public class TestCustomerEndpoint {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

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

        System.out.println("🚀 Testing Customer Profile Endpoints");
        System.out.println("======================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();
        

        testSingleCustomerProfile(sdk, accountId, workspaceId);

    }

    private static void testSingleCustomerProfile(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            // Create a customer profile
            CustomerProfile customer = new CustomerProfile();
            customer.setAccountId(accountId);
            customer.setWorkspaceId(workspaceId);
            customer.setUserId("vinmathi_002");
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

            System.out.println("\n📋 Customer Profile Data:");
            System.out.println(mapper.writeValueAsString(customer));

            ValidationResult valid = customer.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }

            System.out.println("\n🕒 Making API request using SDK...");
         
            long start = System.currentTimeMillis();
            var response = sdk.sendCustomerProfile(customer);
            long end = System.currentTimeMillis();

            System.out.println("\n📡 API Response:");
            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());
            System.out.println("Sending Customer Profile event...");

            if (response.isSuccess()) {
                System.out.println("✅ SUCCESS!");
                System.out.println("Response: " +
                        mapper.writeValueAsString(response.getData()));
            } else {
                System.out.println("❌ FAILED!");
                System.out.println("Error: " +
                        mapper.writeValueAsString(response.getError()));
            }

        } catch (Exception e) {
            System.err.println("\n💥 Exception occurred:");
            e.printStackTrace();
        }
    }
}
