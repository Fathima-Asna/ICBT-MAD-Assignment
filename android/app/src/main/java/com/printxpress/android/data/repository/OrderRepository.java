package com.printxpress.android.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.CreateOrderRequest;
import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final com.printxpress.android.data.remote.ApiService apiService;

    public OrderRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void createOrder(CreateOrderRequest request, MutableLiveData<ApiResponse<PrintOrder>> result) {
        apiService.createOrder(request).enqueue(new Callback<ApiResponse<PrintOrder>>() {
            @Override
            public void onResponse(Call<ApiResponse<PrintOrder>> call, Response<ApiResponse<PrintOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(ApiResponse.error("Failed to create order"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PrintOrder>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void getOrdersByUser(String userId, MutableLiveData<ApiResponse<List<PrintOrder>>> result) {
        apiService.getOrdersByUser(userId).enqueue(new Callback<ApiResponse<List<PrintOrder>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PrintOrder>>> call, Response<ApiResponse<List<PrintOrder>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(ApiResponse.error("Failed to load orders"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PrintOrder>>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }
}
