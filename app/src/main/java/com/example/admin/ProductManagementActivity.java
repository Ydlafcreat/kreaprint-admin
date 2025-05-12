package com.example.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.components.CustomBackToolbar;
import com.example.admin.helper.ToastHelper;
import com.example.admin.helper.firebase.FirestoreCallback;
import com.example.admin.helper.firebase.ImgurUploader;
import com.example.admin.helper.firebase.ProductRepository;
import com.example.admin.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductManagementActivity extends AppCompatActivity {

    // UI References
    private TextInputEditText etProductName, etPrice, etDescription;
    private AutoCompleteTextView actvCategory;

    private ToastHelper productManagementToast;

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
        setContentView(R.layout.activity_manajemen_produk);

        // Initialize UI components
        CustomBackToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setToolbarTitle("Manajemen Produk");
        toolbar.showBackButton(true);

        Button btnTambahProduk = findViewById(R.id.btn_tambah_produk);
        btnTambahProduk.setOnClickListener(v -> showTambahProdukModal());
        productManagementToast = new ToastHelper(this);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        initCategoryEvent();
    }

    private void initCategoryEvent() {
        Map<String, Integer> categoryMap = new HashMap<>();
        categoryMap.put("Banner & Spanduk", R.id.cardViewBannerDanSpanduk);
        categoryMap.put("Sticker & Cutting", R.id.cardViewStickerDanCutting);
        categoryMap.put("Media Promosi & Signage", R.id.cardViewMediaPromosiDanSignage);
        categoryMap.put("Perlengkapan Event", R.id.cardViewPerlengkapanEvent);
        categoryMap.put("Percetakan & Offset", R.id.cardViewPercetakanDanOffset);
        categoryMap.put("Aksesoris dan Merchandise", R.id.cardViewAksesorisDanMerchandise);

        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            String categoryName = entry.getKey();
            int cardViewId = entry.getValue();

            CardView cardView = findViewById(cardViewId);
            if (cardView != null) {
                cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductManagementActivity.this, DetailProdukByKategoriActivity.class);
                    intent.putExtra("kategori", categoryName);
                    startActivity(intent);
                });
            }
        }
    }

    private void showTambahProdukModal() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.modal_tambah_produk, null);
        dialog.setContentView(view);

        // Initialize all input fields
        TextInputEditText etProductName = view.findViewById(R.id.et_product_name);
        AutoCompleteTextView autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        TextInputEditText etPrice = view.findViewById(R.id.et_price);
        TextInputEditText etDescription = view.findViewById(R.id.et_description);
        Button btnUploadImage = view.findViewById(R.id.btn_upload_image);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // Setup category dropdown
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_item,
                categories
        );
        autoCompleteCategory.setAdapter(categoryAdapter);
        autoCompleteCategory.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCategory = parent.getItemAtPosition(position).toString();
        });

        // Handle image upload
        btnUploadImage.setOnClickListener(v -> {
            uploadImage();
        });

        // Handle confirm button
        btnConfirm.setOnClickListener(v -> {
            // Get input values
            String name = etProductName.getText().toString().trim();
            String category = autoCompleteCategory.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
                productManagementToast.showToast("Harap isi semua field wajib!");
                return;
            }

            if (!categories.contains(category)) {
                productManagementToast.showToast("Pilih kategori yang valid!");
                return;
            }

            // Create new product object
            Product newProduct = new Product();
            double price = 0.0;

            try {
                price = Double.parseDouble(priceStr);
                newProduct.setPrice((int) Math.round(price));
            } catch (NumberFormatException e) {
                productManagementToast.showToast("Format harga tidak valid!");
                return;
            }

            // Set product properties
            newProduct.setId(UUID.randomUUID().toString());
            newProduct.setName(name);
            newProduct.setCategoryId(category);
            newProduct.setDescription(description);
            newProduct.setActive(true);
            newProduct.setCreatedAt(Timestamp.now());

            // If image was uploaded
            if (!imageUrl.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                imageUrls.add(imageUrl);
                newProduct.setImageUrls(imageUrls);
            }

            // Show loading indicator
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Menyimpan produk...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Save to Firebase
            ProductRepository productRepository = new ProductRepository();
            productRepository.addProduct(newProduct, new FirestoreCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        productManagementToast.showToast("Produk berhasil ditambahkan!");
                        dialog.dismiss();
                        imageUrl = ""; // Reset image URL
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        productManagementToast.showToast("Gagal menambahkan produk: " + e.getMessage());
                        Log.e("ProductManagement", "Error adding product", e);
                    });
                }
            });
        });

        // Handle cancel button
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            imageUrl = ""; // Reset image URL
        });

        dialog.show();
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadImageToImgur(filePath);
        }
    }

    private void uploadImageToImgur(Uri imageUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mengunggah ke Imgur...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                // 1. Kompresi gambar sebelum upload
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // Kompresi dengan kualitas 80% dan format JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageData = baos.toByteArray();

                // 2. Validasi ukuran file (max 8MB)
                if (imageData.length > 8 * 1024 * 1024) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        productManagementToast.showToast("Ukuran gambar terlalu besar (max 8MB)");
                    });
                    return;
                }

                // 3. Upload dengan timeout yang lebih panjang
                ImgurUploader.uploadImage(imageData, new ImgurUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            ProductManagementActivity.this.imageUrl = imageUrl;
                            productManagementToast.showToast("Upload berhasil!");
                            Log.d("ImgurUpload", "Image URL: " + imageUrl);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            handleImgurError(error);
                        });
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    productManagementToast.showToast("Error: " + e.getMessage());
                    Log.e("ImgurUpload", "Error: ", e);
                });
            }
        }).start();
    }

    private void handleImgurError(String error) {
        String userMessage;

        if (error.contains("Client-ID")) {
            userMessage = "Konfigurasi Imgur tidak valid. Hubungi developer";
        } else if (error.contains("File type invalid")) {
            userMessage = "Format gambar tidak didukung (Gunakan JPG/PNG)";
        } else if (error.contains("too large")) {
            userMessage = "Ukuran gambar terlalu besar (max 8MB)";
        } else {
            userMessage = "Gagal upload. Coba gambar lain atau coba lagi nanti";
        }

        productManagementToast.showToast(userMessage);
        Log.e("ImgurUpload", "Error details: " + error);
    }

    private void testAddProduk() {
        List<String> dummyImages = new ArrayList<>();
        dummyImages.add("https://example.com/images/signage1.jpg");

        Product newProduct = new Product();
        newProduct.setId("product-test-" + UUID.randomUUID().toString());
        newProduct.setName("Produk Test");
        newProduct.setDescription("Ini adalah produk test");
        newProduct.setPrice(100000);
        newProduct.setImageUrls(dummyImages);
        newProduct.setCategoryId("Banner & Spanduk");
        newProduct.setActive(true);
        newProduct.setCreatedAt(Timestamp.now());

        ProductRepository productRepository = new ProductRepository();
        productRepository.addProduct(newProduct, new FirestoreCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                productManagementToast.showToast("Produk test berhasil ditambahkan");
            }

            @Override
            public void onError(Exception e) {
                productManagementToast.showToast("Gagal menambahkan produk test");
            }
        });
    }
}