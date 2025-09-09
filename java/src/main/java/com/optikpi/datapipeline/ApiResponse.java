package com.optikpi.datapipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Standard API response wrapper
 * @param <T> Type of response data
 */
public class ApiResponse<T> {
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("status")
    private int status;
    
    @JsonProperty("data")
    private T data;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("timestamp")
    private Instant timestamp;
    
    public ApiResponse() {}
    
    public ApiResponse(boolean success, int status, T data, String error, Instant timestamp) {
        this.success = success;
        this.status = status;
        this.data = data;
        this.error = error;
        this.timestamp = timestamp;
    }
    
    public static <T> ApiResponse<T> success(int status, T data, Instant timestamp) {
        return new ApiResponse<>(true, status, data, null, timestamp);
    }
    
    public static <T> ApiResponse<T> error(int status, String error, T data, Instant timestamp) {
        return new ApiResponse<>(false, status, data, error, timestamp);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
