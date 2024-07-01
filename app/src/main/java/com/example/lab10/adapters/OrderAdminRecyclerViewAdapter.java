package com.example.lab10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.model.OrderDtoResponse;

import java.util.List;

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
        holder.orderCustomerName.setText(String.valueOf(order.getCustomerId()));
        holder.orderTotalPrice.setText(String.valueOf(order.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdTextView;
        public TextView orderCustomerName;
        public TextView orderTotalPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textViewOrderID);
            orderCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            orderTotalPrice = itemView.findViewById(R.id.textviewTotalPrice);
        }
    }
}
