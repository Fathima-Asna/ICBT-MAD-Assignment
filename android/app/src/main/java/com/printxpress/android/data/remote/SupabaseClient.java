package com.printxpress.android.data.remote;

import com.google.gson.Gson;
import com.printxpress.android.data.remote.dto.TokenResponse;
import com.printxpress.android.util.SessionManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    // Project Settings -> API in the Supabase dashboard.
    public static final String SUPABASE_URL = "https://rydpgctypusymxsuouqs.supabase.co/";
    // anon/public key only - NEVER put the service_role key in client code, it bypasses all security rules.
    public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ5ZHBnY3R5cHVzeW14c3VvdXFzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODQyNzIwNDgsImV4cCI6MjA5OTg0ODA0OH0.j5gJEIGJmNJx9xRAeiYu6pzykKmzFHLvUWSnmbQRjU8";

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final Gson gson = new Gson();

    private static Retrofit retrofit;
    private static OkHttpClient plainClient;

    private static synchronized OkHttpClient plainClient() {
        if (plainClient == null) {
            plainClient = new OkHttpClient.Builder().build();
        }
        return plainClient;
    }

    /** Synchronously exchanges the stored refresh token for a new access token. Returns true on success. */
    private static synchronized boolean refreshSession() {
        String refreshToken = SessionManager.getInstance().getRefreshToken();
        if (refreshToken == null) return false;

        String body = "{\"refresh_token\":\"" + refreshToken + "\"}";
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "auth/v1/token?grant_type=refresh_token")
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body, JSON))
                .build();

        try (Response response = plainClient().newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                SessionManager.getInstance().clearSession();
                return false;
            }
            TokenResponse token = gson.fromJson(response.body().string(), TokenResponse.class);
            if (token == null || !token.hasSession()) {
                SessionManager.getInstance().clearSession();
                return false;
            }
            SessionManager.getInstance().saveSession(
                    token.getAccessToken(), token.getRefreshToken(), token.getExpiresIn(),
                    token.getUser().getId(), token.getUser().getEmail());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static Interceptor authInterceptor() {
        return chain -> {
            SessionManager session = SessionManager.getInstance();
            if (session.isLoggedIn() && session.isAccessTokenExpired()) {
                refreshSession();
            }
            String token = session.isLoggedIn() ? session.getAccessToken() : SUPABASE_ANON_KEY;
            Request request = chain.request().newBuilder()
                    .header("apikey", SUPABASE_ANON_KEY)
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        };
    }

    /** Fallback for the rare case a token is rejected server-side despite looking valid locally. */
    private static Authenticator tokenAuthenticator() {
        return (route, response) -> {
            if (response.request().header("X-Retry-Auth") != null) {
                return null; // already retried once, give up
            }
            if (!SessionManager.getInstance().isLoggedIn() || !refreshSession()) {
                return null;
            }
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getAccessToken())
                    .header("X-Retry-Auth", "1")
                    .build();
        };
    }

    public static synchronized Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor())
                    .authenticator(tokenAuthenticator())
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static SupabaseAuthApi getAuthApi() {
        return getRetrofit().create(SupabaseAuthApi.class);
    }

    public static SupabaseDataApi getDataApi() {
        return getRetrofit().create(SupabaseDataApi.class);
    }
}
