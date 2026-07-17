package com.printxpress.android.data.model;

public class AuthUser {
    private final String uid;
    private final String email;

    public AuthUser(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}
