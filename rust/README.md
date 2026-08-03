# OptikPI Data Pipeline SDK — Rust

Official Rust client for the OptikPI Data Pipeline API.

## Installation

Add the crate via a git dependency in `Cargo.toml`:

```toml
[dependencies]
optikpi-datapipeline-sdk = { git = "https://github.com/gamingcrafts/optikpi-datapipeline-sdk", branch = "main" }
```

The crate lives in the `rust/` subdirectory of this repository; Cargo locates it by package name when resolving the git dependency, no `path` key needed.

Or clone the repo and depend on it locally:

```toml
[dependencies]
optikpi-datapipeline-sdk = { path = "../optikpi-datapipeline-sdk/rust" }
```

## Requirements

- Rust 1.74+ (2021 edition)

## Quick Start

```rust
use optikpi_datapipeline_sdk::{Client, ClientConfig};
use optikpi_datapipeline_sdk::models::DepositEvent;

fn main() {
    let client = Client::new(ClientConfig::new(
        std::env::var("API_BASE_URL").unwrap(),
        std::env::var("AUTH_TOKEN").unwrap(),
        std::env::var("ACCOUNT_ID").unwrap(),
        std::env::var("WORKSPACE_ID").unwrap(),
    )).expect("invalid client config");

    let event = DepositEvent::new(
        std::env::var("ACCOUNT_ID").unwrap(),
        std::env::var("WORKSPACE_ID").unwrap(),
        "player_001",
        "Successful Deposit",
        "dep_001",
        "2026-01-15T10:30:00Z",
        100.0,
    );

    let validation = event.validate();
    if !validation.is_valid() {
        panic!("validation failed: {:?}", validation.errors);
    }

    let result = client.send_deposit_event(&event);
    println!("status: {}", result.status);
}
```

Run the full example (reads credentials from the environment):

```bash
cd rust
API_BASE_URL=... AUTH_TOKEN=... ACCOUNT_ID=... WORKSPACE_ID=... cargo run --example basic_usage
```

## Client Configuration

`ClientConfig::new(base_url, auth_token, account_id, workspace_id)` sets required fields; override defaults with builder methods:

| Method | Default | Description |
|--------|---------|-------------|
| `.with_timeout(Duration)` | `30s` | Per-request timeout |
| `.with_retries(u32)` | `3` | Retry attempts on 5xx or transport errors |
| `.with_retry_delay(Duration)` | `1000ms` | Base retry delay, doubled per attempt |

## Event Models & Send Methods

Each model exposes a `new(..)` constructor with the API's required fields; optional fields are public and set directly (`event.device = Some("desktop".into())`). `validate()` returns a `ValidationResult { errors: Vec<String> }` — call `.is_valid()` before sending.

| Model | `event_category` | Send method |
|-------|-------------------|-------------|
| `CustomerProfile` | — | `send_customer_profile` |
| `AccountEvent` | `"Account"` | `send_account_event` |
| `DepositEvent` | `"Deposit"` | `send_deposit_event` |
| `WithdrawEvent` | `"Withdraw"` | `send_withdraw_event` |
| `GamingActivityEvent` | `"Gaming Activity"` | `send_gaming_activity_event` |
| `WalletBalanceEvent` | `"Wallet Balance"` | `send_wallet_balance_event` |
| `ReferFriendEvent` | `"Refer Friend"` | `send_refer_friend_event` |
| `SystemEvent` | `"SystemEvent"` | `send_system_event` |
| `CustomerExtEvent` | — (extended attributes) | `send_extended_attributes` |

Every `send_*` method is generic over `T: Serialize`, so passing a `Vec<T>` sends a batch in one request — no separate bulk type is needed.

## Batch Send

Send multiple event types in one call with `BatchRequest`:

```rust
use optikpi_datapipeline_sdk::BatchRequest;

let batch = BatchRequest::default()
    .deposit_events(&vec![deposit1, deposit2])
    .account_events(&account_event);

let result = client.send_batch(&batch);
```

Only populated fields are sent; each accepts a single record or a `Vec`.

## Response Shape

Every send method returns `ApiResponse`:

```rust
pub struct ApiResponse {
    pub success: bool,        // true if status is 2xx
    pub status: u16,          // HTTP status code, 0 on transport failure
    pub data: Option<Value>,  // parsed JSON response body
    pub error: Option<String>,
}
```

## Running Tests

```bash
cd rust
cargo test
```
