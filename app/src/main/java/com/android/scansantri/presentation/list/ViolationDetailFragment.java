package com.android.scansantri.presentation.list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.scansantri.data.model.Violation;
import com.android.scansantri.databinding.FragmentViolationDetailBinding;
import com.android.scansantri.presentation.adapter.ViolationAdapter;
import com.android.scansantri.presentation.add.AddSantriActivity;

import java.io.Serializable;
import java.util.List;


public class ViolationDetailFragment extends Fragment {

    private static final String ARG_SANTRI_DETAIL = "current_santri_detail";

    private List<Violation> currentSantriViolation;
    private FragmentViolationDetailBinding binding;

    public static ViolationDetailFragment newInstance(List<Violation> santriData) {
        ViolationDetailFragment fragment = new ViolationDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SANTRI_DETAIL, (Serializable) santriData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentSantriViolation = (List<Violation>) getArguments().getSerializable(ARG_SANTRI_DETAIL);
        }

        binding = FragmentViolationDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViolationAdapter violationAdapter = new ViolationAdapter(currentSantriViolation, requireContext(), false, new ViolationAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Violation clickedRecord) {

            }

            @Override
            public void onItemClick(Violation clickedRecord) {
            }

        });

        binding.rvViolation.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvViolation.setAdapter(violationAdapter);

        if(currentSantriViolation.isEmpty()){
            binding.tvKosong.setVisibility(View.VISIBLE);
            binding.rvViolation.setVisibility(View.GONE);
        }else{
            binding.tvKosong.setVisibility(View.GONE);
            binding.rvViolation.setVisibility(View.VISIBLE);
        }
    }
}