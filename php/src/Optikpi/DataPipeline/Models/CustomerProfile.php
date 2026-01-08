<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Customer Profile Model
 * Represents a customer profile for the Data Pipeline API
 */
class CustomerProfile
{
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
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
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
            if ($value !== null) {
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

