/*package com.optikpi.datapipeline;

import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.WithdrawEvent;
import com.optikpi.datapipeline.model.GamingActivityEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataPipelineClientTest {

    private ClientConfig config;
    private DataPipelineClient client;

    @BeforeEach
    void setUp() {
        config = new ClientConfig("test_token", "test_account", "test_workspace");
        // Use a test base URL that won't actually make requests
        config.setBaseUrl("http://localhost:9999");
        client = new DataPipelineClient(config);
    }

    @Test
    void testConstructorWithValidConfig() {
        assertNotNull(client);
        assertEquals("test_token", client.getConfig().getAuthToken());
        assertEquals("test_account", client.getConfig().getAccountId());
        assertEquals("test_workspace", client.getConfig().getWorkspaceId());
    }

    @Test
    void testConstructorWithNullConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DataPipelineClient(null);
        });
    }

    @Test
    void testConstructorWithMissingAuthToken() {
        ClientConfig invalidConfig = new ClientConfig();
        invalidConfig.setAccountId("test_account");
        invalidConfig.setWorkspaceId("test_workspace");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new DataPipelineClient(invalidConfig);
        });
    }

    @Test
    void testConstructorWithEmptyAuthToken() {
        ClientConfig invalidConfig = new ClientConfig();
        invalidConfig.setAuthToken("");
        invalidConfig.setAccountId("test_account");
        invalidConfig.setWorkspaceId("test_workspace");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new DataPipelineClient(invalidConfig);
        });
    }

    @Test
    void testConstructorWithWhitespaceAuthToken() {
        ClientConfig invalidConfig = new ClientConfig();
        invalidConfig.setAuthToken("   ");
        invalidConfig.setAccountId("test_account");
        invalidConfig.setWorkspaceId("test_workspace");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new DataPipelineClient(invalidConfig);
        });
    }

    @Test
    void testConstructorWithMissingAccountId() {
        ClientConfig invalidConfig = new ClientConfig();
        invalidConfig.setAuthToken("test_token");
        invalidConfig.setWorkspaceId("test_workspace");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new DataPipelineClient(invalidConfig);
        });
    }

    @Test
    void testConstructorWithMissingWorkspaceId() {
        ClientConfig invalidConfig = new ClientConfig();
        invalidConfig.setAuthToken("test_token");
        invalidConfig.setAccountId("test_account");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new DataPipelineClient(invalidConfig);
        });
    }

    @Test
    void testUpdateConfig() {
        ClientConfig newConfig = new ClientConfig();
        newConfig.setAuthToken("new_token");
        newConfig.setAccountId("new_account");
        newConfig.setWorkspaceId("new_workspace");
        newConfig.setTimeout(60000);
        
        client.updateConfig(newConfig);
        
        ClientConfig updatedConfig = client.getConfig();
        assertEquals("new_token", updatedConfig.getAuthToken());
        assertEquals("new_account", updatedConfig.getAccountId());
        assertEquals("new_workspace", updatedConfig.getWorkspaceId());
        assertEquals(60000, updatedConfig.getTimeout());
    }

    @Test
    void testUpdateConfigWithInvalidConfig() {
        ClientConfig invalidConfig = new ClientConfig();
        invalidConfig.setAuthToken(""); // Empty token
        invalidConfig.setAccountId("new_account");
        invalidConfig.setWorkspaceId("new_workspace");
        
        assertThrows(IllegalArgumentException.class, () -> {
            client.updateConfig(invalidConfig);
        });
    }

    @Test
    void testGetConfigReturnsCopy() {
        ClientConfig configCopy = client.getConfig();
        
        // Modify the copy
        configCopy.setTimeout(99999);
        
        // Original config should not be affected
        assertNotEquals(99999, client.getConfig().getTimeout());
    }

    @Test
    void testGetConfigForLogging() {
        ClientConfig loggingConfig = client.getConfigForLogging();
        
        assertNotNull(loggingConfig);
        // Auth token should be masked or handled safely for logging
        assertNotNull(loggingConfig.getAuthToken());
    }

    @Test
    void testClientConfigDefaults() {
        ClientConfig defaultConfig = new ClientConfig("token", "account", "workspace");
        DataPipelineClient defaultClient = new DataPipelineClient(defaultConfig);
        
        ClientConfig config = defaultClient.getConfig();
        assertNotNull(config.getBaseUrl());
        assertTrue(config.getTimeout() > 0);
        assertTrue(config.getRetries() >= 0);
    }

    @Test
    void testSendCustomerProfileWithValidData() {
        CustomerProfile profile = new CustomerProfile();
        profile.setAccountId("test_account");
        profile.setWorkspaceId("test_workspace");
        profile.setUserId("user_001");
        profile.setUsername("testuser");
        profile.setEmail("test@example.com");
        
        // This will fail to connect but tests the client setup
        ApiResponse<Object> response = client.sendCustomerProfile(profile);
        
        assertNotNull(response);
        // Expect connection failure since we're using localhost:9999
        assertFalse(response.isSuccess());
    }

    @Test
    void testSendAccountEventWithValidData() {
        AccountEvent event = new AccountEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("user_001");
        event.setEventName("Login");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setDevice("desktop");
        event.setStatus("completed");
        
        // This will fail to connect but tests the client setup
        ApiResponse<Object> response = client.sendAccountEvent(event);
        
        assertNotNull(response);
        assertFalse(response.isSuccess());
    }

    @Test
    void testSendDepositEventWithValidData() {
        DepositEvent event = new DepositEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("user_001");
        event.setEventName("Successful Deposit");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("100.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("credit_card");
        event.setTransactionId("txn_001");
        event.setStatus("success");
        
        // This will fail to connect but tests the client setup
        ApiResponse<Object> response = client.sendDepositEvent(event);
        
        assertNotNull(response);
        assertFalse(response.isSuccess());
    }

    @Test
    void testSendWithdrawEventWithValidData() {
        WithdrawEvent event = new WithdrawEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("user_001");
        event.setEventName("Successful Withdrawal");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setAmount(new BigDecimal("50.00"));
        event.setCurrency("USD");
        event.setPaymentMethod("bank");
        event.setTransactionId("txn_001");
        event.setStatus("success");
        
        // This will fail to connect but tests the client setup
        ApiResponse<Object> response = client.sendWithdrawEvent(event);
        
        assertNotNull(response);
        assertFalse(response.isSuccess());
    }

    @Test
    void testSendGamingActivityEventWithValidData() {
        GamingActivityEvent event = new GamingActivityEvent();
        event.setAccountId("test_account");
        event.setWorkspaceId("test_workspace");
        event.setUserId("user_001");
        event.setEventName("Play Casino Game");
        event.setEventId("evt_001");
        event.setEventTime(Instant.now().toString());
        event.setGameId("game_001");
        event.setGameTitle("Poker");
        
        // This will fail to connect but tests the client setup
        ApiResponse<Object> response = client.sendGamingActivityEvent(event);
        
        assertNotNull(response);
        assertFalse(response.isSuccess());
    }

    @Test
    void testHealthCheckMethod() {
        // This will fail to connect but tests the client setup
        ApiResponse<Object> response = client.healthCheck();
        
        assertNotNull(response);
        assertFalse(response.isSuccess());
    }

    @Test
    void testSendBatchWithMultipleEvents() {
        BatchData batchData = new BatchData();
        
        CustomerProfile profile = new CustomerProfile();
        profile.setAccountId("test_account");
        profile.setWorkspaceId("test_workspace");
        profile.setUserId("user_001");
        profile.setUsername("testuser");
        profile.setEmail("test@example.com");
        batchData.setCustomers(profile);
        
        AccountEvent accountEvent = new AccountEvent();
        accountEvent.setAccountId("test_account");
        accountEvent.setWorkspaceId("test_workspace");
        accountEvent.setUserId("user_001");
        accountEvent.setEventName("Login");
        accountEvent.setEventId("evt_001");
        accountEvent.setEventTime(Instant.now().toString());
        accountEvent.setDevice("desktop");
        accountEvent.setStatus("completed");
        batchData.setAccountEvents(accountEvent);
        
        // This will fail to connect but tests the client setup
        BatchResponse response = client.sendBatch(batchData);
        
        assertNotNull(response);
        assertNotNull(response.getCustomers());
        assertNotNull(response.getAccountEvents());
    }

    @Test
    void testClientWithCustomTimeout() {
        ClientConfig customConfig = new ClientConfig("token", "account", "workspace");
        customConfig.setTimeout(5000);
        
        DataPipelineClient customClient = new DataPipelineClient(customConfig);
        
        assertEquals(5000, customClient.getConfig().getTimeout());
    }

    @Test
    void testClientWithCustomRetries() {
        ClientConfig customConfig = new ClientConfig("token", "account", "workspace");
        customConfig.setRetries(5);
        
        DataPipelineClient customClient = new DataPipelineClient(customConfig);
        
        assertEquals(5, customClient.getConfig().getRetries());
    }

    @Test
    void testClientWithCustomBaseUrl() {
        ClientConfig customConfig = new ClientConfig("token", "account", "workspace");
        customConfig.setBaseUrl("https://custom-api.example.com");
        
        DataPipelineClient customClient = new DataPipelineClient(customConfig);
        
        assertEquals("https://custom-api.example.com", customClient.getConfig().getBaseUrl());
    }
}
    */