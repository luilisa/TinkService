package com.example.tinkoffservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "application")
public class ApiConfig {
    private Boolean isSandboxMode;
    private List<String> stocks;
}
