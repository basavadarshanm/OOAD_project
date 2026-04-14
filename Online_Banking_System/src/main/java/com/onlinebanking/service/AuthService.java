package com.onlinebanking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.onlinebanking.model.User;
import com.onlinebanking.util.DataSourceFactory;

public class AuthService {
    
    public User login(String username, String password) throws SQLException {
        String query = "SELECT id, username, password, email, name FROM users WHERE username = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getString("password").equals(password)) {
                return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("name")
                );
            }
        }
        return null;
    }

    public boolean register(String username, String password, String email, String name) throws SQLException {
        String checkQuery = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, username);
            if (stmt.executeQuery().next()) {
                return false; // Username exists
            }
        }

        String insertQuery = "INSERT INTO users (username, password, email, name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, name);
            stmt.executeUpdate();
            return true;
        }
    }
}
            otpTimestamps.remove(username);
            return false;
        }
        
        boolean valid = storedOtp.equals(otp);
        if (valid) {
            otpStore.remove(username);
            otpTimestamps.remove(username);
        }
        return valid;
    }

    /**
     * Enable 2FA for user
     */
    public void enable2FA(User user) {
        String secret = generateSecret();
        user.setOtpSecret(secret);
        user.setTwoFactorEnabled(true);
    }

    /**
     * Disable 2FA for user
     */
    public void disable2FA(User user) {
        user.setTwoFactorEnabled(false);
        user.setOtpSecret(null);
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Verify password against hash
     */
    private boolean verifyPassword(String plainPassword, String passwordHash) {
        String hash = hashPassword(plainPassword);
        return hash.equals(passwordHash);
    }

    /**
     * Generate 6-digit OTP
     */
    private String generateRandomOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Generate random secret for 2FA
     */
    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] values = new byte[32];
        random.nextBytes(values);
        return Base64.getEncoder().encodeToString(values);
    }
}
