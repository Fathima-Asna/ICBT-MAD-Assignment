package com.printxpress.android.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.Sample;

import java.util.ArrayList;
import java.util.List;

public class AdminSamplesAdapter extends RecyclerView.Adapter<AdminSamplesAdapter.ViewHolder> {

    public interface OnSampleActionListener {
        void onDeleteClick(Sample sample);
    }

    private final List<Sample> samples = new ArrayList<>();
    private final OnSampleActionListener listener;

    public AdminSamplesAdapter(OnSampleActionListener listener) {
        this.listener = listener;
    }

    public void setSamples(List<Sample> newSamples) {
        samples.clear();
        if (newSamples != null) {
            samples.addAll(newSamples);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reuse the item_sample layout but hide/show action controls or build simple item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sample sample = samples.get(position);
        holder.tvTitle.setText(sample.getName());
        holder.tvCategory.setText(sample.getCategory());
        
        String bleed = sample.getBleedMargins() != null ? sample.getBleedMargins() : "0.125 inches";
        String color = sample.getColorFormats() != null ? sample.getColorFormats() : "CMYK mode";
        String url = sample.getTemplateUrl() != null ? sample.getTemplateUrl() : "Template link";

        holder.tvBleed.setText("Bleed Margins: " + bleed);
        holder.tvColor.setText("Color Format: " + color);
        holder.tvUrl.setText("Template link: " + url);

        // Turn "Use Template" button into a "Delete template" button for Admins!
        holder.btnUse.setText("Delete Template");
        holder.btnUse.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        holder.btnUse.setBackgroundTintList(android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.error)));
        holder.btnUse.setOnClickListener(v -> listener.onDeleteClick(sample));
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvBleed, tvColor, tvUrl;
        Button btnUse;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvSampleTitle);
            tvCategory = itemView.findViewById(R.id.tvSampleCategory);
            tvBleed = itemView.findViewById(R.id.tvBleedMargins);
            tvColor = itemView.findViewById(R.id.tvColorFormats);
            tvUrl = itemView.findViewById(R.id.tvTemplateUrl);
            btnUse = itemView.findViewById(R.id.btnUseTemplate);
        }
    }
}
