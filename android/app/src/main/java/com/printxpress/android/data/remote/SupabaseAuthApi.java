package com.printxpress.android.data.remote;

import com.printxpress.android.data.remote.dto.IdTokenGrantRequest;
import com.printxpress.android.data.remote.dto.PasswordGrantRequest;
import com.printxpress.android.data.remote.dto.RecoverRequest;
import com.printxpress.android.data.remote.dto.RefreshGrantRequest;
import com.printxpress.android.data.remote.dto.SignUpRequest;
import com.printxpress.android.data.remote.dto.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseAuthApi {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    Call<TokenResponse> signUp(@Body SignUpRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token")
    Call<TokenResponse> signInWithPassword(@Query("grant_type") String grantType, @Body PasswordGrantRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token")
    Call<TokenResponse> refreshToken(@Query("grant_type") String grantType, @Body RefreshGrantRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token")
    Call<TokenResponse> signInWithIdToken(@Query("grant_type") String grantType, @Body IdTokenGrantRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/v1/recover")
    Call<Void> recoverPassword(@Body RecoverRequest request);
}
