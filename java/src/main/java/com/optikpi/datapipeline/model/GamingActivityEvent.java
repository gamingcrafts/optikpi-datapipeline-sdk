package com.optikpi.datapipeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Gaming Activity Event Model
 * Represents gaming activity events for the Data Pipeline API
 */
public class GamingActivityEvent {
    
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
    private String eventCategory = "Gaming";
    
    @NotBlank(message = "event_name is required")
    @JsonProperty("event_name")
    private String eventName;
    
    @NotBlank(message = "event_id is required")
    @JsonProperty("event_id")
    private String eventId;
    
    @NotBlank(message = "event_time is required")
    @JsonProperty("event_time")
    private String eventTime;
    
    @JsonProperty("game_id")
    private String gameId;
    
    @JsonProperty("game_name")
    private String gameName;
    
    @JsonProperty("game_provider")
    private String gameProvider;
    
    @JsonProperty("game_category")
    private String gameCategory;
    
    @JsonProperty("bet_amount")
    private BigDecimal betAmount;
    
    @JsonProperty("win_amount")
    private BigDecimal winAmount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("device")
    private String device;
    
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("round_id")
    private String roundId;
    
    @JsonProperty("affiliate_id")
    private String affiliateId;
    
    @JsonProperty("partner_id")
    private String partnerId;
    
    @JsonProperty("campaign_code")
    private String campaignCode;
    
    @JsonProperty("bonus_used")
    private BigDecimal bonusUsed;
    
    @JsonProperty("free_spins_used")
    private Integer freeSpinsUsed;
    
    @JsonProperty("jackpot_amount")
    private BigDecimal jackpotAmount;
    
    @JsonProperty("tournament_id")
    private String tournamentId;
    
    @JsonProperty("tournament_name")
    private String tournamentName;
    
    public GamingActivityEvent() {}
    
    public GamingActivityEvent(String accountId, String workspaceId, String userId, String eventName, String eventId, String eventTime) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
    }
    
    /**
     * Validates the gaming activity event data
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
        if (eventCategory != null && !"Gaming".equals(eventCategory)) {
            errors.add("event_category must be \"Gaming\" for gaming activity events");
        }
        
        // Event name validation
        String[] validEventNames = {
            "Game Started", "Game Completed", "Game Abandoned", "Bet Placed",
            "Bet Won", "Bet Lost", "Free Spin Used", "Bonus Triggered",
            "Jackpot Won", "Tournament Joined", "Tournament Completed"
        };
        
        if (eventName != null && !isValidEventName(eventName, validEventNames)) {
            errors.add("event_name must be one of: " + String.join(", ", validEventNames));
        }
        
        // Device validation
        if (device != null && !isValidDevice(device)) {
            errors.add("device must be one of: desktop, mobile, tablet, app");
        }
        
        // Date format validation
        if (eventTime != null && !isValidDateTime(eventTime)) {
            errors.add("event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)");
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
    
    private boolean isValidDevice(String device) {
        return "desktop".equals(device) || "mobile".equals(device) || 
               "tablet".equals(device) || "app".equals(device);
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
    
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    
    public String getGameProvider() { return gameProvider; }
    public void setGameProvider(String gameProvider) { this.gameProvider = gameProvider; }
    
    public String getGameCategory() { return gameCategory; }
    public void setGameCategory(String gameCategory) { this.gameCategory = gameCategory; }
    
    public BigDecimal getBetAmount() { return betAmount; }
    public void setBetAmount(BigDecimal betAmount) { this.betAmount = betAmount; }
    
    public BigDecimal getWinAmount() { return winAmount; }
    public void setWinAmount(BigDecimal winAmount) { this.winAmount = winAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getRoundId() { return roundId; }
    public void setRoundId(String roundId) { this.roundId = roundId; }
    
    public String getAffiliateId() { return affiliateId; }
    public void setAffiliateId(String affiliateId) { this.affiliateId = affiliateId; }
    
    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    
    public String getCampaignCode() { return campaignCode; }
    public void setCampaignCode(String campaignCode) { this.campaignCode = campaignCode; }
    
    public BigDecimal getBonusUsed() { return bonusUsed; }
    public void setBonusUsed(BigDecimal bonusUsed) { this.bonusUsed = bonusUsed; }
    
    public Integer getFreeSpinsUsed() { return freeSpinsUsed; }
    public void setFreeSpinsUsed(Integer freeSpinsUsed) { this.freeSpinsUsed = freeSpinsUsed; }
    
    public BigDecimal getJackpotAmount() { return jackpotAmount; }
    public void setJackpotAmount(BigDecimal jackpotAmount) { this.jackpotAmount = jackpotAmount; }
    
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    
    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
}
