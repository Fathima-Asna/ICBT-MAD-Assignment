package com.printxpress.android.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.printxpress.android.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnOrders = findViewById(R.id.btnManageOrders);
        Button btnProducts = findViewById(R.id.btnManageProducts);
        Button btnSamples = findViewById(R.id.btnManageSamples);
        Button btnPromos = findViewById(R.id.btnManagePromos);
        Button btnFAQs = findViewById(R.id.btnManageFAQs);

        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, AdminOrdersActivity.class)));
        btnProducts.setOnClickListener(v -> startActivity(new Intent(this, AdminProductsActivity.class)));
        btnSamples.setOnClickListener(v -> startActivity(new Intent(this, AdminSamplesActivity.class)));
        btnPromos.setOnClickListener(v -> startActivity(new Intent(this, AdminPromotionsActivity.class)));
        btnFAQs.setOnClickListener(v -> startActivity(new Intent(this, AdminFAQsActivity.class)));
    }
}
