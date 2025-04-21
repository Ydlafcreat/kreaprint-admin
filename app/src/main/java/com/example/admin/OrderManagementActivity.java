package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_management);

        // Inisialisasi komponen UI
        ImageButton btnBack = findViewById(R.id.btn_back);
        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabAccount = findViewById(R.id.tab_account);

        // Setup click listeners
        btnBack.setOnClickListener(v -> handleBackNavigation());

        tabHome.setOnClickListener(v -> navigateToMainActivity());

        tabAccount.setOnClickListener(v -> navigateToAccountActivity());

        // Window insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.order_management_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });
    }

    private void handleBackNavigation() {
        // Cek jika ada dalam stack history
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish(); // Tutup activity jika tidak ada back stack
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finishAffinity(); // Membersihkan stack activity
    }

    private void navigateToAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        // Handle back button fisik
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}