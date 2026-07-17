package com.printxpress.android.ui.resources;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.ResourceItem;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourcesActivity extends AppCompatActivity {

    private SupabaseDataApi dataApi;
    private ResourcesAdapter adapter;
    private TextView tvEmpty;
    private Button btnAll, btnFaq, btnGuides, btnSupport;
    private List<ResourceItem> allItems = new ArrayList<>();
    private String selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        tvEmpty = findViewById(R.id.tvEmptyResources);
        btnAll = findViewById(R.id.btnResAll);
        btnFaq = findViewById(R.id.btnResFaq);
        btnGuides = findViewById(R.id.btnResGuides);
        btnSupport = findViewById(R.id.btnResSupport);

        RecyclerView rv = findViewById(R.id.rvResources);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResourcesAdapter();
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        btnAll.setOnClickListener(v -> filterCategory(null, btnAll));
        btnFaq.setOnClickListener(v -> filterCategory("faq", btnFaq));
        btnGuides.setOnClickListener(v -> filterCategory("guideline", btnGuides));
        btnSupport.setOnClickListener(v -> filterCategory("support", btnSupport));

        loadResources();
    }

    private void loadResources() {
        dataApi.getResources(null, "*").enqueue(new Callback<List<ResourceItem>>() {
            @Override
            public void onResponse(Call<List<ResourceItem>> call, Response<List<ResourceItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allItems = response.body();
                    applyFilter();
                } else {
                    Toast.makeText(ResourcesActivity.this, "Failed to load support resources", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResourceItem>> call, Throwable t) {
                Toast.makeText(ResourcesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCategory(String category, Button selectedButton) {
        selectedCategory = category;

        // Reset button themes
        resetButtonTheme(btnAll);
        resetButtonTheme(btnFaq);
        resetButtonTheme(btnGuides);
        resetButtonTheme(btnSupport);

        // Highlight selected button
        selectedButton.setTextColor(getResources().getColor(R.color.black));
        selectedButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.cyan_400)));

        applyFilter();
    }

    private void resetButtonTheme(Button btn) {
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.surface_variant_dark)));
    }

    private void applyFilter() {
        if (selectedCategory == null) {
            adapter.setItems(allItems);
            tvEmpty.setVisibility(allItems.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            List<ResourceItem> filtered = new ArrayList<>();
            for (ResourceItem item : allItems) {
                if (selectedCategory.equalsIgnoreCase(item.getCategory())) {
                    filtered.add(item);
                }
            }
            adapter.setItems(filtered);
            tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
