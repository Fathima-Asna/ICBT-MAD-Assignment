package com.printxpress.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private String id;
    private String category;
    private String name;
    private Double basePrice;
    private String specs;
    private String type;
    private String color;
    private String weight;
}
