package app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.integration;

import app.krista.extensions.accounting.taxrateservice.avalarataxrateservice.AvalaraTaxRateServiceAttributes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * The single HTTP facade for the Avalara AvaTax REST API (base URL fixed to the AvaTax Sandbox,
 * {@link AvalaraTaxRateServiceAttributes#BASE_URL}).
 *
 * <p>Responsibilities: one pooled {@link OkHttpClient}, HTTP Basic auth (Account ID + License Key) via
 * an interceptor, a transient-error retry interceptor, one {@code execute()} path, and a single
 * status&rarr;typed-exception mapping point. AvaTax returns plain JSON objects (no envelope) for single
 * resources, and a {@code {"@recordsetCount", "value": [...]}} wrapper for list/fetch-result endpoints —
 * callers read whichever shape the specific endpoint uses directly off the returned {@link JsonObject}.
 *
 * <p>Framework-free (only {@code @Service}) so it is unit-testable against okhttp MockWebServer.
 */
@Service
public class AvaTaxClient {

    private static final Logger LOG = LoggerFactory.getLogger(AvaTaxClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String CLIENT_HEADER_VALUE = "Krista-AvalaraTaxRateService; 1.0.0; HttpClient; 1.0";
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_BASE_DELAY_MILLIS = 500L;

    /** One shared, pooled client per base URL so we don't rebuild pools per request. */
    private static final ConcurrentHashMap<String, OkHttpClient> CLIENTS = new ConcurrentHashMap<>();

    private final AvalaraTaxRateServiceAttributes attributes;

    @Inject
    public AvaTaxClient(AvalaraTaxRateServiceAttributes attributes) {
        this.attributes = attributes;
    }

    // ------------------------------------------------------------------ verbs

    /** GET (with any query string already appended to {@code pathAndQuery}) → the parsed JSON object. */
    public JsonObject get(String pathAndQuery) throws IOException {
        return parseObject(execute(authorized(builder(pathAndQuery).get())));
    }

    /** POST a JSON body → the parsed JSON object. */
    public JsonObject post(String path, JsonObject body) throws IOException {
        return parseObject(execute(authorized(builder(path)
                .post(RequestBody.create(body == null ? "{}" : body.toString(), JSON)))));
    }

    // ------------------------------------------------------------- plumbing

    private Request.Builder builder(String pathAndQuery) {
        String url = attributes.getBaseUrl() + (pathAndQuery.startsWith("/") ? pathAndQuery : "/" + pathAndQuery);
        HttpUrl parsed = HttpUrl.parse(url);
        if (parsed == null) {
            throw new IllegalArgumentException("Invalid request URL derived from Base URL and path: " + url);
        }
        return new Request.Builder().url(parsed)
                .header("Accept", "application/json")
                .header("X-Avalara-Client", CLIENT_HEADER_VALUE);
    }

    private Request authorized(Request.Builder builder) {
        String credential = Credentials.basic(
                attributes.getAccountId() == null ? "" : attributes.getAccountId(),
                attributes.getLicenseKey() == null ? "" : attributes.getLicenseKey());
        return builder.header("Authorization", credential).build();
    }

    /** The single execute + error-mapping path. Successful responses are returned open for the caller. */
    private Response execute(Request request) throws IOException {
        LOG.debug("AvaTax API {} {}", request.method(), request.url().encodedPath());
        Response response = client().newCall(request).execute();
        if (!response.isSuccessful()) {
            handleErrorResponse(response); // reads body, closes, throws typed exception
        }
        return response;
    }

    private JsonObject parseObject(Response response) throws IOException {
        try (response) {
            String body = response.body() != null ? response.body().string() : "{}";
            if (body.isBlank()) {
                return new JsonObject();
            }
            JsonElement root = JsonParser.parseString(body);
            return root.isJsonObject() ? root.getAsJsonObject() : new JsonObject();
        }
    }

    /** SINGLE status → typed-exception mapping point. Full context to logs; friendly message to the user. */
    private void handleErrorResponse(Response response) throws IOException {
        int code = response.code();
        String body = response.body() != null ? response.body().string() : "";
        String apiMsg = extractMessage(body);
        LOG.error("AvaTax API error: {} {} -> HTTP {} | message='{}'",
                response.request().method(), response.request().url().encodedPath(), code, apiMsg);
        response.close();
        switch (code) {
            case 401, 403 -> throw new SecurityException(withDetail(
                    "Authentication failed or access is denied. Verify the Account ID and License Key on the Setup tab.",
                    apiMsg));
            case 400, 404, 422 -> throw new IllegalArgumentException(withDetail(
                    "AvaTax rejected the request. Verify the address fields provided.", apiMsg));
            case 409 -> throw new IllegalStateException(withDetail(
                    "The request conflicts with the current state of the resource.", apiMsg));
            case 429 -> throw new IOException("AvaTax rate limit reached. Retry after a short delay.");
            default -> throw new IOException("AvaTax API error (HTTP " + code + "): " + apiMsg);
        }
    }

    private OkHttpClient client() {
        String key = attributes.getBaseUrl();
        return CLIENTS.computeIfAbsent(key, k -> new OkHttpClient.Builder()
                .connectionPool(new okhttp3.ConnectionPool(20, 5, TimeUnit.MINUTES))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor(MAX_RETRY_ATTEMPTS, RETRY_BASE_DELAY_MILLIS))
                .build());
    }

    private static String withDetail(String friendly, String api) {
        return (api == null || api.isBlank()) ? friendly : friendly + " (" + api + ")";
    }

    /** AvaTax error payloads nest the message under {@code error.message}; fall back to a truncated raw body. */
    private static String extractMessage(String body) {
        try {
            JsonObject o = JsonParser.parseString(body).getAsJsonObject();
            if (o.has("error") && o.get("error").isJsonObject()) {
                JsonObject error = o.getAsJsonObject("error");
                if (error.has("message") && !error.get("message").isJsonNull()) {
                    return error.get("message").getAsString();
                }
            }
            for (String key : new String[]{"message", "title"}) {
                if (o.has(key) && !o.get(key).isJsonNull()) {
                    return o.get(key).getAsString();
                }
            }
        } catch (Exception ignored) {
            // fall through to the truncated raw body
        }
        return body != null && body.length() > 200 ? body.substring(0, 200) : (body == null ? "" : body);
    }

    /** Retry only transient failures (429/502/503/504 + IOException) with exponential backoff + jitter. */
    private static final class RetryInterceptor implements Interceptor {
        private final int maxAttempts;
        private final long baseDelayMillis;

        RetryInterceptor(int maxAttempts, long baseDelayMillis) {
            this.maxAttempts = Math.max(1, maxAttempts);
            this.baseDelayMillis = Math.max(0, baseDelayMillis);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            IOException last = null;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    if (response != null) {
                        response.close();
                    }
                    response = chain.proceed(request);
                    if (shouldRetry(response.code()) && attempt < maxAttempts) {
                        sleep(delay(attempt));
                        continue;
                    }
                    return response;
                } catch (IOException e) {
                    last = e;
                    if (attempt < maxAttempts) {
                        sleep(delay(attempt));
                    } else {
                        throw e;
                    }
                }
            }
            if (last != null) {
                throw last;
            }
            return response;
        }

        private boolean shouldRetry(int code) {
            return code == 429 || code == 502 || code == 503 || code == 504;
        }

        private long delay(int attempt) {
            if (attempt <= 1) {
                return 0;
            }
            long base = (long) Math.pow(2, attempt - 2) * baseDelayMillis;
            return base + (long) (Math.random() * base * 0.5);
        }

        private void sleep(long ms) {
            if (ms <= 0) {
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
