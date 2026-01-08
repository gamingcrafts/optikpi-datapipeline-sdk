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
            // PHP 7.2+ has hash_hkdf function
            if (function_exists('hash_hkdf')) {
                // hash_hkdf returns hex string, convert to binary for consistency
                $derivedKeyHex = hash_hkdf($algorithm, $ikm, $length, $infoBuffer, $salt);
                return hex2bin($derivedKeyHex);
            } else {
                // Fallback implementation for older PHP versions
                return self::hkdfFallback($algorithm, $ikm, $salt, $infoBuffer, $length);
            }
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
        
        // Extract
        if (empty($salt)) {
            $salt = str_repeat("\0", $hashLen);
        }
        $prk = hash_hmac($algo, $ikm, $salt, true);
        
        // Expand
        $okm = '';
        $t = '';
        for ($i = 1; strlen($okm) < $length; $i++) {
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
            
            $dataString = is_string($data) ? $data : json_encode($data, JSON_UNESCAPED_SLASHES);
            $signature = hash_hmac($algorithm, $dataString, $derivedKey);
            
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

