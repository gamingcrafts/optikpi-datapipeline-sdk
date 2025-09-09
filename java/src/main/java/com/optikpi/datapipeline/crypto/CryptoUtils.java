package com.optikpi.datapipeline.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Cryptographic utilities for HMAC and HKDF operations
 */
public class CryptoUtils {
    
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int KEY_LENGTH = 32;
    
    /**
     * Derives a cryptographic key using HKDF (HMAC-based Key Derivation Function)
     * @param authToken Authentication token
     * @param accountId Account ID
     * @param workspaceId Workspace ID
     * @param info Context string for key derivation
     * @return Derived key
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
     * @param data Data to sign
     * @param authToken Authentication token
     * @param accountId Account ID
     * @param workspaceId Workspace ID
     * @return HMAC signature in hex format
     */
    public static String generateHmacSignature(Object data, String authToken, String accountId, String workspaceId) {
        if (data == null || authToken == null || accountId == null || workspaceId == null) {
            throw new IllegalArgumentException("All parameters are required for HMAC signature generation");
        }
        
        try {
            String info = "hmac-signing";
            byte[] derivedKey = deriveKey(authToken, accountId, workspaceId, info);
            
            String dataString = data instanceof String ? (String) data : data.toString();
            byte[] dataBytes = dataString.getBytes(StandardCharsets.UTF_8);
            
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
     * @param data Data that was signed
     * @param signature HMAC signature to validate
     * @param authToken Authentication token
     * @param accountId Account ID
     * @param workspaceId Workspace ID
     * @return True if signature is valid
     */
    public static boolean validateHmacSignature(Object data, String signature, String authToken, String accountId, String workspaceId) {
        try {
            String expectedSignature = generateHmacSignature(data, authToken, accountId, workspaceId);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * HKDF implementation
     */
    private static byte[] hkdf(byte[] ikm, byte[] salt, byte[] info, int length) throws NoSuchAlgorithmException, InvalidKeyException {
        // Extract
        byte[] prk = extract(salt, ikm);
        
        // Expand
        return expand(prk, info, length);
    }
    
    private static byte[] extract(byte[] salt, byte[] ikm) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(salt, HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        return mac.doFinal(ikm);
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
