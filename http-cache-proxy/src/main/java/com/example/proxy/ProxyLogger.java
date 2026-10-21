package com.example.proxy;

import java.util.logging.Logger;

/** Small structured logging facade that avoids sensitive request contents. */
public final class ProxyLogger {
    private static final Logger LOGGER = Logger.getLogger("com.example.proxy");

    private ProxyLogger() { }
    public static void info(String message) { LOGGER.info(message); }
    public static void warning(String message, Throwable cause) { LOGGER.warning(message + ": " + cause.getMessage()); }
}