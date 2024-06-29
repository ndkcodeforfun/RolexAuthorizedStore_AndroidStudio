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

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {

    private List<OrderDtoResponse> orders;
    private Context context;

    public OrderRecyclerViewAdapter(List<OrderDtoResponse> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewAdapter.ViewHolder holder, int position) {
        OrderDtoResponse order = orders.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.totalPriceTextView.setText("Total Price: $" + order.getTotalPrice());

        // Set up the RecyclerView for order details
        OrderDetailRecyclerViewAdapter adapter = new OrderDetailRecyclerViewAdapter(order.getOrderDetails(), context);
        holder.orderDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.orderDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdTextView;
        public TextView totalPriceTextView;
        public RecyclerView orderDetailsRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
            orderDetailsRecyclerView = itemView.findViewById(R.id.order_details_recycler_view);
        }
    }
}
