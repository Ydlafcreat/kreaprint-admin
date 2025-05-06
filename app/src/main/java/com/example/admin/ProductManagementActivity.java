package com.example.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProductManagementActivity extends AppCompatActivity {

    // UI References
    private TextInputEditText etProductName, etSku, etPrice, etDescription;
    private AutoCompleteTextView actvCategory;

    // Firebase References
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Daftar kategori
    private final List<String> categories = Arrays.asList(
            "Aksesoris dan Merchandise",
            "Banner dan Spanduk",
            "Media Promosi dan Signage",
            "Percetakan dan Offset",
            "Perlengkapan Event",
            "Stiker dan Cutting"
    );

    private String imageUrl = "";
    private static final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manajemen_produk);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        // Initialize UI components
        initializeViews();

        // Setup category dropdown
        setupCategoryDropdown();

        // Set up button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        etProductName = findViewById(R.id.et_product_name);
        etSku = findViewById(R.id.et_sku);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);
        actvCategory = findViewById(R.id.autoCompleteCategory);

        // Atur agar tidak bisa edit manual
        actvCategory.setKeyListener(null);
    }

    private void setupCategoryDropdown() {
        // Buat adapter dengan custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_item,
                categories
        );

        actvCategory.setAdapter(adapter);

        // Atur behavior dropdown
        actvCategory.setOnClickListener(v -> actvCategory.showDropDown());

        // Atur ketika focus
        actvCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvCategory.showDropDown();
            }
        });

        // Listener ketika item dipilih
        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = parent.getItemAtPosition(position).toString();
            // Kode tambahan jika diperlukan
        });
    }

    private void setupButtonListeners() {
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

        // Validasi input
        if (name.isEmpty() || category.isEmpty() || sku.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field wajib", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi kategori
        if (!categories.contains(category)) {
            Toast.makeText(this, "Pilih kategori yang valid dari daftar", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan produk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mengunggah...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show();

                        // Get download URL
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Gagal mengunggah gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Terkirim " + (int) progress + "%");
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