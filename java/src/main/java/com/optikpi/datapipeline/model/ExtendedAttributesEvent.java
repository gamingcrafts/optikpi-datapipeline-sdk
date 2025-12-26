package com.optikpi.datapipeline.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtendedAttributesEvent {

    @JsonProperty("workspace_id")
    private String workspaceId;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("list_name")
    private String listName;
    
    @JsonProperty("ext_data")
    private String extData;
    
    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public ExtendedAttributesEvent() {}
    
    // Validation
    public ValidationResult validate() {
        List<String> errors = new ArrayList<>();
        
        if (workspaceId == null || workspaceId.trim().isEmpty()) {
            errors.add("workspace_id is required");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            errors.add("user_id is required");
        }
        
        if (listName == null || listName.trim().isEmpty()) {
            errors.add("list_name is required");
        }
        
        if (extData == null || extData.trim().isEmpty()) {
            errors.add("ext_data is required");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    public String getWorkspaceId() {
        return workspaceId;
    }
    
    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getListName() {
        return listName;
    }
    
    public void setListName(String listName) {
        this.listName = listName;
    }
    
    public String getExtData() {
        return extData;
    }
    
    /**
     * Set ext_data as JSON string (legacy format)
     * @param extData JSON string
     */
    public void setExtDataAsString(String extData) {
        this.extData = extData;
    }
    
    /**
     * Set ext_data as Map (will be auto-converted to JSON string)
     * @param extDataMap Map to convert to JSON string
     */
    public void setExtData(Map<String, String> extDataMap) {
        try {
            this.extData = objectMapper.writeValueAsString(extDataMap);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to convert ext_data map to JSON string", e);
        }
    }
    
    /**
     * Set ext_data directly as string (for backwards compatibility)
     * @param extData JSON string
     */
    public void setExtData(String extData) {
        this.extData = extData;
    }
    
    @Override
    public String toString() {
        return "ExtendedAttributesEvent{" +
                "workspaceId='" + workspaceId + '\'' +
                ", userId='" + userId + '\'' +
                ", listName='" + listName + '\'' +
                ", extData='" + extData + '\'' +
                '}';
    }
}