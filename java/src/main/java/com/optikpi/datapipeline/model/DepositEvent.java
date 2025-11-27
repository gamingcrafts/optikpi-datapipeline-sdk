package com.optikpi.datapipeline.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Deposit Event Model
 * Represents deposit-related events for the Data Pipeline API
 */
public class DepositEvent {
    
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
    private String eventCategory = "Deposit";
    
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
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @Pattern(regexp = "success|pending|failed|cancelled|refunded", message = "status must be one of: success, pending, failed, cancelled, refunded")
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("device")
    private String device;
    
    @JsonProperty("affiliate_id")
    private String affiliateId;
    
    @JsonProperty("partner_id")
    private String partnerId;
    
    @JsonProperty("campaign_code")
    private String campaignCode;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("bonus_amount")
    private BigDecimal bonusAmount;
    
    @JsonProperty("fee_amount")
    private BigDecimal feeAmount;
    
    @JsonProperty("payment_provider_id")
    private String paymentProviderId;
    
    @JsonProperty("payment_provider_name")
    private String paymentProviderName;
    
    @JsonProperty("fees")
    private BigDecimal fees;
    
    @JsonProperty("net_amount")
    private BigDecimal netAmount;
    
    public DepositEvent() {}
    
    public DepositEvent(String accountId, String workspaceId, String userId, String eventName, String eventId, String eventTime, BigDecimal amount) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.amount = amount;
    }
    
    /**
     * Validates the deposit event data
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
        if (eventCategory != null && !"Deposit".equals(eventCategory)) {
            errors.add("event_category must be \"Deposit\" for deposit events");
        }
        
        // Event name validation - CORRECTED to match JavaScript/API
        String[] validEventNames = {
            "Successful Deposit",
            "Failed Deposit",
            "Pending Deposit",
            "Deposit Cancelled",
            "Deposit Refunded"
        };
        
        if (eventName != null && !isValidEventName(eventName, validEventNames)) {
            errors.add("event_name must be one of: " + String.join(", ", validEventNames));
        }
        
        // Status validation - CORRECTED to match JavaScript
        if (status != null && !isValidStatus(status)) {
            errors.add("status must be one of: success, pending, failed, cancelled, refunded");
        }
        
        // Device validation
        if (device != null && !isValidDevice(device)) {
            errors.add("device must be one of: desktop, mobile, tablet, app");
        }
        
        // Payment method validation
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
               "failed".equals(status) || "cancelled".equals(status) || "refunded".equals(status);
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
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
    
    public String getAffiliateId() { return affiliateId; }
    public void setAffiliateId(String affiliateId) { this.affiliateId = affiliateId; }
    
    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    
    public String getCampaignCode() { return campaignCode; }
    public void setCampaignCode(String campaignCode) { this.campaignCode = campaignCode; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }
    
    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    
    public String getPaymentProviderId() { return paymentProviderId; }
    public void setPaymentProviderId(String paymentProviderId) { this.paymentProviderId = paymentProviderId; }
    
    public String getPaymentProviderName() { return paymentProviderName; }
    public void setPaymentProviderName(String paymentProviderName) { this.paymentProviderName = paymentProviderName; }
    
    public BigDecimal getFees() { return fees; }
    public void setFees(BigDecimal fees) { this.fees = fees; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
}