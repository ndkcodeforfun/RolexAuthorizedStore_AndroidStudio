package com.example.lab10.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.activity.admin.OrderAdminDetailActivity;
import com.example.lab10.activity.customer.ProductDetailActivity;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.api.Customer.CustomerService;
import com.example.lab10.model.Customer;
import com.example.lab10.model.OrderDtoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdminRecyclerViewAdapter extends RecyclerView.Adapter<OrderAdminRecyclerViewAdapter.ViewHolder> {

    private List<OrderDtoResponse> orders;
    private Context context;

    public OrderAdminRecyclerViewAdapter(List<OrderDtoResponse> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdminRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_layout, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new OrderAdminRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminRecyclerViewAdapter.ViewHolder holder, int position) {
        OrderDtoResponse order = orders.get(position);
        holder.orderIdTextView.setText(String.valueOf(order.getOrderId()));
        CustomerService customerService = CustomerRepository.getCustomerService();
        Call<Customer> call = customerService.getCustomerInfomation(order.getCustomerId());
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Customer customer = response.body();
                    holder.orderCustomerName.setText(customer.getEmail());
                } else {
                    Log.e("ProfileFragment", "Failed to get customer info");
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching customer info", t);
            }
        });
        holder.orderTotalPrice.setText(String.valueOf(order.getTotalPrice()));

        if (order.getStatus() == 1){
            holder.orderStatus.setText("Đã th.toán");
        } else if (order.getStatus() == 0) {
            holder.orderStatus.setText("Chưa th.toán");
        } else if (order.getStatus() == 2) {
            holder.orderStatus.setText("Đã hủy");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderAdminDetailActivity.class);
            intent.putExtra("order", order);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdTextView;
        public TextView orderCustomerName;
        public TextView orderTotalPrice;
        public TextView orderStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textViewOrderID);
            orderCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            orderTotalPrice = itemView.findViewById(R.id.textviewTotalPrice);
            orderStatus = itemView.findViewById(R.id.textviewOrderStatus);
        }
    }
}
