package com.example.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.model.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<User> customers;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public CustomerAdapter(List<User> customers) {
        this.customers = customers;
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
        User customer = customers.get(position);
        holder.tvName.setText(customer.getName());
        holder.tvEmail.setText(customer.getEmail());
        holder.tvDate.setText(dateFormat.format(customer.getRegistrationDate()));
        holder.tvStatus.setText(customer.isActive() ? "Aktif" : "Nonaktif");
        holder.tvStatus.setTextColor(customer.isActive() ?
                holder.itemView.getContext().getColor(android.R.color.holo_green_light) :
                holder.itemView.getContext().getColor(android.R.color.holo_red_light));
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void updateData(List<User> newCustomers) {
        customers = newCustomers;
        notifyDataSetChanged();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvDate, tvStatus;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_customer_name);
            tvEmail = itemView.findViewById(R.id.tv_customer_email);
            tvDate = itemView.findViewById(R.id.tv_customer_date);
            tvStatus = itemView.findViewById(R.id.tv_customer_status);
        }
    }
}