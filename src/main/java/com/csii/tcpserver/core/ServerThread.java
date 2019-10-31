package com.csii.tcpserver.core;

import com.csii.tcpserver.handler.Handler;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class ServerThread extends Thread {

    private Socket socket;
    private Handler handler;//处理消息

    public ServerThread(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = socket.getOutputStream();
             PrintWriter printWriter = new PrintWriter(outputStream)) {
            // server接收消息
            String str;
            String s = "";
            //TODO 需要完善，但是目前不知道怎么做
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains("</body") || str.contains("\"body\":"))
                    socket.shutdownInput();
                s += str + "\n";
            }
            System.out.println("I am Server, now get message from Client: " + s);
            str = handler.handler(s);//接收到数据我们解析数据
            printWriter.write(str);
            System.out.println(str);
            printWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}