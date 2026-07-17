package com.printxpress.android.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.ResourceItem;

import java.util.ArrayList;
import java.util.List;

public class AdminFAQsAdapter extends RecyclerView.Adapter<AdminFAQsAdapter.ViewHolder> {

    public interface OnFaqActionListener {
        void onDeleteClick(ResourceItem item);
    }

    private final List<ResourceItem> items = new ArrayList<>();
    private final OnFaqActionListener listener;

    public AdminFAQsAdapter(OnFaqActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ResourceItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_faq, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResourceItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText(item.getCategory() != null ? item.getCategory().toUpperCase() : "RESOURCE");
        holder.tvContent.setText(item.getContent());
        
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvContent;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAdminFaqTitle);
            tvCategory = itemView.findViewById(R.id.tvAdminFaqCategory);
            tvContent = itemView.findViewById(R.id.tvAdminFaqContent);
            btnDelete = itemView.findViewById(R.id.btnDeleteFaq);
        }
    }
}
