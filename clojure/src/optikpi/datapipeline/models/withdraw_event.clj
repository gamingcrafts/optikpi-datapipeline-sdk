(ns optikpi.datapipeline.models.withdraw-event)

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:user_id m))      (conj "user_id is required")
          (contains? #{nil ""} (:event_category m)) (conj "event_category is required")
          (not (contains? #{nil "" "Withdraw"} (:event_category m))) (conj "event_category must be \"Withdraw\" for withdraw events")
          (empty? (:event_name m))   (conj "event_name is required")
          (empty? (:event_id m))     (conj "event_id is required")
          (empty? (:event_time m))   (conj "event_time is required")
          (nil? (:amount m))         (conj "amount is required")
          (and (some? (:amount m))
               (not (pos? (:amount m)))) (conj "amount must be positive"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  data)
