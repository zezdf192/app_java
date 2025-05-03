package com.example.dogapp.ui.shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.data.model.Product;
import com.example.dogapp.data.repository.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private ImageButton btnBack;
    private TextView tvTitle;
    private LinearLayout llCartProducts;
    private Button btnCheckout;
    private List<Product> cart;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Ánh xạ các view
        btnBack = view.findViewById(R.id.btnBack);
        tvTitle = view.findViewById(R.id.tvTitle);
        llCartProducts = view.findViewById(R.id.llCartProducts);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Khởi tạo ApiService và giỏ hàng
        apiService = new ApiService(requireContext());
        cart = new ArrayList<>();

        // Lấy email người dùng từ Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user != null && user.getEmail() != null ? user.getEmail() : "duy192203@gmail.com";

        // Lấy dữ liệu giỏ hàng từ server
        fetchCart(email);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).toggleBottomNavigation(true);
            }
        });

        // Xử lý nút Thanh toán
        btnCheckout.setOnClickListener(v -> {
            if (cart.isEmpty()) {
                Toast.makeText(requireContext(), "Giỏ hàng trống, không thể thanh toán", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Xử lý thanh toán (gọi API hoặc chuyển sang màn hình thanh toán)
                Toast.makeText(requireContext(), "Tiến hành thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        // Ẩn BottomNavigationView
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).toggleBottomNavigation(false);
        }

        return view;
    }

    private void fetchCart(String email) {
        apiService.getCart(email,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        if (!success) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            displayCartProducts();
                            return;
                        }

                        cart.clear();
                        JSONArray dataArray = response.getJSONArray("data");
                        if (dataArray.length() == 0) {
                            displayCartProducts();
                            return;
                        }

                        // Duyệt qua tất cả mảng con trong data
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONArray productsArray = dataArray.getJSONArray(i);
                            // Duyệt qua sản phẩm trong mỗi mảng con
                            for (int j = 0; j < productsArray.length(); j++) {
                                JSONObject productJson = productsArray.getJSONObject(j);
                                List<String> images = new ArrayList<>();
                                JSONArray imagesArray = productJson.getJSONArray("images");
                                for (int k = 0; k < imagesArray.length(); k++) {
                                    images.add(imagesArray.getString(k));
                                }
                                Product product = new Product(
                                        productJson.getString("id"),
                                        productJson.getString("name"),
                                        productJson.getString("size"),
                                        productJson.getString("color"),
                                        productJson.getString("des"),
                                        productJson.getInt("price"),
                                        images
                                );
                                cart.add(product);
                            }
                        }
                        Log.d("CartFragment", "Cart size: " + cart.size());
                        displayCartProducts();
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi xử lý dữ liệu giỏ hàng", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        displayCartProducts();
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Không thể kết nối server";
                    Toast.makeText(requireContext(), "Lỗi lấy giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    displayCartProducts();
                });
    }

    private void displayCartProducts() {
        // Xóa các view cũ
        llCartProducts.removeAllViews();

        // Nếu giỏ hàng rỗng, hiển thị thông báo
        if (cart.isEmpty()) {
            TextView tvEmpty = new TextView(requireContext());
            tvEmpty.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvEmpty.setText("Giỏ hàng trống");
            tvEmpty.setTextSize(16);
            tvEmpty.setTextColor(getResources().getColor(R.color.black));
            tvEmpty.setPadding(0, dpToPx(16), 0, dpToPx(16));
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            llCartProducts.addView(tvEmpty);
            return;
        }

        // Hiển thị từng sản phẩm
        for (int i = 0; i < cart.size(); i++) {
            Product product = cart.get(i);

            // Tạo CardView cho mỗi sản phẩm
            CardView cardView = new CardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(dpToPx(8));
            cardView.setCardElevation(dpToPx(4));
            cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            cardView.setBackgroundResource(R.drawable.card_border);

            // Tạo LinearLayout cho mỗi sản phẩm
            LinearLayout productLayout = new LinearLayout(requireContext());
            productLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            productLayout.setOrientation(LinearLayout.HORIZONTAL);
            productLayout.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

            // ImageView cho ảnh sản phẩm
            ImageView ivProductImage = new ImageView(requireContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(60));
            imageParams.setMargins(0, 0, dpToPx(12), 0);
            ivProductImage.setLayoutParams(imageParams);
            ivProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            List<String> images = product.getImages();
            if (images != null && !images.isEmpty()) {
                Picasso.get().load(images.get(0)).into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_profile_placeholder);
            }
            productLayout.addView(ivProductImage);

            // LinearLayout cho thông tin sản phẩm
            LinearLayout infoLayout = new LinearLayout(requireContext());
            infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            infoLayout.setOrientation(LinearLayout.VERTICAL);

            // TextView cho tên sản phẩm
            TextView tvProductName = new TextView(requireContext());
            tvProductName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvProductName.setText(product.getName());
            tvProductName.setTextSize(16);
            tvProductName.setTextColor(getResources().getColor(R.color.black));
            tvProductName.setPadding(0, 0, 0, dpToPx(4));
            infoLayout.addView(tvProductName);

            // TextView cho giá sản phẩm
            TextView tvProductPrice = new TextView(requireContext());
            tvProductPrice.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvProductPrice.setText(String.format("%,d VNĐ", product.getPrice()));
            tvProductPrice.setTextSize(14);
            tvProductPrice.setTextColor(getResources().getColor(R.color.grey));
            infoLayout.addView(tvProductPrice);

            productLayout.addView(infoLayout);

            // ImageButton cho nút Delete
            ImageButton btnDelete = new ImageButton(requireContext());
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
            deleteParams.setMargins(dpToPx(8), 0, 0, 0);
            deleteParams.gravity = android.view.Gravity.CENTER_VERTICAL;
            btnDelete.setLayoutParams(deleteParams);
            btnDelete.setImageResource(R.drawable.ic_delete);
            btnDelete.setBackgroundResource(android.R.drawable.btn_default);
            btnDelete.setContentDescription("Xóa sản phẩm");
            btnDelete.setScaleType(ImageView.ScaleType.FIT_CENTER);
            btnDelete.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            final int index = i;
            btnDelete.setOnClickListener(v -> deleteProductFromCart(product, index));
            productLayout.addView(btnDelete);

            // Thêm productLayout vào CardView
            cardView.addView(productLayout);

            // Thêm CardView vào llCartProducts
            llCartProducts.addView(cardView);
        }
    }

    private void deleteProductFromCart(Product product, int index) {
        try {
            // Lấy email người dùng
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user != null && user.getEmail() != null ? user.getEmail() : "duy192203@gmail.com";

            // Tạo JSON cho product
            JSONObject productJson = new JSONObject();
            productJson.put("id", product.getId());
            productJson.put("name", product.getName());
            productJson.put("size", product.getSize());
            productJson.put("color", product.getColor());
            productJson.put("des", product.getDes());
            productJson.put("price", product.getPrice());
            JSONArray imagesArray = new JSONArray(product.getImages());
            productJson.put("images", imagesArray);

            // Tạo request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("user", email);
            requestBody.put("product", productJson);
            Log.d("CartFragment", "Sending removeCart request: " + requestBody.toString());

            apiService.removeCart(requestBody,
                    response -> {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            if (success) {
                                cart.remove(index);
                                displayCartProducts();
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi xử lý phản hồi server", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Không thể kết nối server";
                        Toast.makeText(requireContext(), "Lỗi xóa sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi tạo dữ liệu gửi server", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}