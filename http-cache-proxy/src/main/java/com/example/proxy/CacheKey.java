package com.example.proxy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;

/** Canonical key for an HTTP request stored in the shared cache. */
public final class CacheKey {
    private final String value;

    private CacheKey(String value) { this.value = value; }

    public static CacheKey from(HttpRequest request) {
        Objects.requireNonNull(request, "request");
        try {
            URI uri = new URI(request.getTarget());
            String scheme = requireComponent(uri.getScheme(), "scheme").toLowerCase(Locale.ROOT);
            if (!scheme.equals("http") && !scheme.equals("https")) {
                throw new IllegalArgumentException("Unsupported URI scheme: " + scheme);
            }
            String host = requireComponent(uri.getHost(), "host").toLowerCase(Locale.ROOT);
            int port = uri.getPort();
            if (port < 0) port = scheme.equals("https") ? 443 : 80;
            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) path = "/";
            String normalizedTarget = scheme + "://" + host + ":" + port + path
                    + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            return new CacheKey(request.getMethod().toUpperCase(Locale.ROOT) + ":" + normalizedTarget);
        } catch (URISyntaxException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid absolute request target: " + request.getTarget(), exception);
        }
    }

    private static String requireComponent(String component, String name) {
        if (component == null || component.isBlank()) throw new IllegalArgumentException("Missing URI " + name);
        return component;
    }

    public String value() { return value; }
    @Override public String toString() { return value; }
    @Override public boolean equals(Object other) {
        return other instanceof CacheKey key && value.equals(key.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
}