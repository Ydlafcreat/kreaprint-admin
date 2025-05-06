package com.example.admin;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private String id;
    private String name;
    private String description;
    private int price = 0;
    private List<String> imageUrls = new ArrayList<>();
    private String categoryId;
    private boolean isActive = true;
    private Timestamp createdAt = Timestamp.now();

    private int favoriteCount = 0;

    private String tips = "";

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    private int salesCount = 0; // Tambahkan ini

    // Getter & Setter
    public int getSalesCount() { return salesCount; }
    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }
    public Product() {}

    // Constructor update
    public Product(String id, String name, String description, int price,
                   List<String> imageUrls, String categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.categoryId = categoryId;
    }

    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = (imageUrls != null) ? imageUrls : new ArrayList<>();
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}