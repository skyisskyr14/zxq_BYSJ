package com.sq.system.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.ip-access")
public class IpAccessConfig {
    private boolean allowUnknownIp = true;
    private String allowListKey = "ip:allowlist";
    private String blockListKey = "ip:blacklist";
}
