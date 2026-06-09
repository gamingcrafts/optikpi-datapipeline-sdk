(ns optikpi.datapipeline.models-test
  (:require [clojure.test :refer :all]
            [optikpi.datapipeline.models.customer-profile   :as cp]
            [optikpi.datapipeline.models.account-event      :as ae]
            [optikpi.datapipeline.models.deposit-event      :as de]
            [optikpi.datapipeline.models.withdraw-event     :as we]
            [optikpi.datapipeline.models.gaming-activity-event :as ga]
            [optikpi.datapipeline.models.wallet-balance-event  :as wb]
            [optikpi.datapipeline.models.refer-friend-event    :as rf]
            [optikpi.datapipeline.models.customer-ext-event    :as ce]
            [optikpi.datapipeline.models.system-event          :as se]))

(def ^:private base
  {:account_id "acc1" :workspace_id "ws1" :user_id "u1"
   :event_name "Test" :event_id "e1"
   :event_time "2026-01-01T00:00:00Z"})

;; CustomerProfile
(deftest customer-profile-valid
  (is (:valid? (cp/validate {:account_id "a" :workspace_id "w" :user_id "u"}))))

(deftest customer-profile-missing-required
  (let [r (cp/validate {})]
    (is (not (:valid? r)))
    (is (some #{"account_id is required"} (:errors r)))))

(deftest customer-profile-invalid-email
  (let [r (cp/validate {:account_id "a" :workspace_id "w" :user_id "u"
                        :email "not-an-email"})]
    (is (not (:valid? r)))
    (is (some #{"email format is invalid"} (:errors r)))))

(deftest customer-profile-invalid-gender
  (let [r (cp/validate {:account_id "a" :workspace_id "w" :user_id "u"
                        :gender "Unknown"})]
    (is (some #{"gender must be Male, Female, or Other"} (:errors r)))))

;; AccountEvent
(deftest account-event-sets-category
  (is (= "Account" (:event_category (ae/build base)))))

(deftest account-event-invalid-device
  (let [r (ae/validate (assoc base :device "smartwatch"))]
    (is (not (:valid? r)))))

;; DepositEvent
(deftest deposit-event-requires-amount
  (let [r (de/validate (dissoc base :amount))]
    (is (some #{"amount is required"} (:errors r)))))

(deftest deposit-event-negative-amount
  (let [r (de/validate (assoc base :amount -10 :payment_method "credit_card" :transaction_id "t1"))]
    (is (some #{"amount must be positive"} (:errors r)))))

(deftest deposit-event-sets-category
  (is (= "Deposit" (:event_category (de/build base)))))

;; WithdrawEvent
(deftest withdraw-event-sets-category
  (is (= "Withdraw" (:event_category (we/build base)))))

;; GamingActivityEvent
(deftest gaming-activity-sets-category
  (is (= "Gaming Activity" (:event_category (ga/build base)))))

;; WalletBalanceEvent
(deftest wallet-balance-invalid-currency
  (let [r (wb/validate (assoc base :currency "usd"))]
    (is (some #{"currency must be a 3-letter ISO code (e.g. EUR, USD)"} (:errors r)))))

(deftest wallet-balance-valid-currency
  (is (:valid? (wb/validate (assoc base :currency "EUR")))))

;; SystemEvent
(deftest system-event-sets-category
  (is (= "SystemEvent" (:event_category (se/build base)))))

;; CustomerExtEvent
(deftest customer-ext-invalid-list-name
  (let [r (ce/validate {:account_id "a" :workspace_id "w" :user_id "u"
                        :list_name "invalid name!"})]
    (is (not (:valid? r)))))

(deftest customer-ext-valid-list-name
  (is (:valid? (ce/validate {:account_id "a" :workspace_id "w" :user_id "u"
                              :list_name "my_list-1"}))))
