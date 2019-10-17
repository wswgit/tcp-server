package com.csii.tcpserver.handler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Handler implements ApplicationContextAware {
    int typeLength = 8;//请求接口数据类型长度
    int apiNameLength = 20;//接口名称长度
    String supportTypes[] = {"XML", "JSON"};
    Handler handler;

    public String handler(String message) {
        if (message.length() < typeLength + apiNameLength)
            return "请求报文体为空";
        String type = message.substring(0, typeLength).trim();
        String name = message.substring(typeLength, typeLength + apiNameLength).trim();
        String body = message.substring(typeLength + apiNameLength);
        if (Arrays.asList(supportTypes).contains(type))
            handler = (Handler) applicationContext.getBean(type.toUpperCase() + "Handler");//根据类型获取对应的Handler
        else
            return "不支持的报文类型";
        return handler.handler(body);
    }


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
        applicationContext = applicationContextParam;
    }
}
