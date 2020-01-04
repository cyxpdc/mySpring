package com.pdc.plugin.security.exception;

/**
 * 认证异常（当非法访问时抛出）
 * @author pdc
 */
public class AuthcException extends Exception {

    public AuthcException() {
        super();
    }

    public AuthcException(String message) {
        super(message);
    }

    public AuthcException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthcException(Throwable cause) {
        super(cause);
    }
}
