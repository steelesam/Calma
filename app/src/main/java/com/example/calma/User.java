package com.example.calma;

/**
 * Author: Samuel Steele
 */

public class User {

    /**
     * Variables
     */
    public String name, email, phone, accountType;

    /**
     * Default constructor
     */
    public User() {

    }

    /**
     * Constructor with args
     * @param name
     * @param email
     * @param phone
     * @param accountType
     */
    public User(String name, String email, String phone, String accountType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.accountType = accountType;
    }
}
