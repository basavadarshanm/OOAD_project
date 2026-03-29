package com.banking.service;

import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.model.Bill;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.BillRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BillService - Business Logic Layer
 * GRASP - High Cohesion: Focused only on bill payment operations
 * GRASP - Controller: Coordinates bill payment operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BillService {
    
    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Create a bill for a customer
     */
    public Bill createBill(Long customerId, String billType, Double billAmount, String biller, LocalDateTime dueDate) throws BankingException {
        Optional<Customer> customer = customerRepository.findById(customerId);
        
        if (customer.isEmpty()) {
            throw new BankingException("Customer not found");
        }
        
        Bill bill = new Bill();
        bill.setCustomer(customer.get());
        bill.setBillType(billType);
        bill.setBillAmount(billAmount);
        bill.setBiller(biller);
        bill.setDueDate(dueDate);
        bill.setStatus("PENDING");
        
        return billRepository.save(bill);
    }
    
    /**
     * Major Use Case: Pay Bills
     */
    public Bill payBill(Long billId, Long accountId) throws BankingException {
        Optional<Bill> billOpt = billRepository.findById(billId);
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        
        if (billOpt.isEmpty()) {
            throw new BankingException("Bill not found");
        }
        
        if (accountOpt.isEmpty()) {
            throw new BankingException("Account not found");
        }
        
        Bill bill = billOpt.get();
        Account account = accountOpt.get();
        
        // Check if account belongs to bill customer
        if (!account.getCustomer().getId().equals(bill.getCustomer().getId())) {
            throw new BankingException("Account does not belong to bill customer");
        }
        
        // Check sufficient balance
        if (!account.hasSufficientBalance(bill.getBillAmount())) {
            throw new BankingException("Insufficient balance to pay bill");
        }
        
        try {
            // Withdraw amount from account (GRASP - Information Expert)
            account.withdraw(bill.getBillAmount());
            accountRepository.save(account);
            
            // Record transaction
            Transaction transaction = new Transaction();
            transaction.setTransactionType("BILL_PAYMENT");
            transaction.setAmount(bill.getBillAmount());
            transaction.setDescription("Bill Payment - " + bill.getBillType());
            transaction.setStatus("SUCCESS");
            transaction.setAccount(account);
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            // Update bill status
            bill.setStatus("PAID");
            bill.setPaidDate(LocalDateTime.now());
            bill.setTransaction(savedTransaction);
            
            return billRepository.save(bill);
        } catch (Exception e) {
            throw new BankingException("Bill payment failed: " + e.getMessage());
        }
    }
    
    /**
     * Get bill by ID
     */
    public Optional<Bill> getBillById(Long id) {
        return billRepository.findById(id);
    }
    
    /**
     * Get all pending bills for a customer
     */
    public List<Bill> getPendingBills(Long customerId) {
        return billRepository.findByCustomerIdAndStatus(customerId, "PENDING");
    }
    
    /**
     * Get all bills for a customer
     */
    public List<Bill> getCustomerBills(Long customerId) {
        return billRepository.findByCustomerId(customerId);
    }
}
