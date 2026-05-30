package com.gp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author ljb
 * @create 2023/6/26
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注入ServerEndpointExporter bean对象，自动注册使用了@ServerEndpoint("/chat")注解的bean
     *
     * @return {@link ServerEndpointExporter}
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
