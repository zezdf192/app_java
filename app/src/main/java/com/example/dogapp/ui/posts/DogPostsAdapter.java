package com.example.dogapp.ui.posts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogapp.R;
import com.example.dogapp.data.model.DogPost;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DogPostsAdapter extends RecyclerView.Adapter<DogPostsAdapter.DogPostViewHolder> {
    private Context context;
    private List<DogPost> dogPostList;

    public DogPostsAdapter(Context context, List<DogPost> dogPostList) {
        this.context = context;
        this.dogPostList = dogPostList;
    }

    @NonNull
    @Override
    public DogPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dog_post, parent, false);
        return new DogPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogPostViewHolder holder, int position) {
        DogPost dogPost = dogPostList.get(position);
        holder.tvDogName.setText(dogPost.getName());

        // Tải ảnh từ URL bằng AsyncTask
        new LoadImageTask(holder.ivDogImage).execute(dogPost.getImageUrl());

        // Xử lý sự kiện nhấp vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            // Truyền dữ liệu DogPost qua Intent
            intent.putExtra("name", dogPost.getName());
            intent.putExtra("line", dogPost.getLine());
            intent.putExtra("character", dogPost.getCharacter());
            intent.putExtra("health", dogPost.getHealth());
            intent.putExtra("medical", dogPost.getMedical());
            intent.putExtra("fullVaccin", dogPost.isFullVaccin());
            intent.putExtra("vaccin", dogPost.getVaccin());
            intent.putExtra("sterilization", dogPost.isSterilization());
            intent.putExtra("takeCare", dogPost.getTakeCare());
            intent.putExtra("specialTakeCare", dogPost.getSpecialTakeCare());
            intent.putExtra("habit", dogPost.getHabit());
            intent.putExtra("liveTogether", dogPost.isLiveTogether());
            intent.putExtra("train", dogPost.isTrain());
            intent.putExtra("phoneNumber", dogPost.getPhoneNumber());
            intent.putExtra("address", dogPost.getAddress());
            intent.putExtra("user", dogPost.getUser());
            intent.putExtra("imageUrl", dogPost.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dogPostList.size();
    }

    static class DogPostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDogImage;
        TextView tvDogName;

        public DogPostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDogImage = itemView.findViewById(R.id.ivDogImage);
            tvDogName = itemView.findViewById(R.id.tvDogName);
        }
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