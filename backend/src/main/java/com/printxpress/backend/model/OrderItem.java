package com.printxpress.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private String id;
    private Integer quantity;
    private Double price;
    private String designUrl;
    private String customText;
    private String productId;
    private String productName;
}
