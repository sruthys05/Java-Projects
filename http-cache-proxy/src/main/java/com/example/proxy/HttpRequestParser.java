package com.example.proxy;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Parses bounded HTTP/1.x requests from a blocking input stream. */
public final class HttpRequestParser {
    private static final int MAX_REQUEST_LINE_BYTES = 8_192;
    private static final int MAX_HEADER_BYTES = 32_768;
    private static final int MAX_BODY_BYTES = 1_048_576;

    public HttpRequest parse(InputStream input) throws IOException {
        String requestLine = readLine(input, MAX_REQUEST_LINE_BYTES);
        if (requestLine == null || requestLine.isBlank()) {
            throw new ProxyException("Missing request line");
        }
        String[] parts = requestLine.split(" ", 3);
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank()
                || !parts[2].startsWith("HTTP/")) {
            throw new ProxyException("Malformed request line");
        }

        Map<String, String> headers = new LinkedHashMap<>();
        int headerBytes = 0;
        while (true) {
            String line = readLine(input, MAX_HEADER_BYTES);
            if (line == null) throw new EOFException("Request ended before headers completed");
            headerBytes += line.getBytes(StandardCharsets.ISO_8859_1).length + 2;
            if (headerBytes > MAX_HEADER_BYTES) throw new ProxyException("Request headers are too large");
            if (line.isEmpty()) break;
            int separator = line.indexOf(':');
            if (separator <= 0) throw new ProxyException("Malformed header");
            String name = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            if (name.isEmpty() || value.contains("\r") || value.contains("\n")) {
                throw new ProxyException("Malformed header");
            }
            String previous = headers.putIfAbsent(name, value);
            if (previous != null) headers.put(name, previous + ", " + value);
        }

        int contentLength = parseContentLength(headers.get("Content-Length"));
        String transferEncoding = getHeader(headers, "Transfer-Encoding");
        if (transferEncoding != null && !transferEncoding.equalsIgnoreCase("identity")) {
            throw new ProxyException("Unsupported transfer encoding");
        }
        if (contentLength > MAX_BODY_BYTES) throw new ProxyException("Request body is too large");
        byte[] body = readFully(input, contentLength);
        return new HttpRequest(parts[0].toUpperCase(Locale.ROOT), parts[1], parts[2], headers, body);
    }

    private static int parseContentLength(String value) {
        if (value == null || value.isBlank()) return 0;
        try {
            long length = Long.parseLong(value.trim());
            if (length < 0 || length > Integer.MAX_VALUE) throw new NumberFormatException();
            return (int) length;
        } catch (NumberFormatException exception) {
            throw new ProxyException("Invalid Content-Length", exception);
        }
    }

    private static String getHeader(Map<String, String> headers, String name) {
        return headers.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue).findFirst().orElse(null);
    }

    static byte[] readFully(InputStream input, int length) throws IOException {
        byte[] result = new byte[length];
        int offset = 0;
        while (offset < length) {
            int count = input.read(result, offset, length - offset);
            if (count < 0) throw new EOFException("Unexpected end of stream");
            if (count == 0) continue;
            offset += count;
        }
        return result;
    }

    static String readLine(InputStream input, int maxBytes) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int previous = -1;
        while (true) {
            int current = input.read();
            if (current < 0) return line.size() == 0 ? null : decodeLine(line);
            if (line.size() >= maxBytes) throw new ProxyException("HTTP line is too large");
            if (previous == '\r' && current == '\n') {
                byte[] bytes = line.toByteArray();
                return new String(bytes, 0, bytes.length - 1, StandardCharsets.ISO_8859_1);
            }
            line.write(current);
            previous = current;
        }
    }

    private static String decodeLine(ByteArrayOutputStream line) {
        return line.toString(StandardCharsets.ISO_8859_1);
    }
}
