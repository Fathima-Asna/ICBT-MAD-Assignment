package com.printxpress.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.printxpress.android.ui.auth.LoginActivity;
import com.printxpress.android.ui.order.OrderHistoryActivity;
import com.printxpress.android.ui.product.ProductListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnBrowse = findViewById(R.id.btnBrowse);
        Button btnOrders = findViewById(R.id.btnOrders);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        btnBrowse.setOnClickListener(v -> startActivity(new Intent(this, ProductListActivity.class)));
        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
    }
}
