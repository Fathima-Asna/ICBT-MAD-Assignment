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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<PrintOrder> orders = new ArrayList<>();

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
        holder.tvOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Pending");
        double total = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
        holder.tvOrderTotal.setText(String.format("Total: $%.2f", total));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderName, tvOrderStatus, tvOrderTotal;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}
