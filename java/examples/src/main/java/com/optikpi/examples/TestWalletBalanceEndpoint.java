package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.WalletBalanceEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class TestWalletBalanceEndpoint {

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

        System.out.println("🚀 Testing Wallet Balance Event Endpoints");
        System.out.println("==========================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " + baseUrl);
        System.out.println("👤 Account ID: " + accountId);
        System.out.println("🏢 Workspace ID: " + workspaceId);
        System.out.println("🔐 Auth Token: " + authToken.substring(0, 6) + "******");
        System.out.println();
        System.out.println("\nMaking API request using SDK...");

        System.out.println("Test 1: Sending wallet balance event...");
        testWalletBalanceEvent(sdk, accountId, workspaceId);
    }

    private static void testWalletBalanceEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            WalletBalanceEvent event = new WalletBalanceEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("vinmathi_002");
            event.setEventName("Balance Update");
            event.setEventId("evt_wb_987654321");
            event.setEventTime(Instant.now().toString());
            event.setWalletType("main");
            event.setCurrency("USD");
            event.setCurrentCashBalance(new BigDecimal("1250.50"));
            event.setCurrentBonusBalance(new BigDecimal("100.00"));
            event.setCurrentTotalBalance(new BigDecimal("1350.50"));
            event.setBlockedAmount(new BigDecimal("50.00"));

           System.out.println("\n📋WalletBalance Event Data:");
            System.out.println(mapper.writeValueAsString(event));

            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }

            System.out.println("\n🕒 making API request using SDK...");
            long start = System.currentTimeMillis();
            var response = sdk.sendWalletBalanceEvent(event);
            long end = System.currentTimeMillis();

            System.out.println("\n📡 API Response:");
            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());
            System.out.println("Sending WalletBalance event...");
            
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


