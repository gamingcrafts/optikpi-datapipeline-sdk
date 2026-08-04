(ns optikpi.datapipeline.models.customer-profile)

(defn validate
  "Validates a CustomerProfile map. Returns {:valid? bool :errors []}."
  [m]
  (let [errors
        (cond-> []
          (empty? (:account_id m))          (conj "account_id is required")
          (empty? (:workspace_id m))        (conj "workspace_id is required")
          (empty? (:user_id m))             (conj "user_id is required")
          (empty? (:username m))            (conj "username is required")
          (empty? (:email m))               (conj "email is required")
          (empty? (:creation_timestamp m))  (conj "creation_timestamp is required")
          (and (some? (:email m))
               (not (re-matches #".+@.+\..+" (:email m))))
          (conj "email format is invalid")
          (and (some? (:date_of_birth m))
               (not (re-matches #"\d{4}-\d{2}-\d{2}" (:date_of_birth m))))
          (conj "date_of_birth must be YYYY-MM-DD")
          (and (some? (:gender m))
               (not (#{"Male" "Female" "Other"} (:gender m))))
          (conj "gender must be Male, Female, or Other"))]
    {:valid? (empty? errors) :errors errors}))

(defn build
  "Returns a CustomerProfile map, merging defaults."
  [data]
  (dissoc data nil))
