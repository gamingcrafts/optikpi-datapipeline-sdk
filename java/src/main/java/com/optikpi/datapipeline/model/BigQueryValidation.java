package com.optikpi.datapipeline.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Validation that mirrors what the warehouse accepts for each column type.
 *
 * <p>A record the API accepts but the warehouse cannot store is lost without an error reaching the caller, so
 * every model checks its fields against these rules before anything is sent. The rules follow what a dev
 * BigQuery probe measured the warehouse to accept and reject, with two deliberate exceptions where it stores
 * junk: NUMERIC rejects non-numeric text such as "abc" and BOOLEAN rejects strings such as "yes".
 *
 * <p>Values are checked as they will be serialised to JSON, so a model is first converted with Jackson.
 */
public final class BigQueryValidation {

    /** Column types of the warehouse tables. */
    public enum ColumnType { STRING, NUMERIC, INTEGER, BOOLEAN, TIMESTAMP, DATE, JSON }

    private static final double ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000;
    private static final double TEN_YEARS_MS = 10 * ONE_YEAR_MS;

    // Java's \\s is ASCII whitespace only ([ \\t\\n\\x0B\\f\\r]); no NBSP or other Unicode spaces.
    private static final Pattern DECIMAL_STRING =
        Pattern.compile("^\\s*[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?\\s*$");
    private static final Pattern INTEGER_STRING = Pattern.compile("^[+-]?\\d+$");
    private static final Pattern DIGITS = Pattern.compile("^\\d+$");
    private static final Pattern DATE_STRING = Pattern.compile("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$");
    // The only date-time strings the warehouse was measured to accept: two-digit parts, T or space, optional
    // seconds and fraction, and an offset of Z, +HH:MM or " UTC" (none = UTC). Zone names are rejected.
    private static final Pattern TIMESTAMP_STRING = Pattern.compile(
        "^(\\d{4})-(\\d{2})-(\\d{2})[T ](\\d{2}):(\\d{2})(?::(\\d{2})(?:\\.(\\d+))?)?(Z|[+-]\\d{2}:\\d{2}| UTC)?$");

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    private static final TypeReference<Map<String, Object>> RECORD = new TypeReference<Map<String, Object>>() {};

    private BigQueryValidation() {}

    /** The fields a table requires, the column types of the fields a model can send, and its partition column. */
    public static final class Rules {
        private final List<String> required;
        private final Map<String, ColumnType> types = new LinkedHashMap<>();
        private String partition;

        private Rules(List<String> required) {
            this.required = required;
        }

        /** Fields that must be present, not null and not empty. */
        public static Rules required(String... fields) {
            return new Rules(Collections.unmodifiableList(Arrays.asList(fields)));
        }

        /** Declares the column type of these fields. */
        public Rules columns(ColumnType type, String... fields) {
            for (String field : fields) {
                types.put(field, type);
            }
            return this;
        }

        /** The column the table is partitioned on. */
        public Rules partition(String field) {
            this.partition = field;
            return this;
        }
    }

    /**
     * Converts a model to the record it serialises to and checks it.
     * @return Error messages; empty when the record can be stored
     */
    public static List<String> validateStorage(Object model, Rules rules) {
        return validateStorage(MAPPER.convertValue(model, RECORD), rules, System.currentTimeMillis());
    }

    /** Appends the storage errors of a model to {@code errors}, skipping any already reported. */
    static void addStorageErrors(List<String> errors, Object model, Rules rules) {
        for (String error : validateStorage(model, rules)) {
            if (!errors.contains(error)) {
                errors.add(error);
            }
        }
    }

    /**
     * @param record The values to check, as they will be sent
     * @param rules Required fields, column types and partition column
     * @param now Current time in ms (for tests)
     * @return Error messages; empty when the record can be stored
     */
    public static List<String> validateStorage(Map<String, ?> record, Rules rules, long now) {
        List<String> errors = new ArrayList<>();

        for (String field : rules.required) {
            Object value = record.get(field);
            if (value == null || "".equals(value)) {
                errors.add(field + " is required");
            }
        }

        for (Map.Entry<String, ColumnType> column : rules.types.entrySet()) {
            Object value = record.get(column.getKey());
            if (value == null) {
                continue;
            }
            String error = checkType(column.getKey(), value, column.getValue());
            if (error != null) {
                errors.add(error);
            }
        }

        // The table is partitioned on this column and only accepts writes within this window. Only string
        // timestamps are checked: the unit of a numeric one is ambiguous.
        Object partitionValue = rules.partition == null ? null : record.get(rules.partition);
        if (partitionValue != null) {
            double time = parseTimestampString(partitionValue);
            if (!Double.isNaN(time) && (time < now - TEN_YEARS_MS || time > now + ONE_YEAR_MS)) {
                errors.add(rules.partition + " must be within the last 10 years and no more than 1 year in the future");
            }
        }

        return errors;
    }

    /** Returns an error message, or null when the value can be stored in a column of this type. */
    public static String checkType(String field, Object value, ColumnType type) {
        switch (type) {
            case STRING:
                return value instanceof String ? null : field + " must be a string";
            case NUMERIC:
                if (value instanceof Number) {
                    return isFinite((Number) value) ? null : field + " must be a finite number";
                }
                return value instanceof String && DECIMAL_STRING.matcher((String) value).matches()
                    ? null : field + " must be a number";
            case INTEGER:
                if (value instanceof Number) {
                    return isIntegral((Number) value) ? null : field + " must be an integer";
                }
                return value instanceof String && INTEGER_STRING.matcher((String) value).matches()
                    ? null : field + " must be an integer";
            case BOOLEAN:
                return isBoolean(value) ? null : field + " must be true or false";
            case TIMESTAMP:
                return isTimestamp(value) ? null : field + " must be a valid ISO 8601 date-time";
            case DATE:
                return isDate(value) ? null : field + " must be a date in YYYY-MM-DD format";
            case JSON:
                // The API serialises objects and arrays to a JSON string before storing them.
                if (value instanceof Map || value instanceof List) {
                    return null;
                }
                return value instanceof String && isJson((String) value)
                    ? null : field + " must be an object or a valid JSON string";
            default:
                return null;
        }
    }

    private static boolean isFinite(Number value) {
        if (value instanceof Double || value instanceof Float) {
            return Double.isFinite(value.doubleValue());
        }
        return true;
    }

    private static boolean isIntegral(Number value) {
        if (value instanceof Double || value instanceof Float) {
            double d = value.doubleValue();
            return Double.isFinite(d) && d == Math.rint(d);
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).stripTrailingZeros().scale() <= 0;
        }
        return value instanceof Integer || value instanceof Long || value instanceof Short
            || value instanceof Byte || value instanceof BigInteger;
    }

    private static boolean isBoolean(Object value) {
        if (value instanceof Boolean) {
            return true;
        }
        if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            return d == 0 || d == 1;
        }
        if (value instanceof String) {
            // ASCII only: Unicode case folding maps some non-ASCII letters onto ASCII ones (U+017F to "s" in
            // equalsIgnoreCase, the Kelvin sign U+212A to "k" in toLowerCase).
            String text = (String) value;
            if (!text.chars().allMatch(c -> c < 128)) {
                return false;
            }
            String lower = text.toLowerCase(Locale.ROOT);
            return lower.equals("true") || lower.equals("false") || text.equals("0") || text.equals("1");
        }
        return false;
    }

    private static boolean isJson(String value) {
        try {
            JsonNode node = MAPPER.readTree(value);
            return node != null && !node.isMissingNode();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDate(Object value) {
        if (value instanceof Number) {
            return isIntegral((Number) value);
        }
        if (!(value instanceof String)) {
            return false;
        }
        if (DIGITS.matcher((String) value).matches()) {
            return true;
        }
        Matcher match = DATE_STRING.matcher((String) value);
        return match.matches() && realDate(match.group(1), match.group(2), match.group(3)) != null;
    }

    /** A finite number or digit-only string (an epoch value), or a date-time string the warehouse parses. */
    private static boolean isTimestamp(Object value) {
        if (value instanceof Number) {
            return isFinite((Number) value);
        }
        if (value instanceof String && DIGITS.matcher((String) value).matches()) {
            return true;
        }
        return !Double.isNaN(parseTimestampString(value));
    }

    /** Milliseconds since the epoch of a date-time string, or NaN when it is not one the warehouse parses. */
    static double parseTimestampString(Object value) {
        if (!(value instanceof String)) {
            return Double.NaN;
        }
        Matcher match = TIMESTAMP_STRING.matcher((String) value);
        if (!match.matches()) {
            return Double.NaN;
        }
        LocalDate date = realDate(match.group(1), match.group(2), match.group(3));
        Integer offsetSeconds = offsetSeconds(match.group(8));
        if (date == null || offsetSeconds == null) {
            return Double.NaN;
        }
        try {
            String fraction = match.group(7) == null ? "0" : (match.group(7) + "000000000").substring(0, 9);
            LocalTime time = LocalTime.of(Integer.parseInt(match.group(4)), Integer.parseInt(match.group(5)),
                match.group(6) == null ? 0 : Integer.parseInt(match.group(6)), Integer.parseInt(fraction));
            return LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC).minusSeconds(offsetSeconds).toEpochMilli();
        } catch (DateTimeException e) {
            return Double.NaN;
        }
    }

    /** The date, or null unless it is a real calendar date in years 0001-9999. */
    private static LocalDate realDate(String year, String month, String day) {
        int y = Integer.parseInt(year);
        if (y < 1 || y > 9999) {
            return null;
        }
        try {
            return LocalDate.of(y, Integer.parseInt(month), Integer.parseInt(day));
        } catch (DateTimeException e) {
            return null;
        }
    }

    /** Seconds east of UTC for Z, " UTC" or +-HH:MM with HH at most 18 and MM at most 59 (none = UTC), or null. */
    private static Integer offsetSeconds(String zone) {
        if (zone == null || zone.equals("Z") || zone.equals(" UTC")) {
            return 0;
        }
        int hours = Integer.parseInt(zone.substring(1, 3));
        int minutes = Integer.parseInt(zone.substring(4, 6));
        if (hours > 18 || minutes > 59) {
            return null;
        }
        // Applied arithmetically: ZoneOffset cannot represent offsets beyond +-18:00 such as +18:30.
        return (zone.charAt(0) == '-' ? -1 : 1) * (hours * 3600 + minutes * 60);
    }
}
