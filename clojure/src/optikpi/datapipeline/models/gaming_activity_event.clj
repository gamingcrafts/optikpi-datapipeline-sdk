(ns optikpi.datapipeline.models.gaming-activity-event)

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))      (conj "account_id is required")
          (empty? (:workspace_id m))    (conj "workspace_id is required")
          (empty? (:user_id m))         (conj "user_id is required")
          (empty? (:event_name m))      (conj "event_name is required")
          (empty? (:event_id m))        (conj "event_id is required")
          (empty? (:event_time m))      (conj "event_time is required")
          (empty? (:game_id m))         (conj "game_id is required")
          (empty? (:game_title m))      (conj "game_title is required")
          (and (seq (:event_category m))
               (not= (:event_category m) "Gaming Activity"))
          (conj "event_category must be \"Gaming Activity\" for gaming events")
          (and (some? (:wager_amount m))
               (or (not (number? (:wager_amount m)))
                   (neg? (:wager_amount m))))
          (conj "wager_amount must be a non-negative number")
          (and (some? (:win_amount m))
               (or (not (number? (:win_amount m)))
                   (neg? (:win_amount m))))
          (conj "win_amount must be a non-negative number")
          (and (some? (:currency m))
               (or (not (string? (:currency m)))
                   (not (re-matches #"[A-Z]{3}" (:currency m)))))
          (conj "currency must be a valid 3-letter ISO currency code")
          (and (some? (:event_time m))
               (not (try (java.time.Instant/parse (:event_time m)) true (catch Exception _ false))))
          (conj "event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Gaming Activity"))
