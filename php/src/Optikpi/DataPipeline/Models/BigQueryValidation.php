<?php

namespace Optikpi\DataPipeline\Models;

/**
 * Validation of a record against the column types of the warehouse table it is stored in.
 *
 * A record the API accepts but the warehouse cannot store is lost without an error reaching the caller, so
 * every model checks its fields against these rules before anything is sent. The rules follow what the
 * warehouse was measured to store; the few values it stores as junk ("abc" as a number, "yes" as a boolean)
 * are rejected on purpose.
 */
class BigQueryValidation
{
    private const TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000;
    private const ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000;

    // The D modifier stops "$" from matching before a trailing newline. Padding is ASCII whitespace only
    // (space, \t, \n, \r, \f, \v), spelled out because \s can follow the locale's character tables.
    private const NUMERIC_STRING = '/^[ \t\n\r\f\x0B]*[+-]?(\d+\.?\d*|\.\d+)([eE][+-]?\d+)?[ \t\n\r\f\x0B]*$/D';
    private const INTEGER_STRING = '/^[+-]?\d+$/D';
    private const DIGIT_STRING = '/^\d+$/D';
    private const DATE_STRING = '/^(\d{4})-(\d{1,2})-(\d{1,2})$/D';
    private const TIMESTAMP_STRING =
        '/^(\d{4})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})(:(\d{2})(\.\d+)?)?(Z|[+-]\d{2}:\d{2}| UTC)?$/D';

    /**
     * The record as it will be stored: every declared (public) property with its current value, even when null,
     * plus the raw input keys the model does not declare.
     *
     * @param object $model The model instance
     * @param array $rawInput The array the model was constructed from
     */
    public static function storageRecord($model, array $rawInput): array
    {
        // Declared fields come from the class, so a property removed with unset() is missing rather than
        // refilled from the raw input. Called from this class, get_class_vars() returns only public properties.
        $declared = [];
        foreach (array_keys(get_class_vars(get_class($model))) as $key) {
            $declared[$key] = $model->$key ?? null;
        }
        return $declared + array_diff_key($rawInput, $declared);
    }

    /**
     * @param array $record The values to check, as they will be sent
     * @param array $rules ['required' => string[], 'types' => [field => type], 'partition' => ?string]
     * @param float|null $nowMs Current time in ms since the epoch (for tests)
     * @return string[] Error messages; empty when the record can be stored
     */
    public static function validateStorage(array $record, array $rules, ?float $nowMs = null): array
    {
        $nowMs = $nowMs ?? microtime(true) * 1000;
        $errors = [];

        foreach ($rules['required'] as $field) {
            $value = $record[$field] ?? null;
            if ($value === null || $value === '') {
                $errors[] = $field . ' is required';
            }
        }

        foreach ($rules['types'] as $field => $type) {
            $value = $record[$field] ?? null;
            if ($value === null) {
                continue;
            }
            $error = self::checkType($field, $value, $type);
            if ($error !== null) {
                $errors[] = $error;
            }
        }

        // The table is partitioned on this column and only accepts writes within this window. Only date-time
        // strings are checked: the unit of a numeric timestamp is ambiguous.
        $partition = $rules['partition'] ?? null;
        if ($partition !== null && is_string($record[$partition] ?? null)) {
            $time = self::parseTimestampString($record[$partition]);
            if ($time !== null && ($time < $nowMs - self::TEN_YEARS_MS || $time > $nowMs + self::ONE_YEAR_MS)) {
                $errors[] = $partition . ' must be within the last 10 years and no more than 1 year in the future';
            }
        }

        return $errors;
    }

    /**
     * @return string|null An error message, or null when the value can be stored in a column of this type
     */
    public static function checkType(string $field, $value, string $type): ?string
    {
        switch ($type) {
            case 'STRING':
                return is_string($value) ? null : $field . ' must be a string';
            case 'NUMERIC':
                if (is_int($value)) {
                    return null;
                }
                if (is_float($value)) {
                    return is_finite($value) ? null : $field . ' must be a finite number';
                }
                return is_string($value) && preg_match(self::NUMERIC_STRING, $value) === 1
                    ? null
                    : $field . ' must be a number';
            case 'INTEGER':
                if (self::isIntegral($value)) {
                    return null;
                }
                return is_string($value) && preg_match(self::INTEGER_STRING, $value) === 1
                    ? null
                    : $field . ' must be an integer';
            case 'BOOLEAN':
                return self::isBoolean($value) ? null : $field . ' must be true or false';
            case 'TIMESTAMP':
                return self::isTimestamp($value) ? null : $field . ' must be a valid ISO 8601 date-time';
            case 'DATE':
                return self::isDate($value) ? null : $field . ' must be a date in YYYY-MM-DD format';
            case 'JSON':
                // The API serialises arrays and objects to a JSON string before storing them.
                if (is_array($value) || is_object($value)) {
                    return null;
                }
                if (is_string($value) && $value !== '') {
                    json_decode($value);
                    if (json_last_error() === JSON_ERROR_NONE) {
                        return null;
                    }
                }
                return $field . ' must be an object or a valid JSON string';
            default:
                return null;
        }
    }

    /** An int, or a finite float with no fractional part. Booleans are not numbers. */
    private static function isIntegral($value): bool
    {
        if (is_int($value)) {
            return true;
        }
        return is_float($value) && is_finite($value) && floor($value) === $value;
    }

    private static function isBoolean($value): bool
    {
        if (is_bool($value)) {
            return true;
        }
        if (is_int($value) || is_float($value)) {
            return $value == 0 || $value == 1;
        }
        return is_string($value) && in_array(strtolower($value), ['true', 'false', '0', '1'], true);
    }

    private static function isTimestamp($value): bool
    {
        if (is_int($value)) {
            return true;
        }
        if (is_float($value)) {
            return is_finite($value);
        }
        if (!is_string($value)) {
            return false;
        }
        return preg_match(self::DIGIT_STRING, $value) === 1 || self::parseTimestampString($value) !== null;
    }

    private static function isDate($value): bool
    {
        if (self::isIntegral($value)) {
            return true;
        }
        if (!is_string($value)) {
            return false;
        }
        if (preg_match(self::DIGIT_STRING, $value) === 1) {
            return true;
        }
        return preg_match(self::DATE_STRING, $value, $m) === 1 && checkdate((int) $m[2], (int) $m[3], (int) $m[1]);
    }

    /**
     * Milliseconds since the epoch for a date-time string in one of the accepted forms, or null.
     * Years 0001-9999; no zone means UTC; offsets up to ±18:59.
     */
    private static function parseTimestampString(string $value): ?float
    {
        if (preg_match(self::TIMESTAMP_STRING, $value, $m) !== 1) {
            return null;
        }
        $year = (int) $m[1];
        $month = (int) $m[2];
        $day = (int) $m[3];
        $hour = (int) $m[4];
        $minute = (int) $m[5];
        $second = isset($m[7]) && $m[7] !== '' ? (int) $m[7] : 0;
        if (!checkdate($month, $day, $year) || $hour >= 24 || $minute >= 60 || $second >= 60) {
            return null;
        }

        // Not gmmktime(): it maps two-digit years (0-100) to 1970-2069.
        $seconds = (float) (self::daysFromCivil($year, $month, $day) * 86400 + $hour * 3600 + $minute * 60 + $second);
        if (isset($m[8]) && $m[8] !== '') {
            $seconds += (float) ('0' . $m[8]);
        }
        $zone = $m[9] ?? '';
        if ($zone !== '' && $zone !== 'Z' && $zone !== ' UTC') {
            $sign = $zone[0] === '-' ? -1 : 1;
            $offsetHours = (int) substr($zone, 1, 2);
            $offsetMinutes = (int) substr($zone, 4, 2);
            if ($offsetHours > 18 || $offsetMinutes > 59) {
                return null;
            }
            $seconds -= $sign * ($offsetHours * 3600 + $offsetMinutes * 60);
        }
        return $seconds * 1000;
    }

    /** Days since 1970-01-01 in the proleptic Gregorian calendar, for years 1 and up. */
    private static function daysFromCivil(int $year, int $month, int $day): int
    {
        $year -= $month <= 2 ? 1 : 0;
        $era = intdiv($year, 400);
        $yearOfEra = $year - $era * 400;
        $dayOfYear = intdiv(153 * ($month + ($month > 2 ? -3 : 9)) + 2, 5) + $day - 1;
        $dayOfEra = $yearOfEra * 365 + intdiv($yearOfEra, 4) - intdiv($yearOfEra, 100) + $dayOfYear;
        return $era * 146097 + $dayOfEra - 719468;
    }
}
