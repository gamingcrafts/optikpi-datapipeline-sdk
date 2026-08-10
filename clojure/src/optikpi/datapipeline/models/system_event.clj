(ns optikpi.datapipeline.models.system-event)

(defn- valid-iso? [s]
  (try (java.time.Instant/parse s) true (catch Exception _ false)))

(defn validate [m]
  (let [event-data (:event_data m)
        errors
        (cond-> []
          (empty? (:account_id m))   (conj "account_id is required")
          (empty? (:workspace_id m)) (conj "workspace_id is required")
          (empty? (:event_name m))   (conj "event_name is required")
          (empty? (:event_id m))     (conj "event_id is required")
          (empty? (:event_time m))   (conj "event_time is required")
          (nil? event-data)
          (conj "event_data is required")
          (and (some? event-data)
               (not (string? event-data))
               (not (map? event-data)))
          (conj "event_data must be a string or an object")
          (and (seq (:event_category m))
               (not= (:event_category m) "SystemEvent"))
          (conj "event_category must be \"SystemEvent\" for system events")
          (and (some? (:event_time m))
               (not (valid-iso? (:event_time m))))
          (conj "event_time must be in ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)"))]
    {:valid? (empty? errors) :errors errors}))

(defn build [data]
  (assoc data :event_category "SystemEvent"))
