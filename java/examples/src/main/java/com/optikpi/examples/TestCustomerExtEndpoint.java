package com.optikpi.examples;

import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ApiResponse;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.ExtendedAttributesEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Test class for Extended Attributes (Customer Extension) endpoint
 * Tests both object format and JSON string format for ext_data
 */
public class TestCustomerExtEndpoint {

    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        String authToken = dotenv.get("AUTH_TOKEN");
        String accountId = dotenv.get("ACCOUNT_ID");
        String workspaceId = dotenv.get("WORKSPACE_ID");
        String baseUrl = dotenv.get("API_BASE_URL");

        // Validate required environment variables
        if (authToken == null || accountId == null || workspaceId == null) {
            System.err.println("❌ Error: Missing required environment variables!");
            System.err.println("   Please set: AUTH_TOKEN, ACCOUNT_ID, WORKSPACE_ID");
            System.err.println("   Copy env.example to .env and fill in your values");
            System.exit(1);
        }

        // Initialize SDK
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        config.setBaseUrl(baseUrl);

        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

        System.out.println("🚀 Testing Customer Extended Attributes Endpoints");
        System.out.println("==================================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " + baseUrl);
        System.out.println("👤 Account ID: " + accountId);
        System.out.println("🏢 Workspace ID: " + workspaceId);
        System.out.println("🔐 Auth Token: " + authToken.substring(0, 6) + "******");
        System.out.println();
        System.out.println("\nMaking API request using SDK...");

        // Test Format 1: Object format
        testObjectFormat(sdk, accountId, workspaceId);

        // Test Format 2: JSON string format
        testJsonStringFormat(sdk, accountId, workspaceId);
    }

    /**
     * Test Format 1: Object format (auto-converted to JSON string)
     */
    private static void testObjectFormat(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            System.out.println("📋 Testing Format 1: Object Format (auto-converted to JSON string)");
            System.out.println("================================================================");

            // Create extended attributes event with Map for ext_data
            ExtendedAttributesEvent event = new ExtendedAttributesEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("vinKLT71234");
            event.setListName("BINGO_PREFERENCES");

            // Create ext_data as Map (will be auto-converted to JSON string)
            Map<String, String> extData = new HashMap<>();
            extData.put("Email", "True");
            extData.put("SMS", "True");
            extData.put("PushNotifications", "False");
            event.setExtData(extData);

            System.out.println("Extended Attributes Data (Object):");
            System.out.println("  User ID: " + event.getUserId());
            System.out.println("  List Name: " + event.getListName());
            System.out.println("  Ext Data: " + extData);
            System.out.println();

            // Validate event
            ValidationResult validResult = event.validate();
            if (!validResult.isValid()) {
                System.out.println("❌ Invalid extended attributes event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            }

            // Send event
            long startTime = System.currentTimeMillis();
            ApiResponse<Object> response = sdk.sendExtendedAttributes(event);
            long endTime = System.currentTimeMillis();

            // Print results
            if (response.isSuccess()) {
                System.out.println("\n✅ Format 1 Success!");
                System.out.println("HTTP Status: " + response.getStatus());
                System.out.println("Response Time: " + (endTime - startTime) + "ms");
                System.out.println("SDK Success: " + response.isSuccess());
                System.out.println("Response Data: " + response.getData());
            } else {
                System.out.println("\n❌ Format 1 API Error!");
                System.out.println("HTTP Status: " + response.getStatus());
                System.out.println("Response Time: " + (endTime - startTime) + "ms");
                System.out.println("SDK Success: " + response.isSuccess());
                System.out.println("Error: " + response.getError());
                System.out.println("Error Data: " + response.getData());
            }

        } catch (Exception e) {
            System.err.println("\n❌ SDK Error occurred!");
            System.err.println("============================================");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test Format 2: JSON string format (legacy format)
     */
    private static void testJsonStringFormat(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            System.out.println("\n📋 Testing Format 2: JSON String Format (legacy)");
            System.out.println("===============================================");

            // Create extended attributes event with JSON string for ext_data
            ExtendedAttributesEvent event = new ExtendedAttributesEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("vinKLT81234");
            event.setListName("GAMING_PREFERENCES");

            // Create ext_data as JSON string (legacy format)
            String extDataJson = "{\"Email\":\"True\",\"SMS\":\"True\",\"PushNotifications\":\"True\"}";
            event.setExtDataAsString(extDataJson);

            System.out.println("Extended Attributes Data (String):");
            System.out.println("  User ID: " + event.getUserId());
            System.out.println("  List Name: " + event.getListName());
            System.out.println("  Ext Data: " + extDataJson);
            System.out.println();

            // Validate event
            ValidationResult validResult = event.validate();
            if (!validResult.isValid()) {
                System.out.println("❌ Invalid extended attributes event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            }

            // Send event
            long startTime = System.currentTimeMillis();
            ApiResponse<Object> response = sdk.sendExtendedAttributes(event);
            long endTime = System.currentTimeMillis();

            // Print results
            if (response.isSuccess()) {
                System.out.println("\n✅ Format 2 Success!");
                System.out.println("HTTP Status: " + response.getStatus());
                System.out.println("Response Time: " + (endTime - startTime) + "ms");
                System.out.println("SDK Success: " + response.isSuccess());
                System.out.println("Response Data: " + response.getData());
            } else {
                System.out.println("\n❌ Format 2 API Error!");
                System.out.println("HTTP Status: " + response.getStatus());
                System.out.println("Response Time: " + (endTime - startTime) + "ms");
                System.out.println("SDK Success: " + response.isSuccess());
                System.out.println("Error: " + response.getError());
                System.out.println("Error Data: " + response.getData());
            }

        } catch (Exception e) {
            System.err.println("\n❌ SDK Error occurred!");
            System.err.println("============================================");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}