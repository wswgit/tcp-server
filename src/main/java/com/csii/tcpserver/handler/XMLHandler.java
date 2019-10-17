package com.csii.tcpserver.handler;

import org.springframework.stereotype.Component;

@Component
public class XMLHandler extends Handler{
    @Override
    public String handler(String message){
        return message;
    }
}
