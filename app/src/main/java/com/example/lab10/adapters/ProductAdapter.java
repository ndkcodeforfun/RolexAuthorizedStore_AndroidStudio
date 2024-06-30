package com.example.lab10.adapters;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.lab10.R;
import com.example.lab10.model.Category;
import com.example.lab10.model.Product;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products, Context context) {
        super(context, R.layout.category_row_layout, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.category_row_layout, parent, false);

        Product product = products.get(position);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        textViewName.setText(product.getName());
        ImageView imageView = convertView.findViewById(R.id.category_image_admin);
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String base64Image = product.getImages().get(0).getBase64StringImage();
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Glide.with(context)
                        .load(imageBytes)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.product); // Default image
            }
        } else {
            imageView.setImageResource(R.drawable.product); // Default image
        }
        return convertView;
    }
}
