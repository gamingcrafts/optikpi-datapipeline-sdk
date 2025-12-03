package com.optikpi.examples;

import java.time.Instant; 

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.ReferFriendEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

public class TestReferFriendEndpoint {

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

        System.out.println("=== Optikpi Data Pipeline SDK - Refer Friend Event Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();

        System.out.println("Test 1: Sending refer friend event...");
        testReferFriendEvent(sdk, accountId, workspaceId);
    }

    private static void testReferFriendEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            ReferFriendEvent event = new ReferFriendEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user12345");
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

            ValidationResult validResult = event.validate();

            if (!validResult.isValid()) {
                System.out.println("❌ Invalid refer friend event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            } else {
                System.out.println("✅ Valid refer friend event: " + validResult.isValid());
            }

            var response = sdk.sendReferFriendEvent(event);

            if (response.isSuccess()) {
                System.out.println("✅ Refer friend event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send refer friend event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}