package com.csii.tcpserver.handler;

import org.dom4j.DocumentException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Component
public class Handler implements ApplicationContextAware {
    @Value("${api.type.length}")
    int typeLength = 8;//请求接口数据类型长度
    @Value("${api.name.length}")
    int apiNameLength = 20;//接口名称长度
    @Value("${api.support.types}")
    String supportTypes = "'XML', 'JSON'";
    @Value("${api.path}")
    String apiPath="api-files";
    Handler handler;

    public String handler(String message) throws DocumentException {
        if (message.length() < typeLength + apiNameLength)
            return "请求报文体为空";
        String type = message.substring(0, typeLength).trim();
        String name = message.substring(typeLength, typeLength + apiNameLength).trim();
        String body = message.substring(typeLength + apiNameLength);
        if (supportTypes.contains("'"+type+"'"))
            handler = (Handler) applicationContext.getBean(type.toUpperCase() + "Handler");//根据类型获取对应的Handler
        else
            return "不支持的报文类型";
        return handler.handler(name,body);
    }
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
        applicationContext = applicationContextParam;
    }
    public String handler(String name,String body) throws DocumentException {
        return "";
    }
}
