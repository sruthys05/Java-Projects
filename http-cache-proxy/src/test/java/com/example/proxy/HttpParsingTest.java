package com.example.proxy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class HttpParsingTest {
    @Test
    void parsesRequestAndBinaryResponse() throws Exception {
        HttpRequest request = new HttpRequestParser().parse(new ByteArrayInputStream(
                "GET http://Example.com:80/data?x=1 HTTP/1.1\r\nHost: Example.com\r\n\r\n"
                        .getBytes(StandardCharsets.ISO_8859_1)));
        assertEquals("GET:http://example.com:80/data?x=1", CacheKey.from(request).value());

        byte[] body = {0, 1, (byte) 255};
        byte[] wire = ("HTTP/1.1 200 OK\r\nContent-Length: 3\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1);
        byte[] responseBytes = new byte[wire.length + body.length];
        System.arraycopy(wire, 0, responseBytes, 0, wire.length);
        System.arraycopy(body, 0, responseBytes, wire.length, body.length);
        HttpResponse response = new HttpResponseParser(10).parse(new ByteArrayInputStream(responseBytes));
        assertArrayEquals(body, response.getBody());
    }

    @Test
    void parsesChunkedBody() throws Exception {
        String wire = "HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n"
                + "4\r\nWiki\r\n5\r\npedia\r\n0\r\n\r\n";
        HttpResponse response = new HttpResponseParser(1024).parse(new ByteArrayInputStream(wire.getBytes(StandardCharsets.ISO_8859_1)));
        assertEquals(200, response.getStatusCode());
        assertArrayEquals("Wikipedia".getBytes(StandardCharsets.UTF_8), response.getBody());
        assertEquals("chunked", response.getHeader("Transfer-Encoding").orElse(""));
    }

    @Test
    void parsesChunkExtension() throws Exception {
        String wire = "HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n"
                + "4;foo=bar\r\nWiki\r\n0\r\n\r\n";
        HttpResponse response = new HttpResponseParser(1024).parse(new ByteArrayInputStream(wire.getBytes(StandardCharsets.ISO_8859_1)));
        assertArrayEquals("Wiki".getBytes(StandardCharsets.UTF_8), response.getBody());
    }

    @Test
    void rejectsInvalidChunkSize() {
        String wire = "HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n< !DOCTYPE html>\r\n";
        assertThrows(ProxyException.class, () -> new HttpResponseParser(1024).parse(new ByteArrayInputStream(wire.getBytes(StandardCharsets.ISO_8859_1))));
    }

    @Test
    void rejectsMissingCrlfAfterChunk() {
        String wire = "HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n4\r\nWikip";
        assertThrows(ProxyException.class, () -> new HttpResponseParser(1024).parse(new ByteArrayInputStream(wire.getBytes(StandardCharsets.ISO_8859_1))));
    }

    @Test
    void rejectsTruncatedChunk() {
        String wire = "HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n10\r\n1234";
        assertThrows(EOFException.class, () -> new HttpResponseParser(1024).parse(new ByteArrayInputStream(wire.getBytes(StandardCharsets.ISO_8859_1))));
    }

    @Test
    void rejectsOversizedChunkedBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n");
        sb.append("3C\r\n"); // 60 bytes
        for (int i = 0; i < 60; i++) sb.append('a');
        sb.append("0\r\n\r\n");
        assertThrows(ProxyException.class, () -> new HttpResponseParser(50).parse(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.ISO_8859_1))));
    }
}
