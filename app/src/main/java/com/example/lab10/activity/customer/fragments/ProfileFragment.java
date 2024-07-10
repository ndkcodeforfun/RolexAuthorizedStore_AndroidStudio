package com.example.lab10.activity.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.lab10.activity.auth.LoginActivity;
import com.example.lab10.activity.customer.MainActivity;
import com.example.lab10.adapters.OrderRecyclerViewAdapter;
import com.example.lab10.api.ApiResponse;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.api.Customer.CustomerService;
import com.example.lab10.api.order.OrderRepository;
import com.example.lab10.api.order.OrderService;
import com.example.lab10.model.Customer;
import com.example.lab10.model.OrderDtoResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private EditText emailEditText;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText phoneEditText;
    private EditText dobEditText;
    private AppCompatButton btnUpdate;
    private AppCompatButton btnSignOut;
    private AppCompatButton btnViewOrders;
    private RecyclerView ordersRecyclerView;
    private int customerId;

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
            getActivity().finish();
        });

        emailEditText = view.findViewById(R.id.editTextEmail);
        nameEditText = view.findViewById(R.id.editTextName);
        addressEditText = view.findViewById(R.id.editTextAddress);
        phoneEditText = view.findViewById(R.id.editTextPhone);
        dobEditText = view.findViewById(R.id.editTextDob);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnViewOrders = view.findViewById(R.id.btnViewOrders);
        ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);

        btnSignOut.setOnClickListener(v -> signOut());
        btnViewOrders.setOnClickListener(v -> viewOrders());
        btnUpdate.setOnClickListener(v -> updateCustomerInfo());

        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            accessToken = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
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
                    emailEditText.setText(customer.getEmail());
                    nameEditText.setText(customer.getName());
                    addressEditText.setText(customer.getAddress());
                    phoneEditText.setText(customer.getPhone());
                    dobEditText.setText(customer.getDoB() != null ? customer.getDoB().toString() : "Not available");
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

    private void updateCustomerInfo() {
        // Lấy các giá trị từ các trường EditText
        String name = nameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String dob = dobEditText.getText().toString();

        // Tạo đối tượng Customer
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setDoB(dob);

        // Gọi API để cập nhật thông tin khách hàng
        Call<ApiResponse> call = CustomerRepository.updateCustomerInfo(customerId, customer);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(getActivity(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Cập nhật thông tin thất bại: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("ProfileFragment", "Failed to update customer info: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                Log.e("ProfileFragment", "Error updating customer info", t);
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
