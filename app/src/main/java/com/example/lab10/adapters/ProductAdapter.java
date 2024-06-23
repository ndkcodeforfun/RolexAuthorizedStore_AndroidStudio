package com.example.lab10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lab10.R;
import com.example.lab10.model.Category;
import com.example.lab10.model.Product;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products, Context context) {
        super(context, R.layout.category_row_layout, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.category_row_layout, parent, false);

        Product product = products.get(position);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        textViewName.setText(product.getName());
        TextView textViewDes = convertView.findViewById(R.id.textViewDescription);
        textViewDes.setText(product.getDescription());
        return convertView;
    }
}
