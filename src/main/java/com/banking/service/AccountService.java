package com.banking.service;

import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * AccountService - Business Logic Layer
 * GRASP - High Cohesion: Focused only on account-related operations
 * GRASP - Information Expert: Uses Account entity which has the balance information
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    
    /**
     * Create new account - Part of Major Use Case: Create Account
     */
    public Account createAccount(Long customerId, String accountType, Double initialBalance) throws BankingException {
        Optional<Customer> customer = customerRepository.findById(customerId);
        
        if (customer.isEmpty()) {
            throw new BankingException("Customer not found");
        }
        
        if (initialBalance < 0) {
            throw new BankingException("Initial balance cannot be negative");
        }
        
        Account account = customer.get().createAccount(accountType, initialBalance);
        return accountRepository.save(account);
    }
    
    /**
     * Get account by account number
     */
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    /**
     * Get account by ID
     */
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    /**
     * Get all accounts for a customer
     */
    public List<Account> getCustomerAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }
    
    /**
     * Get all active accounts for a customer
     */
    public List<Account> getActiveAccounts(Long customerId) {
        return accountRepository.findByCustomerIdAndIsActive(customerId, true);
    }
    
    /**
     * Check balance - Minor Use Case
     */
    public Double checkBalance(Long accountId) throws BankingException {
        Optional<Account> account = accountRepository.findById(accountId);
        
        if (account.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        return account.get().getBalance();
    }
    
    /**
     * Deposit money into account
     */
    public void depositMoney(Long accountId, Double amount) throws BankingException {
        Optional<Account> account = accountRepository.findById(accountId);
        
        if (account.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        if (amount <= 0) {
            throw new BankingException("Deposit amount must be greater than zero");
        }
        
        Account acc = account.get();
        acc.deposit(amount);
        accountRepository.save(acc);
    }
    
    /**
     * Withdraw money from account
     */
    public void withdrawMoney(Long accountId, Double amount) throws BankingException {
        Optional<Account> account = accountRepository.findById(accountId);
        
        if (account.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        Account acc = account.get();
        
        if (!acc.withdraw(amount)) {
            throw new BankingException("Insufficient balance or invalid amount");
        }
        
        accountRepository.save(acc);
    }
    
    /**
     * Close account
     */
    public void closeAccount(Long accountId) throws BankingException {
        Optional<Account> account = accountRepository.findById(accountId);
        
        if (account.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        Account acc = account.get();
        
        if (acc.getBalance() != 0) {
            throw new BankingException("Cannot close account with non-zero balance");
        }
        
        acc.setIsActive(false);
        accountRepository.save(acc);
    }
}
