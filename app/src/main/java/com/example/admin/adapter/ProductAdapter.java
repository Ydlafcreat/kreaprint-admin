package com.example.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.DetailProduk;
import com.example.admin.R;
import com.example.admin.helper.ImageLoaderHelper;
import com.example.admin.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product produk = productList.get(position);
        holder.tvNama.setText(produk.getName());

        ImageLoaderHelper.loadImage(
                holder.itemView.getContext(),
                produk.getImageUrls().get(0),
                holder.imgProduct
        );
        holder.productCard.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailProduk.class);

            intent.putExtra("NAMA_PRODUK", produk.getName());
            intent.putExtra("ID_PRODUK", produk.getId());

            context.startActivity(intent);
        });
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void updateList(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView imgProduct;
        CardView productCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_produk);
            imgProduct = itemView.findViewById(R.id.iv_produk);
            productCard = itemView.findViewById(R.id.productCard);
        }
    }
}
