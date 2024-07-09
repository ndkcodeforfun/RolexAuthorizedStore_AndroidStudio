package com.example.lab10.activity.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab10.R;
import com.example.lab10.api.APIClient;
import com.example.lab10.api.Category.CategoryService;
import com.example.lab10.model.Category;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends AppCompatActivity {


    private Button buttonSave;

    private EditText edName, edDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbarAddCategoryAdmin);
        toolbar.setTitle("Thêm danh mục");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            AddCategoryActivity.this.finish();
        });
        edName = findViewById(R.id.editTextName);
        edDescription = findViewById(R.id.editTextDes);
        buttonSave = findViewById(R.id.btnAdd);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Category cate = new Category();
                    cate.setName(edName.getText().toString());
                    cate.setDescription(edDescription.getText().toString());
                    CategoryService cateService = APIClient.getClient().create(CategoryService.class);
                    Call<Void> call = cateService.create(cate);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
                                Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
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
}