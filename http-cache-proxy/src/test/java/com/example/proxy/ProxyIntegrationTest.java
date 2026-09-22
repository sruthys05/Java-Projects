package com.example.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ProxyIntegrationTest {

    @Test
    void proxyReturnsFixedLengthForChunkedOrigin() throws Exception {
        ServerSocket originServer = new ServerSocket(0);
        int originPort = originServer.getLocalPort();

        Thread originThread = new Thread(() -> {
            try (Socket client = originServer.accept()) {
                client.setSoTimeout(2000);
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                // Consume request
                ByteArrayOutputStream request = new ByteArrayOutputStream();
                int b;
                while ((b = in.read()) != -1) {
                    request.write(b);
                    if (request.size() >= 4) {
                        byte[] data = request.toByteArray();
                        if (data.length >= 4 &&
                            data[data.length - 4] == '\r' && data[data.length - 3] == '\n' &&
                            data[data.length - 2] == '\r' && data[data.length - 1] == '\n') {
                            break;
                        }
                    }
                }

                String response = "HTTP/1.1 200 OK\r\n" +
                        "Transfer-Encoding: chunked\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n" +
                        "4\r\n" +
                        "Wiki\r\n" +
                        "5\r\n" +
                        "pedia\r\n" +
                        "0\r\n" +
                        "\r\n";
                out.write(response.getBytes(StandardCharsets.ISO_8859_1));
                out.flush();
            } catch (IOException e) {
                // ignore
            }
        });
        originThread.start();

        // Fetch from origin
        ProxyConfig config = new ProxyConfig(
                8080, 2, 60, 10, 1024,
                1000, 1000, 1000, 60
        );
        OriginServerClient originClient = new OriginServerClient(config);
        HttpRequest request = new HttpRequest("GET", "http://localhost:" + originPort + "/", "HTTP/1.1",
                Map.of("Host", "localhost:" + originPort), new byte[0]);
        HttpResponse response = originClient.fetch(request);

        ByteArrayOutputStream clientOut = new ByteArrayOutputStream();
        ClientHandler.sendResponse(clientOut, response, "GET");

        String wire = clientOut.toString(StandardCharsets.ISO_8859_1);

        assertTrue(wire.startsWith("HTTP/1.1 200 OK"), "Should have correct status line");
        assertFalse(wire.contains("Transfer-Encoding:"), "Should not forward Transfer-Encoding");
        assertTrue(wire.contains("Content-Length: 9"), "Should contain correct Content-Length");
        assertTrue(wire.contains("Connection: close"), "Should contain Connection: close");
        assertTrue(wire.endsWith("Wikipedia"), "Body should appear exactly once at the end");

        originServer.close();
    }
}