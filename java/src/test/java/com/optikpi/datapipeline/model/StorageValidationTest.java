package com.optikpi.datapipeline.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.optikpi.datapipeline.model.BigQueryValidation.ColumnType;

class StorageValidationTest {

    private static final String NOW = Instant.now().toString();

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CheckType {

        // The values a dev BigQuery probe stored (true) or rejected (false), plus the SDK's own choices noted.
        Stream<Arguments> cases() {
            return Stream.of(
                Arguments.of(ColumnType.STRING, "abc", true),
                Arguments.of(ColumnType.STRING, "", true),
                Arguments.of(ColumnType.STRING, 15466, false),
                Arguments.of(ColumnType.STRING, true, false),
                Arguments.of(ColumnType.STRING, new HashMap<>(), false),
                Arguments.of(ColumnType.STRING, Arrays.asList(1, 2), false),

                Arguments.of(ColumnType.NUMERIC, 10, true),
                Arguments.of(ColumnType.NUMERIC, 10.5, true),
                Arguments.of(ColumnType.NUMERIC, -3, true),
                Arguments.of(ColumnType.NUMERIC, new BigDecimal("10.50"), true),
                Arguments.of(ColumnType.NUMERIC, "10.5", true),
                Arguments.of(ColumnType.NUMERIC, "-10", true),
                Arguments.of(ColumnType.NUMERIC, "1e5", true),
                Arguments.of(ColumnType.NUMERIC, " 10", true),
                Arguments.of(ColumnType.NUMERIC, "10 ", true),
                Arguments.of(ColumnType.NUMERIC, "+10", true),
                Arguments.of(ColumnType.NUMERIC, ".5", true),
                Arguments.of(ColumnType.NUMERIC, "10.", true),
                Arguments.of(ColumnType.NUMERIC, "\t10\n", true),
                Arguments.of(ColumnType.NUMERIC, "\u000B10\f\r", true),
                Arguments.of(ColumnType.NUMERIC, "\u00A010", false), // padding is ASCII whitespace only
                Arguments.of(ColumnType.NUMERIC, "10\u2003", false),
                Arguments.of(ColumnType.NUMERIC, "", false),
                Arguments.of(ColumnType.NUMERIC, true, false),
                Arguments.of(ColumnType.NUMERIC, "1,000", false),
                Arguments.of(ColumnType.NUMERIC, "abc", false), // stored as garbage by BigQuery; rejected on purpose
                Arguments.of(ColumnType.NUMERIC, Double.POSITIVE_INFINITY, false),
                Arguments.of(ColumnType.NUMERIC, Double.NaN, false),

                Arguments.of(ColumnType.INTEGER, 3, true),
                Arguments.of(ColumnType.INTEGER, 3L, true),
                Arguments.of(ColumnType.INTEGER, 3.0, true),
                Arguments.of(ColumnType.INTEGER, "3", true),
                Arguments.of(ColumnType.INTEGER, "+3", true),
                Arguments.of(ColumnType.INTEGER, 3.5, false),
                Arguments.of(ColumnType.INTEGER, "3.5", false),
                Arguments.of(ColumnType.INTEGER, "", false),
                Arguments.of(ColumnType.INTEGER, true, false),
                Arguments.of(ColumnType.INTEGER, " 3", false),
                Arguments.of(ColumnType.INTEGER, "3e2", false),

                Arguments.of(ColumnType.BOOLEAN, true, true),
                Arguments.of(ColumnType.BOOLEAN, false, true),
                Arguments.of(ColumnType.BOOLEAN, "true", true),
                Arguments.of(ColumnType.BOOLEAN, "TRUE", true),
                Arguments.of(ColumnType.BOOLEAN, "True", true),
                Arguments.of(ColumnType.BOOLEAN, "false", true),
                Arguments.of(ColumnType.BOOLEAN, 1, true),
                Arguments.of(ColumnType.BOOLEAN, 0, true),
                Arguments.of(ColumnType.BOOLEAN, "1", true),
                Arguments.of(ColumnType.BOOLEAN, "0", true),
                Arguments.of(ColumnType.BOOLEAN, "fal\u017Fe", false), // U+017F folds to "s" in equalsIgnoreCase
                Arguments.of(ColumnType.BOOLEAN, "TRUE\u212A", false),
                Arguments.of(ColumnType.BOOLEAN, "yes", false), // stored as true by BigQuery; rejected on purpose

                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13.562Z", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29 12:32:13", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29 12:32:13 UTC", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13 UTC", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+05:30", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29 12:30", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13.123456789Z", true),
                Arguments.of(ColumnType.TIMESTAMP, 1761741133562000L, true),
                Arguments.of(ColumnType.TIMESTAMP, "1761741133562000", true),
                Arguments.of(ColumnType.TIMESTAMP, "0001-01-01T00:00:00Z", true),
                Arguments.of(ColumnType.TIMESTAMP, "0099-12-31 23:59:59", true),
                Arguments.of(ColumnType.TIMESTAMP, "9999-12-31T23:59:59.999999Z", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13-18:00", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+18:00", true),
                Arguments.of(ColumnType.TIMESTAMP, "0000-01-01T00:00:00Z", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+05:99", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+05:60", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+19:00", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13-19:00", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+18:59", true),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13 Asia/Kolkata", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+0530", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13+05", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-1-5 1:2:3", false),
                Arguments.of(ColumnType.TIMESTAMP, "20261029T123213Z", false),
                Arguments.of(ColumnType.TIMESTAMP, "Oct 29 2026", false),
                Arguments.of(ColumnType.TIMESTAMP, "", false),
                Arguments.of(ColumnType.TIMESTAMP, "   ", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-02-30T00:00:00Z", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T24:00:00Z", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13 UTC+5", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29T12:32:13z", false),
                Arguments.of(ColumnType.TIMESTAMP, "2026-10-29t12:32:13Z", false),
                Arguments.of(ColumnType.TIMESTAMP, true, false),
                Arguments.of(ColumnType.TIMESTAMP, new HashMap<>(), false),

                Arguments.of(ColumnType.DATE, "1990-05-17", true),
                Arguments.of(ColumnType.DATE, "1990-5-7", true),
                Arguments.of(ColumnType.DATE, 7000, true),
                Arguments.of(ColumnType.DATE, "7000", true),
                Arguments.of(ColumnType.DATE, "0001-01-01", true),
                Arguments.of(ColumnType.DATE, "9999-12-31", true),
                Arguments.of(ColumnType.DATE, "0000-01-01", false),
                Arguments.of(ColumnType.DATE, "1990-02-30", false),
                Arguments.of(ColumnType.DATE, "17/05/1990", false),
                Arguments.of(ColumnType.DATE, "", false),
                Arguments.of(ColumnType.DATE, "1990-05-17T00:00:00Z", false),
                Arguments.of(ColumnType.DATE, "1990-05-17 00:00:00", false),

                Arguments.of(ColumnType.JSON, "{\"a\":1}", true),
                Arguments.of(ColumnType.JSON, "[1,2]", true),
                Arguments.of(ColumnType.JSON, "\"str\"", true),
                Arguments.of(ColumnType.JSON, "null", true),
                // BigQuery rejects a raw object or array, but the API serialises both to a JSON string first.
                Arguments.of(ColumnType.JSON, Collections.singletonMap("a", 1), true),
                Arguments.of(ColumnType.JSON, Arrays.asList(1, 2), true),
                Arguments.of(ColumnType.JSON, "bad{", false),
                Arguments.of(ColumnType.JSON, "", false),
                Arguments.of(ColumnType.JSON, 5, false),
                Arguments.of(ColumnType.JSON, true, false)
            );
        }

        @ParameterizedTest(name = "{0} accepts {1}: {2}")
        @MethodSource("cases")
        void acceptsWhatTheWarehouseAccepts(ColumnType type, Object value, boolean accepted) {
            assertEquals(accepted, BigQueryValidation.checkType("f", value, type) == null);
        }
    }

    @Nested
    class ValidateStorage {
        private final long now = Instant.parse("2026-10-29T00:00:00Z").toEpochMilli();
        private final BigQueryValidation.Rules rules = BigQueryValidation.Rules.required("a", "b")
            .columns(ColumnType.STRING, "a")
            .columns(ColumnType.NUMERIC, "n")
            .columns(ColumnType.TIMESTAMP, "t")
            .partition("t");

        @Test
        void requiredFieldsThatAreMissingNullOrEmptyAreRejected() {
            Map<String, Object> nulls = new HashMap<>();
            nulls.put("a", null);
            nulls.put("b", null);
            Map<String, Object> empty = new HashMap<>();
            empty.put("a", "");
            empty.put("b", "");
            for (Map<String, Object> record : Arrays.asList(new HashMap<String, Object>(), nulls, empty)) {
                List<String> errors = BigQueryValidation.validateStorage(record, rules, now);
                assertTrue(errors.containsAll(Arrays.asList("a is required", "b is required")), errors.toString());
            }
        }

        @Test
        void nullAndAbsentOptionalFieldsAreAccepted() {
            Map<String, Object> record = new HashMap<>();
            record.put("a", "x");
            record.put("b", "y");
            record.put("n", null);
            assertEquals(Collections.emptyList(), BigQueryValidation.validateStorage(record, rules, now));
        }

        @ParameterizedTest(name = "partition value {0} accepted: {1}")
        @CsvSource({
            "2016-10-15T00:00:00Z, false",
            "2016-11-15T00:00:00Z, true",
            "2027-10-01T00:00:00Z, true",
            "2027-12-01T00:00:00Z, false"
        })
        void partitionWindow(String t, boolean accepted) {
            Map<String, Object> record = new HashMap<>();
            record.put("a", "x");
            record.put("b", "y");
            record.put("t", t);
            assertEquals(accepted, BigQueryValidation.validateStorage(record, rules, now).isEmpty());
        }

        @Test
        void numericPartitionValuesAreNotWindowChecked() {
            // The unit of a numeric timestamp is ambiguous, so only string timestamps are window-checked.
            for (Object t : Arrays.<Object>asList(1L, "1", 99999999999999999L)) {
                Map<String, Object> record = new HashMap<>();
                record.put("a", "x");
                record.put("b", "y");
                record.put("t", t);
                assertEquals(Collections.emptyList(), BigQueryValidation.validateStorage(record, rules, now));
            }
        }

        @Test
        void partitionOffsetIsApplied() {
            Map<String, Object> record = new HashMap<>();
            record.put("a", "x");
            record.put("b", "y");
            // The window ends 2027-10-29T06:00Z: 06:30 +01:00 is 05:30 UTC, inside; read without the offset it is outside.
            record.put("t", "2027-10-29T06:30:00+01:00");
            assertEquals(Collections.emptyList(), BigQueryValidation.validateStorage(record, rules, now));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Models {

        private <T> T event(T model) {
            if (model instanceof AccountEvent) {
                AccountEvent e = (AccountEvent) model;
                e.setAccountId("acc"); e.setWorkspaceId("ws"); e.setUserId("1");
                e.setEventId("e1"); e.setEventName("Something"); e.setEventTime(NOW);
            } else if (model instanceof DepositEvent) {
                DepositEvent e = (DepositEvent) model;
                e.setAccountId("acc"); e.setWorkspaceId("ws"); e.setUserId("1");
                e.setEventId("e1"); e.setEventName("Something"); e.setEventTime(NOW);
                e.setAmount(new BigDecimal("10")); e.setPaymentMethod("credit_card"); e.setTransactionId("t1");
            } else if (model instanceof WithdrawEvent) {
                WithdrawEvent e = (WithdrawEvent) model;
                e.setAccountId("acc"); e.setWorkspaceId("ws"); e.setUserId("1");
                e.setEventId("e1"); e.setEventName("Something"); e.setEventTime(NOW);
                e.setAmount(new BigDecimal("10")); e.setPaymentMethod("credit_card"); e.setTransactionId("t1");
            } else if (model instanceof GamingActivityEvent) {
                GamingActivityEvent e = (GamingActivityEvent) model;
                e.setAccountId("acc"); e.setWorkspaceId("ws"); e.setUserId("1");
                e.setEventId("e1"); e.setEventName("Something"); e.setEventTime(NOW);
                e.setGameId("15466"); e.setGameTitle("Aviator");
            } else if (model instanceof WalletBalanceEvent) {
                WalletBalanceEvent e = (WalletBalanceEvent) model;
                e.setAccountId("acc"); e.setWorkspaceId("ws"); e.setUserId("1");
                e.setEventId("e1"); e.setEventName("Something"); e.setEventTime(NOW);
                e.setCurrency("INR"); e.setWalletType("main");
            } else if (model instanceof ReferFriendEvent) {
                ReferFriendEvent e = (ReferFriendEvent) model;
                e.setAccountId("acc"); e.setWorkspaceId("ws"); e.setUserId("1");
                e.setEventId("e1"); e.setEventName("Something"); e.setEventTime(NOW);
            }
            return model;
        }

        Stream<Arguments> models() {
            return Stream.of(
                Arguments.of((Supplier<Object>) AccountEvent::new, "Account"),
                Arguments.of((Supplier<Object>) DepositEvent::new, "Deposit"),
                Arguments.of((Supplier<Object>) WithdrawEvent::new, "Withdraw"),
                Arguments.of((Supplier<Object>) GamingActivityEvent::new, "Gaming Activity"),
                Arguments.of((Supplier<Object>) WalletBalanceEvent::new, "Wallet Balance"),
                Arguments.of((Supplier<Object>) ReferFriendEvent::new, "Refer Friend")
            );
        }

        private ValidationResult validate(Object model) {
            try {
                return (ValidationResult) model.getClass().getMethod("validate").invoke(model);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        private void setCategory(Object model, String category) {
            try {
                model.getClass().getMethod("setEventCategory", String.class).invoke(model, category);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        private Object getCategory(Object model) {
            try {
                return model.getClass().getMethod("getEventCategory").invoke(model);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        @ParameterizedTest(name = "{1} rejects a missing event_category and does not fill one in")
        @MethodSource("models")
        void rejectsMissingCategory(Supplier<Object> factory, String category) {
            Object model = event(factory.get());
            ValidationResult result = validate(model);
            assertFalse(result.isValid());
            assertTrue(result.getErrors().contains("event_category is required"), result.getErrors().toString());
            assertNull(getCategory(model));
        }

        @ParameterizedTest(name = "{1} accepts a complete record")
        @MethodSource("models")
        void acceptsCompleteRecord(Supplier<Object> factory, String category) {
            Object model = event(factory.get());
            setCategory(model, category);
            ValidationResult result = validate(model);
            assertEquals(Collections.emptyList(), result.getErrors());
            assertTrue(result.isValid());
        }

        @ParameterizedTest(name = "{1} keeps rejecting a wrong event_category")
        @MethodSource("models")
        void rejectsWrongCategory(Supplier<Object> factory, String category) {
            Object model = event(factory.get());
            setCategory(model, "Something Else");
            assertFalse(validate(model).isValid());
        }

        @Test
        void eventTimeOutsideThePartitionWindowIsRejected() {
            AccountEvent e = event(new AccountEvent());
            e.setEventCategory("Account");
            e.setEventTime("2012-01-01T00:00:00Z");
            assertEquals(
                Collections.singletonList(
                    "event_time must be within the last 10 years and no more than 1 year in the future"),
                e.validate().getErrors());
        }

        @Test
        void nonFiniteNumericIsRejected() {
            ReferFriendEvent e = event(new ReferFriendEvent());
            e.setEventCategory("Refer Friend");
            e.setRefereeFirstDeposit(Double.NaN);
            assertTrue(e.validate().getErrors().contains("referee_first_deposit must be a finite number"));
        }

        @Test
        void walletPayloadIsRejectedForItsMissingEventCategory() {
            WalletBalanceEvent e = new WalletBalanceEvent();
            e.setWorkspaceId("ws_example_001");
            e.setBlockedAmount(new BigDecimal("100.0"));
            e.setAccountId("acc_example_001");
            e.setCurrentCashBalance(new BigDecimal("3065.5"));
            e.setEventId("da49a497-a41f-4e37-b7b2-e10ad0c0ed4d");
            e.setCurrency("INR");
            e.setWalletType("main");
            e.setUserId("1");
            e.setCurrentBonusBalance(new BigDecimal("20.0"));
            e.setEventTime(NOW);
            e.setEventName("Balance Update");
            e.setCurrentTotalBalance(new BigDecimal("3085.5"));
            assertEquals(Collections.singletonList("event_category is required"), e.validate().getErrors());
        }

        private CustomerProfile customer() {
            return new CustomerProfile("acc", "ws", "1", "u", "u@example.com", NOW);
        }

        @Test
        void customerWithEmptyStringTimestampIsRejected() {
            CustomerProfile c = customer();
            c.setSelfExclusionCreatedTime("");
            assertTrue(c.validate().getErrors().contains(
                "self_exclusion_created_time must be a valid ISO 8601 date-time"));
        }

        @Test
        void customerCreationTimestampOutsideThePartitionWindowIsRejected() {
            CustomerProfile c = customer();
            c.setCreationTimestamp("2012-05-01T00:00:00Z");
            assertTrue(c.validate().getErrors().contains(
                "creation_timestamp must be within the last 10 years and no more than 1 year in the future"));
        }

        @Test
        void customerCustomDataObjectIsAccepted() {
            CustomerProfile c = customer();
            c.setCustomData(Collections.singletonMap("a", 1));
            assertEquals(Collections.emptyList(), c.validate().getErrors());
        }

        @Test
        void customerExtDataMayBeAnObjectOrAJsonStringButNotOtherText() {
            ExtendedAttributesEvent e = new ExtendedAttributesEvent();
            e.setAccountId("acc");
            e.setWorkspaceId("ws");
            e.setUserId("1");
            e.setListName("l");
            e.setExtData(Collections.singletonMap("a", "1"));
            assertTrue(e.validate().isValid());
            e.setExtData("{\"a\":1}");
            assertTrue(e.validate().isValid());
            e.setExtData("{bad");
            assertEquals(Collections.singletonList("ext_data must be an object or a valid JSON string"),
                e.validate().getErrors());
        }
    }
}
