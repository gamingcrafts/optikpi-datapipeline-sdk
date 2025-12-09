package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.ExtendedAttributesEvent;
import com.optikpi.datapipeline.model.GamingActivityEvent;
import com.optikpi.datapipeline.model.ValidationResult;
import com.optikpi.datapipeline.model.WalletBalanceEvent;
import com.optikpi.datapipeline.model.ReferFriendEvent;
import com.optikpi.datapipeline.model.WithdrawEvent;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Example: Test All Endpoints
 * Demonstrates how to use all available endpoints in the Optikpi Data Pipeline SDK
 */
public class TestAllEndpoints {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        // Get configuration from environment variables
        String authToken = dotenv.get("AUTH_TOKEN");
        String accountId = dotenv.get("ACCOUNT_ID");
        String workspaceId = dotenv.get("WORKSPACE_ID");
        String baseUrl = dotenv.get("API_BASE_URL");

        if (authToken == null || accountId == null || workspaceId == null) {
            System.err.println("Error: Missing required environment variables:");
            System.err.println("Please set AUTH_TOKEN, ACCOUNT_ID, and WORKSPACE_ID");
            System.exit(1);
        }

        // Create client configuration
        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        config.setBaseUrl(baseUrl);

        // Create SDK instance
        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

        System.out.println("🚀 Testing All Endpoints");
        System.out.println("=========================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();
       

        // Test all endpoints
        testCustomerProfile(sdk, accountId, workspaceId);
        testExtendedAttributes(sdk, accountId, workspaceId); 
        testAccountEvent(sdk, accountId, workspaceId);
        testDepositEvent(sdk, accountId, workspaceId);
        testWithdrawEvent(sdk, accountId, workspaceId);
        testGamingActivityEvent(sdk, accountId, workspaceId);
        testReferFriendEvent(sdk, accountId, workspaceId);      
        testWalletBalanceEvent(sdk, accountId, workspaceId);
    }

    private static void testCustomerProfile(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Customer Profile ===");
        try {
            CustomerProfile customer = createSampleCustomer(accountId, workspaceId);
            validateEvent(customer, "Customer Profile");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(customer));
            var response = sdk.sendCustomerProfile(customer);
            printResponse("Customer Profile", response);
        } catch (Exception e) {
            System.err.println("❌ Customer profile failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testExtendedAttributes(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Extended Attributes ===");
        
        // Test Format 1: ExtendedAttributesEvent with Map
        try {
            System.out.println("Testing Format 1: Object Format (BINGO_PREFERENCES)");
            ExtendedAttributesEvent extEvent1 = createSampleExtendedAttributesMapFormat(accountId, workspaceId);
            validateEvent(extEvent1, "Extended Attributes (Map Format)");
            var response1 = sdk.sendExtendedAttributes(extEvent1);
            printResponse("Extended Attributes (Map Format)", response1);
        } catch (Exception e) {
            System.err.println("❌ Extended attributes (Map) failed: " + e.getMessage());
        }
        
        // Test Format 2: ExtendedAttributesEvent with String
        try {
            System.out.println("Testing Format 2: JSON String Format (GAMING_PREFERENCES)");
            ExtendedAttributesEvent extEvent2 = createSampleExtendedAttributesStringFormat(accountId, workspaceId);
            validateEvent(extEvent2, "Extended Attributes (String Format)");
            var response2 = sdk.sendExtendedAttributes(extEvent2);
            printResponse("Extended Attributes (String Format)", response2);
        } catch (Exception e) {
            System.err.println("❌ Extended attributes (String) failed: " + e.getMessage());
        }
        
        System.out.println();
    }

    private static void testAccountEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Account Event ===");
        try {
            AccountEvent event = createSampleAccountEvent(accountId, workspaceId);
            validateEvent(event, "Account Event");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(event));
            var response = sdk.sendAccountEvent(event);
            printResponse("Account Event", response);
        } catch (Exception e) {
            System.err.println("❌ Account event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testDepositEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Deposit Event ===");
        try {
            DepositEvent event = createSampleDepositEvent(accountId, workspaceId);
            validateEvent(event, "Deposit Event");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(event));
            var response = sdk.sendDepositEvent(event);
            printResponse("Deposit Event", response);
        } catch (Exception e) {
            System.err.println("❌ Deposit event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testWithdrawEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Withdraw Event ===");
        try {
            WithdrawEvent event = createSampleWithdrawEvent(accountId, workspaceId);
            validateEvent(event, "Withdraw Event");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(event));
            var response = sdk.sendWithdrawEvent(event);
            printResponse("Withdraw Event", response);
        } catch (Exception e) {
            System.err.println("❌ Withdraw event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testGamingActivityEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Gaming Activity Event ===");
        try {
            GamingActivityEvent event = createSampleGamingActivityEvent(accountId, workspaceId);
            validateEvent(event, "Gaming Activity Event");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(event));
            var response = sdk.sendGamingActivityEvent(event);
            printResponse("Gaming Activity Event", response);
        } catch (Exception e) {
            System.err.println("❌ Gaming activity event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testReferFriendEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Refer Friend Event ===");
        try {
            ReferFriendEvent event = createSampleReferFriendEvent(accountId, workspaceId);
            validateEvent(event, "Refer Friend Event");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(event));
            var response = sdk.sendReferFriendEvent(event);
            printResponse("Refer Friend Event", response);
        } catch (Exception e) {
            System.err.println("❌ Refer Friend event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testWalletBalanceEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Wallet Balance Event ===");
        try {
            WalletBalanceEvent event = createSampleWalletBalanceEvent(accountId, workspaceId);
            validateEvent(event, "Wallet Balance Event");
            System.out.println("\n📋 Event Data:");
            System.out.println(mapper.writeValueAsString(event));
            var response = sdk.sendWalletBalanceEvent(event);
            printResponse("Wallet Balance Event", response);
        } catch (Exception e) {
            System.err.println("❌ Wallet Balance event failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static CustomerProfile createSampleCustomer(String accountId, String workspaceId) {
        CustomerProfile customer = new CustomerProfile();
        customer.setAccountId(accountId);
            customer.setWorkspaceId(workspaceId);
            customer.setUserId("javasdk_01");
            customer.setUsername("john_doe");
            customer.setFullName("John Doe");
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setDateOfBirth("1990-01-15");
            customer.setEmail("john.doe@example.com");
            customer.setPhoneNumber("+1234567890");
            customer.setGender("Male");
            customer.setCountry("United States");
            customer.setCity("New York");
            customer.setLanguage("en");
            customer.setCurrency("USD");

            customer.setPhoneVerification("Verified");
            customer.setEmailVerification("Verified");
            customer.setBankVerification("NotVerified");
            customer.setIddocVerification("Verified");
        
            customer.setMarketingEmailPreference("Opt-in");
            customer.setNotificationsPreference("Opt-in");
            customer.setSubscription("Subscribed");
    
            customer.setPrivacySettings("private");
            
            customer.setDepositLimits(1000.00);
            customer.setLossLimits(500.00);
            customer.setWageringLimits(2000.00);
            customer.setSessionTimeLimits(120);

            customer.setCoolingOffExpiryDate("2024-12-31T23:59:59Z");
            customer.setSelfExclusionExpiryDate("2025-01-31T23:59:59Z");
            customer.setAffliateId("affiliate456");
            
            customer.setCoolingOffPeriod(7);
            customer.setSelfExclusionPeriod(30);

            customer.setRealityChecksNotification("daily");
            customer.setVipStatus("Regular");
            customer.setLoyaltyProgramTiers("Bronze");
            customer.setAccountStatus("Active");
            customer.setBonusAbuser("Not flagged");
            customer.setFinancialRiskLevel(0.3);
            
            customer.setAcquisitionSource("Google Ads");
            customer.setPartnerId("partner123");
            customer.setReferralLinkCode("REF789");
            customer.setReferralLimitReached("Not Reached");
            customer.setCreationTimestamp("2024-01-15T10:30:00Z");
        
            customer.setRiskScoreLevel("low");
            customer.setMarketingSmsPreference("Opt-in");
            
            // Custom data as Map (same in both)
            Map<String, Object> customData = new HashMap<>();
            customData.put("favorite_game", "slots");
            customData.put("newsletter_signup", true);
            customer.setCustomData(customData);
            
            customer.setSelfExclusionBy("player");
            customer.setSelfExclusionByType("voluntary");
            customer.setSelfExclusionCheckTime("2024-01-15T10:30:00Z");
            customer.setSelfExclusionCreatedTime("2024-01-01T00:00:00Z");
            customer.setClosedTime(null);

            customer.setRealMoneyEnabled("true");
            customer.setPushToken("push_token_abc123");

        return customer;
    }

    private static ExtendedAttributesEvent createSampleExtendedAttributesMapFormat(String accountId, String workspaceId) {
        ExtendedAttributesEvent event = new ExtendedAttributesEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("sdk_ext_001");
        event.setListName("BINGO_PREFERENCES");
        
        Map<String, String> extDataMap = new HashMap<>();
        extDataMap.put("Email", "True");
        extDataMap.put("SMS", "True");
        extDataMap.put("PushNotifications", "False");
        event.setExtData(extDataMap);
        
        return event;
    }

    private static ExtendedAttributesEvent createSampleExtendedAttributesStringFormat(String accountId, String workspaceId) {
        ExtendedAttributesEvent event = new ExtendedAttributesEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("sdk_ext_002");
        event.setListName("GAMING_PREFERENCES");
        
        String extDataJson = "{\"Email\":\"True\",\"SMS\":\"True\",\"PushNotifications\":\"True\"}";
        event.setExtDataAsString(extDataJson);
        
        return event;
    }

    private static AccountEvent createSampleAccountEvent(String accountId, String workspaceId) {
        AccountEvent event = new AccountEvent();
             event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("javasdk_01");
            event.setEventCategory("Account");
            event.setEventName("Player Registration");
            event.setEventId("evt_login_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setAffiliateId("aff_001");
            event.setPartnerId("partner_001");
            event.setDevice("desktop");
            event.setCampaignCode("CAMPAIGN_001");
            event.setStatus("completed");
            event.setReason("Registration completed successfully");
        return event;
    }

    private static DepositEvent createSampleDepositEvent(String accountId, String workspaceId) {
        DepositEvent event = new DepositEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("java_01");
            event.setEventCategory("Deposit");
            event.setEventName("Successful Deposit");
            event.setEventId("evt_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setPaymentMethod("bank");
            event.setTransactionId("txn_" + System.currentTimeMillis());
            event.setAmount(new BigDecimal("100.00"));
            event.setPaymentProviderId("provider123");
            event.setPaymentProviderName("Chase Bank");
            event.setFailureReason(null);
            event.setCurrency("USD");
            event.setFees(new BigDecimal("2.50"));
            event.setNetAmount(new BigDecimal("97.50"));
            event.setStatus("success");
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("bank_name", "Chase Bank");
            metadata.put("account_last4", "1234");
            event.setMetadata(metadata);
        return event;
    }

    private static WithdrawEvent createSampleWithdrawEvent(String accountId, String workspaceId) {
        WithdrawEvent event = new WithdrawEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("javasdk_01");
            event.setEventName("Successful Withdrawal");
            event.setEventId("evt_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setAmount(new BigDecimal("50.00"));
            event.setCurrency("USD");
            event.setPaymentMethod("bank");
            event.setTransactionId("txn_" + System.currentTimeMillis());
            event.setStatus("success");
            event.setDevice("desktop");
            event.setAffiliateId("aff_123456");
            event.setPartnerId("partner_789");
            event.setCampaignCode("SUMMER2024");
            event.setReason("User requested withdrawal");
            event.setFees(new BigDecimal("1.50"));
            event.setNetAmount(new BigDecimal("48.50"));
            event.setWithdrawalReason("Cash out winnings");
            event.setProcessingTime("2024-01-15T10:30:00Z");
            event.setFailureReason(null);
            event.setEventCategory("Withdraw");
        return event;
    }

    private static GamingActivityEvent createSampleGamingActivityEvent(String accountId, String workspaceId) {
        GamingActivityEvent event = new GamingActivityEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("javasdk_01");
            event.setEventCategory("Gaming Activity");
            event.setEventName("Play Casino Game");
            event.setEventId("evt_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setWagerAmount(new BigDecimal("10.00"));
            event.setWinAmount(new BigDecimal("25.00"));
            event.setLossAmount(new BigDecimal("0.00"));
            event.setGameId("game_001");
            event.setGameTitle("Mega Fortune Slots");
            event.setGameProvider("ProviderXYZ");
            event.setBonusId("bonus_12345");
            event.setFreeSpinId("freespin_67890");
            event.setJackpotAmount(new BigDecimal("1000.00"));
            event.setNumSpinsPlayed(50);
            event.setGameTheme("Egyptian");
            event.setRemainingSpins(10);
            event.setBetValuePerSpin(new BigDecimal("0.50"));
            event.setWageringRequirementsMet(true);
            event.setFreeSpinExpiryDate("2024-12-31T23:59:59Z");
            event.setCampaignId("camp_summer2024");
            event.setCampaignName("Summer Bonanza");
            event.setRtp(new BigDecimal("96.5"));
            event.setGameCategory("slots");
            event.setWinningBetAmount(new BigDecimal("25.00"));
            event.setJackpotType("progressive");
            event.setVolatility("high");
            event.setMinBet(new BigDecimal("0.10"));
            event.setMaxBet(new BigDecimal("100.00"));
            event.setNumberOfReels(5);
            event.setNumberOfPaylines(20);
            event.setFeatureTypes("wild,scatter,freespins");
            event.setGameReleaseDate("2023-01-15T00:00:00Z");
            event.setLiveDealerAvailability(false);
            event.setSideBetsAvailability(true);
            event.setMultiplayerOption(false);
            event.setAutoPlay(true);
            event.setPokerVariant("texas_holdem");
            event.setTournamentName("Weekend Warriors");
            event.setBuyInAmount(new BigDecimal("50.00"));
            event.setTableType("cash_game");
            event.setStakesLevel("medium");
            event.setNumberOfPlayers(6);
            event.setGameDuration(45);
            event.setHandVolume(120);
            event.setPlayerPosition("button");
            event.setFinalHand("royal_flush");
            event.setRakeContribution(new BigDecimal("2.50"));
            event.setMultiTablingIndicator(false);
            event.setSessionResult("win");
            event.setVipStatus("gold");
            event.setBlindLevel("50/100");
            event.setRebuyAndAddonInfo("1 rebuy, 1 addon");
            event.setSportType("football");
            event.setBettingMarket("match_winner");
            event.setOdds(new BigDecimal("2.50"));
            event.setLiveBettingAvailability(true);
            event.setResult("won");
            event.setBetStatus("settled");
            event.setBettingChannel("online");
            event.setBonusType("welcome_bonus");
            event.setBonusAmount(new BigDecimal("100.00"));
            event.setFreeSpinStartDate("2024-01-01T00:00:00Z");
            event.setNumSpinsAwarded(20);
            event.setBonusCode("WELCOME100");
            event.setParentGameCategory("casino");
            event.setCurrency("USD");
            event.setMoneyType("real");
            event.setTransactionType("bet");

        return event;
    }

    private static ReferFriendEvent createSampleReferFriendEvent(String accountId, String workspaceId) {
        ReferFriendEvent event = new ReferFriendEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("javasdk_01");
            event.setEventCategory("Refer Friend");
            event.setEventName("Referral Successful");
            event.setEventId("evt_rf_987654321");
            event.setEventTime(Instant.now().toString());
            event.setReferralCodeUsed("REF123456");
            event.setSuccessfulReferralConfirmation(true);
            event.setRewardType("bonus");
            event.setRewardClaimedStatus("claimed");
            event.setRefereeUserId("user789012");
            event.setRefereeRegistrationDate("2024-01-15T10:30:00Z");
            event.setRefereeFirstDeposit(100.00);
        return event;
    }

    private static WalletBalanceEvent createSampleWalletBalanceEvent(String accountId, String workspaceId) {
        WalletBalanceEvent event = new WalletBalanceEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("javasdk_01");
            event.setEventCategory("Wallet Balance");
            event.setEventName("Balance Update");
            event.setEventId("evt_wb_987654321");
            event.setEventTime(Instant.now().toString());
            event.setWalletType("main");
            event.setCurrency("USD");
            event.setCurrentCashBalance(new BigDecimal("1250.50"));
            event.setCurrentBonusBalance(new BigDecimal("100.00"));
            event.setCurrentTotalBalance(new BigDecimal("1350.50"));
            event.setBlockedAmount(new BigDecimal("50.00"));
        return event;
    }

    private static void printResponse(String operation, com.optikpi.datapipeline.ApiResponse<Object> response) {
        if (response.isSuccess()) {
            System.out.println("✅ " + operation + " successful!");
            System.out.println("Status: " + response.getStatus());
            System.out.println("Response: " + response.getData());
        } else {
            System.out.println("❌ " + operation + " failed");
            System.out.println("Error: " + response.getError());
            System.out.println("Status: " + response.getStatus());
        }
    }

    private static void validateEvent(Object event, String eventName) {
        if (event instanceof CustomerProfile) {
            ValidationResult result = ((CustomerProfile) event).validate();
            printValidationResult(result, eventName);    
        } else if (event instanceof ExtendedAttributesEvent) {
            ValidationResult result = ((ExtendedAttributesEvent) event).validate();
            printValidationResult(result, eventName);    
        } else if (event instanceof AccountEvent) {
            ValidationResult result = ((AccountEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof DepositEvent) {
            ValidationResult result = ((DepositEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof WithdrawEvent) {
            ValidationResult result = ((WithdrawEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof GamingActivityEvent) {
            ValidationResult result = ((GamingActivityEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof ReferFriendEvent) {
            ValidationResult result = ((ReferFriendEvent) event).validate();
            printValidationResult(result, eventName);
        } else if (event instanceof WalletBalanceEvent) {
            ValidationResult result = ((WalletBalanceEvent) event).validate();
            printValidationResult(result, eventName);   
        } else {
            System.out.println("⚠️ Unknown event type: " + eventName);
        }
    }

    private static void printValidationResult(ValidationResult result, String eventName) {
        if (!result.isValid()) {
            System.out.println("❌ Invalid " + eventName + ":");
            System.out.println("Errors: " + result.getErrors());
        } else {
            System.out.println("✅ Valid " + eventName + ": " + result.isValid());
        }
    }
}