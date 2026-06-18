package com.android.scansantri.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.databinding.ItemLayoutAwardBinding;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<Achievement> achievements;
    private Context context;
    private OnItemClickListener listener;
    private boolean showDelete;

    public interface OnItemClickListener {
        void onItemDelete(Achievement achievement);
        void onItemClick(Achievement achievement);
    }

    public AchievementAdapter(List<Achievement> achievements, Context context, Boolean showDelete, OnItemClickListener listener) {
        this.achievements = achievements;
        this.context = context;
        this.listener = listener;
        this.showDelete = showDelete;
    }

    public void updateAchievementList(List<Achievement> newAchievements) {
        achievements = newAchievements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLayoutAwardBinding binding = ItemLayoutAwardBinding.inflate(inflater, parent, false);
        return new AchievementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);

        holder.binding.tvNamaPrestasi.setText(achievement.getEventName());
        holder.binding.tvPeringkatTingkatPrestasi.setText(achievement.getRankOrParticipant() + " (" + achievement.getLevel() + ")");
        holder.binding.tvTglPrestasi.setText(achievement.getAchievementDate());

        holder.binding.getRoot().setOnClickListener(v -> listener.onItemClick(achievement));

        holder.binding.btnDelete.setOnClickListener(v -> listener.onItemDelete(achievement));

        if(showDelete){
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ItemLayoutAwardBinding binding;

        public AchievementViewHolder(ItemLayoutAwardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

