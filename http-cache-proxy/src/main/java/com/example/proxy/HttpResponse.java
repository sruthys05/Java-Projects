package com.example.proxy;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Immutable HTTP response representation with a binary-safe body. */
public class HttpResponse {
	private final int statusCode;
	private final String reasonPhrase;
	private final String version;
	private final Map<String, String> headers;
	private final byte[] body;

	public HttpResponse(int statusCode, String reasonPhrase, String version,
						Map<String, String> headers, byte[] body) {
		if (reasonPhrase == null || version == null || headers == null || body == null) {
			throw new IllegalArgumentException("Response fields must not be null");
		}
		if (statusCode < 100 || statusCode > 599) {
			throw new IllegalArgumentException("Invalid HTTP status code: " + statusCode);
		}
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.version = version;
		TreeMap<String, String> normalizedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		normalizedHeaders.putAll(headers);
		this.headers = Collections.unmodifiableMap(normalizedHeaders);
		this.body = body.clone();
	}

	public int getStatusCode() { return statusCode; }
	public String getReasonPhrase() { return reasonPhrase; }
	public String getVersion() { return version; }

	public Optional<String> getHeader(String name) {
		return Optional.ofNullable(headers.get(name));
	}

	public Map<String, String> getHeaders() { return headers; }

	public byte[] getBody() { return body.clone(); }

	public boolean isSuccessful() {
		return statusCode >= 200 && statusCode < 300;
	}
}
