package com.warehouse.Utils;

import com.warehouse.Http.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class DBProperties {
    static Properties appProps;



    public static String getProperty(String key) {
        try {
            if (appProps == null) {
                String resourceName = "db.properties"; // could also be a constant
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                appProps = new Properties();
                try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
                    appProps.load(resourceStream);
                }
            }
            return appProps.getProperty(key);
        } catch (IOException e) {
            Server.root.fatal("Unable to read database properties: " + e.getMessage());
            return null;
        }
    }
}
