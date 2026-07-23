package com.example.smartfinanceassistant.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinanceassistant.R;

import java.util.List;

public class ProductAdapter extends
        RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;

    // Product Model
    public static class Product {
        public String title, price, rating, url, image;

        public Product(String title, String price,
                       String rating, String url, String image) {
            this.title  = title;
            this.price  = price;
            this.rating = rating;
            this.url    = url;
            this.image  = image;
        }
    }

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void updateList(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvTitle.setText(product.title);
        holder.tvPrice.setText("💰 " + product.price);
        holder.tvRating.setText("⭐ " + product.rating);

        // View on Amazon button
        holder.btnView.setOnClickListener(v -> {
            if (!product.url.isEmpty()) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(product.url));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class ViewHolder extends
            RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvRating;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle  = itemView.findViewById(R.id.tvProductTitle);
            tvPrice  = itemView.findViewById(R.id.tvProductPrice);
            tvRating = itemView.findViewById(R.id.tvProductRating);
            btnView  = itemView.findViewById(R.id.btnViewProduct);
        }
    }
}