package com.printxpress.android.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.ResourceItem;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFAQsActivity extends AppCompatActivity implements AdminFAQsAdapter.OnFaqActionListener {

    private SupabaseDataApi dataApi;
    private AdminFAQsAdapter adapter;
    private TextView tvEmpty;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_faqs);

        tvEmpty = findViewById(R.id.tvEmptyAdminFaqs);
        btnAdd = findViewById(R.id.btnAddFaq);
        RecyclerView rv = findViewById(R.id.rvAdminFaqs);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminFAQsAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        btnAdd.setOnClickListener(v -> showFaqDialog());

        loadResources();
    }

    private void loadResources() {
        dataApi.getResources(null, "*").enqueue(new Callback<List<ResourceItem>>() {
            @Override
            public void onResponse(Call<List<ResourceItem>> call, Response<List<ResourceItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(AdminFAQsActivity.this, "Failed to load FAQ resources", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResourceItem>> call, Throwable t) {
                Toast.makeText(AdminFAQsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(ResourceItem item) {
        dataApi.deleteResource("eq." + item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminFAQsActivity.this, "Resource deleted", Toast.LENGTH_SHORT).show();
                    loadResources();
                } else {
                    Toast.makeText(AdminFAQsActivity.this, "Failed to delete resource", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminFAQsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFaqDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Resource Article");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Article Title");
        layout.addView(etTitle);

        final EditText etContent = new EditText(this);
        etContent.setHint("Detailed Content / FAQs Answer");
        layout.addView(etContent);

        final TextView tvLabel = new TextView(this);
        tvLabel.setText("Category:");
        tvLabel.setPadding(0, 16, 0, 8);
        layout.addView(tvLabel);

        final Spinner spCategory = new Spinner(this);
        String[] categories = {"faq", "guideline", "support"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spinAdapter);
        layout.addView(spCategory);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            String cat = spCategory.getSelectedItem().toString();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and Content are required", Toast.LENGTH_SHORT).show();
                return;
            }

            ResourceItem item = new ResourceItem();
            item.setTitle(title);
            item.setContent(content);
            item.setCategory(cat);

            dataApi.upsertResource("id", item).enqueue(new Callback<List<ResourceItem>>() {
                @Override
                public void onResponse(Call<List<ResourceItem>> call, Response<List<ResourceItem>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminFAQsActivity.this, "Resource created successfully", Toast.LENGTH_SHORT).show();
                        loadResources();
                    } else {
                        Toast.makeText(AdminFAQsActivity.this, "Failed to create resource", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ResourceItem>> call, Throwable t) {
                    Toast.makeText(AdminFAQsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
