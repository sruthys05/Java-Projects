package com.example.proxy;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Parses bounded HTTP/1.x responses, including fixed-length and chunked bodies. */
public final class HttpResponseParser {
    private static final int MAX_STATUS_LINE_BYTES = 8_192;
    private static final int MAX_HEADER_BYTES = 32_768;

    private final long maxResponseSizeBytes;

    public HttpResponseParser(long maxResponseSizeBytes) {
        if (maxResponseSizeBytes <= 0) throw new IllegalArgumentException("Maximum response size must be positive");
        this.maxResponseSizeBytes = maxResponseSizeBytes;
    }

    public HttpResponse parse(InputStream input) throws IOException {
        String statusLine = HttpRequestParser.readLine(input, MAX_STATUS_LINE_BYTES);
        if (statusLine == null) throw new EOFException("Missing response status line");
        String[] parts = statusLine.split(" ", 3);
        if (parts.length < 2 || !parts[0].startsWith("HTTP/")) throw new ProxyException("Malformed status line");
        int statusCode;
        try {
            statusCode = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new ProxyException("Invalid response status code", exception);
        }
        String reason = parts.length == 3 ? parts[2] : "";

        Map<String, String> headers = new LinkedHashMap<>();
        int headerBytes = 0;
        while (true) {
            String line = HttpRequestParser.readLine(input, MAX_HEADER_BYTES);
            if (line == null) throw new EOFException("Response ended before headers completed");
            headerBytes += line.getBytes(StandardCharsets.ISO_8859_1).length + 2;
            if (headerBytes > MAX_HEADER_BYTES) throw new ProxyException("Response headers are too large");
            if (line.isEmpty()) break;
            int separator = line.indexOf(':');
            if (separator <= 0) throw new ProxyException("Malformed response header");
            String name = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            String previous = headers.putIfAbsent(name, value);
            if (previous != null) headers.put(name, previous + ", " + value);
        }

        byte[] body;
        if (statusCode == 204 || statusCode == 304 || (statusCode >= 100 && statusCode < 200)) {
            body = new byte[0];
        } else if (header(headers, "Transfer-Encoding").map(value -> value.toLowerCase().contains("chunked")).orElse(false)) {
            body = readChunked(input);
        } else if (header(headers, "Content-Length").isPresent()) {
            long length = parseLength(header(headers, "Content-Length").get());
            ensureSize(length);
            body = HttpRequestParser.readFully(input, (int) length);
        } else {
            body = readUntilClose(input);
        }
        ProxyLogger.info("ORIGIN_STATUS=" + statusCode);
        header(headers, "Transfer-Encoding").ifPresent(v -> ProxyLogger.info("ORIGIN_TRANSFER_ENCODING=" + v));
        header(headers, "Content-Length").ifPresent(v -> ProxyLogger.info("ORIGIN_CONTENT_LENGTH=" + v));
        ProxyLogger.info("DECODED_BODY_LENGTH=" + body.length);
        return new HttpResponse(statusCode, reason, parts[0], headers, body);
    }

    private byte[] readChunked(InputStream input) throws IOException {
        java.io.ByteArrayOutputStream body = new java.io.ByteArrayOutputStream();
        while (true) {
            String sizeLine = HttpRequestParser.readLine(input, 128);
            if (sizeLine == null) throw new EOFException("Unexpected end of chunked response");
            int semicolon = sizeLine.indexOf(';');
            String sizeText = (semicolon < 0 ? sizeLine : sizeLine.substring(0, semicolon)).trim();
            long chunkSize;
            try {
                chunkSize = Long.parseLong(sizeText, 16);
            } catch (NumberFormatException exception) {
                throw new ProxyException("Invalid chunk size", exception);
            }
            if (chunkSize < 0 || chunkSize > maxResponseSizeBytes - body.size()) {
                throw new ProxyException("Response exceeds maximum size");
            }
            if (chunkSize == 0) {
                while (true) {
                    String trailer = HttpRequestParser.readLine(input, MAX_HEADER_BYTES);
                    if (trailer == null || trailer.isEmpty()) return body.toByteArray();
                }
            }
            body.write(HttpRequestParser.readFully(input, (int) chunkSize));
            String terminator = HttpRequestParser.readLine(input, 2);
            if (terminator == null || !terminator.isEmpty()) throw new ProxyException("Malformed chunk terminator");
        }
    }

    private byte[] readUntilClose(InputStream input) throws IOException {
        java.io.ByteArrayOutputStream body = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8_192];
        int count;
        while ((count = input.read(buffer)) >= 0) {
            if (count == 0) continue;
            if (body.size() > maxResponseSizeBytes - count) throw new ProxyException("Response exceeds maximum size");
            body.write(buffer, 0, count);
        }
        return body.toByteArray();
    }

    private void ensureSize(long length) {
        if (length < 0 || length > maxResponseSizeBytes || length > Integer.MAX_VALUE) {
            throw new ProxyException("Invalid or oversized response length");
        }
    }

    private static long parseLength(String value) {
        try {
            long length = Long.parseLong(value.trim());
            if (length < 0) throw new NumberFormatException();
            return length;
        } catch (NumberFormatException exception) {
            throw new ProxyException("Invalid Content-Length", exception);
        }
    }

    private static java.util.Optional<String> header(Map<String, String> headers, String name) {
        return headers.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue).findFirst();
    }
}
