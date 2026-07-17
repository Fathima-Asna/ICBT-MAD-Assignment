package com.printxpress.android.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.PrintOrder;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onUpdateStatusClick(PrintOrder order, View anchor);
    }

    private final List<PrintOrder> orders = new ArrayList<>();
    private final OnOrderClickListener listener;

    public AdminOrdersAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<PrintOrder> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrintOrder order = orders.get(position);
        holder.tvName.setText(order.getName() != null ? order.getName() : "Order");
        holder.tvStatus.setText("Status: " + (order.getStatus() != null ? order.getStatus() : "PENDING"));
        
        double total = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
        holder.tvTotal.setText(String.format("Total: $%.2f", total));

        StringBuilder sb = new StringBuilder();
        if (order.getPaperType() != null) sb.append("Paper: ").append(order.getPaperType()).append("\n");
        if (order.getSize() != null) sb.append("Size: ").append(order.getSize()).append("\n");
        if (order.getCustomText() != null && !order.getCustomText().isEmpty()) {
            sb.append("Text: ").append(order.getCustomText()).append("\n");
        }
        if (order.getDesignUrl() != null && !order.getDesignUrl().isEmpty()) {
            sb.append("Artwork: ").append(order.getDesignUrl());
        }
        String specs = sb.toString().trim();
        holder.tvSpecs.setText(specs.isEmpty() ? "No custom specs" : specs);

        String delId = order.getDeliveryId() != null ? order.getDeliveryId() : "Standard Delivery";
        if (order.getPickupTime() != null) {
            holder.tvDelivery.setText("Method: " + delId + " (" + order.getPickupTime() + ")");
        } else {
            holder.tvDelivery.setText("Method: " + delId);
        }

        holder.btnUpdate.setOnClickListener(v -> listener.onUpdateStatusClick(order, holder.btnUpdate));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvSpecs, tvDelivery, tvTotal;
        Button btnUpdate;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminOrderName);
            tvStatus = itemView.findViewById(R.id.tvAdminOrderStatus);
            tvSpecs = itemView.findViewById(R.id.tvAdminOrderSpecs);
            tvDelivery = itemView.findViewById(R.id.tvAdminOrderDelivery);
            tvTotal = itemView.findViewById(R.id.tvAdminOrderTotal);
            btnUpdate = itemView.findViewById(R.id.btnUpdateOrderStatus);
        }
    }
}
