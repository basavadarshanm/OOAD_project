package com.banking.service;

import com.banking.exception.BankingException;
import com.banking.model.Receipt;
import com.banking.model.Transaction;
import com.banking.repository.ReceiptRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ReceiptService - Business Logic Layer
 * GRASP - High Cohesion: Focused only on receipt generation
 * GRASP - Creator: Service creates Receipt objects
 * Minor Use Case: Generate Receipt
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptService {
    
    private final ReceiptRepository receiptRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Generate receipt for a transaction - Minor Use Case
     */
    public Receipt generateReceipt(Long transactionId, String receiptType, String details) throws BankingException {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        
        if (transaction.isEmpty()) {
            throw new BankingException("Transaction not found");
        }
        
        Transaction txn = transaction.get();
        
        Receipt receipt = new Receipt();
        receipt.setTransaction(txn);
        receipt.setReceiptType(receiptType);
        receipt.setDetails(details);
        receipt.setAmount(txn.getAmount());
        
        return receiptRepository.save(receipt);
    }
    
    /**
     * Get receipt by ID
     */
    public Optional<Receipt> getReceipt(Long id) {
        return receiptRepository.findById(id);
    }
    
    /**
     * Get receipt by receipt ID
     */
    public Optional<Receipt> getReceiptByReceiptId(String receiptId) {
        return receiptRepository.findByReceiptId(receiptId);
    }
}
