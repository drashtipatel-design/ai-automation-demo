package com.vintelix.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url", "http://localhost:5173");
    }

    public static String getValidUsername() {
        return properties.getProperty("valid.username", "testuser");
    }

    public static String getValidPassword() {
        return properties.getProperty("valid.password", "Test@1234");
    }

    public static String getValidEmail() {
        return properties.getProperty("valid.email", "test@vintelix.com");
    }
}
