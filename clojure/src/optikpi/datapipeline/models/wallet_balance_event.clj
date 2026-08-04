(ns optikpi.datapipeline.models.wallet-balance-event)

(defn- valid-iso? [s]
  (try (java.time.Instant/parse s) true (catch Exception _ false)))

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:user_id m))      (conj "user_id is required")
          (empty? (:event_name m))   (conj "event_name is required")
          (empty? (:event_id m))     (conj "event_id is required")
          (empty? (:event_time m))   (conj "event_time is required")
          (and (some? (:event_category m))
               (not= (:event_category m) "Wallet Balance"))
          (conj "event_category must be \"Wallet Balance\" for wallet balance events")
          (and (some? (:event_time m))
               (not (valid-iso? (:event_time m))))
          (conj "event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)")
          (and (some? (:currency m))
               (not (re-matches #"[A-Z]{3}" (:currency m))))
          (conj "currency must be a valid 3-letter ISO currency code")
          (and (some? (:current_cash_balance m))
               (neg? (:current_cash_balance m)))
          (conj "current_cash_balance must be a non-negative number")
          (and (some? (:current_bonus_balance m))
               (neg? (:current_bonus_balance m)))
          (conj "current_bonus_balance must be a non-negative number")
          (and (some? (:current_total_balance m))
               (neg? (:current_total_balance m)))
          (conj "current_total_balance must be a non-negative number")
          (and (some? (:blocked_amount m))
               (neg? (:blocked_amount m)))
          (conj "blocked_amount must be a non-negative number"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Wallet Balance"))
