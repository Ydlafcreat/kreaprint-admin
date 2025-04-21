package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Update method untuk konsistensi navigasi
    public void navigateToOrderManagement(View view) {
        Intent intent = new Intent(this, OrderManagementActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Opsional: tutup activity saat ini
    }

    // Handle tombol back untuk konsistensi
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Tambahkan logika tambahan jika diperlukan
    }
    public void navigateToTransactionManagement(View view) {
        Intent intent = new Intent(this, TransactionManagementActivity.class);
        startActivity(intent);
    }
    // Di MainActivity.java
    public void navigateToCustomerDetail(View view) {
        try {
            Intent intent = new Intent(this, CustomerDetailActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void navigateToProductManagement(View view) {
        try {
            Intent intent = new Intent(this, ProductManagementActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void navigateToAccount(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }
}