package com.example.admin.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.helper.ToastHelper;
import com.example.admin.helper.firebase.OrderRepository;
import com.example.admin.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OrderRepository orderRepository;

    private ToastHelper orderAdapterToast;


    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
        this.orderRepository = new OrderRepository();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        orderAdapterToast = new ToastHelper(parent.getContext());
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
        holder.btnBatal.setVisibility(View.VISIBLE);
        holder.btnSelesai.setVisibility(View.VISIBLE);

        // Tampilkan status
        if (order.getStatus() != null) {
            switch (order.getStatus()) {
                case "completed":
                    holder.tvStatus.setText("Selesai");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    holder.btnBatal.setVisibility(View.GONE);
                    holder.btnSelesai.setVisibility(View.GONE);
                    break;
                case "canceled":
                    holder.tvStatus.setText("Dibatalkan");
                    holder.btnBatal.setVisibility(View.GONE);
                    holder.btnSelesai.setVisibility(View.GONE);
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    holder.tvStatus.setText("Menunggu");
                    holder.btnBatal.setVisibility(View.VISIBLE);
                    holder.btnSelesai.setVisibility(View.VISIBLE);
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }
        }



        holder.btnBatal.setOnClickListener(v -> {
            if (order.getOrderId() != null) {
                View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_alert_popup, null);

                TextView tvTitle = dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = dialogView.findViewById(R.id.tv_message);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

                tvTitle.setText("Batalkan Order");
                tvMessage.setText("Apakah Anda yakin ingin membatalkan pesanan ini?");

                AlertDialog dialog = new AlertDialog.Builder(holder.itemView.getContext())
                        .setView(dialogView)
                        .create();

                dialog.setCanceledOnTouchOutside(false);

                btnCancel.setOnClickListener(v1 -> dialog.dismiss());

                btnConfirm.setOnClickListener(v1 -> {
                    orderRepository.updateOrderStatus(order.getOrderId(), "completed", new OrderRepository.FirestoreCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            orderAdapterToast.showToast("Order dibatalkan");

                            order.setStatus("canceled");
                            holder.tvStatus.setText("Dibatalkan");
                            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                            holder.btnBatal.setVisibility(View.GONE);
                            holder.btnSelesai.setVisibility(View.GONE);
                            notifyItemChanged(position);
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(holder.itemView.getContext(), "Gagal menyelesaikan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                dialog.show();
            } else {
                Toast.makeText(holder.itemView.getContext(), "ID Order tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (order.getOrderId() != null) {
                // Inflasi layout custom
                View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_alert_popup, null);

                // Inisialisasi view dari layout custom
                TextView tvTitle = dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = dialogView.findViewById(R.id.tv_message);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

                tvTitle.setText("Hapus Order");
                tvMessage.setText("Apakah Anda yakin ingin menghapus pesanan ini?");

                AlertDialog dialog = new AlertDialog.Builder(holder.itemView.getContext())
                        .setView(dialogView)
                        .create();

                dialog.setCanceledOnTouchOutside(false);

                btnCancel.setOnClickListener(v1 -> dialog.dismiss());

                btnConfirm.setOnClickListener(v1 -> {
                    // Proses pembatalan
                    orderRepository.deleteOrder(order.getOrderId(), new OrderRepository.FirestoreCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            orderAdapterToast.showToast("Order Dihapus");
                            holder.tvStatus.setText("Dihapuss..");
                            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                            holder.btnBatal.setVisibility(View.GONE);
                            holder.btnSelesai.setVisibility(View.GONE);
                            orderList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, orderList.size());
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(holder.itemView.getContext(), "Gagal membatalkan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                dialog.show();
            } else {
                Toast.makeText(holder.itemView.getContext(), "ID Order tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnSelesai.setOnClickListener(v -> {
            if (order.getOrderId() != null) {
                View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_alert_popup, null);

                TextView tvTitle = dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = dialogView.findViewById(R.id.tv_message);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

                tvTitle.setText("Selesaikan Order");
                tvMessage.setText("Apakah Anda yakin ingin menyelesaikan pesanan ini?");

                AlertDialog dialog = new AlertDialog.Builder(holder.itemView.getContext())
                        .setView(dialogView)
                        .create();

                dialog.setCanceledOnTouchOutside(false);

                btnCancel.setOnClickListener(v1 -> dialog.dismiss());

                btnConfirm.setOnClickListener(v1 -> {
                    // Proses penyelesaian
                    orderRepository.updateOrderStatus(order.getOrderId(), "completed", new OrderRepository.FirestoreCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            orderAdapterToast.showToast("Order berhasil diselesaikan");

                            order.setStatus("completed");
                            holder.tvStatus.setText("Selesai");
                            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                            holder.btnBatal.setVisibility(View.GONE);
                            holder.btnSelesai.setVisibility(View.GONE);
                            notifyItemChanged(position);
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(holder.itemView.getContext(), "Gagal menyelesaikan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                dialog.show();
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

        CardView btnDelete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvWhatsapp = itemView.findViewById(R.id.tv_whatsapp);
            tvOrderDetails = itemView.findViewById(R.id.tv_order_details);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnBatal = itemView.findViewById(R.id.btn_batal);
            btnSelesai = itemView.findViewById(R.id.btn_selesai);
            btnDelete = itemView.findViewById(R.id.btn_delete_order);
        }
    }

    public void updateList(List<Order> newOrderList) {
        orderList.clear();
        orderList.addAll(newOrderList);
        notifyDataSetChanged();
    }


}