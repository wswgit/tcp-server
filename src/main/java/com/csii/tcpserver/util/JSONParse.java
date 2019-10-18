package com.csii.tcpserver.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class JSONParse {
    public static Map parse(String jsonStr){
        Map map = new HashMap();
        // 把字符串转化为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        for( Object key: jsonObject.keySet()){
            // 取值
            Object v = jsonObject.get(key);
            // 判断是否是集合
            if(v instanceof JSONArray){
                List<Map<String,Object>> list = new ArrayList<>();
                Iterator<Object> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = (JSONObject) it.next();
                    // 递归
                    list.add(JSONParse.parse(json2.toString()));
                }
                map.put(key,list);
            }else{
                map.put(key,v);
            }
        }
        return map;
    }

}
