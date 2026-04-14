package com.onlinebanking.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.onlinebanking.model.Transaction;
import com.onlinebanking.util.DataSourceFactory;

public class TransactionRepository {
    
    public List<Transaction> findByAccountId(long accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT id, account_id, type, amount, description, date FROM transactions WHERE account_id = ? ORDER BY date DESC";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getLong("id"),
                    rs.getLong("account_id"),
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    rs.getString("date")
                ));
            }
        }
        return transactions;
    }

    public void create(long accountId, String type, double amount, String description) throws SQLException {
        String query = "INSERT INTO transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, accountId);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setString(4, description);
            stmt.executeUpdate();
        }
    }
}
