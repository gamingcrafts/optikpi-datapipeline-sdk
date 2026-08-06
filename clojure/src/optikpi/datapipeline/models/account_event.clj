(ns optikpi.datapipeline.models.account-event)

(defn validate [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:user_id m))      (conj "user_id is required")
          (empty? (:event_name m))   (conj "event_name is required")
          (empty? (:event_id m))     (conj "event_id is required")
          (empty? (:event_time m))   (conj "event_time is required")
          (and (seq (:event_category m))
               (not= (:event_category m) "Account"))
          (conj "event_category must be \"Account\" for account events")
          (and (some? (:device m))
               (not (#{"desktop" "mobile" "tablet" "app"} (:device m))))
          (conj "device must be one of: desktop, mobile, tablet, app")
          (and (some? (:event_time m))
               (not (try (java.time.Instant/parse (:event_time m)) true (catch Exception _ false))))
          (conj "event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Account"))
