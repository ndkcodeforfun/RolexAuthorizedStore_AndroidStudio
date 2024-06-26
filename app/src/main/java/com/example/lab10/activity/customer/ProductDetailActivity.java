package com.example.lab10.activity.customer;

import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lab10.R;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_homepage);

        productImageView = findViewById(R.id.product_image);
        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);
        productPriceTextView = findViewById(R.id.product_price);

        // Get data from intent
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        double productPrice = getIntent().getDoubleExtra("productPrice", 0.0);
        String base64Image = getIntent().getStringExtra("productImage");

        // Set data to views
        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        productPriceTextView.setText(String.format("$%.2f", productPrice));
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(this).load(imageBytes).into(productImageView); // Load image using Glide
        } else {
            productImageView.setImageResource(R.drawable.product); // Default image
        }
    }
}
