# OptikPI Data Pipeline SDK — Clojure

Official Clojure client for the OptikPI Data Pipeline API.

## Installation

### Leiningen (`project.clj`)
```clojure
:dependencies [[com.optikpi/datapipeline-sdk "1.0.0"]]
:repositories [["gamingcrafts" {:url "https://maven.pkg.github.com/gamingcrafts/optikpi-datapipeline-sdk"
                                :username [:gpg :env/GITHUB_ACTOR]
                                :password [:gpg :env/GITHUB_TOKEN]}]]
```

### tools.deps (`deps.edn`)
```clojure
{:deps {com.optikpi/datapipeline-sdk
        {:git/url "https://github.com/gamingcrafts/optikpi-datapipeline-sdk"
         :git/sha "<latest-sha>"
         :deps/root "clojure"}}}
```

## Quick Start

```clojure
(require '[optikpi.datapipeline.core :as sdk]
         '[optikpi.datapipeline.models.deposit-event :as deposit])

(def client
  (sdk/create-client
    {:base-url     "https://api.optikpi.com"
     :auth-token   (System/getenv "OPTIKPI_AUTH_TOKEN")
     :account-id   (System/getenv "OPTIKPI_ACCOUNT_ID")
     :workspace-id (System/getenv "OPTIKPI_WORKSPACE_ID")}))

(let [event (deposit/build
              {:user_id        "player_001"
               :event_name     "Successful Deposit"
               :event_id       "dep_001"
               :event_time     "2026-01-15T10:30:00Z"
               :amount         100.0
               :payment_method "credit_card"
               :transaction_id "txn_abc123"})
      {:keys [valid? errors]} (deposit/validate event)]
  (if valid?
    (sdk/send-deposit-event client event)
    (throw (ex-info "Validation failed" {:errors errors}))))
```

## Client Configuration

| Key | Required | Default | Description |
|-----|----------|---------|-------------|
| `:base-url` | ✅ | — | API endpoint URL |
| `:auth-token` | ✅ | — | Operator auth token |
| `:account-id` | ✅ | — | Operator account ID |
| `:workspace-id` | ✅ | — | Workspace ID |
| `:timeout` | | `30000` | Request timeout (ms) |
| `:retries` | | `3` | Retry attempts on 5xx errors |
| `:retry-delay` | | `1000` | Base retry delay (ms), multiplied per retry |

## Event Types & Namespaces

| Namespace | `build` sets `event_category` | Send method |
|-----------|-------------------------------|-------------|
| `models.customer-profile` | N/A | `send-customer-profile` |
| `models.account-event` | `"Account"` | `send-account-event` |
| `models.deposit-event` | `"Deposit"` | `send-deposit-event` |
| `models.withdraw-event` | `"Withdraw"` | `send-withdraw-event` |
| `models.gaming-activity-event` | `"Gaming Activity"` | `send-gaming-activity-event` |
| `models.wallet-balance-event` | `"Wallet Balance"` | `send-wallet-balance-event` |
| `models.refer-friend-event` | `"Refer Friend"` | `send-refer-friend-event` |
| `models.customer-ext-event` | N/A | `send-extended-attributes` |
| `models.system-event` | `"SystemEvent"` | `send-system-event` |

## Validation

Each model namespace exposes a `validate` function returning `{:valid? bool :errors []}`:

```clojure
(let [{:keys [valid? errors]} (deposit/validate event)]
  (when-not valid?
    (println "Errors:" errors)))
```

`build` sets `event_category` automatically — do not pass it manually.

## Batch Send

```clojure
(sdk/send-batch client
  {:deposit-events  (deposit/build {...})
   :account-events  (account/build {...})})
```

All event types in the batch map are sent in parallel. Keys match the send methods above with `-events` suffix (`:deposit-events`, `:account-events`, etc.). Nil keys are skipped.

## Response Shape

```clojure
{:status  200      ; HTTP status code (0 on connection error)
 :success true     ; true if status < 300
 :data    {...}    ; parsed JSON response body
 :error   nil}     ; error message string on failure
```

## Running Tests

```bash
cd clojure
lein test
# or
clojure -M:test -m cognitect.test-runner
```
