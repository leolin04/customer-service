package com.cat.customerservice.exception;

public class NoMoreContentException extends RuntimeException {
    public NoMoreContentException() {}

    public NoMoreContentException(String message) {
        super(message);
    }

    public NoMoreContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMoreContentException(Throwable cause) {
        super(cause);
    }
}
