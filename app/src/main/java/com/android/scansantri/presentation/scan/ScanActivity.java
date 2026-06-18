package com.android.scansantri.presentation.scan;

import static com.android.scansantri.helper.SantriDataFilterHelper.findSantriDataByNis;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.scansantri.R;
import com.android.scansantri.data.model.SantriData;
import com.android.scansantri.presentation.LoadingDialog;
import com.android.scansantri.presentation.MainActivity;
import com.android.scansantri.presentation.add.AddSantriActivity;
import com.android.scansantri.presentation.list.ListSantriActivity;
import com.android.scansantri.presentation.list.edit.EditSantriActivity;
import com.android.scansantri.presentation.viewmodel.MainViewModel;
import com.android.scansantri.presentation.viewmodel.MainViewModelFactory;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private TextView barcodeText;
    private TextView tidakDitemukan;
    private String barcodeData;
    private MainViewModel mainViewModel;
    private List<SantriData> listSemuaSantri = Collections.emptyList();
    private String currentFound = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);

        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.scanResultTV);
        tidakDitemukan = findViewById(R.id.tidakDitemukanTV);
        initialiseDetectorsAndSources();

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
        });
    }

    private void makeToast(String toastText){
        Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }

    private void initialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(ScanActivity.this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        float screenAspectRatio = (float) screenHeight / (float) screenWidth;
        float cameraAspectRatio = 1920f / 1080f;

        if (screenAspectRatio > cameraAspectRatio) {
            cameraSource = new CameraSource.Builder(ScanActivity.this, barcodeDetector)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(1920, (int) (1920 * screenAspectRatio))
                    .build();
        } else {
            cameraSource = new CameraSource.Builder(ScanActivity.this, barcodeDetector)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize((int) (1080 / screenAspectRatio), 1080)
                    .build();
        }


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                if(!currentFound.equals(barcodeData)){
                                    currentFound = barcodeData;
                                    barcodeText.setText("Hasil Scan : " + barcodeData);
                                    checkScanned(barcodeData);
                                }
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                if(!currentFound.equals(barcodeData)){
                                    currentFound = barcodeData;
                                    barcodeText.setText("Hasil Scan : " + barcodeData);
                                    checkScanned(barcodeData);
                                }
                            }
                        }
                    });

                }
            }
        });
    }

    private void checkScanned(String barcodeData) {
        SantriData santriFound = findSantriDataByNis(listSemuaSantri, barcodeData);
        if(santriFound!=null){
            tidakDitemukan.setVisibility(View.INVISIBLE);
            makeToast("Data santri ditemukan (NIS : "+santriFound.getNis()+")");
            openListActivity(santriFound);
        }else{
            tidakDitemukan.setVisibility(View.VISIBLE);
        }
    }

    private void openListActivity(SantriData santriData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        builder.setTitle("SANTRI DITEMUKAN");
        builder.setMessage("Mengarahkan ke halaman untuk melihat detail santri dengan NIS : " + santriData.getNis() + " ?");

        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(ScanActivity.this, ListSantriActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ListSantriActivity.ARG_SANTRI_NIS, santriData);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
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


    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }
}