package com.example.lab10.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.lab10.R;
import com.example.lab10.activity.admin.CategoryActivity;
import com.example.lab10.activity.customer.MainActivity;
import com.example.lab10.api.auth.AuthRepository;
import com.example.lab10.api.auth.AuthService;
import com.example.lab10.model.LoginResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // Views
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvNotAccountYet;
    private Button btnSignIn;



    // Notify
    private final String REQUIRE = "Require";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);




        // Reference from layout
        etUsername = findViewById(R.id.edtU);
        etPassword = findViewById(R.id.edtP);
        tvNotAccountYet = findViewById(R.id.tvAY);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Register event
        tvNotAccountYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {

        if (!checkInput()) {
            return;
        }
        AuthService authService = AuthRepository.getAuthService();
        Call<LoginResponse> call = authService.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String accessToken = loginResponse.getAccessToken();
                        // Save the accessToken using SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("accessToken", accessToken);
                        editor.apply();

                        try {
                            String[] decodedParts = JWTUtils.decoded(accessToken);
                            String body = decodedParts[1];

                            // Parse the body to get the role
                            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                            String email = jsonObject.get("Email").getAsString();

                            if (email != null) {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent;
                                if (email.equals("admin@gmail.com")) {
                                    intent = new Intent(LoginActivity.this, CategoryActivity.class);
                                } else  {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                intent.putExtra("accessToken", accessToken);
                                startActivity(intent);
                                finish(); // Close LoginActivity
                            } else {
                                Toast.makeText(LoginActivity.this, "Email not found in token", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Failed to decode token", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: No token received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Login error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Login error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean checkInput() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            etUsername.setError(REQUIRE);
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError(REQUIRE);
            return false;
        }


        return true;
    }


}