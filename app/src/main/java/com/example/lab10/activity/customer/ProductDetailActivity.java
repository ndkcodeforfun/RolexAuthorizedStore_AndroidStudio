package com.example.lab10.activity.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lab10.R;
import com.example.lab10.activity.admin.AddCategoryActivity;
import com.example.lab10.activity.admin.CategoryActivity;
import com.example.lab10.activity.auth.JWTUtils;
import com.example.lab10.activity.auth.LoginActivity;
import com.example.lab10.api.CartItem.CartItemRepository;
import com.example.lab10.api.CartItem.CartItemService;
import com.example.lab10.model.CartItem;
import com.example.lab10.model.Product;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;

    private TextView productStatusTextView;

    private EditText quantityTextView;

    private Button buttonAddToCart;

    private Button buttonViewCart;
    private Product product;

    private int customerId;

    private final String REQUIRE = "Require";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_homepage);

        productImageView = findViewById(R.id.product_image);
        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);
        productPriceTextView = findViewById(R.id.product_price);
        productStatusTextView = findViewById(R.id.product_status);
        quantityTextView = findViewById(R.id.number_edit_text);
        buttonViewCart = findViewById(R.id.cart);
        buttonViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, CartItemActivity.class);
                startActivity(intent);
            }
        });


        String accessToken = getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }
        if(accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];

                // Parse the body to get the role
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ProductDetailActivity.this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }



        // Get data from intent
//        String productName = getIntent().getStringExtra("productName");
//        String productDescription = getIntent().getStringExtra("productDescription");
//        double productPrice = getIntent().getDoubleExtra("productPrice", 0.0);
        String base64Image = getIntent().getStringExtra("productImage");
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        // Set data to views
        productNameTextView.setText(product.getName());
        productDescriptionTextView.setText(product.getDescription());
        productPriceTextView.setText(String.format("$%.2f", product.getPrice()));
        if (product.getQuantity() > 0) {
            productStatusTextView.setText("Product is avaiable");
        } else if(product.getQuantity() == 0) {
            productStatusTextView.setText("Not avaiable");
            productStatusTextView.setTextColor(getResources().getColor(R.color.red));
        }
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(this).load(imageBytes).into(productImageView); // Load image using Glide
        } else {
            productImageView.setImageResource(R.drawable.product); // Default image
        }

        buttonAddToCart = findViewById(R.id.btnAddToCart);
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    return;
                }
                try{
                    CartItem item = new CartItem();
                    item.setProductId(product.getProductId());
                    item.setCustomerId(customerId);
                    item.setQuantity(Integer.parseInt(quantityTextView.getText().toString()));
                    CartItemService cartItemService = CartItemRepository.getCartItemService();
                    Call<Void> call = cartItemService.AddToCart(item);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "Add to cart successfully", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean checkInput() {
        if (TextUtils.isEmpty(quantityTextView.getText().toString())) {
            quantityTextView.setError(REQUIRE);
            return false;
        }
        if(Integer.parseInt(quantityTextView.getText().toString()) <= 0) {
            quantityTextView.setError(REQUIRE);
            return false;
        }



        return true;
    }
}
