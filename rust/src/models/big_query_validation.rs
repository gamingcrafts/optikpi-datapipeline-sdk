//! Validation of each column against what the warehouse stores.
//!
//! A record the API accepts but the warehouse cannot store is lost without an
//! error reaching the caller, so every model checks the record it will send
//! against these rules. They follow what the warehouse was measured to store,
//! with two deliberate exceptions: `"abc"` in a NUMERIC column and `"yes"` in a
//! BOOLEAN column are rejected, although the warehouse stores them as junk.
//!
//! Checks run on the serialized record (`serde_json::to_value`), so they see
//! exactly what goes over the wire. Typed fields (`f64`, `bool`, `i64`) satisfy
//! most rules by construction; the checks matter for `String` fields that
//! carry timestamps or dates, `serde_json::Value` fields, and empty strings in
//! required fields.

use std::time::{SystemTime, UNIX_EPOCH};

use serde::Serialize;
use serde_json::Value;

const DAY_MS: f64 = 24.0 * 60.0 * 60.0 * 1000.0;
const TEN_YEARS_MS: f64 = 10.0 * 365.25 * DAY_MS;
const ONE_YEAR_MS: f64 = 365.25 * DAY_MS;

/// Column type of a stored field.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub(crate) enum Col {
    String,
    Numeric,
    Integer,
    Boolean,
    Timestamp,
    Date,
    Json,
}

/// What the warehouse requires of one record type.
pub(crate) struct StorageRules {
    pub required: &'static [&'static str],
    pub types: &'static [(&'static str, Col)],
    /// The column the table is partitioned on, if any.
    pub partition: Option<&'static str>,
}

/// Runs the storage rules against `model` as it will be serialized and appends
/// every error not already in `errors`.
pub(crate) fn append_storage_errors<T: Serialize>(errors: &mut Vec<String>, model: &T, rules: &StorageRules) {
    let found = match serde_json::to_value(model) {
        Ok(record) => validate_storage(&record, rules, now_ms()),
        Err(e) => vec![format!("record could not be serialized: {e}")],
    };
    for error in found {
        if !errors.contains(&error) {
            errors.push(error);
        }
    }
}

fn now_ms() -> f64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|d| d.as_millis() as f64)
        .unwrap_or(0.0)
}

/// Error messages for `record`; empty when the record can be stored.
/// `now_ms` is the current time in milliseconds since the epoch.
pub(crate) fn validate_storage(record: &Value, rules: &StorageRules, now_ms: f64) -> Vec<String> {
    let mut errors = Vec::new();
    let get = |field: &str| record.get(field).filter(|v| !v.is_null());

    for field in rules.required {
        match get(field) {
            None => errors.push(format!("{field} is required")),
            Some(Value::String(s)) if s.is_empty() => errors.push(format!("{field} is required")),
            Some(_) => {}
        }
    }

    for (field, ty) in rules.types {
        if let Some(value) = get(field) {
            if let Some(error) = check_type(field, value, *ty) {
                errors.push(error);
            }
        }
    }

    // The table is partitioned on this column and only accepts writes within this window.
    // Only date-time strings are checked: the unit of a numeric timestamp is ambiguous.
    if let Some(partition) = rules.partition {
        if let Some(time) = get(partition).and_then(Value::as_str).and_then(parse_datetime_ms) {
            if time < now_ms - TEN_YEARS_MS || time > now_ms + ONE_YEAR_MS {
                errors.push(format!(
                    "{partition} must be within the last 10 years and no more than 1 year in the future"
                ));
            }
        }
    }

    errors
}

/// An error message, or `None` when `value` can be stored in a column of type `ty`.
pub(crate) fn check_type(field: &str, value: &Value, ty: Col) -> Option<String> {
    let accepted = match ty {
        Col::String => value.is_string(),
        Col::Numeric => match value {
            // serde_json numbers are always finite.
            Value::Number(_) => true,
            Value::String(s) => is_numeric_string(s),
            _ => false,
        },
        Col::Integer => match value {
            Value::Number(n) => is_integral(n),
            Value::String(s) => is_integer_string(s),
            _ => false,
        },
        Col::Boolean => match value {
            Value::Bool(_) => true,
            Value::Number(n) => n.as_f64().is_some_and(|f| f == 0.0 || f == 1.0),
            Value::String(s) => {
                s.eq_ignore_ascii_case("true") || s.eq_ignore_ascii_case("false") || s == "0" || s == "1"
            }
            _ => false,
        },
        Col::Timestamp => match value {
            Value::Number(_) => true,
            Value::String(s) => all_digits(s) || parse_datetime_ms(s).is_some(),
            _ => false,
        },
        Col::Date => match value {
            Value::Number(n) => is_integral(n),
            Value::String(s) => all_digits(s) || is_real_date(s),
            _ => false,
        },
        // The API serialises objects and arrays to a JSON string before storing them.
        Col::Json => match value {
            Value::Object(_) | Value::Array(_) => true,
            Value::String(s) => serde_json::from_str::<Value>(s).is_ok(),
            _ => false,
        },
    };
    if accepted {
        return None;
    }
    Some(match ty {
        Col::String => format!("{field} must be a string"),
        Col::Numeric => format!("{field} must be a number"),
        Col::Integer => format!("{field} must be an integer"),
        Col::Boolean => format!("{field} must be true or false"),
        Col::Timestamp => format!("{field} must be a valid ISO 8601 date-time"),
        Col::Date => format!("{field} must be a date in YYYY-MM-DD format"),
        Col::Json => format!("{field} must be an object or a valid JSON string"),
    })
}

fn is_integral(n: &serde_json::Number) -> bool {
    n.is_i64() || n.is_u64() || n.as_f64().is_some_and(|f| f.is_finite() && f.fract() == 0.0)
}

/// `^\d+$`
fn all_digits(s: &str) -> bool {
    !s.is_empty() && s.bytes().all(|b| b.is_ascii_digit())
}

/// `^[+-]?\d+$`
fn is_integer_string(s: &str) -> bool {
    all_digits(s.strip_prefix(['+', '-']).unwrap_or(s))
}

/// `^[ \t\n\r\f\v]*[+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)?[ \t\n\r\f\v]*$`
/// (ASCII padding only; NBSP and other Unicode spaces are rejected).
fn is_numeric_string(s: &str) -> bool {
    let s = s.trim_matches(|c| matches!(c, ' ' | '\t' | '\n' | '\r' | '\x0C' | '\x0B'));
    let s = s.strip_prefix(['+', '-']).unwrap_or(s);
    let (mantissa, exponent) = match s.find(['e', 'E']) {
        Some(i) => (&s[..i], Some(&s[i + 1..])),
        None => (s, None),
    };
    let mantissa_ok = match mantissa.split_once('.') {
        Some((int, frac)) => {
            (all_digits(int) && (frac.is_empty() || all_digits(frac))) || (int.is_empty() && all_digits(frac))
        }
        None => all_digits(mantissa),
    };
    mantissa_ok && exponent.map_or(true, is_integer_string)
}

/// `^\d{4}-\d{1,2}-\d{1,2}$` and a real calendar date.
fn is_real_date(s: &str) -> bool {
    let mut parts = s.split('-');
    let (Some(y), Some(m), Some(d), None) = (parts.next(), parts.next(), parts.next(), parts.next()) else {
        return false;
    };
    if y.len() != 4 || !all_digits(y) || !(1..=2).contains(&m.len()) || !all_digits(m) {
        return false;
    }
    if !(1..=2).contains(&d.len()) || !all_digits(d) {
        return false;
    }
    is_valid_ymd(y.parse().unwrap_or(0), m.parse().unwrap_or(0), d.parse().unwrap_or(0))
}

/// A real calendar date in years 0001–9999.
fn is_valid_ymd(y: i64, m: u32, d: u32) -> bool {
    if !(1..=9999).contains(&y) {
        return false;
    }
    let leap = (y % 4 == 0 && y % 100 != 0) || y % 400 == 0;
    let days = match m {
        1 | 3 | 5 | 7 | 8 | 10 | 12 => 31,
        4 | 6 | 9 | 11 => 30,
        2 if leap => 29,
        2 => 28,
        _ => return false,
    };
    (1..=days).contains(&d)
}

/// Days since 1970-01-01 (proleptic Gregorian).
fn days_from_civil(y: i64, m: u32, d: u32) -> i64 {
    let y = if m <= 2 { y - 1 } else { y };
    let era = y.div_euclid(400);
    let yoe = y - era * 400;
    let m = m as i64;
    let doy = (153 * (if m > 2 { m - 3 } else { m + 9 }) + 2) / 5 + d as i64 - 1;
    let doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
    era * 146_097 + doe - 719_468
}

/// Milliseconds since the epoch for a string matching
/// `^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(:(\d{2})(\.\d+)?)?(Z|[+-]\d{2}:\d{2}| UTC)?$`
/// that is a real date (years 0001–9999) and time, with an offset of at most
/// ±18:59, or `None`. Without an offset the time is UTC.
fn parse_datetime_ms(s: &str) -> Option<f64> {
    let b = s.as_bytes();
    let num = |from: usize, len: usize| -> Option<u32> {
        let part = s.get(from..from + len)?;
        if all_digits(part) {
            part.parse().ok()
        } else {
            None
        }
    };
    let at = |i: usize| b.get(i).copied();

    let year = num(0, 4)?;
    let month = num(5, 2)?;
    let day = num(8, 2)?;
    let hour = num(11, 2)?;
    let minute = num(14, 2)?;
    if at(4) != Some(b'-') || at(7) != Some(b'-') || !matches!(at(10), Some(b'T' | b' ')) || at(13) != Some(b':') {
        return None;
    }

    let mut i = 16;
    let mut second = 0.0;
    if at(i) == Some(b':') {
        second = f64::from(num(i + 1, 2)?);
        i += 3;
        if at(i) == Some(b'.') {
            let start = i + 1;
            i = start;
            while at(i).is_some_and(|c| c.is_ascii_digit()) {
                i += 1;
            }
            if i == start {
                return None;
            }
            second += format!("0.{}", &s[start..i]).parse::<f64>().ok()?;
        }
    }

    let offset_minutes = match &s[i..] {
        "" | "Z" | " UTC" => 0.0,
        zone => {
            let zb = zone.as_bytes();
            let sign = match zb[0] {
                b'+' => 1.0,
                b'-' => -1.0,
                _ => return None,
            };
            if zone.len() != 6 || zb[3] != b':' {
                return None;
            }
            let (oh, om) = (num(i + 1, 2)?, num(i + 4, 2)?);
            if oh > 18 || om > 59 {
                return None;
            }
            sign * f64::from(oh * 60 + om)
        }
    };

    if !is_valid_ymd(i64::from(year), month, day) || hour > 23 || minute > 59 || second >= 60.0 {
        return None;
    }
    let days = days_from_civil(i64::from(year), month, day) as f64;
    let seconds = f64::from(hour) * 3600.0 + f64::from(minute) * 60.0 + second - offset_minutes * 60.0;
    Some(days * DAY_MS + seconds * 1000.0)
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::json;

    /// Every value of the dev warehouse probe, with whether the warehouse stored it.
    /// `"abc"` (NUMERIC) and `"yes"` (BOOLEAN) are stored as junk there and
    /// rejected here on purpose.
    #[test]
    fn check_type_matches_the_measured_warehouse_acceptance() {
        // (type, value, accepted)
        let cases = [
            (Col::String, json!("abc"), true),
            (Col::String, json!(""), true),
            (Col::String, json!(15466), false),
            (Col::String, json!(true), false),
            (Col::String, json!({}), false),
            (Col::String, json!([]), false),
            (Col::Numeric, json!(10), true),
            (Col::Numeric, json!(10.5), true),
            (Col::Numeric, json!(-3), true),
            (Col::Numeric, json!("10.5"), true),
            (Col::Numeric, json!("-10"), true),
            (Col::Numeric, json!("1e5"), true),
            (Col::Numeric, json!(" 10"), true),
            (Col::Numeric, json!("10 "), true),
            (Col::Numeric, json!("+10"), true),
            (Col::Numeric, json!(".5"), true),
            (Col::Numeric, json!("10."), true),
            (Col::Numeric, json!("\t10\n"), true),
            (Col::Numeric, json!("\u{0B}10\u{0C}\r"), true),
            (Col::Numeric, json!("\u{a0}10"), false),
            (Col::Numeric, json!("10\u{2003}"), false),
            (Col::Numeric, json!("abc"), false), // deliberate: the warehouse stores junk
            (Col::Numeric, json!(""), false),
            (Col::Numeric, json!(true), false),
            (Col::Numeric, json!("1,000"), false),
            (Col::Numeric, json!("."), false),
            (Col::Numeric, json!("1e"), false),
            (Col::Integer, json!(3), true),
            (Col::Integer, json!(3.0), true),
            (Col::Integer, json!("3"), true),
            (Col::Integer, json!("+3"), true),
            (Col::Integer, json!(3.5), false),
            (Col::Integer, json!("3.5"), false),
            (Col::Integer, json!(""), false),
            (Col::Integer, json!(true), false),
            (Col::Integer, json!(" 3"), false),
            (Col::Integer, json!("3e2"), false),
            (Col::Boolean, json!(true), true),
            (Col::Boolean, json!(false), true),
            (Col::Boolean, json!("true"), true),
            (Col::Boolean, json!("TRUE"), true),
            (Col::Boolean, json!("True"), true),
            (Col::Boolean, json!("false"), true),
            (Col::Boolean, json!(1), true),
            (Col::Boolean, json!(0), true),
            (Col::Boolean, json!("1"), true),
            (Col::Boolean, json!("yes"), false), // deliberate: the warehouse stores it as true
            (Col::Boolean, json!(2), false),
            (Col::Boolean, json!(""), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13.562Z"), true),
            (Col::Timestamp, json!("2026-10-29 12:32:13"), true),
            (Col::Timestamp, json!("2026-10-29 12:32:13 UTC"), true),
            (Col::Timestamp, json!("2026-10-29T12:32:13 UTC"), true),
            (Col::Timestamp, json!("2026-10-29T12:32:13+05:30"), true),
            (Col::Timestamp, json!("2026-10-29 12:30"), true),
            (Col::Timestamp, json!("2026-10-29T12:32:13.123456789Z"), true),
            (Col::Timestamp, json!(1761741133562000_i64), true),
            (Col::Timestamp, json!(1761741133), true),
            (Col::Timestamp, json!("1761741133562000"), true),
            (Col::Timestamp, json!("0001-01-01T00:00:00Z"), true),
            (Col::Timestamp, json!("0099-12-31 23:59:59"), true),
            (Col::Timestamp, json!("9999-12-31T23:59:59Z"), true),
            (Col::Timestamp, json!("2026-10-29T12:32:13-18:00"), true),
            (Col::Timestamp, json!("2026-10-29T12:32:13+18:59"), true),
            (Col::Timestamp, json!("0000-01-01T00:00:00Z"), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13+05:99"), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13+19:00"), false),
            (Col::Timestamp, json!("2026-10-29"), false),
            (Col::Timestamp, json!("2026-10-29 12:32:13 Asia/Kolkata"), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13+0530"), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13+05"), false),
            (Col::Timestamp, json!("2026-1-5 1:2:3"), false),
            (Col::Timestamp, json!("20261029T123213Z"), false),
            (Col::Timestamp, json!("Oct 29 2026"), false),
            (Col::Timestamp, json!(""), false),
            (Col::Timestamp, json!("   "), false),
            (Col::Timestamp, json!("2026-02-30T00:00:00Z"), false),
            (Col::Timestamp, json!("2026-10-29T24:00:00Z"), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13 UTC+5"), false),
            (Col::Timestamp, json!("2026-10-29T12:32:13z"), false),
            (Col::Timestamp, json!("2026-10-29t12:32:13Z"), false),
            (Col::Timestamp, json!(true), false),
            (Col::Timestamp, json!({}), false),
            (Col::Date, json!("1990-05-17"), true),
            (Col::Date, json!("1990-5-7"), true),
            (Col::Date, json!(7000), true),
            (Col::Date, json!("7000"), true),
            (Col::Date, json!("0001-01-01"), true),
            (Col::Date, json!("0000-01-01"), false),
            (Col::Date, json!("1990-02-30"), false),
            (Col::Date, json!("17/05/1990"), false),
            (Col::Date, json!(""), false),
            (Col::Date, json!("1990-05-17T00:00:00Z"), false),
            (Col::Date, json!("1990-05-17 00:00:00"), false),
            (Col::Json, json!({"a": 1}), true), // the API serialises objects
            (Col::Json, json!([1, 2]), true),   // and arrays before storage
            (Col::Json, json!(r#"{"a":1}"#), true),
            (Col::Json, json!("[1,2]"), true),
            (Col::Json, json!(r#""str""#), true),
            (Col::Json, json!("null"), true),
            (Col::Json, json!("bad{"), false),
            (Col::Json, json!(""), false),
            (Col::Json, json!(5), false),
            (Col::Json, json!(true), false),
        ];
        for (ty, value, accepted) in cases {
            assert_eq!(check_type("f", &value, ty).is_none(), accepted, "{ty:?} {value}");
        }
    }

    const RULES: StorageRules = StorageRules {
        required: &["a", "b"],
        types: &[("a", Col::String), ("n", Col::Numeric), ("t", Col::Timestamp)],
        partition: Some("t"),
    };

    fn now() -> f64 {
        parse_datetime_ms("2026-10-29T00:00:00Z").unwrap()
    }

    #[test]
    fn required_fields_missing_null_or_empty_are_rejected() {
        for record in [json!({}), json!({"a": null, "b": null}), json!({"a": "", "b": ""})] {
            let errors = validate_storage(&record, &RULES, now());
            assert!(errors.contains(&"a is required".to_string()), "{record}");
            assert!(errors.contains(&"b is required".to_string()), "{record}");
        }
    }

    #[test]
    fn null_and_absent_optional_fields_are_accepted() {
        assert!(validate_storage(&json!({"a": "x", "b": "y", "n": null}), &RULES, now()).is_empty());
    }

    #[test]
    fn partition_window() {
        for (t, accepted) in [
            (json!("2016-10-15T00:00:00Z"), false),
            (json!("2016-11-15T00:00:00Z"), true),
            (json!("2027-10-01T00:00:00Z"), true),
            (json!("2027-12-01T00:00:00Z"), false),
            // Numeric timestamps have an ambiguous unit and are not window-checked.
            (json!(1), true),
            (json!("1"), true),
        ] {
            let errors = validate_storage(&json!({"a": "x", "b": "y", "t": t}), &RULES, now());
            assert_eq!(errors.is_empty(), accepted, "{t}: {errors:?}");
        }
    }

    #[test]
    fn timestamp_offsets_are_applied() {
        let utc = parse_datetime_ms("2026-10-29T12:00:00Z").unwrap();
        assert_eq!(parse_datetime_ms("2026-10-29T17:30:00+05:30").unwrap(), utc);
        assert_eq!(parse_datetime_ms("2026-10-29T07:00:00-05:00").unwrap(), utc);
        assert_eq!(parse_datetime_ms("2026-10-29 12:00 UTC").unwrap(), utc);
        assert_eq!(parse_datetime_ms("1970-01-01T00:00:00Z").unwrap(), 0.0);
    }
}
