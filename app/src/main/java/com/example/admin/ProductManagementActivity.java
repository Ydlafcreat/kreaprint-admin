package com.example.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ProductManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manajemen_produk);

        // Handle back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}