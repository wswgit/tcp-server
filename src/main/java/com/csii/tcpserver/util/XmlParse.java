package com.csii.tcpserver.util;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.*;

public class XmlParse {
    public static Map parse(String xml) throws DocumentException {

        System.out.println(xml);
        //1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new ByteArrayInputStream(xml.getBytes()));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        return parseEle(rootElement, new HashMap());
    }

    public static Map parseEle(Element root, Map map) {
        Iterator iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            List<Attribute> attributes = element.attributes();
            System.out.println("======获取属性值======");
            for (Attribute attribute : attributes) {
                System.out.println(attribute.getValue());
            }
            if (element.isRootElement()) {
                if ("list".equalsIgnoreCase(element.getName())) {
                    List childList = new ArrayList();
                    map.put(element.getName(), childList);

                    parseEle(element, childList);
                } else {
                    Map childMap = new HashMap();
                    map.put(element.getName(), childMap);
                    parseEle(element, childMap);
                }
            } else {
                System.out.println("节点名：" + element.getName() + "---节点值：" + element.getStringValue());
                map.put(element.getName(), element.getStringValue());
            }
        }
        return map;
    }

    private static List parseEle(Element root, List list) {
        Iterator iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            Map  map=new HashMap();
            List<Attribute> attributes = element.attributes();
            System.out.println("======获取属性值======");
            for (Attribute attribute : attributes) {
                System.out.println(attribute.getValue());
            }
            if (element.isRootElement()) {
                if ("list".equalsIgnoreCase(element.getName())) {
                    List childList = new ArrayList();
                    map.put(element.getName(), childList);
                    parseEle(element, childList);
                } else {
                    Map childMap = new HashMap();
                    map.put(element.getName(), childMap);
                    parseEle(element, childMap);
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
