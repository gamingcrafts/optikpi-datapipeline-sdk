package com.optikpi.examples;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map; 

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.ReferFriendEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class TestReferFriendEndpoint {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        String authToken = dotenv.get("AUTH_TOKEN");
        String accountId = dotenv.get("ACCOUNT_ID");
        String workspaceId = dotenv.get("WORKSPACE_ID");
        String baseUrl = dotenv.get("API_BASE_URL");

        if (authToken == null || accountId == null || workspaceId == null) {
            System.err.println("Error: Missing required environment variables");
            System.exit(1);
        }

        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        config.setBaseUrl(baseUrl);

        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

        System.out.println("🚀 Testing Refer Friend Event Endpoints");
        System.out.println("=======================================");
        System.out.println("Configuration:");
       System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();
        System.out.println("\nMaking API request using SDK...");

        System.out.println("Test 1: Sending refer friend event...");
        testReferFriendEvent(sdk, accountId, workspaceId);
    }

    private static void testReferFriendEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            ReferFriendEvent event = new ReferFriendEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("vinmathi_002");
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

            System.out.println("\n📋ReferFriend Event Data:");
            System.out.println(mapper.writeValueAsString(event));

            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }

            System.out.println("\n🕒 making API request using SDK...");
            long start = System.currentTimeMillis();
            var response = sdk.sendReferFriendEvent(event);
            long end = System.currentTimeMillis();

            System.out.println("\n📡 API Response:");
            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());
            System.out.println("Sending ReferFriend event...");
            
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


