mod account_event;
mod customer_ext_event;
mod customer_profile;
mod deposit_event;
mod gaming_activity_event;
mod refer_friend_event;
mod system_event;
mod wallet_balance_event;
mod withdraw_event;

pub use account_event::AccountEvent;
pub use customer_ext_event::CustomerExtEvent;
pub use customer_profile::CustomerProfile;
pub use deposit_event::DepositEvent;
pub use gaming_activity_event::GamingActivityEvent;
pub use refer_friend_event::ReferFriendEvent;
pub use system_event::SystemEvent;
pub use wallet_balance_event::WalletBalanceEvent;
pub use withdraw_event::WithdrawEvent;

/// Result of calling `validate()` on a model. `errors` is empty iff valid.
#[derive(Debug, Clone, Default, PartialEq, Eq)]
pub struct ValidationResult {
    pub errors: Vec<String>,
}

impl ValidationResult {
    pub fn is_valid(&self) -> bool {
        self.errors.is_empty()
    }
}

pub(crate) fn ok(errors: Vec<String>) -> ValidationResult {
    ValidationResult { errors }
}

/// Loose ISO 8601 check (`YYYY-MM-DDTHH:MM:SS` plus a `Z`/offset/fraction
/// tail) — enough to catch malformed timestamps without pulling in a date
/// library.
pub(crate) fn is_valid_datetime(s: &str) -> bool {
    let b = s.as_bytes();
    if b.len() < 20 {
        return false;
    }
    let digit = |i: usize| b.get(i).is_some_and(u8::is_ascii_digit);
    digit(0)
        && digit(1)
        && digit(2)
        && digit(3)
        && b[4] == b'-'
        && digit(5)
        && digit(6)
        && b[7] == b'-'
        && digit(8)
        && digit(9)
        && b[10] == b'T'
        && digit(11)
        && digit(12)
        && b[13] == b':'
        && digit(14)
        && digit(15)
        && b[16] == b':'
        && digit(17)
        && digit(18)
        && matches!(b[19], b'Z' | b'.' | b'+' | b'-')
}

/// `YYYY-MM-DD`.
pub(crate) fn is_valid_date(s: &str) -> bool {
    let b = s.as_bytes();
    if b.len() != 10 {
        return false;
    }
    let digit = |i: usize| b.get(i).is_some_and(u8::is_ascii_digit);
    digit(0) && digit(1) && digit(2) && digit(3) && b[4] == b'-' && digit(5) && digit(6) && b[7] == b'-' && digit(8) && digit(9)
}

pub(crate) fn is_valid_email(s: &str) -> bool {
    match s.split_once('@') {
        Some((local, domain)) => !local.is_empty() && domain.contains('.') && !domain.starts_with('.') && !domain.ends_with('.'),
        None => false,
    }
}
