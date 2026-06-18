package com.android.scansantri.presentation.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.scansantri.R;
import com.android.scansantri.data.model.SantriData;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class ReportSantriAdapter extends ArrayAdapter {
    private Context context;
    private List<SantriData> allSantriList;


    public ReportSantriAdapter(Context context, int resource, ArrayList<SantriData> allSantri) {
        super(context, resource, allSantri);

        this.context = context;
        this.allSantriList = allSantri;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.santri_report_list_item, null);
        final SantriData currentSantri = allSantriList.get(position);

        TextView tvNo;
        TextView tvNama;
        TextView tvNis;
        ImageView barcode;

        tvNis = view.findViewById(R.id.tvNIS);
        tvNama = view.findViewById(R.id.tvNama);
        tvNo = view.findViewById(R.id.tvNomor);
        barcode = view.findViewById(R.id.ivQr);
        tvNo.setText(""+(position+1));
        tvNama.setText(currentSantri.getFullName());
        tvNis.setText(currentSantri.getNis());
        Bitmap generatedQrCode = generateQRCode(currentSantri.getNis());
        if(generatedQrCode != null) barcode.setImageBitmap(generatedQrCode);

        return  view;
    }

    private Bitmap generateQRCode(String text) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 1000, 1000);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
