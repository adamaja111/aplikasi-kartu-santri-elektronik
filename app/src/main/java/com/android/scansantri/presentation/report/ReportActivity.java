package com.android.scansantri.presentation.report;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.scansantri.R;
import com.android.scansantri.data.model.BackupData;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.databinding.ActivityReportBinding;
import com.android.scansantri.presentation.LoadingDialog;
import com.android.scansantri.presentation.adapter.ReportSantriAdapter;
import com.android.scansantri.presentation.adapter.ReportSantriSelectionAdapter;
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
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private ActivityReportBinding binding;
    private MainViewModel mainViewModel;
    private ReportSantriSelectionAdapter adapter;
    private List<SantriData> allSantriList = new ArrayList<>();
    private boolean canClick = true;

    private final ActivityResultLauncher<String> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/json"),
            uri -> {
                if (uri != null) {
                    exportDatabase(uri);
                }
            }
    );

    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    showImportConfirmationDialog(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        MainViewModelFactory factory = new MainViewModelFactory(getApplication());
        mainViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        adapter = new ReportSantriSelectionAdapter(new ArrayList<>());
        binding.rvReportSantri.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReportSantri.setAdapter(adapter);

        mainViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) loadingDialog.startLoadingDialog();
            else loadingDialog.dismissDialog();
        });

        mainViewModel.getSantriList().observe(this, santriList -> {
            allSantriList = santriList;
            adapter.updateList(santriList);
        });

        binding.cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        binding.btnGenerateReport.setOnClickListener(v -> {
            List<SantriData> selected = adapter.getSelectedSantri();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Pilih minimal satu santri", Toast.LENGTH_SHORT).show();
            } else if (canClick) {
                generatePdfSantri(selected);
            }
        });

        binding.btnExportData.setOnClickListener(v -> {
            String fileName = "Backup_ScanSantri_" + System.currentTimeMillis() + ".json";
            exportLauncher.launch(fileName);
        });

        binding.btnImportData.setOnClickListener(v -> {
            importLauncher.launch(new String[]{"application/json", "application/octet-stream"});
        });
    }

    private void generatePdfSantri(List<SantriData> selectedSantriList) {
        canClick = false;
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        Resources r = getResources();
        int widthPx = Math.round(1240 * r.getDisplayMetrics().density + 0.5f);
        int heightPx = Math.round(1754 * r.getDisplayMetrics().density + 0.5f);

        final PdfDocument doc = new PdfDocument(this);
        doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT);
        doc.setRenderWidth(widthPx);
        doc.setRenderHeight(heightPx);
        doc.setSaveDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        doc.setFileName("LaporanQR_" + System.currentTimeMillis());
        doc.setInflateOnMainThread(true);

        int totalItems = selectedSantriList.size();
        int pages = (int) Math.ceil((double) totalItems / 5);

        for (int i = 0; i < pages; i++) {
            final int start = i * 5;
            final int end = Math.min(start + 5, totalItems);

            AbstractViewRenderer page = new AbstractViewRenderer(this, R.layout.santri_report_layout) {
                @Override
                protected void initView(View view) {
                    ListView listView = view.findViewById(R.id.lvSantri);
                    ArrayList<SantriData> sublist = new ArrayList<>(selectedSantriList.subList(start, end));
                    ReportSantriAdapter adapter = new ReportSantriAdapter(ReportActivity.this, 0, sublist);
                    listView.setAdapter(adapter);
                }
            };
            doc.addPage(page);
        }

        doc.setListener(new PdfDocument.Callback() {
            @Override
            public void onComplete(File file) {
                canClick = true;
                loadingDialog.dismissDialog();
                MediaScannerConnection.scanFile(ReportActivity.this, new String[]{file.getAbsolutePath()}, null, null);
                Toast.makeText(ReportActivity.this, "PDF Berhasil dibuat di folder Download", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                canClick = true;
                loadingDialog.dismissDialog();
                Toast.makeText(ReportActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        doc.createPdf(this);
    }

    private void exportDatabase(Uri uri) {
        try {
            BackupData backupData = new BackupData();
            backupData.setSantriList(allSantriList);
            backupData.setParentList(mainViewModel.getParentList().getValue());
            backupData.setOrganizationList(mainViewModel.getOrganizationList().getValue());
            backupData.setAchievementList(mainViewModel.getAchievementList().getValue());
            backupData.setHealthRecordList(mainViewModel.getHealthRecordList().getValue());
            backupData.setViolationList(mainViewModel.getViolationList().getValue());

            Gson gson = new Gson();
            String json = gson.toJson(backupData);

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(json.getBytes());
                outputStream.close();
                Toast.makeText(this, "Data berhasil diekspor!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Gagal ekspor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showImportConfirmationDialog(Uri uri) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Import")
                .setMessage("Peringatan: Semua data Cloud akan diganti dengan data dari file ini. Lanjutkan?")
                .setPositiveButton("Ya, Import", (dialog, which) -> importDatabase(uri))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void importDatabase(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) stringBuilder.append(line);
                inputStream.close();

                Gson gson = new Gson();
                BackupData backupData = gson.fromJson(stringBuilder.toString(), BackupData.class);

                if (backupData != null) {
                    mainViewModel.restoreDatabase(backupData);
                    Toast.makeText(this, "Data berhasil dipulihkan!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Gagal import: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
