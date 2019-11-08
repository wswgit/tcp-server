package com.csii.tcpserver.config;

import com.csii.tcpserver.core.TCPServer;
import com.csii.tcpserver.parser.ExcelAPIParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Configuration
public class Config {
    @Value("${api.path}")
    String apiPath;
    @Autowired
    TCPServer server;

    @Bean
    public String SocketServer() throws IOException {
        ExcelAPIParse.loadFile(apiPath);
        server.startServer();
        return "";
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            String a = "AA".substring(4);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("BBB");
        } catch (Exception e) {
            System.out.println("AAAAAAA");
        }
    }


    public int lastStoneWeight(int[] stones) {
        //int x = maxX;
        //int y = maxY;
        List list= Arrays.asList(stones);
        list.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        });
        return stones[stones.length-1];
    }
}
