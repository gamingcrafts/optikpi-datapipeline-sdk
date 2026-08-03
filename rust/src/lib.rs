//! Official Rust SDK for the OptikPI Data Pipeline API.
//!
//! ```no_run
//! use optikpi_datapipeline_sdk::{Client, ClientConfig};
//! use optikpi_datapipeline_sdk::models::DepositEvent;
//!
//! let client = Client::new(ClientConfig::new(
//!     "https://your-api-gateway-url/apigw/ingest",
//!     std::env::var("AUTH_TOKEN").unwrap(),
//!     std::env::var("ACCOUNT_ID").unwrap(),
//!     std::env::var("WORKSPACE_ID").unwrap(),
//! )).unwrap();
//!
//! let event = DepositEvent::new(
//!     "account-1", "workspace-1", "player_001",
//!     "Successful Deposit", "dep_001", "2026-01-15T10:30:00Z", 100.0,
//! );
//!
//! let result = client.send_deposit_event(&event);
//! println!("status: {}", result.status);
//! ```

pub mod client;
pub mod crypto;
pub mod models;

pub use client::{ApiResponse, BatchRequest, BatchResult, Client, ClientConfig, SdkError};
