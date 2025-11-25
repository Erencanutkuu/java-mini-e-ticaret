package com.example.minieticaret.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank @Size(max = 200) String line1,
        @Size(max = 200) String line2,
        @NotBlank @Size(max = 120) String city,
        @NotBlank @Size(max = 120) String country,
        @NotBlank @Size(max = 20) String zip,
        @Size(max = 30) String phone
) {
}
