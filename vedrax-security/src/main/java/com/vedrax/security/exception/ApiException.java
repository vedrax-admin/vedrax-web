package com.vedrax.security.exception;

/**
 * General business exception
 *
 * @author remypenchenat
 */
public class ApiException extends RuntimeException {

    /**
     * Constructs an <code>ApiException</code> with the specified
     * message.
     *
     * @param msg the detail message
     */
    public ApiException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>ApiException</code> with the specified
     * message and root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public ApiException(String msg, Throwable t) {
        super(msg, t);
    }
}
