package com.android.scansantri.presentation;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.scansantri.R;
import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.data.model.BackupData;
import com.android.scansantri.data.model.SantriAchievement;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.data.model.SantriHealthRecord;
import com.android.scansantri.data.model.SantriViolation;
import com.android.scansantri.data.model.Violation;
import com.android.scansantri.databinding.ActivityMainBinding;
import com.android.scansantri.helper.DateHelper;
import com.android.scansantri.presentation.adapter.ReportSantriAdapter;
import com.android.scansantri.presentation.add.AddSantriActivity;
import com.android.scansantri.presentation.list.ListSantriActivity;
import com.android.scansantri.presentation.report.ReportActivity;
import com.android.scansantri.presentation.scan.ScanActivity;
import com.android.scansantri.presentation.viewmodel.MainViewModel;
import com.android.scansantri.presentation.viewmodel.MainViewModelFactory;
import com.google.gson.Gson;
import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private List<SantriData> listSemuaSantri = Collections.emptyList();
    private Boolean canClick = true;
    private List<SantriAchievement> listSemuaPrestasi = Collections.emptyList();
    private List<SantriViolation> listSemuaPelanggaran = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        LoadingDialog loadingDialog = new LoadingDialog(this);

        MainViewModelFactory factory = new MainViewModelFactory(getApplication());
        mainViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        mainViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                loadingDialog.startLoadingDialog();
            } else {
                loadingDialog.dismissDialog();
            }
        });

        mainViewModel.getErrorMsg().observe(this, errorMsg -> {
            if (errorMsg != null) {
                makeToast(errorMsg);
            }
        });

        mainViewModel.getSantriList().observe(this, santriList -> {
            listSemuaSantri = santriList;
            binding.tvTotalSantri.setText(santriList.size() + "");
            tampilkanRekapTerbaru();
        });

        mainViewModel.getAchievementList().observe(this, prestasiList -> {
            listSemuaPrestasi = prestasiList;
            tampilkanRekapTerbaru();
        });

        mainViewModel.getViolationList().observe(this, violationList -> {
            listSemuaPelanggaran = violationList;
            tampilkanRekapTerbaru();
        });

        binding.btnAddSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddSantriActivity.class);
                startActivity(intent);
            }
        });

        binding.btnScanSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        binding.btnListSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListSantriActivity.class);
                startActivity(intent);
            }
        });

        binding.btnReportSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }

    private void makeToast(String toastText){
        Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }

    private void tampilkanRekapTerbaru() {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date startDate = calendar.getTime();

        Log.d("Rekap", "Mengambil data dari " + startDate + " hingga " + endDate);

        List<SantriWithCount> prestasiTertinggi = getTopSantriByCount(listSemuaPrestasi, listSemuaSantri, startDate, endDate, true);
        List<SantriWithCount> pelanggaranTertinggi = getTopSantriByCount(listSemuaPelanggaran, listSemuaSantri, startDate, endDate, false);
        List<SantriWithCount> pelanggaranTerendah = getBottomSantriByCount(listSemuaPelanggaran, listSemuaSantri, startDate, endDate, false);
        updatePrestasiUI(prestasiTertinggi);
        updatePerhatianUI(pelanggaranTertinggi);
        updatePerilakuUI(pelanggaranTerendah);
//        String toPrint = "";
//        Log.d("Rekap", "Top Prestasi:");
//        for (SantriWithCount swc : prestasiTertinggi) {
//            toPrint = toPrint+ "\n"+ "Top Prestasi:";
//            toPrint = toPrint+ "\n"+swc.santri.getFullName() + ": " + swc.count;
//            Log.d("Rekap", swc.santri.getFullName() + ": " + swc.count);
//        }
//
//        Log.d("Rekap", "Top Pelanggaran:");
//        for (SantriWithCount swc : pelanggaranTertinggi) {
//            toPrint = toPrint+ "\n"+ "Top Pelanggaran:";
//            toPrint = toPrint+ "\n"+swc.santri.getFullName() + ": " + swc.count;
//            Log.d("Rekap", swc.santri.getFullName() + ": " + swc.count);
//        }
//
//        Log.d("Rekap", "Bottom Pelanggaran:");
//        for (SantriWithCount swc : pelanggaranTerendah) {
//            toPrint = toPrint+ "\n"+ "Bottom Pelanggaran:";
//            toPrint = toPrint+ "\n"+swc.santri.getFullName() + ": " + swc.count;
//            Log.d("Rekap", swc.santri.getFullName() + ": " + swc.count);
//        }

    }

    private void updatePrestasiUI(List<SantriWithCount> prestasiTertinggi) {
        if (prestasiTertinggi.isEmpty()) {
            binding.layoutPrestasi.setVisibility(View.GONE);
            binding.layoutNoPrestasi.setVisibility(View.VISIBLE);
        } else {
            binding.layoutPrestasi.setVisibility(View.VISIBLE);
            binding.layoutNoPrestasi.setVisibility(View.GONE);

            binding.layoutPrestasiOne.setVisibility(View.VISIBLE);
            binding.layoutPrestasiTwo.setVisibility(View.GONE);
            binding.layoutPrestasiThree.setVisibility(View.GONE);

            if (!prestasiTertinggi.isEmpty()) {
                SantriWithCount first = prestasiTertinggi.get(0);
                binding.tvFirstPrestasiSantriNama.setText(first.santri.getFullName());
                binding.tvFirstPrestasiSantriNis.setText("NIS: " + first.santri.getNis());
                binding.tvCountPrestasiSantri.setText(String.valueOf(first.count));
            }

            if (prestasiTertinggi.size() > 1) {
                binding.layoutPrestasiTwo.setVisibility(View.VISIBLE);
                SantriWithCount second = prestasiTertinggi.get(1);
                binding.tvSecondPrestasiSantriNama.setText(second.santri.getFullName());
                binding.tvSecondPrestasiSantriNis.setText("NIS: " + second.santri.getNis());
                binding.tvCountPrestasiSantriSecond.setText(String.valueOf(second.count));
            }

            if (prestasiTertinggi.size() > 2) {
                binding.layoutPrestasiThree.setVisibility(View.VISIBLE);
                SantriWithCount third = prestasiTertinggi.get(2);
                binding.tvThirdPrestasiSantriNama.setText(third.santri.getFullName());
                binding.tvThirdPrestasiSantriNis.setText("NIS: " + third.santri.getNis());
                binding.tvCountPrestasiSantriThird.setText(String.valueOf(third.count));
            }
        }
    }

    private void updatePerhatianUI(List<SantriWithCount> pelanggaranTertinggi ) {
        if (pelanggaranTertinggi.isEmpty()) {
            binding.layoutPerhatian.setVisibility(View.GONE);
            binding.layoutNoPerhatian.setVisibility(View.VISIBLE);
        } else {
            binding.layoutPerhatian.setVisibility(View.VISIBLE);
            binding.layoutNoPerhatian.setVisibility(View.GONE);

            // Reset visibility
            binding.layoutPerhatianOne.setVisibility(View.VISIBLE);
            binding.layoutPerhatianTwo.setVisibility(View.GONE);
            binding.layoutPerhatianThree.setVisibility(View.GONE);

            // Isi data untuk item pertama
            if (!pelanggaranTertinggi.isEmpty()) {
                SantriWithCount first = pelanggaranTertinggi.get(0);
                binding.tvFirstPerhatianSantriNama.setText(first.santri.getFullName());
                binding.tvFirstPerhatianSantriNis.setText("NIS: " + first.santri.getNis());
                binding.tvCountPerhatianSantri.setText(String.valueOf(first.count));
            }

            // Isi data untuk item kedua jika ada
            if (pelanggaranTertinggi.size() > 1) {
                binding.layoutPerhatianTwo.setVisibility(View.VISIBLE);
                SantriWithCount second = pelanggaranTertinggi.get(1);
                binding.tvSecondPerhatianSantriNama.setText(second.santri.getFullName());
                binding.tvSecondPerhatianSantriNis.setText("NIS: " + second.santri.getNis());
                binding.tvCountPerhatianSantriSecond.setText(String.valueOf(second.count));
            }

            // Isi data untuk item ketiga jika ada
            if (pelanggaranTertinggi.size() > 2) {
                binding.layoutPerhatianThree.setVisibility(View.VISIBLE);
                SantriWithCount third = pelanggaranTertinggi.get(2);
                binding.tvThirdPerhatianSantriNama.setText(third.santri.getFullName());
                binding.tvThirdPerhatianSantriNis.setText("NIS: " + third.santri.getNis());
                binding.tvCountPerhatianSantriThird.setText(String.valueOf(third.count));
            }
        }
    }

    private void updatePerilakuUI(List<SantriWithCount> pelanggaranTerendah ) {
        // Reset visibility
        binding.layoutPerilakuOne.setVisibility(View.VISIBLE);
        binding.layoutPerilakuTwo.setVisibility(View.VISIBLE);
        binding.layoutPerilakuThree.setVisibility(View.VISIBLE);

        if (!pelanggaranTerendah.isEmpty()) {
            SantriWithCount first = pelanggaranTerendah.get(0); // Item dengan pelanggaran paling sedikit
            binding.tvFirstPerilakuSantriNama.setText(first.santri.getFullName());
            binding.tvFirstPerilakuSantriNis.setText("NIS: " + first.santri.getNis());
            binding.tvCountPerilakuSantri.setText(String.valueOf(first.count));
        }

        // Isi data untuk item kedua
        if (pelanggaranTerendah.size() > 1) {
            SantriWithCount second = pelanggaranTerendah.get(1);
            binding.tvSecondPerilakuSantriNama.setText(second.santri.getFullName());
            binding.tvSecondPerilakuSantriNis.setText("NIS: " + second.santri.getNis());
            binding.tvCountPerilakuSantriSecond.setText(String.valueOf(second.count));
        }

        // Isi data untuk item ketiga
        if (pelanggaranTerendah.size() > 2) {
            SantriWithCount third = pelanggaranTerendah.get(2);
            binding.tvThirdPerilakuSantriNama.setText(third.santri.getFullName());
            binding.tvThirdPerilakuSantriNis.setText("NIS: " + third.santri.getNis());
            binding.tvCountPerilakuSantriThird.setText(String.valueOf(third.count));
        }
    }

    private static class SantriWithCount {
        SantriData santri;
        int count;

        SantriWithCount(SantriData santri, int count) {
            this.santri = santri;
            this.count = count;
        }
    }

    private List<SantriWithCount> getTopSantriByCount(List<?> dataList, List<SantriData> santriList, Date startDate, Date endDate, boolean isAchievement) {
        Map<String, Integer> countMap = new HashMap<>();
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();

        for (Object item : dataList) {
            String santriUid;
            long eventDateInMillis;
            int pointsOrCount;

            if (isAchievement) {
                SantriAchievement sa = (SantriAchievement) item;
                Achievement a = sa.getAchievement();
                santriUid = sa.getSantriUid();
                eventDateInMillis = DateHelper.dateStringToLong(a.getAchievementDate());
                pointsOrCount = 1; // Untuk prestasi tetap menggunakan jumlah (count)
            } else { // Violation
                SantriViolation sv = (SantriViolation) item;
                Violation v = sv.getViolation();
                santriUid = sv.getSantriUid();
                eventDateInMillis = DateHelper.dateStringToLong(v.getViolationDate());
                pointsOrCount = v.getPoints(); // Untuk pelanggaran menggunakan total poin
            }

            if (eventDateInMillis >= startMillis && eventDateInMillis <= endMillis) {
                countMap.put(santriUid, countMap.getOrDefault(santriUid, 0) + pointsOrCount);
            }
        }

        List<SantriWithCount> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            String uid = entry.getKey();
            int count = entry.getValue();
            SantriData santri = findSantriByUid(uid, santriList);
            if (santri != null) {
                result.add(new SantriWithCount(santri, count));
            }
        }

        // Urutkan berdasarkan jumlah (count/points) descending
        result.sort((a, b) -> Integer.compare(b.count, a.count));

        // Ambil 3 teratas
        if (result.size() > 3) {
            result = result.subList(0, 3);
        }
        return result;
    }

    // Fungsi untuk mendapatkan santri dengan jumlah terendah (hanya untuk pelanggaran)
    private List<SantriWithCount> getBottomSantriByCount(List<SantriViolation> dataList, List<SantriData> santriList, Date startDate, Date endDate, boolean isAchievement) {
        // Karena isAchievement selalu false di sini, kita abaikan parameter itu
        Map<String, Integer> countMap = new HashMap<>();
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();

        for (SantriViolation sv : dataList) {
            Violation v = sv.getViolation();
            String santriUid = sv.getSantriUid();
            long eventDateInMillis = DateHelper.dateStringToLong(v.getViolationDate());

            if (eventDateInMillis >= startMillis && eventDateInMillis <= endMillis) {
                countMap.put(santriUid, countMap.getOrDefault(santriUid, 0) + v.getPoints());
            }
        }

        // Masukkan juga santri yang tidak punya pelanggaran dengan count 0
        for (SantriData santri : santriList) {
            if (!countMap.containsKey(santri.getUid())) {
                countMap.put(santri.getUid(), 0);
            }
        }

        List<SantriWithCount> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            String uid = entry.getKey();
            int count = entry.getValue();
            SantriData santri = findSantriByUid(uid, santriList);
            if (santri != null) {
                result.add(new SantriWithCount(santri, count));
            }
        }

        // Urutkan berdasarkan jumlah ascending
        result.sort((a, b) -> Integer.compare(a.count, b.count));

        // Ambil 3 terbawah
        if (result.size() > 3) {
            result = result.subList(0, 3);
        }
        return result;
    }


    // Helper untuk mencari SantriData berdasarkan UID
    private SantriData findSantriByUid(String uid, List<SantriData> santriList) {
        for (SantriData santri : santriList) {
            if (santri.getUid().equals(uid)) {
                return santri;
            }
        }
        return null; // Tidak ditemukan
    }
}