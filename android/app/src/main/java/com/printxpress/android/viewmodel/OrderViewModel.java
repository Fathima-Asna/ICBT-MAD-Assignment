package com.printxpress.android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.printxpress.android.data.model.ApiResponse;
import com.printxpress.android.data.model.CreateOrderRequest;
import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final MutableLiveData<ApiResponse<PrintOrder>> orderResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<List<PrintOrder>>> userOrders = new MutableLiveData<>();

    public OrderViewModel() {
        this.orderRepository = new OrderRepository();
    }

    public MutableLiveData<ApiResponse<PrintOrder>> getOrderResult() {
        return orderResult;
    }

    public MutableLiveData<ApiResponse<List<PrintOrder>>> getUserOrders() {
        return userOrders;
    }

    public void createOrder(CreateOrderRequest request) {
        orderRepository.createOrder(request, orderResult);
    }

    public void loadUserOrders(String userId) {
        orderRepository.getOrdersByUser(userId, userOrders);
    }
}
