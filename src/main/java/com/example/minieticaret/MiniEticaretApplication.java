package com.example.minieticaret;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MiniEticaretApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniEticaretApplication.class, args);
    }
}
