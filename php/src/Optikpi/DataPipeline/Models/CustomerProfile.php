<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Customer Profile Model
 * Represents a customer profile for the Data Pipeline API
 */
class CustomerProfile
{
    // Fields and types the warehouse accepts for this record; anything else is rejected there.
    public const STORAGE_RULES = [
        'required' => ['workspace_id', 'user_id', 'creation_timestamp'],
        'types' => [
            'workspace_id' => 'STRING',
            'user_id' => 'STRING',
            'username' => 'STRING',
            'full_name' => 'STRING',
            'first_name' => 'STRING',
            'last_name' => 'STRING',
            'date_of_birth' => 'DATE',
            'email' => 'STRING',
            'phone_number' => 'STRING',
            'gender' => 'STRING',
            'country' => 'STRING',
            'city' => 'STRING',
            'language' => 'STRING',
            'currency' => 'STRING',
            'phone_verification' => 'STRING',
            'email_verification' => 'STRING',
            'bank_verification' => 'STRING',
            'iddoc_verification' => 'STRING',
            'marketing_email_preference' => 'STRING',
            'notifications_preference' => 'STRING',
            'subscription' => 'STRING',
            'privacy_settings' => 'STRING',
            'deposit_limits' => 'NUMERIC',
            'loss_limits' => 'NUMERIC',
            'wagering_limits' => 'NUMERIC',
            'session_time_limits' => 'NUMERIC',
            'cooling_off_expiry_date' => 'TIMESTAMP',
            'self_exclusion_expiry_date' => 'TIMESTAMP',
            'reality_checks_notification' => 'STRING',
            'vip_status' => 'STRING',
            'loyalty_program_tiers' => 'STRING',
            'account_status' => 'STRING',
            'bonus_abuser' => 'STRING',
            'financial_risk_level' => 'NUMERIC',
            'acquisition_source' => 'STRING',
            'partner_id' => 'STRING',
            'affiliate_id' => 'STRING',
            'utm_source' => 'STRING',
            'utm_campaign' => 'STRING',
            'utm_medium' => 'STRING',
            'referral_link_code' => 'STRING',
            'referral_limit_reached' => 'STRING',
            'creation_timestamp' => 'TIMESTAMP',
            'risk_score_level' => 'STRING',
            'marketing_sms_preference' => 'STRING',
            'custom_data' => 'JSON',
            'self_exclusion_by' => 'STRING',
            'self_exclusion_by_type' => 'STRING',
            'self_exclusion_check_time' => 'TIMESTAMP',
            'self_exclusion_created_time' => 'TIMESTAMP',
            'closed_time' => 'TIMESTAMP',
            'real_money_enabled' => 'STRING',
            'push_token' => 'STRING'
        ],
        'partition' => 'creation_timestamp'
    ];

    /** @var array The record as sent, so validation also covers fields this model does not declare. */
    private $rawInput = [];

    public $account_id;
    public $workspace_id;
    public $user_id;
    public $username;
    public $full_name;
    public $first_name;
    public $last_name;
    public $date_of_birth;
    public $email;
    public $phone_number;
    public $gender;
    public $country;
    public $city;
    public $language;
    public $currency;
    public $marketing_email_preference;
    public $notifications_preference;
    public $subscription;
    public $privacy_settings;
    public $deposit_limits;
    public $loss_limits;
    public $wagering_limits;
    public $session_time_limits;
    public $reality_checks_notification;
    public $account_status;
    public $vip_status;
    public $loyalty_program_tiers;
    public $bonus_abuser;
    public $financial_risk_level;
    public $acquisition_source;
    public $partner_id;
    public $affiliate_id;
    public $utm_source;
    public $utm_campaign;
    public $utm_medium;
    public $referral_link_code;
    public $referral_limit_reached;
    public $creation_timestamp;
    public $phone_verification;
    public $email_verification;
    public $bank_verification;
    public $iddoc_verification;
    public $cooling_off_expiry_date;
    public $self_exclusion_expiry_date;
    public $risk_score_level;
    public $marketing_sms_preference;
    public $custom_data;
    public $self_exclusion_by;
    public $self_exclusion_by_type;
    public $self_exclusion_check_time;
    public $self_exclusion_created_time;
    public $closed_time;
    public $real_money_enabled;
    public $push_token;
    public $android_push_token;
    public $ios_push_token;
    public $windows_push_token;
    public $mac_dmg_push_token;

    /**
     * Constructor
     *
     * @param array $data Customer profile data
     */
    public function __construct(array $data = [])
    {
        $this->rawInput = $data;
        foreach ($data as $key => $value) {
            if ($key !== 'rawInput' && property_exists($this, $key)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the customer profile data
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
        if (empty($this->username)) {
            $errors[] = 'username is required';
        }
        if (empty($this->email)) {
            $errors[] = 'email is required';
        }
        if (empty($this->creation_timestamp)) {
            $errors[] = 'creation_timestamp is required';
        }

        // Email format validation
        if (!empty($this->email) && !$this->isValidEmail($this->email)) {
            $errors[] = 'email must be a valid email address';
        }

        // Date format validation
        if (!empty($this->date_of_birth) && !$this->isValidDate($this->date_of_birth)) {
            $errors[] = 'date_of_birth must be in YYYY-MM-DD format';
        }

        // Enum validations
        if (!empty($this->gender) && !in_array($this->gender, ['Male', 'Female', 'Other'])) {
            $errors[] = 'gender must be one of: Male, Female, Other';
        }

        if (!empty($this->account_status) && !in_array($this->account_status, ['Active', 'Inactive', 'Suspended', 'Closed'])) {
            $errors[] = 'account_status must be one of: Active, Inactive, Suspended, Closed';
        }

        if (!empty($this->vip_status) && !in_array($this->vip_status, ['Regular', 'Silver', 'Gold', 'Platinum', 'Diamond'])) {
            $errors[] = 'vip_status must be one of: Regular, Silver, Gold, Platinum, Diamond';
        }

        $record = BigQueryValidation::storageRecord($this, $this->rawInput);
        foreach (BigQueryValidation::validateStorage($record, self::STORAGE_RULES) as $error) {
            if (!in_array($error, $errors, true)) {
                $errors[] = $error;
            }
        }

        return [
            'isValid' => empty($errors),
            'errors' => $errors
        ];
    }

    /**
     * Validates email format
     *
     * @param string $email Email to validate
     * @return bool True if email is valid
     */
    private function isValidEmail(string $email): bool
    {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }

    /**
     * Validates date format
     *
     * @param string $date Date to validate
     * @return bool True if date is valid
     */
    private function isValidDate(string $date): bool
    {
        if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $date)) {
            return false;
        }

        $d = \DateTime::createFromFormat('Y-m-d', $date);
        return $d && $d->format('Y-m-d') === $date;
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
            if ($key !== 'rawInput' && $value !== null) {
                $arr[$key] = $value;
            }
        }
        return $arr;
    }

    /**
     * Creates a CustomerProfile instance from array
     *
     * @param array $data Plain array data
     * @return CustomerProfile New CustomerProfile instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}

