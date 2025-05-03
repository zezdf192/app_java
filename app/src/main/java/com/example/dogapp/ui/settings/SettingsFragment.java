package com.example.dogapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.ui.login.LoginFragment;
import com.example.dogapp.ui.settings.UserPostsFragment;
import com.example.dogapp.ui.shop.CartFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private Button btnSignOut;
    private Button btnUserPosts, btnCart, btnEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Ánh xạ các view
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnUserPosts = view.findViewById(R.id.btnUserPosts);
        btnCart = view.findViewById(R.id.btnCart);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        // Hiển thị BottomNavigationView
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).toggleBottomNavigation(true);
        }

        // Xử lý sự kiện nhấn nút Sign Out
        btnSignOut.setOnClickListener(v -> signOut());


        btnCart.setOnClickListener(v -> {
            CartFragment cartFragment = new CartFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, cartFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnEditProfile.setOnClickListener(v -> {
                ProfileEditFragment profileEditActivity = new ProfileEditFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, profileEditActivity)
                        .addToBackStack(null)
                        .commit();
        });

        // Xử lý sự kiện nhấn nút Bài đăng của bạn
        btnUserPosts.setOnClickListener(v -> {
            // Chuyển hướng sang UserPostsFragment
            UserPostsFragment userPostsFragment = new UserPostsFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, userPostsFragment)
                    .addToBackStack(null)
                    .commit();

            // Ẩn BottomNavigationView khi vào UserPostsFragment
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).toggleBottomNavigation(false);
            }
        });

        return view;
    }

    private void signOut() {
        // Đăng xuất Firebase
        FirebaseAuth.getInstance().signOut();

        // Đăng xuất Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("928946629510-kjpqb761qdgbubgh4t3qoarq266svpvu.apps.googleusercontent.com") // Thay bằng Web Client ID thực tế
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            // Chuyển về LoginFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new LoginFragment())
                    .commit();
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).toggleBottomNavigation(false);
            }
        });
    }
}