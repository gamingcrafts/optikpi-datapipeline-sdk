(ns optikpi.datapipeline.storage-validation-test
  (:require [clojure.test :refer :all]
            [optikpi.datapipeline.models.bigquery-validation :as bq]
            [optikpi.datapipeline.models.customer-profile      :as cp]
            [optikpi.datapipeline.models.account-event         :as ae]
            [optikpi.datapipeline.models.deposit-event         :as de]
            [optikpi.datapipeline.models.withdraw-event        :as we]
            [optikpi.datapipeline.models.gaming-activity-event :as ga]
            [optikpi.datapipeline.models.wallet-balance-event  :as wb]
            [optikpi.datapipeline.models.refer-friend-event    :as rf]
            [optikpi.datapipeline.models.customer-ext-event    :as ce]))

(deftest check-type-measured-table
  ;; Rows from the dev BigQuery probe (2026-10-29): [type value accepted?]. accepted? = BigQuery STORED it,
  ;; except "abc" NUMERIC and "yes" BOOLEAN, which BigQuery stores as wrong values and the SDK rejects on purpose.
  (doseq [[type value accepted?]
          [;; STRING
           [:STRING "abc" true]
           [:STRING "" true]
           [:STRING 15466 false]
           [:STRING true false]
           [:STRING {:a 1} false]
           [:STRING [1 2] false]
           ;; NUMERIC
           [:NUMERIC 10 true]
           [:NUMERIC 10.5 true]
           [:NUMERIC -3 true]
           [:NUMERIC 10.5M true]
           [:NUMERIC "10.5" true]
           [:NUMERIC "-10" true]
           [:NUMERIC "1e5" true]
           [:NUMERIC " 10" true]
           [:NUMERIC "10 " true]
           [:NUMERIC "+10" true]
           [:NUMERIC ".5" true]
           [:NUMERIC "10." true]
           [:NUMERIC "abc" false]
           [:NUMERIC "" false]
           [:NUMERIC true false]
           [:NUMERIC "1,000" false]
           ;; not measured, chosen so all SDKs agree: ASCII padding only
           [:NUMERIC "\u00A010" false]
           [:NUMERIC "\t10\n" true]
           [:NUMERIC Double/POSITIVE_INFINITY false]
           [:NUMERIC Double/NaN false]
           ;; INTEGER
           [:INTEGER 3 true]
           [:INTEGER 3.0 true]
           [:INTEGER "3" true]
           [:INTEGER "+3" true]
           [:INTEGER 3.5 false]
           [:INTEGER "3.5" false]
           [:INTEGER "" false]
           [:INTEGER true false]
           [:INTEGER " 3" false]
           [:INTEGER "3e2" false]
           ;; BOOLEAN
           [:BOOLEAN true true]
           [:BOOLEAN false true]
           [:BOOLEAN "true" true]
           [:BOOLEAN "TRUE" true]
           [:BOOLEAN "True" true]
           [:BOOLEAN "false" true]
           [:BOOLEAN 1 true]
           [:BOOLEAN 0 true]
           [:BOOLEAN "1" true]
           [:BOOLEAN "yes" false]
           ;; TIMESTAMP
           [:TIMESTAMP "2026-10-29T12:32:13.562Z" true]
           [:TIMESTAMP "2026-10-29 12:32:13" true]
           [:TIMESTAMP "2026-10-29 12:32:13 UTC" true]
           [:TIMESTAMP "2026-10-29T12:32:13 UTC" true]
           [:TIMESTAMP "2026-10-29T12:32:13+05:30" true]
           [:TIMESTAMP "2026-10-29 12:30" true]
           [:TIMESTAMP "2026-10-29T12:32:13.123456789Z" true]
           [:TIMESTAMP 1761741133562000 true]
           [:TIMESTAMP "1761741133562000" true]
           [:TIMESTAMP "2026-10-29" false]
           [:TIMESTAMP "2026-10-29 12:32:13 Asia/Kolkata" false]
           [:TIMESTAMP "2026-10-29T12:32:13+0530" false]
           [:TIMESTAMP "2026-10-29T12:32:13+05" false]
           [:TIMESTAMP "2026-1-5 1:2:3" false]
           [:TIMESTAMP "20261029T123213Z" false]
           [:TIMESTAMP "Oct 29 2026" false]
           [:TIMESTAMP "" false]
           [:TIMESTAMP "   " false]
           [:TIMESTAMP "2026-02-30 12:00:00" false]
           [:TIMESTAMP "2026-10-29 24:00:00" false]
           [:TIMESTAMP "2026-10-29 12:32:13 UTC+5" false]
           [:TIMESTAMP "2026-10-29T12:32:13z" false]
           [:TIMESTAMP "2026-10-29t12:32:13Z" false]
           [:TIMESTAMP true false]
           ;; not measured, chosen so all SDKs agree: years 0001-9999, offsets HH <= 18 and MM <= 59
           [:TIMESTAMP "0001-01-01T00:00:00Z" true]
           [:TIMESTAMP "0099-12-31 23:59:59" true]
           [:TIMESTAMP "0000-01-01T00:00:00Z" false]
           [:TIMESTAMP "2026-10-29T12:32:13+05:99" false]
           [:TIMESTAMP "2026-10-29T12:32:13+19:00" false]
           [:TIMESTAMP "2026-10-29T12:32:13-18:00" true]
           [:TIMESTAMP {} false]
           ;; DATE
           [:DATE "1990-05-17" true]
           [:DATE "1990-5-7" true]
           [:DATE 7000 true]
           [:DATE "7000" true]
           [:DATE "1990-02-30" false]
           [:DATE "17/05/1990" false]
           [:DATE "" false]
           [:DATE "1990-05-17T00:00:00Z" false]
           [:DATE "1990-05-17 00:00:00" false]
           [:DATE "0001-01-01" true]
           [:DATE "0000-01-01" false]
           ;; JSON (maps and sequences are serialised by the API before storage)
           [:JSON "{\"a\":1}" true]
           [:JSON "[1,2]" true]
           [:JSON "\"str\"" true]
           [:JSON "null" true]
           [:JSON {:a 1} true]
           [:JSON [1 2] true]
           [:JSON "bad{" false]
           [:JSON "\"a\tb\"" false]
           [:JSON "\"a\\tb\"" true]
           [:JSON "{\"a\":\"x\ty\"}" false]
           [:JSON "" false]
           [:JSON 5 false]
           [:JSON true false]]]
    (is (= accepted? (nil? (bq/check-type :f value type)))
        (str type " " (pr-str value) " accepted? " accepted?))))

(def ^:private now (.toEpochMilli (java.time.Instant/parse "2026-10-29T00:00:00Z")))
(def ^:private rules {:required [:a :b] :types {:a :STRING :n :NUMERIC :t :TIMESTAMP} :partition :t})

(deftest required-fields-missing-null-or-empty-are-rejected
  (doseq [[label record] [["missing" {}]
                          ["nil" {:a nil :b nil}]
                          ["empty string" {:a "" :b ""}]]]
    (let [errors (set (bq/validate-storage record rules now))]
      (is (contains? errors "a is required") label)
      (is (contains? errors "b is required") label))))

(deftest nil-and-absent-optional-fields-are-accepted
  (is (= [] (bq/validate-storage {:a "x" :b "y" :n nil} rules now))))

(deftest partition-window
  ;; Measured window on 2026-10-29, plus an offset applied before the comparison and a numeric value
  ;; (unit ambiguous, never window-checked).
  (doseq [[t accepted?] [["2016-10-15T00:00:00Z" false]
                         ["2016-11-15T00:00:00Z" true]
                         ["2027-10-01T00:00:00Z" true]
                         ["2027-12-01T00:00:00Z" false]
                         ["2027-10-29 00:00:00" true]
                         ["2027-10-29T00:00:00-23:00" false]
                         [1000 true]]]
    (is (= accepted? (empty? (bq/validate-storage {:a "x" :b "y" :t t} rules now)))
        (str t " accepted? " accepted?))))

(def ^:private event
  {:account_id "acc" :workspace_id "ws" :user_id "1" :event_id "e1" :event_name "Something"
   :event_time (str (java.time.Instant/now))})

(def ^:private models
  ;; [name validate build category extra wrong-category-suffix]
  [["AccountEvent" ae/validate ae/build "Account" {} "account events"]
   ["DepositEvent" de/validate de/build "Deposit" {:amount 10 :payment_method "card" :transaction_id "t1"} "deposit events"]
   ["WithdrawEvent" we/validate we/build "Withdraw" {:amount 10 :payment_method "card" :transaction_id "t1"} "withdraw events"]
   ["GamingActivityEvent" ga/validate ga/build "Gaming Activity" {:game_id "15466" :game_title "Aviator"} "gaming events"]
   ["WalletBalanceEvent" wb/validate wb/build "Wallet Balance" {:currency "INR" :wallet_type "main"} "wallet balance events"]
   ["ReferFriendEvent" rf/validate rf/build "Refer Friend" {} "refer friend events"]])

(deftest models-reject-missing-event-category-and-do-not-fill-one-in
  (doseq [[n validate build _ extra] models]
    (let [m (build (merge event extra))
          r (validate m)]
      (is (false? (:valid? r)) n)
      (is (some #{"event_category is required"} (:errors r)) n)
      (is (nil? (:event_category m)) n))))

(deftest models-reject-a-wrong-event-category
  (doseq [[n validate build category extra suffix] models]
    (let [r (validate (build (merge event extra {:event_category "Other"})))]
      (is (= [(str "event_category must be \"" category "\" for " suffix)] (:errors r)) n))))

(deftest models-accept-a-complete-record
  (doseq [[n validate build category extra] models]
    (is (= {:valid? true :errors []}
           (validate (build (merge event {:event_category category} extra))))
        n)))

(deftest a-field-the-model-does-not-declare-is-still-type-checked
  (let [r (ga/validate (merge event {:event_category "Gaming Activity" :game_id "1"
                                     :game_title "Aviator" :game_category 7}))]
    (is (some #{"game_category must be a string"} (:errors r)))))

(deftest an-undeclared-column-with-a-wrong-type-is-rejected
  (let [r (we/validate (merge event {:event_category "Withdraw" :amount 10 :payment_provider_id 42}))]
    (is (= ["payment_provider_id must be a string"] (:errors r)))))

(deftest a-key-present-with-nil-is-not-filled-from-a-string-key
  (let [r (bq/validate-storage {:a nil "a" "x" :b "y"} rules now)]
    (is (= ["a is required"] r))))

(deftest a-numeric-game-id-is-rejected
  (let [r (ga/validate (merge event {:event_category "Gaming Activity" :game_id 15466 :game_title "Aviator"}))]
    (is (some #{"game_id must be a string"} (:errors r)))))

(deftest non-string-values-in-existing-checks-do-not-throw
  (let [r (de/validate (merge event {:event_category "Deposit" :user_id 1 :amount "abc"
                                     :payment_method "card" :transaction_id "t1"}))]
    (is (= #{"user_id must be a string" "amount must be a number"} (set (:errors r))))))

(deftest wallet-payload-is-rejected-for-its-missing-event-category
  (let [r (wb/validate {:workspace_id "ws_example_001"
                        :blocked_amount 100.0
                        :account_id "acc_example_001"
                        :current_cash_balance 3065.5
                        :event_id "da49a497-a41f-4e37-b7b2-e10ad0c0ed4d"
                        :currency "INR"
                        :wallet_type "main"
                        :user_id "1"
                        :current_bonus_balance 20.0
                        :event_time (str (java.time.Instant/now))
                        :event_name "Balance Update"
                        :current_total_balance 3085.5})]
    (is (= ["event_category is required"] (:errors r)))))

(deftest customer-with-empty-string-timestamp-is-rejected
  (let [r (cp/validate {:account_id "acc" :workspace_id "ws" :user_id "1" :username "u"
                        :email "u@example.com" :creation_timestamp (str (java.time.Instant/now))
                        :self_exclusion_created_time ""})]
    (is (some #{"self_exclusion_created_time must be a valid ISO 8601 date-time"} (:errors r)))))

(deftest customer-creation-timestamp-outside-partition-window-is-rejected
  (let [r (cp/validate {:account_id "acc" :workspace_id "ws" :user_id "1"
                        :creation_timestamp "2012-01-01T00:00:00Z"})]
    (is (= ["creation_timestamp must be within the last 10 years and no more than 1 year in the future"]
           (:errors r)))))

(deftest customer-ext-data-may-be-a-map-or-a-json-string
  (let [base {:account_id "acc" :workspace_id "ws" :user_id "1" :list_name "l"}]
    (is (:valid? (ce/validate (assoc base :ext_data {:a 1}))))
    (is (:valid? (ce/validate (assoc base :ext_data "{\"a\":1}"))))))
