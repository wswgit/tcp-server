package com.csii.tcpserver.util;

import com.csii.tcpserver.handler.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public static Properties ResFieldProperties = getResFieldProperties("/response-files/responseField.properties");

    private static Properties getResFieldProperties(String path) {
        Properties prop = new Properties();
        try (InputStream inputStream = Handler.class.getResourceAsStream(path)) {
            if (inputStream == null)
                throw new RuntimeException("please check the properties file");
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("please check the properties file");
        }
        return prop;
    }
}
