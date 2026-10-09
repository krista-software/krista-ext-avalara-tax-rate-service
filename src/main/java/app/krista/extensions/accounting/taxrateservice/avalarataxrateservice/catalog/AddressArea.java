package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support.AbstractArea;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support.ExtensionResponses;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support.Json;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support.TelemetryHelper;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.impl.transformers.AddressJurisdictionMatchTransformer;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.impl.transformers.ResolvedAddressTransformer;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.integration.AvaTaxClient;
import com.google.gson.JsonObject;

import javax.inject.Inject;

/** Address catalog requests: resolve an address, and list the taxing jurisdictions near it. */
@Domain(id = "catEntryDomain_4d4311d0-88d9-4042-b8e9-7f78187b1ab5",
        name = "Tax Rate Service",
        ecosystemId = "catEntryEcosystem_c30d7d1e-2bf1-438a-b049-522f113db9fc",
        ecosystemName = "Accounting",
        ecosystemVersion = "3ba47083-89d3-49d5-8b0f-41c059b5cfa5")
public class AddressArea extends AbstractArea {

    @Inject
    public AddressArea(AvaTaxClient client, TelemetryHelper telemetry, Invoker invoker) {
        super(client, telemetry, invoker);
    }

    @CatalogRequest(
            id = "localDomainRequest_c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
            name = "Resolve Address",
            description = "Resolves/validates a US or Canadian address against Avalara's address-validation system, returning the standardized address, its geocoordinates, a resolution-confidence rating, and the taxing authorities that govern it.",
            area = "Address",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Resolved Address", type = "Entity(Resolved Address)", required = false)
    public ExtensionResponse resolveAddress(
            @Field.Text(name = "Line 1", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String line1,
            @Field.Text(name = "Line 2", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String line2,
            @Field.Text(name = "Line 3", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String line3,
            @Field.Text(name = "City", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String city,
            @Field.Text(name = "Region", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String region,
            @Field.Text(name = "Postal Code", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String postalCode,
            @Field.Text(name = "Country", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String country,
            @Field.PickOne(name = "Text Case", values = {"Upper", "Lower", "Mixed"}, required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String textCase) {
        return audited("ResolveAddress", () -> {
            JsonObject body = new JsonObject();
            putStr(body, "line1", line1);
            putStr(body, "line2", line2);
            putStr(body, "line3", line3);
            putStr(body, "city", city);
            putStr(body, "region", region);
            putStr(body, "postalCode", postalCode);
            putStr(body, "country", country);
            putStr(body, "textCase", textCase);
            JsonObject data = client.post("/api/v2/addresses/resolve", body);
            var out = out();
            out.put("Resolved Address", ResolvedAddressTransformer.transform(data));
            return ExtensionResponses.ok(out);
        });
    }

    @CatalogRequest(
            id = "localDomainRequest_d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a",
            name = "List Jurisdictions By Address",
            description = "Returns every Avalara-supported taxing jurisdiction that applies to an address, including each jurisdiction's taxing authority type, tax rate, sales-tax rate, and use-tax rate.",
            area = "Address",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Address Jurisdictions", type = "[ Entity(Address Jurisdiction Match) ]", required = false)
    @Field(name = "Count", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse listJurisdictionsByAddress(
            @Field.Text(name = "Line 1", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String line1,
            @Field.Text(name = "Line 2", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String line2,
            @Field.Text(name = "Line 3", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String line3,
            @Field.Text(name = "City", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String city,
            @Field.Text(name = "Region", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String region,
            @Field.Text(name = "Postal Code", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String postalCode,
            @Field.Text(name = "Country", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) String country,
            @Field.Text(name = "Filter", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String filter,
            @Field(name = "Top", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double top,
            @Field(name = "Skip", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double skip,
            @Field.Text(name = "Order By", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String orderBy) {
        return audited("ListJurisdictionsByAddress", () -> {
            String query = query()
                    .add("line1", line1)
                    .add("line2", line2)
                    .add("line3", line3)
                    .add("city", city)
                    .add("region", region)
                    .add("postalCode", postalCode)
                    .add("country", country)
                    .add("$filter", filter)
                    .add("$top", top)
                    .add("$skip", skip)
                    .add("$orderBy", orderBy)
                    .build();
            JsonObject data = client.get("/api/v2/definitions/jurisdictionsnearaddress" + query);
            var items = Json.array(data, "value");
            var out = out();
            out.put("Address Jurisdictions", AddressJurisdictionMatchTransformer.transformList(items));
            out.put("Count", (double) items.size());
            return ExtensionResponses.ok(out);
        });
    }

    private static void putStr(JsonObject body, String key, String value) {
        if (value != null && !value.isBlank()) {
            body.addProperty(key, value);
        }
    }
}
