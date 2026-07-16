package com.printxpress.android.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.viewmodel.ProductViewModel;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private ProductViewModel productViewModel;
    private ProductAdapter adapter;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this);
        rvProducts.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProducts().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess() && response.getData() != null) {
                adapter.setProducts(response.getData());
                setEmptyState(response.getData().isEmpty(), "No products found.\nMake sure the backend is running and products are seeded.");
                if (response.getData().isEmpty()) {
                    Toast.makeText(this, "No products available", Toast.LENGTH_LONG).show();
                }
            } else {
                setEmptyState(true, "Failed to load products.\nCheck backend connection.");
                Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Failed to load products", Toast.LENGTH_LONG).show();
            }
        });

        productViewModel.loadProducts(null);
    }

    private void setEmptyState(boolean show, String message) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        tvEmptyState.setText(message);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
}
