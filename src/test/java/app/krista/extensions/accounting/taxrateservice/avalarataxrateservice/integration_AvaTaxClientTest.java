package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice;

import static org.junit.jupiter.api.Assertions.*;

import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.integration.AvaTaxClient;
import com.google.gson.JsonObject;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class integration_AvaTaxClientTest {

    private MockWebServer server;
    private AvaTaxClient client;

    @BeforeEach void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        String base = server.url("/").toString().replaceAll("/$", "");
        // AvalaraTaxRateServiceAttributes.getBaseUrl() is normally a fixed Sandbox constant; override it
        // here so the client talks to the MockWebServer instead of the real AvaTax host.
        AvalaraTaxRateServiceAttributes attrs = new AvalaraTaxRateServiceAttributes(Map.of(
                AvalaraTaxRateServiceAttributes.ACCOUNT_ID, "acct123",
                AvalaraTaxRateServiceAttributes.LICENSE_KEY, "secretKey")) {
            @Override
            public String getBaseUrl() {
                return base;
            }
        };
        client = new AvaTaxClient(attrs);
    }

    @AfterEach void tearDown() throws Exception {
        server.shutdown();
    }

    @Test void getReturnsParsedJsonObject() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody("{\"version\":\"1.0.0.0\",\"authenticated\":true}"));
        JsonObject o = client.get("/api/v2/utilities/ping");
        assertTrue(o.get("authenticated").getAsBoolean());
    }

    @Test void requestCarriesBasicAuthHeader() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.get("/api/v2/utilities/ping");
        RecordedRequest recorded = server.takeRequest();
        String expected = okhttp3.Credentials.basic("acct123", "secretKey");
        assertEquals(expected, recorded.getHeader("Authorization"));
    }

    @Test void postSendsJsonBodyAndReturnsParsedObject() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200)
                .setBody("{\"address\":{\"line1\":\"2000 Main Street\"},\"resolutionQuality\":\"Intersection\"}"));
        JsonObject body = new JsonObject();
        body.addProperty("line1", "2000 Main Street");
        JsonObject o = client.post("/api/v2/addresses/resolve", body);
        assertEquals("Intersection", o.get("resolutionQuality").getAsString());
        RecordedRequest recorded = server.takeRequest();
        assertTrue(recorded.getBody().readUtf8().contains("2000 Main Street"));
    }

    @Test void unauthorizedMapsToSecurityException() {
        server.enqueue(new MockResponse().setResponseCode(401).setBody("{\"error\":{\"message\":\"bad creds\"}}"));
        assertThrows(SecurityException.class, () -> client.get("/api/v2/utilities/ping"));
    }

    @Test void badRequestMapsToIllegalArgument() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody("{\"error\":{\"message\":\"no address fields\"}}"));
        assertThrows(IllegalArgumentException.class, () -> client.get("/api/v2/addresses/resolve"));
    }

    @Test void rateLimitMapsToIOException() {
        server.enqueue(new MockResponse().setResponseCode(429));
        server.enqueue(new MockResponse().setResponseCode(429));
        server.enqueue(new MockResponse().setResponseCode(429));
        assertThrows(java.io.IOException.class, () -> client.get("/api/v2/utilities/ping"));
    }
}
