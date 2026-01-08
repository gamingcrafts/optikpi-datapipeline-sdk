<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Deposit Event Model
 * Represents deposit-related events for the Data Pipeline API
 */
class DepositEvent
{
    public $account_id;
    public $workspace_id;
    public $user_id;
    public $event_category;
    public $event_name;
    public $event_id;
    public $event_time;
    public $amount;
    public $payment_method;
    public $transaction_id;
    public $payment_provider_id;
    public $payment_provider_name;
    public $failure_reason;

    /**
     * Constructor
     *
     * @param array $data Deposit event data
     */
    public function __construct(array $data = [])
    {
        $this->event_category = $data['event_category'] ?? 'Deposit';
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the deposit event data
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
        if ($this->amount === null || $this->amount === '') {
            $errors[] = 'amount is required';
        }
        if (empty($this->payment_method)) {
            $errors[] = 'payment_method is required';
        }
        if (empty($this->transaction_id)) {
            $errors[] = 'transaction_id is required';
        }

        // Event category validation
        if (!empty($this->event_category) && $this->event_category !== 'Deposit') {
            $errors[] = 'event_category must be "Deposit" for deposit events';
        }

        // Event name validation
        $validEventNames = [
            'Successful Deposit',
            'Failed Deposit',
            'Pending Deposit',
            'Deposit Cancelled',
            'Deposit Refunded'
        ];

        if (!empty($this->event_name) && !in_array($this->event_name, $validEventNames)) {
            $errors[] = 'event_name must be one of: ' . implode(', ', $validEventNames);
        }

        // Amount validation
        if ($this->amount !== null && (!is_numeric($this->amount) || $this->amount <= 0)) {
            $errors[] = 'amount must be a positive number';
        }

        // Payment method validation
        $validPaymentMethods = [
            'bank',
            'credit_card',
            'debit_card',
            'e_wallet',
            'crypto',
            'paypal',
            'skrill',
            'neteller'
        ];

        if (!empty($this->payment_method) && !in_array($this->payment_method, $validPaymentMethods)) {
            $errors[] = 'payment_method must be one of: ' . implode(', ', $validPaymentMethods);
        }

        // Date format validation
        if (!empty($this->event_time) && !$this->isValidDateTime($this->event_time)) {
            $errors[] = 'event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)';
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
     * Creates a DepositEvent instance from array
     *
     * @param array $data Plain array data
     * @return DepositEvent New DepositEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}

