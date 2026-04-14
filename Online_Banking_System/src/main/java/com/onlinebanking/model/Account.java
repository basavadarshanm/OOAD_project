package com.onlinebanking.model;

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

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

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
