<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Gaming Activity Event Model
 * Represents gaming activity events for the Data Pipeline API
 */
class GamingActivityEvent
{
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
        $this->event_category = $data['event_category'] ?? 'Gaming Activity';
        foreach ($data as $key => $value) {
            if (property_exists($this, $key)) {
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

        // Event name validation
        $validEventNames = [
            'Play Casino Game',
            'Game Win',
            'Game Loss',
            'Game Draw',
            'Bonus Game',
            'Free Spins',
            'Tournament Entry',
            'Tournament Win',
            'Progressive Jackpot',
            'Side Bet'
        ];

        if (!empty($this->event_name) && !in_array($this->event_name, $validEventNames)) {
            $errors[] = 'event_name must be one of: ' . implode(', ', $validEventNames);
        }

        // Amount validations
        if ($this->wager_amount !== null && (!is_numeric($this->wager_amount) || $this->wager_amount < 0)) {
            $errors[] = 'wager_amount must be a non-negative number';
        }

        if ($this->win_amount !== null && (!is_numeric($this->win_amount) || $this->win_amount < 0)) {
            $errors[] = 'win_amount must be a non-negative number';
        }

        // Currency validation
        if (!empty($this->currency) && !$this->isValidCurrency($this->currency)) {
            $errors[] = 'currency must be a valid 3-letter ISO currency code';
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
            if ($value !== null) {
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

