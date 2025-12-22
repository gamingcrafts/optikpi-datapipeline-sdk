package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.optikpi.datapipeline.BatchData;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.AccountEvent;
import com.optikpi.datapipeline.model.CustomerProfile;
import com.optikpi.datapipeline.model.DepositEvent;
import com.optikpi.datapipeline.model.ExtendedAttributesEvent;
import com.optikpi.datapipeline.model.GamingActivityEvent;
import com.optikpi.datapipeline.model.ReferFriendEvent;
import com.optikpi.datapipeline.model.ValidationResult;
import com.optikpi.datapipeline.model.WalletBalanceEvent;
import com.optikpi.datapipeline.model.WithdrawEvent;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Example: Test Batch Operations Endpoint
 * Demonstrates how to send multiple events in a single batch request
 */
public class TestBatchOperations {

     private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        String authToken = dotenv.get("AUTH_TOKEN");
        String accountId = dotenv.get("ACCOUNT_ID");
        String workspaceId = dotenv.get("WORKSPACE_ID");
        String baseUrl = dotenv.get("API_BASE_URL");

        if (authToken == null || accountId == null || workspaceId == null) {
            System.err.println("Error: Missing required environment variables");
            System.exit(1);
        }

        ClientConfig config = new ClientConfig(authToken, accountId, workspaceId);
        config.setBaseUrl(baseUrl);

        OptikpiDataPipelineSDK sdk = new OptikpiDataPipelineSDK(config);

        System.out.println("🚀 Testing batch operations Endpoint");
        System.out.println("=====================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();
    
        testBatchOperations(sdk, accountId, workspaceId);
    }

    private static void testBatchOperations(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        System.out.println("=== Batch Operations ===");
        try {
            BatchData batchData = new BatchData();
            batchData.setCustomers(Arrays.asList(createSampleCustomer(accountId, workspaceId)));
            batchData.setExtendedAttributes(Arrays.asList(
                createSampleExtendedAttributesMapFormat(accountId, workspaceId),
                createSampleExtendedAttributesStringFormat(accountId, workspaceId)
            ));
            batchData.setAccountEvents(Arrays.asList(createSampleAccountEvent(accountId, workspaceId)));
            batchData.setDepositEvents(Arrays.asList(createSampleDepositEvent(accountId, workspaceId)));
            batchData.setWithdrawEvents(Arrays.asList(createSampleWithdrawEvent(accountId, workspaceId)));
            batchData.setGamingEvents(Arrays.asList(createSampleGamingActivityEvent(accountId, workspaceId)));
            batchData.setReferFriendEvents(Arrays.asList(createSampleReferFriendEvent(accountId, workspaceId)));
            batchData.setWalletBalanceEvents(Arrays.asList(createSampleWalletBalanceEvent(accountId, workspaceId)));
            
            validateBatchData(batchData);
            
            var response = sdk.sendBatch(batchData);

            if (response.isSuccess()) {
                System.out.println("✅ Batch operation completed successfully!");
                System.out.println("Timestamp: " + response.getTimestamp());

                if (response.getCustomers() != null) {
                    System.out.println("Customer profiles: " + (response.getCustomers().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getExtendedAttributes() != null) {
                    System.out.println("Extended Attributes: " + (response.getExtendedAttributes().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getAccountEvents() != null) {
                    System.out.println("Account events: " + (response.getAccountEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getDepositEvents() != null) {
                    System.out.println("Deposit events: " + (response.getDepositEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getWithdrawEvents() != null) {
                    System.out.println("Withdraw events: " + (response.getWithdrawEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getGamingEvents() != null) {
                    System.out.println("Gaming events: " + (response.getGamingEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getReferFriendEvents() != null) {
                    System.out.println("Refer Friend events: " + (response.getReferFriendEvents().isSuccess() ? "Success" : "Failed"));
                }
                if (response.getWalletBalanceEvents() != null) {
                    System.out.println("Wallet Balance events: " + (response.getWalletBalanceEvents().isSuccess() ? "Success" : "Failed"));
                }
            } else {
                System.out.println("❌ Batch operation failed");
            }
        } catch (Exception e) {
            System.err.println("❌ Batch operation failed: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static CustomerProfile createSampleCustomer(String accountId, String workspaceId) {
           CustomerProfile customer = new CustomerProfile();
            customer.setAccountId(accountId);
            customer.setWorkspaceId(workspaceId);
            customer.setUserId("javabatch_01");
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
            customer.setAndroidPushToken("android_push_token_xyz456");
            customer.setIosPushToken("ios_push_token_def789");
            customer.setWindowsPushToken("windows_push_token_ghi012");  
            customer.setMacDmgPushToken("mac_push_token_jkl345");

             
        return customer;
    }

    private static ExtendedAttributesEvent createSampleExtendedAttributesMapFormat(String accountId, String workspaceId) {
        ExtendedAttributesEvent event = new ExtendedAttributesEvent();
        event.setAccountId(accountId);
        event.setWorkspaceId(workspaceId);
        event.setUserId("batch_ext_001");
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
        event.setUserId("batch_ext_002");
        event.setListName("GAMING_PREFERENCES");
        
        String extDataJson = "{\"Email\":\"True\",\"SMS\":\"True\",\"PushNotifications\":\"True\"}";
        event.setExtDataAsString(extDataJson);
        
        return event;
    }

    private static AccountEvent createSampleAccountEvent(String accountId, String workspaceId) {
        AccountEvent event = new AccountEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("javabatch_01");
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
            event.setUserId("javabatch_01");
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
            event.setUserId("javabatch_01");
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
            event.setUserId("javabatch_01");
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
            event.setUserId("javabatch_01");
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

    private static void validateBatchData(BatchData batch) {
        System.out.println("=== Validating BatchData contents ===");

        if (batch.getCustomers() != null) {
            for (Object c : batch.getCustomers()) {
                if (c instanceof CustomerProfile) {
                    ValidationResult result = ((CustomerProfile) c).validate();
                    printValidationResult(result, "CustomerProfile (Batch)");
                }
            }
        }
        if (batch.getExtendedAttributes() != null) {
            for (Object ea : batch.getExtendedAttributes()) {
                if (ea instanceof ExtendedAttributesEvent) {
                    ValidationResult result = ((ExtendedAttributesEvent) ea).validate();
                    printValidationResult(result, "ExtendedAttributes (Batch)");
                }
            }
        }
        if (batch.getAccountEvents() != null) {
            for (Object a : batch.getAccountEvents()) {
                if (a instanceof AccountEvent) {
                    ValidationResult result = ((AccountEvent) a).validate();
                    printValidationResult(result, "AccountEvent (Batch)");
                }
            }
        }
        if (batch.getDepositEvents() != null) {
            for (Object d : batch.getDepositEvents()) {
                if (d instanceof DepositEvent) {
                    ValidationResult result = ((DepositEvent) d).validate();
                    printValidationResult(result, "DepositEvent (Batch)");
                }
            }
        }
        if (batch.getWithdrawEvents() != null) {
            for (Object w : batch.getWithdrawEvents()) {
                if (w instanceof WithdrawEvent) {
                    ValidationResult result = ((WithdrawEvent) w).validate();
                    printValidationResult(result, "WithdrawEvent (Batch)");
                }
            }
        }
        if (batch.getGamingEvents() != null) {
            for (Object g : batch.getGamingEvents()) {
                if (g instanceof GamingActivityEvent) {
                    ValidationResult result = ((GamingActivityEvent) g).validate();
                    printValidationResult(result, "GamingActivityEvent (Batch)");
                }
            }
        }
        if (batch.getReferFriendEvents() != null) {
            for (Object r : batch.getReferFriendEvents()) {
                if (r instanceof ReferFriendEvent) {
                    ValidationResult result = ((ReferFriendEvent) r).validate();
                    printValidationResult(result, "ReferFriendEvent (Batch)");
                }
            }
        }
        if (batch.getWalletBalanceEvents() != null) {
            for (Object wb : batch.getWalletBalanceEvents()) {
                if (wb instanceof WalletBalanceEvent) {
                    ValidationResult result = ((WalletBalanceEvent) wb).validate();
                    printValidationResult(result, "WalletBalanceEvent (Batch)");
                }
            }
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