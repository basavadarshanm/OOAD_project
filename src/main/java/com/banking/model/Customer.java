package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * GRASP - Information Expert: Customer is responsible for managing its own accounts
 * as it has the necessary data about customer-specific information
 */
@Entity
@DiscriminatorValue("CUSTOMER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends User {
    
    @Column(name = "aadhar_number", unique = true, length = 12)
    private String aadharNumber;
    
    @Column(length = 50)
    private String dateOfBirth;
    
    @Column(length = 200)
    private String address;
    
    @Column(length = 50)
    private String city;
    
    @Column(length = 50)
    private String state;
    
    @Column(length = 10)
    private String pincode;
    
    // One Customer can have many Accounts
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;
    
    /**
     * GRASP - Creator: Customer creates its own accounts logically
     * This method demonstrates the Creator pattern where Customer is responsible
     * for creating Account objects
     */
    public Account createAccount(String accountType, Double initialBalance) {
        Account account = new Account();
        account.setCustomer(this);
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        account.setIsActive(true);
        return account;
    }
}
