package com.printxpress.android.data.remote.dto;

public class TokenResponse {
    private String access_token;
    private String token_type;
    private Long expires_in;
    private String refresh_token;
    private SupabaseAuthUser user;

    public String getAccessToken() {
        return access_token;
    }

    public Long getExpiresIn() {
        return expires_in;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public SupabaseAuthUser getUser() {
        return user;
    }

    public boolean hasSession() {
        return access_token != null && user != null;
    }
}
