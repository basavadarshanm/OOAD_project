# 🏦 Premium Banking System - Setup & Usage Guide

## Overview
A professional JavaFX desktop application for online banking with a modern, secure, and user-friendly interface. The system features comprehensive banking operations including account management, fund transfers, bill payments, loan management, card management, fraud detection, and analytics.

---

## ✨ Key Features

### 1. **Authentication & Account Management**
- Secure user registration and login
- Account creation with automatic account number generation
- Multi-account support per user
- Password validation and security

### 2. **Transfer & Payments**
- Peer-to-peer fund transfers
- Beneficiary management
- Bill payment system with multiple billers
- Transaction history tracking

### 3. **Card Management**
- View all cards (Debit, Credit, Prepaid)
- Real-time spending tracking
- Daily/monthly spending limits
- Card blocking/unblocking
- Limit modifications

### 4. **Loan Management**
- Loan application and approval workflow
- EMI calculator (Equated Monthly Installment)
- Multiple loan types (Personal, Home, Auto, Education)
- Payment recording and amortization tracking
- Loan status monitoring

### 5. **Bill Payment System**
- Add and manage utility bills
- Automatic overdue detection
- Late payment penalties
- Payment history (12 months)
- Predefined billers (Electricity, Water, Internet, Mobile, etc.)

### 6. **Analytics & Insights**
- Spending dashboard with category breakdown
- 6-month historical trend analysis
- Smart spending recommendations
- Spending comparison (current vs previous month)
- Transaction categorization

### 7. **Fraud Detection**
- Real-time transaction monitoring
- Suspicious activity detection
- Transaction pattern analysis
- Fraud alerts and flagging
- Admin fraud management dashboard

---

## 🚀 Quick Start

### Prerequisites
- **Java 21 or higher** (JDK 21+)
- **Maven 3.9+** (included in Maven wrapper)

### Installation & Running

#### **Option 1: Run with Maven**
```bash
cd "Online_Banking_System"
mvn clean javafx:run
```

#### **Option 2: Run the Built JAR**
```bash
cd "Online_Banking_System/target"
java -jar OnlineBankingApp.jar
```

#### **Demo Credentials**
```
Username: demo
Password: password123
```

---

## 📚 User Guide

### 1. **Login Page**
- Enter your username and password
- Click "LOGIN NOW" to access your accounts
- New users can click "Create Account" to register

### 2. **Dashboard Overview**
After successful login, you'll see:
- **Welcome message** with your name
- **Account Selector** dropdown to switch between your accounts
- **Account Details Card** showing:
  - Account Number
  - Account Type (Savings/Checking)
  - Available Balance
  - Account Status

### 3. **Recent Transactions**
- View your last transactions
- See transaction type, amount, and timestamp
- Filter and sort as needed

### 4. **Transfer Funds**
```
1. Click "Transfer Funds" section
2. Enter recipient's account number
3. Enter amount to transfer
4. Add optional description
5. Click "Send Transfer"
6. Confirm the transaction
```

### 5. **Pay Bills**
```
1. Click "Pay Bill" section
2. Select biller from dropdown (Electricity, Water, etc.)
3. Enter consumer number
4. Enter amount to pay
5. Click "Pay Bill"
```

### 6. **Add Beneficiary**
```
1. Click "Add Beneficiary" section
2. Enter beneficiary name
3. Enter beneficiary's account number
4. Enter their bank name
5. Click "Add Beneficiary"
```

### 7. **Cards Management**
Navigate to Cards section to:
- View all your cards
- Check daily spending limits
- View current spending progress
- Block/Unblock cards
- Modify spending limits

### 8. **Loan Management**
Navigate to Loans section to:
- Browse active loans
- View EMI details
- Apply for new loans
- Record EMI payments
- View loan amortization

### 9. **Bill Payments**
Navigate to Bills section to:
- Add bills
- View pending bills
- Track overdue bills
- Pay bills
- View payment history

### 10. **Analytics**
Navigate to Analytics section to:
- View spending dashboard
- See category breakdown
- Track monthly trends
- Get smart recommendations
- Compare spending periods

---

## 🎨 UI/UX Improvements Made

### Visual Enhancements
1. **Modern Gradient Headers** - Professional blue gradient background (#1e3c72 to #2a5298)
2. **Improved Card Design** - Better shadow effects and borders
3. **Enhanced Button Styling** - Gradient backgrounds with hover effects
4. **Better Typography** - Improved font sizes and weights for hierarchy
5. **Color Consistency** - Professional banking color scheme throughout
6. **Shadow Effects** - Subtle drop shadows for depth perception

### Input Fields
- Rounded corners (6px radius)
- Better focus states with blue highlights
- Improved padding and spacing
- Smooth transitions and effects

### Navigation
- Clear, clickable buttons with hover feedback
- Consistent styling across all pages
- Responsive layout

### Cards & Containers
- Cleaner borders with better contrast
- Improved spacing and padding
- Enhanced drop shadow effects
- Hover animations for interactive elements

---

## 🔧 Technical Details

### Architecture
```
com.onlinebanking/
├── App.java                    (Entry point)
├── config/
│   └── ApplicationContext.java (Dependency injection)
├── controller/                 (JavaFX Controllers)
├── service/                    (Business logic)
├── repository/                 (Data access - JDBC)
├── model/                      (Domain entities)
└── util/                       (Utilities)
```

### Database
- **H2 Embedded Database** (file-based)
- Location: `./data/online_banking`
- Auto-schema creation on first run
- Connection pooled via HikariCP

### Security Features
- Password validation
- Account-based access control
- Transaction validation
- Fraud detection engine
- Balance verification

---

## 📋 Demo Workflow

### Recommended Test Flow
1. **Login** with demo credentials
2. **View Account Details** to see balance and account info
3. **Check Recent Transactions** to understand transaction history
4. **Transfer Funds** to another account
5. **Add a Beneficiary** for quick transfers
6. **Pay a Bill** through the bill payment system
7. **View Your Cards** and check spending limits
8. **Apply for a Loan** and see EMI calculation
9. **Check Analytics** for spending insights
10. **Logout** and explore admin features

---

## ⚙️ Configuration

### Environment Variables (Optional)
Create a `.env` file to customize:
```properties
DB_URL=jdbc:h2:./data/online_banking;MODE=MySQL;AUTO_SERVER=TRUE
DB_USER=sa
DB_PASSWORD=

POOL_MAXIMUM_POOL_SIZE=10
POOL_CONNECTION_TIMEOUT_MS=30000
POOL_IDLE_TIMEOUT_MS=600000
POOL_MAX_LIFETIME_MS=1800000
```

### Logging
- Log4j2 configured in `log4j2.xml`
- Application logs to `logs/application.log`
- Debug mode available via `-Dlog4j.debug`

---

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Check Java version
java -version  # Should be 21 or higher

# Check Maven installation
mvn -version   # Should be 3.9+

# Clean build
mvn clean install
```

### Database Issues
```bash
# Delete existing database to reset
rm -rf ./data/

# Rebuild on next run
mvn javafx:run
```

### UI Doesn't Load Correctly
1. Clear CSS cache
2. Restart application
3. Check `styles.css` is in `src/main/resources/`

### Poor Performance
- Close other applications
- Increase JVM heap: `-Xmx2g`
- Check database connection pool settings

---

## 📱 Key Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Tab` | Navigate between fields |
| `Enter` | Submit form |
| `Esc` | Cancel dialog |
| `Ctrl+L` | Logout |
| `Ctrl+Q` | Quit application |

---

## 🔐 Security Best Practices

### For Production Use
1. **Enable Password Hashing** - Replace plain-text with bcrypt/bcrypt
2. **Add SSL/TLS** - Encrypt data in transit
3. **Implement MFA** - Two-factor authentication
4. **Database Encryption** - Encrypt sensitive data at rest
5. **Audit Logging** - Log all transactions and user actions
6. **Rate Limiting** - Limit login attempts
7. **Input Validation** - Sanitize all user inputs

### Current Development Mode
- Plain-text passwords (for demo purposes)
- Local H2 database
- No SSL encryption
- Development logging enabled

---

## 📊 Database Schema

### Key Tables
- **users** - User accounts
- **accounts** - Bank accounts
- **transactions** - Transaction history
- **beneficiaries** - Saved recipients
- **cards** - Card information
- **loans** - Loan details
- **bill_payments** - Bill records
- **fraud_detection** - Fraud flags

---

## 🤝 Contributing

### Adding New Features
1. Create a new model in `model/`
2. Add repository in `repository/`
3. Add service layer in `service/`
4. Create controller in `controller/`
5. Add FXML view in `resources/fxml/`
6. Update `ApplicationContext.java` for DI

---

## 📝 License
This project is for educational purposes.

---

## 📞 Support

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Cannot find main class" | Ensure Java 21 is installed and accessible |
| "Database locked" | Wait a few seconds and retry, ensure single instance |
| "Button doesn't respond" | Check console for errors, restart application |
| "CSS not applying" | Verify styles.css path, clear application cache |

---

## 🎯 Next Steps

1. **Explore Features** - Try all the functionality
2. **Review Code** - Understand the architecture
3. **Customize** - Modify colors, layouts, features
4. **Add Tests** - Create unit tests for components
5. **Deploy** - Package for production distribution

---

## 📅 Version History

### v0.1.0 (Current)
- ✅ Basic authentication
- ✅ Account management
- ✅ Transfer functionality
- ✅ Bill payment system
- ✅ Card management
- ✅ Loan management
- ✅ Analytics dashboard
- ✅ Fraud detection
- ✅ Modern UI/UX
- ✅ Professional styling

### Future Enhancements
- Mobile app version
- Multi-currency support
- Investment management
- Insurance products
- Real-time notifications
- Mobile wallet integration

---

## 🙏 Thank You!
Thank you for using the Premium Banking System. We hope this application provides a solid foundation for your banking application needs.

**Happy Banking! 🏦**
