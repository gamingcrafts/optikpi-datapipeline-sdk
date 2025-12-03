package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

public class TestDepositEndpoint {

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

        System.out.println("=== Optikpi Data Pipeline SDK - Deposit Event Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();

        System.out.println("Test 1: Sending deposit event...");
        testDepositEvent(sdk, accountId, workspaceId);
        
    }

    private static void testDepositEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            DepositEvent event = new DepositEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user_001");
            event.setEventName("Successful Deposit"); // ✅ CORRECTED
            event.setEventId("evt_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setAmount(new BigDecimal("100.00"));
            event.setCurrency("USD");
            event.setPaymentMethod("credit_card");
            event.setTransactionId("txn_" + System.currentTimeMillis());
            event.setStatus("success"); // ✅ CORRECTED (not "completed")
            event.setDevice("mobile");

            ValidationResult validResult = event.validate();

            if (!validResult.isValid()) {
                System.out.println("❌ Invalid deposit event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            } else {
                System.out.println("✅ Valid deposit event: " + validResult.isValid());
            }

            var response = sdk.sendDepositEvent(event);

            if (response.isSuccess()) {
                System.out.println("✅ Deposit event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send deposit event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
