package com.printxpress.android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.User;
import com.printxpress.android.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<ApiResponse<AuthUser>> authResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<AuthUser>> googleAuthResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<Void>> resetResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<User>> profileResult = new MutableLiveData<>();

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
    }

    public MutableLiveData<ApiResponse<AuthUser>> getAuthResult() {
        return authResult;
    }

    public MutableLiveData<ApiResponse<AuthUser>> getGoogleAuthResult() {
        return googleAuthResult;
    }

    public MutableLiveData<ApiResponse<Void>> getResetResult() {
        return resetResult;
    }

    public MutableLiveData<ApiResponse<User>> getProfileResult() {
        return profileResult;
    }

    public void createUserProfile(User user) {
        authRepository.createUserProfile(user, profileResult);
    }

    public void login(String identifier, String password) {
        if (identifier == null || identifier.isBlank() || password == null || password.isBlank()) {
            authResult.setValue(ApiResponse.error("Identifier and password are required"));
            return;
        }
        if (identifier.contains("@")) {
            authRepository.loginWithEmail(identifier.trim(), password, authResult);
        } else {
            authRepository.loginWithUsername(identifier.trim(), password, authResult);
        }
    }

    public void register(User user, String password) {
        authRepository.register(user, password, authResult);
    }

    public void signInWithGoogle(String idToken) {
        authRepository.signInWithGoogleIdToken(idToken, googleAuthResult);
    }

    public void sendPasswordReset(String email) {
        authRepository.sendPasswordResetEmail(email, resetResult);
    }
}
