package com.printxpress.android.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.printxpress.android.R;
import com.printxpress.android.data.model.Product;
import com.printxpress.android.ui.order.OrderSummaryActivity;
import com.printxpress.android.viewmodel.ProductViewModel;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductViewModel productViewModel;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvDescription = findViewById(R.id.tvDescription);
        Button btnAddToOrder = findViewById(R.id.btnAddToOrder);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProduct().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess() && response.getData() != null) {
                currentProduct = response.getData();
                tvName.setText(currentProduct.getName());
                tvCategory.setText(currentProduct.getCategory());
                tvPrice.setText(String.format("$%.2f", currentProduct.getBasePrice()));
                tvDescription.setText(currentProduct.getSpecs());
            } else {
                Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Failed to load product", Toast.LENGTH_LONG).show();
            }
        });

        String productId = getIntent().getStringExtra("product_id");
        if (productId != null) {
            productViewModel.loadProduct(productId);
        } else {
            Toast.makeText(this, "No product selected", Toast.LENGTH_SHORT).show();
        }

        btnAddToOrder.setOnClickListener(v -> {
            if (currentProduct == null) return;
            Intent intent = new Intent(this, OrderSummaryActivity.class);
            intent.putExtra("product_id", currentProduct.getId());
            intent.putExtra("product_name", currentProduct.getName());
            intent.putExtra("product_price", currentProduct.getBasePrice());
            startActivity(intent);
        });
    }
}
