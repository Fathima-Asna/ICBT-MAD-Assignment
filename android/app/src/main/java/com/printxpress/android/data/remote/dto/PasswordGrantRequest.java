package com.printxpress.android.data.remote.dto;

public class PasswordGrantRequest {
    private String email;
    private String password;

    public PasswordGrantRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
