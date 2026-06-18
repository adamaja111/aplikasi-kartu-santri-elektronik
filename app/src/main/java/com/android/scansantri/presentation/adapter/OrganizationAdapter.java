package com.android.scansantri.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scansantri.data.model.Organization;
import com.android.scansantri.databinding.ItemLayoutOrganizationBinding;

import java.util.List;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.OrganizationViewHolder> {

    private List<Organization> organizationList;
    private Context context;
    private OnItemClickListener listener;
    private Boolean showDelete;

    public interface OnItemClickListener {
        void onItemDelete(Organization organization);
        void onItemClick(Organization organization);
    }

    public OrganizationAdapter(List<Organization> organizationList, Context context, Boolean showDelete, OnItemClickListener listener) {
        this.organizationList = organizationList;
        this.context = context;
        this.listener = listener;
        this.showDelete = showDelete;
    }

    public void updateSubmitData(List<Organization> newOrganizations) {
        organizationList = newOrganizations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLayoutOrganizationBinding binding = ItemLayoutOrganizationBinding.inflate(inflater, parent, false);
        return new OrganizationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        Organization organization = organizationList.get(position);

        holder.binding.tvNamaOrganisasi.setText(organization.getOrganizationName());
        holder.binding.tvJabatanOrganisasi.setText(organization.getPosition());

        String dateRange = organization.getStartDate() + " - " + (organization.getEndDate() != null || !organization.getEndDate().isEmpty() ? organization.getEndDate() : "Sekarang");
        holder.binding.tvWaktuOrganisasi.setText(dateRange);

        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(organization));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onItemDelete(organization));

        if(showDelete){
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return organizationList.size();
    }

    public static class OrganizationViewHolder extends RecyclerView.ViewHolder {
        ItemLayoutOrganizationBinding binding;

        public OrganizationViewHolder(ItemLayoutOrganizationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
