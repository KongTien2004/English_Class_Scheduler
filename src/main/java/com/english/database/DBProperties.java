package com.english.database;

import java.io.IOException;
import java.util.Properties;

public class DBProperties {
    private static Properties props = new Properties();

    static {
        try {
            props.load(DBProperties.class.getClassLoader().getResourceAsStream("db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String host() {
        return props.get("db.host").toString();
    }

    public static int port() {
        try {
            return Integer.parseInt(props.get("db.port").toString());
        } catch (NumberFormatException e) {
            return 3306;
        }
    }

    public static String username() {
        return props.get("db.username").toString();
    }

    public static String password() {
        return props.get("db.password").toString();
    }

    public static String dbname() {
        return props.get("db.dbname").toString();
    }

    public static String option() {
        return props.get("db.option").toString();
    }
}
