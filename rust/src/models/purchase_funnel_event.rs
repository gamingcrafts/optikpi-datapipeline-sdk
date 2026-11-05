use serde::{Deserialize, Serialize};
use serde_json::Value;

use super::{ok, ValidationResult};

/// Allowed `funnel_stage` values, in funnel order.
pub const FUNNEL_STAGES: [&str; 13] = [
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
    "abandoned",
];

/// Allowed `failure_reason` values.
pub const FAILURE_REASONS: [&str; 9] = [
    "payment_declined",
    "insufficient_funds",
    "provider_timeout",
    "limit_reached",
    "kyc_required",
    "geo_blocked",
    "draw_closed",
    "technical_error",
    "user_cancelled",
];

const EVENT_CATEGORY: &str = "Purchase Funnel";

const REQUIRED_FIELDS: [&str; 9] = [
    "account_id",
    "workspace_id",
    "user_id",
    "event_category",
    "event_name",
    "event_id",
    "event_time",
    "funnel_session_id",
    "funnel_stage",
];

#[derive(Clone, Copy)]
enum Kind {
    String,
    Money,
    Count,
    Boolean,
    DateTime,
}

const FIELD_KINDS: [(&str, Kind); 66] = [
    ("account_id", Kind::String),
    ("workspace_id", Kind::String),
    ("user_id", Kind::String),
    ("event_id", Kind::String),
    ("event_time", Kind::DateTime),
    ("event_category", Kind::String),
    ("event_name", Kind::String),
    ("funnel_session_id", Kind::String),
    ("funnel_stage", Kind::String),
    ("item_id", Kind::String),
    ("item_name", Kind::String),
    ("item_category", Kind::String),
    ("draw_id", Kind::String),
    ("draw_number", Kind::String),
    ("draw_date", Kind::DateTime),
    ("draw_close_time", Kind::DateTime),
    ("jackpot_amount", Kind::Money),
    ("selection", Kind::String),
    ("selection_method", Kind::String),
    ("lines_count", Kind::Count),
    ("multi_draw_count", Kind::Count),
    ("unit_price", Kind::Money),
    ("item_quantity", Kind::Count),
    ("item_amount", Kind::Money),
    ("cart_id", Kind::String),
    ("cart_item_count", Kind::Count),
    ("cart_quantity_total", Kind::Count),
    ("cart_items", Kind::String),
    ("cart_subtotal", Kind::Money),
    ("discount_amount", Kind::Money),
    ("bonus_used_amount", Kind::Money),
    ("fees_amount", Kind::Money),
    ("tax_amount", Kind::Money),
    ("cart_total", Kind::Money),
    ("coupon_code", Kind::String),
    ("currency", Kind::String),
    ("payment_method", Kind::String),
    ("payment_provider_id", Kind::String),
    ("payment_provider_name", Kind::String),
    ("is_saved_method", Kind::Boolean),
    ("funds_source", Kind::String),
    ("payment_attempt_number", Kind::Count),
    ("payment_status", Kind::String),
    ("transaction_id", Kind::String),
    ("failure_reason", Kind::String),
    ("failure_code", Kind::String),
    ("failure_message", Kind::String),
    ("order_id", Kind::String),
    ("ticket_ids", Kind::String),
    ("order_total", Kind::Money),
    ("order_status", Kind::String),
    ("is_first_purchase", Kind::Boolean),
    ("device", Kind::String),
    ("platform", Kind::String),
    ("app_version", Kind::String),
    ("page_url", Kind::String),
    ("referrer", Kind::String),
    ("utm_source", Kind::String),
    ("utm_medium", Kind::String),
    ("utm_campaign", Kind::String),
    ("campaign_id", Kind::String),
    ("affiliate_id", Kind::String),
    ("entry_point", Kind::String),
    ("is_resumed", Kind::Boolean),
    ("abandoned_at_stage", Kind::String),
    ("inactivity_sec", Kind::Count),
];

// Same window the event store accepts on its partition column.
const TEN_YEARS_MS: f64 = 10.0 * 365.25 * 24.0 * 60.0 * 60.0 * 1000.0;
const ONE_YEAR_MS: f64 = 365.25 * 24.0 * 60.0 * 60.0 * 1000.0;

/// Purchase funnel event: one step of a purchase attempt (a funnel session).
///
/// Every event of one attempt shares a `funnel_session_id`. Its meaning comes
/// from `funnel_stage` (one of [`FUNNEL_STAGES`]); `event_name` is the
/// operator's own free text.
///
/// [`validate`](Self::validate) checks an event built in code, where the field
/// types already rule out wrong JSON kinds.
/// [`validate_json`](Self::validate_json) checks a raw JSON record: each
/// field's JSON kind first, then everything `validate` checks.
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct PurchaseFunnelEvent {
    pub account_id: String,
    pub workspace_id: String,
    pub user_id: String,
    pub event_id: String,
    pub event_time: String,
    pub event_category: String,
    pub event_name: String,
    pub funnel_session_id: String,
    pub funnel_stage: String,
    // Item
    #[serde(skip_serializing_if = "Option::is_none")]
    pub item_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub item_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub item_category: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub draw_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub draw_number: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub draw_date: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub draw_close_time: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub jackpot_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub selection: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub selection_method: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub lines_count: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub multi_draw_count: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub unit_price: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub item_quantity: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub item_amount: Option<f64>,
    // Cart snapshot
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cart_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cart_item_count: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cart_quantity_total: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cart_items: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cart_subtotal: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub discount_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub bonus_used_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub fees_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub tax_amount: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub cart_total: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub coupon_code: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub currency: Option<String>,
    // Payment
    #[serde(skip_serializing_if = "Option::is_none")]
    pub payment_method: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub payment_provider_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub payment_provider_name: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub is_saved_method: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub funds_source: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub payment_attempt_number: Option<i32>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub payment_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub transaction_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub failure_reason: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub failure_code: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub failure_message: Option<String>,
    // Order
    #[serde(skip_serializing_if = "Option::is_none")]
    pub order_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub ticket_ids: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub order_total: Option<f64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub order_status: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub is_first_purchase: Option<bool>,
    // Context
    #[serde(skip_serializing_if = "Option::is_none")]
    pub device: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub platform: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub app_version: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub page_url: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub referrer: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_source: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_medium: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub utm_campaign: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub campaign_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub affiliate_id: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub entry_point: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub is_resumed: Option<bool>,
    // Set by OptikPI on derived events
    #[serde(skip_serializing_if = "Option::is_none")]
    pub abandoned_at_stage: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub inactivity_sec: Option<i32>,
}

impl PurchaseFunnelEvent {
    #[allow(clippy::too_many_arguments)]
    pub fn new(
        account_id: impl Into<String>,
        workspace_id: impl Into<String>,
        user_id: impl Into<String>,
        event_name: impl Into<String>,
        event_id: impl Into<String>,
        event_time: impl Into<String>,
        funnel_session_id: impl Into<String>,
        funnel_stage: impl Into<String>,
    ) -> Self {
        Self {
            account_id: account_id.into(),
            workspace_id: workspace_id.into(),
            user_id: user_id.into(),
            event_id: event_id.into(),
            event_time: event_time.into(),
            event_category: EVENT_CATEGORY.to_string(),
            event_name: event_name.into(),
            funnel_session_id: funnel_session_id.into(),
            funnel_stage: funnel_stage.into(),
            item_id: None,
            item_name: None,
            item_category: None,
            draw_id: None,
            draw_number: None,
            draw_date: None,
            draw_close_time: None,
            jackpot_amount: None,
            selection: None,
            selection_method: None,
            lines_count: None,
            multi_draw_count: None,
            unit_price: None,
            item_quantity: None,
            item_amount: None,
            cart_id: None,
            cart_item_count: None,
            cart_quantity_total: None,
            cart_items: None,
            cart_subtotal: None,
            discount_amount: None,
            bonus_used_amount: None,
            fees_amount: None,
            tax_amount: None,
            cart_total: None,
            coupon_code: None,
            currency: None,
            payment_method: None,
            payment_provider_id: None,
            payment_provider_name: None,
            is_saved_method: None,
            funds_source: None,
            payment_attempt_number: None,
            payment_status: None,
            transaction_id: None,
            failure_reason: None,
            failure_code: None,
            failure_message: None,
            order_id: None,
            ticket_ids: None,
            order_total: None,
            order_status: None,
            is_first_purchase: None,
            device: None,
            platform: None,
            app_version: None,
            page_url: None,
            referrer: None,
            utm_source: None,
            utm_medium: None,
            utm_campaign: None,
            campaign_id: None,
            affiliate_id: None,
            entry_point: None,
            is_resumed: None,
            abandoned_at_stage: None,
            inactivity_sec: None,
        }
    }

    pub fn validate(&self) -> ValidationResult {
        self.validate_at(now_ms())
    }

    /// [`validate`](Self::validate) with an explicit current time in
    /// milliseconds since the epoch, for the `event_time` window.
    pub fn validate_at(&self, now_ms: f64) -> ValidationResult {
        let mut errors = Vec::new();

        for (name, value) in [
            ("account_id", &self.account_id),
            ("workspace_id", &self.workspace_id),
            ("user_id", &self.user_id),
            ("event_category", &self.event_category),
            ("event_name", &self.event_name),
            ("event_id", &self.event_id),
            ("event_time", &self.event_time),
            ("funnel_session_id", &self.funnel_session_id),
            ("funnel_stage", &self.funnel_stage),
        ] {
            if value.is_empty() {
                errors.push(format!("{name} is required"));
            }
        }
        if !self.event_category.is_empty() && self.event_category != EVENT_CATEGORY {
            errors.push(format!(
                r#"event_category must be "{EVENT_CATEGORY}" for purchase funnel events"#
            ));
        }
        if !self.funnel_stage.is_empty() && !FUNNEL_STAGES.contains(&self.funnel_stage.as_str()) {
            errors.push(format!(
                "funnel_stage must be one of: {}",
                FUNNEL_STAGES.join(", ")
            ));
        }
        if let Some(reason) = &self.failure_reason {
            if !FAILURE_REASONS.contains(&reason.as_str()) {
                errors.push(format!(
                    "failure_reason must be one of: {}",
                    FAILURE_REASONS.join(", ")
                ));
            }
        }
        if let Some(currency) = &self.currency {
            if !(currency.len() == 3 && currency.chars().all(|c| c.is_ascii_uppercase())) {
                errors.push("currency must be a valid 3-letter ISO currency code".to_string());
            }
        }
        for (name, value) in [
            ("jackpot_amount", self.jackpot_amount),
            ("unit_price", self.unit_price),
            ("item_amount", self.item_amount),
            ("cart_subtotal", self.cart_subtotal),
            ("discount_amount", self.discount_amount),
            ("bonus_used_amount", self.bonus_used_amount),
            ("fees_amount", self.fees_amount),
            ("tax_amount", self.tax_amount),
            ("cart_total", self.cart_total),
            ("order_total", self.order_total),
        ] {
            match value {
                Some(v) if !v.is_finite() => errors.push(format!("{name} must be a finite number")),
                Some(v) if !fits_money(v) => errors.push(format!(
                    "{name} must fit decimal(15,2): below 10^13 with at most 2 decimal places"
                )),
                _ => {}
            }
        }
        let event_time = (!self.event_time.is_empty()).then_some(&self.event_time);
        for (name, value) in [
            ("event_time", event_time),
            ("draw_date", self.draw_date.as_ref()),
            ("draw_close_time", self.draw_close_time.as_ref()),
        ] {
            if value.is_some_and(|v| parse_datetime_ms(v).is_none()) {
                errors.push(format!(
                    "{name} must be an ISO 8601 date-time with seconds and a zone (YYYY-MM-DDTHH:mm:ss[.fff]Z or +hh:mm)"
                ));
            }
        }
        if let Some(event_ms) = parse_datetime_ms(&self.event_time) {
            if event_ms < now_ms - TEN_YEARS_MS || event_ms > now_ms + ONE_YEAR_MS {
                errors.push(
                    "event_time must be within the last 10 years and no more than 1 year in the future".to_string(),
                );
            }
        }

        ok(errors)
    }

    /// Validates a raw JSON record, as it would be sent: the JSON kind of
    /// every field, then [`validate`](Self::validate). Unknown extra fields
    /// are accepted.
    pub fn validate_json(record: &Value) -> ValidationResult {
        Self::validate_json_at(record, now_ms())
    }

    /// [`validate_json`](Self::validate_json) with an explicit current time in
    /// milliseconds since the epoch.
    pub fn validate_json_at(record: &Value, now_ms: f64) -> ValidationResult {
        let Some(object) = record.as_object() else {
            return ok(vec!["record must be a JSON object".to_string()]);
        };
        let mut errors = Vec::new();
        let mut normalized = object.clone();
        for (name, kind) in FIELD_KINDS {
            let value = match object.get(name) {
                None | Some(Value::Null) => {
                    if REQUIRED_FIELDS.contains(&name) {
                        errors.push(format!("{name} is required"));
                    }
                    continue;
                }
                Some(value) => value,
            };
            let ok_kind = match kind {
                Kind::String | Kind::DateTime => value.is_string(),
                Kind::Money => value.is_number(),
                Kind::Count => {
                    // Counts are stored as 32-bit integers. A larger value is an error
                    // here, before binding, rather than a serde overflow.
                    let in_range =
                        |f: f64| (f64::from(i32::MIN)..=f64::from(i32::MAX)).contains(&f);
                    if let Some(i) = value.as_i64() {
                        i32::try_from(i).is_ok()
                    } else if let Some(f) =
                        value.as_f64().filter(|f| f.fract() == 0.0 && in_range(*f))
                    {
                        // 2.0 is an integer in JSON, as for the API.
                        normalized.insert(name.to_string(), Value::from(f as i32));
                        true
                    } else {
                        false
                    }
                }
                Kind::Boolean => value.is_boolean(),
            };
            if !ok_kind {
                errors.push(match kind {
                    Kind::String => format!("{name} must be a string"),
                    Kind::Money => format!("{name} must be a finite number"),
                    Kind::Count => format!(
                        "{name} must be an integer between {} and {}",
                        i32::MIN,
                        i32::MAX
                    ),
                    Kind::Boolean => format!("{name} must be true or false"),
                    Kind::DateTime => format!("{name} must be an ISO 8601 date-time"),
                });
            }
        }
        if !errors.is_empty() {
            return ok(errors);
        }
        match serde_json::from_value::<Self>(Value::Object(normalized)) {
            Ok(event) => event.validate_at(now_ms),
            Err(err) => ok(vec![format!("record could not be read: {err}")]),
        }
    }
}

// Amounts are stored as decimal(15,2): 13 integer digits and 2 decimal places.
const MONEY_LIMIT: f64 = 1e13;

/// True when a finite amount fits decimal(15,2): its absolute value is below
/// 10^13 and its shortest round-trip decimal form has at most 2 decimal
/// places. That form has at most 2 decimal places exactly when rounding the
/// number to 2 places (`{:.2}` rounds the exact binary value) gives the same
/// number back (2.995 and 1e-7 do not fit).
fn fits_money(value: f64) -> bool {
    value.abs() < MONEY_LIMIT && format!("{value:.2}").parse::<f64>() == Ok(value)
}

fn now_ms() -> f64 {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .map(|d| d.as_millis() as f64)
        .unwrap_or(0.0)
}

/// Days from 1970-01-01 to a proleptic Gregorian date (Howard Hinnant's
/// `days_from_civil`).
fn days_from_civil(year: i64, month: i64, day: i64) -> i64 {
    let y = if month <= 2 { year - 1 } else { year };
    let era = (if y >= 0 { y } else { y - 399 }) / 400;
    let yoe = y - era * 400;
    let mp = (month + 9) % 12;
    let doy = (153 * mp + 2) / 5 + day - 1;
    let doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
    era * 146_097 + doe - 719_468
}

fn days_in_month(year: i64, month: i64) -> i64 {
    match month {
        1 | 3 | 5 | 7 | 8 | 10 | 12 => 31,
        4 | 6 | 9 | 11 => 30,
        2 if (year % 4 == 0 && year % 100 != 0) || year % 400 == 0 => 29,
        2 => 28,
        _ => 0,
    }
}

/// Milliseconds since the epoch for an ISO 8601 date-time with seconds and a
/// zone (`YYYY-MM-DDTHH:mm:ss[.f{1,9}](Z|±hh:mm)`), or `None` when it is not
/// one or names a date or time that does not exist. Seconds and a zone are
/// required: a date-time without them is lost downstream.
pub(crate) fn parse_datetime_ms(s: &str) -> Option<f64> {
    let b = s.as_bytes();
    let digits = |from: usize, len: usize| -> Option<i64> {
        let part = b.get(from..from + len)?;
        if !part.iter().all(u8::is_ascii_digit) {
            return None;
        }
        std::str::from_utf8(part).ok()?.parse().ok()
    };
    let at = |i: usize, c: u8| b.get(i) == Some(&c);

    let year = digits(0, 4)?;
    let month = digits(5, 2)?;
    let day = digits(8, 2)?;
    let hour = digits(11, 2)?;
    let minute = digits(14, 2)?;
    let second = digits(17, 2)?;
    if !(at(4, b'-') && at(7, b'-') && at(10, b'T') && at(13, b':') && at(16, b':')) {
        return None;
    }
    let mut i = 19;
    if at(i, b'.') {
        let start = i + 1;
        let mut end = start;
        while b.get(end).is_some_and(u8::is_ascii_digit) {
            end += 1;
        }
        if end == start || end - start > 9 {
            return None;
        }
        i = end;
    }
    let mut offset_minutes = 0;
    match b.get(i) {
        Some(b'Z') if i + 1 == b.len() => {}
        Some(&sign @ (b'+' | b'-')) if i + 6 == b.len() && at(i + 3, b':') => {
            let hours = digits(i + 1, 2)?;
            let minutes = digits(i + 4, 2)?;
            // A zone offset is at most 18:00 either way.
            if minutes > 59 || hours * 60 + minutes > 18 * 60 {
                return None;
            }
            offset_minutes = (hours * 60 + minutes) * if sign == b'-' { -1 } else { 1 };
        }
        _ => return None,
    }
    if year < 1 || !(1..=12).contains(&month) || day < 1 || day > days_in_month(year, month) {
        return None;
    }
    if hour > 23 || minute > 59 || second > 59 {
        return None;
    }
    let seconds = days_from_civil(year, month, day) * 86_400 + hour * 3_600 + minute * 60 + second
        - offset_minutes * 60;
    Some(seconds as f64 * 1000.0)
}
