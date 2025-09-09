package com.optikpi.datapipeline.crypto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void testGenerateHmacSignatureWithObject() {
        Object data = new TestObject("test_value");
        String authToken = "test_auth_token";
        String accountId = "test_account";
        String workspaceId = "test_workspace";
        
        String signature = CryptoUtils.generateHmacSignature(data, authToken, accountId, workspaceId);
        
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    void testGenerateHmacSignatureWithNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.generateHmacSignature(null, "token", "account", "workspace");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptoUtils.generateHmacSignature("data", null, "account", "workspace");
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

    // Helper class for testing
    private static class TestObject {
        private final String value;
        
        public TestObject(String value) {
            this.value = value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
}
