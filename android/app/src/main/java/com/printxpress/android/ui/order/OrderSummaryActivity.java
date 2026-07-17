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

public class OrderSummaryActivity extends AppCompatActivity {

    private OrderViewModel orderViewModel;
    private TextView tvProductName, tvUnitPrice, tvTotal;
    private EditText etQuantity;
    private Button btnPlaceOrder;
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
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.getOrderResult().observe(this, response -> {
            if (response == null) return;
            if (response.isSuccess()) {
                Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();
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

        etQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateTotal(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void updateTotal() {
        int qty = 1;
        try {
            qty = Integer.parseInt(etQuantity.getText().toString());
        } catch (NumberFormatException ignored) {}
        if (qty < 1) qty = 1;
        tvTotal.setText(String.format("Total: $%.2f", unitPrice * qty));
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
        request.setDeliveryId("standard");
        request.setItems(Collections.singletonList(item));

        orderViewModel.createOrder(request);
    }
}
