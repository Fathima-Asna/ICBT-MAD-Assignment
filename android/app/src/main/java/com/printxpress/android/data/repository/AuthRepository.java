package com.printxpress.android.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.User;
import com.printxpress.android.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final com.printxpress.android.data.remote.ApiService apiService;
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.apiService = RetrofitClient.getApiService();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginWithEmail(String email, String password, MutableLiveData<ApiResponse<FirebaseUser>> result) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> result.setValue(ApiResponse.success("Login successful", authResult.getUser())))
                .addOnFailureListener(e -> result.setValue(ApiResponse.error(e.getMessage())));
    }

    public void loginWithUsername(String username, String password, MutableLiveData<ApiResponse<FirebaseUser>> result) {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        apiService.lookupEmail(request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    loginWithEmail(response.body().getData(), password, result);
                } else {
                    result.setValue(ApiResponse.error("Username not found"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void register(User user, String password, MutableLiveData<ApiResponse<FirebaseUser>> result) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        user.setId(firebaseUser.getUid());
                        apiService.createUserProfile(user).enqueue(new Callback<ApiResponse<User>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                                result.setValue(ApiResponse.success("Registration successful", firebaseUser));
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                                result.setValue(ApiResponse.success("Registered but profile save failed", firebaseUser));
                            }
                        });
                    } else {
                        result.setValue(ApiResponse.error("User creation failed"));
                    }
                })
                .addOnFailureListener(e -> result.setValue(ApiResponse.error(e.getMessage())));
    }

    public void sendPasswordResetEmail(String email, MutableLiveData<ApiResponse<Void>> result) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> result.setValue(ApiResponse.success("Password reset email sent", null)))
                .addOnFailureListener(e -> result.setValue(ApiResponse.error(e.getMessage())));
    }

    public void createUserProfile(User user, MutableLiveData<ApiResponse<User>> result) {
        apiService.createUserProfile(user).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(ApiResponse.error("Failed to save profile"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
