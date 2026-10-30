(ns optikpi.datapipeline.models.customer-ext-event
  (:require [cheshire.core :as json]
            [optikpi.datapipeline.models.bigquery-validation :as bq]))

;; Fields and types the warehouse accepts for this record; anything else is rejected there.
(def storage-rules
  {:required [:workspace_id :user_id]
   :types {:workspace_id :STRING
           :user_id :STRING
           :list_name :STRING
           :ext_data :JSON}})

(defn- valid-list-name? [s]
  (boolean (re-matches #"[a-zA-Z0-9_\-]+" s)))

(defn- valid-ext-data? [v]
  (cond
    (map? v)    true
    (string? v) (try (json/parse-string v) true (catch Exception _ false))
    :else       false))

(defn validate [m]
  (let [errors
        (cond-> []
          (bq/missing? (:account_id m))   (conj "account_id is required")
          (bq/missing? (:workspace_id m)) (conj "workspace_id is required")
          (bq/missing? (:user_id m))      (conj "user_id is required")
          (bq/missing? (:list_name m))    (conj "list_name is required")
          (and (string? (:list_name m))
               (not (valid-list-name? (:list_name m))))
          (conj "list_name must contain only alphanumeric characters, underscores, or hyphens")
          (and (some? (:ext_data m))
               (not (valid-ext-data? (:ext_data m))))
          (conj "ext_data must be a map or valid JSON string"))]
    (bq/with-storage-errors errors m storage-rules)))

(defn build [data]
  (dissoc data nil))
