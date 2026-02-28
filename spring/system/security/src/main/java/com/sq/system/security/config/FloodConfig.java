package com.sq.system.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.flood")
public class FloodConfig {
    private int expireTime = 60;
    private int maxRequests = 10;
}
