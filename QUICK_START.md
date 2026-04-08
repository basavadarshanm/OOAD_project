# 🚀 Quick Start Guide - 5 Minutes to Banking

## ⚡ 30-Second Setup

### Windows
```bash
cd Online_Banking_System
mvn clean javafx:run
```

### Mac/Linux
```bash
cd Online_Banking_System
mvn clean javafx:run
```

**Done!** Application opens automatically. 🎉

---

## 👤 Login in 3 Steps

1. **Username**: `demo`
2. **Password**: `password123`
3. **Click**: LOGIN NOW

Or create your own account by clicking "Create Account"

---

## 🎯 Main Features (5 Minutes to Master)

### 1. View Your Account (30 seconds)
```
✓ Select account from dropdown
✓ See balance in green
✓ View account details
```

### 2. Send Money (1 minute)
```
1. Under "Transfer Funds"
2. Enter recipient account number
3. Enter amount
4. Click "Send Transfer"
```

### 3. Pay a Bill (1 minute)
```
1. Under "Pay Bill"
2. Pick a biller (Electric, Water, etc.)
3. Enter amount
4. Click "Pay Bill"
```

### 4. Add a Contact (30 seconds)
```
1. Under "Add Beneficiary"
2. Enter name and account number
3. Click "Add Beneficiary"
```

### 5. Check Cards (1 minute)
```
1. Click "Cards" in sidebar
2. View all your cards
3. Check spending limits
4. Block/Unblock if needed
```

---

## 💡 Pro Tips

- **Demo Account**: Start with pre-loaded data to explore features
- **Refresh**: Click the 🔄 button to update data
- **Logout**: Click 🚪 to switch accounts
- **Accounts**: Switch between checking/savings accounts
- **Transaction History**: Scroll down to see recent transactions

---

## 🎨 What's New in Styling

✨ **Modern Professional Look**
- Blue gradient headers
- Rounded buttons with hover effects
- Better organized cards
- Improved forms
- Cleaner typography
- Smooth animations

---

## ❓ Quick FAQ

**Q: Where's my data stored?**
A: Local H2 database in `./data/online_banking`

**Q: Can I create multiple accounts?**
A: Yes! Each user can have multiple bank accounts

**Q: Is my data really in local storage?**
A: Yes, for demo/testing. In production, use a real database.

**Q: Can I export transactions?**
A: Check the Analytics page for reports

**Q: What if I forgot the password?**
A: Use the demo account: `demo` / `password123`

---

## 📱 Popular Actions

| Action | Steps |
|--------|-------|
| Send Money | Transfer Card → Fill → Send |
| Pay Bill | Pay Bill Card → Select Biller → Pay |
| Check Balance | Select Account → See Green Total |
| View Cards | Cards Tab → View all cards |
| Apply Loan | Loans Tab → Apply → Get EMI |
| View Analytics | Analytics Tab → See Spending |

---

## 🔐 Security Notes

🟡 **Demo Mode** - For learning/testing only
- Passwords stored as plain text
- No SSL encryption
- Single-user system

⚠️ **Before Production**
- Add password hashing
- Enable SSL/TLS
- Use real database server
- Add two-factor authentication
- Implement audit logging

---

## 🛠️ Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `Tab` | Next field |
| `Enter` | Submit |
| `Esc` | Cancel |
| `Ctrl+L` | Logout |
| `Ctrl+Q` | Quit |

---

## 📊 Sample Test Flow

```
1. Login with demo/password123
2. Select "Savings Account"  
3. View balance ($10,000)
4. Transfer $100 to another account
5. Add John Doe as beneficiary
6. Pay $50 electricity bill
7. View transaction history
8. Check spending analytics
9. Apply for $5,000 loan
10. Logout
```

---

## 🎓 Features to Try

### Beginner
- [ ] Login
- [ ] View accounts
- [ ] Check balance
- [ ] See transactions

### Intermediate
- [ ] Transfer money
- [ ] Pay bills
- [ ] Add beneficiary
- [ ] Set spending limit

### Advanced
- [ ] Apply for loan
- [ ] Calculate EMI
- [ ] View analytics
- [ ] Check fraud alerts

---

## 📁 File Locations

```
Online_Banking_System/
├── target/OnlineBankingApp.jar        ← Run this!
├── data/online_banking                ← Your data
├── src/main/resources/styles.css      ← Styling
└── README.md                          ← More info
```

---

## 💻 System Requirements

| Requirement | Version | Status |
|------------|---------|--------|
| Java | 21+ | ✅ Required |
| Maven | 3.9+ | ✅ Required |
| RAM | 2GB+ | ✅ Recommended |
| Disk | 500MB+ | ✅ For DB |
| OS | Windows/Mac/Linux | ✅ All supported |

---

## 🚨 If Something Breaks

**Problem**: App won't start
```bash
# Clear data and restart
rm -rf data/
mvn clean javafx:run
```

**Problem**: Styles look wrong
```bash
# Restart application
# CSS should reload automatically
```

**Problem**: Database locked
```bash
# Wait 30 seconds, then restart
# Check no other instances running
```

---

## 🎯 What Can You Do?

✅ **Can Do**
- Create accounts
- Transfer money
- Pay bills
- View cards
- Apply for loans
- See analytics
- Check fraud alerts
- Manage beneficiaries

❌ **Can't Do** (Demo Limitations)
- Export to PDF
- Send notifications
- Use mobile apps
- Connect to real bank
- Process actual payments
- Multi-language support

---

## 🔗 Useful Links

- **Setup Guide**: See SETUP_AND_USAGE_GUIDE.md
- **Styling Guide**: See STYLING_GUIDE.md  
- **Code Improvements**: See IMPROVEMENTS_SUMMARY.md

---

## 🎉 You're Ready!

```
✓ Application working
✓ Features enabled
✓ Data available
✓ Styling beautiful
✓ Ready to use
```

**Enjoy exploring the Premium Banking System!** 🏦

---

## 📞 Need More Help?

1. **Can't start?** → Check SETUP_AND_USAGE_GUIDE.md
2. **Want to customize?** → See STYLING_GUIDE.md
3. **Want to code?** → Review code in src/ folder
4. **Need different data?** → Delete data/ folder to reset

---

**Happy Banking! 💰**

Questions? Check the other documentation files included.
