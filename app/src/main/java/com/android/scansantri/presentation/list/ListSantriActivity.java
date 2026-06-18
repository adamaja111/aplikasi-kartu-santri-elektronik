package com.android.scansantri.presentation.list;

import static com.android.scansantri.helper.SantriDataFilterHelper.filterAchievementBySantri;
import static com.android.scansantri.helper.SantriDataFilterHelper.filterHealthRecordsBySantri;
import static com.android.scansantri.helper.SantriDataFilterHelper.filterOrganizationBySantri;
import static com.android.scansantri.helper.SantriDataFilterHelper.filterViolationsBySantri;
import static com.android.scansantri.helper.SantriDataFilterHelper.findParentDataBySantriId;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.scansantri.R;
import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.data.model.HealthRecord;
import com.android.scansantri.data.model.Organization;
import com.android.scansantri.data.model.ParentData;
import com.android.scansantri.data.model.SantriAchievement;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.data.model.SantriHealthRecord;
import com.android.scansantri.data.model.SantriOrganization;
import com.android.scansantri.data.model.SantriViolation;
import com.android.scansantri.data.model.Violation;
import com.android.scansantri.databinding.ActivityListSantriBinding;
import com.android.scansantri.databinding.DialogQrZoomBinding;
import com.android.scansantri.databinding.DialogSantriDetailBinding;
import com.android.scansantri.helper.SantriPDFGenerator;
import com.android.scansantri.presentation.LoadingDialog;
import com.android.scansantri.presentation.MainActivity;
import com.android.scansantri.presentation.adapter.ReportSantriAdapter;
import com.android.scansantri.presentation.adapter.SantriAdapter;
import com.android.scansantri.presentation.add.AddSantriActivity;
import com.android.scansantri.presentation.list.edit.EditSantriActivity;
import com.android.scansantri.presentation.scan.ScanActivity;
import com.android.scansantri.presentation.viewmodel.MainViewModel;
import com.android.scansantri.presentation.viewmodel.MainViewModelFactory;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListSantriActivity extends AppCompatActivity {

    public static final String ARG_SANTRI_NIS = "current_santri_detail";
    private static final int CREATE_PDF_REQUEST_CODE = 1001;
    private SantriData currentSantriForPDF;

    private MainViewModel mainViewModel;
    private SantriAdapter santriAdapter;
    private ActivityListSantriBinding binding;
    private List<ParentData> listParentSantri = Collections.emptyList();
    private List<SantriOrganization> listSemuaOrganisasi = Collections.emptyList();
    private List<SantriAchievement> listSemuaPrestasi = Collections.emptyList();
    private List<SantriHealthRecord> listSemuaHealth = Collections.emptyList();
    private List<SantriViolation> listSemuaPelanggaran = Collections.emptyList();
    private Boolean isOpened = false;
    private List<SantriData> listSemuaSantri = Collections.emptyList();
    private Boolean canClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListSantriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        LoadingDialog loadingDialog = new LoadingDialog(this);

        MainViewModelFactory factory = new MainViewModelFactory(getApplication());
        mainViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        santriAdapter = new SantriAdapter(new ArrayList<>(), this, santriClickListener);
        binding.rvListSantri.setAdapter(santriAdapter);
        binding.rvListSantri.setLayoutManager(new LinearLayoutManager(this));

        mainViewModel.getSantriList().observe(this, santriList -> {
            listSemuaSantri = santriList;
            santriAdapter.updateSantriList(santriList);
        });

        mainViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                loadingDialog.startLoadingDialog();
            } else {
                loadingDialog.dismissDialog();
            }
        });

        mainViewModel.getErrorMsg().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(ListSantriActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        mainViewModel.getParentList().observe(this, parentList -> {
            listParentSantri = parentList;
        });

        mainViewModel.getOrganizationList().observe(this, organisasiList -> {
            listSemuaOrganisasi = organisasiList;
        });

        mainViewModel.getAchievementList().observe(this, prestasiList -> {
            listSemuaPrestasi = prestasiList;
        });

        mainViewModel.getHealthRecordList().observe(this, healthList -> {
            listSemuaHealth = healthList;
        });

        mainViewModel.getViolationList().observe(this, violationList -> {
            listSemuaPelanggaran = violationList;
        });

        mainViewModel.isDataReady().observe(this, dataIsReady -> {
            if(dataIsReady){
                if(getIntent().getExtras()!=null){
                    Bundle bundle = getIntent().getExtras();
                    SantriData passedCurrentSantri = (SantriData) bundle.getSerializable(ARG_SANTRI_NIS);
                    if(passedCurrentSantri!=null && !isOpened){
                        isOpened = true;
                        openSantriDetailDialog(passedCurrentSantri);
                    }
                }else{
                    isOpened = true;
                }
            }
        });


        binding.btnAddSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListSantriActivity.this, AddSantriActivity.class);
                startActivity(intent);
            }
        });

        binding.btnScanSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListSantriActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        binding.btnHomeSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishThisPage();
            }
        });

        binding.btnReportSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick){
                    canClick = false;
                    generatePdfSantri();
                }
            }
        });
    }

    private void finishThisPage(){
        this.finish();
    }

    private void generatePdfSantri(){
        canClick = false;
        LoadingDialog loadingDialog = new LoadingDialog(ListSantriActivity.this);
        loadingDialog.startLoadingDialog();
        Resources r = getResources();
        int widthPx = Math.round(1240 * r.getDisplayMetrics().density + 0.5f);
        int heightPx = Math.round(1754 * r.getDisplayMetrics().density + 0.5f);
        final PdfDocument doc = new PdfDocument(ListSantriActivity.this);

        int totalItems = listSemuaSantri.size();
        int pages = (int) Math.ceil((double) totalItems / 5);

        for (int i = 0; i < pages; i++) {
            final int start = i * 5;
            final int end = Math.min(start + 5, totalItems);

            AbstractViewRenderer page = new AbstractViewRenderer(ListSantriActivity.this, R.layout.santri_report_layout) {
                @Override
                protected void initView(View view) {
                    ListView listView = view.findViewById(R.id.lvSantri);
                    ArrayList<SantriData> sublist = new ArrayList<>(listSemuaSantri.subList(start, end));
                    ReportSantriAdapter adapter = new ReportSantriAdapter(
                            ListSantriActivity.this, 0, sublist);
                    listView.setAdapter(adapter);
                }
            };

            doc.setRenderWidth(widthPx);
            doc.setRenderHeight(heightPx);
            doc.addPage(page);
        }

        doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT);
        doc.setProgressTitle(R.string.generating);
        doc.setProgressMessage(R.string.generating_pdf);
        doc.setSaveDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        doc.setInflateOnMainThread(true);

        doc.setListener(new PdfDocument.Callback() {
            @Override
            public void onComplete(File file) {
                canClick = true;
                loadingDialog.dismissDialog();
                MediaScannerConnection.scanFile(ListSantriActivity.this, new String[]{file.getAbsolutePath()}, null, null);

                Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete");
                Toast.makeText(ListSantriActivity.this, "Sukses tersimpan pada " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                canClick = true;
                loadingDialog.dismissDialog();
                Toast.makeText(ListSantriActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
            }
        });

        doc.setFileName("ListSantri" + System.currentTimeMillis());
        doc.createPdf(ListSantriActivity.this);
    }

    private final SantriAdapter.OnSantriClickListener santriClickListener = new SantriAdapter.OnSantriClickListener() {
        @Override
        public void onSantriClick(SantriData santri) {
            openSantriDetailDialog(santri);
        }

        @Override
        public void onZoomClick(SantriData santri) {
            openQrCodeDialog(santri);
        }
    };

    private void openQrCodeDialog(SantriData santri) {
        DialogQrZoomBinding dialogQrBinding = DialogQrZoomBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogQrBuilder = new AlertDialog.Builder(ListSantriActivity.this).setView(dialogQrBinding.getRoot());
        AlertDialog qrDialog = dialogQrBuilder.create();
        dialogQrBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrDialog.dismiss();
            }
        });

        dialogQrBinding.tvNamaSantri.setText(santri.getFullName());
        Glide.with(ListSantriActivity.this).load(santri.getBarcodeLink()).into(dialogQrBinding.ivQr);
        qrDialog.setCancelable(false);
        qrDialog.show();
    }

    private void openSantriDetailDialog(SantriData santri) {
        DialogSantriDetailBinding dialogSantriDetailBinding = DialogSantriDetailBinding.inflate(getLayoutInflater());
        Dialog santriDetailDialog = new Dialog(ListSantriActivity.this,  R.style.FullscreenDialog);
        santriDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        santriDetailDialog.setContentView(dialogSantriDetailBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(santriDetailDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        santriDetailDialog.getWindow().setAttributes(lp);
        santriDetailDialog.setCancelable(false);

        dialogSantriDetailBinding.tvNamaSantri.setText(santri.getFullName());
        dialogSantriDetailBinding.tvNisSantri.setText("NIS : "+ santri.getNis());
        dialogSantriDetailBinding.tvTtlSantri.setText(santri.getBirthPlace() + ", "+ santri.getBirthDate());
        dialogSantriDetailBinding.tvJenisKelamin.setText(santri.getGender());
        dialogSantriDetailBinding.tvNomorTelpon.setText(santri.getPhoneNumber());
        dialogSantriDetailBinding.tvEmail.setText(santri.getEmail());
        dialogSantriDetailBinding.tvAlamat.setText(santri.getAddress());

        dialogSantriDetailBinding.tvFacebook.setText((santri.getFacebook() != null || !santri.getFacebook().isBlank() ? santri.getFacebook() : "-"));
        dialogSantriDetailBinding.tvInstagram.setText((santri.getInstagram() != null || !santri.getInstagram().isBlank() ? santri.getInstagram() : "-"));
        dialogSantriDetailBinding.tvTwitter.setText((santri.getTwitter() != null || !santri.getTwitter().isBlank() ? santri.getTwitter() : "-"));

        int[] tabIcons = {
                R.drawable.img_parent_santri,
                R.drawable.img_organization,
                R.drawable.img_award,
                R.drawable.img_health,
                R.drawable.img_pelanggaran,
        };

        setupSantriViewPager(dialogSantriDetailBinding.santriDetailViewPager, santri);
        dialogSantriDetailBinding.santriDetailTabLayout.setTabIconTint(null);
        new TabLayoutMediator(dialogSantriDetailBinding.santriDetailTabLayout, dialogSantriDetailBinding.santriDetailViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setIcon(tabIcons[position]);
            }
        }).attach();

        dialogSantriDetailBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrentClicked(santri, santriDetailDialog);
            }
        });

        dialogSantriDetailBinding.btnDownloadSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exportSantriToPDF(santri);
            }
        });

        dialogSantriDetailBinding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditSantriActivity.ARG_SANTRI_DETAIL, santri);
                Intent intent = new Intent(ListSantriActivity.this, EditSantriActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        dialogSantriDetailBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                santriDetailDialog.dismiss();
            }
        });

        Glide.with(ListSantriActivity.this).load(santri.getBarcodeLink()).into(dialogSantriDetailBinding.ivQrSantri);
        santriDetailDialog.show();
    }

    private void setupSantriViewPager(ViewPager2 santriDetailViewPager, SantriData santriData) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        ParentData currentParentData = findParentDataBySantriId(listParentSantri, santriData.getUid());
        adapter.addFrag(currentParentData!=null ? ParentDetailFragment.newInstance(currentParentData): new ParentDetailFragment());
        adapter.addFrag(OrganizationsDetailFragment.newInstance(filterOrganizationBySantri(listSemuaOrganisasi, santriData.getUid())));
        adapter.addFrag(AchievementDetailFragment.newInstance(filterAchievementBySantri(listSemuaPrestasi, santriData.getUid())));
        adapter.addFrag(HealthRecordDetailFragment.newInstance(filterHealthRecordsBySantri(listSemuaHealth, santriData.getUid())));
        adapter.addFrag(ViolationDetailFragment.newInstance(filterViolationsBySantri(listSemuaPelanggaran, santriData.getUid())));
        santriDetailViewPager.setAdapter(adapter);
    }


    static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }

    private void deleteCurrentClicked(SantriData santri, Dialog santriDetailDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListSantriActivity.this);
        builder.setTitle("HAPUS");
        builder.setMessage("Anda yakin ingin menghapus santri ini?");

        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoadingDialog currentLoadingDialog = new LoadingDialog(ListSantriActivity.this);
                dialog.dismiss();
                currentLoadingDialog.startLoadingDialog();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference taskRef = rootRef.child("Santri");
                taskRef.child(santri.getUid()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                currentLoadingDialog.dismissDialog();
                                santriDetailDialog.dismiss();
                                makeToast("Santri berhasil dihapus");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                currentLoadingDialog.dismissDialog();
                                makeToast("Gagal menghapus santri, coba lagi nanti");
                            }
                        });
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void makeToast(String toastText){
        Toast.makeText(ListSantriActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }

    private void exportSantriToPDF(SantriData santri) {
        // Store current santri for later use in onActivityResult
        currentSantriForPDF = santri;

        // Generate filename
        String fileName = "Santri_" + santri.getFullName().replaceAll("[^a-zA-Z0-9]", "_") +
                "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";

        // Create intent to save file using SAF
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        try {
            startActivityForResult(intent, CREATE_PDF_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak dapat membuka file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                generatePDFToUri(currentSantriForPDF, uri);
            } else {
                Toast.makeText(this, "Gagal mendapatkan lokasi penyimpanan", Toast.LENGTH_SHORT).show();
            }
            // Clear the temporary data
            currentSantriForPDF = null;
        }
    }

    private void generatePDFToUri(SantriData santri, Uri uri) {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Membuat PDF");
        progressDialog.setMessage("Mempersiapkan dokumen...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        SantriPDFGenerator pdfGenerator = new SantriPDFGenerator(this);

        // Get related data
        ParentData currentParentData = findParentDataBySantriId(listParentSantri, santri.getUid());
        List<Organization> santriOrganizations = filterOrganizationBySantri(listSemuaOrganisasi, santri.getUid());
        List<Achievement> santriAchievements = filterAchievementBySantri(listSemuaPrestasi, santri.getUid());
        List<HealthRecord> santriHealthRecords = filterHealthRecordsBySantri(listSemuaHealth, santri.getUid());
        List<Violation> santriViolations = filterViolationsBySantri(listSemuaPelanggaran, santri.getUid());

        pdfGenerator.generateSantriPDF(
                santri,
                currentParentData,
                santriOrganizations,
                santriAchievements,
                santriHealthRecords,
                santriViolations,
                uri, // Pass the URI from SAF
                new SantriPDFGenerator.PdfGenerationCallback() {
                    @Override
                    public void onPdfGenerated(String fileName) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            showSuccessDialog("PDF berhasil dibuat!", fileName, uri);
                        });
                    }

                    @Override
                    public void onPdfGenerationFailed(String error) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            showErrorDialog("Gagal membuat PDF", error);
                        });
                    }

                    @Override
                    public void onProgressUpdate(String progress) {
                        runOnUiThread(() -> {
                            progressDialog.setMessage(progress);
                        });
                    }
                }
        );
    }

    private void showSuccessDialog(String title, String fileName, Uri uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("File PDF '" + fileName + "' telah berhasil disimpan di lokasi yang Anda pilih.");
        builder.setPositiveButton("Buka PDF", (dialog, which) -> {
            openPDFFile(uri);
        });
        builder.setNegativeButton("Tutup", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showErrorDialog(String title, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(error);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openPDFFile(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(intent, "Buka PDF dengan");
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak dapat membuka file PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}