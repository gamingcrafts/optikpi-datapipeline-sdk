(ns optikpi.datapipeline.models.withdraw-event
  (:require [optikpi.datapipeline.models.bigquery-validation :as bq]))

;; Fields and types the warehouse accepts for this record; anything else is rejected there.
(def storage-rules
  {:required [:workspace_id :user_id :event_id :event_time :event_category :event_name :amount]
   :types {:workspace_id :STRING
           :user_id :STRING
           :event_id :STRING
           :event_time :TIMESTAMP
           :event_category :STRING
           :event_name :STRING
           :amount :NUMERIC
           :payment_method :STRING
           :transaction_id :STRING
           :failure_reason :STRING
           :payment_provider_id :STRING
           :payment_provider_name :STRING}
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
          (nil? (:amount m))              (conj "amount is required")
          (and (number? (:amount m))
               (not (pos? (:amount m))))  (conj "amount must be positive")
          (and (not (bq/missing? (:event_category m)))
               (not= "Withdraw" (:event_category m)))
          (conj "event_category must be \"Withdraw\" for withdraw events"))]
    (bq/with-storage-errors errors m storage-rules)))

(defn build
  "Returns the event map as given. event_category is required and must be sent by the caller (\"Withdraw\")."
  [data]
  data)
