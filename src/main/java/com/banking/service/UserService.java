package com.banking.service;

import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UserService - Business Logic Layer
 * GRASP - Controller: Service acts as a controller/coordinator for user operations
 * GRASP - Low Coupling: Dependencies are injected, reducing coupling
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    
    /**
     * Signup - Major Use Case: Create Account
     */
    public Customer signup(Customer customer, String password) throws BankingException {
        // Validate User
        if (userRepository.existsByUsername(customer.getUsername())) {
            throw new BankingException("Username already exists");
        }
        
        // Encode password
        customer.setPassword(passwordEncoder.encode(password));
        customer.setIsActive(true);
        
        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        
        // Create default account for customer
        Account account = savedCustomer.createAccount("SAVINGS", 0.0);
        accountRepository.save(account);
        
        return savedCustomer;
    }
    
    /**
     * Login - Minor Use Case: Login
     */
    public User login(String username, String password) throws BankingException {
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isEmpty() || !user.get().getIsActive()) {
            throw new BankingException("Invalid username or account is inactive");
        }
        
        // Validate User - Minor Use Case
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BankingException("Invalid password");
        }
        
        return user.get();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get customer details
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    /**
     * Manage Users - Major Use Case
     */
    public Customer updateCustomer(Long id, Customer customerDetails) throws BankingException {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        
        if (existingCustomer.isEmpty()) {
            throw new BankingException("Customer not found");
        }
        
        Customer customer = existingCustomer.get();
        customer.setFullName(customerDetails.getFullName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        customer.setAddress(customerDetails.getAddress());
        customer.setCity(customerDetails.getCity());
        customer.setState(customerDetails.getState());
        customer.setPincode(customerDetails.getPincode());
        
        return customerRepository.save(customer);
    }
    
    /**
     * Deactivate user account
     */
    public void deactivateUser(Long id) throws BankingException {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isEmpty()) {
            throw new BankingException("User not found");
        }
        
        User userObj = user.get();
        userObj.setIsActive(false);
        userRepository.save(userObj);
    }
}
