package com.csii.tcpserver.core;

import com.csii.tcpserver.handler.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 8  * Socket Server
 * 9  * <p>
 * 11
 */
@Component
public class TCPServer {
    @Value("${socket.server.port}")
    private int port;
   @Autowired
   @Qualifier("handler")
    Handler handler;
    public synchronized void startServer() throws IOException {
        ServerSocket serverSocket=new ServerSocket(port);
        int count = 0;
        while (true) {
            final Socket socket = serverSocket.accept();
            ServerThread serverThread = new ServerThread(socket,handler);
            System.out.println("client host address is: " + socket.getInetAddress().getHostAddress());
            serverThread.start();
            count++;
            System.out.println("now client count is: " + count);
        }
    }

    public String getName() {
        return "TCP-Server";
    }
}