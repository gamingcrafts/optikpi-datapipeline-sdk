package com.optikpi.datapipeline;

/**
 * Configuration class for DataPipelineClient
 */
public class ClientConfig {
    private String baseUrl;
    private String authToken;
    private String accountId;
    private String workspaceId;
    private long timeout = 30000;
    private int retries = 3;
    private long retryDelay = 1000;
    
    public ClientConfig() {}
    
    public ClientConfig(String authToken, String accountId, String workspaceId) {
        this.authToken = authToken;
        this.accountId = accountId;
        this.workspaceId = workspaceId;
    }
    
    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getWorkspaceId() {
        return workspaceId;
    }
    
    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public int getRetries() {
        return retries;
    }
    
    public void setRetries(int retries) {
        this.retries = retries;
    }
    
    public long getRetryDelay() {
        return retryDelay;
    }
    
    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }
    
    /**
     * Updates this config with values from another config
     * @param other Other config to merge from
     */
    public void updateFrom(ClientConfig other) {
        if (other.baseUrl != null) this.baseUrl = other.baseUrl;
        if (other.authToken != null) this.authToken = other.authToken;
        if (other.accountId != null) this.accountId = other.accountId;
        if (other.workspaceId != null) this.workspaceId = other.workspaceId;
        this.timeout = other.timeout;
        this.retries = other.retries;
        this.retryDelay = other.retryDelay;
    }
    
    /**
     * Creates a copy of this config with masked sensitive data
     * @return Copy of config with masked auth token
     */
    public ClientConfig copy() {
        ClientConfig copy = new ClientConfig();
        copy.baseUrl = this.baseUrl;
        copy.authToken = this.authToken != null ? maskToken(this.authToken) : null;
        copy.accountId = this.accountId;
        copy.workspaceId = this.workspaceId;
        copy.timeout = this.timeout;
        copy.retries = this.retries;
        copy.retryDelay = this.retryDelay;
        return copy;
    }
    
    private String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "***";
        }
        return token.substring(0, 8) + "...";
    }
}
