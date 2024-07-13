package com.example.lab10.activity.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.lab10.R;
import com.example.lab10.activity.auth.JWTUtils;
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

    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;
    private TextView productStatusTextView;
    private EditText quantityTextView;
    private Button buttonAddToCart;
    private ImageView productImageView;
    private Product product;
    private int customerId;
    private final String REQUIRE = "Require";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);
        productPriceTextView = findViewById(R.id.product_price);
        productStatusTextView = findViewById(R.id.product_status);
        quantityTextView = findViewById(R.id.number_edit_text);
        productImageView = findViewById(R.id.product_image);

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        if (product != null) {
            productNameTextView.setText(product.getName());
            productDescriptionTextView.setText(product.getDescription());
            productPriceTextView.setText(String.format("%.2f VND", product.getPrice()));
            productStatusTextView.setText(product.getQuantity() > 0 ? "Sản phẩm còn hàng" : "Sản phẩm đã hết hàng");
            productStatusTextView.setTextColor(getResources().getColor(product.getQuantity() > 0 ? R.color.green : R.color.red));

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String base64Image = product.getImages().get(0).getBase64StringImage();
                if (base64Image != null && !base64Image.isEmpty()) {
                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Glide.with(this)
                            .load(imageBytes)
                            .into(productImageView);
                } else {
                    productImageView.setImageResource(R.drawable.product); // Default image
                }
            } else {
                productImageView.setImageResource(R.drawable.product); // Default image
            }
        }

        buttonAddToCart = findViewById(R.id.btnAddToCart);
        buttonAddToCart.setOnClickListener(v -> {
            if (!checkInput()) {
                return;
            }
            try {
                CartItem item = new CartItem();
                item.setProductId(product.getProductId());
                item.setCustomerId(customerId);
                item.setQuantity(Integer.parseInt(quantityTextView.getText().toString()));
                if(item.getQuantity() > product.getQuantity()) {
                    Toast.makeText(getApplicationContext(), "You choose over quantity in our store, please try again", Toast.LENGTH_SHORT).show();
                    return;
                }
                CartItemService cartItemService = CartItemRepository.getCartItemService();
                Call<Void> call = cartItemService.AddToCart(item);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Thêm sản phẩm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Thêm sản phẩm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        String accessToken = getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];

                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ProductDetailActivity.this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(quantityTextView.getText().toString())) {
            quantityTextView.setError(REQUIRE);
            return false;
        }
        if (Integer.parseInt(quantityTextView.getText().toString()) <= 0) {
            quantityTextView.setError(REQUIRE);
            return false;
        }
        return true;
    }
}
