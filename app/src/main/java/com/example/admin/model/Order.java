package com.example.admin.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties // Tambahkan anotasi ini
public class Order {
    private String orderId;
    private String customerName;
    private String whatsappNumber;
    private String orderDetails;
    private String productCategory;
    private double totalPrice;
    private Timestamp orderDate;
    private String status;

    // Wajib: Konstruktor kosong untuk Firebase
    public Order() {}

    // Konstruktor untuk inisialisasi data
    public Order(String customerName, String whatsappNumber, String orderDetails,
                 String productCategory, double totalPrice) {
        this.customerName = customerName;
        this.whatsappNumber = whatsappNumber;
        this.orderDetails = orderDetails;
        this.productCategory = productCategory;
        this.totalPrice = totalPrice;
        this.orderDate = Timestamp.now(); // Auto-set timestamp
        this.status = "pending"; // Default status
    }

    // Getter & Setter
    @Exclude // Dokumen ID tidak perlu disimpan sebagai field terpisah
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    // Field lainnya
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }

    public String getOrderDetails() { return orderDetails; }
    public void setOrderDetails(String orderDetails) { this.orderDetails = orderDetails; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}