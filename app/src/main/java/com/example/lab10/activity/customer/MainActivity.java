package com.example.lab10.activity.customer;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lab10.R;
import com.example.lab10.activity.customer.fragments.CartFragment;
import com.example.lab10.activity.customer.fragments.ChatFragment;
import com.example.lab10.activity.customer.fragments.HomeFragment;
import com.example.lab10.activity.customer.fragments.LocationFragment;
import com.example.lab10.activity.customer.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_cart) {
                    selectedFragment = new CartFragment();
                }else if (itemId == R.id.navigation_location) {
                    selectedFragment = new LocationFragment();
                }else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();

                }else if (itemId == R.id.navigation_chat) {
                    selectedFragment = new ChatFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
