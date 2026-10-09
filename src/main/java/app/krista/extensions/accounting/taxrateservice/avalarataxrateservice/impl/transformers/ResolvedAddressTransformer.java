package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.impl.transformers;

import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.entities.ResolvedAddress;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stateless DTO(JSON)→{@link ResolvedAddress} transformer for the AvaTax
 * {@code POST /api/v2/addresses/resolve} response shape: a top-level {@code address} object,
 * {@code coordinates}, {@code resolutionQuality}, and the {@code taxAuthorities}/{@code messages} arrays.
 */
public final class ResolvedAddressTransformer {

    private ResolvedAddressTransformer() {
    }

    public static ResolvedAddress transform(JsonObject json) {
        if (json == null) {
            return null;
        }
        ResolvedAddress e = new ResolvedAddress();
        JsonObject address = Json.object(json, "address");
        if (address != null) {
            e.line1 = Json.str(address, "line1");
            e.city = Json.str(address, "city");
            e.region = Json.str(address, "region");
            e.country = Json.str(address, "country");
            e.postalCode = Json.str(address, "postalCode");
        }
        JsonObject coordinates = Json.object(json, "coordinates");
        if (coordinates != null) {
            e.latitude = Json.dbl(coordinates, "latitude");
            e.longitude = Json.dbl(coordinates, "longitude");
        }
        e.resolutionQuality = Json.str(json, "resolutionQuality");
        e.taxAuthorities = taxAuthorities(Json.array(json, "taxAuthorities"));
        e.messages = messages(Json.array(json, "messages"));
        return e;
    }

    private static List<Map<String, Object>> taxAuthorities(JsonArray array) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (JsonElement el : array) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("AvalaraId", Json.str(o, "avalaraId"));
            row.put("JurisdictionName", Json.str(o, "jurisdictionName"));
            row.put("JurisdictionType", Json.str(o, "jurisdictionType"));
            row.put("SignatureCode", Json.str(o, "signatureCode"));
            out.add(row);
        }
        return out;
    }

    private static List<Map<String, Object>> messages(JsonArray array) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (JsonElement el : array) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Summary", Json.str(o, "summary"));
            row.put("Details", Json.str(o, "details"));
            row.put("RefersTo", Json.str(o, "refersTo"));
            row.put("Severity", Json.str(o, "severity"));
            row.put("Source", Json.str(o, "source"));
            out.add(row);
        }
        return out;
    }
}
