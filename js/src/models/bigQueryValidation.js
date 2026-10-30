/**
 * Validation that mirrors what the warehouse stores for each column type.
 *
 * A record the API accepts but the warehouse cannot store is lost without an error reaching the caller, so
 * every model checks its fields against these rules before anything is sent. The rules follow what the
 * warehouse was measured to accept. Two deliberate exceptions: a NUMERIC string that is not a number and a
 * BOOLEAN string other than true/false/0/1 are rejected, because the warehouse would store a wrong value.
 */

const TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000;
const ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000;

const NUMERIC_STRING = /^[ \t\n\r\f\v]*[+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)?[ \t\n\r\f\v]*$/;
const INTEGER_STRING = /^[+-]?\d+$/;
const DIGITS = /^\d+$/;
const DATE_STRING = /^(\d{4})-(\d{1,2})-(\d{1,2})$/;
const TIMESTAMP_STRING = /^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(?::(\d{2})(\.\d+)?)?(Z|[+-]\d{2}:\d{2}| UTC)?$/;

function isPlainObject(value) {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

/** Years 0001-9999 only. setUTCFullYear is used because Date.UTC maps years below 100 to 19xx. */
function isRealDate(y, m, d) {
  if (y < 1 || y > 9999) return false;
  const date = new Date(0);
  date.setUTCFullYear(y, m - 1, d);
  return date.getUTCFullYear() === y && date.getUTCMonth() === m - 1 && date.getUTCDate() === d;
}

/**
 * Milliseconds since the epoch for a date-time string the warehouse accepts, or NaN when it does not.
 * Numeric timestamps are accepted elsewhere and never reach this function.
 */
function parseTimestampString(value) {
  const match = TIMESTAMP_STRING.exec(value);
  if (!match) return NaN;
  const [y, mo, d, h, mi, s] = match.slice(1, 7).map(part => Number(part || 0));
  if (!isRealDate(y, mo, d) || h > 23 || mi > 59 || s > 59) return NaN;
  const date = new Date(0);
  date.setUTCFullYear(y, mo - 1, d);
  date.setUTCHours(h, mi, s, 0);
  let time = date.getTime();
  const zone = match[8];
  if (zone && zone !== 'Z' && zone !== ' UTC') {
    const offsetHours = Number(zone.slice(1, 3));
    const offsetMinutes = Number(zone.slice(4, 6));
    if (offsetHours > 18 || offsetMinutes > 59) return NaN;
    const sign = zone[0] === '-' ? -1 : 1;
    time -= sign * (offsetHours * 60 + offsetMinutes) * 60 * 1000;
  }
  return time;
}

function isNumericTimestamp(value) {
  return (typeof value === 'number' && Number.isFinite(value)) || (typeof value === 'string' && DIGITS.test(value));
}

/** Returns an error message, or null when the value can be stored in a column of this type. */
function checkType(field, value, type) {
  switch (type) {
    case 'STRING':
      return typeof value === 'string' ? null : `${field} must be a string`;
    case 'NUMERIC':
      if (typeof value === 'number') return Number.isFinite(value) ? null : `${field} must be a finite number`;
      return typeof value === 'string' && NUMERIC_STRING.test(value) ? null : `${field} must be a number`;
    case 'INTEGER':
      if (typeof value === 'number') return Number.isInteger(value) ? null : `${field} must be an integer`;
      return typeof value === 'string' && INTEGER_STRING.test(value) ? null : `${field} must be an integer`;
    case 'BOOLEAN':
      if (typeof value === 'boolean' || value === 0 || value === 1) return null;
      return typeof value === 'string' && /^(true|false|0|1)$/i.test(value) ? null : `${field} must be true or false`;
    case 'TIMESTAMP':
      if (isNumericTimestamp(value)) return null;
      return typeof value === 'string' && !Number.isNaN(parseTimestampString(value))
        ? null
        : `${field} must be a valid ISO 8601 date-time`;
    case 'DATE': {
      if ((typeof value === 'number' && Number.isInteger(value)) || (typeof value === 'string' && DIGITS.test(value))) {
        return null;
      }
      const match = typeof value === 'string' && DATE_STRING.exec(value);
      return match && isRealDate(...match.slice(1, 4).map(Number)) ? null : `${field} must be a date in YYYY-MM-DD format`;
    }
    case 'JSON':
      // The API serialises objects and arrays to a JSON string before storing them.
      if (typeof value === 'object' && value !== null) return null;
      if (typeof value === 'string') {
        try {
          JSON.parse(value);
          return null;
        } catch (e) {
          return `${field} must be an object or a valid JSON string`;
        }
      }
      return `${field} must be an object or a valid JSON string`;
    default:
      return null;
  }
}

/**
 * @param {Object} record - The values to check, as they will be sent
 * @param {Object} rules - { required: string[], types: {field: type}, partition?: string }
 * @param {number} [now] - Current time in ms (for tests)
 * @returns {string[]} Error messages; empty when the record can be stored
 */
function validateStorage(record, rules, now = Date.now()) {
  const errors = [];

  for (const field of rules.required) {
    const value = record[field];
    if (value === undefined || value === null || value === '') errors.push(`${field} is required`);
  }

  for (const [field, type] of Object.entries(rules.types)) {
    const value = record[field];
    if (value === undefined || value === null) continue;
    const error = checkType(field, value, type);
    if (error) errors.push(error);
  }

  // The table is partitioned on this column and only accepts writes within this window. A numeric timestamp's
  // unit is ambiguous to the warehouse too, so only date-time strings are checked.
  const partitionValue = rules.partition && record[rules.partition];
  if (typeof partitionValue === 'string') {
    const time = parseTimestampString(partitionValue);
    if (!Number.isNaN(time) && (time < now - TEN_YEARS_MS || time > now + ONE_YEAR_MS)) {
      errors.push(`${rules.partition} must be within the last 10 years and no more than 1 year in the future`);
    }
  }

  return errors;
}

/**
 * The record as it will be checked: the model's declared fields as they are now (a field cleared after
 * construction stays cleared), plus any fields from the original input that the model does not declare.
 */
function storageRecord(model, input) {
  // Declared fields come from the class, so a field deleted from the instance is missing, not refilled.
  const declared = new Set(Object.keys(new model.constructor()));
  const record = {};
  for (const [key, value] of Object.entries(input || {})) {
    if (!declared.has(key)) record[key] = value;
  }
  for (const key of declared) record[key] = model[key];
  for (const key of Object.keys(model)) if (!declared.has(key)) record[key] = model[key];
  return record;
}

module.exports = { validateStorage, checkType, storageRecord };
