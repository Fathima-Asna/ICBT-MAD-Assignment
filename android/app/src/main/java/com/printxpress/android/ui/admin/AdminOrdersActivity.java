package com.printxpress.android.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrdersActivity extends AppCompatActivity implements AdminOrdersAdapter.OnOrderClickListener {

    private SupabaseDataApi dataApi;
    private AdminOrdersAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        tvEmpty = findViewById(R.id.tvEmptyAdminOrders);
        RecyclerView rv = findViewById(R.id.rvAdminOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(this);
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        loadAllOrders();
    }

    private void loadAllOrders() {
        dataApi.getAllOrders("*", "createdAt.desc").enqueue(new Callback<List<PrintOrder>>() {
            @Override
            public void onResponse(Call<List<PrintOrder>> call, Response<List<PrintOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setOrders(response.body());
                    tvEmpty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(AdminOrdersActivity.this, "Failed to load orders queue", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PrintOrder>> call, Throwable t) {
                Toast.makeText(AdminOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateStatusClick(PrintOrder order, View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 1, 0, "Set PENDING");
        popup.getMenu().add(0, 2, 0, "Set PROCESSING");
        popup.getMenu().add(0, 3, 0, "Set PRINTING");
        popup.getMenu().add(0, 4, 0, "Set READY FOR PICKUP");
        popup.getMenu().add(0, 5, 0, "Set DELIVERED");

        popup.setOnMenuItemClickListener(item -> {
            String newStatus;
            switch (item.getItemId()) {
                case 1: newStatus = "PENDING"; break;
                case 2: newStatus = "PROCESSING"; break;
                case 3: newStatus = "PRINTING"; break;
                case 4: newStatus = "READY FOR PICKUP"; break;
                case 5: newStatus = "DELIVERED"; break;
                default: return false;
            }
            updateStatus(order.getId(), newStatus);
            return true;
        });
        popup.show();
    }

    private void updateStatus(String orderId, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        dataApi.updateOrderStatus("eq." + orderId, map).enqueue(new Callback<List<PrintOrder>>() {
            @Override
            public void onResponse(Call<List<PrintOrder>> call, Response<List<PrintOrder>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminOrdersActivity.this, "Status updated to " + status, Toast.LENGTH_SHORT).show();
                    loadAllOrders();
                } else {
                    Toast.makeText(AdminOrdersActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PrintOrder>> call, Throwable t) {
                Toast.makeText(AdminOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
