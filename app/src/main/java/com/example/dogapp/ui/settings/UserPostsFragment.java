package com.example.dogapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.data.model.DogPost;
import com.example.dogapp.data.repository.ApiService;

import com.example.dogapp.ui.posts.CreatePostFragment;
import com.example.dogapp.ui.posts.DogPostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserPostsFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvTitle;
    private Button btnCreatePost;
    private RecyclerView rvUserPosts;
    private DogPostsAdapter adapter;
    private List<DogPost> dogPostList;
    private ApiService apiService;

    private FirebaseAuth mAuth; // Instance của FirebaseAuth

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        tvTitle = view.findViewById(R.id.tvTitle);
        btnCreatePost = view.findViewById(R.id.btnCreatePost);
        rvUserPosts = view.findViewById(R.id.rvUserPosts);

        // Thiết lập RecyclerView
        dogPostList = new ArrayList<>();
        adapter = new DogPostsAdapter(getContext(), dogPostList);
        rvUserPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserPosts.setAdapter(adapter);

        // Khởi tạo ApiService
        apiService = new ApiService(requireContext());

        mAuth = FirebaseAuth.getInstance();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại SettingsFragment
            // Hiển thị lại BottomNavigationView
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).toggleBottomNavigation(true);
            }
        });

        // Xử lý nút T- Tạo bài đăng
        btnCreatePost.setOnClickListener(v -> {
            CreatePostFragment createPostFragment = new CreatePostFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, createPostFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Gọi API để lấy dữ liệu bài đăng
        fetchUserPosts();

        return view;
    }

    private void fetchUserPosts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để đăng bài!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUid = currentUser.getEmail();
        apiService.getUserPosts(userUid,
                response -> {
                    try {
                        dogPostList.clear();
                        JSONArray postsArray = response.getJSONArray("data"); // Giả sử API trả về mảng trong key "data"
                        for (int i = 0; i < postsArray.length(); i++) {
                            JSONObject postObject = postsArray.getJSONObject(i).getJSONObject("post");
                            DogPost dogPost = new DogPost(
                                    postObject.getString("name"),
                                    postObject.getString("line"),
                                    postObject.getString("character"),
                                    postObject.getString("health"),
                                    postObject.getString("medical"),
                                    postObject.getBoolean("fullVaccin"),
                                    postObject.getString("vaccin"),
                                    postObject.getBoolean("sterilization"),
                                    postObject.getString("takeCare"),
                                    postObject.getString("specialTakeCare"),
                                    postObject.getString("habit"),
                                    postObject.getBoolean("liveTogether"),
                                    postObject.getBoolean("train"),
                                    postObject.getString("imageUrl"),
                                    postObject.getString("user"),
                                    postObject.getString("phoneNumber"),
                                    postObject.getString("address")
                            );
                            dogPostList.add(dogPost);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Lỗi khi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}