package com.optikpi.datapipeline.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cryptographic utilities for HMAC and HKDF operations
 */
public class CryptoUtils {
    
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int KEY_LENGTH = 32;
    
    /**
     * Derives a cryptographic key using HKDF (HMAC-based Key Derivation Function)
     */
    public static byte[] deriveKey(String authToken, String accountId, String workspaceId, String info) {
        if (authToken == null || accountId == null || workspaceId == null || info == null) {
            throw new IllegalArgumentException("All parameters are required for key derivation");
        }
        
        try {
            byte[] ikm = authToken.getBytes(StandardCharsets.UTF_8);
            byte[] salt = (accountId + workspaceId).getBytes(StandardCharsets.UTF_8);
            byte[] infoBytes = info.getBytes(StandardCharsets.UTF_8);
            
            return hkdf(ikm, salt, infoBytes, KEY_LENGTH);
        } catch (Exception e) {
            throw new RuntimeException("HKDF key derivation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates HMAC signature using HKDF-derived key
     */
    public static String generateHmacSignature(String data, String authToken, String accountId, String workspaceId) {
        if (data == null || authToken == null || accountId == null || workspaceId == null) {
            throw new IllegalArgumentException("All parameters are required for HMAC signature generation");
        }
        
        try {
            String info = "hmac-signing";
            byte[] derivedKey = deriveKey(authToken, accountId, workspaceId, info);
            
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(derivedKey, HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            
            byte[] signature = mac.doFinal(dataBytes);
            return bytesToHex(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC signature generation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates HMAC signature
     */
    public static boolean validateHmacSignature(String data, String signature, String authToken, String accountId, String workspaceId) {
        try {
            String expectedSignature = generateHmacSignature(data, authToken, accountId, workspaceId);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * HKDF implementation matching Node.js crypto.hkdfSync behavior
     */
    private static byte[] hkdf(byte[] ikm, byte[] salt, byte[] info, int length) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] prk = extract(ikm, salt);
        return expand(prk, info, length);
    }
    
    /**
     * CRITICAL: Node.js uses salt as HMAC key and ikm as message (backwards from RFC 5869)
     */
    private static byte[] extract(byte[] ikm, byte[] salt) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(ikm, HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        return mac.doFinal(salt);
    }
    
    private static byte[] expand(byte[] prk, byte[] info, int length) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(prk, HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        
        byte[] result = new byte[length];
        byte[] t = new byte[0];
        int offset = 0;
        int counter = 1;
        
        while (offset < length) {
            mac.update(t);
            mac.update(info);
            mac.update((byte) counter);
            t = mac.doFinal();
            
            int copyLength = Math.min(t.length, length - offset);
            System.arraycopy(t, 0, result, offset, copyLength);
            offset += copyLength;
            counter++;
        }
        
        return result;
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}