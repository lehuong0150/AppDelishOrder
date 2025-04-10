package com.example.appdelishorder.Model;

public class TokenResponse {
    private String token;   // Token xác thực (ví dụ JWT token)
    private String message; // Thông điệp bổ sung, thường để mô tả trạng thái hoặc thông báo lỗi

    // Constructor, getter và setter
    public TokenResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
