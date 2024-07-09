package com.example.lab10.activity.customer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.adapters.CategoryRecyclerViewAdapter;
import com.example.lab10.adapters.ProductRecyclerViewAdapter;
import com.example.lab10.api.APIClient;
import com.example.lab10.api.Category.CategoryService;
import com.example.lab10.api.Product.ProductService;
import com.example.lab10.model.Category;
import com.example.lab10.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecyclerView;
    private RecyclerView productRecyclerView;
    private CategoryRecyclerViewAdapter categoryAdapter;
    private ProductRecyclerViewAdapter productAdapter;
    private SearchView searchView;
    private ImageView searchIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        categoryRecyclerView = view.findViewById(R.id.category_list);
        productRecyclerView = view.findViewById(R.id.featured_list);
        searchView = view.findViewById(R.id.search_view);
        searchIcon = view.findViewById(R.id.ic_search);

        // Set LinearLayoutManager for categoryRecyclerView with horizontal orientation
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // Set GridLayoutManager for productRecyclerView with 2 columns
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadCategories();
        loadAllProducts(); // Load all products initially

        // Set search icon click listener
        searchIcon.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                searchProducts(query);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa để tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadCategories() {
        CategoryService categoryService = APIClient.getClient().create(CategoryService.class);
        Call<List<Category>> call = categoryService.getAllCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryAdapter = new CategoryRecyclerViewAdapter(categories, getContext());
                    categoryRecyclerView.setAdapter(categoryAdapter);

                    categoryAdapter.setOnCategoryClickListener(category -> loadProductsByCategory(category.getCategoryId()));
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllProducts() {
        ProductService productService = APIClient.getClient().create(ProductService.class);
        Call<List<Product>> call = productService.getAllProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    productAdapter = new ProductRecyclerViewAdapter(products, getContext());
                    productRecyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(getContext(), "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        ProductService productService = APIClient.getClient().create(ProductService.class);
        Call<List<Product>> call = productService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    if (products.isEmpty()) {
                        // If no products in this category, load all products
                        loadAllProducts();
                    } else {
                        productAdapter = new ProductRecyclerViewAdapter(products, getContext());
                        productRecyclerView.setAdapter(productAdapter);
                    }
                } else {
                    loadAllProducts();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchProducts(String query) {
        ProductService productService = APIClient.getClient().create(ProductService.class);
        Call<List<Product>> call = productService.searchProducts(query);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    productAdapter = new ProductRecyclerViewAdapter(products, getContext());
                    productRecyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
