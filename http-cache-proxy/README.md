Multi-Threaded HTTP Caching Proxy
A Java 17 HTTP caching proxy built with ServerSocket, Socket, blocking streams, ExecutorService, and ConcurrentHashMap. It is an educational HTTP/1.1 proxy and currently supports absolute-form HTTP GET and HEAD requests.

Architecture
text
Client -> ProxyServer -> fixed ExecutorService -> ClientHandler
                                             |-> HttpRequestParser
                                             |-> CacheKey -> HttpCache -> CacheEntry
                                             |-> RequestCoordinator -> OriginServerClient
                                             |-> HttpResponseParser
                                             |-> CacheMetrics
Each client gets a worker from the fixed pool. Cache misses use a per-key single-flight future so concurrent clients do not issue duplicate origin requests. Entries expire using monotonic elapsed time and are removed by one scheduled cleanup task.

Build and run
bash
mvn clean test
mvn clean package
java -jar target/http-caching-proxy-1.0.0.jar
The configured JAR manifest starts com.example.proxy.ProxyApplication. Maven is required for dependency resolution and JUnit execution.

Configuration
Defaults are port 8080, 50 workers, 60-second TTL, 10,000 entries, and a 10 MiB response limit. Override values with command-line options such as --proxy.port=8081 or environment variables such as PROXY_PORT=8081. Supported keys include proxy.worker-threads, proxy.cache-ttl-seconds, proxy.max-cache-entries, proxy.max-response-size-bytes, client/origin timeouts, and proxy.cleanup-interval-seconds.

Manual test
bash
curl -v -x http://localhost:8080 http://example.com/
curl -v -x http://localhost:8080 http://example.com/
The first request is a miss and the repeated request can be a hit when the response is cacheable. Use a local origin for deterministic testing. Benchmark direct versus proxied repeated requests with curl, ab, or wrk; report measured latency and hit ratio rather than assumed improvement.

Code overview
ProxyServer – Creates a ServerSocket, accepts client connections, and submits each Socket to a fixed ExecutorService.

ClientHandler – Reads the raw HTTP request, delegates to HttpRequestParser, coordinates caching via RequestCoordinator, and writes the HTTP response back to the client.

HttpRequestParser – Parses the request line and headers from the blocking input stream, validates method and URI form, and produces a structured request object.

CacheKey – Normalizes method, host, and path into a stable key used in the cache map.

HttpCache – Thread-safe cache backed by a ConcurrentHashMap<CacheKey, CacheEntry> with TTL and size limits.

CacheEntry – Stores status line, headers, body bytes, and metadata such as creation time and cache-control directives.

RequestCoordinator – Ensures that for a given CacheKey, only one origin request is in flight at a time; other waiters reuse the same future.

OriginServerClient – Opens a plain TCP socket to the origin host, sends the serialized request, reads the response, and applies timeouts.

HttpResponseParser – Parses the origin’s status line and headers, handles chunked and content-length bodies, and returns a binary-safe response.

CacheMetrics – Tracks hits, misses, evictions, and errors for basic observability.

The implementation uses blocking sockets and a fixed thread pool; true non-blocking networking would require NIO channels and selectors.
