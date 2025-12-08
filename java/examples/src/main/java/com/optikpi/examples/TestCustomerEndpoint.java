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
            customer.setPhoneNumber("+1234567890");
            customer.setGender("Male");
            customer.setCountry("US");
            customer.setCity("New York");
            customer.setLanguage("en");
            customer.setCurrency("USD");
            customer.setPhoneVerification(true);
            customer.setEmailVerification(true);
            customer.setBankVerification(false);
            customer.setIddocVerification(true);
            customer.setMarketingEmailPreference(true);
            customer.setNotificationsPreference(true);
            customer.setSubscription("Subscribed");
            Map<String, Object> privacySettings = new HashMap<>();
            privacySettings.put("show_profile", true);
            privacySettings.put("allow_messages", false);
            customer.setPrivacySettings(privacySettings);
            Map<String, Object> depositLimits = new HashMap<>();
            depositLimits.put("daily", 1000);
            depositLimits.put("weekly", 5000);
            customer.setDepositLimits(depositLimits);
            Map<String, Object> lossLimits = new HashMap<>();
            lossLimits.put("daily", 500);
            lossLimits.put("weekly", 2000);
            customer.setLossLimits(lossLimits);
            Map<String, Object> wageringLimits = new HashMap<>();
            wageringLimits.put("daily", 2000);
            wageringLimits.put("weekly", 10000);
            customer.setWageringLimits(wageringLimits);
            Map<String, Object> sessionTimeLimits = new HashMap<>();
            sessionTimeLimits.put("max_minutes", 120);
            customer.setSessionTimeLimits(sessionTimeLimits);
            customer.setCoolingOffPeriod("7 days");
            customer.setSelfExclusionPeriod("30 days");
            customer.setCoolingOffExpiryDate("2024-12-31T23:59:59Z");
            customer.setSelfExclusionExpiryDate("2025-01-31T23:59:59Z");
            customer.setRealityChecksNotification(true);
            customer.setAccountStatus("Active");
            customer.setVipStatus("Gold");
            Map<String, Object> loyaltyTiers = new HashMap<>();
            loyaltyTiers.put("current_tier", "Gold");
            loyaltyTiers.put("points", 1500);
            customer.setLoyaltyProgramTiers(loyaltyTiers);
            customer.setBonusAbuser(false);
            customer.setFinancialRiskLevel("low");
            customer.setAcquisitionSource("organic_search");
            customer.setPartnerId("partner_123");
            customer.setAffiliateId("aff_456");
            customer.setReferralLinkCode("REF12345");
            customer.setReferralLimitReached(false);
            customer.setCreationTimestamp(Instant.now().toString());
            customer.setRiskScoreLevel("low");
            customer.setMarketingSmsPreference(true);
            Map<String, Object> customData = new HashMap<>();
            customData.put("favorite_game", "slots");
            customData.put("newsletter_signup", true);
            customer.setCustomData(customData);
            customer.setSelfExclusionBy("player");
            customer.setSelfExclusionByType("voluntary");
            customer.setSelfExclusionCheckTime("2024-01-15T10:30:00Z");
            customer.setSelfExclusionCreatedTime("2024-01-01T00:00:00Z");
            customer.setClosedTime(null);
            customer.setRealMoneyEnabled(true);
            customer.setPushToken("push_token_abc123");

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
