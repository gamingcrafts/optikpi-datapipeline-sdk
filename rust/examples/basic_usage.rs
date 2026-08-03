use std::time::{SystemTime, UNIX_EPOCH};

use optikpi_datapipeline_sdk::models::{CustomerProfile, DepositEvent};
use optikpi_datapipeline_sdk::{BatchRequest, Client, ClientConfig};

fn main() {
    let client = Client::new(ClientConfig::new(
        std::env::var("API_BASE_URL").expect("API_BASE_URL is required"),
        std::env::var("AUTH_TOKEN").expect("AUTH_TOKEN is required"),
        std::env::var("ACCOUNT_ID").expect("ACCOUNT_ID is required"),
        std::env::var("WORKSPACE_ID").expect("WORKSPACE_ID is required"),
    ))
    .expect("invalid client config");

    let now = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_millis();

    // Send a deposit event
    let deposit = DepositEvent::new(
        std::env::var("ACCOUNT_ID").unwrap(),
        std::env::var("WORKSPACE_ID").unwrap(),
        "player_001",
        "Successful Deposit",
        format!("dep_{now}"),
        "2026-01-15T10:30:00Z",
        100.00,
    );
    let validation = deposit.validate();
    if !validation.is_valid() {
        eprintln!("Validation failed: {:?}", validation.errors);
    } else {
        let result = client.send_deposit_event(&deposit);
        println!("Deposit sent: {}", result.status);
    }

    // Send a customer profile
    let mut profile = CustomerProfile::new(
        std::env::var("ACCOUNT_ID").unwrap(),
        std::env::var("WORKSPACE_ID").unwrap(),
        "player_001",
        "2026-01-15T10:00:00Z",
    );
    profile.email = Some("player@example.com".to_string());
    profile.first_name = Some("Alex".to_string());
    profile.last_name = Some("Smith".to_string());
    profile.country = Some("FI".to_string());

    let validation = profile.validate();
    if !validation.is_valid() {
        eprintln!("Validation failed: {:?}", validation.errors);
    } else {
        let result = client.send_customer_profile(&profile);
        println!("Profile sent: {}", result.status);
    }

    // Batch send
    let another_deposit = DepositEvent::new(
        std::env::var("ACCOUNT_ID").unwrap(),
        std::env::var("WORKSPACE_ID").unwrap(),
        "player_002",
        "Successful Deposit",
        format!("dep_{}", now + 1),
        "2026-01-15T11:00:00Z",
        50.0,
    );
    let batch = BatchRequest::default().deposit_events(&vec![deposit, another_deposit]);
    let batch_result = client.send_batch(&batch);
    println!("Batch result: {batch_result:?}");
}
