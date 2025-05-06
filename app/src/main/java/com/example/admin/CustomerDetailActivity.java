package com.example.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.components.CustomBackToolbar;
import com.example.admin.model.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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


        initializeViews();
        setupRecyclerView();
        initializeFirestore();
        loadCustomerData();
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
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error);
                        Toast.makeText(this, "Gagal memuat data pelanggan", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<DocumentSnapshot> documents = value.getDocuments();
                        processCustomerData(documents);
                    } else {
                        Toast.makeText(this, "Tidak ada data pelanggan", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processCustomerData(List<DocumentSnapshot> documents) {
        List<User> customers = new ArrayList<>();
        List<User> ac = new ArrayList<>();
        List<User> nc = new ArrayList<>();

        int totalCustomers = 0;
        int activeCount = 0;
        Date oneWeekAgo = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        int newCount = 0;

        for (DocumentSnapshot document : documents) {
            User user = document.toObject(User.class);
            if (user != null) {
                user.setId(document.getId());
                customers.add(user);

                if (user.isActive()) {
                    ac.add(user);
                    activeCount++;
                }

                Date regDate = user.getRegistrationDate();
                if (regDate != null && regDate.after(oneWeekAgo)) {
                    nc.add(user);
                    newCount++;
                }
            }
        }
        totalCustomers = customers.size();
        updateUI(nc, totalCustomers, activeCount, newCount);
    }




    private void updateUI(List<User> customers, int totalCustomers, int activeCount, int newCount) {
        tvTotalCustomers.setText(String.valueOf(totalCustomers));
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