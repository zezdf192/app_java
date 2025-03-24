package com.example.dogapp.ui.pet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.dogapp.R;
import com.squareup.picasso.Picasso;

public class PetDetailFragment extends Fragment {
    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_SIZE = "size";
    private static final String ARG_LIFE_SPAN = "life_span";
    private static final String ARG_TEMPERAMENT = "temperament";
    private static final String ARG_DES = "des";
    private static final String ARG_TAKE_CARE = "take_care";
    private static final String ARG_SICK = "sick";
    private static final String ARG_IMAGE = "image";

    public static PetDetailFragment newInstance(String id, String name, String size, String lifeSpan,
                                                String temperament, String des, String takeCare,
                                                String sick, String image) {
        PetDetailFragment fragment = new PetDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_SIZE, size);
        args.putString(ARG_LIFE_SPAN, lifeSpan);
        args.putString(ARG_TEMPERAMENT, temperament);
        args.putString(ARG_DES, des);
        args.putString(ARG_TAKE_CARE, takeCare);
        args.putString(ARG_SICK, sick);
        args.putString(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_detail, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageView ivImage = view.findViewById(R.id.ivDetailImage);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvSize = view.findViewById(R.id.tvDetailSize);
        TextView tvLifeSpan = view.findViewById(R.id.tvDetailLifeSpan);
        TextView tvTemperament = view.findViewById(R.id.tvDetailTemperament);
        TextView tvDes = view.findViewById(R.id.tvDetailDes);
        TextView tvTakeCare = view.findViewById(R.id.tvDetailTakeCare);
        TextView tvSick = view.findViewById(R.id.tvDetailSick);

        Bundle args = getArguments();
        if (args != null) {
            Picasso.get().load(args.getString(ARG_IMAGE)).into(ivImage);
            tvName.setText(args.getString(ARG_NAME));
            tvSize.setText("Kích thước: " + args.getString(ARG_SIZE));
            tvLifeSpan.setText("Tuổi thọ: " + args.getString(ARG_LIFE_SPAN) + " năm");
            tvTemperament.setText("Tính cách: " + args.getString(ARG_TEMPERAMENT));
            tvDes.setText("Mô tả: " + args.getString(ARG_DES));
            tvTakeCare.setText("Chăm sóc: " + args.getString(ARG_TAKE_CARE));
            tvSick.setText("Bệnh thường gặp: " + args.getString(ARG_SICK));
        }

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại Fragment trước đó (PetFragment)
        });

        return view;
    }
}