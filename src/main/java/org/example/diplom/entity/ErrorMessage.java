package org.example.diplom.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ErrorMessage {
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String message;

    public ErrorMessage(){}

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getId() {
        return id;
    }
}
