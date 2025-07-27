package org.example.diplom.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenMessage {
    @JsonProperty("auth-token")
    private String authToken;

    public TokenMessage(){}

    public TokenMessage(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
