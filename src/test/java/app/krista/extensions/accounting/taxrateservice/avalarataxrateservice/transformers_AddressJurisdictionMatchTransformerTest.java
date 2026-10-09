package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice;

import static org.junit.jupiter.api.Assertions.*;

import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.entities.AddressJurisdictionMatch;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.impl.transformers.AddressJurisdictionMatchTransformer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import org.junit.jupiter.api.Test;

class transformers_AddressJurisdictionMatchTransformerTest {

    private static final String SAMPLE =
            "{\"description\":\"a test JO\",\"jurisdictions\":["
            + "{\"code\":\"075\",\"name\":\"LAWRENCE\",\"signatureCode\":\"string\",\"type\":\"Country\","
            + "\"rate\":7434.001262750061,\"salesRate\":157.10809959449844,\"region\":\"AR\",\"useRate\":3116.1549977000714,"
            + "\"city\":\"string\",\"county\":\"LAWRENCE\",\"country\":\"US\",\"taxAuthorityTypeId\":1930,"
            + "\"effectiveDate\":\"1999-09-01T00:00:00\",\"endDate\":\"9999-12-31T00:00:00\"}],"
            + "\"postalCode\":\"92618\",\"region\":\"CA\",\"line1\":\"5500 Irvine Center Dr\",\"city\":\"Irvine\","
            + "\"country\":\"US\",\"effectiveDate\":\"1975-09-07\",\"endDate\":\"1951-02-22\","
            + "\"boundaryLevel\":\"Address\",\"isDefault\":true}";

    @Test void nullInNullOut() {
        assertNull(AddressJurisdictionMatchTransformer.transform(null));
        assertTrue(AddressJurisdictionMatchTransformer.transformList(null).isEmpty());
    }

    @Test void mapsTopLevelAddressAndMetadataFields() {
        JsonObject j = JsonParser.parseString(SAMPLE).getAsJsonObject();
        AddressJurisdictionMatch e = AddressJurisdictionMatchTransformer.transform(j);
        assertEquals("5500 Irvine Center Dr", e.line1);
        assertEquals("Irvine", e.city);
        assertEquals("CA", e.region);
        assertEquals("92618", e.postalCode);
        assertEquals("US", e.country);
        assertEquals("a test JO", e.description);
        assertEquals("Address", e.boundaryLevel);
        assertEquals(Boolean.TRUE, e.isDefault);
        assertNotNull(e.effectiveDate);
        assertNotNull(e.endDate);
    }

    @Test void mapsJurisdictionsAsInlineRowsWithRateFields() {
        JsonObject j = JsonParser.parseString(SAMPLE).getAsJsonObject();
        AddressJurisdictionMatch e = AddressJurisdictionMatchTransformer.transform(j);
        assertEquals(1, e.jurisdictions.size());
        Map<String, Object> row = e.jurisdictions.get(0);
        assertEquals("075", row.get("Code"));
        assertEquals("LAWRENCE", row.get("Name"));
        assertEquals("Country", row.get("Type"));
        assertEquals("AR", row.get("Region"));
        assertEquals(7434.001262750061, (Double) row.get("Rate"));
        assertEquals(157.10809959449844, (Double) row.get("SalesRate"));
        assertEquals(3116.1549977000714, (Double) row.get("UseRate"));
        assertEquals(1930.0, (Double) row.get("TaxAuthorityTypeId"));
    }

    @Test void transformListSkipsNonObjectsAndNeverNull() {
        JsonArray arr = JsonParser.parseString("[" + SAMPLE + ", \"junk\", " + SAMPLE + "]").getAsJsonArray();
        assertEquals(2, AddressJurisdictionMatchTransformer.transformList(arr).size());
    }

    @Test void missingJurisdictionsArrayYieldsEmptyListNotNull() {
        JsonObject j = JsonParser.parseString("{\"line1\":\"123 Main St\"}").getAsJsonObject();
        AddressJurisdictionMatch e = AddressJurisdictionMatchTransformer.transform(j);
        assertNotNull(e.jurisdictions);
        assertTrue(e.jurisdictions.isEmpty());
    }
}
