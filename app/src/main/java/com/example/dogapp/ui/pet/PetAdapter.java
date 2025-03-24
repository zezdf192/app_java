package com.example.dogapp.ui.pet;

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
import com.example.dogapp.data.model.Dog;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private Context context;
    private List<Dog> petList;

    public PetAdapter(Context context, List<Dog> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Dog pet = petList.get(position);
        holder.tvPetName.setText(pet.getName());
        Picasso.get().load(pet.getImage()).into(holder.ivPetImage);

        // Sự kiện nhấn để chuyển sang trang chi tiết
        holder.itemView.setOnClickListener(v -> {
            PetDetailFragment detailFragment = PetDetailFragment.newInstance(pet.getId(), pet.getName(), pet.getSize(), pet.getLifeSpan(), pet.getTemperament(),
                    pet.getDes(), pet.getTakeCare(), pet.getSick(), pet.getImage());
            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null) // Để quay lại trang Pet
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPetImage;
        TextView tvPetName;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPetImage = itemView.findViewById(R.id.ivPetImage);
            tvPetName = itemView.findViewById(R.id.tvPetName);
        }
    }
}