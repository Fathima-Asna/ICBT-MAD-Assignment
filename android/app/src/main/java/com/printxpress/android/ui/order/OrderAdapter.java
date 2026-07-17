package com.printxpress.android.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.PrintOrder;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onCancelOrderClick(PrintOrder order);
        void onRescheduleOrderClick(PrintOrder order);
    }

    private final List<PrintOrder> orders = new ArrayList<>();
    private OnOrderActionListener actionListener;

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.actionListener = listener;
    }

    public void setOrders(List<PrintOrder> orders) {
        this.orders.clear();
        if (orders != null) {
            this.orders.addAll(orders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        PrintOrder order = orders.get(position);
        holder.tvOrderName.setText(order.getName() != null ? order.getName() : "Order");
        
        String status = order.getStatus() != null ? order.getStatus() : "PENDING";
        holder.tvOrderStatus.setText("Status: " + status);
        
        double total = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
        holder.tvOrderTotal.setText(String.format("Total: $%.2f", total));

        // Format and display specifications details
        StringBuilder specsBuilder = new StringBuilder();
        if (order.getPaperType() != null) specsBuilder.append("Paper: ").append(order.getPaperType()).append("\n");
        if (order.getSize() != null) specsBuilder.append("Size: ").append(order.getSize()).append("\n");
        if (order.getCustomText() != null && !order.getCustomText().isEmpty()) {
            specsBuilder.append("Text: ").append(order.getCustomText()).append("\n");
        }
        if (order.getDesignUrl() != null && !order.getDesignUrl().isEmpty()) {
            specsBuilder.append("Artwork: ").append(order.getDesignUrl());
        }
        
        String specs = specsBuilder.toString().trim();
        holder.tvOrderSpecs.setText(specs.isEmpty() ? "No custom specifications" : specs);

        // Display Delivery Method
        String delId = order.getDeliveryId() != null ? order.getDeliveryId() : "Standard Home Delivery";
        if (order.getPickupTime() != null) {
            holder.tvOrderDelivery.setText("Method: " + delId + " (" + order.getPickupTime() + ")");
        } else {
            holder.tvOrderDelivery.setText("Method: " + delId);
        }

        // Configure actions buttons visibility
        if ("PENDING".equalsIgnoreCase(status)) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            
            // Only show reschedule if it's store pickup
            if (delId.contains("Pickup")) {
                holder.btnReschedule.setVisibility(View.VISIBLE);
            } else {
                holder.btnReschedule.setVisibility(View.GONE);
            }
        } else {
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnReschedule.setVisibility(View.GONE);
        }

        holder.btnCancel.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onCancelOrderClick(order);
        });

        holder.btnReschedule.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onRescheduleOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderName, tvOrderStatus, tvOrderSpecs, tvOrderDelivery, tvOrderTotal;
        Button btnCancel, btnReschedule;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderSpecs = itemView.findViewById(R.id.tvOrderSpecs);
            tvOrderDelivery = itemView.findViewById(R.id.tvOrderDelivery);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
            btnReschedule = itemView.findViewById(R.id.btnRescheduleOrder);
        }
    }
}
