package com.printxpress.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    private String id;
    private String name;
    private String mail;
    private String contact;
    private Integer age;
    private String address;
}
