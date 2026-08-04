(ns optikpi.datapipeline.models.refer-friend-event)

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
               (not= (:event_category m) "Refer Friend"))
          (conj "event_category must be \"Refer Friend\" for refer friend events")
          (and (some? (:event_time m))
               (not (valid-iso? (:event_time m))))
          (conj "event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)")
          (and (some? (:referee_registration_date m))
               (not (valid-iso? (:referee_registration_date m))))
          (conj "referee_registration_date must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)")
          (and (some? (:successful_referral_confirmation m))
               (not (instance? Boolean (:successful_referral_confirmation m))))
          (conj "successful_referral_confirmation must be a boolean")
          (and (some? (:referee_first_deposit m))
               (neg? (:referee_first_deposit m)))
          (conj "referee_first_deposit must be a non-negative number"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "Refer Friend"))
