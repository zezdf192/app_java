package com.example.dogapp.ui.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogapp.MainActivity;
import com.example.dogapp.R;
import com.example.dogapp.data.model.Product;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private Context context;
    private List<Product> productList;

    public ShopAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        if (!product.getImages().isEmpty()) {
            Picasso.get().load(product.getImages().get(0)).into(holder.ivProductImage); // Chỉ lấy ảnh đầu tiên
        }

        holder.itemView.setOnClickListener(v -> {
            ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(
                    product.getId(), product.getName(), product.getSize(), product.getColor(),
                    product.getDes(), product.getPrice(), product.getImages());
            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
        }
    }
}