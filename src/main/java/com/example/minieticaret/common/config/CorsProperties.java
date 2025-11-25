package com.example.minieticaret.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        @DefaultValue({"*"})
        List<String> allowedOrigins,
        @DefaultValue({"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"})
        List<String> allowedMethods,
        @DefaultValue({"Authorization", "Content-Type"})
        List<String> allowedHeaders
) {
}
