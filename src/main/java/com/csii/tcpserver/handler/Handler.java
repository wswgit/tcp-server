package com.csii.tcpserver.handler;

import com.csii.tcpserver.Bean.Node;
import com.csii.tcpserver.parser.ExcelAPIParse;
import com.csii.tcpserver.parser.Parser;
import com.csii.tcpserver.util.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class Handler implements ApplicationContextAware {
    public static final String needParseTypes = "int,Integer;double,Double;float,Float;char;boolean,Boolean;Date";
    @Value("${type-only}")
    private String typeOnly;
    @Value("${api.type.length}")
    int typeLength = 8;//请求接口数据类型长度
    @Value("${api.name.length}")
    int apiNameLength = 20;//接口名称长度
    @Value("${api.support.types}")
    String supportTypes = ",XML, JSON,";
    @Value("${api.path}")
    String apiPath = "api-files";
    @Value("${api.response.path}")
    String responsePath;

    public String handler(String message) {
        String type;
        String name;
        String body;
        StringBuilder returnString;
        try {
            if ("".equals(message))
                throw new Exception("请求信息为空");
            if (!"no".equals(typeOnly)) {
                type = typeOnly;
                typeLength = 0;
                if (message.length() < typeLength + apiNameLength)
                    throw new Exception("请求报文体为空");
                name = message.substring(0, apiNameLength - 1).trim();
                body = message.substring(apiNameLength);
            } else {
                if (message.length() < typeLength + apiNameLength)
                    throw new Exception("请求报文体为空");
                type = message.substring(0, typeLength).trim();
                name = message.substring(typeLength, typeLength + apiNameLength).trim();
                body = message.substring(typeLength + apiNameLength);
            }
            if (supportTypes.contains("," + type.toUpperCase() + ","))
                type = type.toUpperCase();
            else
                throw new Exception("不支持的报文类型");
            returnString = new StringBuilder(handler(name, body, type));//正常返回的时候我们返回的是需要添加到body中的接口文档对应的相关字段
        } catch (IndexOutOfBoundsException e) {
            returnString = new StringBuilder("<message>").append("请求信息结构不完整").append("</message>");
        } catch (Exception e) {
            e.printStackTrace();
            //这里我们将response.xml中的${}替换成对应的错误信息
            returnString = new StringBuilder("<message>").append(e.getMessage()).append("</message>");
        }
        String[] strs = responseStr.split("<body>");
        if (strs.length > 2 || strs.length == 1)
            throw new RuntimeException("response.xml中应包含且仅包含一个<body>节点");
        return strs[0] + "<body>" + returnString.toString() + strs[1];
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
        applicationContext = applicationContextParam;
    }

    public String handler(String name, String body, String reqType) throws Exception {
        /**
         *
         *遍历JMap,并根据JMap的位置和键名称我们去JMap对应的位置获取相应的名称,然后将MMap中的value去匹配JMap正则.
         */
        if (ExcelAPIParse.map.isEmpty())
            throw new IOException("请在resources下/api-files下放入正确的接口文档");
        Map<String, Map<String, Node>> apiMap = (Map) ExcelAPIParse.map.get(name);//当前请求对应接口的Map
        Map<String, Node> reqAPI = apiMap.get("Req");
        Map<String, Object> req = ((Parser) applicationContext.getBean(reqType + "Parse")).parse(body);//解析请求过来产生的Map
        Map<String, String> reqMap = req.get("head") instanceof String ? new HashMap() : (Map<String, String>) req.get("head");
        reqMap.putAll(req.get("body") instanceof String ? new HashMap() : (Map<String, String>) req.get("body"));
        StringBuilder responseBody = new StringBuilder();
        if (isMatch(reqAPI, reqMap)) {
            /**
             * 既然是成功的我们根据接口文档返回对应的返回报文，放到response.xml的body中
             */
//          returnString=returnString.replaceFirst("\\$\\{[^\\}]*\\}", e.getMessage());
            //1.创建Reader对象
            Map<String, Node> resAPI = apiMap.get("Res");
            for (Map.Entry<String, Node> entry : resAPI.entrySet()) {
                Node node = entry.getValue();
                String type = node.getType();
                String fieldName = node.getName();
                String fieldValue;
                int length = node.getLength();
                if (reqMap.get(fieldName) != null)//1
                    fieldValue = reqMap.get(fieldName);
                else if (fieldName.toUpperCase().contains("ACNO"))
                    fieldValue = Utils.ResFieldProperties.getProperty("ACNO");//2
                else if (fieldName.toUpperCase().contains("ACNAME"))
                    fieldValue = Utils.ResFieldProperties.getProperty("ACNAME");//3
                else {
                    fieldValue = Utils.ResFieldProperties.getProperty(type.toUpperCase());
                    if (length < fieldValue.length())
                        fieldValue = fieldValue.substring(0, length);
                }
                responseBody.append("<").append(fieldName).append(">").append(fieldValue).append("</").append(fieldName).append(">").append("\n");
            }
        }
        return responseBody.toString();
    }


    public static final String responseStr = getResponseStrFromXML();

    public static String getResponseStrFromXML() {
        File file = new File("src/main/resources/response-files/response.xml");
        String returnString = "";
        if (file.exists()) {
            try (FileReader reader = new FileReader(file); BufferedReader bReader = new BufferedReader(reader)) {
                String s;
                StringBuilder str = new StringBuilder();
                while ((s = bReader.readLine()) != null) {
                    str.append(s + "\n");
                }
                returnString = str.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException("src/main/resources/response-files/response.xml不存在");
        }
        return returnString;
    }

    /**
     * 我们遍历
     *
     * @param apiReq
     * @param reqMap
     * @return
     */
    public boolean isMatch(Map<String, Node> apiReq, Map<String, String> reqMap) throws Exception {
        StringBuilder allmessage = new StringBuilder();
        for (Map.Entry<String, Node> entry : apiReq.entrySet()) {
            StringBuilder message = new StringBuilder();
            String key = entry.getKey();
            Node value = entry.getValue();
            String reqValue = reqMap.get(key);
            if (null == reqValue) {
                if (value.isMust()) {
                    message.append("必传字段：").append(key).append(";;;");
                    message.append("\n");
                    allmessage.append(message);
                    continue;
                }
            }
            if (value.getLength() < reqValue.length() || reqValue.length() == 0)
                message.append("字段").append(key).append("长度应为").append(value.getLength()).append("实际输入长度").append(reqValue.length()).append(";;;");
            String type = value.getType();
            if (needParseTypes.contains(type)) {
                try {
                    if ("int".equals(type) || "Integer".equals(type)) {
                        Integer.parseInt(reqValue);
                    } else if ("float".equals(type) || "Float".equals(type)) {
                        Float.parseFloat(reqValue);
                    } else if ("double".equals(type) || "Double".equals(type)) {
                        Double.parseDouble(reqValue);
                    } else if ("char".equals(type) && reqValue.length() > 1) {
                        message.append("字段").append(key).append("类型不匹配").append(";;;");
                    } else if ("boolean".equals(type) || "Boolean".equals(type)) {
                        Boolean.parseBoolean(reqValue);
                    } else if ("Date".equals(type)) {
                        DateUtil.convert(reqValue);
                    }
                } catch (Exception e) {
                    message.append("字段").append(key).append("类型不匹配").append(";;;");
                }
            }
            if (message.length() > 0)
                message.append("\n");
            allmessage.append(message);
        }
        if (allmessage.length() > 0)
            throw new Exception(allmessage.toString());
        return true;
    }


}
