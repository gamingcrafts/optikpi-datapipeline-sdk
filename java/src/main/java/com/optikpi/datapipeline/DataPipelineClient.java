package com.optikpi.datapipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.optikpi.datapipeline.crypto.CryptoUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Data Pipeline API Client
 * Main client class for interacting with the Optikpi Data Pipeline API
 */
public class DataPipelineClient {
    private static final Logger logger = LoggerFactory.getLogger(DataPipelineClient.class);
    
    private final ClientConfig config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public DataPipelineClient(ClientConfig config) {
        this.config = validateConfig(config);
        this.objectMapper = createObjectMapper();
        this.httpClient = createHttpClient();
    }
    
    private ClientConfig validateConfig(ClientConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Client configuration cannot be null");
        }
        if (config.getAuthToken() == null || config.getAuthToken().trim().isEmpty()) {
            throw new IllegalArgumentException("authToken is required");
        }
        if (config.getAccountId() == null || config.getAccountId().trim().isEmpty()) {
            throw new IllegalArgumentException("accountId is required");
        }
        if (config.getWorkspaceId() == null || config.getWorkspaceId().trim().isEmpty()) {
            throw new IllegalArgumentException("workspaceId is required");
        }
        return config;
    }
    
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(config.getTimeout()))
                .readTimeout(Duration.ofMillis(config.getTimeout()))
                .writeTimeout(Duration.ofMillis(config.getTimeout()))
                .addInterceptor(this::addAuthHeaders)
                .addInterceptor(this::handleRetries)
                .build();
    }
    
    private Response addAuthHeaders(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        if (originalRequest.body() != null && !"GET".equals(originalRequest.method())) {
            try {
                // Buffer the request body for HMAC signing
                RequestBody originalBody = originalRequest.body();
                String requestBody = getRequestBody(originalBody);
                
                String hmacSignature = CryptoUtils.generateHmacSignature(
                    requestBody,
                    config.getAuthToken(),
                    config.getAccountId(),
                    config.getWorkspaceId()
                );
                
                // Create new request body with the same content
                RequestBody newBody = RequestBody.create(
                    requestBody.getBytes(),
                    originalBody.contentType()
                );
                
                Request newRequest = originalRequest.newBuilder()
                    .method(originalRequest.method(), newBody)
                    .addHeader("x-optikpi-token", config.getAuthToken())
                    .addHeader("x-optikpi-account-id", config.getAccountId())
                    .addHeader("x-optikpi-workspace-id", config.getWorkspaceId())
                    .addHeader("x-hmac-signature", hmacSignature)
                    .addHeader("x-hmac-algorithm", "sha256")
                    .build();
                
                return chain.proceed(newRequest);
            } catch (Exception e) {
                logger.error("Failed to add authentication headers", e);
                throw new IOException("Failed to add authentication headers", e);
            }
        }
        
        return chain.proceed(originalRequest);
    }
    
    private Response handleRetries(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        IOException lastException = null;
        
        for (int attempt = 0; attempt <= config.getRetries(); attempt++) {
            try {
                if (response != null) {
                    response.close();
                }
                
                if (attempt > 0) {
                    try {
                        Thread.sleep(config.getRetryDelay() * attempt);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Retry interrupted", e);
                    }
                }
                
                response = chain.proceed(request);
                
                if (response.isSuccessful() || response.code() < 500) {
                    return response;
                }
                
                if (attempt == config.getRetries()) {
                    return response;
                }
                
            } catch (IOException e) {
                lastException = e;
                if (attempt == config.getRetries()) {
                    throw e;
                }
            }
        }
        
        if (lastException != null) {
            throw lastException;
        }
        
        return response;
    }
    
    private String getRequestBody(RequestBody body) throws IOException {
        if (body == null) {
            return "";
        }
        
        if (body instanceof FormBody) {
            FormBody formBody = (FormBody) body;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < formBody.size(); i++) {
                if (i > 0) sb.append("&");
                sb.append(formBody.encodedName(i)).append("=").append(formBody.encodedValue(i));
            }
            return sb.toString();
        } else {
            // For JSON bodies, we need to buffer them
            try (okio.Buffer buffer = new okio.Buffer()) {
                body.writeTo(buffer);
                return buffer.readUtf8();
            }
        }
    }
    
    /**
     * Performs a health check on the API
     * @return Health check response
     */
    public ApiResponse<Object> healthCheck() {
        try {
            Request request = new Request.Builder()
                    .url(config.getBaseUrl() + "/datapipeline/health")
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (response.isSuccessful()) {
                    Object data = objectMapper.readValue(responseBody, Object.class);
                    return ApiResponse.success(response.code(), data, Instant.now());
                } else {
                    return ApiResponse.error(response.code(), "Health check failed", responseBody, Instant.now());
                }
            }
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return ApiResponse.error(0, e.getMessage(), null, Instant.now());
        }
    }
    
    /**
     * Sends customer profile data
     * @param data Customer profile data or array of profiles
     * @return API response
     */
    public ApiResponse<Object> sendCustomerProfile(Object data) {
        return sendData("/customers", data);
    }
    
    /**
     * Sends account event data
     * @param data Account event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendAccountEvent(Object data) {
        return sendData("/events/account", data);
    }
    
    /**
     * Sends deposit event data
     * @param data Deposit event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendDepositEvent(Object data) {
        return sendData("/events/deposit", data);
    }
    
    /**
     * Sends withdrawal event data
     * @param data Withdrawal event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendWithdrawEvent(Object data) {
        return sendData("/events/withdraw", data);
    }
    
    /**
     * Sends gaming activity event data
     * @param data Gaming activity event data or array of events
     * @return API response
     */
    public ApiResponse<Object> sendGamingActivityEvent(Object data) {
        return sendData("/events/gaming-activity", data);
    }
    
    /**
     * Sends multiple events in batch
     * @param batchData Object containing different event types
     * @return Batch response results
     */
    public BatchResponse sendBatch(BatchData batchData) {
        BatchResponse results = new BatchResponse();
        results.setSuccess(true);
        results.setTimestamp(Instant.now());
        
        if (batchData.getCustomers() != null) {
            results.setCustomers(sendCustomerProfile(batchData.getCustomers()));
        }
        
        if (batchData.getAccountEvents() != null) {
            results.setAccountEvents(sendAccountEvent(batchData.getAccountEvents()));
        }
        
        if (batchData.getDepositEvents() != null) {
            results.setDepositEvents(sendDepositEvent(batchData.getDepositEvents()));
        }
        
        if (batchData.getWithdrawEvents() != null) {
            results.setWithdrawEvents(sendWithdrawEvent(batchData.getWithdrawEvents()));
        }
        
        if (batchData.getGamingEvents() != null) {
            results.setGamingEvents(sendGamingActivityEvent(batchData.getGamingEvents()));
        }
        
        return results;
    }
    
    private ApiResponse<Object> sendData(String endpoint, Object data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
            
            Request request = new Request.Builder()
                    .url(config.getBaseUrl() + endpoint)
                    .post(body)
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (response.isSuccessful()) {
                    Object responseData = objectMapper.readValue(responseBody, Object.class);
                    return ApiResponse.success(response.code(), responseData, Instant.now());
                } else {
                    return ApiResponse.error(response.code(), "Request failed", responseBody, Instant.now());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to send data to " + endpoint, e);
            return ApiResponse.error(0, e.getMessage(), null, Instant.now());
        }
    }
    
    /**
     * Updates client configuration
     * @param newConfig New configuration options
     */
    public void updateConfig(ClientConfig newConfig) {
        this.config.updateFrom(newConfig);
        validateConfig(this.config);
    }
    
    /**
     * Gets current configuration (without sensitive data)
     * @return Current configuration
     */
    public ClientConfig getConfig() {
        ClientConfig copy = new ClientConfig();
        copy.updateFrom(config);
        return copy;
    }
    
    /**
     * Returns a copy of the config with masked sensitive data for logging
     * @return Masked config copy
     */
    public ClientConfig getConfigForLogging() {
        return config.copy();
    }
}
