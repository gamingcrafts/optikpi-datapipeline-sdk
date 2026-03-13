package com.optikpi.datapipeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * System Event Model
 * Represents system-related events
 */
public class SystemEvent {
    
    @JsonProperty("account_id")
    private String accountId;
    
    @JsonProperty("workspace_id")
    private String workspaceId;
    
    @JsonProperty("event_category")
    private String eventCategory = "SystemEvent";
    
    @JsonProperty("event_name")
    private String eventName;
    
    @JsonProperty("event_id")
    private String eventId;
    
    @JsonProperty("event_time")
    private String eventTime;
    
    @JsonProperty("event_data")
    private Object eventData;
    
    public SystemEvent() {}
    
    public SystemEvent(String accountId, String workspaceId, String eventName, String eventId, String eventTime, Object eventData) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.eventData = eventData;
    }
    
    /**
     * Validates the system event data
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
        if (eventCategory == null || eventCategory.trim().isEmpty()) {
            errors.add("event_category is required");
        } else if (!"SystemEvent".equals(eventCategory)) {
            errors.add("event_category must be \"SystemEvent\" for system events");
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
        if (eventData == null) {
            errors.add("event_data is required");
        }
        
        // Date format validation
        if (eventTime != null && !isValidDateTime(eventTime)) {
            errors.add("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)");
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
    
    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
    
    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    
    public Object getEventData() { return eventData; }
    public void setEventData(Object eventData) { this.eventData = eventData; }
}
