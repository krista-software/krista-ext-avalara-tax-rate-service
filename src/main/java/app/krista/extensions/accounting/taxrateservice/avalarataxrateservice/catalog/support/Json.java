package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Reusable null-safe JSON accessors for the DTO&rarr;entity transformers. Every read tolerates a
 * missing key or JSON null and returns null.
 */
public final class Json {

    private Json() {
    }

    public static boolean has(JsonObject j, String k) {
        return j != null && j.has(k) && !j.get(k).isJsonNull();
    }

    public static String str(JsonObject j, String k) {
        return has(j, k) ? asString(j.get(k)) : null;
    }

    public static Double dbl(JsonObject j, String k) {
        try {
            return has(j, k) ? j.get(k).getAsDouble() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean bool(JsonObject j, String k) {
        try {
            return has(j, k) ? j.get(k).getAsBoolean() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** AvaTax dates are plain ISO-8601 date/date-time strings; Krista {@code @Field.Date} wants epoch millis. */
    public static Long epochMillisFromDate(JsonObject j, String k) {
        String raw = str(j, k);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            if (raw.length() <= 10) {
                return java.time.LocalDate.parse(raw).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
            }
            return java.time.OffsetDateTime.parse(raw).toInstant().toEpochMilli();
        } catch (Exception e) {
            try {
                return java.time.LocalDateTime.parse(raw).atZone(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static JsonArray array(JsonObject j, String k) {
        return has(j, k) && j.get(k).isJsonArray() ? j.getAsJsonArray(k) : new JsonArray();
    }

    public static JsonObject object(JsonObject j, String k) {
        return has(j, k) && j.get(k).isJsonObject() ? j.getAsJsonObject(k) : null;
    }

    private static String asString(JsonElement el) {
        try {
            return el.getAsString();
        } catch (Exception e) {
            return el.toString();
        }
    }
}
