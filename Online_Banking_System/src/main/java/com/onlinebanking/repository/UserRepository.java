package com.onlinebanking.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.onlinebanking.model.User;
import com.onlinebanking.util.DataSourceFactory;

public class UserRepository {
    
    public User findByUsername(String username) throws SQLException {
        String query = "SELECT id, username, password, email, name FROM users WHERE username = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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

    public User findById(long id) throws SQLException {
        String query = "SELECT id, username, password, email, name FROM users WHERE id = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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

    public void create(String username, String password, String email, String name) throws SQLException {
        String query = "INSERT INTO users (username, password, email, name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, name);
            stmt.executeUpdate();
        }
    }

    public void update(User user) throws SQLException {
        String query = "UPDATE users SET email = ?, name = ? WHERE id = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setLong(3, user.getId());
            stmt.executeUpdate();
        }
    }
}
