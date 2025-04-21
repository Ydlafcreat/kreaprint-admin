package com.example.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Handle back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}