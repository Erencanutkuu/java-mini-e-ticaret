package com.example.minieticaret.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mini E-Ticaret API",
                version = "0.0.1",
                description = "Katalog, sepet, siparis ve odeme akislari icin REST API",
                contact = @Contact(name = "Mini E-Ticaret")
        )
)
public class OpenApiConfig {
}
