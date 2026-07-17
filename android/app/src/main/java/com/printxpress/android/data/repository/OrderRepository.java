package com.printxpress.android.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.CreateOrderRequest;
import com.printxpress.android.data.model.OrderItem;
import com.printxpress.android.data.model.OrderItemRequest;
import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final SupabaseDataApi dataApi;

    public OrderRepository() {
        this.dataApi = SupabaseClient.getDataApi();
    }

    public void createOrder(CreateOrderRequest request, MutableLiveData<ApiResponse<PrintOrder>> result) {
        buildOrderItems(request, 0, new ArrayList<>(), 0.0, result);
    }

    private void buildOrderItems(CreateOrderRequest request, int index, List<OrderItem> items,
                                  double runningTotal, MutableLiveData<ApiResponse<PrintOrder>> result) {
        List<OrderItemRequest> requests = request.getItems();
        if (requests == null || index >= requests.size()) {
            submitOrder(request, items, runningTotal, result);
            return;
        }
        OrderItemRequest req = requests.get(index);
        if (req.getQuantity() == null || req.getQuantity() < 1) {
            result.setValue(ApiResponse.error("Quantity must be positive"));
            return;
        }
        dataApi.getProducts(null, "eq." + req.getProductId(), "*").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    result.setValue(ApiResponse.error("Product not found: " + req.getProductId()));
                    return;
                }
                Product product = response.body().get(0);
                if (product.getBasePrice() == null) {
                    result.setValue(ApiResponse.error("Product is missing a price: " + req.getProductId()));
                    return;
                }
                double price = product.getBasePrice() * req.getQuantity();
                OrderItem item = new OrderItem();
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setQuantity(req.getQuantity());
                item.setPrice(price);
                item.setDesignUrl(req.getDesignUrl());
                item.setCustomText(req.getCustomText());
                items.add(item);
                buildOrderItems(request, index + 1, items, runningTotal + price, result);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    private void submitOrder(CreateOrderRequest request, List<OrderItem> items, double total,
                              MutableLiveData<ApiResponse<PrintOrder>> result) {
        PrintOrder order = new PrintOrder();
        order.setUserId(request.getUserId());
        order.setDeliveryId(request.getDeliveryId());
        order.setName(request.getName());
        order.setType(request.getType());
        order.setStatus("PENDING");
        order.setTotalAmount(total);
        order.setOrderItems(items);
        order.setPaperType(request.getPaperType());
        order.setSize(request.getSize());
        order.setCustomText(request.getCustomText());
        order.setDesignUrl(request.getDesignUrl());
        order.setPickupTime(request.getPickupTime());

        dataApi.createOrder(order).enqueue(new Callback<List<PrintOrder>>() {
            @Override
            public void onResponse(Call<List<PrintOrder>> call, Response<List<PrintOrder>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(ApiResponse.success(response.body().get(0)));
                } else {
                    result.setValue(ApiResponse.error("Failed to create order"));
                }
            }

            @Override
            public void onFailure(Call<List<PrintOrder>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }

    public void getOrdersByUser(String userId, MutableLiveData<ApiResponse<List<PrintOrder>>> result) {
        dataApi.getOrders("eq." + userId, "*", "createdAt.desc").enqueue(new Callback<List<PrintOrder>>() {
            @Override
            public void onResponse(Call<List<PrintOrder>> call, Response<List<PrintOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(ApiResponse.success(response.body()));
                } else {
                    result.setValue(ApiResponse.error("Failed to load orders"));
                }
            }

            @Override
            public void onFailure(Call<List<PrintOrder>> call, Throwable t) {
                result.setValue(ApiResponse.error(t.getMessage()));
            }
        });
    }
}
