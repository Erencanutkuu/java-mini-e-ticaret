package com.example.minieticaret.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        @DefaultValue("3600000") long accessTokenValidityMs,
        @DefaultValue("86400000") long refreshTokenValidityMs,
        @DefaultValue("mini-e-ticaret") String issuer
) {
}
