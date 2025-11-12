package com.teamup.practice.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "key.auth")
public class AuthProperties {
    private List<String> includePaths;
    private List<String> excludePaths;
}
