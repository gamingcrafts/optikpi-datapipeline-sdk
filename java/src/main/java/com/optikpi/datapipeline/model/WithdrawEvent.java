package com.optikpi.datapipeline.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Withdrawal Event Model
 * Represents withdrawal-related events for the Data Pipeline API
 */
public class WithdrawEvent {
    
    @NotBlank(message = "account_id is required")
    @JsonProperty("account_id")
    private String accountId;
    
    @NotBlank(message = "workspace_id is required")
    @JsonProperty("workspace_id")
    private String workspaceId;
    
    @NotBlank(message = "user_id is required")
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("event_category")
    private String eventCategory = "Withdraw";
    
    @NotBlank(message = "event_name is required")
    @JsonProperty("event_name")
    private String eventName;
    
    @NotBlank(message = "event_id is required")
    @JsonProperty("event_id")
    private String eventId;
    
    @NotBlank(message = "event_time is required")
    @JsonProperty("event_time")
    private String eventTime;
    
    @Positive(message = "amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @NotBlank(message = "payment_method is required")
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @NotBlank(message = "transaction_id is required")
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("failure_reason")
    private String failureReason;
    
    public WithdrawEvent() {}
    
    public WithdrawEvent(String accountId, String workspaceId, String userId, String eventName, String eventId, String eventTime, BigDecimal amount) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.amount = amount;
    }
    
    /**
     * Validates the withdrawal event data
     * @return Validation result with isValid boolean and errors list
     */
    public ValidationResult validate() {
        List<String> errors = new ArrayList<>();
        
        // Required fields validation
        if (accountId == null || accountId.trim().isEmpty()) {
            errors.add("account_id is required");
        }
        if (workspaceId == null || workspaceId.trim().isEmpty()) {
            errors.add("workspace_id is required");
        }
        if (userId == null || userId.trim().isEmpty()) {
            errors.add("user_id is required");
        }
        if (eventName == null || eventName.trim().isEmpty()) {
            errors.add("event_name is required");
        }
        if (eventId == null || eventId.trim().isEmpty()) {
            errors.add("event_id is required");
        }
        if (eventTime == null || eventTime.trim().isEmpty()) {
            errors.add("event_time is required");
        }
        if (amount == null) {
            errors.add("amount is required");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            errors.add("payment_method is required");
        }
        if (transactionId == null || transactionId.trim().isEmpty()) {
            errors.add("transaction_id is required");
        }
        
        // Event category validation
        if (eventCategory != null && !"Withdraw".equals(eventCategory)) {
            errors.add("event_category must be \"Withdraw\" for withdrawal events");
        }
        
        // Event name validation - CORRECTED to match JavaScript
        String[] validEventNames = {
            "Successful Withdrawal",
            "Failed Withdrawal",
            "Pending Withdrawal",
            "Withdrawal Cancelled",
            "Withdrawal Rejected"
        };
        
        if (eventName != null && !isValidEventName(eventName, validEventNames)) {
            errors.add("event_name must be one of: " + String.join(", ", validEventNames));
        }
        
        // Payment method validation - ADDED to match JavaScript
        if (paymentMethod != null && !isValidPaymentMethod(paymentMethod)) {
            errors.add("payment_method must be one of: bank, credit_card, debit_card, e_wallet, crypto, paypal, skrill, neteller");
        }
        
        // Date format validation
        if (eventTime != null && !isValidDateTime(eventTime)) {
            errors.add("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)");
        }
        
        // Amount validation
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("amount must be positive");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private boolean isValidEventName(String eventName, String[] validNames) {
        for (String validName : validNames) {
            if (validName.equals(eventName)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidStatus(String status) {
        return "success".equals(status) || "pending".equals(status) || 
               "failed".equals(status) || "cancelled".equals(status) || "rejected".equals(status);
    }
    
    private boolean isValidDevice(String device) {
        return "desktop".equals(device) || "mobile".equals(device) || 
               "tablet".equals(device) || "app".equals(device);
    }
    
    private boolean isValidPaymentMethod(String method) {
        return "bank".equals(method) || "credit_card".equals(method) || 
               "debit_card".equals(method) || "e_wallet".equals(method) ||
               "crypto".equals(method) || "paypal".equals(method) ||
               "skrill".equals(method) || "neteller".equals(method);
    }
    
    private boolean isValidDateTime(String dateTime) {
        try {
            Instant.parse(dateTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}