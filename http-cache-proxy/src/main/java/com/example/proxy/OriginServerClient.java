package com.example.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

/** Forwards HTTP requests to origins using blocking core Java sockets. */
public class OriginServerClient {
	private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
			"connection", "proxy-connection", "proxy-authenticate", "proxy-authorization",
			"te", "trailer", "transfer-encoding", "upgrade", "host");
	private final int connectTimeoutMillis;
	private final int readTimeoutMillis;
	private final HttpResponseParser responseParser;

	public OriginServerClient(ProxyConfig config) {
		connectTimeoutMillis = config.getOriginConnectTimeoutMillis();
		readTimeoutMillis = config.getOriginReadTimeoutMillis();
		responseParser = new HttpResponseParser(config.getMaxResponseSizeBytes());
	}

	public HttpResponse fetch(HttpRequest request) {
		URI uri = parseUri(request.getTarget());
		if (!"http".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
			throw new ProxyException("Only absolute HTTP origin targets are supported");
		}
		int port = uri.getPort() < 0 ? 80 : uri.getPort();
		String path = uri.getRawPath() == null || uri.getRawPath().isEmpty() ? "/" : uri.getRawPath();
		if (uri.getRawQuery() != null) path += "?" + uri.getRawQuery();
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(uri.getHost(), port), connectTimeoutMillis);
			socket.setSoTimeout(readTimeoutMillis);
			writeRequest(socket.getOutputStream(), request, uri, port, path);
			return responseParser.parse(socket.getInputStream());
		} catch (SocketTimeoutException exception) {
			throw new ProxyException("Origin request timed out", exception);
		} catch (IOException exception) {
			throw new ProxyException("Origin request failed", exception);
		}
	}

	private static void writeRequest(OutputStream output, HttpRequest request, URI uri, int port, String path)
			throws IOException {
		String host = uri.getHost() + (port == 80 ? "" : ":" + port);
		StringBuilder headers = new StringBuilder()
				.append(request.getMethod()).append(' ').append(path).append(" HTTP/1.1\r\n")
				.append("Host: ").append(host).append("\r\n")
				.append("Connection: close\r\n");
		for (var header : request.getHeaders().entrySet()) {
			if (!HOP_BY_HOP_HEADERS.contains(header.getKey().toLowerCase(Locale.ROOT))) {
				headers.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
			}
		}
		byte[] body = request.getBody();
		if (body.length > 0) headers.append("Content-Length: ").append(body.length).append("\r\n");
		headers.append("\r\n");
		output.write(headers.toString().getBytes(StandardCharsets.ISO_8859_1));
		output.write(body);
		output.flush();
	}

	private static URI parseUri(String target) {
		try { return new URI(target); }
		catch (URISyntaxException exception) { throw new ProxyException("Invalid origin URI", exception); }
	}
}
