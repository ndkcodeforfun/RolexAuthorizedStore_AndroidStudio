package com.example.lab10.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab10.R;
import com.example.lab10.activity.customer.ProductDetailActivity;
import com.example.lab10.model.CartItem;
import com.example.lab10.model.Product;

import java.util.List;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder>{
    private List<CartItem> items;
    private Context context;


    public CartItemRecyclerViewAdapter(List<CartItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartItemRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemRecyclerViewAdapter.ViewHolder holder, int position) {
        CartItem cart = items.get(position);
        holder.textViewName.setText(cart.getProductVIew().getName());
        holder.textViewPrice.setText(String.format("$%.2f", cart.getProductVIew().getPrice()));
        holder.quantityEditText.setText(String.valueOf(cart.getQuantity()));
        // Load image using Glide
        if (cart.getProductVIew().getImages() != null && !cart.getProductVIew().getImages().isEmpty()) {
            String base64Image = cart.getProductVIew().getImages().get(0).getBase64StringImage();
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Glide.with(context)
                        .load(imageBytes)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.product); // Default image
            }
        } else {
            holder.imageView.setImageResource(R.drawable.product); // Default image
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewPrice;
        public ImageView imageView;

        public EditText quantityEditText;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvItemName);
            textViewPrice = itemView.findViewById(R.id.tvItemPrice);
            quantityEditText = itemView.findViewById(R.id.tvItemQuantity);

            imageView = itemView.findViewById(R.id.imgItem);
        }
    }
}
