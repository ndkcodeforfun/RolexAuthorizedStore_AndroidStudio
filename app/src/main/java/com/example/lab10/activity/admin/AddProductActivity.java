package com.example.lab10.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab10.R;
import com.example.lab10.api.APIClient;
import com.example.lab10.api.Category.CategoryRepository;
import com.example.lab10.api.Category.CategoryService;
import com.example.lab10.api.Product.ProductRepository;
import com.example.lab10.api.Product.ProductService;
import com.example.lab10.model.Category;
import com.example.lab10.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    private Button btnAdd, btnCancel;

    private EditText productName, productDes, productPrice, productQuantity;

    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAdd = findViewById(R.id.btnProductAdd);
        btnCancel = findViewById(R.id.btnProductCancel2);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        productName = findViewById(R.id.editProductName2);
        productDes = findViewById(R.id.editProductDescription2);
        productPrice = findViewById(R.id.editProductPrice2);
        productQuantity = findViewById(R.id.editProductQuantity2);

        spinner = findViewById(R.id.spinner);

        // Initialize the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Fetch categories and populate the spinner
        getCategories();



        // Handle spinner item selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category ID
                int selectedCategoryId = categoryList.get(position).getCategoryId();
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            Product pro = new Product();
                            pro.setName(productName.getText().toString());
                            pro.setDescription(productDes.getText().toString());
                            pro.setPrice(Double.parseDouble(productPrice.getText().toString()));
                            pro.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
                            pro.setCategoryId(selectedCategoryId);
                            ProductService service = APIClient.getClient().create(ProductService.class);
                            Call<Void> call = service.create(pro);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
                                        Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no item is selected if necessary
            }
        });
    }

    private void getCategories() {
        CategoryService cateService = CategoryRepository.getCategoryService();
        Call<List<Category>> call = cateService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    if (categoryList != null && !categoryList.isEmpty()) {
                        List<String> categoryNames = new ArrayList<>();
                        for (Category category : categoryList) {
                            categoryNames.add(category.getName());
                        }
                        adapter.clear();
                        adapter.addAll(categoryNames);
                        adapter.notifyDataSetChanged();

                        spinner.setSelection(0);
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed to get categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("AddProductActivity", "Error fetching categories", t);
                Toast.makeText(AddProductActivity.this, "Error fetching categories", Toast.LENGTH_SHORT).show();
            }
        });

    }


}