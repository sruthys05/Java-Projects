package com.example.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ResponseWriterTest {

    @Test
    void stripsTransferEncodingAndAddsContentLength() throws Exception {
        HttpResponse response = new HttpResponse(200, "OK", "HTTP/1.1",
                Map.of("Transfer-Encoding", "chunked", "Content-Type", "text/html"),
                "<!doctype html>".getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ClientHandler.sendResponse(out, response, "GET");

        String wire = out.toString(StandardCharsets.ISO_8859_1);
        assertFalse(wire.contains("Transfer-Encoding: chunked"), "Should not forward Transfer-Encoding");
        assertTrue(wire.contains("Content-Length: 15"), "Should contain correct Content-Length");
        assertTrue(wire.contains("Connection: close"), "Should contain Connection: close");
        assertTrue(wire.endsWith("<!doctype html>"), "Body should appear exactly once");
    }

    @Test
    void preservesSafeHeaders() throws Exception {
        HttpResponse response = new HttpResponse(200, "OK", "HTTP/1.1",
                Map.of(
                    "Content-Type", "text/html",
                    "Cache-Control", "max-age=3600",
                    "ETag", "\"abc\""
                ),
                new byte[] {1, 2, 3});

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ClientHandler.sendResponse(out, response, "GET");

        String wire = out.toString(StandardCharsets.ISO_8859_1);
        assertTrue(wire.contains("Content-Type: text/html"));
        assertTrue(wire.contains("Cache-Control: max-age=3600"));
        assertTrue(wire.contains("ETag: \"abc\""));
        assertTrue(wire.contains("Content-Length: 3"));
    }

    @Test
    void doesNotSendBodyForHead() throws Exception {
        HttpResponse response = new HttpResponse(200, "OK", "HTTP/1.1",
                Map.of(),
                "secret".getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ClientHandler.sendResponse(out, response, "HEAD");

        String wire = out.toString(StandardCharsets.ISO_8859_1);
        assertFalse(wire.contains("secret"), "HEAD must not include body");
        assertTrue(wire.contains("Content-Length: 6"));
    }
}