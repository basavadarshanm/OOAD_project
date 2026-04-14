package com.onlinebanking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceFactory {
    private static final String DB_URL = "jdbc:sqlite:./banking.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase(connection);
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create tables if they don't exist
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "email TEXT," +
                    "name TEXT" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "account_number TEXT UNIQUE NOT NULL," +
                    "balance REAL DEFAULT 0," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "account_id INTEGER NOT NULL," +
                    "type TEXT NOT NULL," +
                    "amount REAL NOT NULL," +
                    "description TEXT," +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(account_id) REFERENCES accounts(id)" +
                    ")");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
} +
            "    user_id BIGINT NOT NULL," +
            "    name VARCHAR(128) NOT NULL," +
            "    account_number VARCHAR(32) NOT NULL," +
            "    bank VARCHAR(128) NOT NULL," +
            "    FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")",
            "CREATE INDEX idx_users_username ON users(username)",
            "CREATE INDEX idx_accounts_user_id ON accounts(user_id)",
            "CREATE INDEX idx_transactions_account_id ON transactions(account_id)",
            "CREATE INDEX idx_transactions_occurred_at ON transactions(occurred_at)"
        };
        try (var conn = ds.getConnection(); var stmt = conn.createStatement()) {
            for (String sql : ddlStatements) {
                try {
                    stmt.execute(sql);
                    log.debug("Executed: {}", sql.substring(0, Math.min(50, sql.length())));
                } catch (Exception e) {
                    log.debug("Expected error for optional statement: {}", e.getMessage());
                }
            }
            log.info("Schema initialized successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize schema", e);
        }
        initializeTestData(ds);
    }

    private static void initializeTestData(HikariDataSource ds) {
        try (var conn = ds.getConnection(); var pstmt = conn.prepareStatement(
                "INSERT INTO users (username, password_hash, role, email, phone, full_name) VALUES (?, ?, ?, ?, ?, ?)")) {
            String testPassword = hashPassword("password123");
            pstmt.setString(1, "demo");
            pstmt.setString(2, testPassword);
            pstmt.setString(3, "CUSTOMER");
            pstmt.setString(4, "demo@example.com");
            pstmt.setString(5, "1234567890");
            pstmt.setString(6, "Demo User");
            try {
                pstmt.executeUpdate();
                log.info("Test user 'demo' created successfully");
                
                // Create test accounts for the demo user
                try (var accStmt = conn.prepareStatement(
                        "INSERT INTO accounts (user_id, account_number, account_type, balance, status) VALUES (?, ?, ?, ?, ?)")) {
                    long userId = 1;
                    accStmt.setLong(1, userId);
                    accStmt.setString(2, "ACC0001");
                    accStmt.setString(3, "SAVINGS");
                    accStmt.setBigDecimal(4, new java.math.BigDecimal("5000.00"));
                    accStmt.setString(5, "ACTIVE");
                    accStmt.executeUpdate();
                    
                    accStmt.setLong(1, userId);
                    accStmt.setString(2, "ACC0002");
                    accStmt.setString(3, "CHECKING");
                    accStmt.setBigDecimal(4, new java.math.BigDecimal("2500.00"));
                    accStmt.setString(5, "ACTIVE");
                    accStmt.executeUpdate();
                    
                    log.info("Test accounts created for demo user");
                }
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                log.debug("Test user already exists");
            }
        } catch (Exception e) {
            log.warn("Failed to initialize test data: {}", e.getMessage());
        }
    }

    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return null;
    }

    private static int parseInt(String primary, String fallback, int defaultVal) {
        String value = firstNonBlank(primary, fallback);
        return value == null ? defaultVal : Integer.parseInt(value);
    }

    private static long parseLong(String primary, String fallback, long defaultVal) {
        String value = firstNonBlank(primary, fallback);
        return value == null ? defaultVal : Long.parseLong(value);
    }
}
