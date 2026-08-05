(ns optikpi.datapipeline.models.customer-ext-event
  (:require [cheshire.core :as json]))

(defn- valid-list-name? [s]
  (and (string? s)
       (boolean (re-matches #"[a-zA-Z0-9_\-]+" s))))

(defn- valid-ext-data? [v]
  (cond
    (map? v)    true
    (string? v) (try (json/parse-string v) true (catch Exception _ false))
    :else       false))

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:user_id m))      (conj "user_id is required")
          (or (nil? (:list_name m))
              (= "" (:list_name m))) (conj "list_name is required")
          (and (some? (:list_name m))
               (not= "" (:list_name m))
               (not (valid-list-name? (:list_name m))))
          (conj "list_name must contain only alphanumeric characters, underscores, and hyphens")
          (nil? (:ext_data m))       (conj "ext_data is required")
          (and (some? (:ext_data m))
               (not (valid-ext-data? (:ext_data m))))
          (conj "ext_data must be a valid JSON string or object"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (dissoc data nil))
