package com.example.dogapp.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.VolleyMultipartRequest;
import com.example.dogapp.data.model.DogPost;
import com.example.dogapp.data.repository.ApiService;
import com.example.dogapp.ui.pet.PetDetailFragment;
import com.example.dogapp.ui.posts.DogPostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private ImageButton btnCapturePhoto, btnPickPhoto;
    private ProgressBar progressBar;
    private RecyclerView rvDogPosts;
    private DogPostsAdapter adapter;
    private List<DogPost> dogPostList;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> capturePhotoLauncher;
    private ActivityResultLauncher<Intent> pickPhotoLauncher;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_STORAGE_PERMISSION_CODE = 101;
    private RequestQueue requestQueue;

    private FirebaseAuth mAuth; // Instance của FirebaseAuth


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Volley RequestQueue
        requestQueue = Volley.newRequestQueue(requireContext());

        // Khởi tạo launcher để chụp ảnh
        capturePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            uploadImageToServer(imageBitmap);
                        }
                    }
                }
        );

        // Khởi tạo launcher để chọn ảnh
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("HomeFragment", "pickPhotoLauncher result: " + result.getResultCode());
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Log.d("HomeFragment", "Selected image URI: " + imageUri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            uploadImageToServer(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("HomeFragment", "Error loading image: " + e.getMessage());
                            tvGreeting.setText("Lỗi: Không thể tải ảnh");
                        }
                    } else {
                        Log.d("HomeFragment", "No image selected or action canceled");
                        tvGreeting.setText("Không có ảnh được chọn");
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        tvGreeting = view.findViewById(R.id.tvGreeting);
        btnCapturePhoto = view.findViewById(R.id.btnCapturePhoto);
        btnPickPhoto = view.findViewById(R.id.btnPickPhoto);
        progressBar = view.findViewById(R.id.progressBar);
        rvDogPosts = view.findViewById(R.id.rvDogPosts);

        // Thiết lập RecyclerView
        dogPostList = new ArrayList<>();
        adapter = new DogPostsAdapter(requireContext(), dogPostList);
        rvDogPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDogPosts.setAdapter(adapter);

        // Khởi tạo ApiService
        apiService = new ApiService(requireContext());

        // Hiển thị BottomNavigationView
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).toggleBottomNavigation(true);
        }

        // Hiển thị lời chào Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName() != null ? user.getDisplayName() : "Người dùng";
            tvGreeting.setText("Chào " + displayName);
        } else {
            tvGreeting.setText("Chào");
        }

        // Xử lý nút chụp ảnh
        btnCapturePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                capturePhotoLauncher.launch(captureIntent);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        });

        // Xử lý nút chọn ảnh
        btnPickPhoto.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhotoLauncher.launch(pickIntent);
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION_CODE);
                }
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhotoLauncher.launch(pickIntent);
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION_CODE);
                }
            }
        });

        // Lấy danh sách bài đăng
        fetchDogPosts();
    }

    private void fetchDogPosts() {


        apiService.getUserAllPosts(
                response -> {
                    try {
                        dogPostList.clear();
                        JSONArray postsArray = response.getJSONArray("data");
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

    private void uploadImageToServer(Bitmap bitmap) {
        String url = "http://192.168.1.3:8080/detect_file";

        if (bitmap == null) {
            Log.e("HomeFragment", "Bitmap is null");
            tvGreeting.setText("Lỗi: Ảnh không hợp lệ");
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();

        if (imageBytes == null || imageBytes.length == 0) {
            Log.e("HomeFragment", "Image data is empty");
            tvGreeting.setText("Lỗi: Dữ liệu ảnh trống");
            return;
        }

        Log.d("HomeFragment", "Image data size: " + imageBytes.length + " bytes");

        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    // Ẩn ProgressBar
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject jsonResponse = new JSONObject(new String(response.data));
                        Log.d("HomeFragment", "Server response: " + jsonResponse.toString());

                        String code = jsonResponse.optString("code");
                        if ("1".equals(code)) {
                            String message = jsonResponse.optString("message");
                            showAlertDialog(message);
                        } else if ("0".equals(code)) {
                            JSONObject detectedObjects = jsonResponse.optJSONObject("detected_objects");
                            if (detectedObjects != null) {
                                String id = detectedObjects.optString("_id");
                                String name = detectedObjects.optString("name");
                                String size = detectedObjects.optString("size");
                                String lifeSpan = detectedObjects.optString("life_span");
                                String temperament = detectedObjects.optString("temperament");
                                String des = detectedObjects.optString("des");
                                String takeCare = detectedObjects.optString("take_care");
                                String sick = detectedObjects.optString("sick");
                                String image = detectedObjects.optString("image");

                                PetDetailFragment petDetailFragment = PetDetailFragment.newInstance(
                                        id, name, size, lifeSpan, temperament, des, takeCare, sick, image
                                );

                                getParentFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragmentContainer, petDetailFragment)
                                        .addToBackStack(null)
                                        .commit();

                                if (requireActivity() instanceof MainActivity) {
                                    ((MainActivity) requireActivity()).toggleBottomNavigation(false);
                                }
                            } else {
                                tvGreeting.setText("Không tìm thấy thông tin chi tiết");
                            }
                        } else {
                            tvGreeting.setText("Phản hồi không hợp lệ từ server");
                        }
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing server response: " + e.getMessage());
                        tvGreeting.setText("Lỗi: Không thể xử lý phản hồi từ server");
                    }
                },
                error -> {
                    // Ẩn ProgressBar
                    progressBar.setVisibility(View.GONE);

                    String errorMessage = "Unknown error";
                    if (error.networkResponse != null) {
                        errorMessage = "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                    } else if (error.getCause() != null) {
                        errorMessage = error.getCause().toString();
                    } else if (error.getMessage() != null) {
                        errorMessage = error.getMessage();
                    }
                    Log.e("HomeFragment", "Server request failed: " + errorMessage);
                    tvGreeting.setText("Lỗi: Không thể kết nối tới server - " + errorMessage);
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart("image.jpg", imageBytes, "image/jpeg"));
                return params;
            }
        };

        requestQueue.add(multipartRequest);
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                capturePhotoLauncher.launch(captureIntent);
            } else {
                Log.d("HomeFragment", "Camera permission denied");
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhotoLauncher.launch(pickIntent);
            } else {
                Log.d("HomeFragment", "Storage permission denied");
            }
        }
    }
}