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
          (and (some? (:device m))
               (not (#{"desktop" "mobile" "tablet" "app"} (:device m))))
          (conj "device must be desktop, mobile, tablet, or app"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Account"))
