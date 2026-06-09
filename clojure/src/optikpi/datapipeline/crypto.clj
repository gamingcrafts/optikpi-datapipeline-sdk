(ns optikpi.datapipeline.crypto
  (:import (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)
           (java.nio.charset StandardCharsets)))

(def ^:private hmac-algo "HmacSHA256")
(def ^:private key-length 32)

(defn- hmac
  "Computes HMAC-SHA256 of msg using key. Both byte arrays."
  ^bytes [^bytes key ^bytes msg]
  (let [mac (Mac/getInstance hmac-algo)
        spec (SecretKeySpec. key hmac-algo)]
    (.init mac spec)
    (.doFinal mac msg)))

(defn- hkdf-extract
  "CRITICAL: Node.js hkdfSync uses ikm as HMAC key and salt as message —
   reversed from RFC 5869. Must match to interoperate with the Lambda."
  ^bytes [^bytes ikm ^bytes salt]
  (hmac ikm salt))

(defn- hkdf-expand
  ^bytes [^bytes prk ^bytes info ^long length]
  (let [result (byte-array length)]
    (loop [t      (byte-array 0)
           offset 0
           counter 1]
      (when (< offset length)
        (let [mac  (Mac/getInstance hmac-algo)
              spec (SecretKeySpec. prk hmac-algo)
              _    (.init mac spec)
              _    (.update mac t)
              _    (.update mac info)
              _    (.update mac (byte counter))
              t'   (.doFinal mac)
              n    (min (alength t') (- length offset))]
          (System/arraycopy t' 0 result offset n)
          (recur t' (+ offset n) (inc counter)))))
    result))

(defn- bytes->hex [^bytes bs]
  (apply str (map #(format "%02x" (bit-and % 0xff)) bs)))

(defn derive-key
  "Derives a 32-byte signing key via HKDF from auth credentials."
  ^bytes [auth-token account-id workspace-id]
  (let [ikm  (.getBytes ^String auth-token StandardCharsets/UTF_8)
        salt (.getBytes ^String (str account-id workspace-id) StandardCharsets/UTF_8)
        info (.getBytes "hmac-signing" StandardCharsets/UTF_8)
        prk  (hkdf-extract ikm salt)]
    (hkdf-expand prk info key-length)))

(defn generate-signature
  "Returns hex HMAC-SHA256 signature for a JSON body string."
  [^String body auth-token account-id workspace-id]
  (let [key  (derive-key auth-token account-id workspace-id)
        data (.getBytes body StandardCharsets/UTF_8)]
    (bytes->hex (hmac key data))))
