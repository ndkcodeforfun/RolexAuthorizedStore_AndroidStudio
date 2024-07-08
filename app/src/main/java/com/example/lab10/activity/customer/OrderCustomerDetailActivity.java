package com.example.lab10.activity.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.activity.auth.JWTUtils;
import com.example.lab10.activity.customer.fragments.CartFragment;
import com.example.lab10.adapters.CartItemRecyclerViewAdapter;
import com.example.lab10.adapters.OrderCustomerDetailRecycleViewAdapter;
import com.example.lab10.adapters.OrderDetailRecyclerViewAdapter;
import com.example.lab10.api.CartItem.CartItemRepository;
import com.example.lab10.api.CartItem.CartItemService;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.api.Customer.CustomerService;
import com.example.lab10.model.CartItem;
import com.example.lab10.model.Customer;
import com.example.lab10.model.OrderDtoResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCustomerDetailActivity extends AppCompatActivity {

    private List<OrderDtoResponse> products;
    private RecyclerView itemRecyclerView;
    private OrderCustomerDetailRecycleViewAdapter orderAdapter;
    private List<CartItem> items;
    private int customerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_customer_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chi tiết đơn hàng");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(OrderCustomerDetailActivity.this, CartFragment.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            OrderCustomerDetailActivity.this.finish();
        });

        itemRecyclerView = findViewById(R.id.orders_recycler_view);

        // Retrieve the accessToken from the intent
        String accessToken = getIntent().getStringExtra("accessToken");

        // If accessToken is not available in the intent, get it from shared preferences
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }

        // If accessToken is available, decode it to extract the CustomerId
        if (accessToken != null) {
            try {
                // Decode the accessToken (assumes JWTUtils is a utility class to decode JWT tokens)
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];  // The payload of the JWT token

                // Parse the body to get the CustomerId
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());

                // Use the customerId as needed
                // For example, display it in a TextView or use it in your business logic

            } catch (Exception e) {
                // Handle exceptions (e.g., decoding errors, JSON parsing errors)
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }


        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);
        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful()) {
                    items = response.body();
                    orderAdapter = new OrderCustomerDetailRecycleViewAdapter(items, OrderCustomerDetailActivity.this);
                    itemRecyclerView.setLayoutManager(new LinearLayoutManager(OrderCustomerDetailActivity.this)); // Set LayoutManager
                    itemRecyclerView.setAdapter(orderAdapter);
                   // calculateTotalPrice(items);
                } else {
                    Toast.makeText(OrderCustomerDetailActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(OrderCustomerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


//        Call<Customer> call = customerService.getCustomerInfomation(order.getCustomerId());
//        call.enqueue(new Callback<Customer>() {
//            @Override
//            public void onResponse(Call<Customer> call, Response<Customer> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Customer customer = response.body();
//                    customerName.setText(customer.getEmail());
//                    if(customer.getAddress() != null) {
//                        customerAddress.setText(customer.getAddress());
//                    }else{
//                        customerAddress.setText("Not available");
//                    }
//
//                } else {
//                    Log.e("OrderAdminDetailActivity", "Failed to get customer info");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Customer> call, Throwable t) {
//                Log.e("OrderAdminDetailActivity", "Error fetching customer info", t);
//            }
//        });
//        OrderDetailRecyclerViewAdapter adapter = new OrderDetailRecyclerViewAdapter(order.getOrderDetails(), OrderCustomerDetailActivity.this);
//        orderDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(OrderCustomerDetailActivity.this));
//        orderDetailsRecyclerView.setAdapter(adapter);

    }
}