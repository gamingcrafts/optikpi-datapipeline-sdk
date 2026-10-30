(ns optikpi.datapipeline.models.customer-profile
  (:require [optikpi.datapipeline.models.bigquery-validation :as bq]))

;; Fields and types the warehouse accepts for this record; anything else is rejected there.
(def storage-rules
  {:required [:workspace_id :user_id :creation_timestamp]
   :types {:workspace_id :STRING
           :user_id :STRING
           :username :STRING
           :full_name :STRING
           :first_name :STRING
           :last_name :STRING
           :date_of_birth :DATE
           :email :STRING
           :phone_number :STRING
           :gender :STRING
           :country :STRING
           :city :STRING
           :language :STRING
           :currency :STRING
           :phone_verification :STRING
           :email_verification :STRING
           :bank_verification :STRING
           :iddoc_verification :STRING
           :marketing_email_preference :STRING
           :notifications_preference :STRING
           :subscription :STRING
           :privacy_settings :STRING
           :deposit_limits :NUMERIC
           :loss_limits :NUMERIC
           :wagering_limits :NUMERIC
           :session_time_limits :NUMERIC
           :cooling_off_expiry_date :TIMESTAMP
           :self_exclusion_expiry_date :TIMESTAMP
           :reality_checks_notification :STRING
           :vip_status :STRING
           :loyalty_program_tiers :STRING
           :account_status :STRING
           :bonus_abuser :STRING
           :financial_risk_level :NUMERIC
           :acquisition_source :STRING
           :partner_id :STRING
           :affiliate_id :STRING
           :utm_source :STRING
           :utm_campaign :STRING
           :utm_medium :STRING
           :referral_link_code :STRING
           :referral_limit_reached :STRING
           :creation_timestamp :TIMESTAMP
           :risk_score_level :STRING
           :marketing_sms_preference :STRING
           :custom_data :JSON
           :self_exclusion_by :STRING
           :self_exclusion_by_type :STRING
           :self_exclusion_check_time :TIMESTAMP
           :self_exclusion_created_time :TIMESTAMP
           :closed_time :TIMESTAMP
           :real_money_enabled :STRING
           :push_token :STRING}
   :partition :creation_timestamp})

(defn validate
  "Validates a CustomerProfile map. Returns {:valid? bool :errors []}."
  [m]
  (let [errors
        (cond-> []
          (bq/missing? (:account_id m))          (conj "account_id is required")
          (bq/missing? (:workspace_id m))        (conj "workspace_id is required")
          (bq/missing? (:user_id m))             (conj "user_id is required")
          (bq/missing? (:creation_timestamp m))  (conj "creation_timestamp is required")
          (and (string? (:email m))
               (not (re-matches #".+@.+\..+" (:email m))))
          (conj "email format is invalid")
          (and (string? (:date_of_birth m))
               (not (re-matches #"\d{4}-\d{2}-\d{2}" (:date_of_birth m))))
          (conj "date_of_birth must be YYYY-MM-DD")
          (and (some? (:gender m))
               (not (#{"Male" "Female" "Other"} (:gender m))))
          (conj "gender must be Male, Female, or Other"))]
    (bq/with-storage-errors errors m storage-rules)))

(defn build
  "Returns a CustomerProfile map, merging defaults."
  [data]
  (dissoc data nil))
