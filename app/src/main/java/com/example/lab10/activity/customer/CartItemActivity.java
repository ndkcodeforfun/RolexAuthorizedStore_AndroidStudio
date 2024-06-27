package com.example.lab10.activity.customer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.activity.auth.JWTUtils;
import com.example.lab10.adapters.CartItemRecyclerViewAdapter;
import com.example.lab10.adapters.CategoryRecyclerViewAdapter;
import com.example.lab10.api.APIClient;
import com.example.lab10.api.CartItem.CartItemRepository;
import com.example.lab10.api.CartItem.CartItemService;
import com.example.lab10.api.Category.CategoryService;
import com.example.lab10.model.CartItem;
import com.example.lab10.model.Category;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemActivity extends AppCompatActivity {

    private RecyclerView itemRecyclerView;

    private int customerId;
    private TextView totalPriceTextView;

    private CartItemRecyclerViewAdapter cartAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        itemRecyclerView = findViewById(R.id.rvCartItems);

        totalPriceTextView = findViewById(R.id.totalPrice);

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
                Toast.makeText(CartItemActivity.this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }
        loadCartItem();
    }

    private void loadCartItem() {
        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);

        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> items = response.body();
                    cartAdapter = new CartItemRecyclerViewAdapter(items, CartItemActivity.this);
                    itemRecyclerView.setAdapter(cartAdapter);
                    calculateTotalPrice(items);

                } else {
                    Toast.makeText(CartItemActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(CartItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void calculateTotalPrice(List<CartItem> items) {
        double totalPrice = 0.0;
        for (CartItem item : items) {
            totalPrice += item.getProductVIew().getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("$%.2f", totalPrice));
    }
}