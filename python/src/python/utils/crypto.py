"""
Cryptographic utilities for Optikpi Data Pipeline SDK
Handles HKDF key derivation and HMAC signature generation/validation
"""
import hmac
import hashlib
import json
from typing import Union, Optional


def derive_key(
    auth_token: str,
    account_id: str,
    workspace_id: str,
    info: str,
    algorithm: str = 'sha256',
    length: int = 32
) -> bytes:
    """
    Derives a cryptographic key using HKDF (HMAC-based Key Derivation Function)
    Compatible with Node.js crypto.hkdfSync parameter ordering.
    
    Args:
        auth_token: Authentication token (used as input key material)
        account_id: Account ID
        workspace_id: Workspace ID
        info: Context string for key derivation (e.g., 'hmac-signing')
        algorithm: Hash algorithm to use (default: 'sha256')
        length: Output key length in bytes (default: 32)
    
    Returns:
        bytes: Derived cryptographic key
    """
    if not all([auth_token, account_id, workspace_id, info]):
        raise ValueError('All parameters are required for key derivation')
    
    try:
        # Get hash object
        hash_func = getattr(hashlib, algorithm)
        
        # Convert inputs to bytes - MATCHING NODE.JS ORDER
        # Node.js: hkdfSync(algorithm, salt, ikm, info, length)
        # So: salt = accountId + workspaceId, ikm = authToken
        ikm = auth_token.encode('utf-8')
        salt = (account_id + workspace_id).encode('utf-8')
        info_bytes = info.encode('utf-8')
        
        # HKDF-Extract step
        # Node.js uses: salt as key, ikm as message
        # PRK = HMAC-Hash(salt, ikm) - SWAP THESE TO MATCH NODE.JS!
        prk = hmac.new(ikm, salt, hash_func).digest()
        
        # HKDF-Expand step
        hash_len = hash_func().digest_size
        n = (length + hash_len - 1) // hash_len
        okm = b''
        t = b''
        
        for i in range(1, n + 1):
            t = hmac.new(prk, t + info_bytes + bytes([i]), hash_func).digest()
            okm += t
        
        return okm[:length]
    
    except AttributeError:
        raise ValueError(f"Unsupported algorithm: {algorithm}")
    except Exception as error:
        raise Exception(f"HKDF key derivation failed: {str(error)}")


def generate_hmac_signature(
    data: Union[dict, str],
    auth_token: str,
    account_id: str,
    workspace_id: str,
    algorithm: str = 'sha256'
) -> str:
    """
    Generates HMAC signature using HKDF-derived key
    Compatible with Node.js implementation.
    
    Args:
        data: Data to sign (dict or string)
        auth_token: Authentication token
        account_id: Account ID
        workspace_id: Workspace ID
        algorithm: HMAC algorithm (default: 'sha256')
    
    Returns:
        str: HMAC signature in hexadecimal format
    """
    if not all([data, auth_token, account_id, workspace_id]):
        raise ValueError('All parameters are required for HMAC signature generation')
    
    try:
        # Derive key using HKDF
        info = 'hmac-signing'
        derived_key = derive_key(auth_token, account_id, workspace_id, info, algorithm)
        
        # Convert data to string - NO SORT_KEYS to match Node.js JSON.stringify
        data_string = data if isinstance(data, str) else json.dumps(data, separators=(',', ':'))
        
        # Generate HMAC
        signature = hmac.new(
            derived_key,
            data_string.encode('utf-8'),
            getattr(hashlib, algorithm)
        ).hexdigest()
        
        return signature
    
    except Exception as error:
        raise Exception(f"HMAC signature generation failed: {str(error)}")


def validate_hmac_signature(
    data: Union[dict, str],
    signature: str,
    auth_token: str,
    account_id: str,
    workspace_id: str,
    algorithm: str = 'sha256'
) -> bool:
    """
    Validates HMAC signature
    
    Args:
        data: Data that was signed (dict or string)
        signature: HMAC signature to validate (hexadecimal string)
        auth_token: Authentication token
        account_id: Account ID
        workspace_id: Workspace ID
        algorithm: HMAC algorithm (default: 'sha256')
    
    Returns:
        bool: True if signature is valid, False otherwise
    """
    try:
        expected_signature = generate_hmac_signature(
            data,
            auth_token,
            account_id,
            workspace_id,
            algorithm
        )
        
        # Use constant-time comparison to prevent timing attacks
        return hmac.compare_digest(expected_signature, signature)
    
    except Exception:
        return False


def sign_request_data(
    data: dict,
    auth_token: str,
    account_id: str,
    workspace_id: str
) -> str:
    """
    Convenience function to sign request data for API calls
    """
    return generate_hmac_signature(data, auth_token, account_id, workspace_id)


def verify_request_signature(
    data: dict,
    signature: str,
    auth_token: str,
    account_id: str,
    workspace_id: str
) -> bool:
    """
    Convenience function to verify request signatures
    """
    return validate_hmac_signature(data, signature, auth_token, account_id, workspace_id)