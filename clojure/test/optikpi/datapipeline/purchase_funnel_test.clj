(ns optikpi.datapipeline.purchase-funnel-test
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [optikpi.datapipeline.core :as sdk]
            [optikpi.datapipeline.models.purchase-funnel-event :as pf])
  (:import (com.sun.net.httpserver HttpHandler HttpServer)
           (java.net InetSocketAddress)
           (java.time Instant)
           (java.time.temporal ChronoUnit)))

(def ^:private now-ms (.toEpochMilli (Instant/parse "2026-11-04T10:00:00Z")))

;; Shared with the API's tests: the client and the server must agree on every shape.
(def ^:private shapes
  (json/parse-string (slurp (io/file ".." "testdata" "purchase-funnel-shapes.json")) true))

(defn- build-record [{:keys [from unset set]}]
  (merge (apply dissoc (if (= from "full") (:full shapes) (:base shapes)) (map keyword unset))
         set))

(deftest validate-matches-shared-shapes
  (is (seq (:cases shapes)))
  (doseq [{:keys [name valid errorField] :as c} (:cases shapes)]
    (testing name
      (let [{:keys [valid? errors]} (pf/validate (build-record c) now-ms)]
        (is (= valid valid?) (pr-str errors))
        (when-not valid
          (is (some #(str/includes? % errorField) errors) (pr-str errors)))))))

(deftest window-defaults-to-now
  (let [recent (str (.minus (Instant/now) 1 ChronoUnit/MINUTES))
        old    (str (.minus (Instant/now) (* 11 365) ChronoUnit/DAYS))]
    (is (:valid? (pf/validate (assoc (:base shapes) :event_time recent))))
    (is (not (:valid? (pf/validate (assoc (:base shapes) :event_time old)))))))

(deftest build-does-not-fill-in-event-category
  (is (nil? (:event_category (pf/build (dissoc (:base shapes) :event_category))))))

(deftest constants-in-contract-order
  (is (= ["start" "browse" "configure" "cart" "checkout" "payment" "payment_failed" "complete"
          "failed" "cancelled" "exit" "stalled" "abandoned"]
         pf/funnel-stages))
  (is (= ["payment_declined" "insufficient_funds" "provider_timeout" "limit_reached" "kyc_required"
          "geo_blocked" "draw_closed" "technical_error" "user_cancelled"]
         pf/failure-reasons)))

(defn- with-server
  "Runs f with a client pointed at a local HTTP server; returns the requests it received."
  [f]
  (let [requests (atom [])
        server   (HttpServer/create (InetSocketAddress. "127.0.0.1" 0) 0)]
    (.createContext server "/"
                    (reify HttpHandler
                      (handle [_ exchange]
                        (swap! requests conj (str (.getRequestMethod exchange) " "
                                                  (.getPath (.getRequestURI exchange))))
                        (let [body (.getBytes "{}" "UTF-8")]
                          (.sendResponseHeaders exchange 200 (alength body))
                          (.write (.getResponseBody exchange) body)
                          (.close exchange)))))
    (.start server)
    (try
      (f (sdk/create-client {:base-url     (str "http://127.0.0.1:" (.getPort (.getAddress server)))
                             :auth-token   "test-token"
                             :account-id   "acc_example_001"
                             :workspace-id "ws_example_001"
                             :retries      0}))
      @requests
      (finally (.stop server 0)))))

(deftest send-purchase-funnel-event-posts-to-its-endpoint
  (let [result   (atom nil)
        requests (with-server #(reset! result (sdk/send-purchase-funnel-event % (:base shapes))))]
    (is (:success @result))
    (is (= ["POST /events/purchase-funnel"] requests))))

(deftest send-batch-sends-purchase-funnel-events
  (let [result   (atom nil)
        requests (with-server #(reset! result (sdk/send-batch % {:purchase-funnel-events [(:base shapes)]})))]
    (is (:success (:purchase-funnel-events @result)))
    (is (= ["POST /events/purchase-funnel"] requests))))
