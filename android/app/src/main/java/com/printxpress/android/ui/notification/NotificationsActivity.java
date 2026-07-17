package com.printxpress.android.ui.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.PrintOrder;
import com.printxpress.android.data.model.Promotion;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.data.remote.SupabaseDataApi;
import com.printxpress.android.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private SupabaseDataApi dataApi;
    private NotificationsAdapter adapter;
    private TextView tvEmpty;
    private final List<NotificationsAdapter.NotificationItem> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        tvEmpty = findViewById(R.id.tvEmptyNotifications);
        RecyclerView rv = findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter();
        rv.setAdapter(adapter);

        dataApi = SupabaseClient.getDataApi();

        // Seed static welcome notification
        notifications.add(new NotificationsAdapter.NotificationItem(
                "Welcome to PrintXpress!",
                "Check out our new categories (Business Cards, Flyers, Posters, Banners) and configure custom specifications.",
                "Just now"
        ));

        loadData();
    }

    private void loadData() {
        AuthUser user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            adapter.setItems(notifications);
            return;
        }

        // 1. Fetch active promotions
        dataApi.getPromotions("*").enqueue(new Callback<List<Promotion>>() {
            @Override
            public void onResponse(Call<List<Promotion>> call, Response<List<Promotion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Promotion promo : response.body()) {
                        notifications.add(new NotificationsAdapter.NotificationItem(
                                "Promo Alert: " + promo.getTitle(),
                                promo.getDescription() + " Use code: " + promo.getCode() + " for " + promo.getDiscount() + "% off. Exp: " + promo.getExpiryDate(),
                                "Today"
                        ));
                    }
                }
                // 2. Fetch order updates
                fetchOrderAlerts(user.getUid());
            }

            @Override
            public void onFailure(Call<List<Promotion>> call, Throwable t) {
                fetchOrderAlerts(user.getUid());
            }
        });
    }

    private void fetchOrderAlerts(String userId) {
        dataApi.getOrders("eq." + userId, "*", "createdAt.desc").enqueue(new Callback<List<PrintOrder>>() {
            @Override
            public void onResponse(Call<List<PrintOrder>> call, Response<List<PrintOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PrintOrder order : response.body()) {
                        notifications.add(new NotificationsAdapter.NotificationItem(
                                "Order Status Update: " + order.getName(),
                                "Your order is currently " + order.getStatus() + ". Delivery method: " + order.getDeliveryId() + ".",
                                "Order Ref: #" + order.getId().substring(0, 8)
                        ));
                    }
                }
                updateUI();
            }

            @Override
            public void onFailure(Call<List<PrintOrder>> call, Throwable t) {
                updateUI();
            }
        });
    }

    private void updateUI() {
        adapter.setItems(notifications);
        tvEmpty.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
