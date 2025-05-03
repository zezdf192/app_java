package com.example.dogapp;

import android.os.Bundle;
import android.view.View;
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

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Kiểm tra savedInstanceState để tránh thay đổi fragment khi xoay màn hình
        if (savedInstanceState == null) {
            if (auth.getCurrentUser() == null) {
                // Chưa đăng nhập, hiển thị LoginFragment và ẩn BottomNavigationView
                toggleBottomNavigation(false);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment())
                        .commit();
            } else {
                // Đã đăng nhập, hiển thị HomeFragment và hiện BottomNavigationView
                toggleBottomNavigation(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
            }
        }

        // Xử lý sự kiện chọn mục trong BottomNavigationView
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                // Chỉ xử lý nếu người dùng đã đăng nhập
                if (auth.getCurrentUser() != null) {
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
                        toggleBottomNavigation(true);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, selectedFragment)
                                .commit();
                    }
                    return true;
                }
                return false;
            });
        }
    }

    // Phương thức để ẩn/hiện BottomNavigationView từ các fragment
    public void toggleBottomNavigation(boolean show) {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}