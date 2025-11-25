package com.example.minieticaret.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.ratelimit")
public record RateLimitProperties(
        @DefaultValue("100") int limitPerMinute
) {
}
