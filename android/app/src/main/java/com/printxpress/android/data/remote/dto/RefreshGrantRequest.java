package com.printxpress.android.data.remote.dto;

public class RefreshGrantRequest {
    private String refresh_token;

    public RefreshGrantRequest(String refreshToken) {
        this.refresh_token = refreshToken;
    }
}
