package com.example.admin;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.adapter.ProductAdapter;
import com.example.admin.components.CustomBackToolbar;
import com.example.admin.helper.ToastHelper;
import com.example.admin.helper.firebase.FirestoreCallback;
import com.example.admin.helper.firebase.ProductRepository;
import com.example.admin.model.Product;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class DetailProdukByKategoriActivity extends AppCompatActivity {
    private ProductRepository productRepository;
    private RecyclerView productListRecyclerView;

    private ProductAdapter productAdapter;


    private ToastHelper toastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastHelper = new ToastHelper(this);
        productRepository = new ProductRepository();

//        Get Extra
        String kategori = getIntent().getStringExtra("kategori");

        loadProductByCategory(kategori);

        setContentView(R.layout.activity_detail_produk_by_kategori);

        CustomBackToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setToolbarTitle(kategori);
        toolbar.showBackButton(true);

        productListRecyclerView = findViewById(R.id.rv_products);

    }

    private void loadProductByCategory(String category) {
        productRepository.getProductsByCategory(category, new FirestoreCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                productAdapter = new ProductAdapter(DetailProdukByKategoriActivity.this, result);
                productListRecyclerView.setAdapter(productAdapter);

                productListRecyclerView.setLayoutManager(new LinearLayoutManager(DetailProdukByKategoriActivity.this));
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                toastHelper.showToast("Fail");
            }

        });
    }





}