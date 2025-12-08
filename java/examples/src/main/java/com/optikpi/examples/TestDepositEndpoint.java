package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestDepositEndpoint {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory(".").filename(".env").load();

        String authToken = dotenv.get("AUTH_TOKEN");
        String accountId = dotenv.get("ACCOUNT_ID");
        String workspaceId = dotenv.get("WORKSPACE_ID");
        String baseUrl = dotenv.get("API_BASE_URL");

        if (authToken == null || accountId == null || workspaceId == null) {
            System.err.println("❌ Error: Missing required environment variables (AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID)");
            System.exit(1);
        }

        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        config.setBaseUrl(baseUrl);

        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

        System.out.println("🚀 Testing Deposit Event Endpoints");
        System.out.println("===================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();
        

        sendDepositTest(sdk, accountId, workspaceId);
    }

    private static void sendDepositTest(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            DepositEvent event = new DepositEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("vinmathi_223");
            event.setEventCategory("Deposit");
            event.setEventName("Successful Deposit");
            event.setEventId("evt_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setAmount(new BigDecimal("100.00"));
            event.setCurrency("USD");
            event.setPaymentMethod("bank");
            event.setTransactionId("txn_" + System.currentTimeMillis());
            event.setStatus("success");
            event.setDevice("mobile");
            event.setPaymentProviderId("provider123");
            event.setPaymentProviderName("Chase Bank");
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("bank_name", "Chase Bank");
            metadata.put("account_last4", "1234");
            metadata.put("source", "mobile_app");
            event.setMetadata(metadata);

            System.out.println("\n📋 Deposit Event Data:");
            System.out.println(mapper.writeValueAsString(event));

            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }

            System.out.println("\n🕒 making API request using SDK...");
           
            long start = System.currentTimeMillis();
            var response = sdk.sendDepositEvent(event);
            long end = System.currentTimeMillis();

            System.out.println("\n📡 API Response:");
            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());
            System.out.println("Sending Deposit event...");

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
