(ns optikpi.datapipeline.models.gaming-activity-event
  (:require [optikpi.datapipeline.models.bigquery-validation :as bq]))

;; Fields and types the warehouse accepts for this record; anything else is rejected there.
(def storage-rules
  {:required [:workspace_id :user_id :event_id :event_time :event_category :event_name]
   :types {:workspace_id :STRING
           :user_id :STRING
           :event_id :STRING
           :event_time :TIMESTAMP
           :event_category :STRING
           :event_name :STRING
           :free_spin_id :STRING
           :num_spins_played :INTEGER
           :game_id :STRING
           :game_title :STRING
           :game_theme :STRING
           :provider :STRING
           :win_amount :NUMERIC
           :remaining_spins :INTEGER
           :bet_value_per_spin :NUMERIC
           :wagering_requirements_met :BOOLEAN
           :free_spin_expiry_date :TIMESTAMP
           :campaign_id :STRING
           :campaign_name :STRING
           :rtp :NUMERIC
           :jackpot_amount :NUMERIC
           :game_category :STRING
           :winning_bet_amount :NUMERIC
           :jackpot_type :STRING
           :wager_amount :NUMERIC
           :loss_amount :NUMERIC
           :volatility :STRING
           :min_bet :NUMERIC
           :max_bet :NUMERIC
           :number_of_reels :INTEGER
           :number_of_paylines :INTEGER
           :feature_types :STRING
           :game_release_date :TIMESTAMP
           :live_dealer_availability :BOOLEAN
           :side_bets_availability :BOOLEAN
           :multiplayer_option :BOOLEAN
           :auto_play :BOOLEAN
           :poker_variant :STRING
           :tournament_name :STRING
           :buy_in_amount :NUMERIC
           :table_type :STRING
           :stakes_level :STRING
           :number_of_players :INTEGER
           :game_duration :INTEGER
           :hand_volume :INTEGER
           :player_position :STRING
           :final_hand :STRING
           :rake_contribution :NUMERIC
           :multi_tabling_indicator :BOOLEAN
           :session_result :STRING
           :vip_status :STRING
           :blind_level :STRING
           :rebuy_and_addon_info :STRING
           :sport_type :STRING
           :betting_market :STRING
           :odds :NUMERIC
           :live_betting_availability :BOOLEAN
           :result :STRING
           :bet_status :STRING
           :betting_channel :STRING
           :bonus_type :STRING
           :bonus_amount :NUMERIC
           :bonus_code :STRING
           :free_spin_start_date :TIMESTAMP
           :num_spins_awarded :INTEGER
           :parent_game_category :STRING
           :currency :STRING
           :money_type :STRING
           :transaction_type :STRING}
   :partition :event_time})

(defn validate [m]
  (let [errors
        (cond-> []
          (bq/missing? (:account_id m))   (conj "account_id is required")
          (bq/missing? (:workspace_id m)) (conj "workspace_id is required")
          (bq/missing? (:user_id m))      (conj "user_id is required")
          (bq/missing? (:event_name m))   (conj "event_name is required")
          (bq/missing? (:event_id m))     (conj "event_id is required")
          (bq/missing? (:event_time m))   (conj "event_time is required")
          (and (not (bq/missing? (:event_category m)))
               (not= "Gaming Activity" (:event_category m)))
          (conj "event_category must be \"Gaming Activity\" for gaming events"))]
    (bq/with-storage-errors errors m storage-rules)))

(defn build
  "Returns the event map as given. event_category is required and must be sent by the caller (\"Gaming Activity\")."
  [data]
  data)
