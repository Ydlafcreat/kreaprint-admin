package com.example.admin;

public class Product {
    private String id;
    private String name;
    private String category;
    private String sku;
    private double price;
    private String description;
    private String imageUrl;
    private long timestamp;

    // Konstruktor kosong diperlukan untuk Firebase
    public Product() {
    }

    public Product(String name, String category, String sku, double price, String description) {
        this.name = name;
        this.category = category;
        this.sku = sku;
        this.price = price;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}