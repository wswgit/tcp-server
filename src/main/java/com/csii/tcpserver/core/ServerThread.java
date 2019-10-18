package com.csii.tcpserver.core;

import com.csii.tcpserver.handler.Handler;
import org.dom4j.DocumentException;

import java.io.*;
import java.net.Socket;

/**
 */
public class ServerThread extends Thread {

    private Socket socket;
    private Handler handler;//处理消息

    public ServerThread(Socket socket , Handler handler){
        this.socket = socket;
        this.handler=handler;
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        OutputStream outputStream = null;
        PrintWriter printWriter = null;
        try {
            // server接收消息
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String str;
            String s="";
            while ((str = bufferedReader.readLine()) != null) {
                s += "\n"+str;
            }
            if (!"".equals(s)) {
                System.out.println("I am Server, now get message from Client: " + s);
                str=handler.handler(s);//接收到数据我们解析数据
            }else
                str="请求数据为空";
            socket.shutdownInput();
            // server发送消息
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            printWriter.write(str);
            System.out.println(str);
            printWriter.flush();

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (outputStream != null) {
                    outputStream.close();

                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}