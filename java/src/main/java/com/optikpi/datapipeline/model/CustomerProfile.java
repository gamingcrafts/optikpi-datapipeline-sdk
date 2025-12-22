package com.optikpi.datapipeline.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Customer Profile Model
 * Represents a customer profile for the Data Pipeline API
 * Updated to match JavaScript model structure
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
    
    // Changed to String to match JS model
    @JsonProperty("marketing_email_preference")
    private String marketingEmailPreference;
    
    // Changed to String to match JS model
    @JsonProperty("notifications_preference")
    private String notificationsPreference;
    
    @JsonProperty("subscription")
    private String subscription;
    
    // Changed to String to match JS model (was Map)
    @JsonProperty("privacy_settings")
    private String privacySettings;
    
    // Changed to Double to match JS model (was Map)
    @JsonProperty("deposit_limits")
    private Double depositLimits;
    
    // Changed to Double to match JS model (was Map)
    @JsonProperty("loss_limits")
    private Double lossLimits;
    
    // Changed to Double to match JS model (was Map)
    @JsonProperty("wagering_limits")
    private Double wageringLimits;
    
    // Changed to Integer to match JS model (was Map)
    @JsonProperty("session_time_limits")
    private Integer sessionTimeLimits;
    
    // Changed to Integer to match JS model (was String)
    @JsonProperty("cooling_off_period")
    private Integer coolingOffPeriod;
    
    // Changed to Integer to match JS model (was String)
    @JsonProperty("self_exclusion_period")
    private Integer selfExclusionPeriod;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("reality_checks_notification")
    private String realityChecksNotification;
    
    @Pattern(regexp = "Active|Inactive|Suspended|Closed", message = "account_status must be one of: Active, Inactive, Suspended, Closed")
    @JsonProperty("account_status")
    private String accountStatus;
    
    @Pattern(regexp = "Regular|Silver|Gold|Platinum|Diamond", message = "vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond")
    @JsonProperty("vip_status")
    private String vipStatus;
    
    // Changed to String to match JS model (was Map)
    @JsonProperty("loyalty_program_tiers")
    private String loyaltyProgramTiers;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("bonus_abuser")
    private String bonusAbuser;
    
    // Changed to Double to match JS model (was String)
    @JsonProperty("financial_risk_level")
    private Double financialRiskLevel;
    
    @JsonProperty("acquisition_source")
    private String acquisitionSource;
    
    @JsonProperty("partner_id")
    private String partnerId;
    
    // Fixed typo: affiliate_id (was affiliateId in Java, but affliate_id in JS)
    @JsonProperty("affliate_id")
    private String affliateId;
    
    @JsonProperty("referral_link_code")
    private String referralLinkCode;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("referral_limit_reached")
    private String referralLimitReached;
    
    @JsonProperty("creation_timestamp")
    private String creationTimestamp;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("phone_verification")
    private String phoneVerification;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("email_verification")
    private String emailVerification;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("bank_verification")
    private String bankVerification;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("iddoc_verification")
    private String iddocVerification;
    
    @JsonProperty("cooling_off_expiry_date")
    private String coolingOffExpiryDate;
    
    @JsonProperty("self_exclusion_expiry_date")
    private String selfExclusionExpiryDate;
    
    @JsonProperty("risk_score_level")
    private String riskScoreLevel;
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("marketing_sms_preference")
    private String marketingSmsPreference;
    
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
    
    // Changed to String to match JS model (was Boolean)
    @JsonProperty("real_money_enabled")
    private String realMoneyEnabled;
    
    @JsonProperty("push_token")
    private String pushToken;

    @JsonProperty("android_push_token")
    private String androidPushToken;        

    @JsonProperty("ios_push_token")
    private String iosPushToken;

    @JsonProperty("windows_push_token")
    private String windowsPushToken;

    @JsonProperty("mac_dmg_push_token")
    private String macdmgPushToken;
    
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
    
    public String getMarketingEmailPreference() { return marketingEmailPreference; }
    public void setMarketingEmailPreference(String marketingEmailPreference) { this.marketingEmailPreference = marketingEmailPreference; }
    
    public String getNotificationsPreference() { return notificationsPreference; }
    public void setNotificationsPreference(String notificationsPreference) { this.notificationsPreference = notificationsPreference; }
    
    public String getSubscription() { return subscription; }
    public void setSubscription(String subscription) { this.subscription = subscription; }
    
    public String getPrivacySettings() { return privacySettings; }
    public void setPrivacySettings(String privacySettings) { this.privacySettings = privacySettings; }
    
    public Double getDepositLimits() { return depositLimits; }
    public void setDepositLimits(Double depositLimits) { this.depositLimits = depositLimits; }
    
    public Double getLossLimits() { return lossLimits; }
    public void setLossLimits(Double lossLimits) { this.lossLimits = lossLimits; }
    
    public Double getWageringLimits() { return wageringLimits; }
    public void setWageringLimits(Double wageringLimits) { this.wageringLimits = wageringLimits; }
    
    public Integer getSessionTimeLimits() { return sessionTimeLimits; }
    public void setSessionTimeLimits(Integer sessionTimeLimits) { this.sessionTimeLimits = sessionTimeLimits; }
    
    public Integer getCoolingOffPeriod() { return coolingOffPeriod; }
    public void setCoolingOffPeriod(Integer coolingOffPeriod) { this.coolingOffPeriod = coolingOffPeriod; }
    
    public Integer getSelfExclusionPeriod() { return selfExclusionPeriod; }
    public void setSelfExclusionPeriod(Integer selfExclusionPeriod) { this.selfExclusionPeriod = selfExclusionPeriod; }
    
    public String getRealityChecksNotification() { return realityChecksNotification; }
    public void setRealityChecksNotification(String realityChecksNotification) { this.realityChecksNotification = realityChecksNotification; }
    
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    
    public String getVipStatus() { return vipStatus; }
    public void setVipStatus(String vipStatus) { this.vipStatus = vipStatus; }
    
    public String getLoyaltyProgramTiers() { return loyaltyProgramTiers; }
    public void setLoyaltyProgramTiers(String loyaltyProgramTiers) { this.loyaltyProgramTiers = loyaltyProgramTiers; }
    
    public String getBonusAbuser() { return bonusAbuser; }
    public void setBonusAbuser(String bonusAbuser) { this.bonusAbuser = bonusAbuser; }
    
    public Double getFinancialRiskLevel() { return financialRiskLevel; }
    public void setFinancialRiskLevel(Double financialRiskLevel) { this.financialRiskLevel = financialRiskLevel; }
    
    public String getAcquisitionSource() { return acquisitionSource; }
    public void setAcquisitionSource(String acquisitionSource) { this.acquisitionSource = acquisitionSource; }
    
    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    
    public String getAffliateId() { return affliateId; }
    public void setAffliateId(String affliateId) { this.affliateId = affliateId; }
    
    public String getReferralLinkCode() { return referralLinkCode; }
    public void setReferralLinkCode(String referralLinkCode) { this.referralLinkCode = referralLinkCode; }
    
    public String getReferralLimitReached() { return referralLimitReached; }
    public void setReferralLimitReached(String referralLimitReached) { this.referralLimitReached = referralLimitReached; }
    
    public String getCreationTimestamp() { return creationTimestamp; }
    public void setCreationTimestamp(String creationTimestamp) { this.creationTimestamp = creationTimestamp; }
    
    public String getPhoneVerification() { return phoneVerification; }
    public void setPhoneVerification(String phoneVerification) { this.phoneVerification = phoneVerification; }
    
    public String getEmailVerification() { return emailVerification; }
    public void setEmailVerification(String emailVerification) { this.emailVerification = emailVerification; }
    
    public String getBankVerification() { return bankVerification; }
    public void setBankVerification(String bankVerification) { this.bankVerification = bankVerification; }
    
    public String getIddocVerification() { return iddocVerification; }
    public void setIddocVerification(String iddocVerification) { this.iddocVerification = iddocVerification; }
    
    public String getCoolingOffExpiryDate() { return coolingOffExpiryDate; }
    public void setCoolingOffExpiryDate(String coolingOffExpiryDate) { this.coolingOffExpiryDate = coolingOffExpiryDate; }
    
    public String getSelfExclusionExpiryDate() { return selfExclusionExpiryDate; }
    public void setSelfExclusionExpiryDate(String selfExclusionExpiryDate) { this.selfExclusionExpiryDate = selfExclusionExpiryDate; }
    
    public String getRiskScoreLevel() { return riskScoreLevel; }
    public void setRiskScoreLevel(String riskScoreLevel) { this.riskScoreLevel = riskScoreLevel; }
    
    public String getMarketingSmsPreference() { return marketingSmsPreference; }
    public void setMarketingSmsPreference(String marketingSmsPreference) { this.marketingSmsPreference = marketingSmsPreference; }
    
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
    
    public String getRealMoneyEnabled() { return realMoneyEnabled; }
    public void setRealMoneyEnabled(String realMoneyEnabled) { this.realMoneyEnabled = realMoneyEnabled; }
    
    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }

    public String getAndroidPushToken() { return androidPushToken; }
    public void setAndroidPushToken(String androidPushToken) { this.androidPushToken = androidPushToken; }      

    public String getIosPushToken() { return iosPushToken; }
    public void setIosPushToken(String iosPushToken) { this.iosPushToken = iosPushToken; }

    public String getWindowsPushToken() { return windowsPushToken; }
    public void setWindowsPushToken(String windowsPushToken) { this.windowsPushToken = windowsPushToken; }

    public String getMacDmgPushToken() { return macdmgPushToken; }
    public void setMacDmgPushToken(String macdmgPushToken) { this.macdmgPushToken = macdmgPushToken; }
}
