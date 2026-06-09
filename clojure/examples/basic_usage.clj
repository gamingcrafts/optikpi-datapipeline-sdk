(ns examples.basic-usage
  (:require [optikpi.datapipeline.core :as sdk]
            [optikpi.datapipeline.models.deposit-event :as deposit]
            [optikpi.datapipeline.models.customer-profile :as profile]))

(def client
  (sdk/create-client
    {:base-url     "https://api.optikpi.com"
     :auth-token   (System/getenv "OPTIKPI_AUTH_TOKEN")
     :account-id   (System/getenv "OPTIKPI_ACCOUNT_ID")
     :workspace-id (System/getenv "OPTIKPI_WORKSPACE_ID")}))

;; Send a deposit event
(let [event (deposit/build
              {:user_id        "player_001"
               :event_name     "Successful Deposit"
               :event_id       (str "dep_" (System/currentTimeMillis))
               :event_time     "2026-01-15T10:30:00Z"
               :amount         100.00
               :payment_method "credit_card"
               :transaction_id "txn_abc123"})
      {:keys [valid? errors]} (deposit/validate event)]
  (if valid?
    (let [result (sdk/send-deposit-event client event)]
      (println "Deposit sent:" (:status result)))
    (println "Validation failed:" errors)))

;; Send a customer profile
(let [p (profile/build
          {:user_id      "player_001"
           :email        "player@example.com"
           :first_name   "Alex"
           :last_name    "Smith"
           :date_of_birth "1990-05-15"
           :gender       "Male"
           :country      "FI"})
      {:keys [valid? errors]} (profile/validate p)]
  (if valid?
    (let [result (sdk/send-customer-profile client p)]
      (println "Profile sent:" (:status result)))
    (println "Validation failed:" errors)))

;; Batch send
(let [result (sdk/send-batch client
               {:deposit-events
                (deposit/build
                  {:user_id "player_002" :event_name "Deposit"
                   :event_id "e2" :event_time "2026-01-15T11:00:00Z"
                   :amount 50.0 :payment_method "bank" :transaction_id "t2"})})]
  (println "Batch result:" result))
