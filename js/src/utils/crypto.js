const crypto = require('crypto');

/**
 * Derives a cryptographic key using HKDF (HMAC-based Key Derivation Function)
 * @param {string} authToken - Authentication token
 * @param {string} accountId - Account ID
 * @param {string} workspaceId - Workspace ID
 * @param {string} info - Context string for key derivation
 * @param {string} algorithm - Hash algorithm (default: sha256)
 * @param {number} length - Output key length in bytes (default: 32)
 * @returns {Buffer} Derived key
 */
function deriveKey(authToken, accountId, workspaceId, info, algorithm = 'sha256', length = 32) {
  if (!authToken || !accountId || !workspaceId || !info) {
    throw new Error('All parameters are required for key derivation');
  }

  const ikmBuffer = Buffer.from(authToken, 'utf8');
  const saltBuffer = Buffer.from(accountId + workspaceId, 'utf8');
  const infoBuffer = Buffer.from(info, 'utf8');

  try {
    const derivedKey = crypto.hkdfSync(
      algorithm,
      saltBuffer,
      ikmBuffer,
      infoBuffer,
      length
    );
    return derivedKey;
  } catch (error) {
    throw new Error(`HKDF key derivation failed: ${error.message}`);
  }
}

/**
 * Generates HMAC signature using HKDF-derived key
 * @param {Object|string} data - Data to sign
 * @param {string} authToken - Authentication token
 * @param {string} accountId - Account ID
 * @param {string} workspaceId - Workspace ID
 * @param {string} algorithm - HMAC algorithm (default: sha256)
 * @returns {string} HMAC signature in hex format
 */
function generateHmacSignature(data, authToken, accountId, workspaceId, algorithm = 'sha256') {
  if (!data || !authToken || !accountId || !workspaceId) {
    throw new Error('All parameters are required for HMAC signature generation');
  }

  try {
    const info = 'hmac-signing';
    const derivedKey = deriveKey(authToken, accountId, workspaceId, info, algorithm);
    
    const hmac = crypto.createHmac(algorithm, derivedKey);
    const dataString = typeof data === 'string' ? data : JSON.stringify(data);
    hmac.update(dataString, 'utf8');
    const signature = hmac.digest('hex');
    
    return signature;
  } catch (error) {
    throw new Error(`HMAC signature generation failed: ${error.message}`);
  }
}

/**
 * Validates HMAC signature
 * @param {Object|string} data - Data that was signed
 * @param {string} signature - HMAC signature to validate
 * @param {string} authToken - Authentication token
 * @param {string} accountId - Account ID
 * @param {string} workspaceId - Workspace ID
 * @param {string} algorithm - HMAC algorithm (default: sha256)
 * @returns {boolean} True if signature is valid
 */
function validateHmacSignature(data, signature, authToken, accountId, workspaceId, algorithm = 'sha256') {
  try {
    const expectedSignature = generateHmacSignature(data, authToken, accountId, workspaceId, algorithm);
    return expectedSignature === signature;
  } catch (error) {
    return false;
  }
}

module.exports = {
  deriveKey,
  generateHmacSignature,
  validateHmacSignature
};

