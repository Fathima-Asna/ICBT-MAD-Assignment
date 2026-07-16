package com.printxpress.android.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private final com.printxpress.android.data.remote.ApiService apiService;

    public ProductRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void getProducts(String category, MutableLiveData<ApiResponse<List<Product>>> result) {
        apiService.getProducts(category).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(ApiResponse.error("Failed to load products"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void getProduct(String id, MutableLiveData<ApiResponse<Product>> result) {
        apiService.getProduct(id).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(ApiResponse.error("Failed to load product"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }
}
