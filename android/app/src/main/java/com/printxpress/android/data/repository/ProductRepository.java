package com.printxpress.android.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private final SupabaseDataApi dataApi;

    public ProductRepository() {
        this.dataApi = SupabaseClient.getDataApi();
    }

    public void getProducts(String category, MutableLiveData<ApiResponse<List<Product>>> result) {
        String categoryFilter = category != null ? "eq." + category : null;
        dataApi.getProducts(categoryFilter, null, "*").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(ApiResponse.success(response.body()));
                } else {
                    result.setValue(ApiResponse.error("Failed to load products"));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void getProduct(String id, MutableLiveData<ApiResponse<Product>> result) {
        dataApi.getProducts(null, "eq." + id, "*").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(ApiResponse.success(response.body().get(0)));
                } else {
                    result.setValue(ApiResponse.error("Failed to load product"));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }
}
