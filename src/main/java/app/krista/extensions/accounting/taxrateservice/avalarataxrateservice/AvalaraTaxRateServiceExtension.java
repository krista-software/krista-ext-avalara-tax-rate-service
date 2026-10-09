package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice;

import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Extension;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.InvokerRequest;
import app.krista.extension.impl.anno.Java;
import app.krista.extension.impl.anno.StaticResource;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.integration.AvaTaxClient;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

/**
 * Avalara Tax Rate Service extension entry point — resolves a physical address and looks up the
 * taxing jurisdictions (authority + rate) that apply to it via the Avalara AvaTax REST API.
 *
 * Auth is HTTP Basic (Krista pattern A, two secured fields): the admin supplies an Avalara
 * Account ID + License Key on the Setup tab. There is no login/OAuth flow. The target host is fixed
 * to the AvaTax Sandbox ({@link AvalaraTaxRateServiceAttributes#BASE_URL}).
 */
@Field.Text(value = AvalaraTaxRateServiceAttributes.ACCOUNT_ID, isSecured = true)
@Field.Text(value = AvalaraTaxRateServiceAttributes.LICENSE_KEY, isSecured = true)
@Extension(name = "Avalara Tax Rate Service", version = "1.0.0",
        description = "Resolves a physical address and retrieves the taxing jurisdictions (authority and rates) that apply to it via Avalara AvaTax.")
@StaticResource(path = "docs", file = "docs")
@Java(version = Java.Version.JAVA_21)
public final class AvalaraTaxRateServiceExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvalaraTaxRateServiceExtension.class);

    private final Invoker invoker;
    private final AvalaraTaxRateServiceAttributes attributes;
    private final AvaTaxClient client;

    @Inject
    public AvalaraTaxRateServiceExtension(Invoker invoker, AvalaraTaxRateServiceAttributes attributes, AvaTaxClient client) {
        this.invoker = invoker;
        this.attributes = attributes;
        this.client = client;
    }

    /** Setup-tab tabs. */
    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }

    /** Validate configuration before it is saved. Throw to reject. */
    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> connectionAttributes) throws IOException {
        AvalaraTaxRateServiceAttributes candidate = AvalaraTaxRateServiceAttributes.create(invoker, connectionAttributes);
        if (candidate.getAccountId() == null || candidate.getAccountId().isBlank()) {
            throw new IllegalArgumentException("Account ID is required.");
        }
        if (candidate.getLicenseKey() == null || candidate.getLicenseKey().isBlank()) {
            throw new IllegalArgumentException("License Key is required.");
        }
    }

    /** Test-Connection button — calls AvaTax's ping utility and confirms the credentials authenticate. */
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() throws IOException {
        JsonObject ping = client.get("/api/v2/utilities/ping");
        boolean authenticated = ping.has("authenticated") && !ping.get("authenticated").isJsonNull()
                && ping.get("authenticated").getAsBoolean();
        if (!authenticated) {
            throw new SecurityException(
                    "AvaTax did not accept the configured credentials. Verify the Account ID and License Key on the Setup tab.");
        }
        LOGGER.info("Avalara Tax Rate Service test connection succeeded for invoker {}", invoker.getInvokerId());
    }

    /** React to Setup-tab configuration changes. */
    @InvokerRequest(InvokerRequest.Type.INVOKER_UPDATED)
    public void attributesUpdated(Map<String, Object> oldAttributes, Map<String, Object> newAttributes) {
        attributes.update(newAttributes);
    }
}
