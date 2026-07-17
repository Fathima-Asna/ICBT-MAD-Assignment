package com.printxpress.android.ui.resources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.ResourceItem;

import java.util.ArrayList;
import java.util.List;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {

    private final List<ResourceItem> items = new ArrayList<>();

    public ResourcesAdapter() {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResourceItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
        
        String catLabel = item.getCategory() != null ? item.getCategory().toUpperCase() : "ARTICLE";
        holder.tvCategory.setText(catLabel);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvContent;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvResourceTitle);
            tvCategory = itemView.findViewById(R.id.tvResourceCategory);
            tvContent = itemView.findViewById(R.id.tvResourceContent);
        }
    }
}
