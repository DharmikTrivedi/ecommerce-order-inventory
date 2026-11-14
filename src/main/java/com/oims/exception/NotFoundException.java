package com.oims.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String what, Object id) {
        super(what + " with id " + id + " was not found");
    }
}
