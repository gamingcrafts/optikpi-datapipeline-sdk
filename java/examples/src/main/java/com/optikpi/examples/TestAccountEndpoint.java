package com.optikpi.examples;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestAccountEndpoint {

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

        System.out.println("🚀 Testing Account Event Endpoints");
        System.out.println("===================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();    
        testLoginEvent(sdk, accountId, workspaceId);
    }

    private static void testLoginEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            AccountEvent event = new AccountEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("java_01");
            event.setEventCategory("Account");
            event.setEventName("Player Registration");
            event.setEventId("evt_login_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setAffiliateId("aff_001");
            event.setPartnerId("partner_001");
            event.setDevice("desktop");
            event.setCampaignCode("CAMPAIGN_001");
            event.setStatus("completed");
            event.setReason("Registration completed successfully");


            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }
            
            System.out.println("✅ Accout event validated successfully!");
            System.out.println("\n📋 Account Event Data:");
            System.out.println(mapper.writeValueAsString(event));
           
            System.out.println("\n🕒 making API request using SDK..."); 
            long start = System.currentTimeMillis();
            var response = sdk.sendAccountEvent(event);
            long end = System.currentTimeMillis();

            System.out.println("\n📡 API Response:");
            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());
            System.out.println("Sending Account event...");

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
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
