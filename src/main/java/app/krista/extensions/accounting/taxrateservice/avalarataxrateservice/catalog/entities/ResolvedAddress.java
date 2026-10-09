package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.entities;

import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.Searchable;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The result of resolving/validating a physical address against Avalara's address-validation system:
 * the standardized address, its geocoordinates, a resolution-confidence rating, the taxing authorities
 * that govern it, and any validation messages AvaTax returned.
 *
 * <p>{@code taxAuthorities} and {@code messages} are modeled as inline multi-field lists
 * ({@code List<Map<String,Object>>}), not nested {@code @Entity} references — a Krista {@code @Entity}
 * class's own fields can never reference another {@code @Entity}.
 */
@Searchable
@Domain(id = "catEntryDomain_4d4311d0-88d9-4042-b8e9-7f78187b1ab5",
        name = "Tax Rate Service",
        ecosystemId = "catEntryEcosystem_c30d7d1e-2bf1-438a-b049-522f113db9fc",
        ecosystemName = "Accounting",
        ecosystemVersion = "3ba47083-89d3-49d5-8b0f-41c059b5cfa5")
@Entity(name = "Resolved Address", id = "localDomainEntity_7f3b2c1a-6e4d-5a2b-9c3e-1a2b3c4d5e6f", primaryKey = "Line 1",
        supportStore = false,
        description = "The result of resolving a physical address against Avalara's address-validation system: the standardized address, its geocoordinates, a resolution-confidence rating, and the taxing authorities that govern it.")
public class ResolvedAddress {

    @Searchable
    @ToString
    @Field.Text(name = "Line 1", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public String line1;

    @Searchable
    @Field.Text(name = "City", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public String city;

    @Searchable
    @Field.Text(name = "Region", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String region;

    @Searchable
    @Field.Text(name = "Country", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String country;

    @Searchable
    @Field.Text(name = "Postal Code", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String postalCode;

    @Field(name = "Latitude", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public Double latitude;

    @Field(name = "Longitude", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public Double longitude;

    @Searchable
    @Field.Text(name = "Resolution Quality", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public String resolutionQuality;

    @Field.Desc(name = "Tax Authorities",
            type = "[ { AvalaraId: Text, JurisdictionName: Text, JurisdictionType: Text, SignatureCode: Text } ]",
            required = false)
    public List<Map<String, Object>> taxAuthorities;

    @Field.Desc(name = "Messages",
            type = "[ { Summary: Text, Details: Text, RefersTo: Text, Severity: Text, Source: Text } ]",
            required = false)
    public List<Map<String, Object>> messages;

    /** Insertion-ordered field map keyed by the @Field display names. */
    public Map<String, Object> toFields() {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("Line 1", line1);
        f.put("City", city);
        f.put("Region", region);
        f.put("Country", country);
        f.put("Postal Code", postalCode);
        f.put("Latitude", latitude);
        f.put("Longitude", longitude);
        f.put("Resolution Quality", resolutionQuality);
        f.put("Tax Authorities", taxAuthorities);
        f.put("Messages", messages);
        return f;
    }
}
