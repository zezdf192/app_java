package com.example.dogapp.ui.posts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.data.repository.ApiService;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CreatePostFragment extends Fragment {

    private EditText etName, etLine, etCharacter, etMedical, etVaccinationDetails, etTakeCare, etSpecialTakeCare, etHabit, etPhoneNumber, etAddress;
    private RadioGroup rgHealth, rgVaccination, rgSterilization, rgLiveTogether, rgTraining;
    private TextView tvSelectedHealth, tvLiveTogetherStatus, tvTrainingStatus;
    private ImageView ivDogImage;
    private Button btnUploadImage, btnSubmitPost;
    private Uri selectedImageUri;
    private String imageUrl;
    private ApiService apiService;
    private FirebaseAuth mAuth;

    private boolean isCloudinaryInitialized = false;

    // Launcher để chọn ảnh từ thiết bị và tự động upload
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivDogImage.setImageURI(selectedImageUri); // Hiển thị ảnh đã chọn lên ImageView
                    // Tự động upload ảnh lên Cloudinary ngay sau khi chọn
                    uploadImageToCloudinary(selectedImageUri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        // Định nghĩa các biến cục bộ cho Cloudinary

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

        // Khởi tạo ApiService
        apiService = new ApiService(getContext());

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các view
        etName = view.findViewById(R.id.etName);
        etLine = view.findViewById(R.id.etLine);
        etCharacter = view.findViewById(R.id.etCharacter);
        etMedical = view.findViewById(R.id.etMedical);
        etVaccinationDetails = view.findViewById(R.id.etVaccinationDetails);
        etTakeCare = view.findViewById(R.id.etTakeCare);
        etSpecialTakeCare = view.findViewById(R.id.etSpecialTakeCare);
        etHabit = view.findViewById(R.id.etHabit);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etAddress = view.findViewById(R.id.etAddress);

        rgHealth = view.findViewById(R.id.rgHealth);
        rgVaccination = view.findViewById(R.id.rgVaccination);
        rgSterilization = view.findViewById(R.id.rgSterilization);
        rgLiveTogether = view.findViewById(R.id.rgLiveTogether);
        rgTraining = view.findViewById(R.id.rgTraining);

        tvSelectedHealth = view.findViewById(R.id.tvSelectedHealth);
        tvLiveTogetherStatus = view.findViewById(R.id.tvLiveTogetherStatus);
        tvTrainingStatus = view.findViewById(R.id.tvTrainingStatus);

        ivDogImage = view.findViewById(R.id.ivDogImage);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnSubmitPost = view.findViewById(R.id.btnSubmitPost);

        // Hiển thị BottomNavigationView ẩn đi
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).toggleBottomNavigation(false);
        }

        // Xử lý sự kiện chọn tình trạng sức khỏe
        rgHealth.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedHealth = "";
            if (checkedId == R.id.rbHealth1) {
                selectedHealth = "Loại 1 (Sức khỏe tốt và không bệnh tật)";
            } else if (checkedId == R.id.rbHealth2) {
                selectedHealth = "Loại 2 (Mức độ sức khỏe tốt nhưng có một số hạn chế nhỏ)";
            } else if (checkedId == R.id.rbHealth3) {
                selectedHealth = "Loại 3 (Mức độ sức khỏe trung bình, có một số hạn chế nhất định)";
            } else if (checkedId == R.id.rbHealth4) {
                selectedHealth = "Loại 4 (Mức độ sức khỏe dưới trung bình, có nhiều hạn chế)";
            } else if (checkedId == R.id.rbHealth5) {
                selectedHealth = "Loại 5 (Mức độ sức khỏe kém)";
            }
            tvSelectedHealth.setText("Tình trạng đã chọn: " + selectedHealth);
        });

        // Xử lý sự kiện chọn sống cùng trẻ em hoặc vật nuôi
        rgLiveTogether.setOnCheckedChangeListener((group, checkedId) -> {
            String liveTogether = "";
            if (checkedId == R.id.rbLiveTogetherYes) {
                liveTogether = "Có thói quen sống chung với trẻ em hoặc vật nuôi";
            } else if (checkedId == R.id.rbLiveTogetherNo) {
                liveTogether = "Không thói quen sống chung với trẻ em hoặc vật nuôi";
            }
            tvLiveTogetherStatus.setText("Trạng thái sống cùng trẻ em hoặc vật nuôi: " + liveTogether);
        });

        // Xử lý sự kiện chọn trạng thái huấn luyện
        rgTraining.setOnCheckedChangeListener((group, checkedId) -> {
            String trainingStatus = "";
            if (checkedId == R.id.rbTrainingYes) {
                trainingStatus = "Có thói quen được huấn luyện cơ bản (ngồi, đứng, nghe lệnh)";
            } else if (checkedId == R.id.rbTrainingNo) {
                trainingStatus = "Không có thói quen được huấn luyện cơ bản (ngồi, đứng, nghe lệnh)";
            }
            tvTrainingStatus.setText("Trạng thái huấn luyện: " + trainingStatus);
        });

        // Xử lý sự kiện chọn ảnh
        btnUploadImage.setOnClickListener(v -> {
            // Mở trình chọn ảnh từ thiết bị
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Xử lý sự kiện đăng bài
        btnSubmitPost.setOnClickListener(v -> {
            // Kiểm tra xem đã có imageUrl chưa (tức là ảnh đã được upload thành công)
            if (imageUrl == null || imageUrl.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chờ ảnh tải lên hoặc chọn lại ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tiến hành thu thập dữ liệu và gửi lên server
            submitPost();
        });

        return view;
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        // Định nghĩa biến cục bộ cho upload preset
        final String UPLOAD_PRESET = "cr2sv2gr";

        // Upload ảnh lên Cloudinary
        MediaManager.get().upload(imageUri)
                .unsigned(UPLOAD_PRESET)
                .option("resource_type", "image")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(getContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Không cần log tiến trình
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Lấy URL của ảnh từ kết quả
                        imageUrl = resultData.get("secure_url").toString();
                        Toast.makeText(getContext(), "Tải ảnh thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Lỗi tải ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        imageUrl = null; // Reset imageUrl nếu upload thất bại
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Không cần xử lý reschedule
                    }
                })
                .dispatch();
    }

    private void submitPost() {
        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để đăng bài!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy UID từ Firebase
        String userUid = currentUser.getEmail();

        // Thu thập dữ liệu từ các trường
        String name = etName.getText().toString().trim();
        String line = etLine.getText().toString().trim();
        String character = etCharacter.getText().toString().trim();
        String medical = etMedical.getText().toString().trim();
        String vaccinationDetails = etVaccinationDetails.getText().toString().trim();
        String takeCare = etTakeCare.getText().toString().trim();
        String specialTakeCare = etSpecialTakeCare.getText().toString().trim();
        String habit = etHabit.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Lấy dữ liệu từ RadioGroup
        String selectedHealth = tvSelectedHealth.getText().toString().replace("Tình trạng đã chọn: ", "");
        boolean fullVaccin = rgVaccination.getCheckedRadioButtonId() == R.id.rbVaccinatedYes;
        String vaccin = fullVaccin ? (vaccinationDetails.isEmpty() ? "" : vaccinationDetails) : "";
        boolean sterilization = rgSterilization.getCheckedRadioButtonId() == R.id.rbSterilizedYes;
        boolean liveTogether = rgLiveTogether.getCheckedRadioButtonId() == R.id.rbLiveTogetherYes;
        boolean train = rgTraining.getCheckedRadioButtonId() == R.id.rbTrainingYes;

        // Kiểm tra các trường bắt buộc
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên chó!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập địa chỉ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra và thay thế giá trị null
        name = name.isEmpty() ? "" : name;
        line = line.isEmpty() ? "" : line;
        character = character.isEmpty() ? "" : character;
        medical = medical.isEmpty() ? "" : medical;
        takeCare = takeCare.isEmpty() ? "" : takeCare;
        specialTakeCare = specialTakeCare.isEmpty() ? "" : specialTakeCare;
        habit = habit.isEmpty() ? "" : habit;
        phoneNumber = phoneNumber.isEmpty() ? "" : phoneNumber;
        address = address.isEmpty() ? "" : address;
        selectedHealth = selectedHealth.isEmpty() ? "" : selectedHealth;
        imageUrl = imageUrl != null ? imageUrl : ""; // Đảm bảo imageUrl không null

        try {
            // Tạo đối tượng dataDog
            Map<String, Object> dataDog = new HashMap<>();
            dataDog.put("name", name);
            dataDog.put("line", line);
            dataDog.put("character", character);
            dataDog.put("health", selectedHealth);
            dataDog.put("medical", medical);
            dataDog.put("fullVaccin", fullVaccin);
            dataDog.put("vaccin", vaccin);
            dataDog.put("sterilization", sterilization);
            dataDog.put("takeCare", takeCare);
            dataDog.put("specialTakeCare", specialTakeCare);
            dataDog.put("habit", habit);
            dataDog.put("liveTogether", liveTogether);
            dataDog.put("train", train);
            dataDog.put("imageUrl", imageUrl);

            // Tạo đối tượng data
            Map<String, Object> data = new HashMap<>();
            data.put("post", dataDog);
            data.put("user", userUid);
            data.put("phoneNumber", phoneNumber);
            data.put("address", address);

            // Gửi dữ liệu lên server
            sendDataToServer(data, name, line, character, selectedHealth, medical, fullVaccin, vaccin, sterilization, takeCare, specialTakeCare, habit, liveTogether, train, imageUrl, userUid, phoneNumber, address);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khi chuẩn bị dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDataToServer(Map<String, Object> data, String name, String line, String character, String health, String medical, boolean fullVaccin, String vaccin, boolean sterilization, String takeCare, String specialTakeCare, String habit, boolean liveTogether, boolean train, String imageUrl, String userUid, String phoneNumber, String address) {
        // Gọi hàm createPost từ ApiService
        apiService.createPost(data,
                response -> {
                    // Xử lý phản hồi thành công từ server
                    Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển hướng đến PostDetailActivity và truyền dữ liệu bài đăng
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("line", line);
                    intent.putExtra("character", character);
                    intent.putExtra("health", health);
                    intent.putExtra("medical", medical);
                    intent.putExtra("fullVaccin", fullVaccin);
                    intent.putExtra("vaccin", vaccin);
                    intent.putExtra("sterilization", sterilization);
                    intent.putExtra("takeCare", takeCare);
                    intent.putExtra("specialTakeCare", specialTakeCare);
                    intent.putExtra("habit", habit);
                    intent.putExtra("liveTogether", liveTogether);
                    intent.putExtra("train", train);
                    intent.putExtra("imageUrl", imageUrl);
                    intent.putExtra("user", userUid);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("address", address);
                    startActivity(intent);
                },
                error -> {
                    // Xử lý lỗi
                    Toast.makeText(getContext(), "Lỗi khi đăng bài: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hiển thị lại BottomNavigationView khi rời khỏi Fragment
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).toggleBottomNavigation(true);
        }
    }
}