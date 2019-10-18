package com.csii.tcpserver.config;

import com.csii.tcpserver.core.TCPServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class Config {
    @Autowired
    TCPServer server;
    @Bean
    public void SocketServer() throws IOException {
        server.startServer();
    }
}
