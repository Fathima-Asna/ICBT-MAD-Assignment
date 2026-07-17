package com.printxpress.android.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.Sample;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;
import com.printxpress.android.ui.product.ProductListActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SampleGalleryActivity extends AppCompatActivity implements SampleGalleryAdapter.OnTemplateClickListener {

    private SupabaseDataApi dataApi;
    private SampleGalleryAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_gallery);

        tvEmpty = findViewById(R.id.tvEmptySamples);
        RecyclerView rv = findViewById(R.id.rvSamples);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SampleGalleryAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

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
                    Toast.makeText(SampleGalleryActivity.this, "Failed to load sample gallery", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sample>> call, Throwable t) {
                Toast.makeText(SampleGalleryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUseTemplateClick(Sample sample) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("prefill_custom_text", "Using Template: " + sample.getName());
        intent.putExtra("prefill_design_url", sample.getTemplateUrl() != null ? sample.getTemplateUrl() : "https://example.com/templates/" + sample.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
