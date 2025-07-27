package org.example.diplom.exception;

public class BadCredentials extends RuntimeException {
    public BadCredentials(String message) {
        super(message);
    }
}
