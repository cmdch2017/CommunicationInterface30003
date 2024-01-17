package com.example.communicationinterface30003.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
/**
 * 传输webSocket信息
 * @author lst
 * @date 2022/12/15 11:12
 * @param null
 * @return null
 */
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        return new ServerEndpointExporter();
    }
}
