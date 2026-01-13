<?php

namespace Optikpi\DataPipeline\Core;

use Optikpi\DataPipeline\Utils\Crypto;

/**
 * Data Pipeline API Client
 * Main client class for interacting with the Optikpi Data Pipeline API
 */
class DataPipelineClient
{
    private $config;
    private $baseURL;
    private $timeout;
    private $retries;
    private $retryDelay;

    /**
     * Constructor
     *
     * @param array $config Configuration array
     * @throws \Exception If required configuration is missing
     */
    public function __construct(array $config = [])
    {
        $this->config = array_merge([
            'baseURL' => null,
            'authToken' => null,
            'accountId' => null,
            'workspaceId' => null,
            'timeout' => 30000,
            'retries' => 3,
            'retryDelay' => 1000
        ], $config);

        $this->baseURL = $this->config['baseURL'];
        $this->timeout = $this->config['timeout'];
        $this->retries = $this->config['retries'];
        $this->retryDelay = $this->config['retryDelay'];

        $this->validateConfig();
    }

    /**
     * Validates the client configuration
     *
     * @throws \Exception If required configuration is missing
     */
    private function validateConfig(): void
    {
        if (empty($this->config['authToken'])) {
            throw new \Exception('authToken is required');
        }
        if (empty($this->config['accountId'])) {
            throw new \Exception('accountId is required');
        }
        if (empty($this->config['workspaceId'])) {
            throw new \Exception('workspaceId is required');
        }
    }

    /**
     * Makes an HTTP request with authentication
     *
     * @param string $method HTTP method
     * @param string $endpoint API endpoint
     * @param mixed $data Request data
     * @return array Response array with success, status, data, and timestamp
     */
    private function makeRequest(string $method, string $endpoint, $data = null): array
    {
        $url = rtrim($this->baseURL, '/') . '/' . ltrim($endpoint, '/');
        
        $ch = curl_init();
        
        $headers = [
            'Content-Type: application/json',
            'User-Agent: Optikpi-DataPipeline-SDK/1.0.0'
        ];

        if ($data !== null && $method !== 'GET') {
            // CRITICAL: Generate HMAC signature from data object first (before JSON encoding)
            // This matches the JavaScript/Python/Java implementations
            $hmacSignature = Crypto::generateHmacSignature(
                $data,
                $this->config['authToken'],
                $this->config['accountId'],
                $this->config['workspaceId']
            );
            
            // Then convert to JSON string for request body - must match signature encoding
            // Use same format as signature: compact, no key sorting
            $dataString = is_string($data) ? $data : json_encode($data, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);

            $headers[] = 'x-optikpi-token: ' . $this->config['authToken'];
            $headers[] = 'x-optikpi-account-id: ' . $this->config['accountId'];
            $headers[] = 'x-optikpi-workspace-id: ' . $this->config['workspaceId'];
            $headers[] = 'x-hmac-signature: ' . $hmacSignature;
            $headers[] = 'x-hmac-algorithm: sha256';

            curl_setopt($ch, CURLOPT_POSTFIELDS, $dataString);
        }

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_TIMEOUT_MS, $this->timeout);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT_MS, $this->timeout);

        if ($method === 'POST') {
            curl_setopt($ch, CURLOPT_POST, true);
        }

        $response = $this->retryRequest($ch);
        curl_close($ch);

        return $response;
    }

    /**
     * Retries a failed request
     *
     * @param resource $ch cURL handle
     * @param int $retryCount Current retry count
     * @return array Response array
     */
    private function retryRequest($ch, int $retryCount = 0): array
    {
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);

        if ($error) {
            if ($retryCount < $this->retries) {
                usleep($this->retryDelay * 1000 * ($retryCount + 1));
                return $this->retryRequest($ch, $retryCount + 1);
            }
            // Return error structure matching JavaScript
            return [
                'success' => false,
                'error' => $error,
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }

        if ($httpCode >= 500 && $retryCount < $this->retries) {
            usleep($this->retryDelay * 1000 * ($retryCount + 1));
            return $this->retryRequest($ch, $retryCount + 1);
        }

        $responseData = json_decode($response, true);
        if (json_last_error() !== JSON_ERROR_NONE) {
            $responseData = $response;
        }

        if ($httpCode >= 200 && $httpCode < 300) {
            return [
                'success' => true,
                'status' => $httpCode,
                'data' => $responseData,
                'timestamp' => date('c')
            ];
        } else {
            // Match JavaScript error structure: error.response?.status and error.response?.data
            return [
                'success' => false,
                'error' => 'HTTP Error ' . $httpCode,
                'status' => $httpCode,
                'data' => $responseData,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends customer profile data
     *
     * @param array|object $data Customer profile data or array of profiles
     * @return array API response
     */
    public function sendCustomerProfile($data): array
    {
        try {
            return $this->makeRequest('POST', '/customers', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends account event data
     *
     * @param array|object $data Account event data or array of events
     * @return array API response
     */
    public function sendAccountEvent($data): array
    {
        try {
            return $this->makeRequest('POST', '/events/account', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends deposit event data
     *
     * @param array|object $data Deposit event data or array of events
     * @return array API response
     */
    public function sendDepositEvent($data): array
    {
        try {
            return $this->makeRequest('POST', '/events/deposit', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends withdrawal event data
     *
     * @param array|object $data Withdrawal event data or array of events
     * @return array API response
     */
    public function sendWithdrawEvent($data): array
    {
        try {
            return $this->makeRequest('POST', '/events/withdraw', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends gaming activity event data
     *
     * @param array|object $data Gaming activity event data or array of events
     * @return array API response
     */
    public function sendGamingActivityEvent($data): array
    {
        try {
            return $this->makeRequest('POST', '/events/gaming-activity', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends extended attributes data
     *
     * @param array|object $data Extended attributes data or array of attributes
     * @return array API response
     */
    public function sendExtendedAttributes($data): array
    {
        try {
            return $this->makeRequest('POST', '/extattributes', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends wallet balance event data
     *
     * @param array|object $data Wallet balance event data or array of events
     * @return array API response
     */
    public function sendWalletBalanceEvent($data): array
    {
        try {
            return $this->makeRequest('POST', '/events/wallet-balance', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends refer friend event data
     *
     * @param array|object $data Refer friend event data or array of events
     * @return array API response
     */
    public function sendReferFriendEvent($data): array
    {
        try {
            return $this->makeRequest('POST', '/events/refer-friend', $data);
        } catch (\Exception $error) {
            return [
                'success' => false,
                'error' => $error->getMessage(),
                'status' => null,
                'data' => null,
                'timestamp' => date('c')
            ];
        }
    }

    /**
     * Sends multiple events in batch
     *
     * @param array $batchData Object containing different event types
     * @return array Batch response results
     */
    public function sendBatch(array $batchData): array
    {
        $results = [];
        $promises = [];

        if (isset($batchData['customers'])) {
            $results['customers'] = $this->sendCustomerProfile($batchData['customers']);
        }

        if (isset($batchData['accountEvents'])) {
            $results['accountEvents'] = $this->sendAccountEvent($batchData['accountEvents']);
        }

        if (isset($batchData['depositEvents'])) {
            $results['depositEvents'] = $this->sendDepositEvent($batchData['depositEvents']);
        }

        if (isset($batchData['withdrawEvents'])) {
            $results['withdrawEvents'] = $this->sendWithdrawEvent($batchData['withdrawEvents']);
        }

        if (isset($batchData['gamingEvents'])) {
            $results['gamingEvents'] = $this->sendGamingActivityEvent($batchData['gamingEvents']);
        }

        if (isset($batchData['extendedAttributes'])) {
            $results['extendedAttributes'] = $this->sendExtendedAttributes($batchData['extendedAttributes']);
        }

        if (isset($batchData['walletBalanceEvents'])) {
            $results['walletBalanceEvents'] = $this->sendWalletBalanceEvent($batchData['walletBalanceEvents']);
        }

        if (isset($batchData['referFriendEvents'])) {
            $results['referFriendEvents'] = $this->sendReferFriendEvent($batchData['referFriendEvents']);
        }

        return [
            'success' => true,
            'results' => $results,
            'timestamp' => date('c')
        ];
    }

    /**
     * Updates client configuration
     *
     * @param array $newConfig New configuration options
     * @throws \Exception If validation fails
     */
    public function updateConfig(array $newConfig): void
    {
        $this->config = array_merge($this->config, $newConfig);
        $this->baseURL = $this->config['baseURL'] ?? $this->baseURL;
        $this->timeout = $this->config['timeout'] ?? $this->timeout;
        $this->retries = $this->config['retries'] ?? $this->retries;
        $this->retryDelay = $this->config['retryDelay'] ?? $this->retryDelay;
        $this->validateConfig();
    }

    /**
     * Gets current configuration
     *
     * @return array Current configuration (without sensitive data)
     */
    public function getConfig(): array
    {
        $safeConfig = $this->config;
        if (isset($safeConfig['authToken']) && !empty($safeConfig['authToken'])) {
            $safeConfig['authToken'] = substr($safeConfig['authToken'], 0, 8) . '...';
        }
        return $safeConfig;
    }
}

