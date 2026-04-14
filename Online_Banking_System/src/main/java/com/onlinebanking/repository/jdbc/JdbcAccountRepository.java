package com.onlinebanking.repository.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import com.onlinebanking.model.Account;
import com.onlinebanking.repository.AccountRepository;

public class JdbcAccountRepository implements AccountRepository {
    private final DataSource dataSource;

    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Account> findByUserId(long userId) {
        String sql = "SELECT id, user_id, account_number, account_type, balance, created_at, status FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
            return accounts;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to query accounts", e);
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT id, user_id, account_number, account_type, balance, created_at, status FROM accounts WHERE account_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to query account", e);
        }
    }

    @Override
    public Optional<Account> findById(long id) {
        String sql = "SELECT id, user_id, account_number, account_type, balance, created_at, status FROM accounts WHERE id = ?";
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
            throw new IllegalStateException("Failed to query account", e);
        }
    }

    @Override
    public void updateBalance(long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setLong(2, accountId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to update balance", e);
        }
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE accounts SET balance = ?, status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, account.getBalance());
            ps.setString(2, account.getStatus());
            ps.setLong(3, account.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to update account", e);
        }
    }

    private Account mapRow(ResultSet rs) throws Exception {
        Account account = new Account(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("account_number"),
                rs.getString("account_type"),
                rs.getBigDecimal("balance"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
        account.setStatus(rs.getString("status"));
        return account;
    }
}
