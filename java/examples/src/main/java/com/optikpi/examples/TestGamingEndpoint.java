package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.GamingActivityEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

public class TestGamingEndpoint {

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

        System.out.println("=== Optikpi Data Pipeline SDK - Gaming Event Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();

        System.out.println("Test 1: Sending gaming event...");
        testGamingEvent(sdk, accountId, workspaceId);
    }

    private static void testGamingEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            GamingActivityEvent event = new GamingActivityEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user_001");
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

            ValidationResult validResult = event.validate();

            if (!validResult.isValid()) {
                System.out.println("❌ Invalid gaming event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            } else {
                System.out.println("✅ Valid gaming event: " + validResult.isValid());
            }

            var response = sdk.sendGamingActivityEvent(event);

            if (response.isSuccess()) {
                System.out.println("✅ Gaming event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send gaming event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
