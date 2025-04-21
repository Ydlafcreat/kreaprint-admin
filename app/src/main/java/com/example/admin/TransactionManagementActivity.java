package com.example.admin;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_manajemen_transaksi);

            // Handle window insets
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transaction_management_layout), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Back button functionality
            ImageButton backButton = findViewById(R.id.back_button);
            backButton.setOnClickListener(v -> {
                finish(); // Close this activity and return to previous one
            });

        } catch (Exception e) {
            e.printStackTrace();
            finish(); // Close activity if error occurs
        }
    }
}