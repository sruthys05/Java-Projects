package com.example.proxy;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Immutable HTTP request representation. */
public class HttpRequest {
	private final String method;
	private final String target;
	private final String version;
	private final Map<String, String> headers;
	private final byte[] body;

	public HttpRequest(String method, String target, String version,
					   Map<String, String> headers, byte[] body) {
		if (method == null || target == null || version == null || headers == null || body == null) {
			throw new IllegalArgumentException("Request fields must not be null");
		}
		this.method = method;
		this.target = target;
		this.version = version;
		TreeMap<String, String> normalizedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		normalizedHeaders.putAll(headers);
		this.headers = Collections.unmodifiableMap(normalizedHeaders);
		this.body = body.clone();
	}

	public String getMethod() { return method; }
	public String getTarget() { return target; }
	public String getVersion() { return version; }

	public Optional<String> getHeader(String name) {
		return Optional.ofNullable(headers.get(name));
	}

	public Map<String, String> getHeaders() { return headers; }

	public byte[] getBody() { return body.clone(); }
}
