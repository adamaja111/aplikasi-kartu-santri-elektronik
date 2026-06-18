package com.android.scansantri.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.databinding.ItemListReportSantriBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportSantriSelectionAdapter extends RecyclerView.Adapter<ReportSantriSelectionAdapter.ViewHolder> {

    private List<SantriData> santriList;
    private Set<Integer> selectedPositions = new HashSet<>();

    public ReportSantriSelectionAdapter(List<SantriData> santriList) {
        this.santriList = santriList;
    }

    public void updateList(List<SantriData> newList) {
        this.santriList = newList;
        notifyDataSetChanged();
    }

    public void selectAll(boolean isSelected) {
        if (isSelected) {
            for (int i = 0; i < santriList.size(); i++) {
                selectedPositions.add(i);
            }
        } else {
            selectedPositions.clear();
        }
        notifyDataSetChanged();
    }

    public List<SantriData> getSelectedSantri() {
        List<SantriData> selectedList = new ArrayList<>();
        for (int position : selectedPositions) {
            selectedList.add(santriList.get(position));
        }
        return selectedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListReportSantriBinding binding = ItemListReportSantriBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SantriData santri = santriList.get(position);
        holder.binding.tvNamaSantri.setText(santri.getFullName());
        holder.binding.tvNisSantri.setText("NIS: " + santri.getNis());

        holder.binding.cbSelect.setOnCheckedChangeListener(null);
        holder.binding.cbSelect.setChecked(selectedPositions.contains(position));

        holder.binding.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPositions.add(position);
            } else {
                selectedPositions.remove(position);
            }
        });

        holder.binding.getRoot().setOnClickListener(v -> {
            holder.binding.cbSelect.setChecked(!holder.binding.cbSelect.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return santriList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemListReportSantriBinding binding;

        public ViewHolder(ItemListReportSantriBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
