package com.optikpi.examples;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

public class TestAccountEndpoint {

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

        System.out.println("=== Optikpi Data Pipeline SDK - Account Event Test ===");
        System.out.println("Base URL: " + config.getBaseUrl());
        System.out.println("Account ID: " + config.getAccountId());
        System.out.println("Workspace ID: " + config.getWorkspaceId());
        System.out.println();

        System.out.println("Test 1: Sending login event...");
        testLoginEvent(sdk, accountId, workspaceId);

        System.out.println("\nTest 2: Sending account verification event...");
        testAccountVerificationEvent(sdk, accountId, workspaceId);

        System.out.println("\nTest 3: Sending multiple account events...");
        testMultipleAccountEvents(sdk, accountId, workspaceId);
    }

    private static void testLoginEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            AccountEvent event = new AccountEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user_001");
            event.setEventName("Login");
            event.setEventId("evt_login_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setDevice("desktop");
            event.setStatus("completed");
            event.setAffiliateId("aff_001");
            event.setPartnerId("partner_001");
            ValidationResult validResult = event.validate();
            if (!validResult.isValid()) {
                System.out.println("❌ Invalid account event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            } else {
                System.out.println("✅ Valid account event: " + validResult.isValid());
            }

            var response = sdk.sendAccountEvent(event);
            if (response.isSuccess()) {
                System.out.println("✅ Login event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send login event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testAccountVerificationEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            AccountEvent event = new AccountEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("user_001");
            event.setEventName("Account Verification");
            event.setEventId("evt_verification_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setDevice("mobile");
            event.setStatus("verified");
            event.setReason("Email verification completed");
            ValidationResult validResult = event.validate();
            if (!validResult.isValid()) {
                System.out.println("❌ Invalid accountverification event:");
                System.out.println("Errors: " + validResult.getErrors());
                return;
            } else {
                System.out.println("✅ Valid accountverification event: " + validResult.isValid());
            }

            var response = sdk.sendAccountEvent(event);
            if (response.isSuccess()) {
                System.out.println("✅ Account verification event sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send account verification event");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testMultipleAccountEvents(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            AccountEvent loginEvent = new AccountEvent();
            loginEvent.setAccountId(accountId);
            loginEvent.setWorkspaceId(workspaceId);
            loginEvent.setUserId("user_001");
            loginEvent.setEventName("Login");
            loginEvent.setEventId("evt_login_" + System.currentTimeMillis());
            loginEvent.setEventTime(Instant.now().toString());
            loginEvent.setDevice("desktop");
            loginEvent.setStatus("completed");

            AccountEvent logoutEvent = new AccountEvent();
            logoutEvent.setAccountId(accountId);
            logoutEvent.setWorkspaceId(workspaceId);
            logoutEvent.setUserId("user_001");
            logoutEvent.setEventName("Logout");
            logoutEvent.setEventId("evt_logout_" + System.currentTimeMillis());
            logoutEvent.setEventTime(Instant.now().toString());
            logoutEvent.setDevice("desktop");
            logoutEvent.setStatus("completed");

            AccountEvent passwordChangeEvent = new AccountEvent();
            passwordChangeEvent.setAccountId(accountId);
            passwordChangeEvent.setWorkspaceId(workspaceId);
            passwordChangeEvent.setUserId("user_001");
            passwordChangeEvent.setEventName("Password Change");
            passwordChangeEvent.setEventId("evt_password_" + System.currentTimeMillis());
            passwordChangeEvent.setEventTime(Instant.now().toString());
            passwordChangeEvent.setDevice("mobile");
            passwordChangeEvent.setStatus("completed");

            Map<String, AccountEvent> validate = new LinkedHashMap<>();
            validate.put("loginEvent", loginEvent);
            validate.put("logoutEvent", logoutEvent);
            validate.put("passwordChangeEvent", passwordChangeEvent);

            for (Map.Entry<String, AccountEvent> entry : validate.entrySet()) {
                String eventName = entry.getKey();
                AccountEvent event = entry.getValue();
                ValidationResult validResult = event.validate();
                if (!validResult.isValid()) {
                    System.out.println("❌ Invalid " + eventName );
                    System.out.println("Errors: " + validResult.getErrors());
                    return; // stops the loop after first invalid event
                } else {
                    System.out.println("✅ Valid " + eventName + " :"+validResult.isValid());
                }
            }

            var response = sdk.sendAccountEvent(Arrays.asList(loginEvent, logoutEvent, passwordChangeEvent));

            if (response.isSuccess()) {
                System.out.println("✅ Multiple account events sent successfully!");
                System.out.println("Status: " + response.getStatus());
                System.out.println("Response: " + response.getData());
            } else {
                System.out.println("❌ Failed to send multiple account events");
                System.out.println("Error: " + response.getError());
                System.out.println("Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
