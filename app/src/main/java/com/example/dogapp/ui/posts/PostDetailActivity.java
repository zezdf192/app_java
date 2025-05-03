package com.example.dogapp.ui.posts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dogapp.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView ivDogImage;
    private TextView tvName, tvLine, tvCharacter, tvHealth, tvMedical, tvFullVaccin, tvVaccin, tvSterilization;
    private TextView tvTakeCare, tvSpecialTakeCare, tvHabit, tvLiveTogether, tvTrain, tvPhoneNumber, tvAddress, tvUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết bài đăng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Ánh xạ các view
        ivDogImage = findViewById(R.id.ivDogImage);
        tvName = findViewById(R.id.tvName);
        tvLine = findViewById(R.id.tvLine);
        tvCharacter = findViewById(R.id.tvCharacter);
        tvHealth = findViewById(R.id.tvHealth);
        tvMedical = findViewById(R.id.tvMedical);
        tvFullVaccin = findViewById(R.id.tvFullVaccin);
        tvVaccin = findViewById(R.id.tvVaccin);
        tvSterilization = findViewById(R.id.tvSterilization);
        tvTakeCare = findViewById(R.id.tvTakeCare);
        tvSpecialTakeCare = findViewById(R.id.tvSpecialTakeCare);
        tvHabit = findViewById(R.id.tvHabit);
        tvLiveTogether = findViewById(R.id.tvLiveTogether);
        tvTrain = findViewById(R.id.tvTrain);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvUser = findViewById(R.id.tvUser);

        // Lấy dữ liệu từ Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Tải ảnh bằng AsyncTask
            String imageUrl = extras.getString("imageUrl", "");
            if (!imageUrl.isEmpty()) {
                new LoadImageTask(ivDogImage).execute(imageUrl);
            } else {
                ivDogImage.setImageResource(R.drawable.placeholder);
            }

            // Hiển thị thông tin, bỏ qua trường trống
            String name = extras.getString("name", "");
            if (!name.isEmpty()) {
                tvName.setText("Tên: " + name);
            } else {
                tvName.setVisibility(View.GONE);
            }

            String line = extras.getString("line", "");
            if (!line.isEmpty()) {
                tvLine.setText("Dòng chó: " + line);
            } else {
                tvLine.setVisibility(View.GONE);
            }

            String character = extras.getString("character", "");
            if (!character.isEmpty()) {
                tvCharacter.setText("Tính cách: " + character);
            } else {
                tvCharacter.setVisibility(View.GONE);
            }

            String health = extras.getString("health", "");
            if (!health.isEmpty()) {
                tvHealth.setText("Tình trạng sức khỏe: " + health);
            } else {
                tvHealth.setVisibility(View.GONE);
            }

            String medical = extras.getString("medical", "");
            if (!medical.isEmpty()) {
                tvMedical.setText("Tình trạng y tế: " + medical);
            } else {
                tvMedical.setVisibility(View.GONE);
            }

            boolean fullVaccin = extras.getBoolean("fullVaccin", false);
            if (fullVaccin) {
                tvFullVaccin.setText("Đã tiêm phòng đầy đủ: Có");
            } else {
                tvFullVaccin.setVisibility(View.GONE);
            }

            String vaccin = extras.getString("vaccin", "");
            if (!vaccin.isEmpty()) {
                tvVaccin.setText("Chi tiết tiêm phòng: " + vaccin);
            } else {
                tvVaccin.setVisibility(View.GONE);
            }

            boolean sterilization = extras.getBoolean("sterilization", false);
            if (sterilization) {
                tvSterilization.setText("Đã triệt sản: Có");
            } else {
                tvSterilization.setVisibility(View.GONE);
            }

            String takeCare = extras.getString("takeCare", "");
            if (!takeCare.isEmpty()) {
                tvTakeCare.setText("Cách chăm sóc: " + takeCare);
            } else {
                tvTakeCare.setVisibility(View.GONE);
            }

            String specialTakeCare = extras.getString("specialTakeCare", "");
            if (!specialTakeCare.isEmpty()) {
                tvSpecialTakeCare.setText("Chăm sóc đặc biệt: " + specialTakeCare);
            } else {
                tvSpecialTakeCare.setVisibility(View.GONE);
            }

            String habit = extras.getString("habit", "");
            if (!habit.isEmpty()) {
                tvHabit.setText("Thói quen: " + habit);
            } else {
                tvHabit.setVisibility(View.GONE);
            }

            boolean liveTogether = extras.getBoolean("liveTogether", false);
            if (liveTogether) {
                tvLiveTogether.setText("Sống cùng trẻ em/vật nuôi: Có");
            } else {
                tvLiveTogether.setVisibility(View.GONE);
            }

            boolean train = extras.getBoolean("train", false);
            if (train) {
                tvTrain.setText("Đã được huấn luyện: Có");
            } else {
                tvTrain.setVisibility(View.GONE);
            }

            String phoneNumber = extras.getString("phoneNumber", "");
            if (!phoneNumber.isEmpty()) {
                tvPhoneNumber.setText("Số điện thoại: " + phoneNumber);
            } else {
                tvPhoneNumber.setVisibility(View.GONE);
            }

            String address = extras.getString("address", "");
            if (!address.isEmpty()) {
                tvAddress.setText("Địa chỉ: " + address);
            } else {
                tvAddress.setVisibility(View.GONE);
            }

            String user = extras.getString("user", "");
            if (!user.isEmpty()) {
                tvUser.setText("Người đăng: " + user);
            } else {
                tvUser.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // AsyncTask để tải ảnh từ URL
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // Đặt ảnh placeholder nếu tải thất bại
                imageView.setImageResource(R.drawable.placeholder);
            }
        }
    }
}