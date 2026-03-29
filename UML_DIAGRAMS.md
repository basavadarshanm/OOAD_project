# UML Diagrams for Online Banking System

## 1. Use Case Diagram

```
User
  ├─ View Profile
  │
Customer
  ├─ Create Account (MAJOR)
  ├─ Login (MINOR)
  ├─ Validate User (MINOR)
  ├─ Check Balance (MINOR)
  ├─ Transfer Money (MAJOR)
  ├─ Pay Bills (MAJOR)
  └─ Generate Receipt (MINOR)

Manager
  ├─ Manage Users (MAJOR)
  └─ View Reports

System
  └─ Transaction History
```

## 2. Class Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                          <<abstract>>                           │
│                           User                                  │
├─────────────────────────────────────────────────────────────────┤
│ - id: Long                                                      │
│ - username: String                                              │
│ - password: String                                              │
│ - email: String                                                 │
│ - fullName: String                                              │
│ - phoneNumber: String                                           │
│ - isActive: Boolean                                             │
│ - createdAt: LocalDateTime                                      │
│ - updatedAt: LocalDateTime                                      │
├─────────────────────────────────────────────────────────────────┤
│ + login(): void                                                 │
│ + updateProfile(): void                                         │
└─────────────────────────────────────────────────────────────────┘
         △                              △
         │ extends                      │ extends
         │                              │
    ┌────┴────┐                    ┌────┴────┐
    │ Customer │                    │ Manager  │
    └────┬────┘                    └──────────┘
         │
         │ 1..*
         ├───────────────────────────────────────────────┐
         │                                               │
         ▼                                               ▼
┌──────────────────────────┐              ┌──────────────────────────┐
│        Account           │              │      Transaction         │
├──────────────────────────┤              ├──────────────────────────┤
│ - id: Long              │──┐            │ - id: Long              │
│ - accountNumber: String │  │            │ - transactionId: String │
│ - accountType: String   │  │            │ - transactionType: Enum │
│ - balance: Double       │  │            │ - amount: Double        │
│ - isActive: Boolean     │  │            │ - description: String   │
│ - createdAt: DateTime   │  │            │ - status: String        │
│ - updatedAt: DateTime   │  │            │ - transactionDate: DateTime
├──────────────────────────┤  │            │ - receiverAccount: String
│ + withdraw(): Boolean   │  │            ├──────────────────────────┤
│ + deposit(): void       │  │            │ + getDetails(): String   │
│ + hasSufBal(): Boolean  │  │            └──────────────────────────┘
└──────────────────────────┘  │
         △                     │
         │ 1..*               │
         │                    ▼
         └─ 0..1 ────────────────┐
                                 │
                        ┌────────────────┐
                        │      Bill      │
                        ├────────────────┤
                        │ - billId: Str   │
                        │ - billType: Str │
                        │ - amount: Double│
                        │ - dueDate: Date │
                        │ - status: String│
                        └────────────────┘
                                 △
                                 │
                        ┌────────────────┐
                        │    Receipt     │
                        ├────────────────┤
                        │ - receiptId: Str
                        │ - amount: Double
                        │ - generatedAt: Date
                        └────────────────┘
```

## 3. Sequence Diagram: Create Account Use Case

```
Customer          AuthController    UserService      Account Service      Repository
   │                    │                 │                  │              │
   │──── signup() ─────→ │                 │                  │              │
   │                    │─── signup() ───→ │                  │              │
   │                    │                 │─ validateUser ──→ │              │
   │                    │                 │ ← valid ─────────  │              │
   │                    │                 │─ encrypt pwd ───→  │              │
   │                    │                 │                   │─ save() ───→ │
   │                    │                 │                   │ ← saved ───  │
   │                    │                 │─ createAccount ──→ │              │
   │                    │                 │                   │─ save() ───→ │
   │                    │                 │                   │ ← saved ───  │
   │                    │ ← saved customer │                  │              │
   │                    │ ← response ────→ │                  │              │
   │ ← account created ─│                 │                  │              │
```

## 4. Sequence Diagram: Transfer Money Use Case

```
Customer      AccountController  TransactionService  AccountRepository  TransactionRepository
   │                   │                 │                  │                    │
   │─ transfer() ─────→ │                 │                  │                    │
   │                    │─ transfer() ──→ │                  │                    │
   │                    │                 │─ getFromAcct ──→ │                    │
   │                    │                 │ ← Account ───────  │                    │
   │                    │                 │─ getToAcct ────→ │                    │
   │                    │                 │ ← Account ───────  │                    │
   │                    │                 │─ validateAccts → │ (inline) ║          │
   │                    │                 │─ withdraw() ────→ │ ║       │          │
   │                    │                 │ ← success ───────  │ ║       │          │
   │                    │                 │─ deposit() ─────→ │ ║       │          │
   │                    │                 │ ← success ───────  │ ║       │          │
   │                    │                 │─ save(txn) ─────────────────────────→ │
   │                    │                 │                                       │ ← saved
   │                    │ ← Transaction ──│                  │                    │
   │ ← success ────────│                 │                  │                    │
```

## 5. Sequence Diagram: Pay Bills Use Case

```
Customer      BillController    BillService    AccountRepository  TransactionRepository
   │                │              │                  │                  │
   │─ payBill() ───→ │              │                  │                  │
   │                 │─ payBill() ─→ │                  │                  │
   │                 │               │─ getBill() ───→  │                  │
   │                 │               │ ← Bill ────────   │                  │
   │                 │               │─ getAccount() ─→ │                  │
   │                 │               │ ← Account ────    │                  │
   │                 │               │─ validate() ─→   │ (inline)         │
   │                 │               │─ withdraw() ────→ │ ║               │
   │                 │               │ ← success ────    │ ║               │
   │                 │               │─ save txn ────────────────────────→ │
   │                 │               │                                   ║ └ saved
   │                 │               │─ updateBill() ──→ │                  │
   │                 │               │ ← Bill ────────    │                  │
   │                 │ ← success ────│                  │                  │
   │ ← Bill Paid ───│                                  │                  │
```

## 6. Entity-Relationship Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│ Customer (extends User)                                          │
│ ├─ PK: id                                                        │
│ ├─ aadhar_number                                                 │
│ ├─ date_of_birth                                                 │
│ ├─ address, city, state, pincode                                 │
│ └─ has 1..* Accounts                                             │
│     │                                                            │
│     ├─ FK: customer_id                                           │
│     └─ Relationship: ONE_TO_MANY                                │
│                                                                  │
•     Account                                                      │
│     ├─ PK: id                                                    │
│     ├─ account_number (UNIQUE)                                   │
│     ├─ account_type (SAVINGS, CURRENT, etc.)                     │
│     ├─ balance                                                   │
│     ├─ is_active                                                 │
│     └─ has 1..* Transactions                                     │
│         │                                                        │
│         ├─ FK: account_id                                        │
│         └─ Relationship: ONE_TO_MANY                            │
│                                                                  │
•            Transaction                                          │
│            ├─ PK: id                                             │
│            ├─ transaction_id (UNIQUE)                            │
│            ├─ transaction_type (WITHDRAWAL, DEPOSIT, TRANSFER)   │
│            ├─ amount                                             │
│            ├─ status (SUCCESS, PENDING, FAILED)                  │
│            ├─ transaction_date                                   │
│            └─ receiver_account_number (for transfers)            │
│                                                                  │
│                 Bill                                             │
│                 ├─ PK: id                                        │
│                 ├─ bill_id (UNIQUE)                              │
│                 ├─ bill_type (ELECTRICITY, WATER, etc.)          │
│                 ├─ bill_amount                                   │
│                 ├─ status (PENDING, PAID, OVERDUE)               │
│                 ├─ FK: customer_id                               │
│                 ├─ FK: transaction_id (nullable)                 │
│                 └─ due_date, paid_date                           │
│                                                                  │
│                 Receipt                                          │
│                 ├─ PK: id                                        │
│                 ├─ receipt_id (UNIQUE)                           │
│                 ├─ receipt_type                                  │
│                 ├─ amount                                        │
│                 ├─ FK: transaction_id                            │
│                 └─ generated_at                                  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## 7. Component Diagram

```
┌───────────────────────────────────────────────────────────┐
│                    Presentation Layer                      │
│  (REST API - Controllers)                                 │
├───────────────────────────────────────────────────────────┤
│ - AuthController                                          │
│ - AccountController                                       │
│ - TransactionController                                   │
│ - BillController                                          │
│ - ReceiptController                                       │
└──────────────────┬──────────────────────────────────────┘
                   │ uses
┌─────────────────┴──────────────────────────────────────┐
│                  Service Layer                         │
│              (Business Logic)                          │
├──────────────────────────────────────────────────────┤
│ - UserService                                        │
│ - AccountService                                     │
│ - TransactionService                                 │
│ - BillService                                        │
│ - ReceiptService                                     │
└─────────────────┬──────────────────────────────────┘
                  │ uses
┌────────────────┴─────────────────────────────────┐
│            Repository Layer                      │
│         (Data Access)                            │
├─────────────────────────────────────────────────┤
│ - UserRepository                                │
│ - CustomerRepository                            │
│ - ManagerRepository                             │
│ - AccountRepository                             │
│ - TransactionRepository                         │
│ - BillRepository                                │
│ - ReceiptRepository                             │
└─────────────────┬─────────────────────────────┘
                  │ query/persist
┌─────────────────┴─────────────────────────────┐
│              MySQL Database                    │
│    (Banking System Schema)                     │
├─────────────────────────────────────────────────┤
│ - users (CUSTOMER, MANAGER inheritance)        │
│ - accounts                                     │
│ - transactions                                 │
│ - bills                                        │
│ - receipts                                     │
└──────────────────────────────────────────────┘
```

## 8. State Diagram: Account Lifecycle

```
        [New Account]
             │
             ▼
        ┌─────────┐
        │  ACTIVE │
        └────┬────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
 Debit/Credit    Account Closed
    │                 │
    └────────┬────────┘
             │
             ▼
        ┌──────────┐
        │ INACTIVE │
        └──────────┘
```

## 9. State Diagram: Bill Lifecycle

```
        [New Bill]
             │
             ▼
        ┌─────────┐
        │ PENDING │
        └────┬────┘
             │
    ┌────────┴─────────┐
    │                  │
    ▼                  ▼
 Payment Made      Due Date Passed
    │                  │
    ▼                  ▼
┌──────┐          ┌─────────┐
│ PAID │          │ OVERDUE │
└──────┘          └─────────┘
```

## 10. Activity Diagram: Transfer Money Process

```
                    Start
                     │
                     ▼
          ┌─────────────────────┐
          │ Receive Transfer    │
          │ Request             │
          └────────┬────────────┘
                   │
                   ▼
          ┌─────────────────────┐
          │ Validate From       │
          │ Account             │
          └────┬──────┬─────────┘
               │      │
          Valid│      │ Invalid
               │      ▼
               │    (Error Response)
               │
               ▼
          ┌─────────────────────┐
          │ Validate To         │
          │ Account             │
          └────┬──────┬─────────┘
               │      │
          Valid│      │ Invalid
               │      ▼
               │    (Error Response)
               │
               ▼
          ┌─────────────────────────┐
          │ Check Sufficient        │
          │ Balance                 │
          └────┬──────┬─────────────┘
               │      │
               │  Yes │  No
               │      ▼
               │   (Error Response)
               │
               ▼
          ┌─────────────────────────┐
          │ Withdraw From Source    │
          │ Account                 │
          └────────┬────────────────┘
                   │
                   ▼
          ┌─────────────────────────┐
          │ Deposit To              │
          │ Destination Account     │
          └────────┬────────────────┘
                   │
                   ▼
          ┌─────────────────────────┐
          │ Record Transaction      │
          └────────┬────────────────┘
                   │
                   ▼
          ┌─────────────────────────┐
          │ Send Success Response   │
          └────────┬────────────────┘
                   │
                   ▼
                  End
```

## 11. Deployment Diagram

```
┌─────────────────────────────────┐
│        Client Browser           │
│  (Access via http://localhost)  │
└──────────────┬──────────────────┘
               │ HTTP/REST
               ▼
        ┌──────────────────┐
        │ Spring Boot      │
        │ Application      │
        ├──────────────────┤
        │ - Controllers    │
        │ - Services       │
        │ - Repositories   │
        │ - JPA            │
        └────┬─────┬──────┘
             │     │ JDBC
             │     └─────┐
             │           ▼
             │      ┌──────────────┐
             │      │ MySQL        │
             │      │ Database     │
             │      ├──────────────┤
             │      │ banking_     │
             │      │ system DB    │
             │      └──────────────┘
             │
             └─ Properties File
                (application.properties)
```

---

## GRASP Principles Mapping to Diagrams

### Information Expert Pattern
- **Account Class**: Manages balance and withdrawal/deposit logic
- **Customer Class**: Creates accounts
- **Transaction Class**: Maintains transaction details

### Creator Pattern
- **Services**: Create entity objects (Transaction, Receipt)
- **Customer**: Creates Account objects

### Controller Pattern
- **Service Layer**: Acts as coordinator for business operations
- **Controllers**: Handle HTTP requests and delegate to services

### Low Coupling
- **Repository Layer**: Decouples services from database
- **DTOs**: Separate API contracts from entity models
- **Dependency Injection**: All dependencies explicitly provided

### High Cohesion
- Each class has single responsibility
- Clear separation between layers
- No mixing of concerns

