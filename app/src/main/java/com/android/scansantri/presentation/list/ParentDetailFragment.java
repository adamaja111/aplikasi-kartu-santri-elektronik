package com.android.scansantri.presentation.list;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.scansantri.R;
import com.android.scansantri.data.model.FatherData;
import com.android.scansantri.data.model.MotherData;
import com.android.scansantri.data.model.ParentData;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.data.model.StepParentData;
import com.android.scansantri.databinding.FragmentParentDetailBinding;


public class ParentDetailFragment extends Fragment {

    private static final String ARG_SANTRI_DETAIL = "current_santri_detail";

    private ParentData currentParentSantri;
    private FragmentParentDetailBinding binding;


    public static ParentDetailFragment newInstance(ParentData santriData) {
        ParentDetailFragment fragment = new ParentDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SANTRI_DETAIL, santriData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                currentParentSantri = getArguments().getSerializable(ARG_SANTRI_DETAIL, ParentData.class);
            }else{
                currentParentSantri = (ParentData) getArguments().getSerializable(ARG_SANTRI_DETAIL);
            }
        }

        binding = FragmentParentDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(currentParentSantri !=null ){
            binding.cbxCerai.setChecked(currentParentSantri.isDivorced());
            if(currentParentSantri.getFatherData() != null){
                FatherData currentAyah = currentParentSantri.getFatherData();
                binding.tvNamaAyah.setText((currentAyah.getFatherName() != null || !currentAyah.getFatherName().isBlank() ? "Nama : "+currentAyah.getFatherName() : "-"));
                binding.tvPekerjaanAyah.setText((currentAyah.getFatherJob() != null || !currentAyah.getFatherJob().isBlank() ? currentAyah.getFatherJob() : "-"));
                binding.tvTelponAyah.setText((currentAyah.getFatherPhone() != null || !currentAyah.getFatherPhone().isBlank() ? currentAyah.getFatherPhone() : "-"));
            }

            if(currentParentSantri.getMotherData() != null){
                MotherData currentIbu = currentParentSantri.getMotherData();
                binding.tvNamaIbu.setText((currentIbu.getMotherName() != null || !currentIbu.getMotherName().isBlank() ? "Nama : "+currentIbu.getMotherName() : "-"));
                binding.tvPekerjaanIbu.setText((currentIbu.getMotherJob() != null || !currentIbu.getMotherJob().isBlank() ? currentIbu.getMotherJob() : "-"));
                binding.tvTelponIbu.setText((currentIbu.getMotherPhone() != null || !currentIbu.getMotherPhone().isBlank() ? currentIbu.getMotherPhone() : "-"));
            }

            if(currentParentSantri.getStepParentData() != null) {
                StepParentData currentTiri = currentParentSantri.getStepParentData();
                binding.tvNamaTiri.setText((currentTiri.getStepParentName() != null || !currentTiri.getStepParentName().isBlank() ? "Nama : "+currentTiri.getStepParentName() : "-"));
                binding.tvTelponTiri.setText((currentTiri.getStepParentPhone() != null || !currentTiri.getStepParentPhone().isBlank() ? currentTiri.getStepParentPhone() : "-"));
                binding.tvAlamatTiri.setText((currentTiri.getStepParentAddress() != null || !currentTiri.getStepParentAddress().isBlank() ? currentTiri.getStepParentAddress() : "-"));}
            }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}