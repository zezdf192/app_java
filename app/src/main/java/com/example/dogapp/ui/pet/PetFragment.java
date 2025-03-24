package com.example.dogapp.ui.pet;
import com.example.dogapp.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;


import com.example.dogapp.data.repository.ApiService;
import com.example.dogapp.data.model.Dog;
import java.util.ArrayList;
import java.util.List;

public class PetFragment extends Fragment {
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<Dog> petList;
    private ApiService apiService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (petList == null) {
            petList = new ArrayList<>();
        }
        apiService = new ApiService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        petAdapter = new PetAdapter(getContext(), petList);
        recyclerView.setAdapter(petAdapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocus();
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPets(newText);
                return true;
            }
        });

        if (petList.isEmpty()) {
            apiService.getAllDogs(
                    response -> {
                        petList.addAll(response);
                        petAdapter.notifyDataSetChanged();
                    },
                    error -> {}
            );
        } else {
            petAdapter.notifyDataSetChanged();
        }

        return view;
    }

    private void filterPets(String query) {
        List<Dog> filteredList = new ArrayList<>();
        for (Dog pet : petList) {
            if (pet.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(pet);
            }
        }
        petAdapter = new PetAdapter(getContext(), filteredList);
        recyclerView.setAdapter(petAdapter);
    }
}