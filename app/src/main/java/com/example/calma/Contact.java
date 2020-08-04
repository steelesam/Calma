package com.example.calma;

public class Contact {


    /**
     * Instance variables
     */
    public String name, phoneNumber, email;

    /**
     * Default constructor
     */
    public Contact() {

    }

    /**
     * Constructor with args
     * @param name
     * @param phoneNumber
     * @param email
     */
    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * To String
     * @return
     */
    @Override
    public String toString() {
        return "Contact name: " + name + ":\n" + "Phone: " + phoneNumber + ":\n" + "Email address: " + email;
    }
}
