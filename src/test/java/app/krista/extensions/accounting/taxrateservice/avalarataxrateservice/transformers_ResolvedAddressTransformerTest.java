package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice;

import static org.junit.jupiter.api.Assertions.*;

import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.entities.ResolvedAddress;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.impl.transformers.ResolvedAddressTransformer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import org.junit.jupiter.api.Test;

class transformers_ResolvedAddressTransformerTest {

    @Test void nullInNullOut() {
        assertNull(ResolvedAddressTransformer.transform(null));
    }

    @Test void mapsAddressCoordinatesAndResolutionQuality() {
        JsonObject j = JsonParser.parseString(
                "{\"address\":{\"line1\":\"2000 Main Street\",\"city\":\"Irvine\",\"region\":\"CA\",\"country\":\"US\",\"postalCode\":\"92614\"},"
                + "\"coordinates\":{\"latitude\":33.684884,\"longitude\":-117.851321},"
                + "\"resolutionQuality\":\"Intersection\",\"taxAuthorities\":[],\"messages\":[]}").getAsJsonObject();
        ResolvedAddress e = ResolvedAddressTransformer.transform(j);
        assertEquals("2000 Main Street", e.line1);
        assertEquals("Irvine", e.city);
        assertEquals("CA", e.region);
        assertEquals("US", e.country);
        assertEquals("92614", e.postalCode);
        assertEquals(33.684884, e.latitude);
        assertEquals(-117.851321, e.longitude);
        assertEquals("Intersection", e.resolutionQuality);
        assertTrue(e.taxAuthorities.isEmpty());
        assertTrue(e.messages.isEmpty());
    }

    @Test void mapsTaxAuthoritiesAndMessagesAsInlineRows() {
        JsonObject j = JsonParser.parseString(
                "{\"address\":{},\"coordinates\":{},\"resolutionQuality\":\"Intersection\","
                + "\"taxAuthorities\":[{\"avalaraId\":\"AGAM\",\"jurisdictionName\":\"CALIFORNIA\",\"jurisdictionType\":\"State\",\"signatureCode\":\"AGAM\"}],"
                + "\"messages\":[{\"summary\":\"The address is not deliverable.\",\"details\":\"detail\",\"refersTo\":\"address\",\"severity\":\"Error\",\"source\":\"Avalara.AvaTax.Services.Address\"}]}")
                .getAsJsonObject();
        ResolvedAddress e = ResolvedAddressTransformer.transform(j);
        Map<String, Object> authority = e.taxAuthorities.get(0);
        assertEquals("AGAM", authority.get("AvalaraId"));
        assertEquals("CALIFORNIA", authority.get("JurisdictionName"));
        assertEquals("State", authority.get("JurisdictionType"));
        assertEquals("AGAM", authority.get("SignatureCode"));

        Map<String, Object> message = e.messages.get(0);
        assertEquals("The address is not deliverable.", message.get("Summary"));
        assertEquals("address", message.get("RefersTo"));
        assertEquals("Error", message.get("Severity"));
    }

    @Test void missingArraysYieldEmptyListsNotNull() {
        JsonObject j = JsonParser.parseString("{\"address\":{},\"coordinates\":{}}").getAsJsonObject();
        ResolvedAddress e = ResolvedAddressTransformer.transform(j);
        assertNotNull(e.taxAuthorities);
        assertTrue(e.taxAuthorities.isEmpty());
        assertNotNull(e.messages);
        assertTrue(e.messages.isEmpty());
    }
}
