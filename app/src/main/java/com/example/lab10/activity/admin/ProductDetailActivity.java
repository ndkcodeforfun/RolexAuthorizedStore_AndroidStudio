package com.example.lab10.activity.admin;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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

public class ProductDetailActivity extends AppCompatActivity {

    private Button btnEdit, btnDelete, btnCancel;

    private EditText productName, productDes, productPrice, productQuantity;

    Spinner spinner;
     private Product product;

    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnEdit = findViewById(R.id.btnProductUpdate);
        btnDelete = findViewById(R.id.btnProductDelete);
        btnCancel = findViewById(R.id.btnProductCancel);

        productName = findViewById(R.id.editProductName);
        productDes = findViewById(R.id.editProductDescription);
        productPrice = findViewById(R.id.editProductPrice);
        productQuantity = findViewById(R.id.editProductQuantity);

        spinner = findViewById(R.id.categorySpinner);

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");

        productName.setText(product.getName());
        productDes.setText(product.getDescription());
        productPrice.setText(String.valueOf(product.getPrice()));
        productQuantity.setText(String.valueOf(product.getQuantity()));

        // Initialize the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getCategories(product.getCategoryId());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryList.get(position).getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    // Get the selected category ID

                    @Override
                    public void onClick(View v) {
                        try{
                            Product pro = new Product();
                            pro.setName(productName.getText().toString());
                            pro.setDescription(productDes.getText().toString());
                            pro.setPrice(Double.parseDouble(productPrice.getText().toString()));
                            pro.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
                            pro.setCategoryId(selectedCategoryId);
                            pro.setProductId(product.getProductId());
                            ProductService proService = APIClient.getClient().create(ProductService.class);
                            Call<Void> call = proService.update(pro, pro.getProductId());
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                                        Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
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




        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProductService proService = ProductRepository.getProductService();
                            Call<Void> call = proService.setStatus(product.getCategoryId());
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.isSuccessful()) {
                                        Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                                        Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getCategories(int categoryId) {
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

                        spinner.setSelection(categoryId - 1);
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Failed to get categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("AddProductActivity", "Error fetching categories", t);
                Toast.makeText(ProductDetailActivity.this, "Error fetching categories", Toast.LENGTH_SHORT).show();
            }
        });

    }
}