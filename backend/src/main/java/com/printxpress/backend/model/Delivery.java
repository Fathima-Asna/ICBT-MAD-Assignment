package com.printxpress.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    private String id;
    private String name;
    private String account;
    private String date;
    private Boolean homeDelivery;
    private Boolean pickupOption;
}
