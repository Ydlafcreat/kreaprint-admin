package com.example.admin.helper.firebase;


import android.util.Log;

import com.example.admin.helper.ImagekitHelper;
import com.example.admin.model.Product;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository extends BaseRepository {
    private static final String COLLECTION = "products";

    public ProductRepository() {
        super("ProductRepository");
    }

    public void getProductById(String productId, FirestoreCallback<Product> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").document("Neon Box")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        callback.onSuccess(product);
                    } else {
                        callback.onError(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Gagal mengambil produk: " + e.getMessage());
                    callback.onError(null);
                });
    }

    public void getAllProduct(FirestoreCallback<List<Product>> callback) {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> produkList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        if (document.exists()) {
                            Product produk = document.toObject(Product.class);
                            produkList.add(produk);
                        }
                    }
                    callback.onSuccess(produkList);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Gagal mengambil semua produk", e));
    }

    public void getProductsByCategory(String categoryRef, FirestoreCallback<List<Product>> callback) {

        db.collection(COLLECTION)
                .whereEqualTo("categoryId", categoryRef)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Product product = doc.toObject(Product.class);

                        assert product != null;
                        product.setId(doc.getId());
                        products.add(product);
                    }
                    callback.onSuccess(products);
                });
    }

    public void getMostFavoritedProducts(FirestoreCallback<List<Product>> callback) {
        db.collection("products")
                .whereEqualTo("isActive", true)
                .orderBy("favoriteCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = querySnapshot.toObjects(Product.class);
                    callback.onSuccess(products);
                });
    }
    public void getBestSellers(FirestoreCallback<List<Product>> callback) {
        db.collection("products")
                .whereEqualTo("isActive", true)
                .orderBy("salesCount", Query.Direction.DESCENDING)
                .limit(10) // Ambil top 10
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        product.setId(doc.getId());
                        products.add(product);
                    }
                    callback.onSuccess(products);
                });
    }

    public void addProduct(Product product, FirestoreCallback<Boolean> callback) {
        DocumentReference docRef;

        // Jika ID sudah di-set (misal edit mode), gunakan ID itu
        if (product.getId() != null && !product.getId().isEmpty()) {
            docRef = db.collection(COLLECTION).document(product.getId());
        } else {
            docRef = db.collection(COLLECTION).document(); // auto generate ID
            product.setId(docRef.getId()); // set ID ke object
        }

        docRef.set(product)
                .addOnSuccessListener(unused -> {
                    logSuccess("Tambah Produk");
                    callback.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    logError("Tambah Produk", e);
                    callback.onError(e);
                });
    }

    public void uploadImageAndAddProduct(File imageFile,
                                         Product product,
                                         FirestoreCallback<Boolean> callback) {

        Map<String, String> params = new HashMap<>();
        params.put("folder", ImagekitHelper.DEFAULT_PRODUCT_FOLDER);

        ImagekitHelper.uploadFile(imageFile, params, new ImagekitHelper.UploadCallback() {
            @Override
            public void onSuccess(ImagekitHelper.UploadResponse response) {
                // Set URL hasil upload ke produk
                List<String> imageUrls = new ArrayList<>();
                imageUrls.add(response.url);
                product.setImageUrls(imageUrls);

                // Setelah upload, simpan ke Firestore
                addProduct(product, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(new Exception("Upload gagal: " + error));
            }
        });
    }
}