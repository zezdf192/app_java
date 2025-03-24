package com.example.dogapp.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.dogapp.R;
import com.example.dogapp.data.repository.ApiService;
import com.example.dogapp.data.model.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShopFragment extends Fragment {
    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private ApiService apiService;
    private Spinner spinnerFilter;
    private Spinner spinnerSort; // Khai báo instance variable

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (productList == null) {
            productList = new ArrayList<>();
            filteredList = new ArrayList<>();
        }
        apiService = new ApiService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        shopAdapter = new ShopAdapter(getContext(), filteredList);
        recyclerView.setAdapter(shopAdapter);

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
                filterAndSortProducts(newText, getFilterSelection(), getSortSelection());
                return true;
            }
        });

        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortProducts(searchView.getQuery().toString(), position, getSortSelection());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSort = view.findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortProducts(searchView.getQuery().toString(), getFilterSelection(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (productList.isEmpty()) {
            apiService.getAllProducts(
                    response -> {
                        productList.addAll(response);
                        filteredList.addAll(response);
                        shopAdapter.notifyDataSetChanged();
                    },
                    error -> {}
            );
        } else {
            filteredList.clear();
            filteredList.addAll(productList);
            shopAdapter.notifyDataSetChanged();
        }

        return view;
    }

    private void filterAndSortProducts(String query, int filterPosition, int sortPosition) {
        filteredList.clear();
        for (Product product : productList) {
            boolean matchesQuery = product.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesFilter = true;
            switch (filterPosition) {
                case 0: // Tất cả
                    break;
                case 1: // Giá dưới 60,000
                    matchesFilter = product.getPrice() < 60000;
                    break;
                case 2: // Giá từ 60,000 đến 100,000
                    matchesFilter = product.getPrice() >= 60000 && product.getPrice() <= 100000;
                    break;
                case 3: // Giá trên 100,000
                    matchesFilter = product.getPrice() > 100000;
                    break;
            }
            if (matchesQuery && matchesFilter) {
                filteredList.add(product);
            }
        }

        // Sắp xếp theo giá
        switch (sortPosition) {
            case 1: // Giá tăng dần
                Collections.sort(filteredList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p1.getPrice(), p2.getPrice());
                    }
                });
                break;
            case 2: // Giá giảm dần
                Collections.sort(filteredList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p2.getPrice(), p1.getPrice());
                    }
                });
                break;
        }

        shopAdapter.notifyDataSetChanged();
    }

    private int getFilterSelection() {
        return spinnerFilter != null ? spinnerFilter.getSelectedItemPosition() : 0;
    }

    private int getSortSelection() {
        return spinnerSort != null ? spinnerSort.getSelectedItemPosition() : 0;
    }
}