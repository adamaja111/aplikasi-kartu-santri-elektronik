package com.android.scansantri.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scansantri.data.model.HealthRecord;
import com.android.scansantri.databinding.ItemLayoutHealthBinding;

import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.HealthRecordViewHolder> {

    private List<HealthRecord> healthRecords;
    private Context context;
    private OnItemClickListener listener;
    private boolean showDelete;

    public interface OnItemClickListener {
        void onItemDelete(HealthRecord clickedRecord);
        void onItemClick(HealthRecord clickedRecord);
    }

    public HealthRecordAdapter(List<HealthRecord> healthRecords, Context context, Boolean showDelete, OnItemClickListener listener) {
        this.healthRecords = healthRecords;
        this.context = context;
        this.listener = listener;
        this.showDelete = showDelete;
    }

    public void updateSubmitData(List<HealthRecord> newHealthRecords){
        healthRecords = newHealthRecords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HealthRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLayoutHealthBinding binding = ItemLayoutHealthBinding.inflate(inflater, parent, false);
        return new HealthRecordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthRecordViewHolder holder, int position) {
        HealthRecord record = healthRecords.get(position);

        holder.binding.tvNamaPenyakit.setText(record.getDisease());
        holder.binding.tvTanggalPenyakit.setText("Tanggal pemeriksaan: " + record.getCheckupDate());
        holder.binding.tvPenangananPenyakit.setText(record.getTreatment());

        if (record.getAdditionalNotes() != null && !record.getAdditionalNotes().isEmpty()) {
            holder.binding.tvCatatanPenyakit.setVisibility(View.VISIBLE);
            holder.binding.tvCatatanPenyakit.setText(record.getAdditionalNotes());
        } else {
            holder.binding.tvCatatanPenyakit.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(record));

        holder.binding.btnDelete.setOnClickListener(v -> listener.onItemDelete(record));

        if(showDelete){
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return healthRecords.size();
    }

    public class HealthRecordViewHolder extends RecyclerView.ViewHolder {
        ItemLayoutHealthBinding binding;

        public HealthRecordViewHolder(ItemLayoutHealthBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
