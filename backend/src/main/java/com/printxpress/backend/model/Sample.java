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
public class Sample {
    private String id;
    private String name;
    private String designUrl;
    private String userId;
    private String adminId;
    private List<String> productIds;
}
