package com.example.admin;

import java.util.Date;

public class Customer {
    private String id;
    private String name;
    private String email;
    private Date registrationDate;
    private boolean isActive;

    // Constructor kosong diperlukan untuk Firestore
    public Customer() {}

    public Customer(String id, String name, String email, Date registrationDate, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
    }

    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}