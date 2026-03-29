# Online Banking System - OOAD Mini Project

## Project Overview

This is a comprehensive Online Banking System built with Java Spring Boot, MySQL, and following Object-Oriented Analysis and Design (OOAD) principles. The system demonstrates the application of GRASP (General Responsibility Assignment Software Patterns) principles in a real-world banking application.

## System Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│      Presentation Layer (REST API)      │
│    AuthController, AccountController    │
│  TransactionController, BillController  │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│      Controller Layer (Request Handler) │
│   Coordinates request processing        │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│     Service Layer (Business Logic)      │
│  UserService, AccountService,           │
│  TransactionService, BillService        │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│  Repository/Data Access Layer (JPA)     │
│  UserRepository, AccountRepository,     │
│  TransactionRepository, BillRepository  │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│      Model/Entity Layer (Database)      │
│  User, Customer, Manager, Account,      │
│  Transaction, Bill, Receipt             │
└─────────────────────────────────────────┘
```

## GRASP Principles Implementation

### 1. Information Expert Pattern

**Definition**: Assign a responsibility to the class that has the information needed to fulfill it.

**Implementation in System**:
- **Account Class**: Manages balance updates
  - Contains balance information
  - Implements `withdraw()` and `deposit()` methods
  - Validates sufficient balance before withdrawal
  
```java
// Account.java
public boolean withdraw(Double amount) {
    if (amount > 0 && balance >= amount) {
        balance -= amount;
        return true;
    }
    return false;
}

public void deposit(Double amount) {
    if (amount > 0) {
        balance += amount;
    }
}
```

- **Customer Class**: Manages account creation
  - Contains customer information
  - Creates accounts through `createAccount()` method
  
```java
// Customer.java
public Account createAccount(String accountType, Double initialBalance) {
    Account account = new Account();
    account.setCustomer(this);
    account.setAccountType(accountType);
    account.setBalance(initialBalance);
    return account;
}
```

- **Transaction Class**: Manages transaction data
  - Stores all transaction-related information
  - Has detailed transaction records

### 2. Creator Pattern

**Definition**: Assign object creation responsibility to the class that logically creates them.

**Implementation in System**:
- **Customer creates Account**: Logically, a customer creates their banking accounts
  ```java
  // In Customer class
  public Account createAccount(String accountType, Double initialBalance) {
      Account account = new Account();
      account.setCustomer(this);
      // ... initialization
      return account;
  }
  ```

- **Services create entities**: Business services create transaction and receipt objects
  ```java
  // In TransactionService
  Transaction transaction = new Transaction();
  transaction.setTransactionType("TRANSFER");
  transaction.setAmount(amount);
  // ... more initialization
  return transactionRepository.save(transaction);
  ```

### 3. Controller Pattern

**Definition**: Assign the responsibility for handling system operations to a controller class.

**Implementation in System**:
- **AuthController**: Handles authentication operations
  - Signup (Create Account use case)
  - Login (Login use case)
  - User validation

- **AccountController**: Handles account operations
  - Create account
  - Transfer money
  - Check balance
  - Deposit/Withdraw

- **BillController**: Handles bill operations
  - Create bills
  - Pay bills
  - View pending bills

- **TransactionController**: Handles transaction queries
  - Get transaction history
  - Query by date range

- **ReceiptController**: Handles receipt generation
  - Generate receipt for transactions

### 4. Low Coupling Pattern

**Definition**: Assign responsibilities to keep dependencies between classes minimal.

**Implementation in System**:
- **Repository Pattern**: Decouples business logic from data access
  ```java
  // Service layer doesn't directly access database
  // Instead uses repositories
  @RequiredArgsConstructor
  public class AccountService {
      private final AccountRepository accountRepository;
      // Services depend on abstraction, not concrete database
  }
  ```

- **Dependency Injection**: All dependencies injected
  ```java
  // Controller depends on services through constructor injection
  @RequiredArgsConstructor
  public class AccountController {
      private final AccountService accountService;
      private final TransactionService transactionService;
  }
  ```

- **Service Layer**: Abstracts business logic
  - Controllers don't know about repositories
  - Views only know about DTOs and responses
  - Minimal coupling between layers

### 5. High Cohesion Pattern

**Definition**: Ensure each class has a single, focused responsibility.

**Implementation in System**:
- **User Class**: Manages user authentication data
  - Username, password, email
  - Basic user information
  
- **Customer Class**: Extends User with customer-specific data
  - Aadhar number, DOB, address
  - Account management
  
- **Manager Class**: Extends User with manager-specific data
  - Employee ID, department, branch
  
- **Account Class**: Focused solely on account operations
  - Balance management
  - Withdrawal/Deposit logic
  
- **Transaction Class**: Focused on transaction records
  - Transaction details only
  - No business logic mixing
  
- **Bill Class**: Focused on bill information
  - Bill details and status
  
- **Receipt Class**: Focused on receipt generation
  - Receipt details only

## Use Cases Implemented

### Major Use Cases (4)

1. **Create Account**
   - Flow: Customer signup → Account creation with SAVINGS account
   - Controllers: AuthController.signup()
   - Services: UserService.signup(), AccountService.createAccount()
   - Entities: Customer, Account

2. **Transfer Money**
   - Flow: Validate accounts → Check balance → Debit source → Credit destination
   - Controllers: AccountController.transferMoney()
   - Services: TransactionService.transferMoney()
   - Entities: Account, Transaction
   - GRASP Applied: Information Expert (Account manages balance), Low Coupling (Service layer)

3. **Pay Bills**
   - Flow: Get bill → Validate account → Debit account → Mark bill as paid
   - Controllers: BillController.payBill()
   - Services: BillService.payBill()
   - Entities: Bill, Account, Transaction
   - GRASP Applied: Controller (handles operation), Information Expert (Account manages withdrawal)

4. **Manage Users**
   - Flow: Update customer profile → Deactivate accounts
   - Controllers: AuthController (implicit)
   - Services: UserService.updateCustomer(), UserService.deactivateUser()
   - Entities: Customer, User

### Minor Use Cases (4)

1. **Login**
   - Flow: Validate username → Check password → Return user
   - Controllers: AuthController.login()
   - Services: UserService.login()
   - GRASP Applied: Controller (handles system operation)
   - Validation: UserService validates credentials

2. **Validate User**
   - Flow: Check if user exists → Verify password → Check active status
   - Services: UserService.login() includes validation
   - GRASP Applied: Information Expert (User has credential information)

3. **Generate Receipt**
   - Flow: Get transaction → Create receipt object → Save receipt
   - Controllers: ReceiptController.generateReceipt()
   - Services: ReceiptService.generateReceipt()
   - Entities: Receipt, Transaction
   - GRASP Applied: Creator (ReceiptService creates Receipt objects)

4. **Check Balance**
   - Flow: Get account → Return balance
   - Controllers: AccountController.checkBalance()
   - Services: AccountService.checkBalance()
   - GRASP Applied: Information Expert (Account has balance information)

## Database Schema

### User Table (Parent)
- id (PK)
- user_type (DISCRIMINATOR: CUSTOMER, MANAGER)
- username (UNIQUE)
- password
- email
- full_name
- phone_number
- is_active
- created_at
- updated_at

### Customer Table (Inherited)
- aadhar_number
- date_of_birth
- address
- city
- state
- pincode

### Manager Table (Inherited)
- employee_id
- department
- branch

### Account Table
- id (PK)
- account_number (UNIQUE)
- account_type
- balance
- is_active
- customer_id (FK)
- created_at
- updated_at

### Transaction Table
- id (PK)
- transaction_id (UNIQUE)
- transaction_type
- amount
- description
- status
- account_id (FK)
- receiver_account_number
- transaction_date

### Bill Table
- id (PK)
- bill_id (UNIQUE)
- bill_type
- bill_amount
- biller
- due_date
- status
- customer_id (FK)
- transaction_id (FK)
- created_at
- paid_date

### Receipt Table
- id (PK)
- receipt_id (UNIQUE)
- receipt_type
- details
- amount
- transaction_id (FK)
- generated_at

## API Endpoints

### Authentication APIs
- `POST /api/auth/signup` - Create new customer account
- `POST /api/auth/login` - Login user
- `GET /api/auth/profile/{userId}` - Get user profile

### Account APIs
- `POST /api/accounts/create` - Create new account
- `GET /api/accounts/{accountId}` - Get account details
- `GET /api/accounts/{accountId}/balance` - Check account balance
- `GET /api/accounts/customer/{customerId}` - Get customer's accounts
- `POST /api/accounts/transfer` - Transfer money
- `POST /api/accounts/{accountId}/deposit` - Deposit money
- `POST /api/accounts/{accountId}/withdraw` - Withdraw money

### Transaction APIs
- `GET /api/transactions/history/{accountId}` - Get transaction history
- `GET /api/transactions/history/{accountId}/range` - Get transactions by date range

### Bill APIs
- `POST /api/bills/create` - Create bill
- `POST /api/bills/{billId}/pay` - Pay bill
- `GET /api/bills/customer/{customerId}/pending` - Get pending bills
- `GET /api/bills/customer/{customerId}` - Get all bills

### Receipt APIs
- `POST /api/receipts/generate` - Generate receipt
- `GET /api/receipts/{receiptId}` - Get receipt details

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.0.0
- **ORM**: Spring Data JPA with Hibernate
- **Database**: MySQL 8.0
- **Security**: Spring Security with BCrypt
- **Build Tool**: Maven
- **Additional Libraries**: Lombok (for reducing boilerplate code)

## Project Structure

```
online-banking-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banking/
│   │   │           ├── OnlineBankingApplication.java
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── AccountController.java
│   │   │           │   ├── TransactionController.java
│   │   │           │   ├── BillController.java
│   │   │           │   └── ReceiptController.java
│   │   │           ├── dto/
│   │   │           │   ├── SignupRequest.java
│   │   │           │   ├── LoginRequest.java
│   │   │           │   └── TransferRequest.java
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   ├── Customer.java
│   │   │           │   ├── Manager.java
│   │   │           │   ├── Account.java
│   │   │           │   ├── Transaction.java
│   │   │           │   ├── Bill.java
│   │   │           │   └── Receipt.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── CustomerRepository.java
│   │   │           │   ├── AccountRepository.java
│   │   │           │   ├── TransactionRepository.java
│   │   │           │   ├── BillRepository.java
│   │   │           │   └── ReceiptRepository.java
│   │   │           ├── service/
│   │   │           │   ├── UserService.java
│   │   │           │   ├── AccountService.java
│   │   │           │   ├── TransactionService.java
│   │   │           │   ├── BillService.java
│   │   │           │   └── ReceiptService.java
│   │   │           └── exception/
│   │   │               └── BankingException.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── banking/
│                   └── [test classes]
└── pom.xml
```

## Key Design Decisions

1. **Single Table Inheritance for Users**: Used JPA's SINGLE_TABLE inheritance strategy to keep User, Customer, and Manager in one table while allowing polymorphic queries and specialization.

2. **Transaction Management**: Marked service methods with `@Transactional` to handle database transactions atomically.

3. **UUID for Business IDs**: Used timestamp-based IDs for account numbers, transaction IDs, bills, and receipts to ensure uniqueness without depending on database sequences.

4. **Dependency Injection**: All dependencies are injected through constructor, promoting loose coupling and easier testing.

5. **Repository Pattern**: Used Spring Data JPA repositories to abstract database access, following Low Coupling principle.

6. **DTOs for API Communication**: Created separate Data Transfer Objects for API requests to decouple API contracts from entity models.

## Running the Application

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Setup Instructions

1. **Create Database**
```sql
CREATE DATABASE banking_system;
```

2. **Configure Database Connection**
Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=your_password
```

3. **Build Project**
```bash
mvn clean build
```

4. **Run Application**
```bash
mvn spring-boot:run
```

5. **Access Application**
```
http://localhost:8080
```

## GRASP Principles Summary

| Principle | Implementation | Benefit |
|-----------|----------------|---------|
| Information Expert | Account manages balance, Customer creates accounts | Each class uses its own data to fulfill responsibilities |
| Creator | Services create entities, Customer creates accounts | Clear object creation patterns and reduced coupling |
| Controller | Dedicated service classes act as operation coordinators | Centralized business logic handling |
| Low Coupling | Repository pattern, Service layer, Dependency injection | Easy maintenance and testing |
| High Cohesion | Single responsibility per class | Clear, focused, and maintainable code |

## Future Enhancements

1. Role-based access control (RBAC)
2. Two-factor authentication (2FA)
3. Transaction limits and monitoring
4. Notification system (email, SMS)
5. Report generation
6. Mobile application
7. Blockchain for transaction verification
8. Machine learning for fraud detection

## Conclusion

This Online Banking System demonstrates the practical application of OOAD principles and GRASP patterns in building a scalable, maintainable, and secure banking application. The layered architecture ensures separation of concerns, and the consistent application of GRASP patterns makes the codebase easy to understand and extend.
