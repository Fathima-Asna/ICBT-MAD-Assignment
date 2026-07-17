package com.printxpress.android.data.remote.dto;

public class SupabaseErrorResponse {
    private String msg;
    private String message;
    private String error_description;
    private String error;

    public String resolveMessage() {
        if (msg != null) return msg;
        if (message != null) return message;
        if (error_description != null) return error_description;
        if (error != null) return error;
        return null;
    }
}
