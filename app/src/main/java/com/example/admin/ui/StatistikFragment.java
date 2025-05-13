package com.example.admin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.admin.R;
import com.example.admin.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StatistikFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvTotalOrders, tvAvgPrice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        // ✅ Pastikan ID TextView sudah sesuai XML
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvAvgPrice = view.findViewById(R.id.tv_avg_price);

        fetchOrderStats();
        return view;
    }

    private void fetchOrderStats() {
        db.collection("orders")
                .get()
                .addOnSuccessListener(this::processOrderData)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void processOrderData(QuerySnapshot querySnapshot) {
        int totalOrders = 0;
        double totalRevenue = 0;

        for (QueryDocumentSnapshot doc : querySnapshot) {
            Order order = doc.toObject(Order.class);
            totalOrders++;
            totalRevenue += order.getTotalPrice();
        }

        double avgPrice = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        // ✅ Update UI
        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvAvgPrice.setText("Rp " + String.format("%,d", (int) avgPrice));
    }
}