<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Gaming Activity Event Model
 * Represents gaming activity events for the Data Pipeline API
 */
class GamingActivityEvent
{
    // Fields and types the warehouse accepts for this record; anything else is rejected there.
    public const STORAGE_RULES = [
        'required' => ['workspace_id', 'user_id', 'event_id', 'event_time', 'event_category', 'event_name'],
        'types' => [
            'workspace_id' => 'STRING',
            'user_id' => 'STRING',
            'event_id' => 'STRING',
            'event_time' => 'TIMESTAMP',
            'event_category' => 'STRING',
            'event_name' => 'STRING',
            'free_spin_id' => 'STRING',
            'num_spins_played' => 'INTEGER',
            'game_id' => 'STRING',
            'game_title' => 'STRING',
            'game_theme' => 'STRING',
            'provider' => 'STRING',
            'win_amount' => 'NUMERIC',
            'remaining_spins' => 'INTEGER',
            'bet_value_per_spin' => 'NUMERIC',
            'wagering_requirements_met' => 'BOOLEAN',
            'free_spin_expiry_date' => 'TIMESTAMP',
            'campaign_id' => 'STRING',
            'campaign_name' => 'STRING',
            'rtp' => 'NUMERIC',
            'jackpot_amount' => 'NUMERIC',
            'game_category' => 'STRING',
            'winning_bet_amount' => 'NUMERIC',
            'jackpot_type' => 'STRING',
            'wager_amount' => 'NUMERIC',
            'loss_amount' => 'NUMERIC',
            'volatility' => 'STRING',
            'min_bet' => 'NUMERIC',
            'max_bet' => 'NUMERIC',
            'number_of_reels' => 'INTEGER',
            'number_of_paylines' => 'INTEGER',
            'feature_types' => 'STRING',
            'game_release_date' => 'TIMESTAMP',
            'live_dealer_availability' => 'BOOLEAN',
            'side_bets_availability' => 'BOOLEAN',
            'multiplayer_option' => 'BOOLEAN',
            'auto_play' => 'BOOLEAN',
            'poker_variant' => 'STRING',
            'tournament_name' => 'STRING',
            'buy_in_amount' => 'NUMERIC',
            'table_type' => 'STRING',
            'stakes_level' => 'STRING',
            'number_of_players' => 'INTEGER',
            'game_duration' => 'INTEGER',
            'hand_volume' => 'INTEGER',
            'player_position' => 'STRING',
            'final_hand' => 'STRING',
            'rake_contribution' => 'NUMERIC',
            'multi_tabling_indicator' => 'BOOLEAN',
            'session_result' => 'STRING',
            'vip_status' => 'STRING',
            'blind_level' => 'STRING',
            'rebuy_and_addon_info' => 'STRING',
            'sport_type' => 'STRING',
            'betting_market' => 'STRING',
            'odds' => 'NUMERIC',
            'live_betting_availability' => 'BOOLEAN',
            'result' => 'STRING',
            'bet_status' => 'STRING',
            'betting_channel' => 'STRING',
            'bonus_type' => 'STRING',
            'bonus_amount' => 'NUMERIC',
            'bonus_code' => 'STRING',
            'free_spin_start_date' => 'TIMESTAMP',
            'num_spins_awarded' => 'INTEGER',
            'parent_game_category' => 'STRING',
            'currency' => 'STRING',
            'money_type' => 'STRING',
            'transaction_type' => 'STRING'
        ],
        'partition' => 'event_time'
    ];

    /** @var array The record as sent, so validation also covers fields this model does not declare. */
    private $rawInput = [];

    public $account_id;
    public $workspace_id;
    public $user_id;
    public $event_category;
    public $event_name;
    public $event_id;
    public $event_time;
    public $wager_amount;
    public $win_amount;
    public $game_id;
    public $game_title;
    public $provider;
    public $currency;
    public $loss_amount;
    public $bonus_id;
    public $free_spin_id;
    public $jackpot_amount;
    public $tournament_name;
    public $num_spins_played;
    public $game_theme;
    public $remaining_spins;
    public $bet_value_per_spin;
    public $wagering_requirements_met;
    public $free_spin_expiry_date;
    public $campaign_id;
    public $campaign_name;
    public $rtp;
    public $game_category;
    public $winning_bet_amount;
    public $jackpot_type;
    public $volatility;
    public $min_bet;
    public $max_bet;
    public $number_of_reels;
    public $number_of_paylines;
    public $feature_types;
    public $game_release_date;
    public $live_dealer_availability;
    public $side_bets_availability;
    public $multiplayer_option;
    public $auto_play;
    public $poker_variant;
    public $buy_in_amount;
    public $table_type;
    public $stakes_level;
    public $number_of_players;
    public $game_duration;
    public $hand_volume;
    public $player_position;
    public $final_hand;
    public $rake_contribution;
    public $multi_tabling_indicator;
    public $session_result;
    public $vip_status;
    public $blind_level;
    public $rebuy_and_addon_info;
    public $sport_type;
    public $betting_market;
    public $odds;
    public $live_betting_availability;
    public $result;
    public $bet_status;
    public $betting_channel;
    public $bonus_type;
    public $bonus_amount;
    public $free_spin_start_date;
    public $num_spins_awarded;
    public $bonus_code;
    public $parent_game_category;
    public $money_type;
    public $transaction_type;

    /**
     * Constructor
     *
     * @param array $data Gaming activity event data
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
     * Validates the gaming activity event data
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
        if (empty($this->game_id)) {
            $errors[] = 'game_id is required';
        }
        if (empty($this->game_title)) {
            $errors[] = 'game_title is required';
        }

        // Event category validation
        if (!empty($this->event_category) && $this->event_category !== 'Gaming Activity') {
            $errors[] = 'event_category must be "Gaming Activity" for gaming events';
        }

        // Amount validations
        if ($this->wager_amount !== null && (!is_numeric($this->wager_amount) || $this->wager_amount < 0)) {
            $errors[] = 'wager_amount must be a non-negative number';
        }

        if ($this->win_amount !== null && (!is_numeric($this->win_amount) || $this->win_amount < 0)) {
            $errors[] = 'win_amount must be a non-negative number';
        }

        // Game type validation
        $validGameTypes = [
            'slots',
            'table_games',
            'card_games',
            'live_casino',
            'bingo',
            'scratch_cards',
            'lottery',
            'sports_betting',
            'virtual_sports'
        ];

        if (!empty($this->game_type) && !in_array($this->game_type, $validGameTypes)) {
            $errors[] = 'game_type must be one of: ' . implode(', ', $validGameTypes);
        }

        // Device validation
        if (!empty($this->device) && !in_array($this->device, ['desktop', 'mobile', 'tablet', 'app'])) {
            $errors[] = 'device must be one of: desktop, mobile, tablet, app';
        }

        // Platform validation
        $validPlatforms = [
            'web',
            'ios',
            'android',
            'windows',
            'mac',
            'linux'
        ];

        if (!empty($this->platform) && !in_array($this->platform, $validPlatforms)) {
            $errors[] = 'platform must be one of: ' . implode(', ', $validPlatforms);
        }

        // Currency validation
        if (!empty($this->currency) && !$this->isValidCurrency($this->currency)) {
            $errors[] = 'currency must be a valid 3-letter ISO currency code';
        }

        // Date format validation
        if (!empty($this->event_time) && !$this->isValidDateTime($this->event_time)) {
            $errors[] = 'event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)';
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
            if ($key !== 'rawInput' && $value !== null) {
                $arr[$key] = $value;
            }
        }
        return $arr;
    }

    /**
     * Creates a GamingActivityEvent instance from array
     *
     * @param array $data Plain array data
     * @return GamingActivityEvent New GamingActivityEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}

