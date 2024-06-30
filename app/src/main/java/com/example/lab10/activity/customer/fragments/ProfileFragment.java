package com.example.lab10.activity.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab10.R;
import com.example.lab10.activity.customer.MainActivity;
import com.example.lab10.activity.auth.LoginActivity;
import com.example.lab10.adapters.OrderRecyclerViewAdapter;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.api.Customer.CustomerService;
import com.example.lab10.api.order.OrderRepository;
import com.example.lab10.api.order.OrderService;
import com.example.lab10.model.Customer;
import com.example.lab10.model.OrderDtoResponse;
import com.example.lab10.activity.auth.JWTUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView emailTextView;
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView dobTextView;
    private ImageView profileImageView;
    private AppCompatButton btnSignOut;
    private AppCompatButton btnViewOrders;
    private RecyclerView ordersRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setTitle("Hồ sơ");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish(); // Call this to close the current activity
        });

        emailTextView = view.findViewById(R.id.email);
        nameTextView = view.findViewById(R.id.name);
        addressTextView = view.findViewById(R.id.address);
        phoneTextView = view.findViewById(R.id.phone);
        dobTextView = view.findViewById(R.id.dob);
        profileImageView = view.findViewById(R.id.profile_image);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnViewOrders = view.findViewById(R.id.btnViewOrders);
        ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);

        btnSignOut.setOnClickListener(v -> signOut());
        btnViewOrders.setOnClickListener(v -> viewOrders());

        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            accessToken = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                int customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
                getCustomerInfo(customerId);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void getCustomerInfo(int customerId) {
        CustomerService customerService = CustomerRepository.getCustomerService();
        Call<Customer> call = customerService.getCustomerInfomation(customerId);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Customer customer = response.body();
                    emailTextView.setText(customer.getEmail());
                    nameTextView.setText(customer.getName());
                    addressTextView.setText(customer.getAddress());
                    phoneTextView.setText(customer.getPhone());
                    dobTextView.setText(customer.getDoB().toString());

                    // If you have a profile image URL or base64, you can load it into profileImageView using Glide
                    // Glide.with(getActivity()).load(customer.getProfileImageUrl()).into(profileImageView);
                } else {
                    Log.e("ProfileFragment", "Failed to get customer info");
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching customer info", t);
            }
        });
    }

    private void viewOrders() {
        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            accessToken = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                int customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());

                OrderService orderService = OrderRepository.getOrderService();
                Call<List<OrderDtoResponse>> call = orderService.getOrdersByCustomerId(customerId);
                call.enqueue(new Callback<List<OrderDtoResponse>>() {
                    @Override
                    public void onResponse(Call<List<OrderDtoResponse>> call, Response<List<OrderDtoResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<OrderDtoResponse> orders = response.body();
                            // Display orders in the RecyclerView
                            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            ordersRecyclerView.setAdapter(new OrderRecyclerViewAdapter(orders, getContext()));
                        } else {
                            Log.e("ProfileFragment", "Failed to get orders");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderDtoResponse>> call, Throwable t) {
                        Log.e("ProfileFragment", "Error fetching orders", t);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("accessToken");
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}



//package com.example.lab10.activity.customer.fragments;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.widget.Toolbar;
//
//import com.bumptech.glide.Glide;
//import com.example.lab10.R;
//import com.example.lab10.activity.customer.MainActivity;
//import com.example.lab10.activity.auth.LoginActivity;
//import com.example.lab10.api.Customer.CustomerRepository;
//import com.example.lab10.api.Customer.CustomerService;
//import com.example.lab10.api.order.OrderRepository;
//import com.example.lab10.api.order.OrderService;
//import com.example.lab10.model.Customer;
//import com.example.lab10.model.OrderDtoResponse;
//import com.example.lab10.activity.auth.JWTUtils;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ProfileFragment extends Fragment {
//
//    private TextView emailTextView;
//    private TextView nameTextView;
//    private TextView addressTextView;
//    private TextView phoneTextView;
//    private TextView dobTextView;
//    private ImageView profileImageView;
//    private AppCompatButton btnSignOut;
//    private AppCompatButton btnViewOrders;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
//        toolbar.setNavigationOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            getActivity().finish(); // Call this to close the current activity
//        });
//
//        emailTextView = view.findViewById(R.id.email);
//        nameTextView = view.findViewById(R.id.name);
//        addressTextView = view.findViewById(R.id.address);
//        phoneTextView = view.findViewById(R.id.phone);
//        dobTextView = view.findViewById(R.id.dob);
//        profileImageView = view.findViewById(R.id.profile_image);
//        btnSignOut = view.findViewById(R.id.btnSignOut);
//        btnViewOrders = view.findViewById(R.id.btnViewOrders);
//
//        btnSignOut.setOnClickListener(v -> signOut());
//        btnViewOrders.setOnClickListener(v -> viewOrders());
//
//        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
//        if (accessToken == null) {
//            accessToken = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", null);
//        }
//        if (accessToken != null) {
//            try {
//                String[] decodedParts = JWTUtils.decoded(accessToken);
//                String body = decodedParts[1];
//                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
//                int customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
//                getCustomerInfo(customerId);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(getActivity(), "Failed to decode token", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        return view;
//    }
//
//    private void getCustomerInfo(int customerId) {
//        CustomerService customerService = CustomerRepository.getCustomerService();
//        Call<Customer> call = customerService.getCustomerInfomation(customerId);
//        call.enqueue(new Callback<Customer>() {
//            @Override
//            public void onResponse(Call<Customer> call, Response<Customer> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Customer customer = response.body();
//                    emailTextView.setText(customer.getEmail());
//                    nameTextView.setText(customer.getName());
//                    addressTextView.setText(customer.getAddress());
//                    phoneTextView.setText(customer.getPhone());
//                    dobTextView.setText(customer.getDoB().toString());
//
//                    // If you have a profile image URL or base64, you can load it into profileImageView using Glide
//                    // Glide.with(getActivity()).load(customer.getProfileImageUrl()).into(profileImageView);
//                } else {
//                    Log.e("ProfileFragment", "Failed to get customer info");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Customer> call, Throwable t) {
//                Log.e("ProfileFragment", "Error fetching customer info", t);
//            }
//        });
//    }
//
//    private void viewOrders() {
//        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
//        if (accessToken == null) {
//            accessToken = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", null);
//        }
//        if (accessToken != null) {
//            try {
//                String[] decodedParts = JWTUtils.decoded(accessToken);
//                String body = decodedParts[1];
//                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
//                int customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
//
//                OrderService orderService = OrderRepository.getOrderService();
//                Call<List<OrderDtoResponse>> call = orderService.getOrdersByCustomerId(customerId);
//                call.enqueue(new Callback<List<OrderDtoResponse>>() {
//                    @Override
//                    public void onResponse(Call<List<OrderDtoResponse>> call, Response<List<OrderDtoResponse>> response) {
//                        if (response.isSuccessful() && response.body() != null) {
//                            List<OrderDtoResponse> orders = response.body();
//                            // Display orders in a new fragment or activity
//                            // For simplicity, we'll just log the orders
//                            for (OrderDtoResponse order : orders) {
//                                Log.d("Order", order.toString());
//                            }
//                        } else {
//                            Log.e("ProfileFragment", "Failed to get orders");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<OrderDtoResponse>> call, Throwable t) {
//                        Log.e("ProfileFragment", "Error fetching orders", t);
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(getActivity(), "Failed to decode token", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void signOut() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("accessToken");
//        editor.apply();
//
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        getActivity().finish();
//    }
//}
