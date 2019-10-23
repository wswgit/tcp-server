package com.csii.tcpserver.config;

import com.csii.tcpserver.core.TCPServer;
import com.csii.tcpserver.util.ExcelAPIParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class Config {
    @Value("${api.path}")
    String apiPath;
    @Autowired
    TCPServer server;
    @Bean
    public String  SocketServer() throws IOException {
        ExcelAPIParse.loadFile(apiPath);
        server.startServer();
        return "";
    }
}
