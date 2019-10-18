package com.csii.tcpserver.handler;

import com.csii.tcpserver.util.JSONParse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JSONHandler extends Handler{
    /**
     * 这里我们解析JSON格式的报文
     *@param name,body
     * @return
     */
    @Override
    public String handler(String name,String body){
        //解析json字符串
        Map map = JSONParse.parse(body);
        return map.toString();
    }
}
