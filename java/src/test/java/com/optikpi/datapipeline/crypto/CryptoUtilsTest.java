package com.optikpi.datapipeline.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CryptoUtilsTest {

    @Test
    void testDeriveKey() {
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        String info = "test_info";
        
        byte[] key = CryptoUtils.deriveKey(authToken, accountId, workspaceId, info);
        
        assertNotNull(key);
        assertEquals(32, key.length); // Default key length
    }

    @Test
    void testDeriveKeyConsistency() {
        // Test that same inputs produce same key (deterministic)
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        String info = "hmac-signing";
        
        byte[] key1 = CryptoUtils.deriveKey(authToken, accountId, workspaceId, info);
        byte[] key2 = CryptoUtils.deriveKey(authToken, accountId, workspaceId, info);
        
        assertArrayEquals(key1, key2);
    }

    @Test
    void testDeriveKeyWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.deriveKey(null, "account", "workspace", "info");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.deriveKey("token", null, "workspace", "info");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.deriveKey("token", "account", null, "info");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.deriveKey("token", "account", "workspace", null);
        });
    }

    @Test
    void testGenerateHmacSignature() {
        String data = "test_data";
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String signature = CryptoUtils.generateHmacSignature(data, authToken, accountId, workspaceId);
        
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        assertTrue(signature.matches("[a-f0-9]+")); // Hex string
        assertEquals(64, signature.length()); // SHA256 produces 64 hex characters
    }

    @Test
    void testGenerateHmacSignatureWithJsonString() {
        // Test with JSON string (real-world usage)
        String jsonData = "{\"account_id\":\"acc123\",\"user_id\":\"user456\"}";
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String signature = CryptoUtils.generateHmacSignature(jsonData, authToken, accountId, workspaceId);
        
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
        assertEquals(64, signature.length());
    }

    @Test
    void testGenerateHmacSignatureConsistency() {
        // Test that same inputs produce same signature (deterministic)
        String data = "test_data";
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String signature1 = CryptoUtils.generateHmacSignature(data, authToken, accountId, workspaceId);
        String signature2 = CryptoUtils.generateHmacSignature(data, authToken, accountId, workspaceId);
        
        assertEquals(signature1, signature2);
    }

    @Test
    void testGenerateHmacSignatureWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.generateHmacSignature(null, "token", "account", "workspace");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.generateHmacSignature("data", null, "account", "workspace");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.generateHmacSignature("data", "token", null, "workspace");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.generateHmacSignature("data", "token", "account", null);
        });
    }

    @Test
    void testValidateHmacSignature() {
        String data = "test_data";
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String signature = CryptoUtils.generateHmacSignature(data, authToken, accountId, workspaceId);
        
        assertTrue(CryptoUtils.validateHmacSignature(data, signature, authToken, accountId, workspaceId));
        assertFalse(CryptoUtils.validateHmacSignature(data, "invalid_signature", authToken, accountId, workspaceId));
        assertFalse(CryptoUtils.validateHmacSignature("different_data", signature, authToken, accountId, workspaceId));
    }

    @Test
    void testValidateHmacSignatureWithInvalidInput() {
        String data = "test_data";
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        assertFalse(CryptoUtils.validateHmacSignature(data, "invalid", authToken, accountId, workspaceId));
        assertFalse(CryptoUtils.validateHmacSignature(data, null, authToken, accountId, workspaceId));
    }

    @Test
    void testValidateHmacSignatureCaseSensitive() {
        String data = "test_data";
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String signature = CryptoUtils.generateHmacSignature(data, authToken, accountId, workspaceId);
        String upperSignature = signature.toUpperCase();
        
        // HMAC signatures are case-sensitive (lowercase hex)
        assertFalse(CryptoUtils.validateHmacSignature(data, upperSignature, authToken, accountId, workspaceId));
    }

    @Test
    void testDifferentInputsProduceDifferentSignatures() {
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String sig1 = CryptoUtils.generateHmacSignature("data1", authToken, accountId, workspaceId);
        String sig2 = CryptoUtils.generateHmacSignature("data2", authToken, accountId, workspaceId);
        
        assertNotEquals(sig1, sig2);
    }

    @Test
    void testRealWorldExample() {
        // Test with actual values from debugging session
        String authToken = "055da5b3ac7c932573cc1ffbf6a21d9c";
        String accountId = "688743ad724a144fc1e051d9";
        String workspaceId = "67bed03cc39204069f2f0366";
        String jsonData = "{\"account_id\":\"688743ad724a144fc1e051d9\",\"workspace_id\":\"67bed03cc39204069f2f0366\",\"user_id\":\"user123456\",\"event_category\":\"Account\",\"event_name\":\"Player Registration\",\"event_id\":\"evt_123456789\",\"event_time\":\"2024-01-15T10:30:00Z\",\"device\":\"desktop\",\"status\":\"verified\",\"affiliate_id\":\"aff_123\",\"partner_id\":\"partner_456\",\"campaign_code\":\"CAMPAIGN_001\",\"reason\":\"Registration completed successfully\"}";
        
        // Expected signature from Python/Node.js
        String expectedSignature = "55f08947ac0341c22a4b735a0bc4bae50454485d945dbfe4ead7b45b93406252";
        
        String actualSignature = CryptoUtils.generateHmacSignature(jsonData, authToken, accountId, workspaceId);
        
        assertEquals(expectedSignature, actualSignature, "Java signature should match Python/Node.js signature");
    }
}