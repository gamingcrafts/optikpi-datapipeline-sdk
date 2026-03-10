package com.optikpi.examples;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.SystemEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Example of how to send system events using the SDK
 */
public class TestSystemEndpoint {

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

        System.out.println("🚀 Testing System (Back Office) Endpoint");
        System.out.println("========================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();    
        
        // Test 1: event_data as object (Map)
        System.out.println("📋 Test 1: event_data as object");
        testSystemEventWithObject(sdk, accountId, workspaceId);

        System.out.println("\n------------------------------------------------\n");

        // Test 2: event_data as JSON string
        System.out.println("📋 Test 2: event_data as JSON string");
        testSystemEventWithJsonString(sdk, accountId, workspaceId);
    }

    private static void testSystemEventWithObject(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            SystemEvent event = new SystemEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setEventCategory("SystemEvent");
            event.setEventName("Campaign Trigger");
            event.setEventId("evt_sys_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("campaign_id", "camp_001");
            eventData.put("action", "start");
            eventData.put("segment", "vip");
            event.setEventData(eventData);

            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }
            
            System.out.println("✅ System event object validated successfully!");
            executeRequest(sdk, event);
        } catch (Exception e) {
            System.err.println("❌ Object test failed: " + e.getMessage());
        }
    }

    private static void testSystemEventWithJsonString(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            SystemEvent event = new SystemEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setEventCategory("SystemEvent");
            event.setEventName("Manual Action");
            event.setEventId("evt_sys_" + (System.currentTimeMillis() + 1));
            event.setEventTime(Instant.now().toString());
            
            // event_data as JSON string
            String jsonString = "{\"action\":\"notify\",\"target\":\"user_list_1\",\"payload\":{}}";
            event.setEventData(jsonString);

            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }

            System.out.println("✅ System event with JSON string validated successfully!");
            executeRequest(sdk, event);
        } catch (Exception e) {
            System.err.println("❌ JSON string test failed: " + e.getMessage());
        }
    }

    private static void executeRequest(OptikpiDataPipelineSDK sdk, SystemEvent event) {
        try {
            System.out.println("📋 Payload:");
            System.out.println(mapper.writeValueAsString(event));

            long start = System.currentTimeMillis();
            var response = sdk.sendSystemEvent(event);
            long end = System.currentTimeMillis();

            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());

            if (response.isSuccess()) {
                System.out.println("✅ SUCCESS!");
                System.out.println("Response: " + mapper.writeValueAsString(response.getData()));
            } else {
                System.out.println("❌ FAILED!");
                System.out.println("Error: " + response.getError());
            }
        } catch (Exception e) {
            System.err.println("❌ Request execution failed: " + e.getMessage());
        }
    }
}
