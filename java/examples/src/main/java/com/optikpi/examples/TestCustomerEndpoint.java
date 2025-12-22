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

        testCustomerEndpoint(sdk, accountId, workspaceId);
    }

    private static void testCustomerEndpoint(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            // Create customer data matching JavaScript example
            CustomerProfile customer = new CustomerProfile();
            customer.setAccountId(accountId);
            customer.setWorkspaceId(workspaceId);
            customer.setUserId("java_t_03");
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
            customer.setAffliateId("affiliate456");
            
            customer.setCoolingOffPeriod(7);
            customer.setSelfExclusionPeriod(30);

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
            customer.setAndroidPushToken("bRdkk1jSTdKFxOkO3c26rt:APA91bEoaBQn0TnxH7gqJJtyzD9cwUQBGJOtVhQ8kJv6v52Opqb2CTUlfFnPxSPM1PSSG9Ctlb1ASuVfPvKCTchaz_LzZH03TuigP4TsYKYZ76FEKh_GZoA");
            customer.setIosPushToken("bRdkk1jSTdKFxOkO3c26rt:APA91bEoaBQn0TnxH7gqJJtyzD9cwUQBGJOtVhQ8kJv6v52Opqb2CTUlfFnPxSPM1PSSG9Ctlb1ASuVfPvKCTchaz_LzZH03TuigP4TsYKYZ76FEKh_GZoA");
            customer.setWindowsPushToken("bRdkk1jSTdKFxOkO3c26rt:APA91bEoaBQn0TnxH7gqJJtyzD9cwUQBGJOtVhQ8kJv6v52Opqb2CTUlfFnPxSPM1PSSG9Ctlb1ASuVfPvKCTchaz_LzZH03TuigP4TsYKYZ76FEKh_GZoA");
            customer.setMacdmgPushToken("bRdkk1jSTdKFxOkO3c26rt:APA91bEoaBQn0TnxH7gqJJtyzD9cwUQBGJOtVhQ8kJv6v52Opqb2CTUlfFnPxSPM1PSSG9Ctlb1ASuVfPvKCTchaz_LzZH03TuigP4TsYKYZ76FEKh_GZoA");

            // Validate customer data
            ValidationResult valid = customer.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }
            
            System.out.println("✅ Customer event validated successfully!");
            System.out.println("Customer Data:");
            System.out.println(mapper.writeValueAsString(customer));

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
