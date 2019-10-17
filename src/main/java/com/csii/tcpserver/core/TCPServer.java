package com.csii.tcpserver.core;

import com.csii.tcpserver.handler.Handler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 8  * Socket Server
 * 9  * <p>
 * 11
 */
public class TCPServer {
    private int port;
    ServerSocket serverSocket;
    @Autowired
    Handler handler;
    public TCPServer(int port) throws IOException {
        this.port=port;
        serverSocket = new ServerSocket(port);
    }

    public synchronized void startServer() throws IOException {
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