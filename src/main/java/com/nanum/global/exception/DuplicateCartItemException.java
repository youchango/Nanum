package com.nanum.global.exception;

public class DuplicateCartItemException extends RuntimeException {
    public DuplicateCartItemException(String message) {
        super(message);
    }
}
