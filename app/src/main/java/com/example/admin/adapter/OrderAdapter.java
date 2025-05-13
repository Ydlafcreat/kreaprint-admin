package com.example.admin.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.helper.firebase.OrderRepository;
import com.example.admin.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OrderRepository orderRepository;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
        this.orderRepository = new OrderRepository(); // Pastikan OrderRepository sudah benar
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        // Tampilkan status
        if (order.getStatus() != null) {
            switch (order.getStatus()) {
                case "completed":
                    holder.tvStatus.setText("Selesai");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "canceled":
                    holder.tvStatus.setText("Dibatalkan");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    holder.tvStatus.setText("Menunggu");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }
        }

        // Di dalam onBindViewHolder:
        holder.btnBatal.setOnClickListener(v -> {
            if (order.getOrderId() != null) {
                orderRepository.deleteOrder(order.getOrderId(), new OrderRepository.FirestoreCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(holder.itemView.getContext(), "Order dibatalkan", Toast.LENGTH_SHORT).show();
                        orderList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, orderList.size());
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Gagal membatalkan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(holder.itemView.getContext(), "ID Order tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnSelesai.setOnClickListener(v -> {
            if (order.getOrderId() != null) {
                orderRepository.updateOrderStatus(order.getOrderId(), "completed", new OrderRepository.FirestoreCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(holder.itemView.getContext(), "Order selesai", Toast.LENGTH_SHORT).show();
                        order.setStatus("completed");
                        notifyItemChanged(position);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Gagal menyelesaikan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(holder.itemView.getContext(), "ID Order tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvWhatsapp, tvOrderDetails, tvTotalPrice, tvStatus;
        Button btnBatal, btnSelesai;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvWhatsapp = itemView.findViewById(R.id.tv_whatsapp);
            tvOrderDetails = itemView.findViewById(R.id.tv_order_details);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_status); // Pastikan ada di layout
            btnBatal = itemView.findViewById(R.id.btn_batal);
            btnSelesai = itemView.findViewById(R.id.btn_selesai);
        }
    }

    public void updateList(List<Order> newOrderList) {
        orderList.clear();
        orderList.addAll(newOrderList);
        notifyDataSetChanged();
    }
}