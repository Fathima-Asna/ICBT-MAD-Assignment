package com.printxpress.android.data.remote.dto;

public class SignUpRequest {
    private String email;
    private String password;

    public SignUpRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
