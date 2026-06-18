package com.android.scansantri.presentation.list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.databinding.FragmentAchievementDetailBinding;
import com.android.scansantri.presentation.adapter.AchievementAdapter;
import com.android.scansantri.presentation.add.AddSantriActivity;

import java.io.Serializable;
import java.util.List;


public class AchievementDetailFragment extends Fragment {

    private static final String ARG_SANTRI_DETAIL = "current_santri_detail";

    private List<Achievement> currentSantriAchievement;
    private FragmentAchievementDetailBinding binding;


    public static AchievementDetailFragment newInstance(List<Achievement> santriData) {
        AchievementDetailFragment fragment = new AchievementDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SANTRI_DETAIL, (Serializable) santriData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentSantriAchievement = (List<Achievement>) getArguments().getSerializable(ARG_SANTRI_DETAIL);
        }

        binding = FragmentAchievementDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AchievementAdapter achievementAdapter = new AchievementAdapter(currentSantriAchievement, requireContext(), false, new AchievementAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Achievement clickedRecord) {
            }

            @Override
            public void onItemClick(Achievement clickedRecord) {
            }

        });

        binding.rvAchievement.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvAchievement.setAdapter(achievementAdapter);

        if(currentSantriAchievement.isEmpty()){
            binding.tvKosong.setVisibility(View.VISIBLE);
            binding.rvAchievement.setVisibility(View.GONE);
        }else{
            binding.tvKosong.setVisibility(View.GONE);
            binding.rvAchievement.setVisibility(View.VISIBLE);
        }
    }
}