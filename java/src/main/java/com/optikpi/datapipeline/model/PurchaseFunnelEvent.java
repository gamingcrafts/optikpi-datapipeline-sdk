package com.optikpi.datapipeline.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Purchase Funnel Event Model.
 *
 * <p>One step of a purchase attempt (a funnel session). Every event of one attempt shares a
 * {@code funnel_session_id}. Its meaning comes from {@code funnel_stage} (a fixed list, {@link #FUNNEL_STAGES});
 * {@code event_name} is the operator's own free text.
 *
 * <p>{@link #validate()} checks an event built in code; the field types already rule out wrong JSON kinds.
 * {@link #validateJson(JsonNode)} checks a raw JSON record: the JSON kind of each field first (a number where a
 * string belongs, {@code "12.50"} for an amount, {@code 2.5} for a count, {@code "true"} for a boolean), then
 * everything {@link #validate()} checks.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseFunnelEvent {

    /** Allowed {@code funnel_stage} values, in funnel order. */
    public static final List<String> FUNNEL_STAGES = List.of(
        "start", "browse", "configure", "cart", "checkout", "payment", "payment_failed", "complete",
        "failed", "cancelled", "exit", "stalled", "abandoned");

    /** Allowed {@code failure_reason} values. */
    public static final List<String> FAILURE_REASONS = List.of(
        "payment_declined", "insufficient_funds", "provider_timeout", "limit_reached", "kyc_required",
        "geo_blocked", "draw_closed", "technical_error", "user_cancelled");

    public static final String EVENT_CATEGORY = "Purchase Funnel";

    private enum Kind { STRING, MONEY, COUNT, BOOLEAN, DATETIME }

    private static final Map<String, Kind> FIELD_KINDS;
    static {
        Map<String, Kind> kinds = new LinkedHashMap<>();
        kinds.put("account_id", Kind.STRING);
        kinds.put("workspace_id", Kind.STRING);
        kinds.put("user_id", Kind.STRING);
        kinds.put("event_id", Kind.STRING);
        kinds.put("event_time", Kind.DATETIME);
        kinds.put("event_category", Kind.STRING);
        kinds.put("event_name", Kind.STRING);
        kinds.put("funnel_session_id", Kind.STRING);
        kinds.put("funnel_stage", Kind.STRING);
        kinds.put("item_id", Kind.STRING);
        kinds.put("item_name", Kind.STRING);
        kinds.put("item_category", Kind.STRING);
        kinds.put("draw_id", Kind.STRING);
        kinds.put("draw_number", Kind.STRING);
        kinds.put("draw_date", Kind.DATETIME);
        kinds.put("draw_close_time", Kind.DATETIME);
        kinds.put("jackpot_amount", Kind.MONEY);
        kinds.put("selection", Kind.STRING);
        kinds.put("selection_method", Kind.STRING);
        kinds.put("lines_count", Kind.COUNT);
        kinds.put("multi_draw_count", Kind.COUNT);
        kinds.put("unit_price", Kind.MONEY);
        kinds.put("item_quantity", Kind.COUNT);
        kinds.put("item_amount", Kind.MONEY);
        kinds.put("cart_id", Kind.STRING);
        kinds.put("cart_item_count", Kind.COUNT);
        kinds.put("cart_quantity_total", Kind.COUNT);
        kinds.put("cart_items", Kind.STRING);
        kinds.put("cart_subtotal", Kind.MONEY);
        kinds.put("discount_amount", Kind.MONEY);
        kinds.put("bonus_used_amount", Kind.MONEY);
        kinds.put("fees_amount", Kind.MONEY);
        kinds.put("tax_amount", Kind.MONEY);
        kinds.put("cart_total", Kind.MONEY);
        kinds.put("coupon_code", Kind.STRING);
        kinds.put("currency", Kind.STRING);
        kinds.put("payment_method", Kind.STRING);
        kinds.put("payment_provider_id", Kind.STRING);
        kinds.put("payment_provider_name", Kind.STRING);
        kinds.put("is_saved_method", Kind.BOOLEAN);
        kinds.put("funds_source", Kind.STRING);
        kinds.put("payment_attempt_number", Kind.COUNT);
        kinds.put("payment_status", Kind.STRING);
        kinds.put("transaction_id", Kind.STRING);
        kinds.put("failure_reason", Kind.STRING);
        kinds.put("failure_code", Kind.STRING);
        kinds.put("failure_message", Kind.STRING);
        kinds.put("order_id", Kind.STRING);
        kinds.put("ticket_ids", Kind.STRING);
        kinds.put("order_total", Kind.MONEY);
        kinds.put("order_status", Kind.STRING);
        kinds.put("is_first_purchase", Kind.BOOLEAN);
        kinds.put("device", Kind.STRING);
        kinds.put("platform", Kind.STRING);
        kinds.put("app_version", Kind.STRING);
        kinds.put("page_url", Kind.STRING);
        kinds.put("referrer", Kind.STRING);
        kinds.put("utm_source", Kind.STRING);
        kinds.put("utm_medium", Kind.STRING);
        kinds.put("utm_campaign", Kind.STRING);
        kinds.put("campaign_id", Kind.STRING);
        kinds.put("affiliate_id", Kind.STRING);
        kinds.put("entry_point", Kind.STRING);
        kinds.put("is_resumed", Kind.BOOLEAN);
        kinds.put("abandoned_at_stage", Kind.STRING);
        kinds.put("inactivity_sec", Kind.COUNT);
        FIELD_KINDS = Collections.unmodifiableMap(kinds);
    }

    // Same window the event store accepts on its partition column.
    private static final double TEN_YEARS_MS = 10 * 365.25 * 24 * 60 * 60 * 1000;
    private static final double ONE_YEAR_MS = 365.25 * 24 * 60 * 60 * 1000;

    private static final Pattern DATETIME_PATTERN = Pattern.compile(
        "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(\\.\\d{1,9})?(Z|[+-]\\d{2}:\\d{2})$");
    // Amounts are stored as decimal(15,2): 13 integer digits and 2 decimal places.
    private static final BigDecimal MONEY_LIMIT = BigDecimal.TEN.pow(13);

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Z]{3}$");

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("workspace_id")
    private String workspaceId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_time")
    private String eventTime;

    @JsonProperty("event_category")
    private String eventCategory;

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("funnel_session_id")
    private String funnelSessionId;

    @JsonProperty("funnel_stage")
    private String funnelStage;

    // Item
    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_category")
    private String itemCategory;

    @JsonProperty("draw_id")
    private String drawId;

    @JsonProperty("draw_number")
    private String drawNumber;

    @JsonProperty("draw_date")
    private String drawDate;

    @JsonProperty("draw_close_time")
    private String drawCloseTime;

    @JsonProperty("jackpot_amount")
    private BigDecimal jackpotAmount;

    @JsonProperty("selection")
    private String selection;

    @JsonProperty("selection_method")
    private String selectionMethod;

    @JsonProperty("lines_count")
    private Integer linesCount;

    @JsonProperty("multi_draw_count")
    private Integer multiDrawCount;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("item_quantity")
    private Integer itemQuantity;

    @JsonProperty("item_amount")
    private BigDecimal itemAmount;

    // Cart snapshot
    @JsonProperty("cart_id")
    private String cartId;

    @JsonProperty("cart_item_count")
    private Integer cartItemCount;

    @JsonProperty("cart_quantity_total")
    private Integer cartQuantityTotal;

    @JsonProperty("cart_items")
    private String cartItems;

    @JsonProperty("cart_subtotal")
    private BigDecimal cartSubtotal;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonProperty("bonus_used_amount")
    private BigDecimal bonusUsedAmount;

    @JsonProperty("fees_amount")
    private BigDecimal feesAmount;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

    @JsonProperty("cart_total")
    private BigDecimal cartTotal;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("currency")
    private String currency;

    // Payment
    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_provider_id")
    private String paymentProviderId;

    @JsonProperty("payment_provider_name")
    private String paymentProviderName;

    @JsonProperty("is_saved_method")
    private Boolean isSavedMethod;

    @JsonProperty("funds_source")
    private String fundsSource;

    @JsonProperty("payment_attempt_number")
    private Integer paymentAttemptNumber;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("failure_reason")
    private String failureReason;

    @JsonProperty("failure_code")
    private String failureCode;

    @JsonProperty("failure_message")
    private String failureMessage;

    // Order
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("ticket_ids")
    private String ticketIds;

    @JsonProperty("order_total")
    private BigDecimal orderTotal;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("is_first_purchase")
    private Boolean isFirstPurchase;

    // Context
    @JsonProperty("device")
    private String device;

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("app_version")
    private String appVersion;

    @JsonProperty("page_url")
    private String pageUrl;

    @JsonProperty("referrer")
    private String referrer;

    @JsonProperty("utm_source")
    private String utmSource;

    @JsonProperty("utm_medium")
    private String utmMedium;

    @JsonProperty("utm_campaign")
    private String utmCampaign;

    @JsonProperty("campaign_id")
    private String campaignId;

    @JsonProperty("affiliate_id")
    private String affiliateId;

    @JsonProperty("entry_point")
    private String entryPoint;

    @JsonProperty("is_resumed")
    private Boolean isResumed;

    // Set by OptikPI on derived events
    @JsonProperty("abandoned_at_stage")
    private String abandonedAtStage;

    @JsonProperty("inactivity_sec")
    private Integer inactivitySec;

    public PurchaseFunnelEvent() {}

    /**
     * Creates an event with the required fields. {@code event_category} is required and is not defaulted:
     * set it to {@link #EVENT_CATEGORY}.
     */
    public PurchaseFunnelEvent(String accountId, String workspaceId, String userId, String eventCategory,
                               String eventName, String eventId, String eventTime, String funnelSessionId,
                               String funnelStage) {
        this.accountId = accountId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.eventCategory = eventCategory;
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.funnelSessionId = funnelSessionId;
        this.funnelStage = funnelStage;
    }

    /**
     * Validates the purchase funnel event data.
     * @return Validation result with isValid boolean and errors list
     */
    public ValidationResult validate() {
        return validate(System.currentTimeMillis());
    }

    /**
     * Validates the purchase funnel event data.
     * @param nowMs current time in milliseconds since the epoch, for the event_time window (tests)
     * @return Validation result with isValid boolean and errors list
     */
    public ValidationResult validate(long nowMs) {
        List<String> errors = new ArrayList<>();

        String[][] required = {
            {"account_id", accountId}, {"workspace_id", workspaceId}, {"user_id", userId},
            {"event_category", eventCategory}, {"event_name", eventName}, {"event_id", eventId},
            {"event_time", eventTime}, {"funnel_session_id", funnelSessionId}, {"funnel_stage", funnelStage}
        };
        for (String[] field : required) {
            if (field[1] == null || field[1].isEmpty()) {
                errors.add(field[0] + " is required");
            }
        }

        if (eventCategory != null && !eventCategory.isEmpty() && !EVENT_CATEGORY.equals(eventCategory)) {
            errors.add("event_category must be \"" + EVENT_CATEGORY + "\" for purchase funnel events");
        }
        if (funnelStage != null && !funnelStage.isEmpty() && !FUNNEL_STAGES.contains(funnelStage)) {
            errors.add("funnel_stage must be one of: " + String.join(", ", FUNNEL_STAGES));
        }
        if (failureReason != null && !FAILURE_REASONS.contains(failureReason)) {
            errors.add("failure_reason must be one of: " + String.join(", ", FAILURE_REASONS));
        }
        if (currency != null && !CURRENCY_PATTERN.matcher(currency).matches()) {
            errors.add("currency must be a valid 3-letter ISO currency code");
        }

        Object[][] amounts = {
            {"jackpot_amount", jackpotAmount}, {"unit_price", unitPrice}, {"item_amount", itemAmount},
            {"cart_subtotal", cartSubtotal}, {"discount_amount", discountAmount},
            {"bonus_used_amount", bonusUsedAmount}, {"fees_amount", feesAmount}, {"tax_amount", taxAmount},
            {"cart_total", cartTotal}, {"order_total", orderTotal}
        };
        for (Object[] amount : amounts) {
            if (amount[1] != null && !fitsMoney((BigDecimal) amount[1])) {
                errors.add(amount[0] + MONEY_ERROR);
            }
        }

        String[][] dateTimes = {{"event_time", eventTime}, {"draw_date", drawDate}, {"draw_close_time", drawCloseTime}};
        for (String[] field : dateTimes) {
            // An empty event_time already has its required error; an empty optional date-time is not a date-time.
            boolean reported = field[1] == null || (field[0].equals("event_time") && field[1].isEmpty());
            if (!reported && parseDateTimeMs(field[1]) == null) {
                errors.add(field[0] + " must be an ISO 8601 date-time with seconds and a zone (YYYY-MM-DDTHH:mm:ss[.fff]Z or +hh:mm)");
            }
        }

        if (eventTime != null) {
            Long eventMs = parseDateTimeMs(eventTime);
            if (eventMs != null && (eventMs < nowMs - TEN_YEARS_MS || eventMs > nowMs + ONE_YEAR_MS)) {
                errors.add("event_time must be within the last 10 years and no more than 1 year in the future");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates a raw JSON record, as it would be sent: the JSON kind of every field, then {@link #validate()}.
     * Unknown extra fields are accepted.
     */
    public static ValidationResult validateJson(JsonNode record) {
        return validateJson(record, System.currentTimeMillis());
    }

    /** {@link #validateJson(JsonNode)} with an explicit current time in milliseconds (tests). */
    public static ValidationResult validateJson(JsonNode record, long nowMs) {
        List<String> errors = new ArrayList<>();
        if (record == null || !record.isObject()) {
            errors.add("record must be a JSON object");
            return new ValidationResult(false, errors);
        }
        ObjectNode normalized = ((ObjectNode) record).deepCopy();
        for (Map.Entry<String, Kind> field : FIELD_KINDS.entrySet()) {
            JsonNode value = record.get(field.getKey());
            if (value == null || value.isNull()) {
                continue; // a missing required field is reported by validate()
            }
            String error = kindError(field.getKey(), value, field.getValue());
            if (error == null && field.getValue() == Kind.MONEY) {
                BigDecimal amount = jsonMoney(value);
                if (amount == null) {
                    error = field.getKey() + MONEY_ERROR;
                } else {
                    // Bind the decimal the JSON number stands for, not the binary double's expansion.
                    normalized.set(field.getKey(), DecimalNode.valueOf(amount));
                }
            }
            if (error != null) {
                errors.add(error);
            }
        }
        if (!errors.isEmpty()) {
            return new ValidationResult(false, errors);
        }
        PurchaseFunnelEvent event;
        try {
            event = MAPPER.treeToValue(normalized, PurchaseFunnelEvent.class);
        } catch (Exception e) {
            errors.add("record could not be read: " + e.getMessage());
            return new ValidationResult(false, errors);
        }
        return event.validate(nowMs);
    }

    private static final String MONEY_ERROR =
        " must fit decimal(15,2): below 10^13 with at most 2 decimal places";

    /** True when the amount fits decimal(15,2): absolute value below 10^13 and at most 2 decimal places. */
    private static boolean fitsMoney(BigDecimal amount) {
        return amount.abs().compareTo(MONEY_LIMIT) < 0 && amount.stripTrailingZeros().scale() <= 2;
    }

    /**
     * The decimal a JSON number stands for when it fits decimal(15,2), else null. A binary double fits when its
     * shortest round-trip decimal form has at most 2 decimal places, which is exactly when rounding its exact value
     * to 2 places gives the same double back (2.995 and 1e-7 do not fit). This avoids Double.toString, which is
     * not always the shortest form before Java 19.
     */
    private static BigDecimal jsonMoney(JsonNode value) {
        BigDecimal amount;
        if (value.isDouble() || value.isFloat()) {
            double d = value.doubleValue();
            amount = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN);
            if (amount.doubleValue() != d) {
                return null;
            }
        } else {
            amount = value.decimalValue();
        }
        return fitsMoney(amount) ? amount : null;
    }

    private static String kindError(String field, JsonNode value, Kind kind) {
        switch (kind) {
            case STRING:
                return value.isTextual() ? null : field + " must be a string";
            case MONEY:
                return value.isNumber() ? null : field + " must be a finite number";
            case COUNT:
                // Counts are stored as 32-bit integers. 2.0 is an integer in JSON, as for the API. A larger value
                // is an error here, before binding, rather than an overflow.
                boolean fits = value.isIntegralNumber()
                    ? value.canConvertToInt()
                    : value.isFloatingPointNumber() && value.doubleValue() == Math.rint(value.doubleValue())
                        && value.doubleValue() >= Integer.MIN_VALUE && value.doubleValue() <= Integer.MAX_VALUE;
                return fits ? null
                    : field + " must be an integer between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE;
            case BOOLEAN:
                return value.isBoolean() ? null : field + " must be true or false";
            case DATETIME:
                return value.isTextual() ? null : field + " must be an ISO 8601 date-time";
            default:
                return null;
        }
    }

    /**
     * Milliseconds since the epoch for an ISO 8601 date-time string with seconds and a zone, or null when it is
     * not one. Seconds and a zone are required: a date-time without them is lost downstream.
     */
    static Long parseDateTimeMs(String value) {
        Matcher m = DATETIME_PATTERN.matcher(value);
        if (!m.matches()) {
            return null;
        }
        long seconds;
        try {
            LocalDateTime local = LocalDateTime.of(
                Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)),
                Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)),
                Integer.parseInt(m.group(6)));
            if (local.getYear() < 1) {
                return null;
            }
            seconds = local.toEpochSecond(ZoneOffset.UTC);
        } catch (DateTimeException e) {
            return null;
        }
        String zone = m.group(8);
        if (!"Z".equals(zone)) {
            int offsetHours = Integer.parseInt(zone.substring(1, 3));
            int offsetMinutes = Integer.parseInt(zone.substring(4, 6));
            if (offsetHours > 18 || offsetMinutes > 59) {
                return null;
            }
            int sign = zone.charAt(0) == '-' ? -1 : 1;
            seconds -= sign * (offsetHours * 60L + offsetMinutes) * 60L;
        }
        return seconds * 1000;
    }

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getEventCategory() { return eventCategory; }
    public void setEventCategory(String eventCategory) { this.eventCategory = eventCategory; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getFunnelSessionId() { return funnelSessionId; }
    public void setFunnelSessionId(String funnelSessionId) { this.funnelSessionId = funnelSessionId; }

    public String getFunnelStage() { return funnelStage; }
    public void setFunnelStage(String funnelStage) { this.funnelStage = funnelStage; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public String getDrawId() { return drawId; }
    public void setDrawId(String drawId) { this.drawId = drawId; }

    public String getDrawNumber() { return drawNumber; }
    public void setDrawNumber(String drawNumber) { this.drawNumber = drawNumber; }

    public String getDrawDate() { return drawDate; }
    public void setDrawDate(String drawDate) { this.drawDate = drawDate; }

    public String getDrawCloseTime() { return drawCloseTime; }
    public void setDrawCloseTime(String drawCloseTime) { this.drawCloseTime = drawCloseTime; }

    public BigDecimal getJackpotAmount() { return jackpotAmount; }
    public void setJackpotAmount(BigDecimal jackpotAmount) { this.jackpotAmount = jackpotAmount; }

    public String getSelection() { return selection; }
    public void setSelection(String selection) { this.selection = selection; }

    public String getSelectionMethod() { return selectionMethod; }
    public void setSelectionMethod(String selectionMethod) { this.selectionMethod = selectionMethod; }

    public Integer getLinesCount() { return linesCount; }
    public void setLinesCount(Integer linesCount) { this.linesCount = linesCount; }

    public Integer getMultiDrawCount() { return multiDrawCount; }
    public void setMultiDrawCount(Integer multiDrawCount) { this.multiDrawCount = multiDrawCount; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(Integer itemQuantity) { this.itemQuantity = itemQuantity; }

    public BigDecimal getItemAmount() { return itemAmount; }
    public void setItemAmount(BigDecimal itemAmount) { this.itemAmount = itemAmount; }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public Integer getCartItemCount() { return cartItemCount; }
    public void setCartItemCount(Integer cartItemCount) { this.cartItemCount = cartItemCount; }

    public Integer getCartQuantityTotal() { return cartQuantityTotal; }
    public void setCartQuantityTotal(Integer cartQuantityTotal) { this.cartQuantityTotal = cartQuantityTotal; }

    public String getCartItems() { return cartItems; }
    public void setCartItems(String cartItems) { this.cartItems = cartItems; }

    public BigDecimal getCartSubtotal() { return cartSubtotal; }
    public void setCartSubtotal(BigDecimal cartSubtotal) { this.cartSubtotal = cartSubtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getBonusUsedAmount() { return bonusUsedAmount; }
    public void setBonusUsedAmount(BigDecimal bonusUsedAmount) { this.bonusUsedAmount = bonusUsedAmount; }

    public BigDecimal getFeesAmount() { return feesAmount; }
    public void setFeesAmount(BigDecimal feesAmount) { this.feesAmount = feesAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getCartTotal() { return cartTotal; }
    public void setCartTotal(BigDecimal cartTotal) { this.cartTotal = cartTotal; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentProviderId() { return paymentProviderId; }
    public void setPaymentProviderId(String paymentProviderId) { this.paymentProviderId = paymentProviderId; }

    public String getPaymentProviderName() { return paymentProviderName; }
    public void setPaymentProviderName(String paymentProviderName) { this.paymentProviderName = paymentProviderName; }

    public Boolean getIsSavedMethod() { return isSavedMethod; }
    public void setIsSavedMethod(Boolean isSavedMethod) { this.isSavedMethod = isSavedMethod; }

    public String getFundsSource() { return fundsSource; }
    public void setFundsSource(String fundsSource) { this.fundsSource = fundsSource; }

    public Integer getPaymentAttemptNumber() { return paymentAttemptNumber; }
    public void setPaymentAttemptNumber(Integer paymentAttemptNumber) { this.paymentAttemptNumber = paymentAttemptNumber; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getFailureCode() { return failureCode; }
    public void setFailureCode(String failureCode) { this.failureCode = failureCode; }

    public String getFailureMessage() { return failureMessage; }
    public void setFailureMessage(String failureMessage) { this.failureMessage = failureMessage; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTicketIds() { return ticketIds; }
    public void setTicketIds(String ticketIds) { this.ticketIds = ticketIds; }

    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Boolean getIsFirstPurchase() { return isFirstPurchase; }
    public void setIsFirstPurchase(Boolean isFirstPurchase) { this.isFirstPurchase = isFirstPurchase; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }

    public String getUtmSource() { return utmSource; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }

    public String getUtmMedium() { return utmMedium; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }

    public String getUtmCampaign() { return utmCampaign; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }

    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }

    public String getAffiliateId() { return affiliateId; }
    public void setAffiliateId(String affiliateId) { this.affiliateId = affiliateId; }

    public String getEntryPoint() { return entryPoint; }
    public void setEntryPoint(String entryPoint) { this.entryPoint = entryPoint; }

    public Boolean getIsResumed() { return isResumed; }
    public void setIsResumed(Boolean isResumed) { this.isResumed = isResumed; }

    public String getAbandonedAtStage() { return abandonedAtStage; }
    public void setAbandonedAtStage(String abandonedAtStage) { this.abandonedAtStage = abandonedAtStage; }

    public Integer getInactivitySec() { return inactivitySec; }
    public void setInactivitySec(Integer inactivitySec) { this.inactivitySec = inactivitySec; }
}
