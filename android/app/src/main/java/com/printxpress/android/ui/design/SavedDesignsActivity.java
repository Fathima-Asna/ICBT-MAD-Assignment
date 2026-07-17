package com.printxpress.android.ui.design;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.SavedDesign;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;
import com.printxpress.android.ui.product.ProductListActivity;
import com.printxpress.android.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedDesignsActivity extends AppCompatActivity implements SavedDesignsAdapter.OnDesignClickListener {

    private SupabaseDataApi dataApi;
    private SavedDesignsAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_designs);

        tvEmpty = findViewById(R.id.tvEmptyDesigns);
        RecyclerView rv = findViewById(R.id.rvSavedDesigns);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavedDesignsAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        loadDesigns();
    }

    private void loadDesigns() {
        AuthUser user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dataApi.getSavedDesigns("eq." + user.getUid(), "*").enqueue(new Callback<List<SavedDesign>>() {
            @Override
            public void onResponse(Call<List<SavedDesign>> call, Response<List<SavedDesign>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setDesigns(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(SavedDesignsActivity.this, "Failed to load saved designs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SavedDesign>> call, Throwable t) {
                Toast.makeText(SavedDesignsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(SavedDesign design) {
        dataApi.deleteSavedDesign("eq." + design.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SavedDesignsActivity.this, "Design deleted!", Toast.LENGTH_SHORT).show();
                    loadDesigns();
                } else {
                    Toast.makeText(SavedDesignsActivity.this, "Failed to delete design", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SavedDesignsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUseClick(SavedDesign design) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("prefill_custom_text", design.getCustomText());
        intent.putExtra("prefill_design_url", design.getDesignUrl());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
