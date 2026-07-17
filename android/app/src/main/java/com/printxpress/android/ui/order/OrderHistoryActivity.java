package com.printxpress.android.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.ui.auth.LoginActivity;
import com.printxpress.android.util.SessionManager;
import com.printxpress.android.viewmodel.OrderViewModel;

import android.content.Intent;
import android.widget.EditText;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.model.PrintOrder;

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

        AuthUser user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        adapter.setOnOrderActionListener(new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onCancelOrderClick(PrintOrder order) {
                cancelOrder(order);
            }

            @Override
            public void onRescheduleOrderClick(PrintOrder order) {
                showRescheduleDialog(order);
            }
        });

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

    private void cancelOrder(PrintOrder order) {
        SupabaseClient.getDataApi().deleteOrder("eq." + order.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderHistoryActivity.this, "Order cancelled successfully!", Toast.LENGTH_SHORT).show();
                    AuthUser user = SessionManager.getInstance().getCurrentUser();
                    if (user != null) orderViewModel.loadUserOrders(user.getUid());
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRescheduleDialog(PrintOrder order) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Reschedule Pickup");
        
        final EditText input = new EditText(this);
        input.setHint("Enter new pickup date/time (e.g. Tue 2:00 PM)");
        if (order.getPickupTime() != null) input.setText(order.getPickupTime());
        builder.setView(input);

        builder.setPositiveButton("Reschedule", (dialog, which) -> {
            String newTime = input.getText().toString().trim();
            if (newTime.isEmpty()) return;

            java.util.Map<String, Object> update = new java.util.HashMap<>();
            update.put("pickupTime", newTime);

            SupabaseClient.getDataApi().updateOrderStatus("eq." + order.getId(), update).enqueue(new Callback<List<PrintOrder>>() {
                @Override
                public void onResponse(retrofit2.Call<List<PrintOrder>> call, retrofit2.Response<List<PrintOrder>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(OrderHistoryActivity.this, "Pickup rescheduled!", Toast.LENGTH_SHORT).show();
                        AuthUser user = SessionManager.getInstance().getCurrentUser();
                        if (user != null) orderViewModel.loadUserOrders(user.getUid());
                    } else {
                        Toast.makeText(OrderHistoryActivity.this, "Failed to reschedule", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<PrintOrder>> call, Throwable t) {
                    Toast.makeText(OrderHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
