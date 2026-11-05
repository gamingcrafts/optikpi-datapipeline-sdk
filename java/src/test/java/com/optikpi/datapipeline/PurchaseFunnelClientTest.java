package com.optikpi.datapipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/** Sends to a local HTTP server and checks the path and method the client used. */
class PurchaseFunnelClientTest {

    private HttpServer server;
    private final List<String> requests = new CopyOnWriteArrayList<>();
    private DataPipelineClient client;

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            byte[] body = "{\"message\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        ClientConfig config = new ClientConfig("test-token", "acc_example_001", "ws_example_001");
        config.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
        client = new DataPipelineClient(config);
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    @Test
    void sendPurchaseFunnelEventPostsToItsEndpoint() {
        ApiResponse<Object> response = client.sendPurchaseFunnelEvent(Map.of("event_id", "evt_example_001"));
        assertTrue(response.isSuccess());
        assertEquals(List.of("POST /events/purchase-funnel"), requests);
    }

    @Test
    void sendBatchSendsPurchaseFunnelEvents() {
        BatchData batch = new BatchData();
        batch.setPurchaseFunnelEvents(List.of(Map.of("event_id", "evt_example_001")));
        BatchResponse response = client.sendBatch(batch);
        assertEquals(List.of("POST /events/purchase-funnel"), requests);
        assertTrue(response.getPurchaseFunnelEvents().isSuccess());
    }
}
