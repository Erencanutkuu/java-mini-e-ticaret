package com.example.minieticaret.auth.service;

import com.example.minieticaret.auth.dto.AuthRequest;
import com.example.minieticaret.auth.dto.AuthResponse;
import com.example.minieticaret.auth.dto.RefreshRequest;
import com.example.minieticaret.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    AuthResponse refresh(RefreshRequest request);
}
