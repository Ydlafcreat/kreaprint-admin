package com.example.admin.helper.firebase;

import android.util.Log;

import com.example.admin.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String COLLECTION_NAME = "orders";
    private static final String TAG = "OrderRepository";

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public void addOrder(Order order, FirestoreCallback<String> callback) {
        db.collection(COLLECTION_NAME)
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    String docId = documentReference.getId();
                    Log.d(TAG, "Dokumen berhasil dibuat dengan ID: " + docId);
                    callback.onSuccess(docId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal menyimpan order: " + e.getMessage());
                    callback.onError(e);
                });
    }

    public void getAllOrders(FirestoreCallback<List<Order>> callback) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        order.setOrderId(doc.getId());
                        orders.add(order);
                    }
                    Log.d(TAG, "Berhasil mengambil " + orders.size() + " pesanan");
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal memuat pesanan: " + e.getMessage());
                    callback.onError(e);
                });
    }

    // Method untuk membatalkan order (hapus dari Firestore)
    public void deleteOrder(String orderId, FirestoreCallback<Void> callback) {
        db.collection(COLLECTION_NAME).document(orderId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Pesanan berhasil dihapus");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal menghapus pesanan: " + e.getMessage());
                    callback.onError(e);
                });
    }

    // Method untuk memperbarui status order
    public void updateOrderStatus(String orderId, String newStatus, FirestoreCallback<Void> callback) {
        db.collection(COLLECTION_NAME).document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Status pesanan diperbarui");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal memperbarui status: " + e.getMessage());
                    callback.onError(e);
                });
    }
}