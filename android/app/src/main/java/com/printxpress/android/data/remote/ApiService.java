package com.printxpress.android.data.remote;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.CreateOrderRequest;
import com.printxpress.android.data.model.Delivery;
import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.model.Sample;
import com.printxpress.android.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/api/products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("category") String category);

    @GET("/api/products/{id}")
    Call<ApiResponse<Product>> getProduct(@Path("id") String id);

    @POST("/api/orders")
    Call<ApiResponse<PrintOrder>> createOrder(@Body CreateOrderRequest request);

    @GET("/api/orders/user/{userId}")
    Call<ApiResponse<List<PrintOrder>>> getOrdersByUser(@Path("userId") String userId);

    @GET("/api/deliveries")
    Call<ApiResponse<List<Delivery>>> getDeliveries();

    @GET("/api/samples")
    Call<ApiResponse<List<Sample>>> getSamples();

    @POST("/api/users")
    Call<ApiResponse<User>> createUserProfile(@Body User user);

    @GET("/api/users/{id}")
    Call<ApiResponse<User>> getUserProfile(@Path("id") String id);

    @POST("/api/users/lookup-email")
    Call<ApiResponse<String>> lookupEmail(@Body Map<String, String> request);
}
