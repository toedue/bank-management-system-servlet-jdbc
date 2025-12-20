package com.banking.model;

import java.util.Date;

/**
 * Simple class that represents one money movement (transaction).
 *
 * A transaction can be a deposit, withdrawal or transfer.
 * It remembers who sent the money, who received it, how much,
 * and when it happened.
 */
public class Transaction {

    // Unique id from the database.
    private int id;

    // Type of transaction: "deposit", "withdrawal", "transfer", etc.
    private String transactionType;

    // Account that sends the money (can be null for deposits).
    private String senderAccountNumber;

    // Account that receives the money (can be null for withdrawals).
    private String receiverAccountNumber;

    // How much money moved.
    private double amount;

    // Optional small text note from the user/admin.
    private String note;

    // When the transaction was created.
    private Date createdAt;

    // Empty constructor.
    public Transaction() {
    }

    // Constructor that fills every field at once.
    public Transaction(int id,
                       String transactionType,
                       String senderAccountNumber,
                       String receiverAccountNumber,
                       double amount,
                       String note,
                       Date createdAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.note = note;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

