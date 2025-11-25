package com.example.minieticaret.customer.dto;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String line1,
        String line2,
        String city,
        String country,
        String zip,
        String phone
) {
}
