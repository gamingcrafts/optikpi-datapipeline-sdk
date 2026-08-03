//! HKDF key derivation and HMAC signature generation, matching the Node.js
//! Lambda's `crypto.hkdfSync` behavior byte-for-byte.

use hmac::{Hmac, Mac};
use sha2::Sha256;

type HmacSha256 = Hmac<Sha256>;

const KEY_LENGTH: usize = 32;

fn hmac_sha256(key: &[u8], msg: &[u8]) -> Vec<u8> {
    let mut mac = HmacSha256::new_from_slice(key).expect("HMAC accepts a key of any length");
    mac.update(msg);
    mac.finalize().into_bytes().to_vec()
}

/// Node's `hkdfSync(algorithm, salt, ikm, info, length)` uses `ikm` as the
/// HMAC key and `salt` as the message for the extract step — the reverse of
/// RFC 5869. This must match to interoperate with the ingest Lambda.
fn hkdf_extract(ikm: &[u8], salt: &[u8]) -> Vec<u8> {
    hmac_sha256(ikm, salt)
}

fn hkdf_expand(prk: &[u8], info: &[u8], length: usize) -> Vec<u8> {
    let mut okm = Vec::with_capacity(length);
    let mut t: Vec<u8> = Vec::new();
    let mut counter: u8 = 1;
    while okm.len() < length {
        let mut mac = HmacSha256::new_from_slice(prk).expect("HMAC accepts a key of any length");
        mac.update(&t);
        mac.update(info);
        mac.update(&[counter]);
        t = mac.finalize().into_bytes().to_vec();
        okm.extend_from_slice(&t);
        counter += 1;
    }
    okm.truncate(length);
    okm
}

/// Derives the 32-byte signing key for a given set of credentials.
pub fn derive_key(auth_token: &str, account_id: &str, workspace_id: &str) -> Vec<u8> {
    let ikm = auth_token.as_bytes();
    let salt = format!("{account_id}{workspace_id}");
    let prk = hkdf_extract(ikm, salt.as_bytes());
    hkdf_expand(&prk, b"hmac-signing", KEY_LENGTH)
}

/// Returns the hex-encoded HMAC-SHA256 signature for a JSON request body.
pub fn generate_signature(
    body: &str,
    auth_token: &str,
    account_id: &str,
    workspace_id: &str,
) -> String {
    let key = derive_key(auth_token, account_id, workspace_id);
    hex::encode(hmac_sha256(&key, body.as_bytes()))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn signature_is_64_hex_chars() {
        let sig = generate_signature(
            r#"{"user_id":"player1"}"#,
            "test-token-abc123",
            "account-001",
            "workspace-001",
        );
        assert_eq!(sig.len(), 64);
        assert!(sig.chars().all(|c| c.is_ascii_hexdigit()));
    }

    #[test]
    fn same_inputs_produce_same_signature() {
        let sig1 = generate_signature("test body", "tok", "acc", "ws");
        let sig2 = generate_signature("test body", "tok", "acc", "ws");
        assert_eq!(sig1, sig2);
    }

    #[test]
    fn different_bodies_produce_different_signatures() {
        let sig1 = generate_signature("body1", "tok", "acc", "ws");
        let sig2 = generate_signature("body2", "tok", "acc", "ws");
        assert_ne!(sig1, sig2);
    }

    #[test]
    fn different_tokens_produce_different_signatures() {
        let sig1 = generate_signature("body", "token1", "acc", "ws");
        let sig2 = generate_signature("body", "token2", "acc", "ws");
        assert_ne!(sig1, sig2);
    }

    /// Cross-checked against the JS (`js/src/utils/crypto.js`) and Python
    /// (`python/src/python/utils/crypto.py`) implementations, which agree
    /// byte-for-byte on this input. If this fails, the HKDF extract step's
    /// key/message order is wrong (see the comment on `hkdf_extract`).
    #[test]
    fn known_signature_matches_js_and_python_implementations() {
        let sig = generate_signature(
            r#"{"test":true}"#,
            "my-auth-token",
            "my-account-id",
            "my-workspace-id",
        );
        assert_eq!(
            sig,
            "eaf1b496e481d2c1f5da755ffb4a2f865191acea44856be48a6653c4f528dab1"
        );
    }
}
