package com.example.proxy;

/** Application entry point for the HTTP caching proxy. */
public final class ProxyApplication {
    private ProxyApplication() { }

    public static void main(String[] args) throws Exception {
        ProxyServer server = new ProxyServer(ProxyConfig.fromEnvironmentAndArgs(args));
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown, "proxy-shutdown"));
        server.start();
    }
}