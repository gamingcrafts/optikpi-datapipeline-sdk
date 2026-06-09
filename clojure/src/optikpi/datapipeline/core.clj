(ns optikpi.datapipeline.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [optikpi.datapipeline.crypto :as crypto]))

;; ---------------------------------------------------------------------------
;; Client construction
;; ---------------------------------------------------------------------------

(def ^:private defaults
  {:timeout      30000
   :retries      3
   :retry-delay  1000})

(defn create-client
  "Creates a DataPipelineClient config map.

  Required keys: :base-url :auth-token :account-id :workspace-id
  Optional keys: :timeout (ms) :retries :retry-delay (ms)"
  [config]
  (let [{:keys [base-url auth-token account-id workspace-id]} config]
    (when (empty? base-url)      (throw (IllegalArgumentException. "base-url is required")))
    (when (empty? auth-token)    (throw (IllegalArgumentException. "auth-token is required")))
    (when (empty? account-id)    (throw (IllegalArgumentException. "account-id is required")))
    (when (empty? workspace-id)  (throw (IllegalArgumentException. "workspace-id is required")))
    (merge defaults config)))

;; ---------------------------------------------------------------------------
;; HTTP transport
;; ---------------------------------------------------------------------------

(defn- build-headers [client body-str]
  (let [{:keys [auth-token account-id workspace-id]} client
        sig (crypto/generate-signature body-str auth-token account-id workspace-id)]
    {"Content-Type"            "application/json"
     "x-optikpi-token"         auth-token
     "x-optikpi-account-id"    account-id
     "x-optikpi-workspace-id"  workspace-id
     "x-hmac-signature"        sig
     "x-hmac-algorithm"        "sha256"}))

(defn- send-request
  [client endpoint data]
  (let [body-str  (json/generate-string data)
        url       (str (:base-url client) endpoint)
        {:keys [retries retry-delay timeout]} client]
    (loop [attempt 0]
      (let [result
            (try
              (let [resp (http/post url
                           {:body             body-str
                            :headers          (build-headers client body-str)
                            :socket-timeout   timeout
                            :conn-timeout     timeout
                            :throw-exceptions false})]
                {:status  (:status resp)
                 :success (< (:status resp) 300)
                 :data    (when-not (empty? (:body resp))
                            (try (json/parse-string (:body resp) true)
                                 (catch Exception _ (:body resp))))
                 :error   (when (>= (:status resp) 300) (:body resp))})
              (catch Exception e
                {:status  0
                 :success false
                 :data    nil
                 :error   (.getMessage e)}))]
        (if (and (not (:success result))
                 (>= (:status result) 500)
                 (< attempt retries))
          (do (Thread/sleep (* retry-delay (inc attempt)))
              (recur (inc attempt)))
          result)))))

;; ---------------------------------------------------------------------------
;; Send methods
;; ---------------------------------------------------------------------------

(defn send-customer-profile    [client data] (send-request client "/customers"             data))
(defn send-extended-attributes [client data] (send-request client "/extattributes"         data))
(defn send-account-event       [client data] (send-request client "/events/account"        data))
(defn send-deposit-event       [client data] (send-request client "/events/deposit"        data))
(defn send-withdraw-event      [client data] (send-request client "/events/withdraw"       data))
(defn send-gaming-activity-event [client data] (send-request client "/events/gaming-activity" data))
(defn send-wallet-balance-event  [client data] (send-request client "/events/wallet-balance"  data))
(defn send-refer-friend-event    [client data] (send-request client "/events/refer-friend"    data))
(defn send-system-event          [client data] (send-request client "/events/system-events"   data))

(defn send-batch
  "Sends all non-nil event types in batch-data in parallel.
   batch-data keys: :customers :extended-attributes :account-events
   :deposit-events :withdraw-events :gaming-events :wallet-balance-events
   :refer-friend-events :system-events"
  [client batch-data]
  (let [tasks
        (cond-> {}
          (:customers batch-data)
          (assoc :customers
                 (future (send-customer-profile client (:customers batch-data))))
          (:extended-attributes batch-data)
          (assoc :extended-attributes
                 (future (send-extended-attributes client (:extended-attributes batch-data))))
          (:account-events batch-data)
          (assoc :account-events
                 (future (send-account-event client (:account-events batch-data))))
          (:deposit-events batch-data)
          (assoc :deposit-events
                 (future (send-deposit-event client (:deposit-events batch-data))))
          (:withdraw-events batch-data)
          (assoc :withdraw-events
                 (future (send-withdraw-event client (:withdraw-events batch-data))))
          (:gaming-events batch-data)
          (assoc :gaming-events
                 (future (send-gaming-activity-event client (:gaming-events batch-data))))
          (:wallet-balance-events batch-data)
          (assoc :wallet-balance-events
                 (future (send-wallet-balance-event client (:wallet-balance-events batch-data))))
          (:refer-friend-events batch-data)
          (assoc :refer-friend-events
                 (future (send-refer-friend-event client (:refer-friend-events batch-data))))
          (:system-events batch-data)
          (assoc :system-events
                 (future (send-system-event client (:system-events batch-data)))))]
    (reduce-kv (fn [acc k fut] (assoc acc k (deref fut))) {} tasks)))
