# Code Examples - Learn by Reading

This document shows actual code from the simplified banking system. Reference these when learning.

---

## 1. Simple Database Connection

**File**: `util/DataSourceFactory.java`

```java
public class DataSourceFactory {
    private static final String DB_URL = "jdbc:sqlite:./banking.db";
    private static Connection connection;

    // Get a database connection (SQLite automatically creates DB file)
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase(connection);
        }
        return connection;
    }

    // Create tables on first run
    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "email TEXT," +
                    "name TEXT" +
                    ")");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
```

**Key Points**:
- No complicated connection pooling
- One static connection for the whole app
- SQLite file created automatically
- Tables created if they don't exist

---

## 2. Simple Data Model (POJO)

**File**: `model/User.java`

```java
public class User {
    private long id;
    private String username;
    private String password;
    private String email;
    private String name;

    // Constructor for creating from database
    public User(long id, String username, String password, String email, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    // Simple getters
    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getName() { return name; }

    // Simple setters
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
}
```

**Key Points**:
- No complex behavior
- Just data fields
- Simple getters/setters
- Easy to understand

---

## 3. Direct Database Access (Repository)

**File**: `repository/UserRepository.java`

```java
public class UserRepository {
    
    // Find user by username - Direct SQL query
    public User findByUsername(String username) throws SQLException {
        String query = "SELECT id, username, password, email, name FROM users WHERE username = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("name")
                );
            }
        }
        return null;
    }

    // Find user by ID
    public User findById(long id) throws SQLException {
        String query = "SELECT id, username, password, email, name FROM users WHERE id = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("name")
                );
            }
        }
        return null;
    }

    // Create new user in database
    public void create(String username, String password, String email, String name) throws SQLException {
        String query = "INSERT INTO users (username, password, email, name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, name);
            stmt.executeUpdate();
        }
    }
}
```

**Key Points**:
- Direct JDBC (no ORM framework)
- PreparedStatement prevents SQL injection
- Try-with-resources auto-closes connections
- ResultSet directly maps to objects

---

## 4. Business Logic (Service)

**File**: `service/AuthService.java`

```java
public class AuthService {
    
    public User login(String username, String password) throws SQLException {
        String query = "SELECT id, username, password, email, name FROM users WHERE username = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            // Simple password check - NO HASHING (for learning only!)
            if (rs.next() && rs.getString("password").equals(password)) {
                return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("name")
                );
            }
        }
        return null;
    }

    public boolean register(String username, String password, String email, String name) throws SQLException {
        // Check if username exists
        String checkQuery = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, username);
            if (stmt.executeQuery().next()) {
                return false; // Username exists
            }
        }

        // Insert new user
        String insertQuery = "INSERT INTO users (username, password, email, name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataSourceFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, name);
            stmt.executeUpdate();
            return true;
        }
    }
}
```

**Key Points**:
- Clear login logic
- Simple registration
- Username uniqueness check
- Direct database access

---

## 5. UI Controller (JavaFX)

**File**: `controller/LoginController.java`

```java
public class LoginController {
    private AuthService authService = new AuthService();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    // Handle login button click
    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }
        
        try {
            // Attempt login
            User user = authService.login(username, password);
            if (user != null) {
                openDashboard(user);
            } else {
                errorLabel.setText("Invalid credentials");
            }
        } catch (Exception e) {
            errorLabel.setText("Login error: " + e.getMessage());
        }
    }

    // Handle register button click
    @FXML
    protected void handleRegister() throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setScene(scene);
    }

    // Navigate to dashboard
    private void openDashboard(User user) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        DashboardController controller = new DashboardController(user);
        loader.setController(controller);
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setScene(scene);
    }
}
```

**Key Points**:
- `@FXML` binds XML elements to Java
- Direct service instantiation
- Simple event handling
- Scene navigation

---

## 6. Account Operations (Business Logic)

**File**: `service/AccountService.java`

```java
public class AccountService {
    private AccountRepository accountRepo;
    private TransactionRepository transactionRepo;

    public AccountService() {
        this.accountRepo = new AccountRepository();
        this.transactionRepo = new TransactionRepository();
    }

    // Get all accounts for a user
    public List<Account> getUserAccounts(long userId) throws SQLException {
        return accountRepo.findByUserId(userId);
    }

    // Get transaction history for an account
    public List<Transaction> getTransactions(long accountId) throws SQLException {
        return transactionRepo.findByAccountId(accountId);
    }

    // Deposit money
    public void deposit(long accountId, double amount) throws SQLException {
        Account account = accountRepo.findById(accountId);
        if (account != null) {
            account.deposit(amount);
            // Update balance in database
            accountRepo.updateBalance(accountId, account.getBalance());
            // Record the transaction
            transactionRepo.create(accountId, "DEPOSIT", amount, "Deposit");
        }
    }

    // Withdraw money (checks balance)
    public boolean withdraw(long accountId, double amount) throws SQLException {
        Account account = accountRepo.findById(accountId);
        // withdraw() returns true if successful, false if insufficient funds
        if (account != null && account.withdraw(amount)) {
            accountRepo.updateBalance(accountId, account.getBalance());
            transactionRepo.create(accountId, "WITHDRAW", amount, "Withdrawal");
            return true;
        }
        return false;
    }

    // Transfer between accounts
    public boolean transfer(long fromAccountId, long toAccountId, double amount) throws SQLException {
        Account fromAccount = accountRepo.findById(fromAccountId);
        Account toAccount = accountRepo.findById(toAccountId);
        
        // Try to withdraw from source account
        if (fromAccount != null && toAccount != null && fromAccount.withdraw(amount)) {
            // Deposit to target account
            toAccount.deposit(amount);
            // Update both in database
            accountRepo.updateBalance(fromAccountId, fromAccount.getBalance());
            accountRepo.updateBalance(toAccountId, toAccount.getBalance());
            // Record transactions
            transactionRepo.create(fromAccountId, "TRANSFER", amount, "Transfer to " + toAccount.getAccountNumber());
            transactionRepo.create(toAccountId, "TRANSFER", amount, "Transfer from " + fromAccount.getAccountNumber());
            return true;
        }
        return false;
    }

    // Create a new account for a user
    public void createAccount(long userId, String accountNumber, double initialBalance) throws SQLException {
        accountRepo.create(userId, accountNumber, initialBalance);
    }
}
```

**Key Points**:
- Each method does one thing
- All database operations go through repositories
- Transactions recorded automatically
- Balance validation before operations

---

## 7. Dashboard Controller (UI Logic)

**File**: `controller/DashboardController.java`

```java
public class DashboardController {
    private AccountService accountService = new AccountService();
    private User currentUser;
    private Account selectedAccount;

    @FXML private Label welcomeLabel;
    @FXML private ComboBox<Account> accountComboBox;
    @FXML private Label balanceLabel;
    @FXML private ListView<Transaction> transactionsList;
    @FXML private TextField amountField;
    @FXML private Label messageLabel;

    // Constructor receives logged-in user
    public DashboardController(User user) {
        this.currentUser = user;
    }

    // Called automatically when FXML is loaded
    @FXML
    public void initialize() {
        try {
            welcomeLabel.setText("Welcome, " + currentUser.getName());
            loadAccounts();
        } catch (Exception e) {
            messageLabel.setText("Error loading accounts: " + e.getMessage());
        }
    }

    // Load user's accounts into dropdown
    private void loadAccounts() throws Exception {
        List<Account> accounts = accountService.getUserAccounts(currentUser.getId());
        accountComboBox.setItems(FXCollections.observableArrayList(accounts));
        
        if (!accounts.isEmpty()) {
            accountComboBox.getSelectionModel().select(0);
            onAccountSelected();
        }
    }

    // Called when user selects an account from dropdown
    @FXML
    private void onAccountSelected() {
        selectedAccount = accountComboBox.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
            loadTransactions();
        }
    }

    // Load and display transactions for selected account
    private void loadTransactions() {
        try {
            List<Transaction> transactions = accountService.getTransactions(selectedAccount.getId());
            transactionsList.setItems(FXCollections.observableArrayList(transactions));
        } catch (Exception e) {
            messageLabel.setText("Error loading transactions: " + e.getMessage());
        }
    }

    // Deposit button click
    @FXML
    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            accountService.deposit(selectedAccount.getId(), amount);
            selectedAccount.deposit(amount);
            balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
            amountField.clear();
            loadTransactions();
            messageLabel.setText("Deposit successful");
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    // Withdraw button click
    @FXML
    private void handleWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (accountService.withdraw(selectedAccount.getId(), amount)) {
                selectedAccount.withdraw(amount);
                balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
                amountField.clear();
                loadTransactions();
                messageLabel.setText("Withdrawal successful");
            } else {
                messageLabel.setText("Insufficient balance");
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}
```

**Key Points**:
- Initialize method loads data on startup
- Event handlers respond to button clicks
- UI updates reflect database changes
- Error messages shown to user

---

## 8. Simple Account Model

**File**: `model/Account.java`

```java
public class Account {
    private long id;
    private long userId;
    private String accountNumber;
    private double balance;

    public Account(long id, long userId, String accountNumber, double balance) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }

    // Deposit money
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    // Withdraw money - checks if enough balance
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= this.balance) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return accountNumber + " - Balance: $" + balance;
    }
}
```

**Key Points**:
- Simple arithmetic for deposit/withdraw
- Balance validation in withdraw
- toString for display in UI

---

## Execution Flow

```
App.main()
    ↓
App.start()  (loads login.fxml)
    ↓
LoginController.initialize()
    ↓
User clicks Login
    ↓
LoginController.handleLogin()
    ↓
AuthService.login(username, password)
    ↓
UserRepository.findByUsername()
    ↓
Database: SELECT * FROM users WHERE username = ?
    ↓
If match: DashboardController
    ↓
DashboardController.initialize()
    ↓
AccountService.getUserAccounts()
    ↓
Display accounts in dropdown
    ↓
User can deposit/withdraw/transfer
    ↓
AccountService performs operations
    ↓
Update database
    ↓
Refresh UI
```

---

## SQL Queries Used

All queries are in the repositories using PreparedStatement:

```sql
-- Users
SELECT * FROM users WHERE username = ?
SELECT * FROM users WHERE id = ?
INSERT INTO users (username, password, email, name) VALUES (?, ?, ?, ?)
UPDATE users SET email = ?, name = ? WHERE id = ?

-- Accounts
SELECT * FROM accounts WHERE user_id = ?
SELECT * FROM accounts WHERE id = ?
INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)
UPDATE accounts SET balance = ? WHERE id = ?

-- Transactions
SELECT * FROM transactions WHERE account_id = ? ORDER BY date DESC
INSERT INTO transactions (account_id, type, amount, description) VALUES (?, ?, ?, ?)
```

---

## Key Takeaways

1. **No Complex Frameworks**: Just pure Java + JDBC + JavaFX
2. **Direct Database Access**: No ORM, you write SQL
3. **Simple Models**: Just POJOs with getters/setters
4. **Clear Separation**: Controllers → Services → Repositories → Database
5. **Event-Driven UI**: JavaFX controllers respond to button clicks
6. **Error Handling**: Try-catch for database operations

**This is how real banking systems start!** Before adding ORM, dependency injection, or complex patterns, learn these fundamentals.
