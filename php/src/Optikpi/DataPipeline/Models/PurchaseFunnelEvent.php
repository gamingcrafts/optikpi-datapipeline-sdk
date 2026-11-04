<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Purchase Funnel Event Model
 *
 * One step of a purchase attempt (a funnel session). Every event of one attempt shares a funnel_session_id.
 * Its meaning comes from funnel_stage (a fixed list, FUNNEL_STAGES); event_name is the operator's own free text.
 */
class PurchaseFunnelEvent
{
    /** Allowed funnel_stage values, in funnel order. */
    public const FUNNEL_STAGES = [
        'start', 'browse', 'configure', 'cart', 'checkout', 'payment', 'payment_failed', 'complete',
        'failed', 'cancelled', 'exit', 'stalled', 'abandoned',
    ];

    /** Allowed failure_reason values. */
    public const FAILURE_REASONS = [
        'payment_declined', 'insufficient_funds', 'provider_timeout', 'limit_reached', 'kyc_required',
        'geo_blocked', 'draw_closed', 'technical_error', 'user_cancelled',
    ];

    public const EVENT_CATEGORY = 'Purchase Funnel';

    private const KIND_STRING = 'string';
    private const KIND_MONEY = 'money';
    private const KIND_COUNT = 'count';
    private const KIND_BOOLEAN = 'boolean';
    private const KIND_DATETIME = 'datetime';

    /** Every field the model carries, with its kind. */
    private const FIELDS = [
        'account_id' => self::KIND_STRING,
        'workspace_id' => self::KIND_STRING,
        'user_id' => self::KIND_STRING,
        'event_id' => self::KIND_STRING,
        'event_time' => self::KIND_DATETIME,
        'event_category' => self::KIND_STRING,
        'event_name' => self::KIND_STRING,
        'funnel_session_id' => self::KIND_STRING,
        'funnel_stage' => self::KIND_STRING,
        'item_id' => self::KIND_STRING,
        'item_name' => self::KIND_STRING,
        'item_category' => self::KIND_STRING,
        'draw_id' => self::KIND_STRING,
        'draw_number' => self::KIND_STRING,
        'draw_date' => self::KIND_DATETIME,
        'draw_close_time' => self::KIND_DATETIME,
        'jackpot_amount' => self::KIND_MONEY,
        'selection' => self::KIND_STRING,
        'selection_method' => self::KIND_STRING,
        'lines_count' => self::KIND_COUNT,
        'multi_draw_count' => self::KIND_COUNT,
        'unit_price' => self::KIND_MONEY,
        'item_quantity' => self::KIND_COUNT,
        'item_amount' => self::KIND_MONEY,
        'cart_id' => self::KIND_STRING,
        'cart_item_count' => self::KIND_COUNT,
        'cart_quantity_total' => self::KIND_COUNT,
        'cart_items' => self::KIND_STRING,
        'cart_subtotal' => self::KIND_MONEY,
        'discount_amount' => self::KIND_MONEY,
        'bonus_used_amount' => self::KIND_MONEY,
        'fees_amount' => self::KIND_MONEY,
        'tax_amount' => self::KIND_MONEY,
        'cart_total' => self::KIND_MONEY,
        'coupon_code' => self::KIND_STRING,
        'currency' => self::KIND_STRING,
        'payment_method' => self::KIND_STRING,
        'payment_provider_id' => self::KIND_STRING,
        'payment_provider_name' => self::KIND_STRING,
        'is_saved_method' => self::KIND_BOOLEAN,
        'funds_source' => self::KIND_STRING,
        'payment_attempt_number' => self::KIND_COUNT,
        'payment_status' => self::KIND_STRING,
        'transaction_id' => self::KIND_STRING,
        'failure_reason' => self::KIND_STRING,
        'failure_code' => self::KIND_STRING,
        'failure_message' => self::KIND_STRING,
        'order_id' => self::KIND_STRING,
        'ticket_ids' => self::KIND_STRING,
        'order_total' => self::KIND_MONEY,
        'order_status' => self::KIND_STRING,
        'is_first_purchase' => self::KIND_BOOLEAN,
        'device' => self::KIND_STRING,
        'platform' => self::KIND_STRING,
        'app_version' => self::KIND_STRING,
        'page_url' => self::KIND_STRING,
        'referrer' => self::KIND_STRING,
        'utm_source' => self::KIND_STRING,
        'utm_medium' => self::KIND_STRING,
        'utm_campaign' => self::KIND_STRING,
        'campaign_id' => self::KIND_STRING,
        'affiliate_id' => self::KIND_STRING,
        'entry_point' => self::KIND_STRING,
        'is_resumed' => self::KIND_BOOLEAN,
        'abandoned_at_stage' => self::KIND_STRING,
        'inactivity_sec' => self::KIND_COUNT,
    ];

    private const REQUIRED_FIELDS = ['account_id', 'workspace_id', 'user_id', 'event_category', 'event_name', 'event_id', 'event_time', 'funnel_session_id', 'funnel_stage'];

    // Same window the event store accepts on its partition column.
    private const TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000;
    private const ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000;

    private const DATETIME_PATTERN =
        '/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2})(\.\d{1,9})?)?(Z|[+-]\d{2}:\d{2})?$/D';

    public $account_id;
    public $workspace_id;
    public $user_id;
    public $event_id;
    public $event_time;
    public $event_category;
    public $event_name;
    public $funnel_session_id;
    public $funnel_stage;
    public $item_id;
    public $item_name;
    public $item_category;
    public $draw_id;
    public $draw_number;
    public $draw_date;
    public $draw_close_time;
    public $jackpot_amount;
    public $selection;
    public $selection_method;
    public $lines_count;
    public $multi_draw_count;
    public $unit_price;
    public $item_quantity;
    public $item_amount;
    public $cart_id;
    public $cart_item_count;
    public $cart_quantity_total;
    public $cart_items;
    public $cart_subtotal;
    public $discount_amount;
    public $bonus_used_amount;
    public $fees_amount;
    public $tax_amount;
    public $cart_total;
    public $coupon_code;
    public $currency;
    public $payment_method;
    public $payment_provider_id;
    public $payment_provider_name;
    public $is_saved_method;
    public $funds_source;
    public $payment_attempt_number;
    public $payment_status;
    public $transaction_id;
    public $failure_reason;
    public $failure_code;
    public $failure_message;
    public $order_id;
    public $ticket_ids;
    public $order_total;
    public $order_status;
    public $is_first_purchase;
    public $device;
    public $platform;
    public $app_version;
    public $page_url;
    public $referrer;
    public $utm_source;
    public $utm_medium;
    public $utm_campaign;
    public $campaign_id;
    public $affiliate_id;
    public $entry_point;
    public $is_resumed;
    public $abandoned_at_stage;
    public $inactivity_sec;

    /**
     * Constructor. event_category is required and is not defaulted. Unknown keys are ignored.
     *
     * @param array $data Purchase funnel event data
     */
    public function __construct(array $data = [])
    {
        foreach ($data as $key => $value) {
            if (array_key_exists($key, self::FIELDS)) {
                $this->$key = $value;
            }
        }
    }

    /**
     * Validates the purchase funnel event data
     *
     * @param float|null $nowMs Current time in ms since the epoch, for the event_time window (tests)
     * @return array Validation result with isValid boolean and errors array
     */
    public function validate(?float $nowMs = null): array
    {
        $errors = [];

        foreach (self::FIELDS as $field => $kind) {
            $value = $this->$field;
            if ($value === null || $value === '') {
                if (in_array($field, self::REQUIRED_FIELDS, true)) {
                    $errors[] = "$field is required";
                    continue;
                }
                // An empty optional value is still checked: '' is a string, but not a number or a date-time.
                if ($value === null) {
                    continue;
                }
            }
            $error = self::kindError($field, $value, $kind);
            if ($error !== null) {
                $errors[] = $error;
            }
        }

        if (is_string($this->event_category) && $this->event_category !== ''
            && $this->event_category !== self::EVENT_CATEGORY) {
            $errors[] = 'event_category must be "' . self::EVENT_CATEGORY . '" for purchase funnel events';
        }

        if (is_string($this->funnel_stage) && $this->funnel_stage !== ''
            && !in_array($this->funnel_stage, self::FUNNEL_STAGES, true)) {
            $errors[] = 'funnel_stage must be one of: ' . implode(', ', self::FUNNEL_STAGES);
        }

        if (is_string($this->failure_reason) && !in_array($this->failure_reason, self::FAILURE_REASONS, true)) {
            $errors[] = 'failure_reason must be one of: ' . implode(', ', self::FAILURE_REASONS);
        }

        if (is_string($this->currency) && preg_match('/^[A-Z]{3}$/D', $this->currency) !== 1) {
            $errors[] = 'currency must be a valid 3-letter ISO currency code';
        }

        if (is_string($this->event_time)) {
            $eventMs = self::parseDateTimeMs($this->event_time);
            $now = $nowMs ?? microtime(true) * 1000;
            if ($eventMs !== null && ($eventMs < $now - self::TEN_YEARS_MS || $eventMs > $now + self::ONE_YEAR_MS)) {
                $errors[] = 'event_time must be within the last 10 years and no more than 1 year in the future';
            }
        }

        return [
            'isValid' => empty($errors),
            'errors' => $errors
        ];
    }

    /**
     * Returns an error message, or null when the value has the field's kind.
     */
    private static function kindError(string $field, $value, string $kind): ?string
    {
        switch ($kind) {
            case self::KIND_STRING:
                return is_string($value) ? null : "$field must be a string";
            case self::KIND_MONEY:
                // is_int(true) is false, so a boolean is not a number here.
                return ((is_int($value) || is_float($value)) && is_finite((float) $value))
                    ? null : "$field must be a finite number";
            case self::KIND_COUNT:
                // json_decode reads 2.0 as a float; it is an integer in JSON, as for the API.
                return (is_int($value) || (is_float($value) && is_finite($value) && floor($value) === $value))
                    ? null : "$field must be an integer";
            case self::KIND_BOOLEAN:
                return is_bool($value) ? null : "$field must be true or false";
            case self::KIND_DATETIME:
                return (is_string($value) && self::parseDateTimeMs($value) !== null)
                    ? null : "$field must be an ISO 8601 date-time (YYYY-MM-DDTHH:mm[:ss[.fff]][Z|+hh:mm])";
        }
        return null;
    }

    /**
     * Milliseconds since the epoch for an ISO 8601 date-time string, or null when it is not one.
     * A string without a zone is read as UTC.
     */
    public static function parseDateTimeMs(string $value): ?float
    {
        if (preg_match(self::DATETIME_PATTERN, $value, $m) !== 1) {
            return null;
        }
        $year = (int) $m[1];
        $month = (int) $m[2];
        $day = (int) $m[3];
        $hour = (int) $m[4];
        $minute = (int) $m[5];
        $second = isset($m[6]) && $m[6] !== '' ? (int) $m[6] : 0;
        if ($year < 1 || !checkdate($month, $day, $year) || $hour > 23 || $minute > 59 || $second > 59) {
            return null;
        }
        $seconds = gmmktime($hour, $minute, $second, $month, $day, $year);
        $zone = $m[8] ?? '';
        if ($zone !== '' && $zone !== 'Z') {
            $offsetHours = (int) substr($zone, 1, 2);
            $offsetMinutes = (int) substr($zone, 4, 2);
            if ($offsetHours > 18 || $offsetMinutes > 59) {
                return null;
            }
            $sign = $zone[0] === '-' ? -1 : 1;
            $seconds -= $sign * ($offsetHours * 60 + $offsetMinutes) * 60;
        }
        return $seconds * 1000.0;
    }

    /**
     * Converts the model to a plain array
     *
     * @return array Plain array representation
     */
    public function toArray(): array
    {
        $arr = [];
        foreach (get_object_vars($this) as $key => $value) {
            // As in the other models: null values are left out.
            if ($value !== null) {
                $arr[$key] = $value;
            }
        }
        return $arr;
    }

    /**
     * Creates a PurchaseFunnelEvent instance from array
     *
     * @param array $data Plain array data
     * @return PurchaseFunnelEvent New PurchaseFunnelEvent instance
     */
    public static function fromArray(array $data): self
    {
        return new self($data);
    }
}
