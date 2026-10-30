(ns optikpi.datapipeline.models.bigquery-validation
  "Validation that follows what the warehouse was measured to store for each column type.

  A record the API accepts but the warehouse cannot store is lost without an error reaching the caller, so
  every model checks its fields against these rules before anything is sent. Two deliberate exceptions to the
  measured behaviour: a NUMERIC string that is not a number and a BOOLEAN string other than true/false/0/1 are
  rejected, because the warehouse would store a wrong value."
  (:require [cheshire.core :as json]
            [cheshire.factory :as factory])
  (:import (java.time LocalDate ZoneOffset)))

(def ^:private ten-years-ms (* 10 365.25 24 60 60 1000))
(def ^:private one-year-ms (* 365.25 24 60 60 1000))

;; Padding is ASCII whitespace only (space, \t, \n, \r, \f, \v), not NBSP or other Unicode spaces.
(def ^:private numeric-string #"[ \t\n\r\f\x0B]*[+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)?[ \t\n\r\f\x0B]*")
(def ^:private integer-string #"[+-]?\d+")
(def ^:private boolean-string #"(?i)true|false|0|1")
(def ^:private digits #"\d+")
(def ^:private date-string #"(\d{4})-(\d{1,2})-(\d{1,2})")
(def ^:private timestamp-string
  #"(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(?::(\d{2})(\.\d+)?)?(Z|[+-]\d{2}:\d{2}| UTC)?")

(defn missing?
  "True when a value counts as not sent: nil or the empty string."
  [v]
  (or (nil? v) (= "" v)))

(defn- finite-number? [v]
  (and (number? v)
       (not (and (or (instance? Double v) (instance? Float v))
                 (let [d (double v)] (or (Double/isNaN d) (Double/isInfinite d)))))))

(defn- integral-number? [v]
  (and (finite-number? v)
       (or (integer? v) (== v (Math/floor (double v))))))

(defn- real-date?
  "A real calendar date in years 0001-9999 (LocalDate alone would also allow year 0)."
  [y m d]
  (and (<= 1 y 9999)
       (try (LocalDate/of (int y) (int m) (int d)) true
            (catch Exception _ false))))

(defn- parse-timestamp-string
  "Milliseconds since the epoch for a date-time string the warehouse accepts, or nil when it does not.
  Numeric timestamps are accepted elsewhere and never reach this function."
  [v]
  (when-let [[_ y mo d h mi s _frac zone] (and (string? v) (re-matches timestamp-string v))]
    (let [[y mo d h mi s] (map #(if % (Long/parseLong %) 0) [y mo d h mi s])
          utc?  (or (nil? zone) (= zone "Z") (= zone " UTC"))
          off-h (if utc? 0 (Long/parseLong (subs zone 1 3)))
          off-m (if utc? 0 (Long/parseLong (subs zone 4 6)))]
      (when (and (real-date? y mo d) (< h 24) (< mi 60) (< s 60) (<= off-h 18) (<= off-m 59))
        (let [offset-s (* (if (and zone (= \- (first zone))) -1 1) (+ (* 3600 off-h) (* 60 off-m)))]
          (* 1000 (- (.toEpochSecond (.atTime (LocalDate/of (int y) (int mo) (int d)) (int h) (int mi) (int s))
                                     ZoneOffset/UTC)
                     offset-s)))))))

(defn- numeric-timestamp? [v]
  (or (finite-number? v) (and (string? v) (boolean (re-matches digits v)))))

;; Cheshire's default factory accepts raw control characters (U+0000-U+001F) inside strings; the server's
;; JSON.parse does not, so the check uses a factory that rejects them.
(def ^:private strict-json-factory
  (factory/make-json-factory {:allow-unquoted-control-chars false}))

(defn- json-string? [^String s]
  (try
    ;; Exactly one JSON value, nothing after it; "" has none.
    (binding [factory/*json-factory* strict-json-factory]
      (= 1 (count (doall (json/parsed-seq (java.io.StringReader. s))))))
    (catch Exception _ false)))

(defn check-type
  "Returns an error message, or nil when the value can be stored in a column of this type."
  [field value type]
  (let [f (name field)]
    (case type
      :STRING    (when-not (string? value) (str f " must be a string"))
      :NUMERIC   (cond
                   (number? value) (when-not (finite-number? value) (str f " must be a finite number"))
                   (and (string? value) (re-matches numeric-string value)) nil
                   :else (str f " must be a number"))
      :INTEGER   (cond
                   (number? value) (when-not (integral-number? value) (str f " must be an integer"))
                   (and (string? value) (re-matches integer-string value)) nil
                   :else (str f " must be an integer"))
      :BOOLEAN   (when-not (or (boolean? value)
                               (and (number? value) (or (== value 0) (== value 1)))
                               (and (string? value) (re-matches boolean-string value)))
                   (str f " must be true or false"))
      :TIMESTAMP (when-not (or (numeric-timestamp? value) (parse-timestamp-string value))
                   (str f " must be a valid ISO 8601 date-time"))
      :DATE      (when-not (or (integral-number? value)
                               (and (string? value) (re-matches digits value))
                               (when-let [[_ y m d] (and (string? value) (re-matches date-string value))]
                                 (real-date? (Long/parseLong y) (Long/parseLong m) (Long/parseLong d))))
                   (str f " must be a date in YYYY-MM-DD format"))
      ;; The API serialises maps and sequences to a JSON string before storing them.
      :JSON      (when-not (or (coll? value) (and (string? value) (json-string? value)))
                   (str f " must be an object or a valid JSON string"))
      nil)))

(defn- lookup
  "The value checked for column `k`: the keyword key when the map has it (even when nil), else a string key."
  [record k]
  (if (contains? record k) (get record k) (get record (name k))))

(defn validate-storage
  "Checks a record against storage rules {:required [kw] :types {kw type} :partition kw?}.
  `now` is the current time in ms (for tests). Returns a vector of error messages; empty when storable."
  ([record rules] (validate-storage record rules (System/currentTimeMillis)))
  ([record {:keys [required types partition]} now]
   (let [required-errors (for [k required
                               :when (missing? (lookup record k))]
                           (str (name k) " is required"))
         type-errors     (for [[k type] types
                               :let [v (lookup record k)]
                               :when (some? v)
                               :let [e (check-type k v type)]
                               :when e]
                           e)
         ;; The table is partitioned on this column and only accepts writes within this window. A numeric
         ;; timestamp's unit is ambiguous to the warehouse too, so only date-time strings are checked.
         pv              (when partition (lookup record partition))
         t               (when (string? pv) (parse-timestamp-string pv))
         partition-error (when (and t (or (< t (- now ten-years-ms)) (> t (+ now one-year-ms))))
                           [(str (name partition)
                                 " must be within the last 10 years and no more than 1 year in the future")])]
     (vec (concat required-errors type-errors partition-error)))))

(defn with-storage-errors
  "Appends storage errors for `record` that are not already in `errors`; returns {:valid? :errors}."
  [errors record rules]
  (let [all (reduce (fn [acc e] (if (some #{e} acc) acc (conj acc e)))
                    (vec errors)
                    (validate-storage record rules))]
    {:valid? (empty? all) :errors all}))
