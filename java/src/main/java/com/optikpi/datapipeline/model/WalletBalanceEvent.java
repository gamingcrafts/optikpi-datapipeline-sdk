package com.optikpi.datapipeline.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

/**
 * Wallet Balance Event Model
 * Represents wallet balance events for the Data Pipeline API
 */
public class WalletBalanceEvent {
    
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
    private String eventCategory = "Wallet Balance";
    
    @NotBlank(message = "event_name is required")
    @JsonProperty("event_name")
    private String eventName;
    
    @NotBlank(message = "event_id is required")
    @JsonProperty("event_id")
    private String eventId;
    
    @NotBlank(message = "event_time is required")
    @JsonProperty("event_time")
    private String eventTime;
    
    @JsonProperty("wallet_type")
    private String walletType;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("current_cash_balance")
    private BigDecimal currentCashBalance;
    
    @JsonProperty("current_bonus_balance")
    private BigDecimal currentBonusBalance;
    
    @JsonProperty("current_total_balance")
    private BigDecimal currentTotalBalance;
    
    @JsonProperty("blocked_amount")
    private BigDecimal blockedAmount;
    
    public WalletBalanceEvent() {}
    
    public WalletBalanceEvent(String accountId, String workspaceId, String userId, String eventName, String eventId, String eventTime) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
    }
    
    /**
     * Validates the wallet balance event data
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
        
        // Event category validation
        if (eventCategory != null && !"Wallet Balance".equals(eventCategory)) {
            errors.add("event_category must be \"Wallet Balance\" for wallet balance events");
        }
        
        // Date format validation
        if (eventTime != null && !isValidDateTime(eventTime)) {
            errors.add("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)");
        }
        
        // Currency validation
        if (currency != null && !isValidCurrency(currency)) {
            errors.add("currency must be a valid 3-letter ISO currency code");
        }
        
        // Balance validation
        if (currentCashBalance != null && currentCashBalance.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("current_cash_balance must be a non-negative number");
        }
        
        if (currentBonusBalance != null && currentBonusBalance.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("current_bonus_balance must be a non-negative number");
        }
        
        if (currentTotalBalance != null && currentTotalBalance.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("current_total_balance must be a non-negative number");
        }
        
        if (blockedAmount != null && blockedAmount.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("blocked_amount must be a non-negative number");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private boolean isValidDateTime(String dateTime) {
        try {
            Instant.parse(dateTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isValidCurrency(String currency) {
        return currency != null && currency.matches("^[A-Z]{3}$");
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
    
    public String getWalletType() { return walletType; }
    public void setWalletType(String walletType) { this.walletType = walletType; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public BigDecimal getCurrentCashBalance() { return currentCashBalance; }
    public void setCurrentCashBalance(BigDecimal currentCashBalance) { 
        this.currentCashBalance = currentCashBalance; 
    }
    
    public BigDecimal getCurrentBonusBalance() { return currentBonusBalance; }
    public void setCurrentBonusBalance(BigDecimal currentBonusBalance) { 
        this.currentBonusBalance = currentBonusBalance; 
    }
    
    public BigDecimal getCurrentTotalBalance() { return currentTotalBalance; }
    public void setCurrentTotalBalance(BigDecimal currentTotalBalance) { 
        this.currentTotalBalance = currentTotalBalance; 
    }
    
    public BigDecimal getBlockedAmount() { return blockedAmount; }
    public void setBlockedAmount(BigDecimal blockedAmount) { this.blockedAmount = blockedAmount; }
}