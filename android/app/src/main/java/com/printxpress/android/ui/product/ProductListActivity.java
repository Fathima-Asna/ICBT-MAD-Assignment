package com.printxpress.android.ui.product;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this);
        rvProducts.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProducts().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess() && response.getData() != null) {
                adapter.setProducts(response.getData());
                if (response.getData().isEmpty()) {
                    Toast.makeText(this, "No products available", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Failed to load products", Toast.LENGTH_LONG).show();
            }
        });

        productViewModel.loadProducts(null);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
}
