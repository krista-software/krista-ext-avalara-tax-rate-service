package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice;

import app.krista.extension.executor.Invoker;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Avalara AvaTax connection configuration and credentials.
 *
 * HK2 {@code @Service} so it can be injected into the extension and the HTTP client. The key constants
 * are the saved-configuration contract — they match the class-level {@code @Field.Text(value = ...)} on
 * {@link AvalaraTaxRateServiceExtension}. Keep the key strings stable.
 *
 * Auth model: HTTP Basic Authentication (Krista pattern A, two secured fields instead of one token).
 * AvaTax accepts an Avalara Account ID as the Basic-Auth username and a License Key as the password —
 * both static, pre-issued credentials with no OAuth/login flow. The target environment is fixed to the
 * AvaTax Sandbox per the deployment's explicit choice; it is not a Setup-tab field.
 */
@Service
public class AvalaraTaxRateServiceAttributes {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvalaraTaxRateServiceAttributes.class);

    public static final String ACCOUNT_ID = "Account ID";
    public static final String LICENSE_KEY = "License Key";

    /** Fixed per the deployment's explicit choice (AvaTax Sandbox) — not a Setup-tab field. */
    public static final String BASE_URL = "https://sandbox-rest.avatax.com";

    private String accountId;
    private String licenseKey;

    @Inject
    public AvalaraTaxRateServiceAttributes(Invoker invoker) {
        this(invoker.getAttributes());
    }

    public AvalaraTaxRateServiceAttributes(Map<String, Object> attributes) {
        update(attributes);
    }

    public static AvalaraTaxRateServiceAttributes create(Invoker invoker, Map<String, Object> attributes) {
        AvalaraTaxRateServiceAttributes result = new AvalaraTaxRateServiceAttributes(invoker);
        result.update(attributes);
        return result;
    }

    public static AvalaraTaxRateServiceAttributes create(Map<String, Object> attributes) {
        return new AvalaraTaxRateServiceAttributes(attributes);
    }

    public void update(Map<String, Object> attributes) {
        this.accountId = getString(attributes, ACCOUNT_ID, "");
        this.licenseKey = getString(attributes, LICENSE_KEY, "");
        // Never log the license key; account id only.
        LOGGER.info("Avalara Tax Rate Service attributes updated - Account ID: {}", accountId);
    }

    /** Round-trip map for persistence (no secret masking here — the platform KeyValueStore encrypts at rest). */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ACCOUNT_ID, accountId);
        map.put(LICENSE_KEY, licenseKey);
        return map;
    }

    private static String getString(Map<String, Object> attributes, String key, String defaultValue) {
        Object value = attributes == null ? null : attributes.get(key);
        return value != null && !value.toString().isBlank() ? value.toString() : defaultValue;
    }

    public String getAccountId() { return accountId; }
    public String getLicenseKey() { return licenseKey; }
    public String getBaseUrl() { return BASE_URL; }
}
