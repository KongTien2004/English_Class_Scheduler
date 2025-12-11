package com.english.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
    static Connection conn;
    static String url = "jdbc:mysql://" + DBProperties.host() + ":" + DBProperties.port() + "/" + DBProperties.dbname() + "?" + DBProperties.option();

    public static Statement getStatement() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, DBProperties.username(), DBProperties.password());
            }
            return conn.createStatement();

        } catch (SQLException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting database with URL: " + url);
            conn = DriverManager.getConnection(url, DBProperties.username(), DBProperties.password());
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy Driver MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối đến cơ sở dữ liệu");
            e.printStackTrace();
        }
        return conn;
    }


    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("-> Connected Successfully!");
            } else {
                System.out.println("-> Connected Failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
