package com.onlinebanking.model;

public class Transaction {
    private long id;
    private long accountId;
    private String type;
    private double amount;
    private String description;
    private String date;

    public Transaction(long id, long accountId, String type, double amount, String description, String date) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public long getId() { return id; }
    public long getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return date + " - " + type + " - $" + amount + " - " + description;
    }
}
}
