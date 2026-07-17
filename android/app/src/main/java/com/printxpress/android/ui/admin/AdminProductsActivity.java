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
import com.printxpress.android.data.model.Product;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductsActivity extends AppCompatActivity implements AdminProductsAdapter.OnProductActionListener {

    private SupabaseDataApi dataApi;
    private AdminProductsAdapter adapter;
    private TextView tvEmpty;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_products);

        tvEmpty = findViewById(R.id.tvEmptyAdminProducts);
        btnAdd = findViewById(R.id.btnAddProduct);
        RecyclerView rv = findViewById(R.id.rvAdminProducts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductsAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        btnAdd.setOnClickListener(v -> showProductDialog(null));

        loadProducts();
    }

    private void loadProducts() {
        dataApi.getProducts(null, null, "*").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setProducts(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(AdminProductsActivity.this, "Failed to load products catalog", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(AdminProductsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Product product) {
        showProductDialog(product);
    }

    @Override
    public void onDeleteClick(Product product) {
        dataApi.deleteProduct("eq." + product.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductsActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(AdminProductsActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProductDialog(Product existing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existing == null ? "Add Product" : "Edit Product");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etName = new EditText(this);
        etName.setHint("Name");
        if (existing != null) etName.setText(existing.getName());
        layout.addView(etName);

        final EditText etCategory = new EditText(this);
        etCategory.setHint("Category (e.g., Business Cards)");
        if (existing != null) etCategory.setText(existing.getCategory());
        layout.addView(etCategory);

        final EditText etPrice = new EditText(this);
        etPrice.setHint("Base Price (USD)");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (existing != null) etPrice.setText(String.valueOf(existing.getBasePrice()));
        layout.addView(etPrice);

        final EditText etSpecs = new EditText(this);
        etSpecs.setHint("Specifications / Description");
        if (existing != null) etSpecs.setText(existing.getSpecs());
        layout.addView(etSpecs);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String specs = etSpecs.getText().toString().trim();

            if (name.isEmpty() || cat.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in Name, Category, and Price", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            Product prod = existing != null ? existing : new Product();
            prod.setName(name);
            prod.setCategory(cat);
            prod.setBasePrice(price);
            prod.setSpecs(specs);
            prod.setType("print");
            prod.setColor("full-color");
            prod.setWeight("300gsm");

            dataApi.upsertProduct("id", prod).enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminProductsActivity.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    } else {
                        Toast.makeText(AdminProductsActivity.this, "Failed to save product", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    Toast.makeText(AdminProductsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
