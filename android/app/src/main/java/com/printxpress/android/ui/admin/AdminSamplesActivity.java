package com.printxpress.android.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.Sample;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSamplesActivity extends AppCompatActivity implements AdminSamplesAdapter.OnSampleActionListener {

    private SupabaseDataApi dataApi;
    private AdminSamplesAdapter adapter;
    private TextView tvEmpty;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_samples);

        tvEmpty = findViewById(R.id.tvEmptyAdminSamples);
        btnAdd = findViewById(R.id.btnAddSample);
        RecyclerView rv = findViewById(R.id.rvAdminSamples);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminSamplesAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        btnAdd.setOnClickListener(v -> showSampleDialog());

        loadSamples();
    }

    private void loadSamples() {
        dataApi.getSamples("*").enqueue(new Callback<List<Sample>>() {
            @Override
            public void onResponse(Call<List<Sample>> call, Response<List<Sample>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setSamples(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(AdminSamplesActivity.this, "Failed to load sample templates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sample>> call, Throwable t) {
                Toast.makeText(AdminSamplesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Sample sample) {
        dataApi.deleteSample("eq." + sample.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSamplesActivity.this, "Template deleted", Toast.LENGTH_SHORT).show();
                    loadSamples();
                } else {
                    Toast.makeText(AdminSamplesActivity.this, "Failed to delete template", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminSamplesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSampleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Design Template");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Template Title");
        layout.addView(etTitle);

        final EditText etCategory = new EditText(this);
        etCategory.setHint("Category (e.g. Business Cards)");
        layout.addView(etCategory);

        final EditText etBleed = new EditText(this);
        etBleed.setHint("Bleed Margins (e.g. 0.125 inches)");
        layout.addView(etBleed);

        final EditText etColor = new EditText(this);
        etColor.setHint("Color Format (e.g. CMYK)");
        layout.addView(etColor);

        final EditText etUrl = new EditText(this);
        etUrl.setHint("Template Download URL / PDF Link");
        layout.addView(etUrl);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();
            String bleed = etBleed.getText().toString().trim();
            String color = etColor.getText().toString().trim();
            String url = etUrl.getText().toString().trim();

            if (title.isEmpty() || cat.isEmpty()) {
                Toast.makeText(this, "Title and Category are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Sample sample = new Sample();
            sample.setName(title);
            sample.setCategory(cat);
            sample.setBleedMargins(bleed.isEmpty() ? "0.125 inches" : bleed);
            sample.setColorFormats(color.isEmpty() ? "CMYK" : color);
            sample.setTemplateUrl(url.isEmpty() ? "https://example.com/template" : url);

            dataApi.upsertSample("id", sample).enqueue(new Callback<List<Sample>>() {
                @Override
                public void onResponse(Call<List<Sample>> call, Response<List<Sample>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminSamplesActivity.this, "Template saved successfully", Toast.LENGTH_SHORT).show();
                        loadSamples();
                    } else {
                        Toast.makeText(AdminSamplesActivity.this, "Failed to save template", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Sample>> call, Throwable t) {
                    Toast.makeText(AdminSamplesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
