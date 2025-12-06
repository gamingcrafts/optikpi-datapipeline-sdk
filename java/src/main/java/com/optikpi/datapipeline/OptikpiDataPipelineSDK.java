package com.optikpi.datapipeline;

/**
 * Optikpi Data Pipeline API Java SDK
 * 
 * A comprehensive Java SDK for integrating with the Optikpi Data Pipeline API.
 * Provides secure authentication, data validation, and easy-to-use methods for
 * sending customer profiles and event data.
 * 
 * @version 1.0.0
 * @author Optikpi
 * License: MIT
 */
public class OptikpiDataPipelineSDK {
    private final DataPipelineClient client;
    
    public OptikpiDataPipelineSDK(ClientConfig config) {
        this.client = new DataPipelineClient(config);
    }
    
    /**
     * Sends customer profile data
     * @param data Customer profile data or array of profiles
     * @return API response
     */
    public ApiResponse<Object> sendCustomerProfile(Object data) {
        return client.sendCustomerProfile(data);
    }
    
    /**
     * Sends extended attributes data
     * @param data Extended attributes data or array of attributes
     * @return API response
     */
    public ApiResponse<Object> sendExtendedAttributes(Object data) {
        return client.sendExtendedAttributes(data);
    }

    /**
     * Sends account event data
     * @param data Account event data or array of events
     * @return API response
     */

    public ApiResponse<Object> sendAccountEvent(Object data) {
        return client.sendAccountEvent(data);
    }
    
    /**
     * Sends deposit event data
     * @param data Deposit event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendDepositEvent(Object data) {
        return client.sendDepositEvent(data);
    }
    
    /**
     * Sends withdrawal event data
     * @param data Withdrawal event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendWithdrawEvent(Object data) {
        return client.sendWithdrawEvent(data);
    }
    
    /**
     * Sends gaming activity event data
     * @param data Gaming activity event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendGamingActivityEvent(Object data) {
        return client.sendGamingActivityEvent(data);
    }

    /**
     * Sends refer friend event data
     * @param data Refer friend event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendReferFriendEvent(Object data) {
        return client.sendReferFriendEvent(data);
    }

    /**
     * Sends wallet balance event data
     * @param data Wallet balance event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendWalletBalanceEvent(Object data) {
        return client.sendWalletBalanceEvent(data);
    }

    /**
     * Sends multiple events in batch
     * @param batchData Object containing different event types
     * @return Batch response results
     */
    public BatchResponse sendBatch(BatchData batchData) {
        return client.sendBatch(batchData);
    }
    
    /**
     * Updates client configuration
     * @param newConfig New configuration options
     */
    public void updateConfig(ClientConfig newConfig) {
        client.updateConfig(newConfig);
    }
    
    /**
     * Gets current configuration (without sensitive data)
     * @return Current configuration
     */
    public ClientConfig getConfig() {
        return client.getConfig();
    }
    
    /**
     * Creates a new SDK instance with the given configuration
     * @param config Client configuration
     * @return New SDK instance
     */
    public static OptikpiDataPipelineSDK createClient(ClientConfig config) {
        return new OptikpiDataPipelineSDK(config);
    }
    
    /**
     * Creates a new SDK instance with basic configuration
     * @param authToken Authentication token
     * @param accountId Account ID
     * @param workspaceId Workspace ID
     * @return New SDK instance
     */
    public static OptikpiDataPipelineSDK createClient(String authToken, String accountId, String workspaceId) {
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        return new OptikpiDataPipelineSDK(config);
    }
}
