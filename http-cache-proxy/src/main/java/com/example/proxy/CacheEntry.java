package com.example.proxy;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/** Immutable response metadata stored alongside a cached response. */
public class CacheEntry {
	private final CacheKey key;
	private final HttpResponse response;
	private final Instant createdAt;
	private final Instant expiresAt;
	private final long createdNanos;
	private final long ttlSeconds;
	private final AtomicLong hitCount = new AtomicLong();

	public CacheEntry(CacheKey key, HttpResponse response, long ttlSeconds) {
		if (key == null || response == null || ttlSeconds <= 0) {
			throw new IllegalArgumentException("Invalid cache entry");
		}
		this.key = key;
		this.response = response;
		this.ttlSeconds = ttlSeconds;
		this.createdAt = Instant.now();
		this.expiresAt = createdAt.plusSeconds(ttlSeconds);
		this.createdNanos = System.nanoTime();
	}

	public boolean isExpired() {
		return System.nanoTime() - createdNanos >= ttlSeconds * 1_000_000_000L;
	}

	public long getAgeSeconds() {
		return Math.max(0, (System.nanoTime() - createdNanos) / 1_000_000_000L);
	}

	public CacheKey getKey() { return key; }
	public HttpResponse getResponse() { return response; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getExpiresAt() { return expiresAt; }
	public long getTtlSeconds() { return ttlSeconds; }
	public long getHitCount() { return hitCount.get(); }
	public void recordHit() { hitCount.incrementAndGet(); }
}
