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

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private List<Category> categories;
    private Context context;

    public CategoryAdapter(List<Category> categories, Context context) {
        super(context, R.layout.category_row_layout, categories);
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.category_row_layout, parent, false);

        Category category = categories.get(position);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        textViewName.setText(category.getName());
        TextView textViewDes = convertView.findViewById(R.id.textViewDescription);
        textViewDes.setText(category.getDescription());
        return convertView;
    }
}
