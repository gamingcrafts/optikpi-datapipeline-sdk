package com.optikpi.datapipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import com.optikpi.datapipeline.model.CustomerProfile;

/**
 * Integration tests for the Data Pipeline SDK
 * These tests require actual API credentials to be set as environment variables
 */
@EnabledIfEnvironmentVariable(named = "OPTIKPI_AUTH_TOKEN", matches = ".*")
class IntegrationTest {

    @Test
    void testHealthCheck() {
        // This test will only run if OPTIKPI_AUTH_TOKEN is set
        String authToken = System.getenv("OPTIKPI_AUTH_TOKEN");
        String accountId = System.getenv("OPTIKPI_ACCOUNT_ID");
        String workspaceId = System.getenv("OPTIKPI_WORKSPACE_ID");
        
        if (authToken == null || accountId == null || workspaceId == null) {
            // Skip test if credentials are not available
            return;
        }
        
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        var response = sdk.healthCheck();
        
        // We don't assert success here as the API might not be available in test environment
        // Just verify the response structure
        assertNotNull(response);
        assertNotNull(response.getTimestamp());
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
    }

    @Test
    void testSdkCreation() {
        ClientConfig config = new ClientConfig("test_token", "test_account", "test_workspace");
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);
        
        assertNotNull(sdk);
        assertNotNull(sdk.getConfig());
        assertEquals("test_account", sdk.getConfig().getAccountId());
    }
}
