package com.optikpi.datapipeline.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

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
    private String eventCategory = "Gaming Activity";
    
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
    
    @JsonProperty("game_title")
    private String gameTitle;
    
    @JsonProperty("game_provider")
    private String gameProvider;
    
    @JsonProperty("game_category")
    private String gameCategory;
    
    @JsonProperty("bet_amount")
    private BigDecimal betAmount;
    
    @JsonProperty("wager_amount")
    private BigDecimal wagerAmount;

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
    
    @JsonProperty("loss_amount")
    private BigDecimal lossAmount;
    
    @JsonProperty("bonus_id")
    private String bonusId;
    
    @JsonProperty("free_spin_id")
    private String freeSpinId;
    
    @JsonProperty("num_spins_played")
    private Integer numSpinsPlayed;
    
    @JsonProperty("game_theme")
    private String gameTheme;
    
    @JsonProperty("remaining_spins")
    private Integer remainingSpins;
    
    @JsonProperty("bet_value_per_spin")
    private BigDecimal betValuePerSpin;
    
    @JsonProperty("wagering_requirements_met")
    private Boolean wageringRequirementsMet;
    
    @JsonProperty("free_spin_expiry_date")
    private String freeSpinExpiryDate;
    
    @JsonProperty("campaign_id")
    private String campaignId;
    
    @JsonProperty("campaign_name")
    private String campaignName;
    
    @JsonProperty("rtp")
    private BigDecimal rtp;
    
    @JsonProperty("winning_bet_amount")
    private BigDecimal winningBetAmount;
    
    @JsonProperty("jackpot_type")
    private String jackpotType;
    
    @JsonProperty("volatility")
    private String volatility;
    
    @JsonProperty("min_bet")
    private BigDecimal minBet;
    
    @JsonProperty("max_bet")
    private BigDecimal maxBet;
    
    @JsonProperty("number_of_reels")
    private Integer numberOfReels;
    
    @JsonProperty("number_of_paylines")
    private Integer numberOfPaylines;
    
    @JsonProperty("feature_types")
    private String featureTypes;
    
    @JsonProperty("game_release_date")
    private String gameReleaseDate;
    
    @JsonProperty("live_dealer_availability")
    private Boolean liveDealerAvailability;
    
    @JsonProperty("side_bets_availability")
    private Boolean sideBetsAvailability;
    
    @JsonProperty("multiplayer_option")
    private Boolean multiplayerOption;
    
    @JsonProperty("auto_play")
    private Boolean autoPlay;
    
    @JsonProperty("poker_variant")
    private String pokerVariant;
    
    @JsonProperty("buy_in_amount")
    private BigDecimal buyInAmount;
    
    @JsonProperty("table_type")
    private String tableType;
    
    @JsonProperty("stakes_level")
    private String stakesLevel;
    
    @JsonProperty("number_of_players")
    private Integer numberOfPlayers;
    
    @JsonProperty("game_duration")
    private Integer gameDuration;
    
    @JsonProperty("hand_volume")
    private Integer handVolume;
    
    @JsonProperty("player_position")
    private String playerPosition;
    
    @JsonProperty("final_hand")
    private String finalHand;
    
    @JsonProperty("rake_contribution")
    private BigDecimal rakeContribution;
    
    @JsonProperty("multi_tabling_indicator")
    private Boolean multiTablingIndicator;
    
    @JsonProperty("session_result")
    private String sessionResult;
    
    @JsonProperty("vip_status")
    private String vipStatus;
    
    @JsonProperty("blind_level")
    private String blindLevel;
    
    @JsonProperty("rebuy_and_addon_info")
    private String rebuyAndAddonInfo;
    
    @JsonProperty("sport_type")
    private String sportType;
    
    @JsonProperty("betting_market")
    private String bettingMarket;
    
    @JsonProperty("odds")
    private BigDecimal odds;
    
    @JsonProperty("live_betting_availability")
    private Boolean liveBettingAvailability;
    
    @JsonProperty("result")
    private String result;
    
    @JsonProperty("bet_status")
    private String betStatus;
    
    @JsonProperty("betting_channel")
    private String bettingChannel;
    
    @JsonProperty("bonus_type")
    private String bonusType;
    
    @JsonProperty("bonus_amount")
    private BigDecimal bonusAmount;
    
    @JsonProperty("free_spin_start_date")
    private String freeSpinStartDate;
    
    @JsonProperty("num_spins_awarded")
    private Integer numSpinsAwarded;
    
    @JsonProperty("bonus_code")
    private String bonusCode;
    
    @JsonProperty("parent_game_category")
    private String parentGameCategory;
    
    @JsonProperty("money_type")
    private String moneyType;
    
    @JsonProperty("transaction_type")
    private String transactionType;
    
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
        
        // Event category validation - CORRECTED
        if (eventCategory != null && !"Gaming Activity".equals(eventCategory)) {
            errors.add("event_category must be \"Gaming Activity\" for gaming activity events");
        }
        
        // Event name validation - CORRECTED to match API
        String[] validEventNames = {
            "Play Casino Game",
            "Play Slot Game", 
            "Play Table Game",
            "Play Live Casino Game",
            "Bet Placed",
            "Bet Won",
            "Bet Lost",
            "Game Session Started",
            "Game Session Ended",
            "Bonus Game Triggered",
            "Free Spins Triggered",
            "Jackpot Won"
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
    
    public String getGameTitle() { return gameTitle; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
    
    public String getGameProvider() { return gameProvider; }
    public void setGameProvider(String gameProvider) { this.gameProvider = gameProvider; }
    
    public String getGameCategory() { return gameCategory; }
    public void setGameCategory(String gameCategory) { this.gameCategory = gameCategory; }
    
    public BigDecimal getBetAmount() { return betAmount; }
    public void setBetAmount(BigDecimal betAmount) { this.betAmount = betAmount; }

    public BigDecimal getWagerAmount() { return wagerAmount; }
    public void setWagerAmount(BigDecimal wagerAmount) { this.wagerAmount = wagerAmount; }
    
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
    
    public BigDecimal getLossAmount() { return lossAmount; }
    public void setLossAmount(BigDecimal lossAmount) { this.lossAmount = lossAmount; }
    
    public String getBonusId() { return bonusId; }
    public void setBonusId(String bonusId) { this.bonusId = bonusId; }
    
    public String getFreeSpinId() { return freeSpinId; }
    public void setFreeSpinId(String freeSpinId) { this.freeSpinId = freeSpinId; }
    
    public Integer getNumSpinsPlayed() { return numSpinsPlayed; }
    public void setNumSpinsPlayed(Integer numSpinsPlayed) { this.numSpinsPlayed = numSpinsPlayed; }
    
    public String getGameTheme() { return gameTheme; }
    public void setGameTheme(String gameTheme) { this.gameTheme = gameTheme; }
    
    public Integer getRemainingSpins() { return remainingSpins; }
    public void setRemainingSpins(Integer remainingSpins) { this.remainingSpins = remainingSpins; }
    
    public BigDecimal getBetValuePerSpin() { return betValuePerSpin; }
    public void setBetValuePerSpin(BigDecimal betValuePerSpin) { this.betValuePerSpin = betValuePerSpin; }
    
    public Boolean getWageringRequirementsMet() { return wageringRequirementsMet; }
    public void setWageringRequirementsMet(Boolean wageringRequirementsMet) { this.wageringRequirementsMet = wageringRequirementsMet; }
    
    public String getFreeSpinExpiryDate() { return freeSpinExpiryDate; }
    public void setFreeSpinExpiryDate(String freeSpinExpiryDate) { this.freeSpinExpiryDate = freeSpinExpiryDate; }
    
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    
    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }
    
    public BigDecimal getRtp() { return rtp; }
    public void setRtp(BigDecimal rtp) { this.rtp = rtp; }
    
    public BigDecimal getWinningBetAmount() { return winningBetAmount; }
    public void setWinningBetAmount(BigDecimal winningBetAmount) { this.winningBetAmount = winningBetAmount; }
    
    public String getJackpotType() { return jackpotType; }
    public void setJackpotType(String jackpotType) { this.jackpotType = jackpotType; }
    
    public String getVolatility() { return volatility; }
    public void setVolatility(String volatility) { this.volatility = volatility; }
    
    public BigDecimal getMinBet() { return minBet; }
    public void setMinBet(BigDecimal minBet) { this.minBet = minBet; }
    
    public BigDecimal getMaxBet() { return maxBet; }
    public void setMaxBet(BigDecimal maxBet) { this.maxBet = maxBet; }
    
    public Integer getNumberOfReels() { return numberOfReels; }
    public void setNumberOfReels(Integer numberOfReels) { this.numberOfReels = numberOfReels; }
    
    public Integer getNumberOfPaylines() { return numberOfPaylines; }
    public void setNumberOfPaylines(Integer numberOfPaylines) { this.numberOfPaylines = numberOfPaylines; }
    
    public String getFeatureTypes() { return featureTypes; }
    public void setFeatureTypes(String featureTypes) { this.featureTypes = featureTypes; }
    
    public String getGameReleaseDate() { return gameReleaseDate; }
    public void setGameReleaseDate(String gameReleaseDate) { this.gameReleaseDate = gameReleaseDate; }
    
    public Boolean getLiveDealerAvailability() { return liveDealerAvailability; }
    public void setLiveDealerAvailability(Boolean liveDealerAvailability) { this.liveDealerAvailability = liveDealerAvailability; }
    
    public Boolean getSideBetsAvailability() { return sideBetsAvailability; }
    public void setSideBetsAvailability(Boolean sideBetsAvailability) { this.sideBetsAvailability = sideBetsAvailability; }
    
    public Boolean getMultiplayerOption() { return multiplayerOption; }
    public void setMultiplayerOption(Boolean multiplayerOption) { this.multiplayerOption = multiplayerOption; }
    
    public Boolean getAutoPlay() { return autoPlay; }
    public void setAutoPlay(Boolean autoPlay) { this.autoPlay = autoPlay; }
    
    public String getPokerVariant() { return pokerVariant; }
    public void setPokerVariant(String pokerVariant) { this.pokerVariant = pokerVariant; }
    
    public BigDecimal getBuyInAmount() { return buyInAmount; }
    public void setBuyInAmount(BigDecimal buyInAmount) { this.buyInAmount = buyInAmount; }
    
    public String getTableType() { return tableType; }
    public void setTableType(String tableType) { this.tableType = tableType; }
    
    public String getStakesLevel() { return stakesLevel; }
    public void setStakesLevel(String stakesLevel) { this.stakesLevel = stakesLevel; }
    
    public Integer getNumberOfPlayers() { return numberOfPlayers; }
    public void setNumberOfPlayers(Integer numberOfPlayers) { this.numberOfPlayers = numberOfPlayers; }
    
    public Integer getGameDuration() { return gameDuration; }
    public void setGameDuration(Integer gameDuration) { this.gameDuration = gameDuration; }
    
    public Integer getHandVolume() { return handVolume; }
    public void setHandVolume(Integer handVolume) { this.handVolume = handVolume; }
    
    public String getPlayerPosition() { return playerPosition; }
    public void setPlayerPosition(String playerPosition) { this.playerPosition = playerPosition; }
    
    public String getFinalHand() { return finalHand; }
    public void setFinalHand(String finalHand) { this.finalHand = finalHand; }
    
    public BigDecimal getRakeContribution() { return rakeContribution; }
    public void setRakeContribution(BigDecimal rakeContribution) { this.rakeContribution = rakeContribution; }
    
    public Boolean getMultiTablingIndicator() { return multiTablingIndicator; }
    public void setMultiTablingIndicator(Boolean multiTablingIndicator) { this.multiTablingIndicator = multiTablingIndicator; }
    
    public String getSessionResult() { return sessionResult; }
    public void setSessionResult(String sessionResult) { this.sessionResult = sessionResult; }
    
    public String getVipStatus() { return vipStatus; }
    public void setVipStatus(String vipStatus) { this.vipStatus = vipStatus; }
    
    public String getBlindLevel() { return blindLevel; }
    public void setBlindLevel(String blindLevel) { this.blindLevel = blindLevel; }
    
    public String getRebuyAndAddonInfo() { return rebuyAndAddonInfo; }
    public void setRebuyAndAddonInfo(String rebuyAndAddonInfo) { this.rebuyAndAddonInfo = rebuyAndAddonInfo; }
    
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    
    public String getBettingMarket() { return bettingMarket; }
    public void setBettingMarket(String bettingMarket) { this.bettingMarket = bettingMarket; }
    
    public BigDecimal getOdds() { return odds; }
    public void setOdds(BigDecimal odds) { this.odds = odds; }
    
    public Boolean getLiveBettingAvailability() { return liveBettingAvailability; }
    public void setLiveBettingAvailability(Boolean liveBettingAvailability) { this.liveBettingAvailability = liveBettingAvailability; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public String getBetStatus() { return betStatus; }
    public void setBetStatus(String betStatus) { this.betStatus = betStatus; }
    
    public String getBettingChannel() { return bettingChannel; }
    public void setBettingChannel(String bettingChannel) { this.bettingChannel = bettingChannel; }
    
    public String getBonusType() { return bonusType; }
    public void setBonusType(String bonusType) { this.bonusType = bonusType; }
    
    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }
    
    public String getFreeSpinStartDate() { return freeSpinStartDate; }
    public void setFreeSpinStartDate(String freeSpinStartDate) { this.freeSpinStartDate = freeSpinStartDate; }
    
    public Integer getNumSpinsAwarded() { return numSpinsAwarded; }
    public void setNumSpinsAwarded(Integer numSpinsAwarded) { this.numSpinsAwarded = numSpinsAwarded; }
    
    public String getBonusCode() { return bonusCode; }
    public void setBonusCode(String bonusCode) { this.bonusCode = bonusCode; }
    
    public String getParentGameCategory() { return parentGameCategory; }
    public void setParentGameCategory(String parentGameCategory) { this.parentGameCategory = parentGameCategory; }
    
    public String getMoneyType() { return moneyType; }
    public void setMoneyType(String moneyType) { this.moneyType = moneyType; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
}