package com.example.proxy;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/** Thread-safe counters for proxy and cache activity. */
public final class CacheMetrics {
    private final LongAdder totalRequests = new LongAdder();
    private final LongAdder cacheHits = new LongAdder();
    private final LongAdder cacheMisses = new LongAdder();
    private final LongAdder expiredEntries = new LongAdder();
    private final LongAdder originRequests = new LongAdder();
    private final LongAdder originFailures = new LongAdder();
    private final LongAdder totalErrors = new LongAdder();
    private final LongAdder cacheBytes = new LongAdder();
    private final LongAdder originBytes = new LongAdder();
    private final LongAdder latencyNanos = new LongAdder();
    private final AtomicLong activeConnections = new AtomicLong();

    public void requestStarted() { totalRequests.increment(); activeConnections.incrementAndGet(); }
    public void requestFinished(long elapsedNanos) { latencyNanos.add(elapsedNanos); activeConnections.decrementAndGet(); }
    public void cacheHit(long bytes) { cacheHits.increment(); cacheBytes.add(bytes); }
    public void cacheMiss() { cacheMisses.increment(); }
    public void expiredEntry() { expiredEntries.increment(); }
    public void originRequest() { originRequests.increment(); }
    public void originResponse(long bytes) { originBytes.add(bytes); }
    public void originFailure() { originFailures.increment(); }
    public void error() { totalErrors.increment(); }
    public long getTotalRequests() { return totalRequests.sum(); }
    public long getCacheHits() { return cacheHits.sum(); }
    public long getCacheMisses() { return cacheMisses.sum(); }
    public double getHitRate() { long total = getCacheHits() + getCacheMisses(); return total == 0 ? 0.0 : (double) getCacheHits() / total; }
    public long getExpiredEntries() { return expiredEntries.sum(); }
    public long getOriginRequests() { return originRequests.sum(); }
    public long getActiveConnections() { return activeConnections.get(); }
    public long getAverageLatencyNanos() { long requests = getTotalRequests(); return requests == 0 ? 0 : latencyNanos.sum() / requests; }
}