package com.example.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** Blocking multithreaded proxy server backed by a fixed worker pool. */
public class ProxyServer {
	private final ProxyConfig config;
	private final HttpCache cache;
	private final CachePolicy cachePolicy;
	private final CacheMetrics metrics = new CacheMetrics();
	private final ExecutorService workers;
	private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
	private final AtomicBoolean running = new AtomicBoolean();
	private final RequestCoordinator coordinator;
	private final OriginServerClient originClient;
	private volatile ServerSocket serverSocket;

	public ProxyServer(ProxyConfig config) {
		this.config = config;
		cache = new HttpCache(config.getMaxCacheEntries(), config.getMaxResponseSizeBytes());
		cachePolicy = new CachePolicy(config.getCacheTtlSeconds(), config.getMaxResponseSizeBytes());
		workers = Executors.newFixedThreadPool(config.getWorkerThreads());
		coordinator = new RequestCoordinator(workers);
		originClient = new OriginServerClient(config);
	}

	public void start() throws IOException {
		if (!running.compareAndSet(false, true)) return;
		cleanupScheduler.scheduleAtFixedRate(() -> {
			int removed = cache.removeExpiredEntries();
			if (removed > 0) ProxyLogger.info("CACHE_CLEANUP removed=" + removed + " remaining=" + cache.size());
		}, config.getCleanupIntervalSeconds(), config.getCleanupIntervalSeconds(), TimeUnit.SECONDS);
		try (ServerSocket socket = new ServerSocket(config.getPort())) {
			serverSocket = socket;
			ProxyLogger.info("Proxy started on port " + config.getPort());
			while (running.get()) {
				try {
					Socket client = socket.accept();
					client.setSoTimeout(config.getClientTimeoutMillis());
					workers.submit(new ClientHandler(client, cache, cachePolicy, coordinator, originClient, metrics));
				} catch (IOException exception) {
					if (running.get()) ProxyLogger.warning("Failed to accept client connection", exception);
				}
			}
		} finally {
			running.set(false);
			shutdownExecutors();
		}
	}

	public void shutdown() {
		if (running.compareAndSet(true, false)) {
			try { if (serverSocket != null) serverSocket.close(); }
			catch (IOException exception) { ProxyLogger.warning("Failed to close server socket", exception); }
		}
		shutdownExecutors();
	}

	public CacheMetrics getMetrics() { return metrics; }

	private void shutdownExecutors() {
		workers.shutdown();
		cleanupScheduler.shutdown();
		try {
			if (!workers.awaitTermination(5, TimeUnit.SECONDS)) workers.shutdownNow();
			if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) cleanupScheduler.shutdownNow();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			workers.shutdownNow();
			cleanupScheduler.shutdownNow();
		}
	}
}
