package com.example.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final List<Customer> customers;
    private final Context context;

    public CustomerAdapter(List<Customer> customers, Context context) {
        this.customers = customers;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer, context);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvStatus;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_customer_name);
            tvEmail = itemView.findViewById(R.id.tv_customer_email);
            tvStatus = itemView.findViewById(R.id.tv_customer_status);
        }

        public void bind(Customer customer, Context context) {
            tvName.setText(customer.getName());
            tvEmail.setText(customer.getEmail());
            tvStatus.setText(customer.getStatus());

            // Set status color
            int colorRes = getStatusColor(customer.getStatus());
            tvStatus.setTextColor(ContextCompat.getColor(context, colorRes));
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "Aktif": return R.color.green;
                case "Baru": return R.color.orange;
                default: return R.color.gray;
            }
        }
    }
}