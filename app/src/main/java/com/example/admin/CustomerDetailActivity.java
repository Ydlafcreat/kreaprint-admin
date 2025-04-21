package com.example.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        // Handle back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}