package com.example.dogapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.ui.home.HomeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import org.json.JSONObject;

public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogleSignIn;
    private TextView tvError;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private RequestQueue requestQueue;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getContext());

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("928946629510-kjpqb761qdgbubgh4t3qoarq266svpvu.apps.googleusercontent.com") // Thay bằng Web Client ID thực tế
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Khởi tạo ActivityResultLauncher
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            tvError.setText("Đăng nhập Google thất bại: " + e.getMessage());
                            tvError.setVisibility(View.VISIBLE);
                            Log.e("LoginFragment", "Google Sign-In failed: " + e.getStatusCode() + " - " + e.getMessage());
                        }
                    } else {
                        tvError.setText("Bạn đã hủy đăng nhập Google. Vui lòng thử lại.");
                        tvError.setVisibility(View.VISIBLE);
                        Log.e("LoginFragment", "Google Sign-In canceled, resultCode: " + result.getResultCode());
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnGoogleSignIn = view.findViewById(R.id.btnGoogleSignIn);

        // Ẩn BottomNavigationView khi ở LoginFragment
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).toggleBottomNavigation(false);
        }

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        String name = account.getDisplayName();
                        String email = account.getEmail();
                        String photoURL = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
                        registerUserWithApi(uid, name, email, photoURL);
                    } else {
                        tvError.setText("Xác thực Firebase thất bại");
                        tvError.setVisibility(View.VISIBLE);
                        Log.e("LoginFragment", "Firebase Auth failed: " + task.getException());
                    }
                });
    }

    private void registerUserWithApi(String uid, String name, String email, String photoURL) {
        String url = "http://192.168.1.3:5000/api/user/register";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("uid", uid);
            requestBody.put("name", name);
            requestBody.put("email", email);
            requestBody.put("photoURL", photoURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        if (success) {
                            // Chuyển hướng tới HomeFragment
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, new HomeFragment())
                                    .commit();
                            // Hiển thị BottomNavigationView sau khi đăng nhập thành công
                            if (requireActivity() instanceof MainActivity) {
                                ((MainActivity) requireActivity()).toggleBottomNavigation(true);
                            }
                        } else {
                            tvError.setText(message);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        tvError.setText("Lỗi phân tích phản hồi: " + e.getMessage());
                        tvError.setVisibility(View.VISIBLE);
                    }
                }, error -> {
            tvError.setText("Lỗi kết nối API: " + error.getMessage());
            tvError.setVisibility(View.VISIBLE);
            Log.e("LoginFragment", "API error: " + error.getMessage());
        });
        requestQueue.add(jsonObjectRequest);
    }
}