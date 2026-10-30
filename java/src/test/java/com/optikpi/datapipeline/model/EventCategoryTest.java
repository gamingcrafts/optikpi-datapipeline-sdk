package com.optikpi.datapipeline.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class EventCategoryTest {

    /**
     * A missing category is rejected, the model's own category is accepted, and any other category is rejected.
     * {@code event} has every other required field set.
     */
    private static <T> void assertCategoryRequired(T event, String category, BiConsumer<T, String> setCategory,
                                                   Function<T, ValidationResult> validate) {
        assertTrue(validate.apply(event).getErrors().contains("event_category is required"),
            "missing category must be rejected");

        setCategory.accept(event, "");
        assertEquals(List.of("event_category is required"),
            validate.apply(event).getErrors().stream().filter(e -> e.startsWith("event_category")).collect(Collectors.toList()),
            "empty category gives only the required error");

        setCategory.accept(event, category);
        assertFalse(validate.apply(event).getErrors().contains("event_category is required"),
            "own category must be accepted");

        setCategory.accept(event, "Other");
        assertFalse(validate.apply(event).isValid(), "wrong category must be rejected");
    }

    @Test
    void accountEvent() {
        AccountEvent e = new AccountEvent();
        e.setAccountId("acc_example_001"); e.setWorkspaceId("ws_example_001"); e.setUserId("1");
        e.setEventId("e1"); e.setEventName("Login"); e.setEventTime("2026-01-15T10:30:00Z");
        assertCategoryRequired(e, "Account", AccountEvent::setEventCategory, AccountEvent::validate);
    }

    @Test
    void depositEvent() {
        DepositEvent e = new DepositEvent();
        e.setAccountId("acc_example_001"); e.setWorkspaceId("ws_example_001"); e.setUserId("1");
        e.setEventId("e1"); e.setEventName("Deposit"); e.setEventTime("2026-01-15T10:30:00Z");
        assertCategoryRequired(e, "Deposit", DepositEvent::setEventCategory, DepositEvent::validate);
    }

    @Test
    void withdrawEvent() {
        WithdrawEvent e = new WithdrawEvent();
        e.setAccountId("acc_example_001"); e.setWorkspaceId("ws_example_001"); e.setUserId("1");
        e.setEventId("e1"); e.setEventName("Withdraw"); e.setEventTime("2026-01-15T10:30:00Z");
        assertCategoryRequired(e, "Withdraw", WithdrawEvent::setEventCategory, WithdrawEvent::validate);
    }

    @Test
    void gamingActivityEvent() {
        GamingActivityEvent e = new GamingActivityEvent();
        e.setAccountId("acc_example_001"); e.setWorkspaceId("ws_example_001"); e.setUserId("1");
        e.setEventId("e1"); e.setEventName("Bet Placed"); e.setEventTime("2026-01-15T10:30:00Z");
        assertCategoryRequired(e, "Gaming Activity", GamingActivityEvent::setEventCategory, GamingActivityEvent::validate);
    }

    @Test
    void walletBalanceEvent() {
        WalletBalanceEvent e = new WalletBalanceEvent();
        e.setAccountId("acc_example_001"); e.setWorkspaceId("ws_example_001"); e.setUserId("1");
        e.setEventId("e1"); e.setEventName("Balance Update"); e.setEventTime("2026-01-15T10:30:00Z");
        assertCategoryRequired(e, "Wallet Balance", WalletBalanceEvent::setEventCategory, WalletBalanceEvent::validate);
    }

    @Test
    void referFriendEvent() {
        ReferFriendEvent e = new ReferFriendEvent();
        e.setAccountId("acc_example_001"); e.setWorkspaceId("ws_example_001"); e.setUserId("1");
        e.setEventId("e1"); e.setEventName("Referral"); e.setEventTime("2026-01-15T10:30:00Z");
        assertCategoryRequired(e, "Refer Friend", ReferFriendEvent::setEventCategory, ReferFriendEvent::validate);
    }
}
