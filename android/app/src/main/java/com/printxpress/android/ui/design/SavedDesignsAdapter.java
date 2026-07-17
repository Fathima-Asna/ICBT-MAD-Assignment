package com.printxpress.android.ui.design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.SavedDesign;

import java.util.ArrayList;
import java.util.List;

public class SavedDesignsAdapter extends RecyclerView.Adapter<SavedDesignsAdapter.ViewHolder> {

    public interface OnDesignClickListener {
        void onDeleteClick(SavedDesign design);
        void onUseClick(SavedDesign design);
    }

    private final List<SavedDesign> designs = new ArrayList<>();
    private final OnDesignClickListener listener;

    public SavedDesignsAdapter(OnDesignClickListener listener) {
        this.listener = listener;
    }

    public void setDesigns(List<SavedDesign> newDesigns) {
        designs.clear();
        if (newDesigns != null) {
            designs.addAll(newDesigns);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedDesign design = designs.get(position);
        holder.tvDesignName.setText(design.getName());
        holder.tvCustomText.setText("Custom text: " + (design.getCustomText() != null ? design.getCustomText() : "None"));
        holder.tvDesignUrl.setText("Artwork URL: " + (design.getDesignUrl() != null ? design.getDesignUrl() : "None"));

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(design));
        holder.btnUse.setOnClickListener(v -> listener.onUseClick(design));
    }

    @Override
    public int getItemCount() {
        return designs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesignName, tvCustomText, tvDesignUrl;
        Button btnDelete, btnUse;

        ViewHolder(View itemView) {
            super(itemView);
            tvDesignName = itemView.findViewById(R.id.tvDesignName);
            tvCustomText = itemView.findViewById(R.id.tvCustomText);
            tvDesignUrl = itemView.findViewById(R.id.tvDesignUrl);
            btnDelete = itemView.findViewById(R.id.btnDeleteDesign);
            btnUse = itemView.findViewById(R.id.btnUseDesign);
        }
    }
}
