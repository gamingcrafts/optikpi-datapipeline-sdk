(ns optikpi.datapipeline.models.deposit-event)

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))      (conj "account_id is required")
          (empty? (:workspace_id m))    (conj "workspace_id is required")
          (empty? (:user_id m))         (conj "user_id is required")
          (empty? (:event_name m))      (conj "event_name is required")
          (empty? (:event_id m))        (conj "event_id is required")
          (empty? (:event_time m))      (conj "event_time is required")
          (empty? (:payment_method m))  (conj "payment_method is required")
          (empty? (:transaction_id m))  (conj "transaction_id is required")
          (nil? (:amount m))            (conj "amount is required")
          (and (some? (:amount m))
               (or (not (number? (:amount m)))
                   (not (pos? (:amount m)))))
          (conj "amount must be positive")
          (and (some? (:event_category m))
               (not= (:event_category m) "Deposit"))
          (conj "event_category must be \"Deposit\" for deposit events")
          (and (some? (:event_time m))
               (not (try (java.time.Instant/parse (:event_time m)) true (catch Exception _ false))))
          (conj "event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Deposit"))
