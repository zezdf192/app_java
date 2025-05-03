package com.example.dogapp.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

public class ProductDetailFragment extends Fragment {
    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_SIZE = "size";
    private static final String ARG_COLOR = "color";
    private static final String ARG_DES = "des";
    private static final String ARG_PRICE = "price";
    private static final String ARG_IMAGES = "images";

    private static List<Product> cart = new ArrayList<>(); // Danh sách giỏ hàng tạm thời
    private ApiService apiService;

    public static ProductDetailFragment newInstance(String id, String name, String size, String color,
                                                    String des, int price, List<String> images) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_SIZE, size);
        args.putString(ARG_COLOR, color);
        args.putString(ARG_DES, des);
        args.putInt(ARG_PRICE, price);
        args.putStringArrayList(ARG_IMAGES, new ArrayList<>(images));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Ánh xạ các view
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageView ivDetailImage = view.findViewById(R.id.ivDetailImage);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        Spinner spinnerSize = view.findViewById(R.id.spinnerSize);
        Spinner spinnerColor = view.findViewById(R.id.spinnerColor);
        TextView tvDes = view.findViewById(R.id.tvDetailDes);
        TextView tvPrice = view.findViewById(R.id.tvDetailPrice);
        Button btnAddToCart = view.findViewById(R.id.btnAddToCart);

        // Khởi tạo ApiService
        apiService = new ApiService(requireContext());

        // Lấy dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            List<String> images = args.getStringArrayList(ARG_IMAGES);
            if (images != null && !images.isEmpty()) {
                Picasso.get().load(images.get(0)).into(ivDetailImage);
            }

            tvName.setText(args.getString(ARG_NAME));
            tvDes.setText(args.getString(ARG_DES));
            tvPrice.setText(String.format("%,d VNĐ", args.getInt(ARG_PRICE)));

            // Thiết lập Spinner cho kích thước
            String[] sizes = args.getString(ARG_SIZE).split(", ");
            ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, sizes);
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSize.setAdapter(sizeAdapter);

            // Thiết lập Spinner cho màu sắc
            String[] colors = args.getString(ARG_COLOR).split(", ");
            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, colors);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerColor.setAdapter(colorAdapter);
        }

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Xử lý nút Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            if (args != null) {
                String selectedSize = spinnerSize.getSelectedItem().toString();
                String selectedColor = spinnerColor.getSelectedItem().toString();
                Product productToAdd = new Product(
                        args.getString(ARG_ID),
                        args.getString(ARG_NAME),
                        selectedSize,
                        selectedColor,
                        args.getString(ARG_DES),
                        args.getInt(ARG_PRICE),
                        args.getStringArrayList(ARG_IMAGES)
                );
                cart.add(productToAdd);
                Toast.makeText(requireContext(), "Đã thêm " + args.getString(ARG_NAME) + " vào giỏ hàng", Toast.LENGTH_SHORT).show();

                // Gọi API để lưu giỏ hàng
                saveCartToServer(productToAdd);  // Pass the specific product to the API
            }
        });

        return view;
    }

    private void saveCartToServer(Product productToAdd) {
        // Lấy userId từ Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getEmail();

        // Tạo JSON cho sản phẩm
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("user", userId);

            // Tạo JSON cho sản phẩm cần thêm vào giỏ hàng
            JSONObject productJson = new JSONObject();
            productJson.put("id", productToAdd.getId());
            productJson.put("name", productToAdd.getName());
            productJson.put("size", productToAdd.getSize());
            productJson.put("color", productToAdd.getColor());
            productJson.put("des", productToAdd.getDes());
            productJson.put("price", productToAdd.getPrice());
            productJson.put("images", new JSONArray(productToAdd.getImages()));

            // Thêm sản phẩm vào mảng sản phẩm
            JSONArray productsArray = new JSONArray();
            productsArray.put(productJson);

            requestBody.put("product", productsArray);

            // Gọi API qua ApiService
            apiService.saveCart(requestBody,
                    response -> {
                        try {
                            String code = response.optString("code");
                            String message = response.optString("message", "Lưu giỏ hàng thành công");
                            if ("1".equals(code)) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                               // Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi xử lý phản hồi server", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Không thể kết nối server";
                        Toast.makeText(requireContext(), "Lỗi lưu giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
            );
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi tạo dữ liệu gửi server", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static List<Product> getCart() {
        return cart;
    }
}
