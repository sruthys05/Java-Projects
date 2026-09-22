package com.example.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Processes one client connection from parsing through response delivery. */
public class ClientHandler implements Runnable {
	private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
			"connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
			"te", "trailer", "transfer-encoding", "upgrade", "content-length"
	);

	private final Socket clientSocket;
	private final HttpCache cache;
	private final CachePolicy cachePolicy;
	private final RequestCoordinator coordinator;
	private final OriginServerClient originClient;
	private final CacheMetrics metrics;

	public ClientHandler(Socket clientSocket, HttpCache cache, CachePolicy cachePolicy,
						 RequestCoordinator coordinator, OriginServerClient originClient,
						 CacheMetrics metrics) {
		this.clientSocket = clientSocket;
		this.cache = cache;
		this.cachePolicy = cachePolicy;
		this.coordinator = coordinator;
		this.originClient = originClient;
		this.metrics = metrics;
	}

	@Override
	public void run() {
		long started = System.nanoTime();
		metrics.requestStarted();
		try (Socket socket = clientSocket) {
			HttpRequest request = new HttpRequestParser().parse(socket.getInputStream());
			if (!request.getMethod().equals("GET") && !request.getMethod().equals("HEAD")) {
				writeError(socket.getOutputStream(), 501, "Not Implemented", "Method is not supported");
				return;
			}
			CacheKey key = CacheKey.from(request);
			var cached = cache.get(key);
			if (cached.isPresent()) {
				metrics.cacheHit(cached.get().getResponse().getBody().length);
				sendResponse(socket.getOutputStream(), cached.get().getResponse(), request.getMethod());
				return;
			}
			metrics.cacheMiss();
			HttpResponse response = coordinator.fetch(key, () -> {
				metrics.originRequest();
				HttpResponse fetched = originClient.fetch(request);
				metrics.originResponse(fetched.getBody().length);
				if (cachePolicy.shouldCache(request, fetched)) {
					cache.put(key, new CacheEntry(key, fetched, cachePolicy.ttlSeconds(fetched)));
				}
				return fetched;
			});
			sendResponse(socket.getOutputStream(), response, request.getMethod());
		} catch (ProxyException | IOException exception) {
			metrics.error();
			try { writeError(clientSocket.getOutputStream(), 400, "Bad Request", exception.getMessage()); }
			catch (IOException ignored) { ProxyLogger.warning("Unable to write error response", exception); }
		} finally {
			metrics.requestFinished(System.nanoTime() - started);
		}
	}

	static void sendResponse(OutputStream output, HttpResponse response, String method) throws IOException {
		byte[] body = response.getBody();

		ProxyLogger.info("ORIGIN_STATUS=" + response.getStatusCode());
		response.getHeader("Transfer-Encoding").ifPresent(v -> ProxyLogger.info("ORIGIN_TRANSFER_ENCODING=" + v));
		response.getHeader("Content-Length").ifPresent(v -> ProxyLogger.info("ORIGIN_CONTENT_LENGTH=" + v));
		ProxyLogger.info("DECODED_BODY_LENGTH=" + body.length);
		ProxyLogger.info("CLIENT_TRANSFER_ENCODING=none");
		ProxyLogger.info("CLIENT_CONTENT_LENGTH=" + body.length);

		StringBuilder wire = new StringBuilder();
		wire.append(response.getVersion()).append(' ')
			.append(response.getStatusCode()).append(' ')
			.append(response.getReasonPhrase()).append("\r\n");

		for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
			String lowerName = entry.getKey().toLowerCase(Locale.ROOT);
			if (HOP_BY_HOP_HEADERS.contains(lowerName)) {
				continue;
			}
			wire.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
		}

		wire.append("Content-Length: ").append(body.length).append("\r\n");
		wire.append("Connection: close\r\n");
		wire.append("\r\n");

		output.write(wire.toString().getBytes(StandardCharsets.ISO_8859_1));
		if (!method.equals("HEAD")) {
			output.write(body);
		}
		output.flush();
	}

	private static void writeError(OutputStream output, int status, String reason, String message) throws IOException {
		byte[] body = (message == null ? reason : message).getBytes(StandardCharsets.UTF_8);
		HttpResponse response = new HttpResponse(status, reason, "HTTP/1.1",
				Map.of("Content-Type", "text/plain; charset=utf-8"), body);
		sendResponse(output, response, "GET");
	}
}
