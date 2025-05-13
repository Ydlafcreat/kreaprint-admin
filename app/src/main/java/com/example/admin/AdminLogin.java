package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLogin extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private ImageView eyeIcon, backButton;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inisialisasi komponen UI
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        eyeIcon = findViewById(R.id.eye_icon);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        Button loginButton = findViewById(R.id.signupButton);

        // Setup toggle password visibility
        eyeIcon.setOnClickListener(v -> togglePasswordVisibility());

        // Tombol Login
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (validateInput(username, password)) {
                loginAdmin(username, password);
            }
        });

        // Tombol Kembali
        backButton.setOnClickListener(v -> finish());
    }

    private void togglePasswordVisibility() {
        if (passwordInput.getTransformationMethod() instanceof PasswordTransformationMethod) {
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            eyeIcon.setImageResource(R.drawable.ic_eye);
        } else {
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eyeIcon.setImageResource(R.drawable.ic_eye);
        }
        passwordInput.setSelection(passwordInput.getText().length());
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            usernameInput.setError("Username wajib diisi");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password wajib diisi");
            return false;
        }
        return true;
    }

    private void loginAdmin(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Admins")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String storedPassword = document.getString("password_hash");

                            if (password.equals(storedPassword)) {
                                // Login berhasil
                                startActivity(new Intent(AdminLogin.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Admin tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}