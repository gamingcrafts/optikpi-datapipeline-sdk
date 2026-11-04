package com.optikpi.datapipeline.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class PurchaseFunnelEventTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long NOW_MS = Instant.parse("2026-11-04T10:00:00Z").toEpochMilli();

    /** Shared with the API's tests: the client and the server must agree on every shape. */
    static JsonNode shapes() throws IOException {
        Path path = Paths.get("..", "testdata", "purchase-funnel-shapes.json");
        return MAPPER.readTree(Files.readString(path));
    }

    static ObjectNode buildRecord(JsonNode shapes, JsonNode testCase) {
        String from = testCase.path("from").asText("base");
        ObjectNode record = ((ObjectNode) shapes.get("full".equals(from) ? "full" : "base")).deepCopy();
        for (JsonNode field : testCase.path("unset")) {
            record.remove(field.asText());
        }
        Iterator<Map.Entry<String, JsonNode>> set = testCase.path("set").fields();
        while (set.hasNext()) {
            Map.Entry<String, JsonNode> entry = set.next();
            record.set(entry.getKey(), entry.getValue().deepCopy());
        }
        return record;
    }

    @TestFactory
    List<DynamicTest> validateJsonMatchesSharedShapes() throws IOException {
        JsonNode shapes = shapes();
        List<DynamicTest> tests = new ArrayList<>();
        for (JsonNode testCase : shapes.get("cases")) {
            tests.add(dynamicTest(testCase.get("name").asText(), () -> {
                ValidationResult result = PurchaseFunnelEvent.validateJson(buildRecord(shapes, testCase), NOW_MS);
                assertEquals(testCase.get("valid").asBoolean(), result.isValid(), result.getErrors().toString());
                if (!testCase.get("valid").asBoolean()) {
                    String field = testCase.get("errorField").asText();
                    assertTrue(result.getErrors().stream().anyMatch(e -> e.contains(field)), result.getErrors().toString());
                }
            }));
        }
        return tests;
    }

    @Test
    void windowDefaultsToNow() throws IOException {
        ObjectNode record = ((ObjectNode) shapes().get("base")).deepCopy();
        record.put("event_time", Instant.now().minus(1, ChronoUnit.MINUTES).toString());
        assertTrue(PurchaseFunnelEvent.validateJson(record).isValid());
        record.put("event_time", Instant.now().minus(11 * 365, ChronoUnit.DAYS).toString());
        assertFalse(PurchaseFunnelEvent.validateJson(record).isValid());
    }

    @Test
    void eventCategoryIsNotFilledIn() {
        PurchaseFunnelEvent event = new PurchaseFunnelEvent("acc_example_001", "ws_example_001", "user_example_001",
            null, "checkout_started", "evt_example_001", "2026-11-04T10:00:00Z", "fs_example_001", "checkout");
        assertNull(event.getEventCategory());
        assertTrue(event.validate(NOW_MS).getErrors().contains("event_category is required"));
        event.setEventCategory(PurchaseFunnelEvent.EVENT_CATEGORY);
        assertTrue(event.validate(NOW_MS).isValid(), event.validate(NOW_MS).getErrors().toString());
    }

    @Test
    void constantsInContractOrder() {
        assertEquals(List.of("start", "browse", "configure", "cart", "checkout", "payment", "payment_failed",
            "complete", "failed", "cancelled", "exit", "stalled", "abandoned"), PurchaseFunnelEvent.FUNNEL_STAGES);
        assertEquals(List.of("payment_declined", "insufficient_funds", "provider_timeout", "limit_reached",
            "kyc_required", "geo_blocked", "draw_closed", "technical_error", "user_cancelled"),
            PurchaseFunnelEvent.FAILURE_REASONS);
    }
}
