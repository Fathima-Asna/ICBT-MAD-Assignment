package com.printxpress.android.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.printxpress.android.R;
import com.printxpress.android.ui.auth.LoginActivity;
import com.printxpress.android.viewmodel.OrderViewModel;

import android.content.Intent;

public class OrderHistoryActivity extends AppCompatActivity {

    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter();
        rvOrders.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getUserOrders().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess() && response.getData() != null) {
                adapter.setOrders(response.getData());
                tvEmpty.setVisibility(response.getData().isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Failed to load orders", Toast.LENGTH_LONG).show();
            }
        });

        orderViewModel.loadUserOrders(user.getUid());
    }
}
