package com.example.lab10.activity.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.activity.auth.JWTUtils;
import com.example.lab10.activity.customer.MainActivity;
import com.example.lab10.adapters.CartItemRecyclerViewAdapter;
import com.example.lab10.api.CartItem.CartItemRepository;
import com.example.lab10.api.CartItem.CartItemService;
import com.example.lab10.api.order.OrderRepository;
import com.example.lab10.api.order.OrderService;
import com.example.lab10.model.CartItem;
import com.example.lab10.model.OrderRequestDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView itemRecyclerView;
    private TextView totalPriceTextView;
    private CartItemRecyclerViewAdapter cartAdapter;
    private int customerId;

    AppCompatButton buttonOrder;
    private List<CartItem> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Cart");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        itemRecyclerView = view.findViewById(R.id.rvCartItems);
        totalPriceTextView = view.findViewById(R.id.totalPrice);
        buttonOrder = view.findViewById(R.id.btnOrder);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];

                // Parse the body to get the role
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }
        loadCartItem();
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<OrderRequestDto> cartItems = new ArrayList<>();
                for (CartItem item : items) {
                    OrderRequestDto orderProduct = new OrderRequestDto();
                    orderProduct.setItemId(item.getItemId());
                    orderProduct.setCustomerId(item.getCustomerId());
                    orderProduct.setProductId(item.getProductId());
                    orderProduct.setQuantity(item.getQuantity());
                    cartItems.add(orderProduct);
                }

                OrderService orderService = OrderRepository.getOrderService();
                Call<Void> call = orderService.createOrder(cartItems);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Order successfully", Toast.LENGTH_SHORT).show();
                            // Điều hướng về MainActivity sau khi đặt hàng thành công
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            try {
                                // Log the error body to understand the 400 error
                                String errorBody = response.errorBody().string();
                                Log.e("OrderError", "Error: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(), "Order fail", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }

    private void loadCartItem() {
        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);

        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful()) {
                    items = response.body();
                    cartAdapter = new CartItemRecyclerViewAdapter(items, getContext(), CartFragment.this);
                    itemRecyclerView.setAdapter(cartAdapter);
                    calculateTotalPrice(items);
                } else {
                    Toast.makeText(getContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void updateTotalPrice() {
        calculateTotalPrice(items);
    }
}
