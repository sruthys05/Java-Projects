package com.example.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CacheTest {
    @Test
    void storesAndReturnsFreshEntries() {
        HttpRequest request = new HttpRequest("GET", "http://localhost/data", "HTTP/1.1", Map.of(), new byte[0]);
        CacheKey key = CacheKey.from(request);
        HttpResponse response = new HttpResponse(200, "OK", "HTTP/1.1", Map.of(), new byte[] {1, 2});
        HttpCache cache = new HttpCache(2, 10);
        cache.put(key, new CacheEntry(key, response, 60));
        assertTrue(cache.get(key).isPresent());
        assertEquals(1, cache.size());
    }
}
