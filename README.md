# Online Banking System - OOAD Mini Project

## Overview

A comprehensive **Online Banking System** built with Java and Spring Boot, demonstrating Object-Oriented Analysis and Design (OOAD) principles and GRASP (General Responsibility Assignment Software Patterns) design patterns.

## Key Features

### Core Banking Operations
- ✅ Customer Account Creation (with automatic savings account)
- ✅ Secure User Authentication (password encryption with BCrypt)
- ✅ Fund Transfers between accounts
- ✅ Bill Payment System
- ✅ Transaction History Tracking
- ✅ Receipt Generation
- ✅ Account Balance Management
- ✅ User Profile Management

### System Architecture
- **MVC Architecture** with Spring Boot
- **Layered Architecture** (Presentation, Controller, Service, Repository, Model)
- **Database**: MySQL with JPA/Hibernate ORM
- **RESTful API** for all operations
- **Role-based Support**: Customer and Manager roles

### GRASP Principles Applied

1. **Information Expert**: Account manages balance, Customer manages accounts
2. **Creator**: Services create entities, Customer creates accounts
3. **Controller**: Service layer coordinates business operations
4. **Low Coupling**: Repository pattern, Dependency Injection
5. **High Cohesion**: Single responsibility per class

## Project Structure

```
online-banking-system/
├── src/
│   ├── main/
│   │   ├── java/com/banking/
│   │   │   ├── OnlineBankingApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/           (5 REST Controllers)
│   │   │   ├── dto/                  (Request/Response DTOs)
│   │   │   ├── model/                (7 Entity Classes)
│   │   │   ├── repository/           (7 JPA Repositories)
│   │   │   ├── service/              (5 Business Logic Services)
│   │   │   └── exception/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── README.md (this file)
├── OOAD_DOCUMENTATION.md
├── UML_DIAGRAMS.md
└── SETUP_AND_TESTING.md
```

## Use Cases Implemented

### Major Use Cases (4)
1. **Create Account** - Customer signup with automatic savings account
2. **Transfer Money** - Send money between accounts with validation
3. **Pay Bills** - Pay bills from customer accounts
4. **Manage Users** - Update customer profiles and account management

### Minor Use Cases (4)
1. **Login** - User authentication
2. **Validate User** - Credential validation during login
3. **Generate Receipt** - Create receipt for transactions
4. **Check Balance** - Query account balance

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.0.0 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Build Tool | Maven 3.6+ |
| Security | Spring Security + BCrypt |
| Additional | Lombok, Jakarta Persistence |

## API Endpoints

### Authentication
```
POST   /api/auth/signup           - Create customer account
POST   /api/auth/login            - User login
GET    /api/auth/profile/{userId} - Get user profile
```

### Accounts
```
POST   /api/accounts/create                    - Create new account
GET    /api/accounts/{accountId}               - Get account details
GET    /api/accounts/{accountId}/balance       - Check balance
GET    /api/accounts/customer/{customerId}    - Get customer accounts
POST   /api/accounts/transfer                  - Transfer money
POST   /api/accounts/{accountId}/deposit       - Deposit money
POST   /api/accounts/{accountId}/withdraw      - Withdraw money
```

### Transactions
```
GET    /api/transactions/history/{accountId}       - Get transaction history
GET    /api/transactions/history/{accountId}/range - Get by date range
```

### Bills
```
POST   /api/bills/create                       - Create bill
POST   /api/bills/{billId}/pay                 - Pay bill
GET    /api/bills/customer/{customerId}/pending - Get pending bills
GET    /api/bills/customer/{customerId}        - Get all bills
```

### Receipts
```
POST   /api/receipts/generate        - Generate receipt
GET    /api/receipts/{receiptId}     - Get receipt
```

## Database Schema

### Main Tables
- **users** (with inheritance: CUSTOMER, MANAGER)
- **accounts** (linked to customers)
- **transactions** (deposit, withdrawal, transfer, bill payment)
- **bills** (pending, paid, overdue)
- **receipts** (generated for transactions)

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Installation

1. **Clone/Download Project**
```bash
cd d:/SEM\ 6/OOAD/project
```

2. **Create Database**
```sql
CREATE DATABASE banking_system;
```

3. **Configure Database** (application.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Build Project**
```bash
mvn clean install
```

5. **Run Application**
```bash
mvn spring-boot:run
```

6. **Access API**
```
http://localhost:8080
```

## Example Usage

### Signup
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phoneNumber": "9876543210"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Transfer Money
```bash
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountNumber": "ACC1234567890",
    "amount": 1000.0,
    "description": "Payment for services"
  }'
```

## Design Patterns Used

### Architectural Patterns
- **MVC** - Model-View-Controller
- **Layered Architecture** - Separation of concerns

### Design Patterns
- **Repository Pattern** - Data access abstraction
- **Service Layer Pattern** - Business logic encapsulation
- **DTO Pattern** - Decoupling API from entities
- **Dependency Injection** - Loose coupling
- **Factory Pattern** - Object creation (implicit in services)

### GRASP Patterns
- Information Expert
- Creator
- Controller
- Low Coupling
- High Cohesion

## Key Features Demonstration

### 1. Information Expert Pattern
```java
// Account handles its own balance management
public boolean withdraw(Double amount) {
    if (amount > 0 && balance >= amount) {
        balance -= amount;
        return true;
    }
    return false;
}
```

### 2. Creator Pattern
```java
// Customer creates its own accounts
public Account createAccount(String accountType, Double initialBalance) {
    Account account = new Account();
    account.setCustomer(this);
    account.setAccountType(accountType);
    account.setBalance(initialBalance);
    return account;
}
```

### 3. Controller & Service Layer
```java
// Controller delegates to service
@PostMapping("/transfer")
public ResponseEntity<?> transferMoney(@RequestBody TransferRequest req) {
    var transaction = transactionService.transferMoney(
        req.getFromAccountId(),
        req.getToAccountNumber(),
        req.getAmount(),
        req.getDescription()
    );
    return ResponseEntity.ok(response);
}
```

### 4. Low Coupling with Repositories
```java
// Service depends on repository abstraction, not concrete DB
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository; // Injected
    // Services don't know about JDBC or SQL
}
```

### 5. High Cohesion
- Each class has single responsibility
- No mixing of concerns
- Clear separation between layers

## Documentation

- **OOAD_DOCUMENTATION.md** - Comprehensive architecture and GRASP implementation
- **UML_DIAGRAMS.md** - All use case, class, sequence, and entity relationship diagrams
- **SETUP_AND_TESTING.md** - Complete setup and testing guide with curl commands

## Project Statistics

| Metric | Value |
|--------|-------|
| Java Classes | 25+ |
| Entity Models | 7 |
| Repositories | 7 |
| Services | 5 |
| Controllers | 5 |
| DTOs | 3 |
| Database Tables | 5 main + inheritance |
| API Endpoints | 20+ |
| Use Cases | 8 (4 major + 4 minor) |

## GRASP Principles Summary

| Principle | Implementation | Location |
|-----------|----------------|----------|
| Information Expert | Account balance, Customer accounts | model/ |
| Creator | Services create entities | service/ |
| Controller | Service layer coordinates | service/ |
| Low Coupling | Repository pattern, DI | repository/, di |
| High Cohesion | Single responsibility | all classes |

## Error Handling

- **BankingException** - Custom exception for all banking operations
- **Validation** - Input validation on all operations
- **HTTP Status Codes** - Appropriate status codes in responses
- **Error Messages** - Clear, descriptive error messages

## Security Features

- ✅ Password encryption (BCrypt)
- ✅ User validation
- ✅ Account ownership verification
- ✅ Transaction authorization
- ✅ Active status checks

## Testing Instructions

For detailed testing with curl commands, see [SETUP_AND_TESTING.md](SETUP_AND_TESTING.md)

## Future Enhancements

1. Two-factor authentication (2FA)
2. Role-based access control (RBAC)
3. Transaction limits and alerts
4. Notification system (Email/SMS)
5. Advanced reporting
6. Mobile application
7. Blockchain integration
8. Fraud detection with ML

## Contributing

This project is for educational purposes. It demonstrates OOAD principles and GRASP patterns in a real-world banking system context.

## License

Educational Project - OOAD Mini Project

## Acknowledgments

- Spring Boot Documentation
- GRASP Pattern Best Practices
- OOAD Design Principles
- MySQL Documentation

## Contact

For questions about the project design and GRASP implementation, refer to:
- OOAD_DOCUMENTATION.md
- UML_DIAGRAMS.md
- Source code comments

---

**Project Status**: ✅ Complete with all 8 use cases, 5 GRASP principles, full documentation and UML diagrams

## Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or using JAR
java -jar target/online-banking-system-1.0.0.jar
```

Access at `http://localhost:8080`

See [SETUP_AND_TESTING.md](SETUP_AND_TESTING.md) for comprehensive testing guide.

#   O O A D _ p r o j e c t  
 