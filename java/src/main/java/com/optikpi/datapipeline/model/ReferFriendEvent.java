package com.optikpi.datapipeline.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

/**
 * Refer Friend Event Model
 * Represents refer friend events for the Data Pipeline API
 */
public class ReferFriendEvent {
    
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
    private String eventCategory = "Refer Friend";
    
    @NotBlank(message = "event_name is required")
    @JsonProperty("event_name")
    private String eventName;
    
    @NotBlank(message = "event_id is required")
    @JsonProperty("event_id")
    private String eventId;
    
    @NotBlank(message = "event_time is required")
    @JsonProperty("event_time")
    private String eventTime;
    
    @JsonProperty("referral_code_used")
    private String referralCodeUsed;
    
    @JsonProperty("successful_referral_confirmation")
    private Boolean successfulReferralConfirmation;
    
    @JsonProperty("reward_type")
    private String rewardType;
    
    @JsonProperty("reward_claimed_status")
    private String rewardClaimedStatus;
    
    @JsonProperty("referee_user_id")
    private String refereeUserId;
    
    @JsonProperty("referee_registration_date")
    private String refereeRegistrationDate;
    
    @JsonProperty("referee_first_deposit")
    private Double refereeFirstDeposit;
    
    public ReferFriendEvent() {}
    
    public ReferFriendEvent(String accountId, String workspaceId, String userId, String eventName, String eventId, String eventTime) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
    }
    
    /**
     * Validates the refer friend event data
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
        if (eventCategory != null && !"Refer Friend".equals(eventCategory)) {
            errors.add("event_category must be \"Refer Friend\" for refer friend events");
        }
        
        // Date format validation
        if (eventTime != null && !isValidDateTime(eventTime)) {
            errors.add("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)");
        }
        
        if (refereeRegistrationDate != null && !isValidDateTime(refereeRegistrationDate)) {
            errors.add("referee_registration_date must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)");
        }
        
        // Reward type validation
        if (rewardType != null && !isValidRewardType(rewardType)) {
            errors.add("reward_type must be one of: bonus, cash, points, free_spins, other");
        }
        
        // Reward claimed status validation
        if (rewardClaimedStatus != null && !isValidClaimedStatus(rewardClaimedStatus)) {
            errors.add("reward_claimed_status must be one of: pending, claimed, expired, cancelled");
        }
        
        // First deposit validation
        if (refereeFirstDeposit != null && refereeFirstDeposit < 0) {
            errors.add("referee_first_deposit must be a non-negative number");
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
    
    private boolean isValidRewardType(String rewardType) {
        return "bonus".equals(rewardType) || "cash".equals(rewardType) || 
               "points".equals(rewardType) || "free_spins".equals(rewardType) || 
               "other".equals(rewardType);
    }
    
    private boolean isValidClaimedStatus(String status) {
        return "pending".equals(status) || "claimed".equals(status) || 
               "expired".equals(status) || "cancelled".equals(status);
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
    
    public String getReferralCodeUsed() { return referralCodeUsed; }
    public void setReferralCodeUsed(String referralCodeUsed) { this.referralCodeUsed = referralCodeUsed; }
    
    public Boolean getSuccessfulReferralConfirmation() { return successfulReferralConfirmation; }
    public void setSuccessfulReferralConfirmation(Boolean successfulReferralConfirmation) { 
        this.successfulReferralConfirmation = successfulReferralConfirmation; 
    }
    
    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }
    
    public String getRewardClaimedStatus() { return rewardClaimedStatus; }
    public void setRewardClaimedStatus(String rewardClaimedStatus) { this.rewardClaimedStatus = rewardClaimedStatus; }
    
    public String getRefereeUserId() { return refereeUserId; }
    public void setRefereeUserId(String refereeUserId) { this.refereeUserId = refereeUserId; }
    
    public String getRefereeRegistrationDate() { return refereeRegistrationDate; }
    public void setRefereeRegistrationDate(String refereeRegistrationDate) { 
        this.refereeRegistrationDate = refereeRegistrationDate; 
    }
    
    public Double getRefereeFirstDeposit() { return refereeFirstDeposit; }
    public void setRefereeFirstDeposit(Double refereeFirstDeposit) { 
        this.refereeFirstDeposit = refereeFirstDeposit; 
    }
}