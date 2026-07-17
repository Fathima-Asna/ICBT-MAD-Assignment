package com.printxpress.android.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.User;
import com.printxpress.android.data.remote.SupabaseAuthApi;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;
import com.printxpress.android.data.remote.dto.IdTokenGrantRequest;
import com.printxpress.android.data.remote.dto.PasswordGrantRequest;
import com.printxpress.android.data.remote.dto.RecoverRequest;
import com.printxpress.android.data.remote.dto.SignUpRequest;
import com.printxpress.android.data.remote.dto.SupabaseErrorResponse;
import com.printxpress.android.data.remote.dto.TokenResponse;
import com.printxpress.android.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    // Shown when Supabase's "Confirm email" setting is on, so signup doesn't return a session.
    // RegisterActivity matches on this exact message to route the user to the login screen
    // instead of treating it like an ordinary registration failure.
    public static final String EMAIL_CONFIRMATION_REQUIRED =
            "Account created! Check your email to confirm your account, then log in.";

    private final SupabaseAuthApi authApi;
    private final SupabaseDataApi dataApi;
    private final Gson gson = new Gson();

    public AuthRepository() {
        this.authApi = SupabaseClient.getAuthApi();
        this.dataApi = SupabaseClient.getDataApi();
    }

    public void loginWithEmail(String email, String password, MutableLiveData<ApiResponse<AuthUser>> result) {
        authApi.signInWithPassword("password", new PasswordGrantRequest(email, password)).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().hasSession()) {
                    TokenResponse token = response.body();
                    SessionManager.getInstance().saveSession(token.getAccessToken(), token.getRefreshToken(),
                            token.getExpiresIn(), token.getUser().getId(), token.getUser().getEmail());
                    result.setValue(ApiResponse.success("Login successful",
                            new AuthUser(token.getUser().getId(), token.getUser().getEmail())));
                } else {
                    result.setValue(ApiResponse.error(resolveError(response, "Invalid credentials")));
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void loginWithUsername(String username, String password, MutableLiveData<ApiResponse<AuthUser>> result) {
        dataApi.findProfiles("eq." + username, null, "email").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    loginWithEmail(response.body().get(0).getEmail(), password, result);
                } else {
                    result.setValue(ApiResponse.error("Username not found"));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void register(User user, String password, MutableLiveData<ApiResponse<AuthUser>> result) {
        authApi.signUp(new SignUpRequest(user.getEmail(), password)).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getUser() == null) {
                    result.setValue(ApiResponse.error(resolveError(response, "Registration failed")));
                    return;
                }
                TokenResponse token = response.body();
                if (!token.hasSession()) {
                    result.setValue(ApiResponse.error(EMAIL_CONFIRMATION_REQUIRED));
                    return;
                }
                SessionManager.getInstance().saveSession(token.getAccessToken(), token.getRefreshToken(),
                        token.getExpiresIn(), token.getUser().getId(), token.getUser().getEmail());
                AuthUser authUser = new AuthUser(token.getUser().getId(), token.getUser().getEmail());

                user.setId(token.getUser().getId());
                dataApi.upsertProfile("id", user).enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        result.setValue(ApiResponse.success("Registration successful", authUser));
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        result.setValue(ApiResponse.error("Account created, but profile could not be saved. Check your connection and try logging in."));
                    }
                });
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void signInWithGoogleIdToken(String idToken, MutableLiveData<ApiResponse<AuthUser>> result) {
        authApi.signInWithIdToken("id_token", new IdTokenGrantRequest("google", idToken)).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().hasSession()) {
                    TokenResponse token = response.body();
                    SessionManager.getInstance().saveSession(token.getAccessToken(), token.getRefreshToken(),
                            token.getExpiresIn(), token.getUser().getId(), token.getUser().getEmail());
                    result.setValue(ApiResponse.success("Login successful",
                            new AuthUser(token.getUser().getId(), token.getUser().getEmail())));
                } else {
                    result.setValue(ApiResponse.error(resolveError(response, "Google sign-in failed")));
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void sendPasswordResetEmail(String email, MutableLiveData<ApiResponse<Void>> result) {
        authApi.recoverPassword(new RecoverRequest(email)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(ApiResponse.success("Password reset email sent", null));
                } else {
                    result.setValue(ApiResponse.error(resolveError(response, "Failed to send reset email")));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void createUserProfile(User user, MutableLiveData<ApiResponse<User>> result) {
        dataApi.upsertProfile("id", user).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(ApiResponse.success(response.body().get(0)));
                } else {
                    result.setValue(ApiResponse.error("Failed to save profile"));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public AuthUser getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
    }

    private <T> String resolveError(Response<T> response, String fallback) {
        if (response.errorBody() != null) {
            try {
                SupabaseErrorResponse error = gson.fromJson(response.errorBody().string(), SupabaseErrorResponse.class);
                if (error != null && error.resolveMessage() != null) {
                    return error.resolveMessage();
                }
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }
}
