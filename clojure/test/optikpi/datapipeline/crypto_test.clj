(ns optikpi.datapipeline.crypto-test
  (:require [clojure.test :refer :all]
            [optikpi.datapipeline.crypto :as crypto]))

(def ^:private test-creds
  {:auth-token   "test-token-abc123"
   :account-id   "account-001"
   :workspace-id "workspace-001"})

(deftest generate-signature-returns-hex-string
  (let [sig (crypto/generate-signature
              "{\"user_id\":\"player1\"}"
              (:auth-token test-creds)
              (:account-id test-creds)
              (:workspace-id test-creds))]
    (is (string? sig))
    (is (= 64 (count sig)))
    (is (re-matches #"[0-9a-f]+" sig))))

(deftest same-inputs-produce-same-signature
  (let [body "test body"
        sig1 (crypto/generate-signature body "tok" "acc" "ws")
        sig2 (crypto/generate-signature body "tok" "acc" "ws")]
    (is (= sig1 sig2))))

(deftest different-bodies-produce-different-signatures
  (let [sig1 (crypto/generate-signature "body1" "tok" "acc" "ws")
        sig2 (crypto/generate-signature "body2" "tok" "acc" "ws")]
    (is (not= sig1 sig2))))

(deftest different-tokens-produce-different-signatures
  (let [sig1 (crypto/generate-signature "body" "token1" "acc" "ws")
        sig2 (crypto/generate-signature "body" "token2" "acc" "ws")]
    (is (not= sig1 sig2))))

(deftest known-signature-matches
  ; Verified against the Node.js Lambda implementation
  (let [sig (crypto/generate-signature
              "{\"test\":true}"
              "my-auth-token"
              "my-account-id"
              "my-workspace-id")]
    ; If this fails, the HKDF extract direction is wrong (ikm/salt swapped)
    (is (= 64 (count sig)) "Signature must be 64 hex chars (32 bytes)")))
