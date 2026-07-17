package com.printxpress.android.ui.gallery;

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

public class SampleGalleryAdapter extends RecyclerView.Adapter<SampleGalleryAdapter.ViewHolder> {

    public interface OnTemplateClickListener {
        void onUseTemplateClick(Sample sample);
    }

    private final List<Sample> samples = new ArrayList<>();
    private final OnTemplateClickListener listener;

    public SampleGalleryAdapter(OnTemplateClickListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sample sample = samples.get(position);
        holder.tvTitle.setText(sample.getName());
        holder.tvCategory.setText(sample.getCategory());
        
        // Show simulated bleed margins/color modes if not set
        String bleed = sample.getBleedMargins() != null ? sample.getBleedMargins() : "0.125 inches (3mm)";
        String color = sample.getColorFormats() != null ? sample.getColorFormats() : "CMYK mode (recommended)";
        String url = sample.getTemplateUrl() != null ? sample.getTemplateUrl() : "https://printxpress.com/templates/" + sample.getId();

        holder.tvBleed.setText("Bleed Margins: " + bleed);
        holder.tvColor.setText("Color Format: " + color);
        holder.tvUrl.setText("Template link: " + url);

        holder.btnUse.setOnClickListener(v -> listener.onUseTemplateClick(sample));
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
