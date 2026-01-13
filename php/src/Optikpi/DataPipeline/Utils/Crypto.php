<?php

namespace Optikpi\DataPipeline\Utils;

/**
 * Cryptographic utilities for HMAC and HKDF operations
 */
class Crypto
{
    /**
     * Derives a cryptographic key using HKDF (HMAC-based Key Derivation Function)
     *
     * @param string $authToken Authentication token
     * @param string $accountId Account ID
     * @param string $workspaceId Workspace ID
     * @param string $info Context string for key derivation
     * @param string $algorithm Hash algorithm (default: sha256)
     * @param int $length Output key length in bytes (default: 32)
     * @return string Derived key as binary string
     * @throws \Exception If key derivation fails
     */
    public static function deriveKey(
        string $authToken,
        string $accountId,
        string $workspaceId,
        string $info,
        string $algorithm = 'sha256',
        int $length = 32
    ): string {
        if (empty($authToken) || empty($accountId) || empty($workspaceId) || empty($info)) {
            throw new \Exception('All parameters are required for key derivation');
        }

        $ikm = $authToken;
        $salt = $accountId . $workspaceId;
        $infoBuffer = $info;

        try {
            // CRITICAL: PHP's hash_hkdf follows RFC 5869, but Node.js crypto.hkdfSync
            // uses a different parameter order (ikm as key, salt as message).
            // We must use the fallback implementation to match Node.js behavior.
            return self::hkdfFallback($algorithm, $ikm, $salt, $infoBuffer, $length);
        } catch (\Exception $e) {
            throw new \Exception("HKDF key derivation failed: " . $e->getMessage());
        }
    }

    /**
     * Fallback HKDF implementation for PHP < 7.2
     *
     * @param string $algo Hash algorithm
     * @param string $ikm Input keying material
     * @param string $salt Salt
     * @param string $info Context
     * @param int $length Output length
     * @return string Derived key
     */
    private static function hkdfFallback(
        string $algo,
        string $ikm,
        string $salt,
        string $info,
        int $length
    ): string {
        $hashLen = strlen(hash($algo, '', true));
        
        // Extract step - CRITICAL: Node.js uses ikm as HMAC key and salt as message
        // This is backwards from RFC 5869, but matches Node.js crypto.hkdfSync behavior
        if (empty($salt)) {
            $salt = str_repeat("\0", $hashLen);
        }
        // PRK = HMAC-Hash(ikm, salt) - ikm is key, salt is message
        $prk = hash_hmac($algo, $salt, $ikm, true);
        
        // Expand step
        $okm = '';
        $t = '';
        $n = (int)ceil($length / $hashLen);
        for ($i = 1; $i <= $n; $i++) {
            $t = hash_hmac($algo, $t . $info . chr($i), $prk, true);
            $okm .= $t;
        }
        
        return substr($okm, 0, $length);
    }

    /**
     * Generates HMAC signature using HKDF-derived key
     *
     * @param mixed $data Data to sign (array or string)
     * @param string $authToken Authentication token
     * @param string $accountId Account ID
     * @param string $workspaceId Workspace ID
     * @param string $algorithm HMAC algorithm (default: sha256)
     * @return string HMAC signature in hex format
     * @throws \Exception If signature generation fails
     */
    public static function generateHmacSignature(
        $data,
        string $authToken,
        string $accountId,
        string $workspaceId,
        string $algorithm = 'sha256'
    ): string {
        if (empty($data) || empty($authToken) || empty($accountId) || empty($workspaceId)) {
            throw new \Exception('All parameters are required for HMAC signature generation');
        }

        try {
            $info = 'hmac-signing';
            $derivedKey = self::deriveKey($authToken, $accountId, $workspaceId, $info, $algorithm);
            
            // CRITICAL: Match JavaScript JSON.stringify behavior exactly
            // - Compact format (no spaces): use JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE
            // - No key sorting (preserve insertion order)
            // - Same as Python: json.dumps(data, separators=(',', ':'))
            $dataString = is_string($data) ? $data : json_encode($data, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
            $signature = hash_hmac($algorithm, $dataString, $derivedKey, false); // false = return hex string
            
            return $signature;
        } catch (\Exception $e) {
            throw new \Exception("HMAC signature generation failed: " . $e->getMessage());
        }
    }

    /**
     * Validates HMAC signature
     *
     * @param mixed $data Data that was signed
     * @param string $signature HMAC signature to validate
     * @param string $authToken Authentication token
     * @param string $accountId Account ID
     * @param string $workspaceId Workspace ID
     * @param string $algorithm HMAC algorithm (default: sha256)
     * @return bool True if signature is valid
     */
    public static function validateHmacSignature(
        $data,
        string $signature,
        string $authToken,
        string $accountId,
        string $workspaceId,
        string $algorithm = 'sha256'
    ): bool {
        try {
            $expectedSignature = self::generateHmacSignature($data, $authToken, $accountId, $workspaceId, $algorithm);
            return hash_equals($expectedSignature, $signature);
        } catch (\Exception $e) {
            return false;
        }
    }
}