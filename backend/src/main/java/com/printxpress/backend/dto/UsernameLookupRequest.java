package com.printxpress.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsernameLookupRequest {
    @NotBlank(message = "Username is required")
    private String username;
}
