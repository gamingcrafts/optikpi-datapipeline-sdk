<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Refer Friend Event Model
 * Represents refer friend events for the Data Pipeline API
 */
class ReferFriendEvent
{
    public $account_id;
    public $workspace_id;
    public $user_id;
    public $event_category;
    public $event_name;
    public $event_id;
    public $event_time;
    public $referral_code_used;
    public $successful_referral_confirmation;
    public $reward_type;
    public $reward_claimed_status;
    public $referee_user_id;
    public $referee_registration_date;
    public $referee_first_deposit;

    /**
     * Constructor
     *
     * @param array $data Refer friend event data
     */
    public function __construct(array $data = [])
    {
        $this->event_category = $data['event_category'] ?? 'Refer Friend';
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the refer friend event data
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
        if (!empty($this->event_category) && $this->event_category !== 'Refer Friend') {
            $errors[] = 'event_category must be "Refer Friend" for refer friend events';
        }

        // Date format validation
        if (!empty($this->event_time) && !$this->isValidDateTime($this->event_time)) {
            $errors[] = 'event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)';
        }

        if (!empty($this->referee_registration_date) && !$this->isValidDateTime($this->referee_registration_date)) {
            $errors[] = 'referee_registration_date must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)';
        }

        // Boolean validation
        if ($this->successful_referral_confirmation !== null && !is_bool($this->successful_referral_confirmation)) {
            $errors[] = 'successful_referral_confirmation must be a boolean';
        }

        // Reward type validation
        $validRewardTypes = ['bonus', 'cash', 'points', 'free_spins', 'other'];
        if (!empty($this->reward_type) && !in_array($this->reward_type, $validRewardTypes)) {
            $errors[] = 'reward_type must be one of: ' . implode(', ', $validRewardTypes);
        }

        // Reward claimed status validation
        $validClaimedStatuses = ['pending', 'claimed', 'expired', 'cancelled'];
        if (!empty($this->reward_claimed_status) && !in_array($this->reward_claimed_status, $validClaimedStatuses)) {
            $errors[] = 'reward_claimed_status must be one of: ' . implode(', ', $validClaimedStatuses);
        }

        // First deposit validation (must be non-negative if provided)
        if ($this->referee_first_deposit !== null && (!is_numeric($this->referee_first_deposit) || $this->referee_first_deposit < 0)) {
            $errors[] = 'referee_first_deposit must be a non-negative number';
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
     * Creates a ReferFriendEvent instance from array
     *
     * @param array $data Plain array data
     * @return ReferFriendEvent New ReferFriendEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}

