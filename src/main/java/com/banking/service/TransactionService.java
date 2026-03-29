package com.banking.service;

import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TransactionService - Business Logic Layer
 * GRASP - Controller: Coordinates transfer operations
 * GRASP - Low Coupling: Uses repositories to decouple from data layer
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    
    /**
     * Major Use Case: Transfer Money
     * Handles fund transfers between accounts
     */
    public Transaction transferMoney(Long fromAccountId, String toAccountNumber, Double amount, String description) throws BankingException {
        // Validate amount
        if (amount <= 0) {
            throw new BankingException("Transfer amount must be greater than zero");
        }
        
        // Get source account
        Optional<Account> fromAccountOpt = accountRepository.findById(fromAccountId);
        if (fromAccountOpt.isEmpty()) {
            throw new BankingException("Source account not found");
        }
        
        // Get destination account
        Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(toAccountNumber);
        if (toAccountOpt.isEmpty()) {
            throw new BankingException("Destination account not found");
        }
        
        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();
        
        // Check if accounts are active
        if (!fromAccount.getIsActive() || !toAccount.getIsActive()) {
            throw new BankingException("One or both accounts are inactive");
        }
        
        // Check sufficient balance
        if (!fromAccount.hasSufficientBalance(amount)) {
            throw new BankingException("Insufficient balance");
        }
        
        // Perform transaction
        try {
            // Withdraw from source account
            fromAccount.withdraw(amount);
            accountRepository.save(fromAccount);
            
            // Deposit to destination account
            toAccount.deposit(amount);
            accountRepository.save(toAccount);
            
            // Record transaction
            Transaction transaction = new Transaction();
            transaction.setTransactionType("TRANSFER");
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transaction.setStatus("SUCCESS");
            transaction.setAccount(fromAccount);
            transaction.setReceiverAccountNumber(toAccountNumber);
            
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            throw new BankingException("Transfer failed: " + e.getMessage());
        }
    }
    
    /**
     * Record withdrawal transaction
     */
    public Transaction recordWithdrawal(Long accountId, Double amount, String description) throws BankingException {
        Optional<Account> account = accountRepository.findById(accountId);
        
        if (account.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus("SUCCESS");
        transaction.setAccount(account.get());
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Record deposit transaction
     */
    public Transaction recordDeposit(Long accountId, Double amount, String description) throws BankingException {
        Optional<Account> account = accountRepository.findById(accountId);
        
        if (account.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setStatus("SUCCESS");
        transaction.setAccount(account.get());
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Get transaction by ID
     */
    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }
    
    /**
     * Get transaction history - Minor Use Case
     */
    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
    }
    
    /**
     * Get transactions within date range
     */
    public List<Transaction> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
    }
}
