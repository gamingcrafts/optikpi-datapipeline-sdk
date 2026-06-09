(ns optikpi.datapipeline.models.wallet-balance-event)

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:user_id m))      (conj "user_id is required")
          (empty? (:event_name m))   (conj "event_name is required")
          (empty? (:event_id m))     (conj "event_id is required")
          (empty? (:event_time m))   (conj "event_time is required")
          (and (some? (:currency m))
               (not (re-matches #"[A-Z]{3}" (:currency m))))
          (conj "currency must be a 3-letter ISO code (e.g. EUR, USD)"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Wallet Balance"))
