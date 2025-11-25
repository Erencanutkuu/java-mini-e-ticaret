package com.example.minieticaret.customer.controller;

import com.example.minieticaret.customer.dto.AddressRequest;
import com.example.minieticaret.customer.dto.AddressResponse;
import com.example.minieticaret.customer.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer/addresses")
@Tag(name = "Customer - Addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Kullanıcı adreslerini listele")
    public ResponseEntity<List<AddressResponse>> list(@AuthenticationPrincipal(expression = "user.id") UUID userId) {
        return ResponseEntity.ok(addressService.list(userId));
    }

    @PostMapping
    @Operation(summary = "Adres oluştur")
    public ResponseEntity<AddressResponse> create(@AuthenticationPrincipal(expression = "user.id") UUID userId,
                                                  @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(request, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Adres güncelle")
    public ResponseEntity<AddressResponse> update(@PathVariable UUID id,
                                                  @AuthenticationPrincipal(expression = "user.id") UUID userId,
                                                  @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Adres sil")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal(expression = "user.id") UUID userId) {
        addressService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
