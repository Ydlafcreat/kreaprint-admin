package com.example.admin.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber = ""; // Diubah dari 'phone' ke 'phoneNumber'
    private String address = "";
    private String role = "customer";
    private String imageUrl = "https://i.ibb.co.com/99DSRD4c/default-profile-kreaprint.png";
    private String imageUrlId;
    private Timestamp createdAt = Timestamp.now();
    private List<DocumentReference> favorites = new ArrayList<>();

    public User() {}

//    Image Url
    public String getImageUrl() { return this.imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

//    Image Url ID
    public String getImageUrlId() { return this.imageUrlId; }
    public void setImageUrlId(String imageUrlId) { this.imageUrlId = imageUrlId; }

//    User ID
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

//    Name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

//    Email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


//    Phone Number
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
    }

// Address
    public String getAddress() { return address; }
    public void setAddress(String alamat) { this.address = (alamat != null) ? alamat : ""; }

//    Role
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

//    Created At
    public Timestamp  getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp  createdAt) { this.createdAt = createdAt; }


//    Favorites
    public List<DocumentReference> getFavorites() { return favorites; }
    public void setFavorites(List<DocumentReference> favorites) {
        this.favorites = (favorites != null) ? favorites : new ArrayList<>();
    }

    public boolean isActive() {
        // Misalnya logika active = user sudah punya phone number & email
        return phoneNumber != null && !phoneNumber.isEmpty() && email != null && !email.isEmpty();
    }

    public Date getRegistrationDate() {
        return (createdAt != null) ? createdAt.toDate() : null;
    }

    public void addFavorite(DocumentReference productRef) {
        if (!this.favorites.contains(productRef)) {
            this.favorites.add(productRef);
        }
    }
}
