package com.english.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBProperties {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBProperties.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("Warning: db.properties not found on classpath. Using defaults and env overrides.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // helper: first check environment variable, then properties file, then default
    private static String get(String envName, String propName, String defaultValue) {
        String v = System.getenv(envName);
        if (v != null && !v.isEmpty()) return v;
        return props.getProperty(propName, defaultValue);
    }

    public static String host() {
        return get("DB_HOST", "db.host", "localhost");
    }

    public static int port() {
        String val = get("DB_PORT", "db.port", "3306");
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 3306;
        }
    }

    public static String username() {
        return get("DB_USERNAME", "db.username", "root");
    }

    public static String password() {
        return get("DB_PASSWORD", "db.password", "");
    }

    public static String dbname() {
        return get("DB_NAME", "db.dbname", "english_class_scheduler");
    }

    public static String option() {
        return get("DB_OPTION", "db.option", "useUnicode=true&characterEncoding=utf8");
    }
}
