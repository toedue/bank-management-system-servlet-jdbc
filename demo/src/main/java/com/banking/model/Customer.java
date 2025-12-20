package com.banking.model;

/**
 * Very simple Java class (model) that represents one bank customer.
 *
 * This class ONLY stores data. It does not talk to the database
 * or to web pages. Other classes use this class to pass customer
 * information around in the program.
 */
public class Customer {

    // Unique number given by the database for each customer row.
    private int id;

    // Human‑readable bank account number, for example "ACC12345".
    private String accountNumber;

    // The id of the user record that owns this customer/account.
    private int userId;

    // Basic contact information.
    private String name;
    private String email;
    private String phone;
    private String address;

    // How much money the customer has in this account.
    private double balance;

    // Empty constructor – used when we want to create an empty object first.
    public Customer() {
    }

    // Constructor that fills every field at once.
    public Customer(int id,
                    String accountNumber,
                    int userId,
                    String name,
                    String email,
                    String phone,
                    String address,
                    double balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.balance = balance;
    }

    // Below are simple "get" and "set" methods for each field.
    // They let other classes read and change the data safely.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

