<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Wallet Balance Event Model
 * Represents wallet balance events for the Data Pipeline API
 */
class WalletBalanceEvent
{
    public $account_id;
    public $workspace_id;
    public $user_id;
    public $event_category;
    public $event_name;
    public $event_id;
    public $event_time;
    public $wallet_type;
    public $currency;
    public $current_cash_balance;
    public $current_bonus_balance;
    public $current_total_balance;
    public $blocked_amount;

    /**
     * Constructor
     *
     * @param array $data Wallet balance event data
     */
    public function __construct(array $data = [])
    {
        $this->event_category = $data['event_category'] ?? 'Wallet Balance';
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the wallet balance event data
     *
     * @return array Validation result with isValid boolean and errors array
     */
    public function validate(): array
    {
        $errors = [];

        // Required fields
        if (empty($this->account_id)) {
            $errors[] = 'account_id is required';
        }
        if (empty($this->workspace_id)) {
            $errors[] = 'workspace_id is required';
        }
        if (empty($this->user_id)) {
            $errors[] = 'user_id is required';
        }
        if (empty($this->event_name)) {
            $errors[] = 'event_name is required';
        }
        if (empty($this->event_id)) {
            $errors[] = 'event_id is required';
        }
        if (empty($this->event_time)) {
            $errors[] = 'event_time is required';
        }

        // Event category validation
        if (!empty($this->event_category) && $this->event_category !== 'Wallet Balance') {
            $errors[] = 'event_category must be "Wallet Balance" for wallet balance events';
        }

        // Date format validation
        if (!empty($this->event_time) && !$this->isValidDateTime($this->event_time)) {
            $errors[] = 'event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)';
        }

        // Currency validation
        if (!empty($this->currency) && !$this->isValidCurrency($this->currency)) {
            $errors[] = 'currency must be a valid 3-letter ISO currency code';
        }

        // Balance validation (must be non-negative if provided)
        if ($this->current_cash_balance !== null && (!is_numeric($this->current_cash_balance) || $this->current_cash_balance < 0)) {
            $errors[] = 'current_cash_balance must be a non-negative number';
        }

        if ($this->current_bonus_balance !== null && (!is_numeric($this->current_bonus_balance) || $this->current_bonus_balance < 0)) {
            $errors[] = 'current_bonus_balance must be a non-negative number';
        }

        if ($this->current_total_balance !== null && (!is_numeric($this->current_total_balance) || $this->current_total_balance < 0)) {
            $errors[] = 'current_total_balance must be a non-negative number';
        }

        if ($this->blocked_amount !== null && (!is_numeric($this->blocked_amount) || $this->blocked_amount < 0)) {
            $errors[] = 'blocked_amount must be a non-negative number';
        }

        return [
            'isValid' => empty($errors),
            'errors' => $errors
        ];
    }

    /**
     * Validates ISO 8601 datetime format
     *
     * @param string $dateTime DateTime string to validate
     * @return bool True if datetime is valid
     */
    private function isValidDateTime(string $dateTime): bool
    {
        try {
            $date = new \DateTime($dateTime);
            return $date !== false;
        } catch (\Exception $e) {
            return false;
        }
    }

    /**
     * Validates currency code format
     *
     * @param string $currency Currency code to validate
     * @return bool True if currency code is valid
     */
    private function isValidCurrency(string $currency): bool
    {
        return preg_match('/^[A-Z]{3}$/', $currency) === 1;
    }

    /**
     * Converts the model to a plain array
     *
     * @return array Plain array representation
     */
    public function toArray(): array
    {
        $arr = [];
        foreach (get_object_vars($this) as $key => $value) {
            // Match JavaScript toJSON() behavior: exclude undefined properties
            // In PHP, unset properties are null, so excluding null approximates JavaScript's undefined exclusion
            // Note: This means explicitly null values are also excluded, which differs slightly from JavaScript
            if ($value !== null) {
                $arr[$key] = $value;
            }
        }
        return $arr;
    }

    /**
     * Creates a WalletBalanceEvent instance from array
     *
     * @param array $data Plain array data
     * @return WalletBalanceEvent New WalletBalanceEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}

