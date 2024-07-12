package com.example.lab10.activity.auth;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.lab10.R;
import com.example.lab10.activity.admin.AdminDashboardActivity;
import com.example.lab10.activity.customer.MainActivity;
import com.example.lab10.api.CartItem.CartItemRepository;
import com.example.lab10.api.CartItem.CartItemService;
import com.example.lab10.api.auth.AuthRepository;
import com.example.lab10.api.auth.AuthService;
import com.example.lab10.model.CartItem;
import com.example.lab10.model.LoginResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_POST_NOTIFICATIONS = 1;

    // Views
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvNotAccountYet;
    private Button btnSignIn;

    // Notify
    private final String REQUIRE = "Không để trống";

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

                            // Parse the body to get the role and possibly the CustomerId
                            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                            String role = jsonObject.get("Role").getAsString();
                            int customerId = -1;
                            Intent intent = null;

                            if (role != null) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                if (role.equals("ADMIN")) {
                                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                } else if (role.equals("CUSTOMER")) {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                    if (jsonObject.has("CustomerId")) {
                                        customerId = jsonObject.get("CustomerId").getAsInt();
                                        editor.putInt("customerId", customerId);
                                        editor.apply();
                                    }
                                }

                                if (intent != null) {
                                    intent.putExtra("accessToken", accessToken);
                                    startActivity(intent);
                                    finish(); // Close LoginActivity
                                    checkAndRequestNotificationPermission(customerId);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Không tìm thấy Role ở token", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Decode token thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: No token received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 404) {
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Lỗi đăng nhập: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void fetchCartItemsAndNotify(int customerId) {
        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);

        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful()) {
                    List<CartItem> items = response.body();
                    if (items != null && !items.isEmpty()) {
                        showCartNotification(items.size());
                    }
                } else {
                    Log.e("LoginActivity", "Thất bại khi lấy sản phẩm trong giỏ hàng (onResponse)");
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("LoginActivity", "Thất bại khi lấy sản phẩm trong giỏ hàng (onFailure)", t);
            }
        });
    }

    private void checkAndRequestNotificationPermission(int customerId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            } else {
                // Permission granted, proceed to fetch cart items and notify
                fetchCartItemsAndNotify(customerId);
            }
        } else {
            // No runtime permission required below Android 13, proceed to fetch cart items and notify
            fetchCartItemsAndNotify(customerId);
        }
    }

    private void showCartNotification(int cartItemCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "cart_notification_channel";
        String channelName = "Cart Notifications";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("Giỏ hàng của bạn")
                .setContentText("Bạn có " + cartItemCount + " sản phẩm trong giỏ hàng")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(2, builder.build());
    }
}
