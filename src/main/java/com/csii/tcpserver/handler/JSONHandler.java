package com.csii.tcpserver.handler;

import org.springframework.stereotype.Component;

@Component
public class JSONHandler extends Handler{
    /**
     * 这里我们解析JSON格式的报文
     *@param name,body
     * @return
     */
    @Override
    public String handler(String name,String body){
        return body;
    }
}
