package com.example.lab10.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.api.Customer.CustomerRepository;
import com.example.lab10.api.Customer.CustomerService;
import com.example.lab10.api.Product.ProductRepository;
import com.example.lab10.api.Product.ProductService;
import com.example.lab10.model.Customer;
import com.example.lab10.model.OrderDetailDtoResponse;
import com.example.lab10.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        ProductService productService = ProductRepository.getProductService();
        Call<Product> call = productService.find(detail.getProductId());
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    holder.productNameTextView.setText("Tên sản phẩm: " + product.getName());
                } else {
                    Log.e("ProfileFragment", "Failed to get customer info");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching customer info", t);
            }
        });
        holder.pricePerUnitTextView.setText("Giá: $" + detail.getPricePerUnit());
        holder.quantityTextView.setText("Số lượng: " + detail.getQuantity());
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
