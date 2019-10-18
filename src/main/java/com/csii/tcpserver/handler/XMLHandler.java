package com.csii.tcpserver.handler;

import com.csii.tcpserver.util.XmlParse;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public class XMLHandler extends Handler{
    /**
     * 这里我们解析xml格式的报文
     * @param name,body
     * @return
     */
    @Override
    public String handler(String name,String body) throws DocumentException {
        /**
         * 1.解析xml将节点存进MMap
         * 2.读取接口文档，并解析JMap<String,Bean>
         * 3.遍历JMap,并根据JMap的位置和键名称我们去JMap对应的位置获取相应的名称,然后将MMap中的value去匹配JMap正则.
         */
        Map map=XmlParse.parse(body);


//        InputStream in = this.getClass().getResourceAsStream(apiPath+"/"+name+".xls");


        return body;
    }
}
