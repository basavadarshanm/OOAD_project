# Setup and Testing Guide - Online Banking System

## Prerequisites

- Java JDK 17 or higher
- MySQL 8.0 or higher
- Maven 3.6.0 or higher
- Git
- Postman (for API testing) or curl

## Project Setup

### Step 1: Database Setup

1. **Start MySQL Server**
```bash
# On Windows
net start MySQL80

# On Mac
brew services start mysql

# On Linux
sudo systemctl start mysql
```

2. **Create Database**
```bash
mysql -u root -p

# In MySQL console
CREATE DATABASE banking_system;
USE banking_system;
```

3. **Create Database User (Optional but Recommended)**
```sql
CREATE USER 'banking_user'@'localhost' IDENTIFIED BY 'banking_password';
GRANT ALL PRIVILEGES ON banking_system.* TO 'banking_user'@'localhost';
FLUSH PRIVILEGES;
```

### Step 2: Project Configuration

1. **Clone/Download Project**
```bash
cd d:/SEM\ 6/OOAD/project
```

2. **Update application.properties**
Edit `src/main/resources/application.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=create-drop  # First run: create, Next: update
```

### Step 3: Build and Run

1. **Build Project**
```bash
mvn clean install
```

2. **Run Application**
```bash
mvn spring-boot:run
```

Or directly run:
```bash
java -jar target/online-banking-system-1.0.0.jar
```

3. **Verify Application Started**
```
Application should be running at: http://localhost:8080
Check console logs for "Started OnlineBankingApplication"
```

## Testing Use Cases

### Test Case 1: Create Account (Signup)

**Use Case**: Create Account (MAJOR)

**Endpoint**: `POST /api/auth/signup`

**Request**:
```json
{
  "username": "john_doe",
  "password": "password123",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phoneNumber": "9876543210"
}
```

**Expected Response**:
```json
{
  "message": "Signup successful",
  "customerId": 1
}
```

**Curl Command**:
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

### Test Case 2: Login

**Use Case**: Login (MINOR)

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Expected Response**:
```json
{
  "message": "Login successful",
  "userId": 1,
  "username": "john_doe",
  "userType": "Customer"
}
```

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Test Case 3: Check Balance (Validate User)

**Use Case**: Check Balance (MINOR), Validate User (MINOR)

**Endpoint**: `GET /api/accounts/{accountId}/balance`

**Request Parameters**:
```
accountId: 1
```

**Expected Response**:
```json
{
  "balance": 0.0
}
```

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/accounts/1/balance
```

### Test Case 4: Deposit Money

**Endpoint**: `POST /api/accounts/{accountId}/deposit`

**Request Parameters**:
```
accountId: 1
amount: 10000
```

**Expected Response**:
```json
{
  "message": "Deposit successful"
}
```

**Curl Command**:
```bash
curl -X POST http://localhost:8080/api/accounts/1/deposit?amount=10000
```

### Test Case 5: Transfer Money

**Use Case**: Transfer Money (MAJOR)

**Endpoint**: `POST /api/accounts/transfer`

**Setup**: First create multiple customers and accounts

1. Create Customer 2:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane_doe",
    "password": "password456",
    "email": "jane@example.com",
    "fullName": "Jane Doe",
    "phoneNumber": "9876543211"
  }'
```

2. Get Account Numbers: Make GET request to retrieve accounts

3. Perform Transfer:
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

**Expected Response**:
```json
{
  "message": "Transfer successful",
  "transactionId": 1,
  "transactionDate": "2024-01-15T10:30:00"
}
```

### Test Case 6: Pay Bills

**Use Case**: Pay Bills (MAJOR)

**Step 1**: Create Bill
```bash
curl -X POST "http://localhost:8080/api/bills/create?customerId=1&billType=ELECTRICITY&billAmount=500&biller=PowerCorp&dueDate=2024-02-15T00:00:00"
```

**Step 2**: Pay Bill
```bash
curl -X POST http://localhost:8080/api/bills/1/pay?accountId=1
```

**Expected Response**:
```json
{
  "message": "Bill paid successfully",
  "billId": 1,
  "paidAmount": 500.0,
  "paidDate": "2024-01-15T10:35:00"
}
```

### Test Case 7: Get Transaction History

**Use Case**: Transaction History (Minor)

**Endpoint**: `GET /api/transactions/history/{accountId}`

**Request Parameters**:
```
accountId: 1
```

**Expected Response**:
```json
{
  "transactions": [
    {
      "id": 1,
      "transactionId": "TXN1234567890",
      "transactionType": "DEPOSIT",
      "amount": 10000.0,
      "description": "Deposit",
      "status": "SUCCESS",
      "transactionDate": "2024-01-15T10:30:00"
    }
  ],
  "totalRecords": 1
}
```

**Curl Command**:
```bash
curl -X GET http://localhost:8080/api/transactions/history/1
```

### Test Case 8: Generate Receipt

**Use Case**: Generate Receipt (MINOR)

**Endpoint**: `POST /api/receipts/generate`

**Request Parameters**:
```
transactionId: 1
receiptType: TRANSACTION
details: Transfer receipt for transaction TXN1234567890
```

**Expected Response**:
```json
{
  "message": "Receipt generated successfully",
  "receiptId": "RCP1234567890",
  "amount": 10000.0,
  "generatedAt": "2024-01-15T10:40:00"
}
```

**Curl Command**:
```bash
curl -X POST "http://localhost:8080/api/receipts/generate?transactionId=1&receiptType=TRANSACTION&details=Transfer receipt"
```

### Test Case 9: Manage Users

**Use Case**: Manage Users (MAJOR)

**Update Customer Profile**:
```bash
curl -X PUT http://localhost:8080/api/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Updated",
    "email": "john_updated@example.com",
    "phoneNumber": "9876543212",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "pincode": "10001"
  }'
```

### Test Case 10: Error Scenarios

**Test Insufficient Balance**:
```bash
# First deposit 500
curl -X POST http://localhost:8080/api/accounts/1/deposit?amount=500

# Try to transfer 1000 (should fail)
curl -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountNumber": "ACC1234567890",
    "amount": 1000.0,
    "description": "Payment"
  }'

# Expected Error Response
{
  "error": "Insufficient balance"
}
```

## Database Verification

After running tests, verify data in database:

```sql
-- View all customers
SELECT * FROM users WHERE user_type = 'CUSTOMER';

-- View all accounts
SELECT * FROM accounts;

-- View all transactions
SELECT * FROM transactions;

-- View all bills
SELECT * FROM bills;

-- View all receipts
SELECT * FROM receipts;

-- Check account balance
SELECT account_number, balance FROM accounts WHERE id = 1;

-- View transaction history
SELECT * FROM transactions WHERE account_id = 1 ORDER BY transaction_date DESC;
```

## Testing Checklist

- [ ] Database created and configured
- [ ] Application starts successfully
- [ ] Create Account (Signup) works
- [ ] Login validates credentials
- [ ] Account balance check works
- [ ] Deposit money functionality works
- [ ] Withdraw money functionality works
- [ ] Transfer between accounts works
- [ ] Transfer with insufficient balance rejected
- [ ] Bill creation works
- [ ] Bill payment works
- [ ] Transaction history retrieves all transactions
- [ ] Receipt generation works
- [ ] User profile update works
- [ ] Error handling works properly
- [ ] All database records created correctly

## Logging and Debugging

### Enable Debug Logging

Update `application.properties`:
```properties
logging.level.com.banking=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
spring.jpa.properties.hibernate.format_sql=true
```

### Check Logs

```bash
# Linux/Mac
tail -f nohup.out

# Windows PowerShell
Get-Content -Path logfile.txt -Tail -Wait
```

## Common Issues and Solutions

### Issue 1: Database Connection Failed
**Solution**:
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1"

# Check database exists
mysql -u root -p -e "USE banking_system; SHOW TABLES;"

# Update application.properties with correct credentials
```

### Issue 2: Port Already in Use
**Solution**:
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <PID> /F

# Or change port in application.properties
server.port=8081
```

### Issue 3: JPA UUID Not Generated
**Solution**: Ensure `@PrePersist` methods are called:
```java
// Check entities have @PrePersist methods
// Or set IDs before saving
transaction.setTransactionId("TXN" + System.currentTimeMillis());
```

## Performance Testing (Optional)

Test with multiple concurrent requests:

```bash
# Install Apache Bench (Linux/Mac)
ab -n 100 -c 10 http://localhost:8080/api/accounts/1/balance

# Or use wrk
wrk -t4 -c100 -d30s http://localhost:8080/api/accounts/1/balance
```

## Security Notes

1. **Change Default Credentials** in `application.properties`
2. **Enable HTTPS** in production
3. **Use environment variables** for sensitive data
4. **Implement rate limiting**
5. **Add request validation**
6. **Use JWT tokens** for API authentication

## Production Deployment

1. Build WAR file:
```bash
mvn clean package -DskipTests
```

2. Deploy to server (Tomcat, Heroku, AWS, etc.)

3. Update database configuration for production

4. Enable SSL/HTTPS

5. Set up monitoring and logging

## Contact & Support

For issues or questions:
- Check application logs
- Verify database connectivity
- Review OOAD_DOCUMENTATION.md for architecture details
- Check UML_DIAGRAMS.md for system design

