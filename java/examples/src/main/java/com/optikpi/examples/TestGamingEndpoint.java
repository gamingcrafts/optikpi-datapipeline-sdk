package com.optikpi.examples;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.optikpi.datapipeline.ClientConfig;
import com.optikpi.datapipeline.OptikpiDataPipelineSDK;
import com.optikpi.datapipeline.model.GamingActivityEvent;
import com.optikpi.datapipeline.model.ValidationResult;

import io.github.cdimascio.dotenv.Dotenv;

public class TestGamingEndpoint {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    public static void main(String[] args) {
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

        System.out.println("🚀 Testing Gaming Event Endpoints");
        System.out.println("==================================");
        System.out.println("Configuration:");
        System.out.println("📌 API Base URL: " +  config.getBaseUrl());
        System.out.println("👤 Account ID: " + config.getAccountId());
        System.out.println("🏢 Workspace ID: " + config.getWorkspaceId());
        System.out.println();
        
        testGamingEvent(sdk, accountId, workspaceId);
    }

    private static void testGamingEvent(OptikpiDataPipelineSDK sdk, String accountId, String workspaceId) {
        try {
            GamingActivityEvent event = new GamingActivityEvent();
            event.setAccountId(accountId);
            event.setWorkspaceId(workspaceId);
            event.setUserId("java_012");
            event.setEventCategory("Gaming Activity");
            event.setEventName("Play Casino Game");
            event.setEventId("evt_" + System.currentTimeMillis());
            event.setEventTime(Instant.now().toString());
            event.setWagerAmount(new BigDecimal("10.00"));
            event.setWinAmount(new BigDecimal("25.00"));
            event.setLossAmount(new BigDecimal("0.00"));
            event.setGameId("game_001");
            event.setGameTitle("Mega Fortune Slots");
            event.setProvider("ProviderXYZ");
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
            
            ValidationResult valid = event.validate();
            if (!valid.isValid()) {
                System.out.println("\n❌ Validation Failed!");
                System.out.println("Errors: " + valid.getErrors());
                return;
            }
            
            System.out.println("✅ Gaming Activity event validated successfully!");
            System.out.println("\n📋Gaming Activity Event Data:");
            System.out.println(mapper.writeValueAsString(event));

            System.out.println("\n🕒 making API request using SDK...");
            long start = System.currentTimeMillis();
            var response = sdk.sendGamingActivityEvent(event);
            long end = System.currentTimeMillis();

            System.out.println("\n📡 API Response:");
            System.out.println("⏱ Response Time: " + (end - start) + "ms");
            System.out.println("HTTP Status: " + response.getStatus());
            System.out.println("Sending Gaming event...");
            
            if (response.isSuccess()) {
                System.out.println("✅ SUCCESS!");
                System.out.println("Response: " +
                        mapper.writeValueAsString(response.getData()));
            } else {
                System.out.println("❌ FAILED!");
                System.out.println("Error: " +
                        mapper.writeValueAsString(response.getError()));
            }

        } catch (Exception e) {
            System.err.println("\n💥 Exception occurred:");
            e.printStackTrace();
        }
    }
}


