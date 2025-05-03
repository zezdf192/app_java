package com.example.dogapp.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.dogapp.R;
import com.example.dogapp.data.repository.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditFragment extends Fragment {

    private ImageView ivProfileImage;
    private EditText etName, etAddress, etPhoneNumber;
    private TextView tvEmail;
    private Button btnChangeImage, btnSaveProfile;
    private Uri selectedImageUri;
    private FirebaseUser user;
    private ApiService apiService;
    private boolean isCloudinaryInitialized = false;

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    public static ProfileEditFragment newInstance() {
        return new ProfileEditFragment();
    }

    // Launcher để chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Picasso.get().load(selectedImageUri).into(ivProfileImage);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Cloudinary
        if (!isCloudinaryInitialized) {
            try {
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", getString(R.string.cloudinary_cloud_name));
                config.put("api_key", getString(R.string.cloudinary_api_key));
                config.put("api_secret", getString(R.string.cloudinary_api_secret));
                MediaManager.init(requireContext(), config);
                isCloudinaryInitialized = true;
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Lỗi khởi tạo Cloudinary!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        // Ánh xạ các view
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        // Khởi tạo ApiService
        apiService = new ApiService(requireContext());

        // Lấy thông tin người dùng từ Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            // Gọi API để lấy hồ sơ
            Map<String, Object> data = new HashMap<>();
            data.put("email", user.getEmail());
            apiService.getProfileByEmail(data,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONObject profileData = response.getJSONObject("data");
                                etName.setText(profileData.optString("name", ""));
                                etAddress.setText(profileData.optString("address", ""));
                                etPhoneNumber.setText(profileData.optString("phoneNumber", ""));
                                String photoURL = profileData.optString("photoURL", "");
                                if (!photoURL.isEmpty()) {
                                    Picasso.get().load(photoURL).into(ivProfileImage);
                                } else {
                                    ivProfileImage.setImageResource(R.drawable.placeholder);
                                }
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), "Lỗi lấy hồ sơ: " + message, Toast.LENGTH_SHORT).show();
                                // Hiển thị dữ liệu từ Firebase nếu API thất bại
                                etName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
                                if (user.getPhotoUrl() != null) {
                                    Picasso.get().load(user.getPhotoUrl()).into(ivProfileImage);
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Lỗi xử lý dữ liệu hồ sơ!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            // Hiển thị dữ liệu từ Firebase nếu lỗi
                            etName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
                            if (user.getPhotoUrl() != null) {
                                Picasso.get().load(user.getPhotoUrl()).into(ivProfileImage);
                            }
                        }
                    },
                    error -> {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Không thể kết nối server";
                        Toast.makeText(getContext(), "Lỗi lấy hồ sơ: " + errorMessage, Toast.LENGTH_SHORT).show();
                        // Hiển thị dữ liệu từ Firebase nếu lỗi
                        etName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
                        if (user.getPhotoUrl() != null) {
                            Picasso.get().load(user.getPhotoUrl()).into(ivProfileImage);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
        }

        // Chọn ảnh đại diện
        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        // Lưu thay đổi
        btnSaveProfile.setOnClickListener(v -> {
            String updatedName = etName.getText().toString().trim();
            String updatedAddress = etAddress.getText().toString().trim();
            String updatedPhoneNumber = etPhoneNumber.getText().toString().trim();
            String email = tvEmail.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu có ảnh mới, tải lên Cloudinary
            if (selectedImageUri != null) {
                uploadImageToCloudinary(updatedName, updatedAddress, updatedPhoneNumber, email);
            } else {
                // Không có ảnh mới, dùng photoUrl hiện tại
                String photoUrl = user != null && user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                updateProfile(updatedName, updatedAddress, updatedPhoneNumber, email, photoUrl);
            }
        });

        return view;
    }

    private void uploadImageToCloudinary(String updatedName, String updatedAddress, String updatedPhoneNumber, String email) {
        if (!isCloudinaryInitialized) {
            Toast.makeText(getContext(), "Cloudinary chưa được khởi tạo!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tải ảnh lên Cloudinary
        MediaManager.get().upload(selectedImageUri)
                .option("public_id", "profile_images/" + (user != null ? user.getUid() : "unknown"))
                .unsigned("your_unsigned_preset") // Thay bằng preset của bạn
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // Có thể thêm ProgressBar
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Cập nhật tiến trình nếu cần
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        updateProfile(updatedName, updatedAddress, updatedPhoneNumber, email, imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Lỗi tải ảnh lên: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Xử lý retry nếu cần
                    }
                })
                .dispatch();
    }

    private void updateProfile(String name, String address, String phoneNumber, String email, String photoUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("address", address);
        data.put("phoneNumber", phoneNumber);
        data.put("email", email);
        data.put("photoURL", photoUrl);

        apiService.updateProfile(data,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (success) {
                            Toast.makeText(getContext(), "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                            // Quay lại SettingsFragment
                            getParentFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi xử lý phản hồi server!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Không thể kết nối server";
                    Toast.makeText(getContext(), "Lỗi cập nhật hồ sơ: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
    }
}