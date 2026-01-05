package com.english.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnect {
    private static final Logger LOGGER = Logger.getLogger(DBConnect.class.getName());
    private static final HikariDataSource ds;

    static {
        HikariConfig cfg = new HikariConfig();

        // Build JDBC URL from DBProperties
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?%s&serverTimezone=UTC&useSSL=false",
                DBProperties.host(),
                DBProperties.port(),
                DBProperties.dbname(),
                DBProperties.option());

        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(DBProperties.username());
        cfg.setPassword(DBProperties.password());

        // Pool sizing & timeouts (tune to your workload or make configurable)
        cfg.setMaximumPoolSize(Integer.parseInt(System.getenv().getOrDefault("DB_MAX_POOL", "10")));
        cfg.setMinimumIdle(Integer.parseInt(System.getenv().getOrDefault("DB_MIN_IDLE", "2")));
        cfg.setConnectionTimeout(Long.parseLong(System.getenv().getOrDefault("DB_CONN_TIMEOUT", "10000")));
        cfg.setIdleTimeout(Long.parseLong(System.getenv().getOrDefault("DB_IDLE_TIMEOUT", "300000")));
        cfg.setMaxLifetime(Long.parseLong(System.getenv().getOrDefault("DB_MAX_LIFETIME", "1800000")));

        // Leak detection helpful in dev; set to 0 to disable in production
        long leakMs = Long.parseLong(System.getenv().getOrDefault("DB_LEAK_DETECTION_MS", "20000"));
        if (leakMs > 0) {
            cfg.setLeakDetectionThreshold(leakMs);
        }

        ds = new HikariDataSource(cfg);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ds != null && !ds.isClosed()) {
                ds.close();
                LOGGER.info("HikariDataSource closed.");
            }
        }));
    }

    // Keep signature same so callers don't need changes
    public static Connection getConnection() throws SQLException {
        try {
            return ds.getConnection();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Can't get connection pool", ex);
            throw ex;
        }
    }

    // Expose DataSource if you want to initialize Jdbi or other tools directly
    public static HikariDataSource getDataSource() {
        return ds;
    }

    // Optional quick connectivity test
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("-> Connected Successfully!");
            } else {
                System.out.println("-> Connected Failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
