package com.printxpress.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    private String userId;

    private String name;
    private String type;

    @NotNull(message = "Delivery ID is required")
    private String deliveryId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
}
