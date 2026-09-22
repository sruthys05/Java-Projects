package com.example.proxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/** Shares one in-flight origin fetch among concurrent requests for one key. */
public final class RequestCoordinator {
    private final ConcurrentHashMap<CacheKey, CompletableFuture<HttpResponse>> inFlight = new ConcurrentHashMap<>();
    private final Executor executor;

    public RequestCoordinator(Executor executor) { this.executor = executor; }

    public HttpResponse fetch(CacheKey key, Supplier<HttpResponse> originFetch) {
        CompletableFuture<HttpResponse> created = new CompletableFuture<>();
        CompletableFuture<HttpResponse> existing = inFlight.putIfAbsent(key, created);
        if (existing == null) {
            executor.execute(() -> {
                try { created.complete(originFetch.get()); }
                catch (Throwable failure) { created.completeExceptionally(failure); }
                finally { inFlight.remove(key, created); }
            });
            existing = created;
        }
        try { return existing.join(); }
        catch (RuntimeException exception) { throw exception; }
    }

    public int inFlightCount() { return inFlight.size(); }
}