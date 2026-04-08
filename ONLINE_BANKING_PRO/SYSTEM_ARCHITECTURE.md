# Online Banking Application - Complete System Design

**Version:** 1.0.0  
**Date:** April 2, 2026  
**Status:** Production-Ready  

---

## Table of Contents
1. [System Overview](#system-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture Design](#architecture-design)
4. [Database Schema](#database-schema)
5. [Security Architecture](#security-architecture)
6. [API Design](#api-design)
7. [Deployment Guide](#deployment-guide)

---

## 1. System Overview

### High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                      │
├─────────────────────────────────────────────────────────────────┤
│  React SPA (Dashboard) │ Admin Portal │ Mobile App (React Native)│
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              API GATEWAY (Kong/Nginx)                    │  │
│  │  - Load balancing, Rate limiting, SSL termination       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                               │                                  │
├─────────────────────────────────────────────────────────────────┤
│                    APPLICATION LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │   Auth   │  │ Account  │  │Transfer  │  │  Loan    │        │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │   Card   │  │  Fraud   │  │   AI &   │  │ Payment  │        │
│  │ Service  │  │Detection │  │ Analytics │  │ Service  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                    DATA & PERSISTENCE LAYER                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Primary DB (PostgreSQL) │ Redis Cache │ Elasticsearch (Logs)  │
│  - User Data             │ - Sessions  │ - Audit Trail        │
│  - Transactions          │ - OTP Cache │ - Search Index       │
│  - Account Info          │ - Tokens    │                       │
│                                                                   │
├─────────────────────────────────────────────────────────────────┤
│                    SUPPORTING SERVICES                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Message Queue (RabbitMQ/Kafka) │ Email/SMS Service (Twilio)    │
│  - Async transactions            │ - OTP delivery               │
│  - Audit logging                 │ - Notifications              │
│  - Event streaming               │                               │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Technology Stack

### Backend
| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| **Language** | Java | 21 LTS | Type-safe, enterprise-grade, JVM performance |
| **Framework** | Spring Boot | 3.2.x | Rapid development, extensive ecosystem |
| **Security** | Spring Security | 6.2.x | OAuth2, JWT, RBAC support |
| **Database ORM** | JPA/Hibernate | 6.4.x | Automatic CRUD, lazy loading, query optimization |
| **Validation** | Jakarta Bean Validation | 3.0 | Declarative, cross-layer validation |
| **REST Framework** | Spring Web/REST | 6.2.x | RESTful API, content negotiation |
| **Async Processing** | Spring Async/CompletableFuture | - | Non-blocking I/O, reactive patterns |
| **Caching** | Spring Data Redis | 3.2.x | High-performance in-memory caching |
| **Message Queue** | Spring AMQP (RabbitMQ) | 2.5.x | Event-driven architecture |
| **Monitoring** | Spring Boot Actuator + Micrometer | 3.2.x | Metrics, health checks, monitoring |
| **Testing** | JUnit 5, Mockito, TestContainers | 5.10.x | Unit, integration, E2E testing |
| **Build Tool** | Maven | 3.9.x | Dependency management, plugin ecosystem |
| **API Documentation** | SpringFox / Springdoc OpenAPI | 2.x | Swagger/OpenAPI documentation |

### Frontend
| Component | Technology | Version | Rationale |
|-----------|-----------|---------|-----------|
| **Library** | React | 18.x | Component-based, virtual DOM, large ecosystem |
| **Language** | TypeScript | 5.x | Type safety, IDE support, large codebase maintainability |
| **State Management** | Redux Toolkit | 1.9.x | Centralized state, time-travel debugging |
| **UI Framework** | Material-UI / Ant Design | 5.x | Rich components, accessibility, theming |
| **HTTP Client** | Axios | 1.6.x | Promise-based, interceptors, request cancellation |
| **Forms** | React Hook Form | 7.x | Performant, minimal re-renders, validation integration |
| **Routing** | React Router | 6.x | Dynamic routing, nested routes, lazy loading |
| **Charts & Analytics** | Chart.js / Recharts | 4.x | Beautiful data visualization |
| **Build Tool** | Vite | 5.x | Fast dev server, optimized production builds |
| **Testing** | Jest + React Testing Library | - | Unit and component testing |
| **Package Manager** | npm / pnpm | - | Dependency management |

### Database
| Component | Technology | Rationale |
|-----------|-----------|-----------|
| **Primary Database** | PostgreSQL 15+ | ACID compliance, JSON support, advanced indexing, open-source |
| **Cache Layer** | Redis 7.x | Sub-millisecond latency, session management, rate limiting |
| **Search Index** | Elasticsearch 8.x | Full-text search, log aggregation, analytics |
| **Message Queue** | RabbitMQ 3.12+ | Reliable message delivery, fanout exchanges, dead-letter queues |

### DevOps & Infrastructure
| Component | Technology | Rationale |
|-----------|-----------|-----------|
| **Containerization** | Docker | Container images for consistent environments |
| **Orchestration** | Kubernetes | Auto-scaling, self-healing, rolling updates |
| **CI/CD** | GitHub Actions / Jenkins | Automated testing, building, deployment |
| **API Gateway** | Kong / Nginx | Load balancing, SSL termination, rate limiting |
| **Monitoring** | Prometheus + Grafana | Metrics collection, visualization, alerting |
| **Logging** | ELK Stack (Elasticsearch, Logstash, Kibana) | Centralized logging, analysis |
| **Secrets Management** | HashiCorp Vault | Secure credential storage, rotation |
| **IaC** | Terraform | Infrastructure as code, reproducible deployments |

---

## 3. Architecture Design

### 3.1 Microservices Architecture (Future-Ready)

```
Current: Monolithic with Service-oriented layers
Future: Migrate to microservices per domain

┌──────────────────────────────────────────────────────────┐
│         API GATEWAY (Kong/Nginx)                        │
│  - Authentication, Rate limiting, Request routing       │
└────────────────┬─────────────────────────────────────────┘
                 │
     ┌───────────┼───────────┬────────────┬───────────┐
     │           │           │            │           │
┌────▼──┐  ┌─────▼──┐  ┌────▼──┐  ┌──────▼─┐  ┌─────▼──┐
│ Auth  │  │Account │  │Transfer│  │ Card  │  │ Loan  │
│ Svc   │  │  Svc   │  │  Svc  │  │ Svc  │  │ Svc  │
└───────┘  └────────┘  └────────┘  └───────┘  └────────┘
    │          │          │            │          │
    │    ┌─────┴──────────┴────────────┴──────────┴─────┐
    │    │                                              │
    │    ▼                                              ▼
    │  PostgreSQL (Primary)              Cache & Message Queue
    │  - Users, Auth tokens             - Redis (Sessions, Cache)
    │  - Accounts, Transactions         - RabbitMQ (Events)
    │  - Loans, Cards, Beneficiaries    - Elasticsearch (Logs)
    │
    └─► Fraud Detection Service (Python/Scala)
        - Anomaly Detection
        - Rule-based alerts
        - ML Model serving
```

### 3.2 Layered Architecture (Current Implementation)

```
┌────────────────────────────────────┐
│   PRESENTATION LAYER               │
│  (Controllers, REST endpoints)     │
└────────────────┬───────────────────┘
                 │
┌────────────────▼───────────────────┐
│   APPLICATION/SERVICE LAYER         │
│  (Business logic, transactions)    │
├────────────────────────────────────┤
│ - AuthService      - AccountService│
│ - TransferService  - CardService   │
│ - LoanService      - PaymentService│
│ - FraudDetection   - Analytics     │
└────────────────┬───────────────────┘
                 │
┌────────────────▼───────────────────┐
│   DATA ACCESS LAYER                 │
│  (JPA Repositories, queries)       │
├────────────────────────────────────┤
│ - UserRepository                   │
│ - AccountRepository                │
│ - TransactionRepository            │
│ - LoanRepository                   │
└────────────────┬───────────────────┘
                 │
┌────────────────▼───────────────────┐
│   DATABASE LAYER                    │
│  (PostgreSQL, Redis, Elasticsearch)│
└────────────────────────────────────┘
```

### 3.3 Security Layers

```
Request Flow with Security:

1. Client (React SPA Browser)
   │
   ├─► CORS Check (Spring Security)
   │
2. API Gateway (Kong/Nginx)
   │
   ├─► SSL/TLS Termination
   ├─► Rate Limiting (Redis)
   ├─► IP Whitelisting
   │
3. Spring Security Filter Chain
   │
   ├─► JWT Token Validation
   ├─► OTP Verification (if 2FA enabled)
   ├─► OAuth2 Authorization
   │
4. Role-Based Access Control (RBAC)
   │
   ├─► @PreAuthorize("hasRole('ADMIN')")
   ├─► @PreAuthorize("hasPermission(...)")
   │
5. Method-Level Security
   │
   ├─► Audit logging before/after
   ├─► Sensitive data encryption/masking
   │
6. Database-Level Security
   │
   ├─► Row-level encryption (for PII)
   ├─► Parameterized queries (SQL injection prevention)
   ├─► Database audit triggers
```

---

## 4. Database Schema

### Entity-Relationship Diagram (Text Format)

```
┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│    User      │1       m │   Account    │1       m │ Transaction  │
├──────────────┤◄────────►├──────────────┤◄────────►├──────────────┤
│ user_id (PK) │         │account_id(PK)│         │trans_id (PK) │
│ email        │         │user_id (FK)  │         │account_id(FK)│
│ password     │         │account_type  │         │amount        │
│ full_name    │         │balance       │         │trans_type    │
│ phone        │         │currency      │         │description   │
│ status       │         │created_at    │         │created_at    │
│ kyc_status   │         │updated_at    │         │status        │
└──────────────┘         └──────────────┘         └──────────────┘
       │1                        │                        
       │ m                       │
       ▼                         ▼
┌──────────────┐         ┌──────────────┐
│   Token      │         │   Beneficiary│
├──────────────┤         ├──────────────┤
│token_id (PK) │         │benef_id (PK) │
│user_id (FK)  │         │user_id (FK)  │
│token_value   │         │account_num   │
│token_type    │         │account_holder│
│expiry_at     │         │bank_name     │
│created_at    │         │status        │
└──────────────┘         └──────────────┘
       
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Card       │1       m │  CreditLimit │         │   Loan       │
├──────────────┤◄────────►├──────────────┤         ├──────────────┤
│card_id (PK)  │         │limit_id (PK) │         │loan_id (PK)  │
│account_id(FK)│         │card_id (FK)  │         │account_id(FK)│
│card_number   │         │limit_amount  │         │principal     │
│card_type     │         │used_amount   │         │rate_of_int   │
│status        │         │created_at    │         │term_months   │
│issued_at     │         └──────────────┘         │status        │
│expires_at    │                                  │created_at    │
└──────────────┘                                  └──────────────┘
       
┌──────────────┐         ┌──────────────┐
│   OTP        │         │ AuditLog     │
├──────────────┤         ├──────────────┤
│otp_id (PK)   │         │log_id (PK)   │
│user_id (FK)  │         │user_id (FK)  │
│otp_code      │         │action        │
│otp_type      │         │timestamp     │
│expiry_at     │         │ip_address    │
│is_verified   │         │user_agent    │
│attempts      │         │status        │
└──────────────┘         └──────────────┘
```

### SQL Schema (Key Tables)

```sql
-- Users Table
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    gender CHAR(1),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(10),
    country VARCHAR(100),
    kyc_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    kyc_document_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, LOCKED, SUSPENDED, DELETED
    profile_picture_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    two_fa_enabled BOOLEAN DEFAULT FALSE,
    two_fa_method VARCHAR(20), -- EMAIL, SMS, AUTHENTICATOR
    CONSTRAINT email_format CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$')
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- Accounts Table
CREATE TABLE accounts (
    account_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(50) NOT NULL, -- CHECKING, SAVINGS, MONEY_MARKET
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    interest_rate DECIMAL(5,3) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, FROZEN
    is_primary BOOLEAN DEFAULT FALSE,
    overdraft_limit DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_balance CHECK (balance >= 0),
    CONSTRAINT unique_primary_per_user UNIQUE (user_id, is_primary) WHERE is_primary = TRUE
);

CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);

-- Transactions Table (Immutable - for compliance)
CREATE TABLE transactions (
    transaction_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(account_id),
    transaction_type VARCHAR(50) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN, PAYMENT
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3),
    description TEXT,
    reference_number VARCHAR(50) UNIQUE,
    beneficiary_id BIGINT REFERENCES beneficiaries(beneficiary_id),
    transaction_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED, REVERSED
    fraud_risk_score DECIMAL(3,2), -- 0.00 to 1.00
    is_flagged BOOLEAN DEFAULT FALSE,
    ip_address INET,
    device_info VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT positive_amount CHECK (amount > 0)
);

CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_created ON transactions(created_at);
CREATE INDEX idx_transactions_status ON transactions(transaction_status);
CREATE INDEX idx_transactions_risk ON transactions(fraud_risk_score) WHERE is_flagged = TRUE;

-- Beneficiaries Table
CREATE TABLE beneficiaries (
    beneficiary_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    account_number VARCHAR(20) NOT NULL,
    account_holder_name VARCHAR(255) NOT NULL,
    bank_name VARCHAR(255),
    bank_code VARCHAR(20),
    is_internal BOOLEAN DEFAULT FALSE, -- Same bank transfer
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, DELETED
    is_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_beneficiary UNIQUE (user_id, account_number)
);

CREATE INDEX idx_beneficiaries_user ON beneficiaries(user_id);

-- Cards Table
CREATE TABLE cards (
    card_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(account_id),
    card_number VARCHAR(20) NOT NULL, -- Encrypted in application
    card_type VARCHAR(50), -- DEBIT, CREDIT
    card_brand VARCHAR(50), -- VISA, MASTERCARD, AMEX
    cardholder_name VARCHAR(255),
    cvv_hash VARCHAR(255), -- Never store actual CVV
    expiry_month INT CHECK (expiry_month BETWEEN 1 AND 12),
    expiry_year INT,
    issued_at DATE,
    expires_at DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, BLOCKED, EXPIRED, CLOSED
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_expiry CHECK (expiry_year > EXTRACT(YEAR FROM CURRENT_DATE) 
                                   OR (expiry_year = EXTRACT(YEAR FROM CURRENT_DATE) 
                                   AND expiry_month >= EXTRACT(MONTH FROM CURRENT_DATE)))
);

CREATE INDEX idx_cards_account ON cards(account_id);

-- Loans Table
CREATE TABLE loans (
    loan_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(account_id),
    loan_type VARCHAR(50), -- PERSONAL, HOME, AUTO, EDUCATION
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,3) NOT NULL,
    term_months INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, DEFAULTED
    start_date DATE,
    end_date DATE,
    emi_amount DECIMAL(15,2),
    paid_amount DECIMAL(15,2) DEFAULT 0.00,
    outstanding_amount DECIMAL(15,2),
    last_payment_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_principal CHECK (principal_amount > 0),
    CONSTRAINT positive_rate CHECK (interest_rate >= 0),
    CONSTRAINT positive_term CHECK (term_months > 0)
);

CREATE INDEX idx_loans_account ON loans(account_id);
CREATE INDEX idx_loans_status ON loans(status);

-- OTP Table
CREATE TABLE otp (
    otp_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    otp_code VARCHAR(10) NOT NULL,
    otp_type VARCHAR(20), -- EMAIL, SMS, TRANSACTION
    is_verified BOOLEAN DEFAULT FALSE,
    attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    verified_at TIMESTAMP,
    CONSTRAINT max_attempts CHECK (attempts <= 5)
);

CREATE INDEX idx_otp_user ON otp(user_id);
CREATE INDEX idx_otp_expires ON otp(expires_at);

-- Audit Log Table
CREATE TABLE audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    status VARCHAR(20), -- SUCCESS, FAILURE
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_created ON audit_logs(created_at);

-- Fraud Detection Alerts
CREATE TABLE fraud_alerts (
    alert_id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT REFERENCES transactions(transaction_id),
    user_id BIGINT REFERENCES users(user_id),
    alert_type VARCHAR(50), -- UNUSUAL_LOCATION, HIGH_AMOUNT, MULTIPLE_FAILED_ATTEMPTS
    risk_score DECIMAL(3,2),
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, REVIEWED, RESOLVED, FALSE_POSITIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT REFERENCES users(user_id)
);

CREATE INDEX idx_fraud_alerts_status ON fraud_alerts(status);
CREATE INDEX idx_fraud_alerts_created ON fraud_alerts(created_at);
```

---

## 5. Security Architecture

### 5.1 Authentication & Authorization

```
┌─────────────────────────────────────────────────────┐
│           User Login Flow                           │
├─────────────────────────────────────────────────────┤

1. User Registration
   ├─ Password validation (length, complexity)
   ├─ Bcrypt hash: rounds = 10+ (takes ~100ms)
   ├─ Email verification link sent
   └─ Account created in PENDING status

2. User Login
   ├─ Email + Password submitted via HTTPS
   ├─ Query user by email
   ├─ Verify password: bcrypt.compare(password, hash)
   ├─ Check account status (not LOCKED, not SUSPENDED)
   ├─ Generate JWT Token
   │  ├─ Header: { alg: "HS256", typ: "JWT" }
   │  ├─ Payload: { 
   │  │    sub: user_id,
   │  │    email: email,
   │  │    roles: [ROLE_USER, ...],
   │  │    iat: now,
   │  │    exp: now + 1hour,
   │  │    jti: unique_token_id (for token revocation)
   │  │  }
   │  └─ Signature: HMAC256(header.payload, secret_key)
   │
   ├─ IF 2FA enabled:
   │  ├─ Generate 6-digit OTP
   │  ├─ Store OTP in Redis with 5-minute expiry
   │  ├─ Send via SMS/Email/Authenticator app
   │  └─ Return temporary token (valid for 5 min)
   │
   ├─ Verify OTP
   │  ├─ Compare OTP from request with Redis value
   │  ├─ Check attempts (max 5)
   │  ├─ If success: issue full JWT token + refresh token
   │  └─ If fail: increment attempts, lock account after 5 failures
   │
   ├─ Store refresh token in Redis
   │  ├─ Key: refresh_token_{user_id}_{jti}
   │  ├─ Value: hashed_refresh_token
   │  └─ TTL: 7 days
   │
   └─ Return to client:
      ├─ Access Token (JWT, expires in 1 hour)
      ├─ Refresh Token (for getting new access token)
      └─ User info (id, email, roles, 2fa_enabled)

3. Token Refresh
   ├─ Client sends refresh token
   ├─ Verify refresh token (hash match, not expired)
   ├─ Generate new access token
   └─ Store new refresh token in Redis

4. All Subsequent Requests
   ├─ Include Authorization header: Bearer {access_token}
   ├─ Spring Security validates JWT:
   │  ├─ Check signature
   │  ├─ Check expiry (exp > current_time)
   │  ├─ Check blacklist (jti not in revoked_tokens)
   │  └─ Extract claims (user_id, roles, permissions)
   └─ Proceed to resource

5. Logout
   ├─ Add token JTI to blacklist in Redis (TTL = token expiry time)
   ├─ Remove refresh token from Redis
   └─ Clear session cookies on client
```

### 5.2 Password Security Policy

```
Password Requirements:
✓ Minimum 12 characters
✓ At least 1 uppercase letter (A-Z)
✓ At least 1 lowercase letter (a-z)
✓ At least 1 digit (0-9)
✓ At least 1 special character (!@#$%^&*)
✓ No more than 3 consecutive same characters
✓ Cannot contain username or email
✓ Cannot be one of last 5 passwords (history check)

Password Hashing:
Algorithm:  BCrypt with Spring Security
Cost Factor: 10-12 (100-200ms per hash)
Salt:       Generated per user (random 16 bytes)
Storage:    $2b$12$............................ (60-character hash)

Password Change Flow:
1. Require current password verification
2. Verify new password meets policy
3. Check password history
4. Hash with new salt
5. Audit log the change
6. Invalidate all existing tokens (force re-login on other devices)
```

### 5.3 Transaction Security

```
High-Value Transaction Process:

Transaction Amount > Threshold (e.g., $1000):

1. Initial Validation
   ├─ Verify sufficient balance
   ├─ Check account status (not FROZEN)
   ├─ Verify 24-hour transaction limit not exceeded
   └─ Check daily withdrawal limit

2. Fraud Detection Analysis
   ├─ Check transaction against user profile
   ├─ Compare with historical patterns
   ├─ Detect unusual locations (geolocation)
   ├─ Score: Risk Score = (anomaly_score * 100)
   └─ If risk_score > threshold:
      ├─ Flag transaction for manual review
      ├─ Require additional OTP or step-up authentication
      └─ Send alert email to user

3. Additional Verification (for high-risk)
   ├─ Generate transaction-specific OTP
   ├─ Send via SMS/Email
   ├─ Verify OTP in separate API call
   ├─ Check verification timestamp (max 10 minutes)
   └─ If expired: require re-verification

4. Transaction Processing
   ├─ Start database transaction (BEGIN)
   ├─ Lock both accounts (SELECT FOR UPDATE)
   ├─ Debit from source account
   ├─ Credit to destination account
   ├─ Create audit log entry
   ├─ Update fraud detection scores
   └─ COMMIT

5. Post-Transaction
   ├─ Send confirmation email/SMS
   ├─ Update user dashboard in real-time (WebSocket)
   ├─ Publish event for reconciliation service
   ├─ Publish event for fraud detection ML pipeline
   └─ Delete OTP from Redis

Atomic Transaction Guarantee:
- All-or-nothing: Either entire transaction succeeds or rolls back
- Consistency: Account balances always reconcile
- Isolation: Concurrent transactions don't interfere
- Durability: Committed transactions survive failures
```

### 5.4 Encryption Strategy

```
Data Classification:

PII (Personally Identifiable Information) - Encrypted at rest
├─ SSN / Tax ID
├─ Date of Birth
├─ Phone Number
├─ Address
└─ ID Document Numbers

Sensitive Financial Data - Encrypted at rest + in transit
├─ Account Numbers
├─ Card Numbers (encrypted, tokenized)
├─ Transaction Details
└─ Loan Details

Public Data - Not encrypted at rest, encrypted in transit
├─ Email Address
├─ Full Name
├─ Profile Picture
└─ Public Account Info

Encryption Methods:

At Rest (Database):
- Algorithm: AES-256-GCM
- Key Management: AWS KMS / HashiCorp Vault
- Per-record encryption (field-level)
- Separate encryption key per data type
- Key rotation: Quarterly

In Transit (Network):
- TLS 1.3 minimum
- Cipher suite: TLS_AES_256_GCM_SHA384
- Certificate pinning (mobile apps)
- HSTS enabled (Strict-Transport-Security)
- Perfect forward secrecy enabled
```

---

## 6. API Design

### 6.1 REST API Endpoints

```
BASE_URL: https://api.onlinebanking.com/v1

Authentication Endpoints:
─────────────────────────────────────────
POST   /api/v1/auth/register
       Request: { email, password, full_name, phone, dob }
       Response: { user_id, email, message, verification_pending }
       Status: 201 Created

POST   /api/v1/auth/login
       Request: { email, password }
       Response: { access_token, refresh_token, expires_in, user: { id, email, roles } }
       Status: 200 OK
       Errors: 401 Unauthorized, 429 Too Many Attempts

POST   /api/v1/auth/verify-otp
       Request: { otp_code, otp_type }
       Response: { access_token, refresh_token, expires_in }
       Status: 200 OK
       Errors: 400 Invalid OTP, 410 OTP Expired

POST   /api/v1/auth/refresh-token
       Request: { refresh_token }
       Response: { access_token, expires_in }
       Status: 200 OK
       Errors: 401 Invalid Refresh Token

POST   /api/v1/auth/logout
       Headers: Authorization: Bearer {token}
       Response: { message: "Logged out successfully" }
       Status: 204 No Content

POST   /api/v1/auth/forgot-password
       Request: { email }
       Response: { message: "Password reset link sent" }
       Status: 200 OK

POST   /api/v1/auth/reset-password
       Request: { reset_token, new_password }
       Response: { message: "Password reset successfully" }
       Status: 200 OK

User Management:
─────────────────────────────────────────
GET    /api/v1/users/me
       Headers: Authorization: Bearer {token}
       Response: { id, email, full_name, phone, kyc_status, 2fa_enabled, ... }
       Status: 200 OK

PUT    /api/v1/users/me
       Headers: Authorization: Bearer {token}
       Request: { full_name, phone, address, city, ... }
       Response: { message: "Profile updated", user: {...} }
       Status: 200 OK

PUT    /api/v1/users/me/password
       Headers: Authorization: Bearer {token}
       Request: { current_password, new_password }
       Response: { message: "Password changed successfully" }
       Status: 200 OK

POST   /api/v1/users/me/2fa/enable
       Headers: Authorization: Bearer {token}
       Request: { method: "SMS" | "EMAIL" | "AUTHENTICATOR" }
       Response: { qr_code_url, backup_codes }
       Status: 200 OK

POST   /api/v1/users/me/2fa/verify
       Headers: Authorization: Bearer {token}
       Request: { code }
       Response: { message: "2FA enabled successfully" }
       Status: 200 OK

Account Management:
─────────────────────────────────────────
GET    /api/v1/accounts
       Headers: Authorization: Bearer {token}
       Response: [{ account_id, number, type, balance, currency, status, ... }]
       Status: 200 OK

GET    /api/v1/accounts/{account_id}
       Headers: Authorization: Bearer {token}
       Response: { account_id, number, type, balance, interest_rate, ... }
       Status: 200 OK

POST   /api/v1/accounts
       Headers: Authorization: Bearer {token}
       Request: { account_type: "CHECKING" | "SAVINGS", currency: "USD" }
       Response: { account_id, number, type, balance, status, created_at }
       Status: 201 Created

Transactions:
─────────────────────────────────────────
GET    /api/v1/accounts/{account_id}/transactions
       Headers: Authorization: Bearer {token}
       Query: ?page=0&size=20&sort=created_at,desc
       Response: { content: [...], totalElements, totalPages, number, size }
       Status: 200 OK

GET    /api/v1/accounts/{account_id}/transactions?type=TRANSFER&from=2024-01-01&to=2024-12-31
       Headers: Authorization: Bearer {token}
       Response: [...filtered transactions...]
       Status: 200 OK

GET    /api/v1/transactions/{transaction_id}
       Headers: Authorization: Bearer {token}
       Response: { transaction_id, account_id, type, amount, status, created_at, ... }
       Status: 200 OK

POST   /api/v1/accounts/{account_id}/transfer
       Headers: Authorization: Bearer {token}
       Request: {
         beneficiary_id: 123,
         amount: 1000.00,
         description: "Payment for invoice #123",
         schedule: null | "2024-04-15" (for scheduled transfers)
       }
       Response: { transaction_id, reference_number, status, amount, ... }
       Status: 202 Accepted (or 200 OK if immediate)

POST   /api/v1/accounts/{account_id}/transfer-verify-otp
       Headers: Authorization: Bearer {token}
       Request: { transaction_id, otp_code }
       Response: { message: "Transfer completed", transaction: {...} }
       Status: 200 OK

GET    /api/v1/accounts/{account_id}/statement
       Headers: Authorization: Bearer {token}
       Query: ?from=2024-01-01&to=2024-12-31&format=PDF|CSV
       Response: Binary file (PDF/CSV download)
       Status: 200 OK
       Content-Type: application/pdf or text/csv

Beneficiaries:
─────────────────────────────────────────
GET    /api/v1/beneficiaries
       Headers: Authorization: Bearer {token}
       Response: [{ benef_id, account_number, holder_name, bank_name, status, ... }]
       Status: 200 OK

POST   /api/v1/beneficiaries
       Headers: Authorization: Bearer {token}
       Request: {
         account_number: "1234567890",
         holder_name: "John Doe",
         bank_name: "Example Bank",
         is_internal: false
       }
       Response: { benef_id, status: "PENDING_VERIFICATION", verification_sent: true }
       Status: 201 Created

POST   /api/v1/beneficiaries/{benef_id}/verify
       Headers: Authorization: Bearer {token}
       Request: { verification_code }
       Response: { message: "Beneficiary verified", status: "ACTIVE" }
       Status: 200 OK

DELETE /api/v1/beneficiaries/{benef_id}
       Headers: Authorization: Bearer {token}
       Response: { message: "Beneficiary deleted" }
       Status: 204 No Content

Cards:
─────────────────────────────────────────
GET    /api/v1/cards
       Headers: Authorization: Bearer {token}
       Response: [{ card_id, last_four, type, status, expires_at, ... }]
       Status: 200 OK

POST   /api/v1/cards
       Headers: Authorization: Bearer {token}
       Request: { account_id, card_type: "DEBIT" | "CREDIT" }
       Response: { card_id, card_number, status: "ACTIVE", ... }
       Status: 201 Created

PUT    /api/v1/cards/{card_id}/block
       Headers: Authorization: Bearer {token}
       Response: { message: "Card blocked", status: "BLOCKED" }
       Status: 200 OK

PUT    /api/v1/cards/{card_id}/unblock
       Headers: Authorization: Bearer {token}
       Response: { message: "Card unblocked", status: "ACTIVE" }
       Status: 200 OK

Loans:
─────────────────────────────────────────
GET    /api/v1/loans
       Headers: Authorization: Bearer {token}
       Response: [{ loan_id, type, amount, rate, term, status, emi, ... }]
       Status: 200 OK

POST   /api/v1/loan-calculator
       Headers: Authorization: Bearer {token}
       Request: { principal: 100000, rate: 8.5, term_months: 60 }
       Response: { principal, rate, term, emi: 1970.55, total_interest: 18527, total_amount }
       Status: 200 OK

POST   /api/v1/loans/apply
       Headers: Authorization: Bearer {token}
       Request: {
         loan_type: "PERSONAL" | "HOME" | "AUTO",
         principal: 100000,
         purpose: "Business expansion",
         employment_status: "EMPLOYED"
       }
       Response: { loan_id, status: "PENDING_APPROVAL", reference_number, ... }
       Status: 201 Created

Fraud Detection & Security:
─────────────────────────────────────────
GET    /api/v1/security/fraud-alerts
       Headers: Authorization: Bearer {token}
       Response: [{ alert_id, type, description, status, created_at, ... }]
       Status: 200 OK

POST   /api/v1/security/report-suspicious
       Headers: Authorization: Bearer {token}
       Request: { transaction_id, reason }
       Response: { message: "Report recorded", report_id }
       Status: 201 Created

GET    /api/v1/security/login-history
       Headers: Authorization: Bearer {token}
       Response: [{ timestamp, ip_address, device, location, status, ... }]
       Status: 200 OK

Admin Endpoints:
─────────────────────────────────────────
GET    /api/v1/admin/users
       Headers: Authorization: Bearer {admin_token}
       Query: ?page=0&size=50&status=ACTIVE|LOCKED|SUSPENDED
       Response: [{ user_id, email, name, status, kyc_status, created_at, ... }]
       Status: 200 OK
       Requires: ROLE_ADMIN

POST   /api/v1/admin/users/{user_id}/approve-kyc
       Headers: Authorization: Bearer {admin_token}
       Request: { approved: true, notes: "Document verified" }
       Response: { message: "KYC approved", user: {...} }
       Status: 200 OK
       Requires: ROLE_ADMIN

PUT    /api/v1/admin/users/{user_id}/status
       Headers: Authorization: Bearer {admin_token}
       Request: { status: "LOCKED" | "SUSPENDED" | "ACTIVE" }
       Response: { message: "User status updated", user: {...} }
       Status: 200 OK
       Requires: ROLE_ADMIN

GET    /api/v1/admin/fraud-alerts
       Headers: Authorization: Bearer {admin_token}
       Query: ?status=PENDING|REVIEWED&page=0&size=50
       Response: [{ alert_id, transaction, user, risk_score, status, ... }]
       Status: 200 OK
       Requires: ROLE_ADMIN

POST   /api/v1/admin/fraud-alerts/{alert_id}/resolve
       Headers: Authorization: Bearer {admin_token}
       Request: { status: "RESOLVED" | "FALSE_POSITIVE", notes: "..." }
       Response: { message: "Alert resolved", alert: {...} }
       Status: 200 OK
       Requires: ROLE_ADMIN

GET    /api/v1/admin/reports/transactions
       Headers: Authorization: Bearer {admin_token}
       Query: ?from=2024-01-01&to=2024-12-31&type=TRANSFER
       Response: Binary file (CSV/PDF report)
       Status: 200 OK
       Requires: ROLE_ADMIN

Analytics & AI:
─────────────────────────────────────────
GET    /api/v1/analytics/spending-summary
       Headers: Authorization: Bearer {token}
       Query: ?period=MONTH|QUARTER|YEAR
       Response: {
         total_spent: 15000,
         trends: [{ category, amount, percentage }],
         top_merchant: "..."
       }
       Status: 200 OK

GET    /api/v1/analytics/spending-by-category
       Headers: Authorization: Bearer {token}
       Response: [
         { category: "Food", amount: 500, percentage: 10, trend: "UP" },
         { category: "Transport", amount: 200, percentage: 4, trend: "DOWN" }
       ]
       Status: 200 OK

GET    /api/v1/chatbot/ask
       Headers: Authorization: Bearer {token}
       Query: ?question=How do I transfer money?
       Response: { answer: "To transfer money...", references: [...] }
       Status: 200 OK

POST   /api/v1/chatbot/query
       Headers: Authorization: Bearer {token}
       Request: { query: "What was my spending last month?" }
       Response: { response: "Based on your data...", type: "TEXT" | "INSIGHTS" }
       Status: 200 OK
```

### 6.2 Error Response Format

```json
{
  "timestamp": "2024-04-02T12:30:45.123Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email must be valid",
      "rejectedValue": "invalid-email"
    },
    {
      "field": "password",
      "message": "Password must be at least 12 characters",
      "rejectedValue": "short"
    }
  ],
  "path": "/api/v1/auth/register",
  "requestId": "req_xyz123abc"
}
```

---

## 7. Deployment Guide

### 7.1 Environment Setup

#### Development Environment
```yaml
# application-dev.yml
spring:
  application:
    name: online-banking-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/banking_dev
    username: banking_user
    password: dev_password
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  redis:
    host: localhost
    port: 6379
    database: 0
  
  security:
    jwt:
      secret-key: dev_secret_key_only_for_development
      expiration: 3600000 # 1 hour (ms)
      refresh-expiration: 604800000 # 7 days (ms)

logging:
  level:
    root: INFO
    com.onlinebanking: DEBUG
```

#### Production Environment
```yaml
# application-prod.yml
spring:
  application:
    name: online-banking-service
  
  datasource:
    url: ${DB_URL} # From environment variable / secrets manager
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 10000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate # Only validate, never auto-create
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        use_sql_comments: false
        dialect: org.hibernate.dialect.PostgreSQL10Dialect
  
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    password: ${REDIS_PASSWORD}
    ssl: true
  
  security:
    jwt:
      secret-key: ${JWT_SECRET_KEY} # From Vault
      expiration: 3600000
      refresh-expiration: 604800000

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: WARN
    com.onlinebanking: INFO
  pattern:
    console: "[%d{ISO8601}] [%-5p] [%t] %c{1.} - %msg%n"
    file: "[%d{ISO8601}] [%-5p] [%t] %c{1.} - %msg%n"
  file:
    name: /var/log/banking-app/application.log
    max-size: 100MB
    max-history: 30
    total-size-cap: 5GB
```

### 7.2 Docker Deployment

```dockerfile
# Backend Dockerfile (Multi-stage build)
FROM maven:3.9.0-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -P prod

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/banking-app-*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 banking && adduser -D -u 1000 -G banking banking
USER banking

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher healthcheck

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: banking-db
    environment:
      POSTGRES_DB: banking_prod
      POSTGRES_USER: banking_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U banking_user"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: banking-cache
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: banking-app
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://postgres:5432/banking_prod
      DB_USER: banking_user
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: ${REDIS_PASSWORD}
      JWT_SECRET_KEY: ${JWT_SECRET_KEY}
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  nginx:
    image: nginx:alpine
    container_name: banking-gateway
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - app

volumes:
  postgres-data:
  redis-data:
```

### 7.3 Kubernetes Deployment

```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: banking-app
  namespace: banking
spec:
  replicas: 3
  selector:
    matchLabels:
      app: banking-app
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: banking-app
    spec:
      serviceAccountName: banking-app
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
      - name: app
        image: onlinebanking.azurecr.io/banking-app:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 8080
          protocol: TCP
        - name: actuator
          containerPort: 9090
          protocol: TCP
        
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: url
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: password
        - name: REDIS_HOST
          value: "redis-cluster.banking"
        - name: JWT_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: jwt-secrets
              key: secret-key
        
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: actuator
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: actuator
          initialDelaySeconds: 20
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 2
        
        volumeMounts:
        - name: logs
          mountPath: /var/log/banking-app
        - name: tmp
          mountPath: /tmp
      
      volumes:
      - name: logs
        emptyDir: {}
      - name: tmp
        emptyDir: {}

---
apiVersion: v1
kind: Service
metadata:
  name: banking-app-service
  namespace: banking
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
    protocol: TCP
    name: http
  selector:
    app: banking-app

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: banking-app-hpa
  namespace: banking
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: banking-app
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

This completes the comprehensive system architecture documentation. Next sections will include backend code samples, frontend components, and implementation roadmap.

---

**Next in series:**
- Backend Code Samples (Java/Spring Boot)
- Frontend Components (React/TypeScript)
- Fraud Detection Implementation
- AI/Analytics Modules
- Implementation Roadmap & Timeline
