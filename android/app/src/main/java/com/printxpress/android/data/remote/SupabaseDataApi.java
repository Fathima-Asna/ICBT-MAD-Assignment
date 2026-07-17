package com.printxpress.android.data.remote;

import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.model.User;
import com.printxpress.android.data.model.SavedDesign;
import com.printxpress.android.data.model.Sample;
import com.printxpress.android.data.model.Promotion;
import com.printxpress.android.data.model.ResourceItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
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

    @Headers({"Content-Type: application/json", "Prefer: resolution=merge-duplicates,return=representation"})
    @POST("rest/v1/products")
    Call<List<Product>> upsertProduct(@Query("on_conflict") String onConflict, @Body Product product);

    @DELETE("rest/v1/products")
    Call<Void> deleteProduct(@Query("id") String idFilter);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/print_orders")
    Call<List<PrintOrder>> createOrder(@Body PrintOrder order);

    @GET("rest/v1/print_orders")
    Call<List<PrintOrder>> getOrders(@Query("userId") String userIdFilter,
                                      @Query("select") String select,
                                      @Query("order") String order);

    @GET("rest/v1/print_orders")
    Call<List<PrintOrder>> getAllOrders(@Query("select") String select,
                                        @Query("order") String order);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @PATCH("rest/v1/print_orders")
    Call<List<PrintOrder>> updateOrderStatus(@Query("id") String idFilter, @Body Map<String, Object> update);

    @DELETE("rest/v1/print_orders")
    Call<Void> deleteOrder(@Query("id") String idFilter);

    @GET("rest/v1/saved_designs")
    Call<List<SavedDesign>> getSavedDesigns(@Query("userId") String userIdFilter, @Query("select") String select);

    @Headers({"Content-Type: application/json", "Prefer: return=representation"})
    @POST("rest/v1/saved_designs")
    Call<List<SavedDesign>> createSavedDesign(@Body SavedDesign design);

    @DELETE("rest/v1/saved_designs")
    Call<Void> deleteSavedDesign(@Query("id") String idFilter);

    @GET("rest/v1/samples")
    Call<List<Sample>> getSamples(@Query("select") String select);

    @Headers({"Content-Type: application/json", "Prefer: resolution=merge-duplicates,return=representation"})
    @POST("rest/v1/samples")
    Call<List<Sample>> upsertSample(@Query("on_conflict") String onConflict, @Body Sample sample);

    @DELETE("rest/v1/samples")
    Call<Void> deleteSample(@Query("id") String idFilter);

    @GET("rest/v1/promotions")
    Call<List<Promotion>> getPromotions(@Query("select") String select);

    @Headers({"Content-Type: application/json", "Prefer: resolution=merge-duplicates,return=representation"})
    @POST("rest/v1/promotions")
    Call<List<Promotion>> upsertPromotion(@Query("on_conflict") String onConflict, @Body Promotion promotion);

    @DELETE("rest/v1/promotions")
    Call<Void> deletePromotion(@Query("id") String idFilter);

    @GET("rest/v1/resources")
    Call<List<ResourceItem>> getResources(@Query("category") String categoryFilter, @Query("select") String select);

    @Headers({"Content-Type: application/json", "Prefer: resolution=merge-duplicates,return=representation"})
    @POST("rest/v1/resources")
    Call<List<ResourceItem>> upsertResource(@Query("on_conflict") String onConflict, @Body ResourceItem resource);

    @DELETE("rest/v1/resources")
    Call<Void> deleteResource(@Query("id") String idFilter);
}
