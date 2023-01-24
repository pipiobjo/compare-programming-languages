package com.pipiobjo.app;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

/**
 * Application configuration values
 */
//TODO: load via yaml
@Data
@Value
public class Configuration {

    private boolean useInMemoryUserDB = true;
    private ContextConfig app = ContextConfig.builder().contextPath("/api").port(8080).build();
    private ContextConfig ops = ContextConfig.builder().contextPath("/ops").port(8081).build();
    @Data
    @Value
    @Builder
    public static class ContextConfig{
        private int port;
        private String contextPath;
    }
}
