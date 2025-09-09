package com.optikpi.datapipeline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Customer Profile Model
 * Represents a customer profile for the Data Pipeline API
 */
public class CustomerProfile {
    
    @NotBlank(message = "account_id is required")
    @JsonProperty("account_id")
    private String accountId;
    
    @NotBlank(message = "workspace_id is required")
    @JsonProperty("workspace_id")
    private String workspaceId;
    
    @NotBlank(message = "user_id is required")
    @JsonProperty("user_id")
    private String userId;
    
    @NotBlank(message = "username is required")
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    
    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phone_number")
    private String phoneNumber;
    
    @Pattern(regexp = "Male|Female|Other", message = "gender must be one of: Male, Female, Other")
    @JsonProperty("gender")
    private String gender;
    
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("marketing_email_preference")
    private Boolean marketingEmailPreference;
    
    @JsonProperty("notifications_preference")
    private Boolean notificationsPreference;
    
    @JsonProperty("subscription")
    private String subscription;
    
    @JsonProperty("privacy_settings")
    private Map<String, Object> privacySettings;
    
    @JsonProperty("deposit_limits")
    private Map<String, Object> depositLimits;
    
    @JsonProperty("loss_limits")
    private Map<String, Object> lossLimits;
    
    @JsonProperty("wagering_limits")
    private Map<String, Object> wageringLimits;
    
    @JsonProperty("session_time_limits")
    private Map<String, Object> sessionTimeLimits;
    
    @JsonProperty("cooling_off_period")
    private String coolingOffPeriod;
    
    @JsonProperty("self_exclusion_period")
    private String selfExclusionPeriod;
    
    @JsonProperty("reality_checks_notification")
    private Boolean realityChecksNotification;
    
    @Pattern(regexp = "Active|Inactive|Suspended|Closed", message = "account_status must be one of: Active, Inactive, Suspended, Closed")
    @JsonProperty("account_status")
    private String accountStatus;
    
    @Pattern(regexp = "Regular|Silver|Gold|Platinum|Diamond", message = "vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond")
    @JsonProperty("vip_status")
    private String vipStatus;
    
    @JsonProperty("loyalty_program_tiers")
    private Map<String, Object> loyaltyProgramTiers;
    
    @JsonProperty("bonus_abuser")
    private Boolean bonusAbuser;
    
    @JsonProperty("financial_risk_level")
    private String financialRiskLevel;
    
    @JsonProperty("acquisition_source")
    private String acquisitionSource;
    
    @JsonProperty("partner_id")
    private String partnerId;
    
    @JsonProperty("affiliate_id")
    private String affiliateId;
    
    @JsonProperty("referral_link_code")
    private String referralLinkCode;
    
    @JsonProperty("referral_limit_reached")
    private Boolean referralLimitReached;
    
    @JsonProperty("creation_timestamp")
    private String creationTimestamp;
    
    @JsonProperty("phone_verification")
    private Boolean phoneVerification;
    
    @JsonProperty("email_verification")
    private Boolean emailVerification;
    
    @JsonProperty("bank_verification")
    private Boolean bankVerification;
    
    @JsonProperty("iddoc_verification")
    private Boolean iddocVerification;
    
    @JsonProperty("cooling_off_expiry_date")
    private String coolingOffExpiryDate;
    
    @JsonProperty("self_exclusion_expiry_date")
    private String selfExclusionExpiryDate;
    
    @JsonProperty("risk_score_level")
    private String riskScoreLevel;
    
    @JsonProperty("marketing_sms_preference")
    private Boolean marketingSmsPreference;
    
    @JsonProperty("custom_data")
    private Map<String, Object> customData;
    
    @JsonProperty("self_exclusion_by")
    private String selfExclusionBy;
    
    @JsonProperty("self_exclusion_by_type")
    private String selfExclusionByType;
    
    @JsonProperty("self_exclusion_check_time")
    private String selfExclusionCheckTime;
    
    @JsonProperty("self_exclusion_created_time")
    private String selfExclusionCreatedTime;
    
    @JsonProperty("closed_time")
    private String closedTime;
    
    @JsonProperty("real_money_enabled")
    private Boolean realMoneyEnabled;
    
    @JsonProperty("push_token")
    private String pushToken;
    
    public CustomerProfile() {}
    
    public CustomerProfile(String accountId, String workspaceId, String userId, String username, String email) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
    
    /**
     * Validates the customer profile data
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
        if (username == null || username.trim().isEmpty()) {
            errors.add("username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("email is required");
        }
        
        // Email format validation
        if (email != null && !isValidEmail(email)) {
            errors.add("email must be a valid email address");
        }
        
        // Date format validation
        if (dateOfBirth != null && !isValidDate(dateOfBirth)) {
            errors.add("date_of_birth must be in YYYY-MM-DD format");
        }
        
        // Enum validations
        if (gender != null && !isValidGender(gender)) {
            errors.add("gender must be one of: Male, Female, Other");
        }
        
        if (accountStatus != null && !isValidAccountStatus(accountStatus)) {
            errors.add("account_status must be one of: Active, Inactive, Suspended, Closed");
        }
        
        if (vipStatus != null && !isValidVipStatus(vipStatus)) {
            errors.add("vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
    
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    private boolean isValidGender(String gender) {
        return "Male".equals(gender) || "Female".equals(gender) || "Other".equals(gender);
    }
    
    private boolean isValidAccountStatus(String status) {
        return "Active".equals(status) || "Inactive".equals(status) || 
               "Suspended".equals(status) || "Closed".equals(status);
    }
    
    private boolean isValidVipStatus(String status) {
        return "Regular".equals(status) || "Silver".equals(status) || "Gold".equals(status) || 
               "Platinum".equals(status) || "Diamond".equals(status);
    }
    
    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Boolean getMarketingEmailPreference() { return marketingEmailPreference; }
    public void setMarketingEmailPreference(Boolean marketingEmailPreference) { this.marketingEmailPreference = marketingEmailPreference; }
    
    public Boolean getNotificationsPreference() { return notificationsPreference; }
    public void setNotificationsPreference(Boolean notificationsPreference) { this.notificationsPreference = notificationsPreference; }
    
    public String getSubscription() { return subscription; }
    public void setSubscription(String subscription) { this.subscription = subscription; }
    
    public Map<String, Object> getPrivacySettings() { return privacySettings; }
    public void setPrivacySettings(Map<String, Object> privacySettings) { this.privacySettings = privacySettings; }
    
    public Map<String, Object> getDepositLimits() { return depositLimits; }
    public void setDepositLimits(Map<String, Object> depositLimits) { this.depositLimits = depositLimits; }
    
    public Map<String, Object> getLossLimits() { return lossLimits; }
    public void setLossLimits(Map<String, Object> lossLimits) { this.lossLimits = lossLimits; }
    
    public Map<String, Object> getWageringLimits() { return wageringLimits; }
    public void setWageringLimits(Map<String, Object> wageringLimits) { this.wageringLimits = wageringLimits; }
    
    public Map<String, Object> getSessionTimeLimits() { return sessionTimeLimits; }
    public void setSessionTimeLimits(Map<String, Object> sessionTimeLimits) { this.sessionTimeLimits = sessionTimeLimits; }
    
    public String getCoolingOffPeriod() { return coolingOffPeriod; }
    public void setCoolingOffPeriod(String coolingOffPeriod) { this.coolingOffPeriod = coolingOffPeriod; }
    
    public String getSelfExclusionPeriod() { return selfExclusionPeriod; }
    public void setSelfExclusionPeriod(String selfExclusionPeriod) { this.selfExclusionPeriod = selfExclusionPeriod; }
    
    public Boolean getRealityChecksNotification() { return realityChecksNotification; }
    public void setRealityChecksNotification(Boolean realityChecksNotification) { this.realityChecksNotification = realityChecksNotification; }
    
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    
    public String getVipStatus() { return vipStatus; }
    public void setVipStatus(String vipStatus) { this.vipStatus = vipStatus; }
    
    public Map<String, Object> getLoyaltyProgramTiers() { return loyaltyProgramTiers; }
    public void setLoyaltyProgramTiers(Map<String, Object> loyaltyProgramTiers) { this.loyaltyProgramTiers = loyaltyProgramTiers; }
    
    public Boolean getBonusAbuser() { return bonusAbuser; }
    public void setBonusAbuser(Boolean bonusAbuser) { this.bonusAbuser = bonusAbuser; }
    
    public String getFinancialRiskLevel() { return financialRiskLevel; }
    public void setFinancialRiskLevel(String financialRiskLevel) { this.financialRiskLevel = financialRiskLevel; }
    
    public String getAcquisitionSource() { return acquisitionSource; }
    public void setAcquisitionSource(String acquisitionSource) { this.acquisitionSource = acquisitionSource; }
    
    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    
    public String getAffiliateId() { return affiliateId; }
    public void setAffiliateId(String affiliateId) { this.affiliateId = affiliateId; }
    
    public String getReferralLinkCode() { return referralLinkCode; }
    public void setReferralLinkCode(String referralLinkCode) { this.referralLinkCode = referralLinkCode; }
    
    public Boolean getReferralLimitReached() { return referralLimitReached; }
    public void setReferralLimitReached(Boolean referralLimitReached) { this.referralLimitReached = referralLimitReached; }
    
    public String getCreationTimestamp() { return creationTimestamp; }
    public void setCreationTimestamp(String creationTimestamp) { this.creationTimestamp = creationTimestamp; }
    
    public Boolean getPhoneVerification() { return phoneVerification; }
    public void setPhoneVerification(Boolean phoneVerification) { this.phoneVerification = phoneVerification; }
    
    public Boolean getEmailVerification() { return emailVerification; }
    public void setEmailVerification(Boolean emailVerification) { this.emailVerification = emailVerification; }
    
    public Boolean getBankVerification() { return bankVerification; }
    public void setBankVerification(Boolean bankVerification) { this.bankVerification = bankVerification; }
    
    public Boolean getIddocVerification() { return iddocVerification; }
    public void setIddocVerification(Boolean iddocVerification) { this.iddocVerification = iddocVerification; }
    
    public String getCoolingOffExpiryDate() { return coolingOffExpiryDate; }
    public void setCoolingOffExpiryDate(String coolingOffExpiryDate) { this.coolingOffExpiryDate = coolingOffExpiryDate; }
    
    public String getSelfExclusionExpiryDate() { return selfExclusionExpiryDate; }
    public void setSelfExclusionExpiryDate(String selfExclusionExpiryDate) { this.selfExclusionExpiryDate = selfExclusionExpiryDate; }
    
    public String getRiskScoreLevel() { return riskScoreLevel; }
    public void setRiskScoreLevel(String riskScoreLevel) { this.riskScoreLevel = riskScoreLevel; }
    
    public Boolean getMarketingSmsPreference() { return marketingSmsPreference; }
    public void setMarketingSmsPreference(Boolean marketingSmsPreference) { this.marketingSmsPreference = marketingSmsPreference; }
    
    public Map<String, Object> getCustomData() { return customData; }
    public void setCustomData(Map<String, Object> customData) { this.customData = customData; }
    
    public String getSelfExclusionBy() { return selfExclusionBy; }
    public void setSelfExclusionBy(String selfExclusionBy) { this.selfExclusionBy = selfExclusionBy; }
    
    public String getSelfExclusionByType() { return selfExclusionByType; }
    public void setSelfExclusionByType(String selfExclusionByType) { this.selfExclusionByType = selfExclusionByType; }
    
    public String getSelfExclusionCheckTime() { return selfExclusionCheckTime; }
    public void setSelfExclusionCheckTime(String selfExclusionCheckTime) { this.selfExclusionCheckTime = selfExclusionCheckTime; }
    
    public String getSelfExclusionCreatedTime() { return selfExclusionCreatedTime; }
    public void setSelfExclusionCreatedTime(String selfExclusionCreatedTime) { this.selfExclusionCreatedTime = selfExclusionCreatedTime; }
    
    public String getClosedTime() { return closedTime; }
    public void setClosedTime(String closedTime) { this.closedTime = closedTime; }
    
    public Boolean getRealMoneyEnabled() { return realMoneyEnabled; }
    public void setRealMoneyEnabled(Boolean realMoneyEnabled) { this.realMoneyEnabled = realMoneyEnabled; }
    
    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }
}
