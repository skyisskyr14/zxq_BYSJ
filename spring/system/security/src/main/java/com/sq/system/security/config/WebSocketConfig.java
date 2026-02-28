package com.sq.system.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig {


    // 注册所有使用 @ServerEndpoint 注解的 websocket 类
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        System.out.println("✅ WebSocket 服务初始化完成，ServerEndpointExporter 已注册");
        return new ServerEndpointExporter();
    }
}
