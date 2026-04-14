package com.onlinebanking.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.onlinebanking.model.Account;
import com.onlinebanking.util.DataSourceFactory;

public class AccountRepository {
    
    public List<Account> findByUserId(long userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT id, user_id, account_number, balance FROM accounts WHERE user_id = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(new Account(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("account_number"),
                    rs.getDouble("balance")
                ));
            }
        }
        return accounts;
    }

    public Account findById(long id) throws SQLException {
        String query = "SELECT id, user_id, account_number, balance FROM accounts WHERE id = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Account(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("account_number"),
                    rs.getDouble("balance")
                );
            }
        }
        return null;
    }

    public void create(long userId, String accountNumber, double balance) throws SQLException {
        String query = "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setString(2, accountNumber);
            stmt.setDouble(3, balance);
            stmt.executeUpdate();
        }
    }

    public void updateBalance(long accountId, double newBalance) throws SQLException {
        String query = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newBalance);
            stmt.setLong(2, accountId);
            stmt.executeUpdate();
        }
    }
}
