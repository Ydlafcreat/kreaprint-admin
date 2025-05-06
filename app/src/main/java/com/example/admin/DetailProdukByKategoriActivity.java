package com.example.admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.components.CustomBackToolbar;

public class DetailProdukByKategoriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Get Extra
        String kategori = getIntent().getStringExtra("kategori");

        setContentView(R.layout.activity_detail_produk_by_kategori);

        CustomBackToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setToolbarTitle(kategori);
        toolbar.showBackButton(true);

    }
}