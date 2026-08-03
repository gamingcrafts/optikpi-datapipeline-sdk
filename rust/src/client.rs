use std::fmt;
use std::time::Duration;

use serde::Serialize;
use serde_json::Value;

use crate::crypto;

/// Client configuration. Required fields have no default and must be
/// supplied to [`ClientConfig::new`]; everything else has a sane default
/// and can be overridden with the `with_*` builder methods.
#[derive(Clone)]
pub struct ClientConfig {
    pub base_url: String,
    pub auth_token: String,
    pub account_id: String,
    pub workspace_id: String,
    pub timeout: Duration,
    pub retries: u32,
    pub retry_delay: Duration,
}

impl fmt::Debug for ClientConfig {
    /// Redacts `auth_token` so it can't leak through `{:?}` logging, panic
    /// messages, or `dbg!()`.
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.debug_struct("ClientConfig")
            .field("base_url", &self.base_url)
            .field("auth_token", &"<redacted>")
            .field("account_id", &self.account_id)
            .field("workspace_id", &self.workspace_id)
            .field("timeout", &self.timeout)
            .field("retries", &self.retries)
            .field("retry_delay", &self.retry_delay)
            .finish()
    }
}

impl ClientConfig {
    pub fn new(
        base_url: impl Into<String>,
        auth_token: impl Into<String>,
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
    ) -> Self {
        Self {
            base_url: base_url.into(),
            auth_token: auth_token.into(),
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            timeout: Duration::from_secs(30),
            retries: 3,
            retry_delay: Duration::from_millis(1000),
        }
    }

    pub fn with_timeout(mut self, timeout: Duration) -> Self {
        self.timeout = timeout;
        self
    }

    /// Clamped to 10 — beyond that, exponential backoff delays become
    /// impractically long and risk overflow in the backoff calculation.
    pub fn with_retries(mut self, retries: u32) -> Self {
        self.retries = retries.min(10);
        self
    }

    pub fn with_retry_delay(mut self, retry_delay: Duration) -> Self {
        self.retry_delay = retry_delay;
        self
    }
}

#[derive(Debug)]
pub enum SdkError {
    InvalidConfig(String),
    Http(reqwest::Error),
}

impl fmt::Display for SdkError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            SdkError::InvalidConfig(msg) => write!(f, "invalid config: {msg}"),
            SdkError::Http(err) => write!(f, "http error: {err}"),
        }
    }
}

impl std::error::Error for SdkError {}

impl From<reqwest::Error> for SdkError {
    fn from(err: reqwest::Error) -> Self {
        SdkError::Http(err)
    }
}

/// Uniform response shape returned by every send method.
#[derive(Debug, Clone)]
pub struct ApiResponse {
    pub success: bool,
    /// HTTP status code, or `0` on a connection/transport failure.
    pub status: u16,
    pub data: Option<Value>,
    pub error: Option<String>,
}

/// Batch payload builder. Only populated fields are sent; each accepts
/// either a single record or a `Vec` of records.
///
/// A slot holds `Err` if the record failed to serialize (e.g. a `NaN` or
/// infinite float amount) — `send_batch` reports that as a failed result
/// for the slot rather than silently dropping it from the batch.
#[derive(Debug, Default)]
pub struct BatchRequest {
    customers: Option<Result<Value, String>>,
    account_events: Option<Result<Value, String>>,
    deposit_events: Option<Result<Value, String>>,
    withdraw_events: Option<Result<Value, String>>,
    gaming_events: Option<Result<Value, String>>,
    wallet_balance_events: Option<Result<Value, String>>,
    refer_friend_events: Option<Result<Value, String>>,
    system_events: Option<Result<Value, String>>,
    extended_attributes: Option<Result<Value, String>>,
}

macro_rules! batch_setter {
    ($name:ident) => {
        pub fn $name<T: Serialize>(mut self, data: &T) -> Self {
            self.$name = Some(serde_json::to_value(data).map_err(|e| e.to_string()));
            self
        }
    };
}

impl BatchRequest {
    batch_setter!(customers);
    batch_setter!(account_events);
    batch_setter!(deposit_events);
    batch_setter!(withdraw_events);
    batch_setter!(gaming_events);
    batch_setter!(wallet_balance_events);
    batch_setter!(refer_friend_events);
    batch_setter!(system_events);
    batch_setter!(extended_attributes);
}

/// Results of a batch send, one slot per event type present in the request.
#[derive(Debug, Default)]
pub struct BatchResult {
    pub customers: Option<ApiResponse>,
    pub account_events: Option<ApiResponse>,
    pub deposit_events: Option<ApiResponse>,
    pub withdraw_events: Option<ApiResponse>,
    pub gaming_events: Option<ApiResponse>,
    pub wallet_balance_events: Option<ApiResponse>,
    pub refer_friend_events: Option<ApiResponse>,
    pub system_events: Option<ApiResponse>,
    pub extended_attributes: Option<ApiResponse>,
}

/// Data Pipeline API client. Handles authentication, retries, and every
/// ingest endpoint.
pub struct Client {
    config: ClientConfig,
    http: reqwest::blocking::Client,
}

impl Client {
    pub fn new(config: ClientConfig) -> Result<Self, SdkError> {
        if config.base_url.is_empty() {
            return Err(SdkError::InvalidConfig("base_url is required".into()));
        }
        if config.auth_token.is_empty() {
            return Err(SdkError::InvalidConfig("auth_token is required".into()));
        }
        if config.account_id.is_empty() {
            return Err(SdkError::InvalidConfig("account_id is required".into()));
        }
        if config.workspace_id.is_empty() {
            return Err(SdkError::InvalidConfig("workspace_id is required".into()));
        }

        let http = reqwest::blocking::Client::builder()
            .timeout(config.timeout)
            .build()?;

        Ok(Self { config, http })
    }

    fn send_request<T: Serialize>(&self, endpoint: &str, data: &T) -> ApiResponse {
        let body = match serde_json::to_string(data) {
            Ok(body) => body,
            Err(err) => {
                return ApiResponse {
                    success: false,
                    status: 0,
                    data: None,
                    error: Some(format!("failed to serialize request body: {err}")),
                }
            }
        };
        let url = format!("{}{}", self.config.base_url, endpoint);
        let signature = crypto::generate_signature(
            &body,
            &self.config.auth_token,
            &self.config.account_id,
            &self.config.workspace_id,
        );

        let mut attempt = 0;
        loop {
            let outcome = self
                .http
                .post(url.as_str())
                .header("Content-Type", "application/json")
                .header("x-optikpi-token", self.config.auth_token.as_str())
                .header("x-optikpi-account-id", self.config.account_id.as_str())
                .header("x-optikpi-workspace-id", self.config.workspace_id.as_str())
                .header("x-hmac-signature", signature.as_str())
                .header("x-hmac-algorithm", "sha256")
                .body(body.clone())
                .send();

            let (status, data, transport_error) = match outcome {
                Ok(resp) => {
                    let status = resp.status().as_u16();
                    let text = resp.text().unwrap_or_default();
                    let data = if text.is_empty() {
                        None
                    } else {
                        serde_json::from_str(&text).ok()
                    };
                    (status, data, None)
                }
                Err(err) => (0, None, Some(err.to_string())),
            };

            let success = (200..300).contains(&status);
            let should_retry = attempt < self.config.retries && (status == 0 || status >= 500);

            if !success && should_retry {
                attempt += 1;
                std::thread::sleep(self.config.retry_delay * 2u32.pow(attempt - 1));
                continue;
            }

            let error = if success {
                None
            } else {
                transport_error.or_else(|| {
                    data.as_ref()
                        .and_then(|d: &Value| d.get("message"))
                        .and_then(|m| m.as_str())
                        .map(str::to_string)
                        .or_else(|| Some(format!("request failed with status {status}")))
                })
            };

            return ApiResponse {
                success,
                status,
                data,
                error,
            };
        }
    }

    pub fn send_customer_profile<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/customers", data)
    }

    pub fn send_account_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/account", data)
    }

    pub fn send_deposit_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/deposit", data)
    }

    pub fn send_withdraw_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/withdraw", data)
    }

    pub fn send_gaming_activity_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/gaming-activity", data)
    }

    pub fn send_wallet_balance_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/wallet-balance", data)
    }

    pub fn send_refer_friend_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/refer-friend", data)
    }

    pub fn send_system_event<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/events/system-events", data)
    }

    pub fn send_extended_attributes<T: Serialize>(&self, data: &T) -> ApiResponse {
        self.send_request("/extattributes", data)
    }

    /// Sends a pre-serialized batch slot, or synthesizes a failed
    /// [`ApiResponse`] (no network call) if the record failed to serialize.
    fn send_slot(&self, endpoint: &str, slot: &Option<Result<Value, String>>) -> Option<ApiResponse> {
        slot.as_ref().map(|result| match result {
            Ok(value) => self.send_request(endpoint, value),
            Err(err) => ApiResponse {
                success: false,
                status: 0,
                data: None,
                error: Some(format!("failed to serialize request body: {err}")),
            },
        })
    }

    /// Sends every populated event type in `batch` sequentially and
    /// collects the results.
    pub fn send_batch(&self, batch: &BatchRequest) -> BatchResult {
        BatchResult {
            customers: self.send_slot("/customers", &batch.customers),
            account_events: self.send_slot("/events/account", &batch.account_events),
            deposit_events: self.send_slot("/events/deposit", &batch.deposit_events),
            withdraw_events: self.send_slot("/events/withdraw", &batch.withdraw_events),
            gaming_events: self.send_slot("/events/gaming-activity", &batch.gaming_events),
            wallet_balance_events: self.send_slot("/events/wallet-balance", &batch.wallet_balance_events),
            refer_friend_events: self.send_slot("/events/refer-friend", &batch.refer_friend_events),
            system_events: self.send_slot("/events/system-events", &batch.system_events),
            extended_attributes: self.send_slot("/extattributes", &batch.extended_attributes),
        }
    }
}
