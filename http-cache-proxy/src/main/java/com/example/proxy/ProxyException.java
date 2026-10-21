package com.example.proxy;

/** Base unchecked exception for proxy processing failures. */
public class ProxyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}
