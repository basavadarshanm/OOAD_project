# Quick Start Guide

## Project Setup

Your online banking system has been simplified for easier learning. Here's what changed:

### Before (Complex)
- H2 database + HikariCP connection pooling
- Complex repository interfaces
- Multiple services for loans, fraud, analytics, etc.
- 2FA, OTP, role-based access
- BigDecimal and LocalDateTime complexity

### After (Simple)
- SQLite file-based database (automatically created)
- Direct JDBC repository classes
- Only core banking features
- Simple username/password login
- Basic data types (double, String)

---

## Building & Running

### Option 1: Command Line
```powershell
cd "d:\SEM 6\OOAD\project1\Online_Banking_System"
& 'C:\Users\Darshan\.maven\maven-3.9.14\bin\mvn' clean compile
& 'C:\Users\Darshan\.maven\maven-3.9.14\bin\mvn' javafx:run
```

### Option 2: IDE
Open the project in IntelliJ IDEA or Eclipse and run `App.java` as a JavaFX application.

---

## How It Works

### 1. First Time Running?
- The app will create `banking.db` in the project root
- Database tables are created automatically
- You'll see the login screen

### 2. Register a New Account
- Click "Register" button
- Fill in: username, name, email, password
- Click "Create Account"
- You'll be taken to login screen

### 3. Login
- Enter username and password
- Click "Login"
- You'll see your dashboard

### 4. Dashboard Features
- **Select Account**: Dropdown to choose which account to view
- **Balance**: Shows current balance
- **Recent Transactions**: Lists all deposits/withdrawals/transfers
- **Deposit**: Add money to account
- **Withdraw**: Remove money (checks if enough balance)
- **Transfer**: Send money to another account (if you have multiple)

---

## Database Location
```
d:\SEM 6\OOAD\project1\Online_Banking_System\banking.db
```

To reset the database, just delete this file and rebuild!

---

## Code Files to Understand

### Models (Data Classes)
- `User.java` - Username, password, email, name
- `Account.java` - Account number, balance, deposit/withdraw methods
- `Transaction.java` - Transaction record

### Repositories (Database Access)
- `UserRepository.java` - Save/load users
- `AccountRepository.java` - Save/load accounts
- `TransactionRepository.java` - Save/load transactions

### Services (Business Logic)
- `AuthService.java` - Login/register users
- `AccountService.java` - Deposit/withdraw/transfer logic

### Controllers (UI Logic)
- `LoginController.java` - Login screen
- `RegisterController.java` - Registration screen
- `DashboardController.java` - Main banking dashboard

---

## Example: Add a New Feature

### Task: Add "View Account Details" screen

**Step 1**: Create a new FXML file in `src/main/resources/fxml/account-details.fxml`

**Step 2**: Create controller `AccountDetailsController.java`
```java
public class AccountDetailsController {
    @FXML private Label accountLabel;
    @FXML private Label balanceLabel;
    
    public void setAccount(Account account) {
        accountLabel.setText("Account: " + account.getAccountNumber());
        balanceLabel.setText("Balance: $" + account.getBalance());
    }
}
```

**Step 3**: Call from DashboardController
```java
@FXML
private void handleViewDetails() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-details.fxml"));
    AccountDetailsController controller = new AccountDetailsController();
    loader.setController(controller);
    controller.setAccount(selectedAccount);
    // Load and show...
}
```

---

## Troubleshooting

### Compilation Errors
```powershell
# Clean and rebuild
& 'C:\Users\Darshan\.maven\maven-3.9.14\bin\mvn' clean compile -DskipTests
```

### Can't Find SQLite Driver
- The dependency is already in `pom.xml`
- Run: `& 'C:\Users\Darshan\.maven\maven-3.9.14\bin\mvn' dependency:resolve`

### Database File Permission Issues
- Delete `banking.db` file
- Restart the application
- It will recreate the database

### Login Not Working
- Check that `banking.db` exists
- Register a new user first
- Use the correct username/password

---

## What to Study

1. **JDBC Basics**: How `UserRepository`, `AccountRepository` work
2. **JavaFX Controllers**: How UI binds to controllers
3. **MVC Pattern**: Models → Services → Controllers → Views
4. **FXML**: XML-based UI definition
5. **Database Schema**: Simple 3-table setup (users, accounts, transactions)

All code is simple and directly written - no complex frameworks!
