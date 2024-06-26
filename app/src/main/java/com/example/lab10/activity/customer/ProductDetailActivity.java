package com.example.lab10.activity.customer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab10.R;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_homepage);

        productImageView = findViewById(R.id.product_image);
        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);

        // Get data from intent
        String productName = getIntent().getStringExtra("productName");
        String productDescription = getIntent().getStringExtra("productDescription");
        int productImage = getIntent().getIntExtra("productImage", R.drawable.product);

        // Set data to views
        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        productImageView.setImageResource(productImage); // Use static image resource
    }
}
