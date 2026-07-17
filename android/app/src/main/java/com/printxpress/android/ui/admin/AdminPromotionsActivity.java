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
import com.printxpress.android.data.model.Promotion;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPromotionsActivity extends AppCompatActivity implements AdminPromotionsAdapter.OnPromoActionListener {

    private SupabaseDataApi dataApi;
    private AdminPromotionsAdapter adapter;
    private TextView tvEmpty;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_promos);

        tvEmpty = findViewById(R.id.tvEmptyAdminPromos);
        btnAdd = findViewById(R.id.btnAddPromo);
        RecyclerView rv = findViewById(R.id.rvAdminPromos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPromotionsAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        btnAdd.setOnClickListener(v -> showPromoDialog());

        loadPromos();
    }

    private void loadPromos() {
        dataApi.getPromotions("*").enqueue(new Callback<List<Promotion>>() {
            @Override
            public void onResponse(Call<List<Promotion>> call, Response<List<Promotion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setPromos(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(AdminPromotionsActivity.this, "Failed to load promotions catalog", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Promotion>> call, Throwable t) {
                Toast.makeText(AdminPromotionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Promotion promo) {
        dataApi.deletePromotion("eq." + promo.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPromotionsActivity.this, "Promotion removed", Toast.LENGTH_SHORT).show();
                    loadPromos();
                } else {
                    Toast.makeText(AdminPromotionsActivity.this, "Failed to delete promotion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminPromotionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPromoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Promotion Code");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("Promo Title (e.g. Welcome Promo)");
        layout.addView(etTitle);

        final EditText etDesc = new EditText(this);
        etDesc.setHint("Description (e.g. 10% off printing)");
        layout.addView(etDesc);

        final EditText etCode = new EditText(this);
        etCode.setHint("Promo Code (e.g. WELCOME10)");
        layout.addView(etCode);

        final EditText etDiscount = new EditText(this);
        etDiscount.setHint("Discount Percentage (e.g. 10)");
        etDiscount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etDiscount);

        final EditText etExpiry = new EditText(this);
        etExpiry.setHint("Expiry Date (YYYY-MM-DD)");
        layout.addView(etExpiry);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String code = etCode.getText().toString().trim().toUpperCase();
            String discStr = etDiscount.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();

            if (title.isEmpty() || code.isEmpty() || discStr.isEmpty()) {
                Toast.makeText(this, "Title, Code, and Discount are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double discount;
            try {
                discount = Double.parseDouble(discStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid discount value", Toast.LENGTH_SHORT).show();
                return;
            }

            Promotion promo = new Promotion();
            promo.setTitle(title);
            promo.setDescription(desc);
            promo.setCode(code);
            promo.setDiscount(discount);
            promo.setExpiryDate(expiry.isEmpty() ? "2026-12-31" : expiry);

            dataApi.upsertPromotion("id", promo).enqueue(new Callback<List<Promotion>>() {
                @Override
                public void onResponse(Call<List<Promotion>> call, Response<List<Promotion>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminPromotionsActivity.this, "Promo code created successfully", Toast.LENGTH_SHORT).show();
                        loadPromos();
                    } else {
                        Toast.makeText(AdminPromotionsActivity.this, "Failed to create promo code", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Promotion>> call, Throwable t) {
                    Toast.makeText(AdminPromotionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
