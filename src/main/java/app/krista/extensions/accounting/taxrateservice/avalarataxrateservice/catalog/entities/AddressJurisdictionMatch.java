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
 * One taxing-jurisdiction match for an address, for one effective-date range: the address AvaTax
 * matched against, and every taxing jurisdiction (with its tax authority type and rate/salesRate/
 * useRate) that applies to it.
 *
 * <p>{@code jurisdictions} is modeled as an inline multi-field list ({@code List<Map<String,Object>>}),
 * not a nested {@code @Entity} reference — a Krista {@code @Entity} class's own fields can never
 * reference another {@code @Entity}.
 */
@Searchable
@Domain(id = "catEntryDomain_4d4311d0-88d9-4042-b8e9-7f78187b1ab5",
        name = "Tax Rate Service",
        ecosystemId = "catEntryEcosystem_c30d7d1e-2bf1-438a-b049-522f113db9fc",
        ecosystemName = "Accounting",
        ecosystemVersion = "3ba47083-89d3-49d5-8b0f-41c059b5cfa5")
@Entity(name = "Address Jurisdiction Match", id = "localDomainEntity_2d8e4f6a-1b3c-4d5e-8f7a-9b0c1d2e3f4a", primaryKey = "Line 1",
        supportStore = false,
        description = "One taxing-jurisdiction match for an address: the address AvaTax matched against, and every taxing jurisdiction (authority type + rate/salesRate/useRate) that applies to it for that effective-date range.")
public class AddressJurisdictionMatch {

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
    @Field.Text(name = "Postal Code", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String postalCode;

    @Searchable
    @Field.Text(name = "Country", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public String country;

    @Field.Text(name = "Description", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {})
    public String description;

    @Searchable
    @Field.Text(name = "Boundary Level", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public String boundaryLevel;

    @Field.Boolean(name = "Is Default", required = false, attributes = {}, options = {})
    public Boolean isDefault;

    @Field.Date(name = "Effective Date", required = false, includeTimeOfDay = false,
            allowPast = true, allowToday = true, allowFuture = true,
            attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public Long effectiveDate;

    @Field.Date(name = "End Date", required = false, includeTimeOfDay = false,
            allowPast = true, allowToday = true, allowFuture = true,
            attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public Long endDate;

    @Field.Desc(name = "Jurisdictions",
            type = "[ { Code: Text, Name: Text, Type: Text, SignatureCode: Text, Region: Text, City: Text, County: Text, Country: Text, Rate: Number, SalesRate: Number, UseRate: Number, TaxAuthorityTypeId: Number, EffectiveDate: Date, EndDate: Date } ]",
            required = false)
    public List<Map<String, Object>> jurisdictions;

    /** Insertion-ordered field map keyed by the @Field display names. */
    public Map<String, Object> toFields() {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("Line 1", line1);
        f.put("City", city);
        f.put("Region", region);
        f.put("Postal Code", postalCode);
        f.put("Country", country);
        f.put("Description", description);
        f.put("Boundary Level", boundaryLevel);
        f.put("Is Default", isDefault);
        f.put("Effective Date", effectiveDate);
        f.put("End Date", endDate);
        f.put("Jurisdictions", jurisdictions);
        return f;
    }
}
