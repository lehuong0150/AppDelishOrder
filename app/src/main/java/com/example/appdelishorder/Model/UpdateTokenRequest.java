package com.example.appdelishorder.Model;

public class UpdateTokenRequest {
    private String email;
    private String token;

    public UpdateTokenRequest(String email, String token) {
        this.email = email;
        this.token = token;
    }
    // Getter và setter cho email và token

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}