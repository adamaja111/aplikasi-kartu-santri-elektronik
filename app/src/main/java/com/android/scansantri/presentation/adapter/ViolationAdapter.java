package com.android.scansantri.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scansantri.data.model.Violation;
import com.android.scansantri.databinding.ItemLayoutPelanggaranBinding;

import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViolationViewHolder> {

    private List<Violation> violations;
    private Context context;
    private OnItemClickListener listener;
    private boolean showDelete;

    public interface OnItemClickListener {
        void onItemDelete(Violation violation);
        void onItemClick(Violation violation);
    }

    public ViolationAdapter(List<Violation> violations, Context context, Boolean showDelete, OnItemClickListener listener) {
        this.violations = violations;
        this.context = context;
        this.listener = listener;
        this.showDelete = showDelete;
    }

    public void updateViolationList(List<Violation> newViolations) {
        violations = newViolations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLayoutPelanggaranBinding binding = ItemLayoutPelanggaranBinding.inflate(inflater, parent, false);
        return new ViolationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationViewHolder holder, int position) {
        Violation violation = violations.get(position);

        // Bind data to TextViews
        holder.binding.tvNamaPelanggaran.setText(violation.getViolationName());
        holder.binding.tvJenisPelanggaran.setText("* " + violation.getViolationType());
        holder.binding.tvPoinPelanggaran.setText("Poin: " + violation.getPoints());
        holder.binding.tvTanggalPelanggaran.setText("Tanggal pelanggaran: " + violation.getViolationDate());
        holder.binding.tvSanksiPelanggaran.setText("Sanksi: " + violation.getSanction());
        holder.binding.tvTanggalSanksiPenyakit.setText("Tanggal Sanksi: " + violation.getSanctionDate());

        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(violation));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onItemDelete(violation));

        if(showDelete){
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return violations.size();
    }

    public static class ViolationViewHolder extends RecyclerView.ViewHolder {
        ItemLayoutPelanggaranBinding binding;

        public ViolationViewHolder(ItemLayoutPelanggaranBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

