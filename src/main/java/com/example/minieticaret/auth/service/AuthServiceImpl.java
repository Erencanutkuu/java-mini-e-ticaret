package com.example.minieticaret.auth.service;

import com.example.minieticaret.auth.domain.Role;
import com.example.minieticaret.auth.domain.RoleName;
import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.auth.domain.UserStatus;
import com.example.minieticaret.auth.dto.AuthRequest;
import com.example.minieticaret.auth.dto.AuthResponse;
import com.example.minieticaret.auth.dto.RefreshRequest;
import com.example.minieticaret.auth.dto.RegisterRequest;
import com.example.minieticaret.auth.repository.RoleRepository;
import com.example.minieticaret.auth.repository.UserRepository;
import com.example.minieticaret.common.exception.ApiErrorCode;
import com.example.minieticaret.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email kullaniliyor");
        }

        Role userRole = findRole(RoleName.USER);

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .status(UserStatus.ACTIVE)
                .roles(Set.of(userRole))
                .build();

        User saved = userRepository.save(user);
        return buildTokens(saved);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Kimlik dogrulama basarisiz: " + ex.getMessage());
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Kullanici bulunamadi"));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new BusinessException(ApiErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, "Kullanici aktif degil");
        }

        return buildTokens(user);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isRefreshToken(token)) {
            throw new IllegalArgumentException("Gecersiz refresh token");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanici bulunamadi"));

        if (!jwtService.isTokenValid(token, user)) {
            throw new IllegalArgumentException("Gecersiz veya suresi dolmus token");
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new BusinessException(ApiErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, "Kullanici aktif degil");
        }

        return buildTokens(user);
    }

    private AuthResponse buildTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.bearer(accessToken, refreshToken);
    }

    private Role findRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException(name + " rolu bulunamadi"));
    }
}
