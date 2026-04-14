package com.onlinebanking.service;

import java.sql.SQLException;
import java.util.List;

import com.onlinebanking.model.Account;
import com.onlinebanking.model.Transaction;
import com.onlinebanking.repository.AccountRepository;
import com.onlinebanking.repository.TransactionRepository;

public class AccountService {
    private AccountRepository accountRepo;
    private TransactionRepository transactionRepo;

    public AccountService() {
        this.accountRepo = new AccountRepository();
        this.transactionRepo = new TransactionRepository();
    }

    public List<Account> getUserAccounts(long userId) throws SQLException {
        return accountRepo.findByUserId(userId);
    }

    public List<Transaction> getTransactions(long accountId) throws SQLException {
        return transactionRepo.findByAccountId(accountId);
    }

    public void deposit(long accountId, double amount) throws SQLException {
        Account account = accountRepo.findById(accountId);
        if (account != null) {
            account.deposit(amount);
            accountRepo.updateBalance(accountId, account.getBalance());
            transactionRepo.create(accountId, "DEPOSIT", amount, "Deposit");
        }
    }

    public boolean withdraw(long accountId, double amount) throws SQLException {
        Account account = accountRepo.findById(accountId);
        if (account != null && account.withdraw(amount)) {
            accountRepo.updateBalance(accountId, account.getBalance());
            transactionRepo.create(accountId, "WITHDRAW", amount, "Withdrawal");
            return true;
        }
        return false;
    }

    public boolean transfer(long fromAccountId, long toAccountId, double amount) throws SQLException {
        Account fromAccount = accountRepo.findById(fromAccountId);
        Account toAccount = accountRepo.findById(toAccountId);
        
        if (fromAccount != null && toAccount != null && fromAccount.withdraw(amount)) {
            toAccount.deposit(amount);
            accountRepo.updateBalance(fromAccountId, fromAccount.getBalance());
            accountRepo.updateBalance(toAccountId, toAccount.getBalance());
            transactionRepo.create(fromAccountId, "TRANSFER", amount, "Transfer to " + toAccount.getAccountNumber());
            transactionRepo.create(toAccountId, "TRANSFER", amount, "Transfer from " + fromAccount.getAccountNumber());
            return true;
        }
        return false;
    }

    public void createAccount(long userId, String accountNumber, double initialBalance) throws SQLException {
        accountRepo.create(userId, accountNumber, initialBalance);
    }
}
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        // Perform transfer with atomic operations
        fromAccount.withdraw(amount); // Will throw if insufficient balance
        toAccount.deposit(amount);

        // Update accounts in repository
        accountRepository.update(fromAccount);
        accountRepository.update(toAccount);

        // Record transactions
        long fromTxnId = transactionRepository.create(
            fromAccountId, 
            "DEBIT", 
            amount, 
            LocalDateTime.now(),
            "Transfer to " + toAccount.getAccountNumber() + " - " + description
        ).getId();

        long toTxnId = transactionRepository.create(
            toAccountId, 
            "CREDIT", 
            amount, 
            LocalDateTime.now(),
            "Transfer from " + fromAccount.getAccountNumber() + " - " + description
        ).getId();
    }

    /**
     * Deposit money into account
     */
    public void depositMoney(long accountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.deposit(amount);
        accountRepository.update(account);

        transactionRepository.create(
            accountId,
            "CREDIT",
            amount,
            LocalDateTime.now(),
            description != null ? description : "Deposit"
        );
    }

    /**
     * Withdraw money from account
     */
    public void withdrawMoney(long accountId, BigDecimal amount, String description) 
            throws Account.InsufficientBalanceException {
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.withdraw(amount);
        accountRepository.update(account);

        transactionRepository.create(
            accountId,
            "DEBIT",
            amount,
            LocalDateTime.now(),
            description != null ? description : "Withdrawal"
        );
    }

    /**
     * Get account balance
     */
    public BigDecimal getBalance(long accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Check daily transaction limit (example: $5000/day)
     */
    public BigDecimal getDailySpent(long accountId) {
        List<Transaction> today = transactionRepository.findByAccountId(accountId, Integer.MAX_VALUE);
        return today.stream()
                .filter(t -> t.getType().equals("DEBIT") && isToday(t.getOccurredAt()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if transaction would exceed daily limit
     */
    public boolean canTransfer(long accountId, BigDecimal amount) {
        BigDecimal dailyLimit = new BigDecimal("5000");
        BigDecimal spent = getDailySpent(accountId);
        return spent.add(amount).compareTo(dailyLimit) <= 0;
    }

    private boolean isToday(LocalDateTime dateTime) {
        return dateTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
}
