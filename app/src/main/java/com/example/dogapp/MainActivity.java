package com.example.dogapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.dogapp.ui.home.HomeFragment;
import com.example.dogapp.ui.login.LoginFragment;
import com.example.dogapp.ui.pet.PetFragment;
import com.example.dogapp.ui.settings.SettingsFragment;
import com.example.dogapp.ui.shop.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra savedInstanceState để tránh thay đổi fragment khi xoay màn hình
        if (savedInstanceState == null) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                // Chưa đăng nhập, hiển thị LoginFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment())
                        .commit();
            } else {
                // Đã đăng nhập, hiển thị HomeFragment
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragmentContainer, new HomeFragment())
//                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment())
                        .commit();
            }
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_pet) {
                selectedFragment = new PetFragment();
            } else if (itemId == R.id.nav_shop) {
                selectedFragment = new ShopFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}