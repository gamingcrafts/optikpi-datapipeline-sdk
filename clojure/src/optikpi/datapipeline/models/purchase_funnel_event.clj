(ns optikpi.datapipeline.models.purchase-funnel-event
  "Purchase funnel event: one step of a purchase attempt (a funnel session).

  Every event of one attempt shares a :funnel_session_id. Its meaning comes from
  :funnel_stage (one of `funnel-stages`); :event_name is the operator's own free text."
  (:require [clojure.string :as str])
  (:import (java.math BigDecimal RoundingMode)
           (java.time DateTimeException LocalDateTime ZoneOffset)))

(def funnel-stages
  "Allowed :funnel_stage values, in funnel order."
  ["start" "browse" "configure" "cart" "checkout" "payment" "payment_failed" "complete"
   "failed" "cancelled" "exit" "stalled" "abandoned"])

(def failure-reasons
  "Allowed :failure_reason values."
  ["payment_declined" "insufficient_funds" "provider_timeout" "limit_reached" "kyc_required"
   "geo_blocked" "draw_closed" "technical_error" "user_cancelled"])

(def event-category "Purchase Funnel")

(def ^:private field-kinds
  "Every field the model carries, with its kind, in column order."
  [[:account_id :string]
   [:workspace_id :string]
   [:user_id :string]
   [:event_id :string]
   [:event_time :datetime]
   [:event_category :string]
   [:event_name :string]
   [:funnel_session_id :string]
   [:funnel_stage :string]
   [:item_id :string]
   [:item_name :string]
   [:item_category :string]
   [:draw_id :string]
   [:draw_number :string]
   [:draw_date :datetime]
   [:draw_close_time :datetime]
   [:jackpot_amount :money]
   [:selection :string]
   [:selection_method :string]
   [:lines_count :count]
   [:multi_draw_count :count]
   [:unit_price :money]
   [:item_quantity :count]
   [:item_amount :money]
   [:cart_id :string]
   [:cart_item_count :count]
   [:cart_quantity_total :count]
   [:cart_items :string]
   [:cart_subtotal :money]
   [:discount_amount :money]
   [:bonus_used_amount :money]
   [:fees_amount :money]
   [:tax_amount :money]
   [:cart_total :money]
   [:coupon_code :string]
   [:currency :string]
   [:payment_method :string]
   [:payment_provider_id :string]
   [:payment_provider_name :string]
   [:is_saved_method :boolean]
   [:funds_source :string]
   [:payment_attempt_number :count]
   [:payment_status :string]
   [:transaction_id :string]
   [:failure_reason :string]
   [:failure_code :string]
   [:failure_message :string]
   [:order_id :string]
   [:ticket_ids :string]
   [:order_total :money]
   [:order_status :string]
   [:is_first_purchase :boolean]
   [:device :string]
   [:platform :string]
   [:app_version :string]
   [:page_url :string]
   [:referrer :string]
   [:utm_source :string]
   [:utm_medium :string]
   [:utm_campaign :string]
   [:campaign_id :string]
   [:affiliate_id :string]
   [:entry_point :string]
   [:is_resumed :boolean]
   [:abandoned_at_stage :string]
   [:inactivity_sec :count]])

(def ^:private required-fields #{:account_id :workspace_id :user_id :event_category :event_name :event_id :event_time :funnel_session_id :funnel_stage})

;; Same window the event store accepts on its partition column.
(def ^:private ten-years-ms (* 10 365.25 24 60 60 1000))
(def ^:private one-year-ms (* 365.25 24 60 60 1000))

(def ^:private datetime-pattern
  #"(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\.\d{1,9})?(Z|[+-]\d{2}:\d{2})")

(defn parse-datetime-ms
  "Milliseconds since the epoch for an ISO 8601 date-time string with seconds and a
  zone, or nil when it is not one. Seconds and a zone are required: a date-time
  without them is lost downstream."
  [s]
  (when-let [[_ y mo d h mi sec _ zone] (re-matches datetime-pattern s)]
    (let [year (Long/parseLong y)]
      (when (>= year 1)
        (when-let [seconds (try
                             (.toEpochSecond
                              (LocalDateTime/of (int year) (Integer/parseInt mo) (Integer/parseInt d)
                                                (Integer/parseInt h) (Integer/parseInt mi)
                                                (Integer/parseInt sec))
                              ZoneOffset/UTC)
                             (catch DateTimeException _ nil))]
          (if (= zone "Z")
            (* seconds 1000)
            (let [hours   (Integer/parseInt (subs zone 1 3))
                  minutes (Integer/parseInt (subs zone 4 6))
                  sign    (if (= (first zone) \-) -1 1)]
              (when (and (<= hours 18) (<= minutes 59))
                (* (- seconds (* sign (+ (* hours 60) minutes) 60)) 1000)))))))))

(defn- finite? [v]
  (Double/isFinite (double v)))

;; Amounts are stored as decimal(15,2): 13 integer digits and 2 decimal places.
(def ^:private money-limit (.pow BigDecimal/TEN 13))

(defn- fits-money?
  "True when a finite number fits decimal(15,2): its absolute value is below 10^13 and
  its shortest round-trip decimal form has at most 2 decimal places. For a double that
  holds exactly when rounding its exact value to 2 places gives the same double back
  (2.995 and 1e-7 do not fit)."
  [v]
  (let [exact (if (float? v) (BigDecimal. (double v)) (bigdec v))]
    (and (neg? (compare (.abs exact) money-limit))
         (if (float? v)
           (== (double v) (.doubleValue (.setScale exact 2 RoundingMode/HALF_EVEN)))
           (<= (.scale (.stripTrailingZeros exact)) 2)))))

(defn- kind-error [field v kind]
  (let [n (name field)]
    (case kind
      :string   (when-not (string? v) (str n " must be a string"))
      ;; A JSON true is not a number; (number? true) is false.
      :money    (cond
                  (not (and (number? v) (finite? v))) (str n " must be a finite number")
                  (not (fits-money? v))
                  (str n " must fit decimal(15,2): below 10^13 with at most 2 decimal places"))
      ;; 2.0 is an integer in JSON, as for the API.
      ;; Counts are stored as 32-bit integers.
      :count    (when-not (and (or (integer? v)
                                   (and (float? v) (finite? v) (== v (Math/floor (double v)))))
                               (<= Integer/MIN_VALUE v Integer/MAX_VALUE))
                  (str n " must be an integer between " Integer/MIN_VALUE " and " Integer/MAX_VALUE))
      :boolean  (when-not (boolean? v) (str n " must be true or false"))
      :datetime (when-not (and (string? v) (parse-datetime-ms v))
                  (str n " must be an ISO 8601 date-time with seconds and a zone (YYYY-MM-DDTHH:mm:ss[.fff]Z or +hh:mm)")))))

(defn validate
  "Validates a purchase funnel event map (keyword keys, e.g. :funnel_stage).
  `now-ms` (milliseconds since the epoch, for the :event_time window) defaults to now.
  Unknown extra keys are accepted."
  ([m] (validate m (System/currentTimeMillis)))
  ([m now-ms]
   (let [{:keys [event_category funnel_stage failure_reason currency event_time]} m
         field-errors
         (keep (fn [[field kind]]
                 (let [v (get m field)]
                   (cond
                     (and (or (nil? v) (= "" v)) (required-fields field)) (str (name field) " is required")
                     ;; An empty optional value is still checked: "" is a string, but not a number or a date-time.
                     (nil? v) nil
                     :else (kind-error field v kind))))
               field-kinds)
         event-ms (when (string? event_time) (parse-datetime-ms event_time))
         errors
         (cond-> (vec field-errors)
           (and (string? event_category) (not= "" event_category) (not= event-category event_category))
           (conj (str "event_category must be \"" event-category "\" for purchase funnel events"))

           (and (string? funnel_stage) (not= "" funnel_stage) (not (some #{funnel_stage} funnel-stages)))
           (conj (str "funnel_stage must be one of: " (str/join ", " funnel-stages)))

           (and (string? failure_reason) (not (some #{failure_reason} failure-reasons)))
           (conj (str "failure_reason must be one of: " (str/join ", " failure-reasons)))

           (and (string? currency) (not (re-matches #"[A-Z]{3}" currency)))
           (conj "currency must be a valid 3-letter ISO currency code")

           (and event-ms (or (< event-ms (- now-ms ten-years-ms)) (> event-ms (+ now-ms one-year-ms))))
           (conj "event_time must be within the last 10 years and no more than 1 year in the future"))]
     {:valid? (empty? errors) :errors errors})))

(defn build
  "Returns the event map unchanged. :event_category is required and is not defaulted."
  [data]
  data)
