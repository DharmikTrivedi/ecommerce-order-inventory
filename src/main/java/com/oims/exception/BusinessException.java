package com.oims.exception;

/**
 * Thrown when a business rule is broken (not enough stock, wrong order status etc).
 * Controllers catch it and show the message to the user.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
