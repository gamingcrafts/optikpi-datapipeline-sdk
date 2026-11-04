/**
 * Purchase Funnel Event Model
 * Represents one step of a purchase attempt (a funnel session) for the Data Pipeline API.
 *
 * Every event of one attempt shares a funnel_session_id. Its meaning comes from funnel_stage (a fixed list);
 * event_name is the operator's own free text.
 */

/** Allowed funnel_stage values, in funnel order. */
const FUNNEL_STAGES = Object.freeze([
  'start',
  'browse',
  'configure',
  'cart',
  'checkout',
  'payment',
  'payment_failed',
  'complete',
  'failed',
  'cancelled',
  'exit',
  'stalled',
  'abandoned'
]);

/** Allowed failure_reason values. */
const FAILURE_REASONS = Object.freeze([
  'payment_declined',
  'insufficient_funds',
  'provider_timeout',
  'limit_reached',
  'kyc_required',
  'geo_blocked',
  'draw_closed',
  'technical_error',
  'user_cancelled'
]);

// Field kinds: string, money (finite number), count (integer), boolean, date-time (ISO 8601 string).
const STRING = 'string';
const MONEY = 'money';
const COUNT = 'count';
const BOOLEAN = 'boolean';
const DATETIME = 'datetime';

/** Every field the model carries, with its kind. Required fields are listed in REQUIRED_FIELDS. */
const FIELDS = Object.freeze({
  account_id: STRING,
  workspace_id: STRING,
  user_id: STRING,
  event_id: STRING,
  event_time: DATETIME,
  event_category: STRING,
  event_name: STRING,
  funnel_session_id: STRING,
  funnel_stage: STRING,
  // Item
  item_id: STRING,
  item_name: STRING,
  item_category: STRING,
  draw_id: STRING,
  draw_number: STRING,
  draw_date: DATETIME,
  draw_close_time: DATETIME,
  jackpot_amount: MONEY,
  selection: STRING,
  selection_method: STRING,
  lines_count: COUNT,
  multi_draw_count: COUNT,
  unit_price: MONEY,
  item_quantity: COUNT,
  item_amount: MONEY,
  // Cart snapshot
  cart_id: STRING,
  cart_item_count: COUNT,
  cart_quantity_total: COUNT,
  cart_items: STRING,
  cart_subtotal: MONEY,
  discount_amount: MONEY,
  bonus_used_amount: MONEY,
  fees_amount: MONEY,
  tax_amount: MONEY,
  cart_total: MONEY,
  coupon_code: STRING,
  currency: STRING,
  // Payment
  payment_method: STRING,
  payment_provider_id: STRING,
  payment_provider_name: STRING,
  is_saved_method: BOOLEAN,
  funds_source: STRING,
  payment_attempt_number: COUNT,
  payment_status: STRING,
  transaction_id: STRING,
  failure_reason: STRING,
  failure_code: STRING,
  failure_message: STRING,
  // Order
  order_id: STRING,
  ticket_ids: STRING,
  order_total: MONEY,
  order_status: STRING,
  is_first_purchase: BOOLEAN,
  // Context
  device: STRING,
  platform: STRING,
  app_version: STRING,
  page_url: STRING,
  referrer: STRING,
  utm_source: STRING,
  utm_medium: STRING,
  utm_campaign: STRING,
  campaign_id: STRING,
  affiliate_id: STRING,
  entry_point: STRING,
  is_resumed: BOOLEAN,
  // Set by OptikPI on derived events
  abandoned_at_stage: STRING,
  inactivity_sec: COUNT
});

const REQUIRED_FIELDS = Object.freeze([
  'account_id',
  'workspace_id',
  'user_id',
  'event_category',
  'event_name',
  'event_id',
  'event_time',
  'funnel_session_id',
  'funnel_stage'
]);

const EVENT_CATEGORY = 'Purchase Funnel';

// Same window the event store accepts on its partition column.
const TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000;
const ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000;

// Seconds and a zone are required: a date-time without them is lost downstream.
const DATETIME_PATTERN = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\.\d{1,9})?(Z|[+-]\d{2}:\d{2})$/;
const CURRENCY_PATTERN = /^[A-Z]{3}$/;

/**
 * Milliseconds since the epoch for an ISO 8601 date-time string, or NaN when it is not one.
 * @param {string} value
 * @returns {number}
 */
function parseDateTime(value) {
  const match = DATETIME_PATTERN.exec(value);
  if (!match) return NaN;
  const [year, month, day, hour, minute, second] = match.slice(1, 7).map(Number);
  if (year < 1 || month < 1 || month > 12 || hour > 23 || minute > 59 || second > 59) return NaN;
  const date = new Date(0);
  // setUTCFullYear, because Date.UTC maps years below 100 to 19xx.
  date.setUTCFullYear(year, month - 1, day);
  if (date.getUTCFullYear() !== year || date.getUTCMonth() !== month - 1 || date.getUTCDate() !== day) return NaN;
  date.setUTCHours(hour, minute, second, 0);
  let time = date.getTime();
  const zone = match[8];
  if (zone !== 'Z') {
    const offsetHours = Number(zone.slice(1, 3));
    const offsetMinutes = Number(zone.slice(4, 6));
    // A zone offset is at most 18:00 either way.
    if (offsetMinutes > 59 || offsetHours * 60 + offsetMinutes > 18 * 60) return NaN;
    const sign = zone[0] === '-' ? -1 : 1;
    time -= sign * (offsetHours * 60 + offsetMinutes) * 60 * 1000;
  }
  return time;
}

const INT32_MIN = -2147483648;
const INT32_MAX = 2147483647;

// Amounts are stored as decimal(15,2): 13 integer digits and 2 decimal places.
const MONEY_LIMIT = 1e13;

/**
 * True when a finite number fits decimal(15,2): its absolute value is below 10^13 and its shortest round-trip
 * decimal form has at most 2 decimal places. That form has at most 2 decimal places exactly when rounding the
 * number to 2 places gives the same number back, which is what this checks (2.995 and 1e-7 do not fit).
 * @param {number} value
 * @returns {boolean}
 */
function fitsMoney(value) {
  return Math.abs(value) < MONEY_LIMIT && Number(value.toFixed(2)) === value;
}

/** Returns an error message, or null when the value has the field's kind. */
function checkKind(field, value, kind) {
  switch (kind) {
  case STRING:
    return typeof value === 'string' ? null : `${field} must be a string`;
  case MONEY:
    if (typeof value !== 'number' || !Number.isFinite(value)) return `${field} must be a finite number`;
    return fitsMoney(value) ? null : `${field} must fit decimal(15,2): below 10^13 with at most 2 decimal places`;
  case COUNT:
    // Counts are stored as 32-bit integers.
    return Number.isInteger(value) && value >= INT32_MIN && value <= INT32_MAX
      ? null
      : `${field} must be an integer between ${INT32_MIN} and ${INT32_MAX}`;
  case BOOLEAN:
    return typeof value === 'boolean' ? null : `${field} must be true or false`;
  case DATETIME:
    return typeof value === 'string' && !Number.isNaN(parseDateTime(value))
      ? null
      : `${field} must be an ISO 8601 date-time with seconds and a zone (YYYY-MM-DDTHH:mm:ss[.fff]Z or +hh:mm)`;
  default:
    return null;
  }
}

class PurchaseFunnelEvent {
  constructor(data = {}) {
    Object.keys(FIELDS).forEach(field => {
      this[field] = data[field];
    });
  }

  /**
   * Validates the purchase funnel event data
   * @param {number} [now] - Current time in ms, for the event_time window (tests)
   * @returns {Object} Validation result with isValid boolean and errors array
   */
  validate(now = Date.now()) {
    const errors = [];

    Object.entries(FIELDS).forEach(([field, kind]) => {
      const value = this[field];
      if (value === undefined || value === null || value === '') {
        if (REQUIRED_FIELDS.includes(field)) {
          errors.push(`${field} is required`);
          return;
        }
        // An empty optional value is still checked: '' is a string, but not a number or a date-time.
        if (value !== '') return;
      }
      const error = checkKind(field, value, kind);
      if (error) errors.push(error);
    });

    if (typeof this.event_category === 'string' && this.event_category !== ''
      && this.event_category !== EVENT_CATEGORY) {
      errors.push(`event_category must be "${EVENT_CATEGORY}" for purchase funnel events`);
    }

    if (typeof this.funnel_stage === 'string' && this.funnel_stage !== ''
      && !FUNNEL_STAGES.includes(this.funnel_stage)) {
      errors.push(`funnel_stage must be one of: ${FUNNEL_STAGES.join(', ')}`);
    }

    if (typeof this.failure_reason === 'string' && !FAILURE_REASONS.includes(this.failure_reason)) {
      errors.push(`failure_reason must be one of: ${FAILURE_REASONS.join(', ')}`);
    }

    if (typeof this.currency === 'string' && !CURRENCY_PATTERN.test(this.currency)) {
      errors.push('currency must be a valid 3-letter ISO currency code');
    }

    if (typeof this.event_time === 'string') {
      const time = parseDateTime(this.event_time);
      if (!Number.isNaN(time) && (time < now - TEN_YEARS_MS || time > now + ONE_YEAR_MS)) {
        errors.push('event_time must be within the last 10 years and no more than 1 year in the future');
      }
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  /**
   * Converts the model to a plain object
   * @returns {Object} Plain object representation
   */
  toJSON() {
    const obj = {};
    Object.keys(this).forEach(key => {
      if (this[key] !== undefined) {
        obj[key] = this[key];
      }
    });
    return obj;
  }

  /**
   * Creates a PurchaseFunnelEvent instance from plain object
   * @param {Object} data - Plain object data
   * @returns {PurchaseFunnelEvent} New PurchaseFunnelEvent instance
   */
  static fromObject(data) {
    return new PurchaseFunnelEvent(data);
  }
}

PurchaseFunnelEvent.FUNNEL_STAGES = FUNNEL_STAGES;
PurchaseFunnelEvent.FAILURE_REASONS = FAILURE_REASONS;

module.exports = PurchaseFunnelEvent;
