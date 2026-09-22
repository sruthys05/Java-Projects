package com.example.proxy;

import java.util.HashMap;
import java.util.Map;

/** Immutable runtime configuration for the proxy server. */
public final class ProxyConfig {
    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_WORKER_THREADS = 50;
    public static final long DEFAULT_CACHE_TTL_SECONDS = 60;
    public static final int DEFAULT_MAX_CACHE_ENTRIES = 10_000;
    public static final long DEFAULT_MAX_RESPONSE_SIZE_BYTES = 10_485_760;
    public static final int DEFAULT_CLIENT_TIMEOUT_MILLIS = 10_000;
    public static final int DEFAULT_ORIGIN_CONNECT_TIMEOUT_MILLIS = 5_000;
    public static final int DEFAULT_ORIGIN_READ_TIMEOUT_MILLIS = 10_000;
    public static final int DEFAULT_CLEANUP_INTERVAL_SECONDS = 30;

    private final int port;
    private final int workerThreads;
    private final long cacheTtlSeconds;
    private final int maxCacheEntries;
    private final long maxResponseSizeBytes;
    private final int clientTimeoutMillis;
    private final int originConnectTimeoutMillis;
    private final int originReadTimeoutMillis;
    private final int cleanupIntervalSeconds;

    public ProxyConfig(int port, int workerThreads, long cacheTtlSeconds, int maxCacheEntries,
                       long maxResponseSizeBytes, int clientTimeoutMillis,
                       int originConnectTimeoutMillis, int originReadTimeoutMillis,
                       int cleanupIntervalSeconds) {
        validate(port, workerThreads, cacheTtlSeconds, maxCacheEntries, maxResponseSizeBytes,
                clientTimeoutMillis, originConnectTimeoutMillis, originReadTimeoutMillis,
                cleanupIntervalSeconds);
        this.port = port;
        this.workerThreads = workerThreads;
        this.cacheTtlSeconds = cacheTtlSeconds;
        this.maxCacheEntries = maxCacheEntries;
        this.maxResponseSizeBytes = maxResponseSizeBytes;
        this.clientTimeoutMillis = clientTimeoutMillis;
        this.originConnectTimeoutMillis = originConnectTimeoutMillis;
        this.originReadTimeoutMillis = originReadTimeoutMillis;
        this.cleanupIntervalSeconds = cleanupIntervalSeconds;
    }

    public static ProxyConfig defaults() {
        return new ProxyConfig(DEFAULT_PORT, DEFAULT_WORKER_THREADS, DEFAULT_CACHE_TTL_SECONDS,
                DEFAULT_MAX_CACHE_ENTRIES, DEFAULT_MAX_RESPONSE_SIZE_BYTES,
                DEFAULT_CLIENT_TIMEOUT_MILLIS, DEFAULT_ORIGIN_CONNECT_TIMEOUT_MILLIS,
                DEFAULT_ORIGIN_READ_TIMEOUT_MILLIS, DEFAULT_CLEANUP_INTERVAL_SECONDS);
    }

    public static ProxyConfig fromEnvironmentAndArgs(String[] args) {
        Map<String, String> values = new HashMap<>();
        values.put("proxy.port", System.getenv("PROXY_PORT"));
        values.put("proxy.worker-threads", System.getenv("PROXY_WORKER_THREADS"));
        values.put("proxy.cache-ttl-seconds", System.getenv("PROXY_CACHE_TTL_SECONDS"));
        values.put("proxy.max-cache-entries", System.getenv("PROXY_MAX_CACHE_ENTRIES"));
        values.put("proxy.max-response-size-bytes", System.getenv("PROXY_MAX_RESPONSE_SIZE_BYTES"));
        values.put("proxy.client-timeout-millis", System.getenv("PROXY_CLIENT_TIMEOUT_MILLIS"));
        values.put("proxy.origin-connect-timeout-millis", System.getenv("PROXY_ORIGIN_CONNECT_TIMEOUT_MILLIS"));
        values.put("proxy.origin-read-timeout-millis", System.getenv("PROXY_ORIGIN_READ_TIMEOUT_MILLIS"));
        values.put("proxy.cleanup-interval-seconds", System.getenv("PROXY_CLEANUP_INTERVAL_SECONDS"));
        if (args != null) {
            for (int index = 0; index < args.length; index++) {
                String argument = args[index];
                if (!argument.startsWith("--")) {
                    throw new IllegalArgumentException("Invalid argument: " + argument);
                }
                String option = argument.substring(2);
                int separator = option.indexOf('=');
                if (separator >= 0) {
                    values.put(option.substring(0, separator), option.substring(separator + 1));
                } else if (index + 1 < args.length) {
                    values.put(option, args[++index]);
                } else {
                    throw new IllegalArgumentException("Missing value for --" + option);
                }
            }
        }
        return new ProxyConfig(
                integer(values, "proxy.port", DEFAULT_PORT),
                integer(values, "proxy.worker-threads", DEFAULT_WORKER_THREADS),
                longValue(values, "proxy.cache-ttl-seconds", DEFAULT_CACHE_TTL_SECONDS),
                integer(values, "proxy.max-cache-entries", DEFAULT_MAX_CACHE_ENTRIES),
                longValue(values, "proxy.max-response-size-bytes", DEFAULT_MAX_RESPONSE_SIZE_BYTES),
                integer(values, "proxy.client-timeout-millis", DEFAULT_CLIENT_TIMEOUT_MILLIS),
                integer(values, "proxy.origin-connect-timeout-millis", DEFAULT_ORIGIN_CONNECT_TIMEOUT_MILLIS),
                integer(values, "proxy.origin-read-timeout-millis", DEFAULT_ORIGIN_READ_TIMEOUT_MILLIS),
                integer(values, "proxy.cleanup-interval-seconds", DEFAULT_CLEANUP_INTERVAL_SECONDS));
    }

    private static int integer(Map<String, String> values, String key, int defaultValue) {
        String value = values.get(key);
        return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
    }

    private static long longValue(Map<String, String> values, String key, long defaultValue) {
        String value = values.get(key);
        return value == null || value.isBlank() ? defaultValue : Long.parseLong(value);
    }

    private static void validate(int port, int workerThreads, long cacheTtlSeconds, int maxCacheEntries,
                                long maxResponseSizeBytes, int clientTimeoutMillis,
                                int originConnectTimeoutMillis, int originReadTimeoutMillis,
                                int cleanupIntervalSeconds) {
        if (port < 1 || port > 65_535) throw new IllegalArgumentException("Port must be between 1 and 65535");
        if (workerThreads <= 0) throw new IllegalArgumentException("Worker threads must be positive");
        if (cacheTtlSeconds <= 0) throw new IllegalArgumentException("Cache TTL must be positive");
        if (maxCacheEntries <= 0) throw new IllegalArgumentException("Maximum cache entries must be positive");
        if (maxResponseSizeBytes <= 0) throw new IllegalArgumentException("Maximum response size must be positive");
        if (clientTimeoutMillis <= 0 || originConnectTimeoutMillis <= 0 || originReadTimeoutMillis <= 0) {
            throw new IllegalArgumentException("Timeouts must be positive");
        }
        if (cleanupIntervalSeconds <= 0) throw new IllegalArgumentException("Cleanup interval must be positive");
    }

    public int getPort() { return port; }
    public int getWorkerThreads() { return workerThreads; }
    public long getCacheTtlSeconds() { return cacheTtlSeconds; }
    public int getMaxCacheEntries() { return maxCacheEntries; }
    public long getMaxResponseSizeBytes() { return maxResponseSizeBytes; }
    public int getClientTimeoutMillis() { return clientTimeoutMillis; }
    public int getOriginConnectTimeoutMillis() { return originConnectTimeoutMillis; }
    public int getOriginReadTimeoutMillis() { return originReadTimeoutMillis; }
    public int getCleanupIntervalSeconds() { return cleanupIntervalSeconds; }
}
