package com.csii.tcpserver.config;

import com.csii.tcpserver.core.TCPServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class Config {
    @Bean
    public TCPServer TCPServer() throws IOException {
        TCPServer server = new TCPServer(8888);
        server.startServer();
        return server;
    }
}
