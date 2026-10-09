package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.catalog.support;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponse.Error.ExceptionType;
import app.krista.extension.executor.Invoker;
import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.integration.AvaTaxClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared base for every Avalara Tax Rate Service Area. Holds the injected {@link AvaTaxClient},
 * {@link TelemetryHelper}, and the {@code invokerId} (multi-tenant scope), and owns the one
 * {@code audited(...)} wrapper so each {@code @CatalogRequest} method stays thin: validate &rarr; call
 * client &rarr; transform &rarr; build response. The wrapper writes the uniform try/catch &rarr;
 * classify &rarr; {@link ExtensionResponse} + telemetry + timing exactly once.
 */
public abstract class AbstractArea {

    protected static final String PREFIX = "avalaraTaxRateService";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArea.class);

    protected final AvaTaxClient client;
    protected final TelemetryHelper telemetry;
    protected final String invokerId;

    protected AbstractArea(AvaTaxClient client, TelemetryHelper telemetry, Invoker invoker) {
        this.client = client;
        this.telemetry = telemetry;
        this.invokerId = invoker.getInvokerId();
    }

    @FunctionalInterface
    protected interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    /** The uniform boundary: timing + telemetry + try/catch → classified ExtensionResponse. */
    protected ExtensionResponse audited(String name, CheckedSupplier<ExtensionResponse> action) {
        long t0 = telemetry.start(PREFIX + "." + name);
        LOG.info("Catalog '{}' started for invoker {}", name, invokerId);
        try {
            ExtensionResponse r = action.get();
            LOG.info("Catalog '{}' ok in {}ms for invoker {}", name, System.currentTimeMillis() - t0, invokerId);
            telemetry.recordSuccess(PREFIX + "." + name, t0);
            return r;
        } catch (SecurityException e) {
            LOG.error("Catalog '{}' AUTH: {}", name, e.getMessage());
            telemetry.recordError(PREFIX + "." + name, t0, e);
            return ExtensionResponses.failure(e.getMessage(), ExceptionType.AUTHENTICATION_ERROR);
        } catch (IllegalArgumentException e) {
            LOG.warn("Catalog '{}' INPUT: {}", name, e.getMessage());
            telemetry.recordError(PREFIX + "." + name, t0, e);
            return ExtensionResponses.failure(e.getMessage(), ExceptionType.INPUT_ERROR);
        } catch (Exception e) {
            LOG.error("Catalog '{}' SYSTEM: {}", name, e.getMessage(), e);
            telemetry.recordError(PREFIX + "." + name, t0, e);
            return ExtensionResponses.failure(e.getMessage(), ExceptionType.SYSTEM_ERROR);
        }
    }

    // ------------------------------------------------------------- query params

    /** Accumulates only non-blank query parameters, URL-encoding keys and values. */
    protected static final class Query {
        private final List<String> parts = new ArrayList<>();

        public Query add(String key, String value) {
            if (value != null && !value.isBlank()) {
                parts.add(enc(key) + "=" + enc(value.trim()));
            }
            return this;
        }

        public Query add(String key, Double value) {
            if (value != null) {
                parts.add(enc(key) + "=" + (value == Math.floor(value) ? String.valueOf(value.longValue()) : value));
            }
            return this;
        }

        public String build() {
            return parts.isEmpty() ? "" : "?" + String.join("&", parts);
        }
    }

    protected static Query query() {
        return new Query();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------- response maps

    protected static Map<String, Object> out() {
        return new LinkedHashMap<>();
    }
}
