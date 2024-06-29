package com.example.lab10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.model.OrderDetailDtoResponse;

import java.util.List;

public class OrderDetailRecyclerViewAdapter extends RecyclerView.Adapter<OrderDetailRecyclerViewAdapter.ViewHolder> {

    private List<OrderDetailDtoResponse> orderDetails;
    private Context context;

    public OrderDetailRecyclerViewAdapter(List<OrderDetailDtoResponse> orderDetails, Context context) {
        this.orderDetails = orderDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderDetailRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailRecyclerViewAdapter.ViewHolder holder, int position) {
        OrderDetailDtoResponse detail = orderDetails.get(position);
        holder.productNameTextView.setText("Product Name: " + detail.getProductName());
        holder.pricePerUnitTextView.setText("Price Per Unit: $" + detail.getPricePerUnit());
        holder.quantityTextView.setText("Quantity: " + detail.getQuantity());
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        public TextView pricePerUnitTextView;
        public TextView quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name);
            pricePerUnitTextView = itemView.findViewById(R.id.price_per_unit);
            quantityTextView = itemView.findViewById(R.id.quantity);
        }
    }
}
