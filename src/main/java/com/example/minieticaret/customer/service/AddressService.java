package com.example.minieticaret.customer.service;

import com.example.minieticaret.customer.dto.AddressRequest;
import com.example.minieticaret.customer.dto.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse create(AddressRequest request, UUID userId);

    AddressResponse update(UUID id, AddressRequest request, UUID userId);

    void delete(UUID id, UUID userId);

    List<AddressResponse> list(UUID userId);
}
