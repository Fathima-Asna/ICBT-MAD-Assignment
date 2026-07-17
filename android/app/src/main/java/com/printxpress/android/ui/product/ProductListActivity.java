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

import android.widget.Button;
import android.widget.PopupMenu;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.User;
import com.printxpress.android.data.remote.SupabaseClient;
import com.printxpress.android.ui.admin.AdminDashboardActivity;
import com.printxpress.android.ui.profile.ProfileActivity;
import com.printxpress.android.ui.design.SavedDesignsActivity;
import com.printxpress.android.ui.gallery.SampleGalleryActivity;
import com.printxpress.android.ui.resources.ResourcesActivity;
import com.printxpress.android.ui.order.OrderHistoryActivity;
import com.printxpress.android.ui.notification.NotificationsActivity;
import com.printxpress.android.ui.auth.LoginActivity;
import com.printxpress.android.util.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private ProductViewModel productViewModel;
    private ProductAdapter adapter;
    private TextView tvEmptyState;
    private Button btnMenu;
    private Button btnCatAll, btnCatCards, btnCatFlyers, btnCatPosters, btnCatBanners;
    
    private String selectedCategory = null;
    private String prefillCustomText = null;
    private String prefillDesignUrl = null;
    private String userRole = "customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnMenu = findViewById(R.id.btnMenu);

        btnCatAll = findViewById(R.id.btnCatAll);
        btnCatCards = findViewById(R.id.btnCatCards);
        btnCatFlyers = findViewById(R.id.btnCatFlyers);
        btnCatPosters = findViewById(R.id.btnCatPosters);
        btnCatBanners = findViewById(R.id.btnCatBanners);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this);
        rvProducts.setAdapter(adapter);

        // Fetch prefilled data if returning from saved designs or sample templates
        if (getIntent() != null) {
            prefillCustomText = getIntent().getStringExtra("prefill_custom_text");
            prefillDesignUrl = getIntent().getStringExtra("prefill_design_url");
            if (prefillCustomText != null || prefillDesignUrl != null) {
                Toast.makeText(this, "Template selected! Click any product to order.", Toast.LENGTH_LONG).show();
            }
        }

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProducts().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess() && response.getData() != null) {
                adapter.setProducts(response.getData());
                setEmptyState(response.getData().isEmpty(), "No products found in this category.");
            } else {
                setEmptyState(true, "Failed to load products.\nCheck your internet connection.");
                Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Failed to load products", Toast.LENGTH_LONG).show();
            }
        });

        // Set up category button click listeners
        btnCatAll.setOnClickListener(v -> filterCategory(null, btnCatAll));
        btnCatCards.setOnClickListener(v -> filterCategory("Business Cards", btnCatCards));
        btnCatFlyers.setOnClickListener(v -> filterCategory("Flyers", btnCatFlyers));
        btnCatPosters.setOnClickListener(v -> filterCategory("Posters", btnCatPosters));
        btnCatBanners.setOnClickListener(v -> filterCategory("Banners", btnCatBanners));

        btnMenu.setOnClickListener(v -> showMenuPopup());

        productViewModel.loadProducts(null);
        fetchUserRole();
    }

    private void fetchUserRole() {
        AuthUser authUser = SessionManager.getInstance().getCurrentUser();
        if (authUser == null) return;
        SupabaseClient.getDataApi().findProfiles(null, "eq." + authUser.getUid(), "*").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    userRole = response.body().get(0).getRole();
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }

    private void filterCategory(String category, Button selectedButton) {
        selectedCategory = category;
        productViewModel.loadProducts(category);

        // Reset all buttons
        resetButtonTheme(btnCatAll);
        resetButtonTheme(btnCatCards);
        resetButtonTheme(btnCatFlyers);
        resetButtonTheme(btnCatPosters);
        resetButtonTheme(btnCatBanners);

        // Highlight selected
        selectedButton.setTextColor(getResources().getColor(R.color.black));
        selectedButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.cyan_400)));
    }

    private void resetButtonTheme(Button btn) {
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.surface_variant_dark)));
    }

    private void showMenuPopup() {
        PopupMenu popup = new PopupMenu(this, btnMenu);
        popup.getMenu().add(0, 1, 0, "My Profile");
        popup.getMenu().add(0, 2, 0, "Saved Designs");
        popup.getMenu().add(0, 3, 0, "Sample Gallery");
        popup.getMenu().add(0, 4, 0, "FAQs & Support");
        popup.getMenu().add(0, 5, 0, "My Orders");
        popup.getMenu().add(0, 9, 0, "Notifications & Alerts");
        if ("admin".equals(userRole)) {
            popup.getMenu().add(0, 6, 0, "Admin Panel (Admin Only)");
        } else {
            popup.getMenu().add(0, 7, 0, "View Admin Panel (Demo Override)");
        }
        popup.getMenu().add(0, 8, 0, "Logout");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                case 2:
                    startActivity(new Intent(this, SavedDesignsActivity.class));
                    return true;
                case 3:
                    startActivity(new Intent(this, SampleGalleryActivity.class));
                    return true;
                case 4:
                    startActivity(new Intent(this, ResourcesActivity.class));
                    return true;
                case 5:
                    startActivity(new Intent(this, OrderHistoryActivity.class));
                    return true;
                case 9:
                    startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                case 6:
                case 7:
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                    return true;
                case 8:
                    SessionManager.getInstance().clearSession();
                    Intent logoutIntent = new Intent(this, LoginActivity.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutIntent);
                    finish();
                    return true;
            }
            return false;
        });
        popup.show();
    }

    private void setEmptyState(boolean show, String message) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        tvEmptyState.setText(message);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        if (prefillCustomText != null) {
            intent.putExtra("prefill_custom_text", prefillCustomText);
        }
        if (prefillDesignUrl != null) {
            intent.putExtra("prefill_design_url", prefillDesignUrl);
        }
        startActivity(intent);
    }
}
