package com.android.scansantri.presentation.list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.scansantri.data.model.HealthRecord;
import com.android.scansantri.databinding.FragmentHealthRecordDetailBinding;
import com.android.scansantri.presentation.adapter.HealthRecordAdapter;
import com.android.scansantri.presentation.add.AddSantriActivity;

import java.io.Serializable;
import java.util.List;


public class HealthRecordDetailFragment extends Fragment {

    private static final String ARG_SANTRI_DETAIL = "current_santri_detail";

    private List<HealthRecord> currentSantriHealthRecords;
    private FragmentHealthRecordDetailBinding binding;

    public static HealthRecordDetailFragment newInstance(List<HealthRecord> santriData) {
        HealthRecordDetailFragment fragment = new HealthRecordDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SANTRI_DETAIL, (Serializable) santriData);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentSantriHealthRecords = (List<HealthRecord>) getArguments().getSerializable(ARG_SANTRI_DETAIL);
        }

        binding = FragmentHealthRecordDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HealthRecordAdapter healthAdapter = new HealthRecordAdapter(currentSantriHealthRecords, requireContext(), false, new HealthRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(HealthRecord clickedRecord) {
            }

            @Override
            public void onItemClick(HealthRecord clickedRecord) {
            }

        });

        binding.rvHealthRecord.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvHealthRecord.setAdapter(healthAdapter);

        if(currentSantriHealthRecords.isEmpty()){
            binding.tvKosong.setVisibility(View.VISIBLE);
            binding.rvHealthRecord.setVisibility(View.GONE);
        }else{
            binding.tvKosong.setVisibility(View.GONE);
            binding.rvHealthRecord.setVisibility(View.VISIBLE);
        }
    }
}