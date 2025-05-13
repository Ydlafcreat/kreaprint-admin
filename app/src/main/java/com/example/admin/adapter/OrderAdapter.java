package com.example.admin.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.helper.firebase.OrderRepository;
import com.example.admin.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }



    @androidx.annotation.NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Bind data ke view
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvWhatsapp.setText(order.getWhatsappNumber());
        holder.tvOrderDetails.setText(order.getOrderDetails());
        holder.tvTotalPrice.setText("Rp " + String.format("%,d", (int) order.getTotalPrice()));

        holder.btnBatal.setOnClickListener(v -> {

        });

        // Tombol Selesai (Update Status)
        holder.btnSelesai.setOnClickListener(v -> {

        });
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder class
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvWhatsapp, tvOrderDetails, tvTotalPrice;
        Button btnBatal, btnSelesai;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvWhatsapp = itemView.findViewById(R.id.tv_whatsapp);
            tvOrderDetails = itemView.findViewById(R.id.tv_order_details);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            btnBatal = itemView.findViewById(R.id.btn_batal);
            btnSelesai = itemView.findViewById(R.id.btn_selesai);

        }
    }

    // Method untuk update data
    public void updateList(List<Order> newOrderList) {
        orderList.clear();
        orderList.addAll(newOrderList);
        notifyDataSetChanged();
    }
}