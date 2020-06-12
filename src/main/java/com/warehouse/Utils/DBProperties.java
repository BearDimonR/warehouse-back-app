package com.warehouse.Utils;

import com.warehouse.Http.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class DBProperties {
    static Properties appProps;
    static String appConfigPath =
            Objects.requireNonNull
                    (Thread.currentThread().getContextClassLoader().getResource("db.properties")).getPath();


    public static String getProperty(String key) {
        try {
            if (appProps == null) {
                appProps = new Properties();
                appProps.load(new FileInputStream(appConfigPath));
            }
            return appProps.getProperty(key);
        } catch (IOException e) {
            Server.root.fatal("Unable to read database properties: " + e.getMessage());
            return null;
        }
    }
}
