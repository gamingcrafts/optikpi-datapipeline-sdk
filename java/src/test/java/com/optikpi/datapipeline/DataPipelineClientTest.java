package com.optikpi.datapipeline;

import com.optikpi.datapipeline.model.CustomerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataPipelineClientTest {

    private ClientConfig config;
    private DataPipelineClient client;

    @BeforeEach
    void setUp() {
        config = new ClientConfig("test_token", "test_account", "test_workspace");
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
    void testGetConfigReturnsCopy() {
        ClientConfig configCopy = client.getConfig();
        
        // Modify the copy
        configCopy.setTimeout(99999);
        
        // Original config should not be affected
        assertNotEquals(99999, client.getConfig().getTimeout());
    }
}
