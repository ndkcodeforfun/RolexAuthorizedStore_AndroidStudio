package com.example.lab10.activity.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lab10.R;
import com.example.lab10.activity.auth.LoginActivity;
import com.example.lab10.adapters.CategoryAdapter;
import com.example.lab10.api.Category.CategoryRepository;
import com.example.lab10.api.Category.CategoryService;
import com.example.lab10.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private ListView listViewCategory;
    private Button buttonProduct;

    private ImageView logout;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewCategory = findViewById(R.id.listViewCategory);
        buttonProduct = findViewById(R.id.btnProduct);
        logout = findViewById(R.id.logout_image_category);
        logout.setOnClickListener(v -> signOut());
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });
        buttonProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, ProductActivity.class);
                startActivity(intent);

            }
        });

        CategoryService cateService = CategoryRepository.getCategoryService();
        Call<List<Category>> call = cateService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!response.isSuccessful()) {
                    Log.e("CategoryActivity", "Error: " + response.code());
                    Toast.makeText(CategoryActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Category> categories = response.body();
                if (categories == null || categories.isEmpty()) {
                    Log.e("CategoryActivity", "No data received");
                    Toast.makeText(CategoryActivity.this, "There's no data", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set the adapter for the ListView
                listViewCategory.setAdapter(new CategoryAdapter(categories, getApplicationContext()));

                listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Category category = (Category) parent.getItemAtPosition(position);
                        Intent intent = new Intent(CategoryActivity.this, CategoryDetailActivity.class);
                        intent.putExtra("category", category);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("CategoryActivity", "Failed to load data: " + t.getMessage(), t);
                Toast.makeText(CategoryActivity.this, "Failed to load data: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void signOut() {
        String accessToken = getIntent().getStringExtra("accessToken");
        if(accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("accessToken");
            editor.apply();
        }

        Intent intent = new Intent(CategoryActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}