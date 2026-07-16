package com.printxpress.android.util;

import android.util.Patterns;

public class ValidationUtils {

    private static final String PHONE_REGEX = "^[+]?[0-9]{10,15}$";

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && phone.matches(PHONE_REGEX);
    }

    public static String validateRegisterInput(String name, String phone, String email,
                                                String address, String password,
                                                String username) {
        if (!isNotEmpty(name)) return "Name is required";
        if (!isValidPhone(phone)) return "Valid phone number is required";
        if (!isValidEmail(email)) return "Valid email is required";
        if (!isNotEmpty(address)) return "Address is required";
        if (!isNotEmpty(password) || password.length() < 6) return "Password must be at least 6 characters";
        if (!isNotEmpty(username)) return "Username is required";
        return null;
    }
}
