package com.example.lab10.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab10.R;
import com.example.lab10.adapters.CustomerAdapter;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.model.Customer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatCustomerListActivity extends AppCompatActivity {
    private ListView customerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_customer_list);

        customerListView = findViewById(R.id.customerListView);
        loadCustomerList();
    }

    private void loadCustomerList() {
        CustomerRepository.getCustomerService().getCustomers().enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful()) {
                    List<Customer> customers = response.body();
                    CustomerAdapter adapter = new CustomerAdapter(ChatCustomerListActivity.this, customers);
                    customerListView.setAdapter(adapter);

                    customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Customer selectedCustomer = customers.get(position);
                            Intent intent = new Intent(ChatCustomerListActivity.this, AdminChatActivity.class);
                            intent.putExtra("CustomerId", selectedCustomer.getCustomerId());
                            intent.putExtra("CustomerName", selectedCustomer.getName());
                            startActivity(intent);
                        }
                    });
                } else {
                    Log.e("ChatCustomerListActivity", "Tải danh sách customer thất bại (onResponse)");
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Log.e("ChatCustomerListActivity", "Tải danh sách customer thất bại (onFailure)", t);
            }
        });
    }
}
