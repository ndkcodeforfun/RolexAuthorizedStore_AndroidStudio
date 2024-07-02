package com.example.lab10.activity.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.adapters.OrderDetailRecyclerViewAdapter;
import com.example.lab10.adapters.OrderRecyclerViewAdapter;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.api.Customer.CustomerService;
import com.example.lab10.api.order.OrderRepository;
import com.example.lab10.api.order.OrderService;
import com.example.lab10.model.Customer;
import com.example.lab10.model.OrderDtoResponse;
import com.example.lab10.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdminDetailActivity extends AppCompatActivity {

    private OrderDtoResponse order;
    private RecyclerView orderDetailsRecyclerView;

    private TextView orderId, totalPrice, customerName, customerAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.order_admin_detail);
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
            Intent intent = new Intent(OrderAdminDetailActivity.this, OrderAdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            OrderAdminDetailActivity.this.finish();
        });
        orderId = findViewById(R.id.order_id);
        totalPrice = findViewById(R.id.total_price);
        customerName = findViewById(R.id.customer_name);
        customerAddress = findViewById(R.id.customer_address);
        orderDetailsRecyclerView = findViewById(R.id.order_details_recycler_view);
        Intent intent = getIntent();
        order = (OrderDtoResponse) intent.getSerializableExtra("order");

        orderId.setText(String.valueOf(order.getOrderId()));
        totalPrice.setText(String.valueOf(order.getTotalPrice()));
        CustomerService customerService = CustomerRepository.getCustomerService();
        Call<Customer> call = customerService.getCustomerInfomation(order.getCustomerId());
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Customer customer = response.body();
                    customerName.setText(customer.getEmail());
                    if(customer.getAddress() != null) {
                        customerAddress.setText(customer.getAddress());
                    }else{
                        customerAddress.setText("Not available");
                    }

                } else {
                    Log.e("OrderAdminDetailActivity", "Failed to get customer info");
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.e("OrderAdminDetailActivity", "Error fetching customer info", t);
            }
        });
        OrderDetailRecyclerViewAdapter adapter = new OrderDetailRecyclerViewAdapter(order.getOrderDetails(), OrderAdminDetailActivity.this);
        orderDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(OrderAdminDetailActivity.this));
        orderDetailsRecyclerView.setAdapter(adapter);

    }
}