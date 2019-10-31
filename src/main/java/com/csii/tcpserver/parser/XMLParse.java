package com.csii.tcpserver.parser;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.*;

@Component
public class XMLParse  implements Parser{
    private static int index=0;

    public Map parse(String xml) throws Exception {
        //1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new ByteArrayInputStream(xml.getBytes()));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        return parseEle(rootElement);
    }

    public static Map parseEle(Element root) {
        Map map=new HashMap();
        Iterator iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (!element.isTextOnly()) {
                if ("list".equalsIgnoreCase(element.getName())) {
                    List childList = new ArrayList();
                    map.put("___"+element.getName()+(index++), childList);//因为可能存在多个list，所以我们需要考虑分隔多个list
                    parseEle(element, childList);
                } else
                    map.put(element.getName(), parseEle(element));
            } else {
                System.out.println("节点名：" + element.getName() + "---节点值：" + element.getStringValue());
                map.put(element.getName(), element.getStringValue());
            }
        }
        System.out.println(map);
        return map;
    }

    private static List parseEle(Element root, List list) {
        Iterator iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            Map  map=new HashMap();
            if (!element.isTextOnly()) {
                if ("list".equalsIgnoreCase(element.getName())) {
                    List childList = new ArrayList();
                    map.put("___"+element.getName()+(index++), childList);
                    parseEle(element, childList);
                } else {
                    map.put(element.getName(), parseEle(element));

                }
            } else {
                System.out.println("节点名：" + element.getName() + "---节点值：" + element.getStringValue());
                map.put(element.getName(), element.getStringValue());
                list.add(map);
            }
        }
        return list;
    }
}
