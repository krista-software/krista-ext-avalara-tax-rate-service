package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.impl.transformers;

import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.entities.AddressJurisdictionMatch;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stateless DTO(JSON)→{@link AddressJurisdictionMatch} transformer for the AvaTax
 * {@code GET /api/v2/definitions/jurisdictionsnearaddress} response shape: a {@code FetchResult} whose
 * {@code value} array holds one {@code JurisdictionOverrideModel} per effective-date range, each nesting
 * a {@code jurisdictions} array (code/name/type/rate/salesRate/useRate/taxAuthorityTypeId, ...).
 */
public final class AddressJurisdictionMatchTransformer {

    private AddressJurisdictionMatchTransformer() {
    }

    public static AddressJurisdictionMatch transform(JsonObject json) {
        if (json == null) {
            return null;
        }
        AddressJurisdictionMatch e = new AddressJurisdictionMatch();
        e.line1 = Json.str(json, "line1");
        e.city = Json.str(json, "city");
        e.region = Json.str(json, "region");
        e.postalCode = Json.str(json, "postalCode");
        e.country = Json.str(json, "country");
        e.description = Json.str(json, "description");
        e.boundaryLevel = Json.str(json, "boundaryLevel");
        e.isDefault = Json.bool(json, "isDefault");
        e.effectiveDate = Json.epochMillisFromDate(json, "effectiveDate");
        e.endDate = Json.epochMillisFromDate(json, "endDate");
        e.jurisdictions = jurisdictions(Json.array(json, "jurisdictions"));
        return e;
    }

    public static List<AddressJurisdictionMatch> transformList(JsonArray array) {
        List<AddressJurisdictionMatch> out = new ArrayList<>();
        if (array == null) {
            return out;
        }
        for (JsonElement el : array) {
            if (el.isJsonObject()) {
                AddressJurisdictionMatch t = transform(el.getAsJsonObject());
                if (t != null) {
                    out.add(t);
                }
            }
        }
        return out;
    }

    private static List<Map<String, Object>> jurisdictions(JsonArray array) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (JsonElement el : array) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Code", Json.str(o, "code"));
            row.put("Name", Json.str(o, "name"));
            row.put("Type", Json.str(o, "type"));
            row.put("SignatureCode", Json.str(o, "signatureCode"));
            row.put("Region", Json.str(o, "region"));
            row.put("City", Json.str(o, "city"));
            row.put("County", Json.str(o, "county"));
            row.put("Country", Json.str(o, "country"));
            row.put("Rate", Json.dbl(o, "rate"));
            row.put("SalesRate", Json.dbl(o, "salesRate"));
            row.put("UseRate", Json.dbl(o, "useRate"));
            row.put("TaxAuthorityTypeId", Json.dbl(o, "taxAuthorityTypeId"));
            row.put("EffectiveDate", Json.epochMillisFromDate(o, "effectiveDate"));
            row.put("EndDate", Json.epochMillisFromDate(o, "endDate"));
            out.add(row);
        }
        return out;
    }
}
