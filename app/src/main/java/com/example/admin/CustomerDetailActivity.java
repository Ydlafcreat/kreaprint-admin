package com.example.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.components.CustomBackToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerDetailActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTotalCustomers, tvActiveCustomers, tvNewCustomers;
    private RecyclerView rvCustomers;

    // Adapter and Firebase
    private CustomerAdapter customerAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        CustomBackToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setToolbarTitle("Detail Pelanggan");
        toolbar.showBackButton(true);

//        initializeViews();
//        setupRecyclerView();
//        initializeFirestore();
//        loadCustomerData();
    }



    private void initializeViews() {
        tvTotalCustomers = findViewById(R.id.tv_total_customers);
        tvActiveCustomers = findViewById(R.id.tv_active_customers);
        tvNewCustomers = findViewById(R.id.tv_new_customers);
        rvCustomers = findViewById(R.id.rv_customers);
    }

    private void setupRecyclerView() {
        customerAdapter = new CustomerAdapter(new ArrayList<>());
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        rvCustomers.setAdapter(customerAdapter);
    }

    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadCustomerData() {
        registration = db.collection("users")
                .orderBy("registrationDate", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        Toast.makeText(this, "Gagal memuat data pelanggan", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        processCustomerData(value.getDocuments());
                    } else {
                        Toast.makeText(this, "Tidak ada data pelanggan", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processCustomerData(List<DocumentSnapshot> documents) {
        List<Customer> customers = new ArrayList<>();
        int activeCount = 0;
        Date oneWeekAgo = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
        int newCount = 0;

        for (DocumentSnapshot document : documents) {
            Customer customer = document.toObject(Customer.class);
            if (customer != null) {
                customer.setId(document.getId());
                customers.add(customer);

                if (customer.isActive()) {
                    activeCount++;
                }

                if (customer.getRegistrationDate() != null &&
                        customer.getRegistrationDate().after(oneWeekAgo)) {
                    newCount++;
                }
            }
        }

        updateUI(customers, activeCount, newCount);
    }

    private void updateUI(List<Customer> customers, int activeCount, int newCount) {
        tvTotalCustomers.setText(String.valueOf(customers.size()));
        tvActiveCustomers.setText(String.valueOf(activeCount));
        tvNewCustomers.setText(String.valueOf(newCount));
        customerAdapter.updateData(customers);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}