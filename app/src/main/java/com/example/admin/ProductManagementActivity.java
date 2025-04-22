package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ProductManagementActivity extends AppCompatActivity {

    // UI References
    private TextInputEditText etProductName, etSku, etPrice, etDescription;
    private AutoCompleteTextView actvCategory;

    // Firebase References
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Variables
    private String[] categories = {
            "Banner dan Spanduk",
            "Stiker dan Cutting",
            "Media Promosi dan Signage",
            "Perlengkapan Event",
            "Percetakan dan Offset",
            "Aksesoris dan Merchandise"
    };
    private String imageUrl = "";
    private static final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manajemen_produk);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images"); // ✅ Diperbaiki

        // Initialize UI components
        etProductName = findViewById(R.id.et_product_name);
        etSku = findViewById(R.id.et_sku);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        actvCategory = findViewById(R.id.autoCompleteCategory);

        // Setup category dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        actvCategory.setAdapter(adapter);

        // Handle back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Handle save button
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveProduct());

        // Handle upload image button
        findViewById(R.id.buttonUploadImage).setOnClickListener(v -> uploadImage());
    }

    private void saveProduct() {
        // Get input values
        String name = etProductName.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String sku = etSku.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || category.isEmpty() || sku.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field wajib", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format harga tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create product ID
        String productId = databaseReference.push().getKey();

        // Create product object
        Product product = new Product(name, category, sku, price, description);
        product.setId(productId);
        product.setImageUrl(imageUrl);

        // Save to Firebase
        databaseReference.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProductManagementActivity.this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProductManagementActivity.this, "Gagal menyimpan produk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // ✅ Diperbaiki: Gunakan storageReference yang sudah diinisialisasi
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Gambar berhasil diupload", Toast.LENGTH_SHORT).show();

                        // Get download URL
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Gagal upload gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    private void clearForm() {
        etProductName.setText("");
        actvCategory.setText("");
        etSku.setText("");
        etPrice.setText("");
        etDescription.setText("");
        imageUrl = "";
    }
}