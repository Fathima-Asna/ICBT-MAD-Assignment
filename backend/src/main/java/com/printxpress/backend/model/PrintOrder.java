package com.printxpress.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintOrder {
    private String id;
    private String name;
    private String type;
    private Double totalAmount;
    private String status;
    private String userId;
    private String deliveryId;
    private List<OrderItem> orderItems;
}
