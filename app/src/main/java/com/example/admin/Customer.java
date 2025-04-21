package com.example.admin;

public class Customer {
    private String name;
    private String email;
    private String status;

    public Customer(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }

    // Getter methods
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
}