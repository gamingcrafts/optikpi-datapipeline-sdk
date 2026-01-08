<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Account Event Model
 * Represents account-related events for the Data Pipeline API
 */
class AccountEvent
{
    public $account_id;
    public $workspace_id;
    public $user_id;
    public $event_category;
    public $event_name;
    public $event_id;
    public $event_time;
    public $device;
    public $status;
    public $affiliate_id;
    public $partner_id;
    public $campaign_code;
    public $reason;

    /**
     * Constructor
     *
     * @param array $data Account event data
     */
    public function __construct(array $data = [])
    {
        $this->event_category = $data['event_category'] ?? 'Account';
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the account event data
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
        if (!empty($this->event_category) && $this->event_category !== 'Account') {
            $errors[] = 'event_category must be "Account" for account events';
        }

        // Event name validation
        $validEventNames = [
            'Player Registration',
            'Account Verification',
            'Password Change',
            'Email Update',
            'Phone Update',
            'Account Suspension',
            'Account Reactivation',
            'Profile Update',
            'Login',
            'Logout'
        ];

        if (!empty($this->event_name) && !in_array($this->event_name, $validEventNames)) {
            $errors[] = 'event_name must be one of: ' . implode(', ', $validEventNames);
        }

        // Status validation
        if (!empty($this->status) && !in_array($this->status, ['verified', 'pending', 'failed', 'completed'])) {
            $errors[] = 'status must be one of: verified, pending, failed, completed';
        }

        // Device validation
        if (!empty($this->device) && !in_array($this->device, ['desktop', 'mobile', 'tablet', 'app'])) {
            $errors[] = 'device must be one of: desktop, mobile, tablet, app';
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
            if ($value !== null) {
                $arr[$key] = $value;
            }
        }
        return $arr;
    }

    /**
     * Creates an AccountEvent instance from array
     *
     * @param array $data Plain array data
     * @return AccountEvent New AccountEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}

