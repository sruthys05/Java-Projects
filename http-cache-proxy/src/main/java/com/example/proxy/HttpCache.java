package com.example.proxy;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Thread-safe bounded in-memory HTTP response cache. */
public class HttpCache {
	private final ConcurrentHashMap<CacheKey, CacheEntry> entries = new ConcurrentHashMap<>();
	private final int maxEntries;
	private final long maxResponseSizeBytes;

	public HttpCache(int maxEntries, long maxResponseSizeBytes) {
		if (maxEntries <= 0 || maxResponseSizeBytes <= 0) {
			throw new IllegalArgumentException("Cache limits must be positive");
		}
		this.maxEntries = maxEntries;
		this.maxResponseSizeBytes = maxResponseSizeBytes;
	}

	public Optional<CacheEntry> get(CacheKey key) {
		CacheEntry entry = entries.get(key);
		if (entry == null) return Optional.empty();
		if (entry.isExpired()) {
			entries.remove(key, entry);
			return Optional.empty();
		}
		entry.recordHit();
		return Optional.of(entry);
	}

	public void put(CacheKey key, CacheEntry entry) {
		if (entry.getResponse().getBody().length > maxResponseSizeBytes) return;
		if (entries.size() >= maxEntries && !entries.containsKey(key)) {
			CacheKey oldestKey = entries.keySet().stream().findFirst().orElse(null);
			if (oldestKey != null) entries.remove(oldestKey);
		}
		entries.put(key, entry);
	}

	public void remove(CacheKey key) { entries.remove(key); }
	public void clear() { entries.clear(); }
	public int size() { return entries.size(); }

	public int removeExpiredEntries() {
		int removed = 0;
		for (var entry : entries.entrySet()) {
			if (entry.getValue().isExpired() && entries.remove(entry.getKey(), entry.getValue())) removed++;
		}
		return removed;
	}
}
