package com.android.scansantri.presentation.list;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.scansantri.R;
import com.android.scansantri.data.model.Organization;
import com.android.scansantri.data.model.ParentData;
import com.android.scansantri.databinding.FragmentOrganizationsDetailBinding;
import com.android.scansantri.databinding.FragmentParentDetailBinding;
import com.android.scansantri.presentation.adapter.OrganizationAdapter;
import com.android.scansantri.presentation.add.AddSantriActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OrganizationsDetailFragment extends Fragment {

    private static final String ARG_SANTRI_DETAIL = "current_santri_detail";

    private List<Organization> currentSantriOrganization;
    private FragmentOrganizationsDetailBinding binding;

    public static OrganizationsDetailFragment newInstance(List<Organization> santriData) {
        OrganizationsDetailFragment fragment = new OrganizationsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SANTRI_DETAIL, (Serializable) santriData);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentSantriOrganization = (List<Organization>) getArguments().getSerializable(ARG_SANTRI_DETAIL);
        }

        binding = FragmentOrganizationsDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OrganizationAdapter organizationAdapter = new OrganizationAdapter(currentSantriOrganization, requireContext(), false, new OrganizationAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Organization clickedRecord) {

            }

            @Override
            public void onItemClick(Organization clickedRecord) {
            }

        });

        binding.rvOrganizations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrganizations.setAdapter(organizationAdapter);

        if(currentSantriOrganization.isEmpty()){
            binding.tvKosong.setVisibility(View.VISIBLE);
            binding.rvOrganizations.setVisibility(View.GONE);
        }else{
            binding.tvKosong.setVisibility(View.GONE);
            binding.rvOrganizations.setVisibility(View.VISIBLE);
        }
    }
}