package com.printxpress.android.data.remote.dto;

public class IdTokenGrantRequest {
    private String provider;
    private String id_token;

    public IdTokenGrantRequest(String provider, String idToken) {
        this.provider = provider;
        this.id_token = idToken;
    }
}
