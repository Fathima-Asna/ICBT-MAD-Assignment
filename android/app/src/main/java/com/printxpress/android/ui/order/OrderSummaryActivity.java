package com.printxpress.android.ui.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.printxpress.android.R;
import com.printxpress.android.data.model.AuthUser;
import com.printxpress.android.data.model.CreateOrderRequest;
import com.printxpress.android.data.model.OrderItemRequest;
import com.printxpress.android.util.SessionManager;
import com.printxpress.android.viewmodel.OrderViewModel;

import java.util.Collections;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.printxpress.android.data.model.SavedDesign;
import com.printxpress.android.data.remote.SupabaseClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSummaryActivity extends AppCompatActivity {

    private OrderViewModel orderViewModel;
    private TextView tvProductName, tvUnitPrice, tvTotal;
    private EditText etQuantity, etCustomText, etDesignUrl, etPickupTime;
    private Spinner spPaperType, spSize, spDelivery;
    private Button btnPlaceOrder, btnSaveDesign;
    
    private double unitPrice;
    private String productId;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        tvProductName = findViewById(R.id.tvProductName);
        tvUnitPrice = findViewById(R.id.tvUnitPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etCustomText = findViewById(R.id.etCustomText);
        etDesignUrl = findViewById(R.id.etDesignUrl);
        etPickupTime = findViewById(R.id.etPickupTime);
        
        spPaperType = findViewById(R.id.spPaperType);
        spSize = findViewById(R.id.spSize);
        spDelivery = findViewById(R.id.spDelivery);
        
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnSaveDesign = findViewById(R.id.btnSaveDesign);

        setupSpinners();

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderResult().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess()) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Order failed", Toast.LENGTH_LONG).show();
            }
        });

        productId = getIntent().getStringExtra("product_id");
        productName = getIntent().getStringExtra("product_name");
        unitPrice = getIntent().getDoubleExtra("product_price", 0.0);

        tvProductName.setText(productName != null ? productName : "No product");
        tvUnitPrice.setText(String.format("Unit price: $%.2f", unitPrice));
        updateTotal();

        // Handle prefilled custom design or template details
        String prefillCustom = getIntent().getStringExtra("prefill_custom_text");
        String prefillUrl = getIntent().getStringExtra("prefill_design_url");
        if (prefillCustom != null) etCustomText.setText(prefillCustom);
        if (prefillUrl != null) etDesignUrl.setText(prefillUrl);

        etQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotal(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
        btnSaveDesign.setOnClickListener(v -> saveDesignToProfile());
    }

    private void setupSpinners() {
        String[] papers = {"300gsm Matte Cardboard", "150gsm Glossy Paper", "200gsm Satin Photo Paper", "13oz Weatherproof Vinyl"};
        ArrayAdapter<String> paperAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, papers);
        paperAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaperType.setAdapter(paperAdapter);

        String[] sizes = {"Standard Size", "A5 Booklet Size", "A2 Poster Size", "3x6ft Banner Size"};
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSize.setAdapter(sizeAdapter);

        String[] deliveries = {"Standard Home Delivery", "Express Courier Delivery", "Store Scheduled Pickup"};
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deliveries);
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDelivery.setAdapter(deliveryAdapter);

        spDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If "Store Scheduled Pickup" is selected, show time entry
                if (position == 2) {
                    etPickupTime.setVisibility(View.VISIBLE);
                } else {
                    etPickupTime.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etPickupTime.setVisibility(View.GONE);
            }
        });
    }

    private void updateTotal() {
        int qty = 1;
        try {
            qty = Integer.parseInt(etQuantity.getText().toString());
        } catch (NumberFormatException ignored) {}
        if (qty < 1) qty = 1;
        tvTotal.setText(String.format("Total: $%.2f", unitPrice * qty));
    }

    private void saveDesignToProfile() {
        AuthUser user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String custom = etCustomText.getText().toString().trim();
        String url = etDesignUrl.getText().toString().trim();

        if (custom.isEmpty() && url.isEmpty()) {
            Toast.makeText(this, "Enter some text or design URL to save", Toast.LENGTH_SHORT).show();
            return;
        }

        SavedDesign design = new SavedDesign();
        design.setUserId(user.getUid());
        design.setName(productName + " (" + spPaperType.getSelectedItem().toString() + ")");
        design.setCustomText(custom);
        design.setDesignUrl(url);

        SupabaseClient.getDataApi().createSavedDesign(design).enqueue(new Callback<List<SavedDesign>>() {
            @Override
            public void onResponse(Call<List<SavedDesign>> call, Response<List<SavedDesign>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderSummaryActivity.this, "Design saved to your profile!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderSummaryActivity.this, "Failed to save design", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SavedDesign>> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeOrder() {
        AuthUser user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(etQuantity.getText().toString());
            if (qty < 1) qty = 1;
        } catch (NumberFormatException e) {
            qty = 1;
        }

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(productId);
        item.setQuantity(qty);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(user.getUid());
        request.setName(productName);
        request.setType("print");
        
        // Delivery maps to selected option
        String delMethod = spDelivery.getSelectedItem().toString();
        request.setDeliveryId(delMethod);

        // Add details from UI
        request.setPaperType(spPaperType.getSelectedItem().toString());
        request.setSize(spSize.getSelectedItem().toString());
        request.setCustomText(etCustomText.getText().toString().trim());
        request.setDesignUrl(etDesignUrl.getText().toString().trim());
        
        if (etPickupTime.getVisibility() == View.VISIBLE) {
            request.setPickupTime(etPickupTime.getText().toString().trim());
        }

        request.setItems(Collections.singletonList(item));

        orderViewModel.createOrder(request);
    }
}
