package com.optikpi.datapipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Response for batch operations
 */
public class BatchResponse {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("customers")
    private ApiResponse<Object> customers;
    
    @JsonProperty("accountEvents")
    private ApiResponse<Object> accountEvents;
    
    @JsonProperty("depositEvents")
    private ApiResponse<Object> depositEvents;
    
    @JsonProperty("withdrawEvents")
    private ApiResponse<Object> withdrawEvents;
    
    @JsonProperty("gamingEvents")
    private ApiResponse<Object> gamingEvents;
    
    @JsonProperty("timestamp")
    private Instant timestamp;
    
    public BatchResponse() {}
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public ApiResponse<Object> getCustomers() {
        return customers;
    }
    
    public void setCustomers(ApiResponse<Object> customers) {
        this.customers = customers;
    }
    
    public ApiResponse<Object> getAccountEvents() {
        return accountEvents;
    }
    
    public void setAccountEvents(ApiResponse<Object> accountEvents) {
        this.accountEvents = accountEvents;
    }
    
    public ApiResponse<Object> getDepositEvents() {
        return depositEvents;
    }
    
    public void setDepositEvents(ApiResponse<Object> depositEvents) {
        this.depositEvents = depositEvents;
    }
    
    public ApiResponse<Object> getWithdrawEvents() {
        return withdrawEvents;
    }
    
    public void setWithdrawEvents(ApiResponse<Object> withdrawEvents) {
        this.withdrawEvents = withdrawEvents;
    }
    
    public ApiResponse<Object> getGamingEvents() {
        return gamingEvents;
    }
    
    public void setGamingEvents(ApiResponse<Object> gamingEvents) {
        this.gamingEvents = gamingEvents;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
