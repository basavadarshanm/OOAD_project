package com.onlinebanking.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onlinebanking.model.User;
import com.onlinebanking.repository.UserRepository;

public class JdbcUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepository.class);
    private final DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role, email, phone, full_name, created_at FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Database error querying user by username: {}", username, e);
            throw new IllegalStateException("Database connection error: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT id, username, password_hash, role, email, phone, full_name, created_at FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to query user", e);
        }
    }

    @Override
    public User create(String username, String passwordHash, String role) {
        return create(username, passwordHash, role, "", "", username, LocalDateTime.now());
    }

    @Override
    public User create(String username, String passwordHash, String role, String email, String phone, String fullName, LocalDateTime createdAt) {
        String sql = "INSERT INTO users (username, password_hash, role, email, phone, full_name, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, fullName);
            ps.setTimestamp(7, Timestamp.valueOf(createdAt));
            int rows = ps.executeUpdate();
            log.debug("Insert user rows affected: {}", rows);
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    User user = new User(keys.getLong(1), username, passwordHash, role, email, phone, fullName, createdAt);
                    log.info("User created with id: {}", user.getId());
                    return user;
                }
            }
            throw new IllegalStateException("Failed to create user: no generated key returned");
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            log.error("Username already exists: {}", username);
            throw new IllegalStateException("Username already exists");
        } catch (Exception e) {
            log.error("Error creating user: {} with email: {}", username, email, e);
            throw new IllegalStateException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET email = ?, phone = ?, full_name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getFullName());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to update user", e);
        }
    }

    private User mapRow(ResultSet rs) throws Exception {
        User user = new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("full_name"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
        return user;
    }
}
