package com.printxpress.android.data.remote;

import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseDataApi {

    @GET("rest/v1/profiles")
    Call<List<User>> findProfiles(@Query("username") String usernameFilter,
                                   @Query("id") String idFilter,
                                   @Query("select") String select);

    @Headers({"Content-Type: application/json", "Prefer: resolution=merge-duplicates,return=representation"})
    @POST("rest/v1/profiles")
    Call<List<User>> upsertProfile(@Query("on_conflict") String onConflict, @Body User profile);

    @GET("rest/v1/products")
    Call<List<Product>> getProducts(@Query("category") String categoryFilter,
                                     @Query("id") String idFilter,
                                     @Query("select") String select);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/print_orders")
    Call<List<PrintOrder>> createOrder(@Body PrintOrder order);

    @GET("rest/v1/print_orders")
    Call<List<PrintOrder>> getOrders(@Query("userId") String userIdFilter,
                                      @Query("select") String select,
                                      @Query("order") String order);
}
