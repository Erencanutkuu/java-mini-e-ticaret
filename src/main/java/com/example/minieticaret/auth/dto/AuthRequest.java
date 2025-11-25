package com.example.minieticaret.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @Email(message = "Gecerli bir email giriniz")
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, message = "Sifre en az 6 karakter olmalidir")
        String password
) {
}
