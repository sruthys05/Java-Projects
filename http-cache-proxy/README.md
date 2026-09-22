# Multi-Threaded HTTP Caching Proxy

A Java 17 HTTP caching proxy built with `ServerSocket`, `Socket`, blocking streams, `ExecutorService`, and `ConcurrentHashMap`. It is an educational HTTP/1.1 proxy and currently supports absolute-form HTTP `GET` and `HEAD` requests.

## Architecture

```text
Client -> ProxyServer -> fixed ExecutorService -> ClientHandler
                                             |-> HttpRequestParser
                                             |-> CacheKey -> HttpCache -> CacheEntry
                                             |-> RequestCoordinator -> OriginServerClient
                                             |-> HttpResponseParser
                                             |-> CacheMetrics
```

Each client gets a worker from the fixed pool. Cache misses use a per-key single-flight future so concurrent clients do not issue duplicate origin requests. Entries expire using monotonic elapsed time and are removed by one scheduled cleanup task.

## Build and run

```bash
mvn clean test
mvn clean package
java -jar target/http-caching-proxy-1.0.0.jar
```

The configured JAR manifest starts `com.example.proxy.ProxyApplication`. Maven is required for dependency resolution and JUnit execution.

## Configuration

Defaults are port `8080`, 50 workers, 60-second TTL, 10,000 entries, and a 10 MiB response limit. Override values with command-line options such as `--proxy.port=8081` or environment variables such as `PROXY_PORT=8081`. Supported keys include `proxy.worker-threads`, `proxy.cache-ttl-seconds`, `proxy.max-cache-entries`, `proxy.max-response-size-bytes`, client/origin timeouts, and `proxy.cleanup-interval-seconds`.

## Manual test

```bash
curl -v -x http://localhost:8080 http://example.com/
curl -v -x http://localhost:8080 http://example.com/
```

The first request is a miss and the repeated request can be a hit when the response is cacheable. Use a local origin for deterministic testing. Benchmark direct versus proxied repeated requests with `curl`, `ab`, or `wrk`; report measured latency and hit ratio rather than assumed improvement.

## Policy and limitations

Only successful `GET` responses are cached. `no-store`, `private`, `Set-Cookie`, authorized requests, oversized bodies, and unsupported schemes are rejected by the shared-cache policy. `s-maxage` and `max-age` override the configured fallback TTL. HTTPS `CONNECT` tunneling and TLS interception are intentionally not implemented; encrypted traffic must not be inspected or cached. Authentication, access control, request-rate limiting, and broader production hardening are future work.

Malformed requests return `400`, unsupported methods return `501`, and origin failures are handled by the proxy runtime. The implementation uses blocking sockets; true non-blocking networking would require NIO channels and selectors.

## Interview summary

This project demonstrates a multithreaded Core Java proxy: `ServerSocket` accepts connections, a fixed `ExecutorService` processes them, normalized `CacheKey` values address a thread-safe `ConcurrentHashMap` cache, TTL metadata controls freshness, and `RequestCoordinator` prevents cache stampedes. The design also demonstrates bounded HTTP parsing, origin timeouts, binary-safe responses, cleanup scheduling, metrics, and local testable components.
