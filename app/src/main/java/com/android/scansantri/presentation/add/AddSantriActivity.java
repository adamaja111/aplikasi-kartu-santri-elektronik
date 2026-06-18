package com.android.scansantri.presentation.add;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.scansantri.R;
import com.android.scansantri.data.model.Achievement;
import com.android.scansantri.data.model.FatherData;
import com.android.scansantri.data.model.HealthRecord;
import com.android.scansantri.data.model.MotherData;
import com.android.scansantri.data.model.Organization;
import com.android.scansantri.data.model.ParentData;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.data.model.StepParentData;
import com.android.scansantri.data.model.Violation;
import com.android.scansantri.databinding.ActivityAddSantriBinding;
import com.android.scansantri.databinding.DialogFormAyahBinding;
import com.android.scansantri.databinding.DialogFormHealthBinding;
import com.android.scansantri.databinding.DialogFormIbuBinding;
import com.android.scansantri.databinding.DialogFormOrganizationBinding;
import com.android.scansantri.databinding.DialogFormPelanggaranBinding;
import com.android.scansantri.databinding.DialogFormPrestasiBinding;
import com.android.scansantri.databinding.DialogFormTiriBinding;
import com.android.scansantri.helper.DateHelper;
import com.android.scansantri.presentation.LoadingDialog;
import com.android.scansantri.presentation.MainActivity;
import com.android.scansantri.presentation.adapter.AchievementAdapter;
import com.android.scansantri.presentation.adapter.HealthRecordAdapter;
import com.android.scansantri.presentation.adapter.OrganizationAdapter;
import com.android.scansantri.presentation.adapter.ViolationAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddSantriActivity extends AppCompatActivity {

    private ActivityAddSantriBinding binding;
    private String selectedTanggalLahir = "";
    private FatherData dataAyahSekarang = null;
    private MotherData dataIbuSekarang = null;
    private StepParentData dataOrtuTiriSekarang = null;
    private ArrayList<HealthRecord> dataCurrentHealthRecord = new ArrayList<>();
    private HealthRecordAdapter healthAdapter;

    private ArrayList<Organization> dataCurrentOrganizations = new ArrayList<>();
    private OrganizationAdapter organizationAdapter;

    private ArrayList<Achievement> dataCurrentAchievements = new ArrayList<>();
    private AchievementAdapter achievementAdapter;

    private ArrayList<Violation> dataCurrentViolations = new ArrayList<>();
    private ViolationAdapter violationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSantriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        healthAdapter = new HealthRecordAdapter(dataCurrentHealthRecord, this, true, new HealthRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(HealthRecord clickedRecord) {
                deleteCurrentClicked(new HapusData() {
                    @Override
                    public void hapusDataSekarang() {
                        dataCurrentHealthRecord.remove(clickedRecord);
                        healthAdapter.updateSubmitData(dataCurrentHealthRecord);
                    }
                });
            }

            @Override
            public void onItemClick(HealthRecord clickedRecord) {
                handleHealthForm(clickedRecord);
            }

        });

        binding.rvKesehatan.setLayoutManager(new LinearLayoutManager(this));
        binding.rvKesehatan.setAdapter(healthAdapter);

        organizationAdapter = new OrganizationAdapter(dataCurrentOrganizations, this, true, new OrganizationAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Organization clickedRecord) {
                deleteCurrentClicked(new HapusData() {
                    @Override
                    public void hapusDataSekarang() {
                        dataCurrentOrganizations.remove(clickedRecord);
                        organizationAdapter.updateSubmitData(dataCurrentOrganizations);
                    }
                });
            }

            @Override
            public void onItemClick(Organization clickedRecord) {
                handleOrganizationForm(clickedRecord);
            }

        });

        binding.rvOrganization.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrganization.setAdapter(organizationAdapter);

        achievementAdapter = new AchievementAdapter(dataCurrentAchievements, this, true, new AchievementAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Achievement clickedRecord) {
                deleteCurrentClicked(new HapusData() {
                    @Override
                    public void hapusDataSekarang() {
                        dataCurrentAchievements.remove(clickedRecord);
                        achievementAdapter.updateAchievementList(dataCurrentAchievements);
                    }
                });
            }

            @Override
            public void onItemClick(Achievement clickedRecord) {
                handlePrestasiForm(clickedRecord);
            }

        });

        binding.rvPrestasi.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvPrestasi.setAdapter(achievementAdapter);

        violationAdapter = new ViolationAdapter(dataCurrentViolations, this, true, new ViolationAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Violation clickedRecord) {
                deleteCurrentClicked(new HapusData() {
                    @Override
                    public void hapusDataSekarang() {
                        dataCurrentViolations.remove(clickedRecord);
                        violationAdapter.updateViolationList(dataCurrentViolations);
                    }
                });
            }

            @Override
            public void onItemClick(Violation clickedRecord) {
                handlePelanggaranForm(clickedRecord);
            }

        });

        binding.rvPelanggaran.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPelanggaran.setAdapter(violationAdapter);

        binding.etTanggalLahir.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTanggalLahirPicker();
            }
        });

        binding.cbxCerai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                   binding.layoutOrtuCerai.setVisibility(View.VISIBLE);
                }else{
                    binding.layoutOrtuCerai.setVisibility(View.GONE);
                }
            }
        });

        binding.btnBukaFormAyah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAyahForm();
            }
        });

        binding.btnBukaFormIbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleIbuForm();
            }
        });

        binding.layoutOrtuCerai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTiriForm();
            }
        });

        binding.btnAddHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleHealthForm(null);
            }
        });

        binding.btnAddOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOrganizationForm(null);
            }
        });

        binding.btnAddPelanggaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePelanggaranForm(null);
            }
        });

        binding.btnAddPrestasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePrestasiForm(null);
            }
        });

        LoadingDialog loadingDialog = new LoadingDialog(AddSantriActivity.this);

        binding.btnTambahSantri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etNis.setError(null);
                binding.etNama.setError(null);
                binding.etTempatLahir.setError(null);
                binding.etTanggalLahir.setError(null);
                binding.etAlamatLengkap.setError(null);
                binding.etNomorTelpon.setError(null);
                binding.etEmail.setError(null);
                if(binding.etNis.getEditText().getText() == null || binding.etNis.getEditText().getText().toString().isEmpty()){
                    binding.etNis.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etNis.requestFocus();
                }else if(binding.etNama.getEditText().getText() == null || binding.etNama.getEditText().getText().toString().isEmpty()){
                    binding.etNama.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etNama.requestFocus();
                }else if(binding.etTempatLahir.getEditText().getText() == null || binding.etTempatLahir.getEditText().getText().toString().isEmpty()){
                    binding.etTempatLahir.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etTempatLahir.requestFocus();
                } else if(binding.etTanggalLahir.getEditText().getText() == null || binding.etTanggalLahir.getEditText().getText().toString().isEmpty()){
                    binding.etTanggalLahir.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etTanggalLahir.requestFocus();
                }else if (binding.radioJenisKelamin.getCheckedRadioButtonId() == -1) {
                    makeToast("Pilih Jenis Kelamin terlebih dahulu");
                }else if(binding.etAlamatLengkap.getEditText().getText() == null || binding.etAlamatLengkap.getEditText().getText().toString().isEmpty()){
                    binding.etAlamatLengkap.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etAlamatLengkap.requestFocus();
                }else if(binding.etNomorTelpon.getEditText().getText() == null || binding.etNomorTelpon.getEditText().getText().toString().isEmpty()){
                    binding.etNomorTelpon.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etNomorTelpon.requestFocus();
                } else if(binding.etEmail.getEditText().getText() == null || binding.etEmail.getEditText().getText().toString().isEmpty()){
                    binding.etEmail.setError(getString(R.string.field_cant_be_empty_error));
                    binding.etEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getEditText().getText().toString()).matches()){
                    binding.etEmail.setError(getString(R.string.email_must_valid));
                    binding.etEmail.requestFocus();
                }else{
                    loadingDialog.startLoadingDialog();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference taskRef = rootRef.child("Santri");
                    String key = taskRef.push().getKey();
                    String nis = binding.etNis.getEditText().getText().toString();
                    String nama = binding.etNama.getEditText().getText().toString();
                    String tempatLahir = binding.etTempatLahir.getEditText().getText().toString();
                    String tanggalLahir = binding.etTanggalLahir.getEditText().getText().toString();
                    String alamatLengkap = binding.etAlamatLengkap.getEditText().getText().toString();
                    String nomorTelpon = binding.etNomorTelpon.getEditText().getText().toString();
                    RadioButton selectedRadioButton = findViewById(binding.radioJenisKelamin.getCheckedRadioButtonId());
                    String jenisKelamin = selectedRadioButton.getText().toString();
                    String email = binding.etEmail.getEditText().getText().toString();

                    String facebook = binding.etFacebook.getEditText().getText().toString();
                    String instagram = binding.etInstagram.getEditText().getText().toString();
                    String twitter = binding.etTwitter.getEditText().getText().toString();

                    makeToast("Generating QR");
                    String filePath = generateQRCode(nis);
                    if(filePath.isEmpty()){
                        loadingDialog.dismissDialog();
                    }else{
                        makeToast("Mengupload gambar QR...");
                        File barcodeImage = new File(filePath);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference().child("santriBarcode/" + key);

                        storageRef.putFile(Uri.fromFile(barcodeImage))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        makeToast("Gambar barcode berhasil diupload");
                                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String barcodeLinkStorage = uri.toString();
                                                        SantriData currentSantri = new SantriData(
                                                                key, nis, nama, tempatLahir, tanggalLahir, jenisKelamin, alamatLengkap,
                                                                nomorTelpon, email, barcodeLinkStorage, facebook, instagram, twitter
                                                        );

                                                        taskRef.child(key)
                                                                .setValue(currentSantri)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
//                                                                            loadingDialog.dismissDialog();
                                                                            if(dataAyahSekarang!=null || dataIbuSekarang != null || (dataOrtuTiriSekarang!=null && binding.cbxCerai.isChecked()) ||
                                                                                    !dataCurrentOrganizations.isEmpty() || !dataCurrentAchievements.isEmpty() || !dataCurrentHealthRecord.isEmpty() || !dataCurrentViolations.isEmpty())
                                                                            {

                                                                                List<Task<Void>> tasks = new ArrayList<>();

                                                                                if(dataAyahSekarang!=null || dataIbuSekarang != null || (dataOrtuTiriSekarang!=null && binding.cbxCerai.isChecked())){
                                                                                    DatabaseReference parentRef = rootRef.child("SantriParent");
                                                                                    String parentKey = parentRef.push().getKey();
                                                                                    ParentData currentSantriParentData = new ParentData(key, parentKey, dataAyahSekarang, dataIbuSekarang, binding.cbxCerai.isChecked(),
                                                                                            dataOrtuTiriSekarang);
                                                                                    tasks.add(parentRef.child(parentKey)
                                                                                            .setValue(currentSantriParentData));
                                                                                }

                                                                                if(!dataCurrentOrganizations.isEmpty()){
                                                                                    for(Organization org: dataCurrentOrganizations){
                                                                                        org.setSantriId(key);
                                                                                    }
                                                                                    DatabaseReference parentRef = rootRef.child("SantriOrganizations");
                                                                                    tasks.add(parentRef.child(key)
                                                                                            .setValue(dataCurrentOrganizations));
                                                                                }

                                                                                if(!dataCurrentAchievements.isEmpty()){
                                                                                    for(Achievement org: dataCurrentAchievements){
                                                                                        org.setSantriId(key);
                                                                                    }
                                                                                    DatabaseReference parentRef = rootRef.child("SantriAchievements");
                                                                                    tasks.add(parentRef.child(key)
                                                                                            .setValue(dataCurrentAchievements));
                                                                                }

                                                                                if(!dataCurrentHealthRecord.isEmpty()){
                                                                                    for(HealthRecord org: dataCurrentHealthRecord){
                                                                                        org.setSantriId(key);
                                                                                    }
                                                                                    DatabaseReference parentRef = rootRef.child("SantriHealthRecord");
                                                                                    tasks.add(parentRef.child(key)
                                                                                            .setValue(dataCurrentHealthRecord));
                                                                                }

                                                                                if(!dataCurrentViolations.isEmpty()){
                                                                                    for(Violation org: dataCurrentViolations){
                                                                                        org.setSantriId(key);
                                                                                    }
                                                                                    DatabaseReference parentRef = rootRef.child("SantriViolations");
                                                                                    tasks.add(parentRef.child(key)
                                                                                            .setValue(dataCurrentViolations));
                                                                                }

                                                                                Tasks.whenAllComplete(tasks).addOnCompleteListener(allTask -> {
                                                                                    if (allTask.isSuccessful()) {
                                                                                        loadingDialog.dismissDialog();
                                                                                        santriBerhasilDitambahkan();
                                                                                    } else {
                                                                                        makeToast("Gagal menambahkan data, coba lagi.");
                                                                                    }
                                                                                });
                                                                            }else{
                                                                                loadingDialog.dismissDialog();
                                                                                santriBerhasilDitambahkan();
                                                                            }
                                                                        } else {
                                                                            loadingDialog.dismissDialog();
                                                                            makeToast(task.getException().getMessage());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        loadingDialog.dismissDialog();
                                                        makeToast(e.getMessage());
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.dismissDialog();
                                        makeToast(e.getMessage());
                                    }
                                });
                    }
                }
            }
        });
    }

    private void santriBerhasilDitambahkan() {
        makeToast("Santri berhasil ditambahkan.");
        Intent intent = new Intent(AddSantriActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void showTanggalLahirPicker() {
        final Calendar calendar = Calendar.getInstance();

        if (selectedTanggalLahir != null && !selectedTanggalLahir.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(selectedTanggalLahir);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddSantriActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        selectedTanggalLahir = DateHelper.longToDateString(selectedDate.getTimeInMillis());
                        binding.etTanggalLahir.getEditText().setText(selectedTanggalLahir);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    public void showDatePickerUmum(@Nullable String currentTime, OnDateSelectedListener listener) {
        final Calendar calendar = Calendar.getInstance();

        if (currentTime != null && !currentTime.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(currentTime);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddSantriActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        listener.onDateSelected(DateHelper.longToDateString(selectedDate.getTimeInMillis()));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String dateFormat);
    }

    private void handleAyahForm(){
        DialogFormAyahBinding dialogAyahView = DialogFormAyahBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogAyahForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogAyahView.getRoot());
        AlertDialog ayahDialog = dialogAyahForm.create();

        if(dataAyahSekarang!=null){
            dialogAyahView.etNamaAyah.getEditText().setText(dataAyahSekarang.getFatherName());
            dialogAyahView.etPekerjaanAyah.getEditText().setText(dataAyahSekarang.getFatherJob());
            dialogAyahView.etTelponAyah.getEditText().setText(dataAyahSekarang.getFatherPhone());
        }

        dialogAyahView.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAyahView.etNamaAyah.setError(null);
                dialogAyahView.etPekerjaanAyah.setError(null);
                dialogAyahView.etTelponAyah.setError(null);
                if(dialogAyahView.etNamaAyah.getEditText().getText() == null ||dialogAyahView.etNamaAyah.getEditText().getText().toString().isEmpty()){
                    dialogAyahView.etNamaAyah.setError(getString(R.string.field_cant_be_empty_error));
                    dialogAyahView.etNamaAyah.requestFocus();
                }else if(dialogAyahView.etPekerjaanAyah.getEditText().getText() == null ||dialogAyahView.etPekerjaanAyah.getEditText().getText().toString().isEmpty()){
                    dialogAyahView.etPekerjaanAyah.setError(getString(R.string.field_cant_be_empty_error));
                    dialogAyahView.etPekerjaanAyah.requestFocus();
                }else if(dialogAyahView.etTelponAyah.getEditText().getText() == null ||dialogAyahView.etTelponAyah.getEditText().getText().toString().isEmpty()){
                    dialogAyahView.etTelponAyah.setError(getString(R.string.field_cant_be_empty_error));
                    dialogAyahView.etTelponAyah.requestFocus();
                }else{
                    String namaAyah = dialogAyahView.etNamaAyah.getEditText().getText().toString();
                    String pekerjaanAyah = dialogAyahView.etPekerjaanAyah.getEditText().getText().toString();
                    String telponAyah = dialogAyahView.etTelponAyah.getEditText().getText().toString();
                    FatherData dataAyahBaru = new FatherData("","",namaAyah, pekerjaanAyah, telponAyah);
                    makeToast("Data ayah berhasil diupdate");
                    dataAyahSekarang = dataAyahBaru;
                    updateDataAyah();
                    ayahDialog.dismiss();
                }
            }
        });

        ayahDialog.show();
    }

    public interface HapusData {
        void hapusDataSekarang();
    }

    private void deleteCurrentClicked(HapusData hapusData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSantriActivity.this);
        builder.setTitle("HAPUS");
        builder.setMessage("Anda yakin ingin menghapus data ini?");

        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                hapusData.hapusDataSekarang();
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

    private void updateDataAyah(){
        binding.layoutAyah.setVisibility(View.VISIBLE);
        binding.tvBelumInputAyah.setVisibility(View.GONE);
        binding.tvNamaAyah.setText(dataAyahSekarang.getFatherName());
        binding.tvPekerjaanAyah.setText(dataAyahSekarang.getFatherJob());
        binding.tvTelponAyah.setText(dataAyahSekarang.getFatherPhone());
    }

    private void handleIbuForm(){
        DialogFormIbuBinding dialogIbuBinding = DialogFormIbuBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogIbuForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogIbuBinding.getRoot());
        AlertDialog ibuDialog = dialogIbuForm.create();

        if(dataIbuSekarang!=null){
            dialogIbuBinding.etNamaIbu.getEditText().setText(dataIbuSekarang.getMotherName());
            dialogIbuBinding.etPekerjaanIbu.getEditText().setText(dataIbuSekarang.getMotherJob());
            dialogIbuBinding.etTelponIbu.getEditText().setText(dataIbuSekarang.getMotherPhone());
        }

        dialogIbuBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogIbuBinding.etNamaIbu.setError(null);
                dialogIbuBinding.etPekerjaanIbu.setError(null);
                dialogIbuBinding.etTelponIbu.setError(null);
                if(dialogIbuBinding.etNamaIbu.getEditText().getText() == null ||dialogIbuBinding.etNamaIbu.getEditText().getText().toString().isEmpty()){
                    dialogIbuBinding.etNamaIbu.setError(getString(R.string.field_cant_be_empty_error));
                    dialogIbuBinding.etNamaIbu.requestFocus();
                }else if(dialogIbuBinding.etPekerjaanIbu.getEditText().getText() == null ||dialogIbuBinding.etPekerjaanIbu.getEditText().getText().toString().isEmpty()){
                    dialogIbuBinding.etPekerjaanIbu.setError(getString(R.string.field_cant_be_empty_error));
                    dialogIbuBinding.etPekerjaanIbu.requestFocus();
                }else if(dialogIbuBinding.etTelponIbu.getEditText().getText() == null ||dialogIbuBinding.etTelponIbu.getEditText().getText().toString().isEmpty()){
                    dialogIbuBinding.etTelponIbu.setError(getString(R.string.field_cant_be_empty_error));
                    dialogIbuBinding.etTelponIbu.requestFocus();
                }else{
                    String namaIbu = dialogIbuBinding.etNamaIbu.getEditText().getText().toString();
                    String pekerjaanIbu = dialogIbuBinding.etPekerjaanIbu.getEditText().getText().toString();
                    String telponIbu = dialogIbuBinding.etTelponIbu.getEditText().getText().toString();
                    MotherData dataIbuBaru = new MotherData("","",namaIbu, pekerjaanIbu, telponIbu);
                    makeToast("Data ibu berhasil diupdate");
                    dataIbuSekarang = dataIbuBaru;
                    updateDataIbu();
                    ibuDialog.dismiss();
                }
            }
        });

        ibuDialog.show();
    }

    private void updateDataIbu(){
        binding.layoutIbu.setVisibility(View.VISIBLE);
        binding.tvBelumInputIbu.setVisibility(View.GONE);
        binding.tvNamaIbu.setText(dataIbuSekarang.getMotherName());
        binding.tvPekerjaanIbu.setText(dataIbuSekarang.getMotherJob());
        binding.tvTelponIbu.setText(dataIbuSekarang.getMotherPhone());
    }

    private void handleTiriForm(){
        DialogFormTiriBinding dialogTiriBinding = DialogFormTiriBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogTiriForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogTiriBinding.getRoot());
        AlertDialog tiriDialog = dialogTiriForm.create();

        if(dataOrtuTiriSekarang!=null){
            dialogTiriBinding.etNamaTiri.getEditText().setText(dataOrtuTiriSekarang.getStepParentName());
            dialogTiriBinding.etAlamatTiri.getEditText().setText(dataOrtuTiriSekarang.getStepParentAddress());
            dialogTiriBinding.etTelponTiri.getEditText().setText(dataOrtuTiriSekarang.getStepParentPhone());
        }

        dialogTiriBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTiriBinding.etNamaTiri.setError(null);
                dialogTiriBinding.etAlamatTiri.setError(null);
                dialogTiriBinding.etTelponTiri.setError(null);
                if(dialogTiriBinding.etNamaTiri.getEditText().getText() == null ||dialogTiriBinding.etNamaTiri.getEditText().getText().toString().isEmpty()){
                    dialogTiriBinding.etNamaTiri.setError(getString(R.string.field_cant_be_empty_error));
                    dialogTiriBinding.etNamaTiri.requestFocus();
                }else if(dialogTiriBinding.etTelponTiri.getEditText().getText() == null ||dialogTiriBinding.etTelponTiri.getEditText().getText().toString().isEmpty()){
                    dialogTiriBinding.etTelponTiri.setError(getString(R.string.field_cant_be_empty_error));
                    dialogTiriBinding.etTelponTiri.requestFocus();
                }else if(dialogTiriBinding.etAlamatTiri.getEditText().getText() == null ||dialogTiriBinding.etAlamatTiri.getEditText().getText().toString().isEmpty()){
                    dialogTiriBinding.etAlamatTiri.setError(getString(R.string.field_cant_be_empty_error));
                    dialogTiriBinding.etAlamatTiri.requestFocus();
                }else{
                    String namaTiri = dialogTiriBinding.etNamaTiri.getEditText().getText().toString();
                    String alamatTiri = dialogTiriBinding.etAlamatTiri.getEditText().getText().toString();
                    String telponTiri = dialogTiriBinding.etTelponTiri.getEditText().getText().toString();
                    StepParentData dataTiriBaru = new StepParentData("","",namaTiri, telponTiri, alamatTiri);
                    makeToast("Data ortu tiri berhasil diupdate");
                    dataOrtuTiriSekarang = dataTiriBaru;
                    updateDataTiri();
                    tiriDialog.dismiss();
                }
            }
        });

        tiriDialog.show();
    }

    private void updateDataTiri(){
        binding.layoutOrtuTiri.setVisibility(View.VISIBLE);
        binding.tvBelumInputTiri.setVisibility(View.GONE);
        binding.tvNamaTiri.setText(dataOrtuTiriSekarang.getStepParentName());
        binding.tvTelponTiri.setText(dataOrtuTiriSekarang.getStepParentPhone());
        binding.tvAlamatTiri.setText(dataOrtuTiriSekarang.getStepParentAddress());
    }

    private void handleHealthForm(@Nullable HealthRecord dataHealthSekarang){
        DialogFormHealthBinding dialogHealthBinding = DialogFormHealthBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogHealthForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogHealthBinding.getRoot());
        AlertDialog healthDialog = dialogHealthForm.create();

        if(dataHealthSekarang!=null){
            dialogHealthBinding.etNamaPenyakit.getEditText().setText(dataHealthSekarang.getDisease());
            dialogHealthBinding.etTglPemeriksaan.getEditText().setText(dataHealthSekarang.getCheckupDate());
            dialogHealthBinding.etPenanganan.getEditText().setText(dataHealthSekarang.getTreatment());

            dialogHealthBinding.etTglPemeriksaan.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(dataHealthSekarang.getCheckupDate(), new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogHealthBinding.etTglPemeriksaan.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

            if(dataHealthSekarang.getAdditionalNotes()!=null)dialogHealthBinding.etCatatanPenyakit.getEditText().setText(dataHealthSekarang.getAdditionalNotes());
        }else {
            dialogHealthBinding.etTglPemeriksaan.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(null, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogHealthBinding.etTglPemeriksaan.getEditText().setText(dateFormat);
                        }
                    });
                }
            });
        }


        dialogHealthBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHealthBinding.etNamaPenyakit.setError(null);
                dialogHealthBinding.etTglPemeriksaan.setError(null);
                dialogHealthBinding.etPenanganan.setError(null);
                dialogHealthBinding.etCatatanPenyakit.setError(null);
                if(dialogHealthBinding.etNamaPenyakit.getEditText().getText() == null ||dialogHealthBinding.etNamaPenyakit.getEditText().getText().toString().isEmpty()){
                    dialogHealthBinding.etNamaPenyakit.setError(getString(R.string.field_cant_be_empty_error));
                    dialogHealthBinding.etNamaPenyakit.requestFocus();
                }else if(dialogHealthBinding.etTglPemeriksaan.getEditText().getText() == null ||dialogHealthBinding.etTglPemeriksaan.getEditText().getText().toString().isEmpty()){
                    dialogHealthBinding.etTglPemeriksaan.setError(getString(R.string.field_cant_be_empty_error));
                    dialogHealthBinding.etTglPemeriksaan.requestFocus();
                }else if(dialogHealthBinding.etPenanganan.getEditText().getText() == null ||dialogHealthBinding.etPenanganan.getEditText().getText().toString().isEmpty()){
                    dialogHealthBinding.etPenanganan.setError(getString(R.string.field_cant_be_empty_error));
                    dialogHealthBinding.etPenanganan.requestFocus();
                }else{
                    String namaPenyakit = dialogHealthBinding.etNamaPenyakit.getEditText().getText().toString();
                    String tanggalPemeriksaan = dialogHealthBinding.etTglPemeriksaan.getEditText().getText().toString();
                    String penanganan = dialogHealthBinding.etPenanganan.getEditText().getText().toString();
                    String catatan = dialogHealthBinding.etCatatanPenyakit.getEditText().getText().toString();
                    HealthRecord dataHealthBaru = new HealthRecord("","",tanggalPemeriksaan, namaPenyakit, penanganan, catatan);
                    if(dataHealthSekarang!=null) {
                        dataHealthBaru.setHealthRecordId(dataHealthSekarang.getHealthRecordId());
                        dataCurrentHealthRecord.removeIf(n -> (Objects.equals(n.getHealthRecordId(), dataHealthBaru.getHealthRecordId())));
                        dataCurrentHealthRecord.add(dataHealthBaru);
                    }else{
                        dataHealthBaru.setHealthRecordId(""+dataCurrentHealthRecord.size());
                        dataCurrentHealthRecord.add(dataHealthBaru);
                    }
//                    updateDataHealth();
                    healthAdapter.updateSubmitData(dataCurrentHealthRecord);
                    makeToast("Data kesehatan berhasil diupdate");
                    healthDialog.dismiss();
                }
            }
        });

        healthDialog.show();
    }

    private void handleOrganizationForm(@Nullable Organization dataOrganisasiSekarang){
        DialogFormOrganizationBinding dialogOrganizationBinding = DialogFormOrganizationBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogOrganisasiForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogOrganizationBinding.getRoot());
        AlertDialog organisasiDialog = dialogOrganisasiForm.create();

        if(dataOrganisasiSekarang!=null){
            dialogOrganizationBinding.etNamaOrganisasi.getEditText().setText(dataOrganisasiSekarang.getOrganizationName());
            dialogOrganizationBinding.etJabatan.getEditText().setText(dataOrganisasiSekarang.getPosition());
            dialogOrganizationBinding.etTglMasuk.getEditText().setText(dataOrganisasiSekarang.getStartDate());


            dialogOrganizationBinding.etTglMasuk.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(dataOrganisasiSekarang.getStartDate(), new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogOrganizationBinding.etTglMasuk.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

            if(dataOrganisasiSekarang.getEndDate()!=null || !dataOrganisasiSekarang.getEndDate().isEmpty()) {
                dialogOrganizationBinding.etTglSelesai.getEditText().setText(dataOrganisasiSekarang.getEndDate());

                dialogOrganizationBinding.etTglSelesai.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePickerUmum(dataOrganisasiSekarang.getEndDate(), new OnDateSelectedListener() {
                            @Override
                            public void onDateSelected(String dateFormat) {
                                dialogOrganizationBinding.etTglSelesai.getEditText().setText(dateFormat);
                            }
                        });
                    }
                });
            }else{
                dialogOrganizationBinding.etTglSelesai.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePickerUmum(null, new OnDateSelectedListener() {
                            @Override
                            public void onDateSelected(String dateFormat) {
                                dialogOrganizationBinding.etTglSelesai.getEditText().setText(dateFormat);
                            }
                        });
                    }
                });
            }


        }else {
            dialogOrganizationBinding.etTglSelesai.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(null, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogOrganizationBinding.etTglSelesai.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

            dialogOrganizationBinding.etTglMasuk.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(null, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogOrganizationBinding.etTglMasuk.getEditText().setText(dateFormat);
                        }
                    });
                }
            });
        }


        dialogOrganizationBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOrganizationBinding.etNamaOrganisasi.setError(null);
                dialogOrganizationBinding.etJabatan.setError(null);
                dialogOrganizationBinding.etTglMasuk.setError(null);
                if(dialogOrganizationBinding.etNamaOrganisasi.getEditText().getText() == null ||dialogOrganizationBinding.etNamaOrganisasi.getEditText().getText().toString().isEmpty()){
                    dialogOrganizationBinding.etNamaOrganisasi.setError(getString(R.string.field_cant_be_empty_error));
                    dialogOrganizationBinding.etNamaOrganisasi.requestFocus();
                }else if(dialogOrganizationBinding.etJabatan.getEditText().getText() == null ||dialogOrganizationBinding.etJabatan.getEditText().getText().toString().isEmpty()){
                    dialogOrganizationBinding.etJabatan.setError(getString(R.string.field_cant_be_empty_error));
                    dialogOrganizationBinding.etJabatan.requestFocus();
                }else if(dialogOrganizationBinding.etTglMasuk.getEditText().getText() == null ||dialogOrganizationBinding.etTglMasuk.getEditText().getText().toString().isEmpty()){
                    dialogOrganizationBinding.etTglMasuk.setError(getString(R.string.field_cant_be_empty_error));
                    dialogOrganizationBinding.etTglMasuk.requestFocus();
                }else{
                    String namaOrganisasi = dialogOrganizationBinding.etNamaOrganisasi.getEditText().getText().toString();
                    String jabatan = dialogOrganizationBinding.etJabatan.getEditText().getText().toString();
                    String tglMasuk = dialogOrganizationBinding.etTglMasuk.getEditText().getText().toString();
                    String tglSelesai = dialogOrganizationBinding.etTglSelesai.getEditText().getText().toString();

                    Organization dataOrganisasiBaru =
                            new Organization("","",namaOrganisasi, jabatan, tglMasuk, tglSelesai);
                    if(dataOrganisasiSekarang!=null) {
                        dataOrganisasiBaru.setOrganizationId(dataOrganisasiSekarang.getOrganizationId());
                        dataCurrentOrganizations.removeIf(n -> (Objects.equals(n.getOrganizationId(), dataOrganisasiBaru.getOrganizationId())));
                        dataCurrentOrganizations.add(dataOrganisasiBaru);
                    }else{
                        dataOrganisasiBaru.setOrganizationId(""+dataCurrentOrganizations.size());
                        dataCurrentOrganizations.add(dataOrganisasiBaru);
                    }
                    organizationAdapter.updateSubmitData(dataCurrentOrganizations);
                    makeToast("Data organisasi berhasil diupdate");
                    organisasiDialog.dismiss();
                }
            }
        });

        organisasiDialog.show();
    }

    private void handlePrestasiForm(@Nullable Achievement dataPrestasiSekarang){
        DialogFormPrestasiBinding dialogPrestasiBinding = DialogFormPrestasiBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogPrestasiForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogPrestasiBinding.getRoot());
        AlertDialog prestasiDialog = dialogPrestasiForm.create();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.peringkat_spinner_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogPrestasiBinding.peringkatSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> tingkatAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tingkat_spinner_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        tingkatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogPrestasiBinding.tingkatSpinner.setAdapter(tingkatAdapter);

        if(dataPrestasiSekarang!=null){
            dialogPrestasiBinding.etNamaPrestasi.getEditText().setText(dataPrestasiSekarang.getEventName());
            int position = adapter.getPosition(dataPrestasiSekarang.getRankOrParticipant());
            if (position >= 0) {
                dialogPrestasiBinding.peringkatSpinner.setSelection(position);
            }else{
                dialogPrestasiBinding.peringkatSpinner.setSelection(0);
            }

            int tingkatPosition = tingkatAdapter.getPosition(dataPrestasiSekarang.getLevel());
            if (tingkatPosition >= 0) {
                dialogPrestasiBinding.tingkatSpinner.setSelection(tingkatPosition);
            }else{
                dialogPrestasiBinding.tingkatSpinner.setSelection(0);
            }
//            dialogPrestasiBinding.etPeringkatPrestasi.getEditText().setText(dataPrestasiSekarang.getRankOrParticipant());
//            dialogPrestasiBinding.etTingkatPrestasi.getEditText().setText(dataPrestasiSekarang.getLevel());
            dialogPrestasiBinding.etTglPrestasi.getEditText().setText(dataPrestasiSekarang.getAchievementDate());

            dialogPrestasiBinding.etTglPrestasi.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(dataPrestasiSekarang.getAchievementDate(), new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogPrestasiBinding.etTglPrestasi.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

        }else {
            dialogPrestasiBinding.etTglPrestasi.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(null, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogPrestasiBinding.etTglPrestasi.getEditText().setText(dateFormat);
                        }
                    });
                }
            });
        }


        dialogPrestasiBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPrestasiBinding.etNamaPrestasi.setError(null);
//                dialogPrestasiBinding.etPeringkatPrestasi.setError(null);
//                dialogPrestasiBinding.etTingkatPrestasi.setError(null);
                dialogPrestasiBinding.etTglPrestasi.setError(null);
                if(dialogPrestasiBinding.etNamaPrestasi.getEditText().getText() == null ||dialogPrestasiBinding.etNamaPrestasi.getEditText().getText().toString().isEmpty()){
                    dialogPrestasiBinding.etNamaPrestasi.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPrestasiBinding.etNamaPrestasi.requestFocus();
//                }else if(dialogPrestasiBinding.etPeringkatPrestasi.getEditText().getText() == null ||dialogPrestasiBinding.etPeringkatPrestasi.getEditText().getText().toString().isEmpty()){
//                    dialogPrestasiBinding.etPeringkatPrestasi.setError(getString(R.string.field_cant_be_empty_error));
//                    dialogPrestasiBinding.etPeringkatPrestasi.requestFocus();
//                }else if(dialogPrestasiBinding.etTingkatPrestasi.getEditText().getText() == null ||dialogPrestasiBinding.etTingkatPrestasi.getEditText().getText().toString().isEmpty()){
//                    dialogPrestasiBinding.etTingkatPrestasi.setError(getString(R.string.field_cant_be_empty_error));
//                    dialogPrestasiBinding.etTingkatPrestasi.requestFocus();
                }else if(dialogPrestasiBinding.etTglPrestasi.getEditText().getText() == null ||dialogPrestasiBinding.etTglPrestasi.getEditText().getText().toString().isEmpty()){
                    dialogPrestasiBinding.etTglPrestasi.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPrestasiBinding.etTglPrestasi.requestFocus();
                }else{
                    String namaPrestasi = dialogPrestasiBinding.etNamaPrestasi.getEditText().getText().toString();
                    String peringkat = dialogPrestasiBinding.peringkatSpinner.getSelectedItem().toString();
                    String tingkat = dialogPrestasiBinding.tingkatSpinner.getSelectedItem().toString();
                    String tanggalPrestasi = dialogPrestasiBinding.etTglPrestasi.getEditText().getText().toString();
                    Achievement dataPrestasiBaru = new Achievement("","",
                            namaPrestasi, peringkat, tanggalPrestasi, tingkat);
                    if(dataPrestasiSekarang!=null) {
                        dataPrestasiBaru.setAchievementId(dataPrestasiSekarang.getAchievementId());
                        dataCurrentAchievements.removeIf(n -> (Objects.equals(n.getAchievementId(), dataPrestasiBaru.getAchievementId())));
                        dataCurrentAchievements.add(dataPrestasiBaru);
                    }else{
                        dataPrestasiBaru.setAchievementId(""+dataCurrentAchievements.size());
                        dataCurrentAchievements.add(dataPrestasiBaru);
                    }
//                    updateDataHealth();
                    achievementAdapter.updateAchievementList(dataCurrentAchievements);
                    makeToast("Data prestasi berhasil diupdate");
                    prestasiDialog.dismiss();
                }
            }
        });

        prestasiDialog.show();
    }

    private void handlePelanggaranForm(@Nullable Violation dataPelanggaranSekarang){
        DialogFormPelanggaranBinding dialogPelanggaranBinding = DialogFormPelanggaranBinding.inflate(getLayoutInflater());
        AlertDialog.Builder dialogPelanggaranForm = new AlertDialog.Builder(AddSantriActivity.this).setView(dialogPelanggaranBinding.getRoot());
        AlertDialog pelanggaranDialog = dialogPelanggaranForm.create();

        if(dataPelanggaranSekarang!=null){
            dialogPelanggaranBinding.etNamaPelanggaran.getEditText().setText(dataPelanggaranSekarang.getViolationName());
            dialogPelanggaranBinding.etTglPelanggaran.getEditText().setText(dataPelanggaranSekarang.getViolationDate());
            dialogPelanggaranBinding.etPoinPelanggaran.getEditText().setText(String.valueOf(dataPelanggaranSekarang.getPoints()));
            dialogPelanggaranBinding.etSanksiPelanggaran.getEditText().setText(dataPelanggaranSekarang.getSanction());
            dialogPelanggaranBinding.etTglSanksi.getEditText().setText(dataPelanggaranSekarang.getSanctionDate());
            if(dataPelanggaranSekarang.getViolationType().equalsIgnoreCase("Sedang")){
                dialogPelanggaranBinding.radioJenisBerat.setChecked(false);
                dialogPelanggaranBinding.radioJenisSedang.setChecked(true);
            }else{
                dialogPelanggaranBinding.radioJenisSedang.setChecked(false);
                dialogPelanggaranBinding.radioJenisBerat.setChecked(true);
            }

            dialogPelanggaranBinding.etTglPelanggaran.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(dataPelanggaranSekarang.getViolationDate(), new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogPelanggaranBinding.etTglPelanggaran.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

            dialogPelanggaranBinding.etTglSanksi.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(dataPelanggaranSekarang.getSanctionDate(), new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogPelanggaranBinding.etTglSanksi.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

        }else {
            dialogPelanggaranBinding.etTglPelanggaran.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(null, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogPelanggaranBinding.etTglPelanggaran.getEditText().setText(dateFormat);
                        }
                    });
                }
            });

            dialogPelanggaranBinding.etTglSanksi.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerUmum(null, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(String dateFormat) {
                            dialogPelanggaranBinding.etTglSanksi.getEditText().setText(dateFormat);
                        }
                    });
                }
            });
        }


        dialogPelanggaranBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPelanggaranBinding.etNamaPelanggaran.setError(null);
                dialogPelanggaranBinding.etTglPelanggaran.setError(null);
                dialogPelanggaranBinding.etPoinPelanggaran.setError(null);
                dialogPelanggaranBinding.etSanksiPelanggaran.setError(null);
                dialogPelanggaranBinding.etTglSanksi.setError(null);
                if(dialogPelanggaranBinding.etNamaPelanggaran.getEditText().getText() == null ||dialogPelanggaranBinding.etNamaPelanggaran.getEditText().getText().toString().isEmpty()){
                    dialogPelanggaranBinding.etNamaPelanggaran.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPelanggaranBinding.etNamaPelanggaran.requestFocus();
                }else if(dialogPelanggaranBinding.etTglPelanggaran.getEditText().getText() == null ||dialogPelanggaranBinding.etTglPelanggaran.getEditText().getText().toString().isEmpty()){
                    dialogPelanggaranBinding.etTglPelanggaran.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPelanggaranBinding.etTglPelanggaran.requestFocus();
                }else if(dialogPelanggaranBinding.radioJenisPelanggaran.getCheckedRadioButtonId() == -1){
                    makeToast("Pilih jenis pelanggaran terlebih dahulu");
                }else if(dialogPelanggaranBinding.etPoinPelanggaran.getEditText().getText() == null ||dialogPelanggaranBinding.etPoinPelanggaran.getEditText().getText().toString().isEmpty()){
                    dialogPelanggaranBinding.etPoinPelanggaran.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPelanggaranBinding.etPoinPelanggaran.requestFocus();
                }else if(dialogPelanggaranBinding.etSanksiPelanggaran.getEditText().getText() == null ||dialogPelanggaranBinding.etSanksiPelanggaran.getEditText().getText().toString().isEmpty()){
                    dialogPelanggaranBinding.etSanksiPelanggaran.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPelanggaranBinding.etSanksiPelanggaran.requestFocus();
                }else if(dialogPelanggaranBinding.etTglSanksi.getEditText().getText() == null ||dialogPelanggaranBinding.etTglSanksi.getEditText().getText().toString().isEmpty()){
                    dialogPelanggaranBinding.etTglSanksi.setError(getString(R.string.field_cant_be_empty_error));
                    dialogPelanggaranBinding.etTglSanksi.requestFocus();
                }else{
                    String nama = dialogPelanggaranBinding.etNamaPelanggaran.getEditText().getText().toString();
                    String tanggal = dialogPelanggaranBinding.etTglPelanggaran.getEditText().getText().toString();
                    String sanksi = dialogPelanggaranBinding.etSanksiPelanggaran.getEditText().getText().toString();
                    String tglSanksi = dialogPelanggaranBinding.etTglSanksi.getEditText().getText().toString();
                    String jenisPelanggaran = (dialogPelanggaranBinding.radioJenisSedang.isChecked() ? "Sedang" : "Berat");
                    int poin = Integer.parseInt(dialogPelanggaranBinding.etPoinPelanggaran.getEditText().getText().toString());

                    if(jenisPelanggaran.equals("Sedang") && (poin < 5 || poin > 8)){
                        dialogPelanggaranBinding.etPoinPelanggaran.setError("Poin pelanggaran sedang harus diantara 5 - 8");
                        dialogPelanggaranBinding.etPoinPelanggaran.requestFocus();
                        return;
                    }

                    if(jenisPelanggaran.equals("Berat") && (poin < 10 || poin > 15)){
                        dialogPelanggaranBinding.etPoinPelanggaran.setError("Poin pelanggaran berat harus diantara 10 - 15");
                        dialogPelanggaranBinding.etPoinPelanggaran.requestFocus();
                        return;
                    }

                    Violation dataPelanggaranBaru =
                            new Violation("","",tanggal, nama, jenisPelanggaran, sanksi, tglSanksi, poin);
                    if(dataPelanggaranSekarang!=null) {
                        dataPelanggaranBaru.setViolationId(dataPelanggaranSekarang.getViolationId());
                        dataCurrentViolations.removeIf(n -> (Objects.equals(n.getViolationId(), dataPelanggaranBaru.getViolationId())));
                        dataCurrentViolations.add(dataPelanggaranBaru);
                    }else{
                        dataPelanggaranBaru.setViolationId(""+dataCurrentViolations.size());
                        dataCurrentViolations.add(dataPelanggaranBaru);
                    }
                    violationAdapter.updateViolationList(dataCurrentViolations);
                    makeToast("Data pelanggaran berhasil diupdate");
                    pelanggaranDialog.dismiss();
                }
            }
        });

        pelanggaranDialog.show();
    }

    private String generateQRCode(String text) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 2000, 2000);

            String fileName = "generated_barcode.png";
            File file = new File(getApplicationContext().getFilesDir(), fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void makeToast(String toastText){
        Toast.makeText(AddSantriActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }
}