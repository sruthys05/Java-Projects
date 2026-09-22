package com.example.proxy;

import java.util.Locale;
import java.util.Map;
import java.util.OptionalLong;

/** Decides whether an HTTP response can enter the shared cache. */
public final class CachePolicy {
    private final long defaultTtlSeconds;
    private final long maxResponseSizeBytes;

    public CachePolicy(long defaultTtlSeconds, long maxResponseSizeBytes) {
        if (defaultTtlSeconds <= 0 || maxResponseSizeBytes <= 0) throw new IllegalArgumentException("Policy limits must be positive");
        this.defaultTtlSeconds = defaultTtlSeconds;
        this.maxResponseSizeBytes = maxResponseSizeBytes;
    }

    public boolean shouldCache(HttpRequest request, HttpResponse response) {
        if (!request.getMethod().equalsIgnoreCase("GET") || response.getStatusCode() != 200
                || response.getBody().length > maxResponseSizeBytes) return false;
        String cacheControl = response.getHeader("Cache-Control").orElse("").toLowerCase(Locale.ROOT);
        if (hasDirective(cacheControl, "no-store") || hasDirective(cacheControl, "private")) return false;
        if (response.getHeader("Set-Cookie").isPresent()) return false;
        if (request.getHeader("Authorization").isPresent()) return false;
        return true;
    }

    public long ttlSeconds(HttpResponse response) {
        String directives = response.getHeader("Cache-Control").orElse("");
        OptionalLong shared = directiveValue(directives, "s-maxage");
        if (shared.isPresent()) return Math.max(1, shared.getAsLong());
        OptionalLong maxAge = directiveValue(directives, "max-age");
        if (maxAge.isPresent()) return Math.max(1, maxAge.getAsLong());
        return defaultTtlSeconds;
    }

    private static boolean hasDirective(String value, String directive) {
        return value.matches(".*(^|,)\\s*" + directive + "(?:\\s*=|\\s*,|\\s*$).*");
    }

    private static OptionalLong directiveValue(String directives, String name) {
        for (String directive : directives.toLowerCase(Locale.ROOT).split(",")) {
            String[] parts = directive.trim().split("=", 2);
            if (parts.length == 2 && parts[0].trim().equals(name)) {
                try { return OptionalLong.of(Long.parseLong(parts[1].trim())); }
                catch (NumberFormatException ignored) { return OptionalLong.empty(); }
            }
        }
        return OptionalLong.empty();
    }
}