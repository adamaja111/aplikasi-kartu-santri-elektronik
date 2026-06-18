package com.android.scansantri.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scansantri.R;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.databinding.ItemListSantriBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class SantriAdapter extends RecyclerView.Adapter<SantriAdapter.SantriViewHolder> {

    private List<SantriData> santriList;
    private Context context;
    private OnSantriClickListener listener;

    public interface OnSantriClickListener {
        void onSantriClick(SantriData santri);
        void onZoomClick(SantriData santri);
    }

    public SantriAdapter(List<SantriData> santriList, Context context, OnSantriClickListener listener) {
        this.santriList = santriList;
        this.context = context;
        this.listener = listener;
    }

    // Method to update the list
    public void updateSantriList(List<SantriData> newSantriList) {
        this.santriList = newSantriList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SantriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemListSantriBinding binding = ItemListSantriBinding.inflate(inflater, parent, false);
        return new SantriViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SantriViewHolder holder, int position) {
        SantriData santri = santriList.get(position);

        holder.binding.tvNamaSantri.setText(santri.getFullName());
        holder.binding.tvTTLahir.setText(santri.getBirthPlace() + " (" + santri.getBirthDate() + ")");
        holder.binding.tvNomorTelpon.setText(santri.getPhoneNumber());
        holder.binding.tvEmail.setText(santri.getEmail());

        if (santri.getGender().equalsIgnoreCase("Laki-laki")) {
            holder.binding.ivGender.setImageResource(R.drawable.img_male);
        } else {
            holder.binding.ivGender.setImageResource(R.drawable.img_female);
        }
        Glide.with(context).load(santri.getBarcodeLink()).into(holder.binding.ivQrSantri);

        holder.binding.getRoot().setOnClickListener(v -> listener.onSantriClick(santri));

        holder.binding.btnZoom.setOnClickListener(v -> listener.onZoomClick(santri));
    }

    @Override
    public int getItemCount() {
        return santriList.size();
    }

    public static class SantriViewHolder extends RecyclerView.ViewHolder {
        ItemListSantriBinding binding;

        public SantriViewHolder(ItemListSantriBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
