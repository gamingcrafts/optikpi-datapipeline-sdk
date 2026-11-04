use std::io::{BufRead, BufReader, Read, Write};
use std::net::TcpListener;
use std::sync::mpsc;
use std::thread;
use std::time::{Duration, SystemTime, UNIX_EPOCH};

use optikpi_datapipeline_sdk::models::{PurchaseFunnelEvent, FAILURE_REASONS, FUNNEL_STAGES};
use optikpi_datapipeline_sdk::{BatchRequest, Client, ClientConfig};
use serde_json::{Map, Value};

// 2026-11-04T10:00:00Z
const NOW_MS: f64 = 1_793_786_400_000.0;

/// Shared with the API's tests: the client and the server must agree on every shape.
fn shapes() -> Value {
    let path = concat!(
        env!("CARGO_MANIFEST_DIR"),
        "/../testdata/purchase-funnel-shapes.json"
    );
    serde_json::from_str(&std::fs::read_to_string(path).expect("read shapes"))
        .expect("parse shapes")
}

fn build_record(shapes: &Value, case: &Value) -> Value {
    let from = if case.get("from").and_then(Value::as_str) == Some("full") {
        "full"
    } else {
        "base"
    };
    let mut record: Map<String, Value> = shapes[from].as_object().unwrap().clone();
    for field in case
        .get("unset")
        .and_then(Value::as_array)
        .into_iter()
        .flatten()
    {
        record.remove(field.as_str().unwrap());
    }
    for (key, value) in case
        .get("set")
        .and_then(Value::as_object)
        .into_iter()
        .flatten()
    {
        record.insert(key.clone(), value.clone());
    }
    Value::Object(record)
}

#[test]
fn validate_json_matches_shared_shapes() {
    let shapes = shapes();
    let cases = shapes["cases"].as_array().unwrap();
    assert!(!cases.is_empty());
    let mut failures = Vec::new();
    for case in cases {
        let name = case["name"].as_str().unwrap();
        let valid = case["valid"].as_bool().unwrap();
        let result = PurchaseFunnelEvent::validate_json_at(&build_record(&shapes, case), NOW_MS);
        if result.is_valid() != valid {
            failures.push(format!(
                "{name}: expected valid={valid}, errors={:?}",
                result.errors
            ));
        } else if !valid {
            let field = case["errorField"].as_str().unwrap();
            if !result.errors.iter().any(|e| e.contains(field)) {
                failures.push(format!(
                    "{name}: no error names {field}: {:?}",
                    result.errors
                ));
            }
        }
    }
    assert!(failures.is_empty(), "{failures:#?}");
}

#[test]
fn window_defaults_to_now() {
    let shapes = shapes();
    let now = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs();
    let mut event: PurchaseFunnelEvent = serde_json::from_value(shapes["base"].clone()).unwrap();
    event.event_time = iso_utc(now - 60);
    assert!(event.validate().is_valid(), "{:?}", event.validate().errors);
    event.event_time = iso_utc(now - 11 * 365 * 86_400);
    assert!(!event.validate().is_valid());
}

/// `YYYY-MM-DDTHH:MM:SSZ` for seconds since the epoch (civil_from_days).
fn iso_utc(secs: u64) -> String {
    let days = (secs / 86_400) as i64;
    let rem = secs % 86_400;
    let z = days + 719_468;
    let era = z / 146_097;
    let doe = z - era * 146_097;
    let yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365;
    let doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
    let mp = (5 * doy + 2) / 153;
    let day = doy - (153 * mp + 2) / 5 + 1;
    let month = if mp < 10 { mp + 3 } else { mp - 9 };
    let year = yoe + era * 400 + i64::from(month <= 2);
    format!(
        "{year:04}-{month:02}-{day:02}T{:02}:{:02}:{:02}Z",
        rem / 3600,
        rem % 3600 / 60,
        rem % 60
    )
}

#[test]
fn new_sets_event_category_and_validates() {
    let event = PurchaseFunnelEvent::new(
        "acc_example_001",
        "ws_example_001",
        "user_example_001",
        "checkout_started",
        "evt_example_001",
        "2026-11-04T10:00:00Z",
        "fs_example_001",
        "checkout",
    );
    assert_eq!(event.event_category, "Purchase Funnel");
    assert!(
        event.validate_at(NOW_MS).is_valid(),
        "{:?}",
        event.validate_at(NOW_MS).errors
    );
    let value = serde_json::to_value(&event).unwrap();
    assert!(
        value.get("cart_total").is_none(),
        "None fields are not serialized"
    );
}

#[test]
fn typed_validate_rejects_non_finite_amount() {
    let mut event: PurchaseFunnelEvent = serde_json::from_value(shapes()["base"].clone()).unwrap();
    event.cart_total = Some(f64::NAN);
    let result = event.validate_at(NOW_MS);
    assert!(
        result.errors.iter().any(|e| e.contains("cart_total")),
        "{:?}",
        result.errors
    );
}

#[test]
fn constants_in_contract_order() {
    assert_eq!(
        FUNNEL_STAGES,
        [
            "start",
            "browse",
            "configure",
            "cart",
            "checkout",
            "payment",
            "payment_failed",
            "complete",
            "failed",
            "cancelled",
            "exit",
            "stalled",
            "abandoned"
        ]
    );
    assert_eq!(
        FAILURE_REASONS,
        [
            "payment_declined",
            "insufficient_funds",
            "provider_timeout",
            "limit_reached",
            "kyc_required",
            "geo_blocked",
            "draw_closed",
            "technical_error",
            "user_cancelled"
        ]
    );
}

/// A one-request HTTP server on a free local port; sends back the request line it received.
fn serve_once() -> (String, mpsc::Receiver<String>) {
    let listener = TcpListener::bind("127.0.0.1:0").unwrap();
    let base_url = format!("http://{}", listener.local_addr().unwrap());
    let (tx, rx) = mpsc::channel();
    thread::spawn(move || {
        let (stream, _) = listener.accept().unwrap();
        let mut reader = BufReader::new(stream);
        let mut request_line = String::new();
        reader.read_line(&mut request_line).unwrap();
        let mut content_length = 0;
        loop {
            let mut header = String::new();
            reader.read_line(&mut header).unwrap();
            if header == "\r\n" || header.is_empty() {
                break;
            }
            if let Some(v) = header.to_ascii_lowercase().strip_prefix("content-length:") {
                content_length = v.trim().parse().unwrap();
            }
        }
        let mut body = vec![0; content_length];
        reader.read_exact(&mut body).unwrap();
        let mut stream = reader.into_inner();
        let response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: 2\r\nConnection: close\r\n\r\n{}";
        stream.write_all(response.as_bytes()).unwrap();
        tx.send(request_line.trim_end().to_string()).unwrap();
    });
    (base_url, rx)
}

fn client(base_url: &str) -> Client {
    Client::new(
        ClientConfig::new(base_url, "test-token", "acc_example_001", "ws_example_001")
            .with_retries(0),
    )
    .unwrap()
}

#[test]
fn send_purchase_funnel_event_posts_to_its_endpoint() {
    let (base_url, rx) = serve_once();
    let event: PurchaseFunnelEvent = serde_json::from_value(shapes()["base"].clone()).unwrap();
    let response = client(&base_url).send_purchase_funnel_event(&event);
    assert!(response.success, "{:?}", response.error);
    assert_eq!(
        rx.recv_timeout(Duration::from_secs(5)).unwrap(),
        "POST /events/purchase-funnel HTTP/1.1"
    );
}

#[test]
fn send_batch_sends_purchase_funnel_events() {
    let (base_url, rx) = serve_once();
    let event: PurchaseFunnelEvent = serde_json::from_value(shapes()["base"].clone()).unwrap();
    let result =
        client(&base_url).send_batch(&BatchRequest::default().purchase_funnel_events(&vec![event]));
    assert!(result.purchase_funnel_events.expect("slot sent").success);
    assert_eq!(
        rx.recv_timeout(Duration::from_secs(5)).unwrap(),
        "POST /events/purchase-funnel HTTP/1.1"
    );
}
