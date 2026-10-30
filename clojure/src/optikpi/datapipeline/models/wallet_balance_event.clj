(ns optikpi.datapipeline.models.wallet-balance-event
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
           :wallet_type :STRING
           :currency :STRING
           :current_cash_balance :NUMERIC
           :current_bonus_balance :NUMERIC
           :current_total_balance :NUMERIC
           :blocked_amount :NUMERIC}
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
               (not= "Wallet Balance" (:event_category m)))
          (conj "event_category must be \"Wallet Balance\" for wallet balance events")
          (and (string? (:currency m))
               (not (re-matches #"[A-Z]{3}" (:currency m))))
          (conj "currency must be a 3-letter ISO code (e.g. EUR, USD)"))]
    (bq/with-storage-errors errors m storage-rules)))

(defn build
  "Returns the event map as given. event_category is required and must be sent by the caller (\"Wallet Balance\")."
  [data]
  data)
