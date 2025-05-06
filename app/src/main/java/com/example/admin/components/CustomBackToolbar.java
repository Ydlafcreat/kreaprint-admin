package com.example.admin.components;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.example.admin.R; // Change this based on your package name

public class CustomBackToolbar extends Toolbar {
    private ImageView ivBack;
    private TextView tvTitle;

    public CustomBackToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_toolbar, this, true);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);

        ivBack.setOnClickListener(v -> {
            if (getContext() instanceof androidx.appcompat.app.AppCompatActivity) {
                ((androidx.appcompat.app.AppCompatActivity) getContext()).onBackPressed();
            }
        });
    }

    public void setToolbarTitle(String title) {
        tvTitle.setText(title);
    }

    public void showBackButton(boolean show) {
        ivBack.setVisibility(show ? VISIBLE : GONE);
    }
}