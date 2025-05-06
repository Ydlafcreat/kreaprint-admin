package com.example.admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.components.CustomBackToolbar;

public class DetailProduk extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);

        String productName = getIntent().getStringExtra("NAMA_PRODUK");

        CustomBackToolbar customBackToolbar = findViewById(R.id.toolbar);
        customBackToolbar.setToolbarTitle(productName);
        customBackToolbar.showBackButton(true);
    }
}