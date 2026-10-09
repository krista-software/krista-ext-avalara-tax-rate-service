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
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.integration.AvaTaxClient;
import com.google.gson.JsonObject;

import javax.inject.Inject;
import java.lang.management.ManagementFactory;

/** Setup area — the standard Test Connection + Health Check requests. */
@Domain(id = "catEntryDomain_4d4311d0-88d9-4042-b8e9-7f78187b1ab5",
        name = "Tax Rate Service",
        ecosystemId = "catEntryEcosystem_c30d7d1e-2bf1-438a-b049-522f113db9fc",
        ecosystemName = "Accounting",
        ecosystemVersion = "3ba47083-89d3-49d5-8b0f-41c059b5cfa5")
public class SetupArea extends AbstractArea {

    @Inject
    public SetupArea(AvaTaxClient client, TelemetryHelper telemetry, Invoker invoker) {
        super(client, telemetry, invoker);
    }

    @CatalogRequest(
            id = "localDomainRequest_a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
            name = "Test Connection",
            description = "Validate connectivity and authentication against Avalara AvaTax.",
            area = "Setup",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Success", required = false, attributes = {}, options = {})
    @Field.Text(name = "Message", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {})
    public ExtensionResponse testConnection() {
        return audited("TestConnection", () -> {
            JsonObject ping = client.get("/api/v2/utilities/ping");
            boolean authenticated = Boolean.TRUE.equals(Json.bool(ping, "authenticated"));
            var out = out();
            if (!authenticated) {
                out.put("Success", false);
                out.put("Message", "AvaTax did not accept the configured credentials. Verify the Account ID and License Key on the Setup tab.");
                return ExtensionResponses.ok(out);
            }
            out.put("Success", true);
            out.put("Message", "Connected to AvaTax (server version " + Json.str(ping, "version") + ").");
            return ExtensionResponses.ok(out);
        });
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
            name = "Health Check",
            description = "Report extension JVM health and AvaTax reachability.",
            area = "Setup",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Is Healthy", required = false, attributes = {}, options = {})
    @Field(name = "Heap Used MB", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    @Field.Text(name = "Details", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {})
    public ExtensionResponse healthCheck() {
        return audited("HealthCheck", () -> {
            long heapUsedMb = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1024 * 1024);
            boolean healthy;
            String details;
            try {
                JsonObject ping = client.get("/api/v2/utilities/ping");
                healthy = Boolean.TRUE.equals(Json.bool(ping, "authenticated"));
                details = healthy ? "OK" : "AvaTax reachable but credentials were not accepted.";
            } catch (Exception e) {
                healthy = false;
                details = "AvaTax unreachable: " + e.getMessage();
            }
            var out = out();
            out.put("Is Healthy", healthy);
            out.put("Heap Used MB", (double) heapUsedMb);
            out.put("Details", details);
            return ExtensionResponses.ok(out);
        });
    }
}
