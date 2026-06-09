(ns optikpi.datapipeline.models.customer-ext-event
  (:require [cheshire.core :as json]))

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
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:user_id m))      (conj "user_id is required")
          (empty? (:list_name m))    (conj "list_name is required")
          (and (some? (:list_name m))
               (not (valid-list-name? (:list_name m))))
          (conj "list_name must contain only alphanumeric characters, underscores, or hyphens")
          (and (some? (:ext_data m))
               (not (valid-ext-data? (:ext_data m))))
          (conj "ext_data must be a map or valid JSON string"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (dissoc data nil))
