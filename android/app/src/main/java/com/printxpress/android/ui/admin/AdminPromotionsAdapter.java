package com.printxpress.android.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.android.R;
import com.printxpress.android.data.model.Promotion;

import java.util.ArrayList;
import java.util.List;

public class AdminPromotionsAdapter extends RecyclerView.Adapter<AdminPromotionsAdapter.ViewHolder> {

    public interface OnPromoActionListener {
        void onDeleteClick(Promotion promo);
    }

    private final List<Promotion> promos = new ArrayList<>();
    private final OnPromoActionListener listener;

    public AdminPromotionsAdapter(OnPromoActionListener listener) {
        this.listener = listener;
    }

    public void setPromos(List<Promotion> newPromos) {
        promos.clear();
        if (newPromos != null) {
            promos.addAll(newPromos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_promo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promotion promo = promos.get(position);
        holder.tvTitle.setText(promo.getTitle());
        holder.tvDesc.setText(promo.getDescription());
        holder.tvDetails.setText("CODE: " + promo.getCode() + " | Discount: " + promo.getDiscount() + "%");
        
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(promo));
    }

    @Override
    public int getItemCount() {
        return promos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDetails;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAdminPromoTitle);
            tvDesc = itemView.findViewById(R.id.tvAdminPromoDesc);
            tvDetails = itemView.findViewById(R.id.tvAdminPromoDetails);
            btnDelete = itemView.findViewById(R.id.btnDeletePromo);
        }
    }
}
