<?php

namespace Optikpi\DataPipeline;

use Optikpi\DataPipeline\Core\DataPipelineClient;
use Optikpi\DataPipeline\Models\CustomerProfile;
use Optikpi\DataPipeline\Models\AccountEvent;
use Optikpi\DataPipeline\Models\DepositEvent;
use Optikpi\DataPipeline\Models\WithdrawEvent;
use Optikpi\DataPipeline\Models\GamingActivityEvent;
use Optikpi\DataPipeline\Models\WalletBalanceEvent;
use Optikpi\DataPipeline\Models\ReferFriendEvent;
use Optikpi\DataPipeline\Models\CustomerExtEvent;
use Optikpi\DataPipeline\Utils\Crypto;

/**
 * Main SDK class for Optikpi Data Pipeline API
 * 
 * This class provides a simplified interface to the Data Pipeline API,
 * delegating all operations to the underlying DataPipelineClient.
 */
class OptikpiDataPipelineSDK
{
    private $client;

    /**
     * Constructor
     *
     * @param array $config Configuration array with authToken, accountId, workspaceId, etc.
     */
    public function __construct(array $config = [])
    {
        $this->client = new DataPipelineClient($config);
    }

    /**
     * Sends customer profile data
     *
     * @param array|CustomerProfile $data Customer profile data
     * @return array API response
     */
    public function sendCustomerProfile($data): array
    {
        if ($data instanceof CustomerProfile) {
            $data = $data->toArray();
        }
        return $this->client->sendCustomerProfile($data);
    }

    /**
     * Sends account event data
     *
     * @param array|AccountEvent $data Account event data
     * @return array API response
     */
    public function sendAccountEvent($data): array
    {
        if ($data instanceof AccountEvent) {
            $data = $data->toArray();
        }
        return $this->client->sendAccountEvent($data);
    }

    /**
     * Sends deposit event data
     *
     * @param array|DepositEvent $data Deposit event data
     * @return array API response
     */
    public function sendDepositEvent($data): array
    {
        if ($data instanceof DepositEvent) {
            $data = $data->toArray();
        }
        return $this->client->sendDepositEvent($data);
    }

    /**
     * Sends withdraw event data
     *
     * @param array|WithdrawEvent $data Withdraw event data
     * @return array API response
     */
    public function sendWithdrawEvent($data): array
    {
        if ($data instanceof WithdrawEvent) {
            $data = $data->toArray();
        }
        return $this->client->sendWithdrawEvent($data);
    }

    /**
     * Sends gaming activity event data
     *
     * @param array|GamingActivityEvent $data Gaming activity event data
     * @return array API response
     */
    public function sendGamingActivityEvent($data): array
    {
        if ($data instanceof GamingActivityEvent) {
            $data = $data->toArray();
        }
        return $this->client->sendGamingActivityEvent($data);
    }

    /**
     * Sends extended attributes data
     *
     * @param array|CustomerExtEvent $data Extended attributes data
     * @return array API response
     */
    public function sendExtendedAttributes($data): array
    {
        if ($data instanceof CustomerExtEvent) {
            $data = $data->toAPIFormat();
        }
        return $this->client->sendExtendedAttributes($data);
    }

    /**
     * Sends wallet balance event data
     *
     * @param array|WalletBalanceEvent $data Wallet balance event data
     * @return array API response
     */
    public function sendWalletBalanceEvent($data): array
    {
        if ($data instanceof WalletBalanceEvent) {
            $data = $data->toArray();
        }
        return $this->client->sendWalletBalanceEvent($data);
    }

    /**
     * Sends refer friend event data
     *
     * @param array|ReferFriendEvent $data Refer friend event data
     * @return array API response
     */
    public function sendReferFriendEvent($data): array
    {
        if ($data instanceof ReferFriendEvent) {
            $data = $data->toArray();
        }
        return $this->client->sendReferFriendEvent($data);
    }

    /**
     * Sends multiple events in batch
     *
     * @param array $batchData Array containing different event types
     * @return array Batch response results
     */
    public function sendBatch(array $batchData): array
    {
        return $this->client->sendBatch($batchData);
    }

    /**
     * Updates client configuration
     *
     * @param array $newConfig New configuration options
     */
    public function updateConfig(array $newConfig): void
    {
        $this->client->updateConfig($newConfig);
    }

    /**
     * Gets current configuration
     *
     * @return array Current configuration (without sensitive data)
     */
    public function getConfig(): array
    {
        return $this->client->getConfig();
    }
}

