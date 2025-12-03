package com.optikpi.datapipeline;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.GamingActivityEvent;
import com.optikpi.datapipeline.model.WithdrawEvent;

/**
 * Integration tests for the Data Pipeline SDK
 * These tests require actual API credentials to be set as environment variables:
 * - AUTH_TOKEN (or OPTIKPI_AUTH_TOKEN)
 * - ACCOUNT_ID (or OPTIKPI_ACCOUNT_ID)
 * - WORKSPACE_ID (or OPTIKPI_WORKSPACE_ID)
 * - API_BASE_URL (optional, defaults to production URL)
 */
class IntegrationTest {

    private String authToken;
    private String accountId;
    private String workspaceId;
    private String baseUrl;
    private boolean credentialsAvailable;

    @BeforeEach
    void setUp() {
        // Try both environment variable formats
        authToken = System.getenv("AUTH_TOKEN");
        if (authToken == null) {
            authToken = System.getenv("OPTIKPI_AUTH_TOKEN");
        }
        
        accountId = System.getenv("ACCOUNT_ID");
        if (accountId == null) {
            accountId = System.getenv("OPTIKPI_ACCOUNT_ID");
        }
        
        workspaceId = System.getenv("WORKSPACE_ID");
        if (workspaceId == null) {
            workspaceId = System.getenv("OPTIKPI_WORKSPACE_ID");
        }
        
        baseUrl = System.getenv("API_BASE_URL");
        if (baseUrl == null) {
            baseUrl = System.getenv("OPTIKPI_BASE_URL");
        }
        
        credentialsAvailable = (authToken != null && accountId != null && workspaceId != null);
    }

    @Test
    void testSdkCreation() {
        ClientConfig config = new ClientConfig("test_token", "test_account", "test_workspace");
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        assertNotNull(sdk);
        assertNotNull(sdk.getConfig());
        assertEquals("test_account", sdk.getConfig().getAccountId());
        assertEquals("test_workspace", sdk.getConfig().getWorkspaceId());
    }

    @Test
    void testSdkCreationWithCustomConfig() {
        ClientConfig config = new ClientConfig("test_token", "test_account", "test_workspace");
        config.setTimeout(60000);
        config.setRetries(5);
        config.setBaseUrl("https://custom-api.example.com");
        
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        assertNotNull(sdk);
        assertEquals(60000, sdk.getConfig().getTimeout());
        assertEquals(5, sdk.getConfig().getRetries());
        assertEquals("https://custom-api.example.com", sdk.getConfig().getBaseUrl());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "AUTH_TOKEN", matches = ".*")
    void testHealthCheckWithRealCredentials() {
        if (!credentialsAvailable) {
            System.out.println("⚠️ Skipping integration test - credentials not available");
            return;
        }
        
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        if (baseUrl != null) {
            config.setBaseUrl(baseUrl);
        }
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        var response = sdk.healthCheck();
        
        assertNotNull(response);
        assertNotNull(response.getTimestamp());
        System.out.println("✅ Health check status: " + response.getStatus());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "AUTH_TOKEN", matches = ".*")
    void testSendAccountEventWithRealCredentials() {
        if (!credentialsAvailable) {
            System.out.println("⚠️ Skipping integration test - credentials not available");
            return;
        }
        
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        if (baseUrl != null) {
            config.setBaseUrl(baseUrl);
        }
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        AccountEvent event = new AccountEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("integration_test_user_" + System.currentTimeMillis());
        event.setEventName("Login");
        event.setEventId("integration_test_evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setDevice("desktop");
        event.setStatus("completed");
        
        var response = sdk.sendAccountEvent(event);
        
        assertNotNull(response);
        System.out.println("✅ Account event status: " + response.getStatus());
        if (!response.isSuccess()) {
            System.out.println("⚠️ Response: " + response.getData());
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "AUTH_TOKEN", matches = ".*")
    void testSendDepositEventWithRealCredentials() {
        if (!credentialsAvailable) {
            System.out.println("⚠️ Skipping integration test - credentials not available");
            return;
        }
        
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        if (baseUrl != null) {
            config.setBaseUrl(baseUrl);
        }
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        DepositEvent event = new DepositEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("integration_test_user_" + System.currentTimeMillis());
        event.setEventName("Successful Deposit");
        event.setEventId("integration_test_evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("credit_card");
        event.setTransactionId("integration_test_txn_" + System.currentTimeMillis());
        event.setStatus("success");
        event.setDevice("mobile");
        
        var response = sdk.sendDepositEvent(event);
        
        assertNotNull(response);
        System.out.println("✅ Deposit event status: " + response.getStatus());
        if (!response.isSuccess()) {
            System.out.println("⚠️ Response: " + response.getData());
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "AUTH_TOKEN", matches = ".*")
    void testSendWithdrawEventWithRealCredentials() {
        if (!credentialsAvailable) {
            System.out.println("⚠️ Skipping integration test - credentials not available");
            return;
        }
        
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        if (baseUrl != null) {
            config.setBaseUrl(baseUrl);
        }
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        WithdrawEvent event = new WithdrawEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("integration_test_user_" + System.currentTimeMillis());
        event.setEventName("Successful Withdrawal");
        event.setEventId("integration_test_evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("50.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("bank");
        event.setTransactionId("integration_test_txn_" + System.currentTimeMillis());
        event.setStatus("success");
        event.setDevice("desktop");
        
        var response = sdk.sendWithdrawEvent(event);
        
        assertNotNull(response);
        System.out.println("✅ Withdraw event status: " + response.getStatus());
        if (!response.isSuccess()) {
            System.out.println("⚠️ Response: " + response.getData());
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "AUTH_TOKEN", matches = ".*")
    void testSendGamingActivityEventWithRealCredentials() {
        if (!credentialsAvailable) {
            System.out.println("⚠️ Skipping integration test - credentials not available");
            return;
        }
        
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        if (baseUrl != null) {
            config.setBaseUrl(baseUrl);
        }
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        GamingActivityEvent event = new GamingActivityEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("integration_test_user_" + System.currentTimeMillis());
        event.setEventName("Play Casino Game");
        event.setEventId("integration_test_evt_" + System.currentTimeMillis());
        event.setEventTime(Instant.now().toString());
        event.setGameId("game_001");
        event.setGameTitle("Poker");
        event.setDevice("mobile");
        
        var response = sdk.sendGamingActivityEvent(event);
        
        assertNotNull(response);
        System.out.println("✅ Gaming activity event status: " + response.getStatus());
        if (!response.isSuccess()) {
            System.out.println("⚠️ Response: " + response.getData());
        }
    }

    @Test
    void testCustomerProfileValidation() {
        CustomerProfile customer = new CustomerProfile();
        customer.setAccountId("test_account");
        customer.setWorkspaceId("test_workspace");
        customer.setUserId("test_user");
        customer.setUsername("test_username");
        customer.setEmail("test@example.com");
        customer.setAccountStatus("Active");
        
        var result = customer.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testAccountEventValidation() {
        AccountEvent event = new AccountEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("test_user");
        event.setEventName("Login");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setDevice("desktop");
        event.setStatus("completed");
        
        var result = event.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testDepositEventValidation() {
        DepositEvent event = new DepositEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("test_user");
        event.setEventName("Successful Deposit");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("credit_card");
        event.setTransactionId("txn_001");
        event.setStatus("success");
        
        var result = event.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testWithdrawEventValidation() {
        WithdrawEvent event = new WithdrawEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("test_user");
        event.setEventName("Successful Withdrawal");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("50.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("bank");
        event.setTransactionId("txn_001");
        event.setStatus("success");
        
        var result = event.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testGamingActivityEventValidation() {
        GamingActivityEvent event = new GamingActivityEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("test_user");
        event.setEventName("Play Casino Game");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        
        var result = event.validate();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testCredentialsLoading() {
        System.out.println("=== Integration Test Credentials Status ===");
        System.out.println("AUTH_TOKEN available: " + (authToken != null));
        System.out.println("ACCOUNT_ID available: " + (accountId != null));
        System.out.println("WORKSPACE_ID available: " + (workspaceId != null));
        System.out.println("API_BASE_URL available: " + (baseUrl != null));
        System.out.println("All credentials available: " + credentialsAvailable);
        System.out.println("==========================================");
        
        // This test always passes but logs credential status
        assertTrue(true);
    }
}