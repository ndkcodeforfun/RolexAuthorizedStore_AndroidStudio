package com.example.lab10;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lab10.adapters.CategoryAdapter;
import com.example.lab10.api.APIClient;
import com.example.lab10.api.CategoryRepository;
import com.example.lab10.api.CategoryService;
import com.example.lab10.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewCategory = findViewById(R.id.listViewCategory);

        CategoryService cateService = CategoryRepository.getCategoryService();
        Call<List<Category>> call = cateService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!response.isSuccessful()) {
                    Log.e("MainActivity", "Error: " + response.code());
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Category> categories = response.body();
                if (categories == null || categories.isEmpty()) {
                    Log.e("MainActivity", "No data received");
                    Toast.makeText(MainActivity.this, "There's no data", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set the adapter for the ListView
                listViewCategory.setAdapter(new CategoryAdapter(categories, getApplicationContext()));
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("MainActivity", "Failed to load data: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Failed to load data: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}