package com.android.scansantri.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SantriPDFGenerator {
    private static final String TAG = "SantriPDFGenerator";
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int MARGIN = 50;
    private static final int HEADER_HEIGHT = 132; // Height reserved for header
    private static final int FOOTER_MARGIN = 30; // Space from bottom for page numbers

    private Context context;
    private PdfGenerationCallback callback;
    private Bitmap qrCodeBitmap;
    private Bitmap logoBitmap;
    private boolean isQrCodeLoaded = false;
    private boolean hasQrCodeError = false;
    private Typeface timesNewRomanTypeface;
    private Typeface timesNewRomanBoldTypeface;

    public interface PdfGenerationCallback {
        void onPdfGenerated(String fileName);
        void onPdfGenerationFailed(String error);
        void onProgressUpdate(String progress);
    }

    public SantriPDFGenerator(Context context) {
        this.context = context;
        loadLogo();
        loadFonts();
    }

    private void loadLogo() {
        try {
            Drawable logoDrawable = ContextCompat.getDrawable(context, R.drawable.img_pesantren_logo);
            if (logoDrawable != null) {
                logoBitmap = Bitmap.createBitmap(logoDrawable.getIntrinsicWidth(),
                        logoDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(logoBitmap);
                logoDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                logoDrawable.draw(canvas);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading logo", e);
        }
    }

    private void loadFonts() {
        try {
            // Load Times New Roman from assets if available
//             If you have TTF/OTF files in assets/fonts/, uncomment below:
            timesNewRomanTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/times_new_roman.ttf");
            timesNewRomanBoldTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/times_new_romans_bold.ttf");

            // Fallback to default serif fonts
            timesNewRomanTypeface = Typeface.SERIF;
            timesNewRomanBoldTypeface = Typeface.create(Typeface.SERIF, Typeface.BOLD);
        } catch (Exception e) {
            Log.e(TAG, "Error loading fonts, using defaults", e);
            timesNewRomanTypeface = Typeface.DEFAULT;
            timesNewRomanBoldTypeface = Typeface.DEFAULT_BOLD;
        }
    }

    public void generateSantriPDF(SantriData santri, ParentData parentData,
                                  List<Organization> organizations, List<Achievement> achievements,
                                  List<HealthRecord> healthRecords, List<Violation> violations,
                                  Uri outputUri, PdfGenerationCallback callback) {
        this.callback = callback;

        callback.onProgressUpdate("Memuat QR Code...");

        // Load QR Code first
        if (santri.getBarcodeLink() != null && !santri.getBarcodeLink().isEmpty()) {
            Glide.with(context)
                    .asBitmap()
                    .load(santri.getBarcodeLink())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            qrCodeBitmap = resource;
                            isQrCodeLoaded = true;
                            generatePDF(santri, parentData, organizations, achievements, healthRecords, violations, outputUri);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(android.graphics.drawable.Drawable errorDrawable) {
                            Log.e(TAG, "Failed to load QR Code image");
                            hasQrCodeError = true;
                            isQrCodeLoaded = true; // Continue without QR code
                            generatePDF(santri, parentData, organizations, achievements, healthRecords, violations, outputUri);
                        }
                    });
        } else {
            isQrCodeLoaded = true;
            generatePDF(santri, parentData, organizations, achievements, healthRecords, violations, outputUri);
        }
    }

    private void generatePDF(SantriData santri, ParentData parentData,
                             List<Organization> organizations, List<Achievement> achievements,
                             List<HealthRecord> healthRecords, List<Violation> violations,
                             Uri outputUri) {

        callback.onProgressUpdate("Membuat dokumen PDF...");

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        try {
            int currentPageNumber = 1;
            PdfDocument.Page currentPage = createNewPage(pdfDocument, currentPageNumber);
            Canvas canvas = currentPage.getCanvas();

            int yPosition = drawHeader(canvas, paint, titlePaint);
            int maxYPosition = PAGE_HEIGHT - FOOTER_MARGIN - 20; // Reserve space for footer

            // A. Basic Information
            PageBreakResult result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 150, currentPageNumber);
            currentPage = result.page;
            canvas = currentPage.getCanvas();
            yPosition = result.yPosition;
            currentPageNumber = result.pageNumber;

            if (result.isNewPage) {
                yPosition = drawHeader(canvas, paint, titlePaint);
            }
            yPosition = drawSection(canvas, paint, titlePaint, "A. INFORMASI DASAR", yPosition);
            yPosition = drawSantriBasicInfo(canvas, paint, titlePaint, santri, yPosition);

            // B. Social Media
            result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 120, currentPageNumber + 1);
            currentPage = result.page;
            canvas = currentPage.getCanvas();
            yPosition = result.yPosition;
            currentPageNumber = result.pageNumber;

            if (result.isNewPage) {
                yPosition = drawHeader(canvas, paint, titlePaint);
            }
            yPosition = drawSection(canvas, paint, titlePaint, "B. MEDIA SOSIAL", yPosition);
            yPosition = drawSocialMedia(canvas, paint, titlePaint, santri, yPosition);

            // QR Code
            result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 140, currentPageNumber + 1);
            currentPage = result.page;
            canvas = currentPage.getCanvas();
            yPosition = result.yPosition;
            currentPageNumber = result.pageNumber;

            if (result.isNewPage) {
                yPosition = drawHeader(canvas, paint, titlePaint);
            }
            yPosition = drawQRCode(canvas, paint, yPosition);

            // C. Parent Information
            if (parentData != null) {
                result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 200, currentPageNumber + 1);
                currentPage = result.page;
                canvas = currentPage.getCanvas();
                yPosition = result.yPosition;
                currentPageNumber = result.pageNumber;

                if (result.isNewPage) {
                    yPosition = drawHeader(canvas, paint, titlePaint);
                }
                yPosition = drawSection(canvas, paint, titlePaint, "C. INFORMASI ORANG TUA", yPosition);
                yPosition = drawParentInfo(canvas, paint, titlePaint, parentData, yPosition);
            }

            // D. Organizations
            if (organizations != null && !organizations.isEmpty()) {
                result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 100, currentPageNumber + 1);
                currentPage = result.page;
                canvas = currentPage.getCanvas();
                yPosition = result.yPosition;
                currentPageNumber = result.pageNumber;

                if (result.isNewPage) {
                    yPosition = drawHeader(canvas, paint, titlePaint);
                }
                yPosition = drawSection(canvas, paint, titlePaint, "D. ORGANISASI", yPosition);
                PageBreakResult orgResult = drawOrganizations(canvas, paint, titlePaint, organizations, yPosition, pdfDocument, currentPage, currentPageNumber, maxYPosition);
                currentPage = orgResult.page;
                yPosition = orgResult.yPosition;
                currentPageNumber = orgResult.pageNumber;
            }

            // E. Achievements
            if (achievements != null && !achievements.isEmpty()) {
                result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 100, currentPageNumber + 1);
                currentPage = result.page;
                canvas = currentPage.getCanvas();
                yPosition = result.yPosition;
                currentPageNumber = result.pageNumber;

                if (result.isNewPage) {
                    yPosition = drawHeader(canvas, paint, titlePaint);
                }
                yPosition = drawSection(canvas, paint, titlePaint, "E. PRESTASI", yPosition);
                PageBreakResult achResult = drawAchievements(canvas, paint, titlePaint, achievements, yPosition, pdfDocument, currentPage, currentPageNumber, maxYPosition);
                currentPage = achResult.page;
                yPosition = achResult.yPosition;
                currentPageNumber = achResult.pageNumber;
            }

            // F. Health Records
            if (healthRecords != null && !healthRecords.isEmpty()) {
                result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 100, currentPageNumber + 1);
                currentPage = result.page;
                canvas = currentPage.getCanvas();
                yPosition = result.yPosition;
                currentPageNumber = result.pageNumber;

                if (result.isNewPage) {
                    yPosition = drawHeader(canvas, paint, titlePaint);
                }
                yPosition = drawSection(canvas, paint, titlePaint, "F. REKAM MEDIS", yPosition);
                PageBreakResult healthResult = drawHealthRecords(canvas, paint, titlePaint, healthRecords, yPosition, pdfDocument, currentPage, currentPageNumber, maxYPosition);
                currentPage = healthResult.page;
                yPosition = healthResult.yPosition;
                currentPageNumber = healthResult.pageNumber;
            }

            // G. Violations
            if (violations != null && !violations.isEmpty()) {
                result = checkPageBreak(pdfDocument, currentPage, yPosition, maxYPosition, 100, currentPageNumber + 1);
                currentPage = result.page;
                canvas = currentPage.getCanvas();
                yPosition = result.yPosition;
                currentPageNumber = result.pageNumber;

                if (result.isNewPage) {
                    yPosition = drawHeader(canvas, paint, titlePaint);
                }
                yPosition = drawSection(canvas, paint, titlePaint, "G. PELANGGARAN", yPosition);
                PageBreakResult violResult = drawViolations(canvas, paint, titlePaint, violations, yPosition, pdfDocument, currentPage, currentPageNumber, maxYPosition);
                currentPage = violResult.page;
                yPosition = violResult.yPosition;
                currentPageNumber = violResult.pageNumber;
            }

            // Draw page number on last page
            drawPageNumber(currentPage.getCanvas(), paint, currentPageNumber);
            pdfDocument.finishPage(currentPage);

            callback.onProgressUpdate("Menyimpan file PDF...");

            // Save PDF using SAF
            OutputStream outputStream = context.getContentResolver().openOutputStream(outputUri);
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream);
                outputStream.close();
                pdfDocument.close();

                String fileName = "Santri_" + santri.getFullName().replaceAll("[^a-zA-Z0-9]", "_") +
                        "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
                callback.onPdfGenerated(fileName);
            } else {
                throw new IOException("Tidak dapat membuka output stream");
            }

        } catch (IOException e) {
            Log.e(TAG, "Error generating PDF", e);
            callback.onPdfGenerationFailed("Error: " + e.getMessage());
            pdfDocument.close();
        }
    }

    // Helper class to return page break results
    private static class PageBreakResult {
        PdfDocument.Page page;
        int yPosition;
        int pageNumber;
        boolean isNewPage;

        PageBreakResult(PdfDocument.Page page, int yPosition, int pageNumber, boolean isNewPage) {
            this.page = page;
            this.yPosition = yPosition;
            this.pageNumber = pageNumber;
            this.isNewPage = isNewPage;
        }
    }

    private PdfDocument.Page createNewPage(PdfDocument pdfDocument, int pageNumber) {
        PdfDocument.PageInfo.Builder pageInfoBuilder = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber);
        PdfDocument.PageInfo pageInfo = pageInfoBuilder.create();
        return pdfDocument.startPage(pageInfo);
    }

    private PageBreakResult checkPageBreak(PdfDocument pdfDocument, PdfDocument.Page currentPage,
                                           int currentY, int maxY, int requiredSpace, int nextPageNumber) {
        if (currentY + requiredSpace > maxY) {
            // Draw page number before finishing page
            Paint paint = new Paint();
            paint.setTypeface(timesNewRomanTypeface);
            drawPageNumber(currentPage.getCanvas(), paint, currentPage.getInfo().getPageNumber());
            pdfDocument.finishPage(currentPage);

            // Create new page
            PdfDocument.Page newPage = createNewPage(pdfDocument, nextPageNumber);
            return new PageBreakResult(newPage, MARGIN + HEADER_HEIGHT, nextPageNumber, true);
        }
        return new PageBreakResult(currentPage, currentY, currentPage.getInfo().getPageNumber(), false);
    }

    private void drawPageNumber(Canvas canvas, Paint paint, int pageNumber) {
        // Add null check for canvas
        if (canvas == null) {
            Log.e(TAG, "Canvas is null in drawPageNumber");
            return;
        }
        Paint newPaint = new Paint();
        newPaint.setColor(Color.BLACK);
        newPaint.setTextSize(10);
        newPaint.setTextAlign(Paint.Align.CENTER);
        newPaint.setTypeface(timesNewRomanTypeface);
        canvas.drawText("- " + pageNumber + " -", PAGE_WIDTH / 2, PAGE_HEIGHT - FOOTER_MARGIN + 15, newPaint);
    }

    private int drawHeader(Canvas canvas, Paint paint, Paint titlePaint) {
        // Add null check for canvas
        if (canvas == null) {
            Log.e(TAG, "Canvas is null in drawHeader");
            return MARGIN + HEADER_HEIGHT;
        }

        // Setup paints with Times New Roman
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(timesNewRomanBoldTypeface);

        Paint subTitlePaint = new Paint();
        subTitlePaint.setColor(Color.BLACK);
        subTitlePaint.setTextAlign(Paint.Align.CENTER);
        subTitlePaint.setTypeface(timesNewRomanBoldTypeface);

        Paint addressPaint = new Paint();
        addressPaint.setColor(Color.BLACK);
        addressPaint.setTextAlign(Paint.Align.CENTER);
        addressPaint.setTypeface(timesNewRomanTypeface);
        addressPaint.setTextSize(9);

        // Draw logo
        if (logoBitmap != null) {
            Bitmap resizedLogo = Bitmap.createScaledBitmap(logoBitmap, 80, 80, false);
            canvas.drawBitmap(resizedLogo, MARGIN, MARGIN, paint);
        }

        // Header text positioning (adjusted for logo)
        int textStartX = PAGE_WIDTH / 2;
        int textStartY = MARGIN + 15;

        // Main title
        titlePaint.setTextSize(14);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("YAYASAN PENDIDIKAN SULTAN HASANUDDIN", textStartX+30, textStartY, titlePaint);
        titlePaint.setTextAlign(Paint.Align.LEFT);

        subTitlePaint.setTextSize(13);
        canvas.drawText("PESANTREN SULTAN HASANUDDIN", textStartX+30, textStartY + 20, subTitlePaint);

        // Arabic text (you may want to replace this with an image if Arabic font is not available)
        subTitlePaint.setTextSize(12);
        canvas.drawText("معهد السلطان حسن الدين", textStartX+30, textStartY + 40, subTitlePaint);

        // Address
        canvas.drawText("Pattunggalengang Limbung - Kab. Gowa - Sulawesi Selatan - Indonesia", textStartX+30, textStartY + 58, addressPaint);


        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        canvas.drawLine(MARGIN, textStartY + 68, PAGE_WIDTH - MARGIN, textStartY + 68, paint);
        paint.setStrokeWidth(1);
        canvas.drawLine(MARGIN, textStartY + 71, PAGE_WIDTH - MARGIN, textStartY + 71, paint);

        canvas.drawText("Alamat : Jln. Muhammad Arief Mansyur No. 20                    Kode Pos : 92152 HP. : 082347763800 / 081343637009",
                textStartX, textStartY + 82, addressPaint);

        // Draw lines
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        canvas.drawLine(MARGIN, MARGIN + 102, PAGE_WIDTH - MARGIN, MARGIN + 102, paint);
        paint.setStrokeWidth(2);
        canvas.drawLine(MARGIN, MARGIN + 106, PAGE_WIDTH - MARGIN, MARGIN + 106, paint);

        return MARGIN + HEADER_HEIGHT;
    }

    private int drawSection(Canvas canvas, Paint paint, Paint titlePaint, String sectionTitle, int startY) {
        titlePaint.setTextSize(14);
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(timesNewRomanBoldTypeface);
        canvas.drawText(sectionTitle, MARGIN, startY, titlePaint);
        return startY + 25;
    }

    private int drawSantriBasicInfo(Canvas canvas, Paint paint, Paint titlePaint, SantriData santri, int startY) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(11);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(timesNewRomanTypeface);

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);

        int yPosition = startY;
        int lineSpacing = 20;

        // Basic info
        canvas.drawText("Nama Lengkap:", MARGIN + 20, yPosition, boldPaint);
        canvas.drawText(santri.getFullName() != null ? santri.getFullName() : "-", MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("NIS:", MARGIN + 20, yPosition, boldPaint);
        canvas.drawText(santri.getNis() != null ? santri.getNis() : "-", MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Tempat, Tanggal Lahir:", MARGIN + 20, yPosition, boldPaint);
        String ttl = (santri.getBirthPlace() != null ? santri.getBirthPlace() : "") +
                (santri.getBirthDate() != null ? ", " + santri.getBirthDate() : "");
        canvas.drawText(ttl.isEmpty() ? "-" : ttl, MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Jenis Kelamin:", MARGIN + 20, yPosition, boldPaint);
        canvas.drawText(santri.getGender() != null ? santri.getGender() : "-", MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Nomor Telepon:", MARGIN + 20, yPosition, boldPaint);
        canvas.drawText(santri.getPhoneNumber() != null ? santri.getPhoneNumber() : "-", MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Email:", MARGIN + 20, yPosition, boldPaint);
        canvas.drawText(santri.getEmail() != null ? santri.getEmail() : "-", MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Alamat:", MARGIN + 20, yPosition, boldPaint);
        String address = santri.getAddress() != null ? santri.getAddress() : "-";
        if (address.length() > 50) {
            String[] addressLines = splitText(address, 50);
            canvas.drawText(addressLines[0], MARGIN + 150, yPosition, paint);
            yPosition += lineSpacing;
            for (int i = 1; i < addressLines.length; i++) {
                canvas.drawText(addressLines[i], MARGIN + 150, yPosition, paint);
                yPosition += lineSpacing;
            }
        } else {
            canvas.drawText(address, MARGIN + 150, yPosition, paint);
            yPosition += lineSpacing;
        }

        return yPosition + 15;
    }

    private int drawSocialMedia(Canvas canvas, Paint paint, Paint titlePaint, SantriData santri, int startY) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(11);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(timesNewRomanTypeface);

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);

        int yPosition = startY;
        int lineSpacing = 20;

        canvas.drawText("Facebook:", MARGIN + 20, yPosition, boldPaint);
        String facebook = (santri.getFacebook() != null && !santri.getFacebook().isBlank()) ? santri.getFacebook() : "-";
        canvas.drawText(facebook, MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Instagram:", MARGIN + 20, yPosition, boldPaint);
        String instagram = (santri.getInstagram() != null && !santri.getInstagram().isBlank()) ? santri.getInstagram() : "-";
        canvas.drawText(instagram, MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        canvas.drawText("Twitter:", MARGIN + 20, yPosition, boldPaint);
        String twitter = (santri.getTwitter() != null && !santri.getTwitter().isBlank()) ? santri.getTwitter() : "-";
        canvas.drawText(twitter, MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        return yPosition + 15;
    }

    private int drawQRCode(Canvas canvas, Paint paint, int startY) {
        if (isQrCodeLoaded && qrCodeBitmap != null && !hasQrCodeError) {
            paint.setTextSize(11);
            paint.setTypeface(timesNewRomanTypeface);
            Paint boldPaint = new Paint(paint);
            boldPaint.setTypeface(timesNewRomanBoldTypeface);

            canvas.drawText("QR Code Santri:", MARGIN + 20, startY, boldPaint);

            // Draw QR Code (resize to 100x100)
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(qrCodeBitmap, 100, 100, false);
            canvas.drawBitmap(resizedBitmap, MARGIN + 20, startY + 10, paint);

            return startY + 125;
        } else if (hasQrCodeError) {
            paint.setColor(Color.RED);
            paint.setTypeface(timesNewRomanTypeface);
            canvas.drawText("QR Code tidak dapat dimuat (masalah koneksi)", MARGIN + 20, startY, paint);
            paint.setColor(Color.BLACK);
            return startY + 30;
        }

        return startY;
    }

    private int drawParentInfo(Canvas canvas, Paint paint, Paint titlePaint, ParentData parentData, int startY) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(11);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(timesNewRomanTypeface);

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);

        int yPosition = startY;
        int lineSpacing = 18;

        // Father info
        if (parentData.getFatherData() != null) {
            canvas.drawText("Ayah:", MARGIN + 20, yPosition, boldPaint);
            yPosition += 20;
            yPosition = drawParentDetails(canvas, paint, boldPaint, parentData.getFatherData(), yPosition, lineSpacing);
            yPosition += 15;
        }

        // Mother info
        if (parentData.getMotherData() != null) {
            canvas.drawText("Ibu:", MARGIN + 20, yPosition, boldPaint);
            yPosition += 20;
            yPosition = drawParentDetails(canvas, paint, boldPaint, parentData.getMotherData(), yPosition, lineSpacing);
            yPosition += 15;
        }

        // Divorce status
        canvas.drawText("Status Perceraian:", MARGIN + 20, yPosition, boldPaint);
        canvas.drawText(parentData.isDivorced() ? "Ya" : "Tidak", MARGIN + 150, yPosition, paint);
        yPosition += lineSpacing;

        // Step parent info
        if (parentData.getStepParentData() != null) {
            yPosition += 10;
            canvas.drawText("Orang Tua Tiri:", MARGIN + 20, yPosition, boldPaint);
            yPosition += 20;
            yPosition = drawParentDetails(canvas, paint, boldPaint, parentData.getStepParentData(), yPosition, lineSpacing);
        }

        return yPosition + 15;
    }

    private int drawParentDetails(Canvas canvas, Paint paint, Paint boldPaint, Object parentObj, int yPosition, int lineSpacing) {
        if (parentObj instanceof FatherData) {
            FatherData father = (FatherData) parentObj;

            canvas.drawText("   Nama:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(father.getFatherName() != null ? father.getFatherName() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Pekerjaan:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(father.getFatherJob() != null ? father.getFatherJob() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Nomor Telepon:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(father.getFatherPhone() != null ? father.getFatherPhone() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

        } else if (parentObj instanceof MotherData) {
            MotherData mother = (MotherData) parentObj;

            canvas.drawText("   Nama:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(mother.getMotherName() != null ? mother.getMotherName() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Pekerjaan:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(mother.getMotherJob() != null ? mother.getMotherJob() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Nomor Telepon:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(mother.getMotherPhone() != null ? mother.getMotherPhone() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

        } else if (parentObj instanceof StepParentData) {
            StepParentData stepParent = (StepParentData) parentObj;

            canvas.drawText("   Nama:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(stepParent.getStepParentName() != null ? stepParent.getStepParentName() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Nomor Telepon:", MARGIN + 40, yPosition, boldPaint);
            canvas.drawText(stepParent.getStepParentPhone() != null ? stepParent.getStepParentPhone() : "-", MARGIN + 170, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Alamat:", MARGIN + 40, yPosition, boldPaint);
            String address = stepParent.getStepParentAddress() != null ? stepParent.getStepParentAddress() : "-";
            if (address.length() > 45) {
                String[] addressLines = splitText(address, 45);
                canvas.drawText(addressLines[0], MARGIN + 170, yPosition, paint);
                yPosition += lineSpacing;
                for (int i = 1; i < addressLines.length && i < 3; i++) {
                    canvas.drawText(addressLines[i], MARGIN + 170, yPosition, paint);
                    yPosition += lineSpacing;
                }
            } else {
                canvas.drawText(address, MARGIN + 170, yPosition, paint);
                yPosition += lineSpacing;
            }
        }

        return yPosition;
    }

    private PageBreakResult drawOrganizations(Canvas canvas, Paint paint, Paint titlePaint, List<Organization> organizations,
                                              int startY, PdfDocument pdfDocument, PdfDocument.Page currentPage, int pageNumber, int maxY) {
        int yPosition = startY;
        int lineSpacing = 18;

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);
        paint.setTypeface(timesNewRomanTypeface);

        for (int i = 0; i < organizations.size(); i++) {
            // Check if we need a page break
            if (yPosition + 100 > maxY) {
                drawPageNumber(canvas, paint, pageNumber);
                pdfDocument.finishPage(currentPage);
                currentPage = createNewPage(pdfDocument, ++pageNumber);
                canvas = currentPage.getCanvas();
                yPosition = drawHeader(canvas, paint, titlePaint);
            }

            Organization org = organizations.get(i);

            canvas.drawText((i + 1) + ". " + (org.getOrganizationName() != null ? org.getOrganizationName() : "-"),
                    MARGIN + 20, yPosition, boldPaint);
            yPosition += lineSpacing;

            canvas.drawText("   Posisi: " + (org.getPosition() != null ? org.getPosition() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            String period = (org.getStartDate() != null ? org.getStartDate() : "") +
                    (org.getEndDate() != null ? " - " + org.getEndDate() : " - Sekarang");
            canvas.drawText("   Periode: " + period, MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing + 10;
        }

        return new PageBreakResult(currentPage, yPosition + 15, pageNumber, false);
    }

    private PageBreakResult drawAchievements(Canvas canvas, Paint paint, Paint titlePaint, List<Achievement> achievements,
                                             int startY, PdfDocument pdfDocument, PdfDocument.Page currentPage, int pageNumber, int maxY) {
        int yPosition = startY;
        int lineSpacing = 18;

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);
        paint.setTypeface(timesNewRomanTypeface);

        for (int i = 0; i < achievements.size(); i++) {
            // Check if we need a page break
            if (yPosition + 120 > maxY) {
                drawPageNumber(canvas, paint, pageNumber);
                pdfDocument.finishPage(currentPage);
                currentPage = createNewPage(pdfDocument, ++pageNumber);
                canvas = currentPage.getCanvas();
                yPosition = drawHeader(canvas, paint, titlePaint);
            }

            Achievement achievement = achievements.get(i);

            canvas.drawText((i + 1) + ". " + (achievement.getEventName() != null ? achievement.getEventName() : "-"),
                    MARGIN + 20, yPosition, boldPaint);
            yPosition += lineSpacing;

            canvas.drawText("   Peringkat: " + (achievement.getRankOrParticipant() != null ? achievement.getRankOrParticipant() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Tanggal: " + (achievement.getAchievementDate() != null ? achievement.getAchievementDate() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Tingkat: " + (achievement.getLevel() != null ? achievement.getLevel() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing + 10;
        }

        return new PageBreakResult(currentPage, yPosition + 15, pageNumber, false);
    }

    private PageBreakResult drawHealthRecords(Canvas canvas, Paint paint, Paint titlePaint, List<HealthRecord> healthRecords,
                                              int startY, PdfDocument pdfDocument, PdfDocument.Page currentPage, int pageNumber, int maxY) {
        int yPosition = startY;
        int lineSpacing = 18;

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);
        paint.setTypeface(timesNewRomanTypeface);

        for (int i = 0; i < healthRecords.size(); i++) {
            // Check if we need a page break
            if (yPosition + 140 > maxY) {
                drawPageNumber(canvas, paint, pageNumber);
                pdfDocument.finishPage(currentPage);
                currentPage = createNewPage(pdfDocument, ++pageNumber);
                canvas = currentPage.getCanvas();
                yPosition = drawHeader(canvas, paint, titlePaint);
            }

            HealthRecord record = healthRecords.get(i);

            canvas.drawText("Pemeriksaan " + (i + 1), MARGIN + 20, yPosition, boldPaint);
            yPosition += lineSpacing;

            canvas.drawText("   Tanggal: " + (record.getCheckupDate() != null ? record.getCheckupDate() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Penyakit: " + (record.getDisease() != null ? record.getDisease() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Perawatan: " + (record.getTreatment() != null ? record.getTreatment() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            if (record.getAdditionalNotes() != null && !record.getAdditionalNotes().isEmpty()) {
                canvas.drawText("   Catatan: " + record.getAdditionalNotes(), MARGIN + 20, yPosition, paint);
                yPosition += lineSpacing;
            }

            yPosition += 10;
        }

        return new PageBreakResult(currentPage, yPosition + 15, pageNumber, false);
    }

    private PageBreakResult drawViolations(Canvas canvas, Paint paint, Paint titlePaint, List<Violation> violations,
                                           int startY, PdfDocument pdfDocument, PdfDocument.Page currentPage, int pageNumber, int maxY) {
        int yPosition = startY;
        int lineSpacing = 18;

        Paint boldPaint = new Paint(paint);
        boldPaint.setTypeface(timesNewRomanBoldTypeface);
        paint.setTypeface(timesNewRomanTypeface);

        for (int i = 0; i < violations.size(); i++) {
            // Check if we need a page break
            if (yPosition + 140 > maxY) {
                drawPageNumber(canvas, paint, pageNumber);
                pdfDocument.finishPage(currentPage);
                currentPage = createNewPage(pdfDocument, ++pageNumber);
                canvas = currentPage.getCanvas();
                yPosition = drawHeader(canvas, paint, titlePaint);
            }

            Violation violation = violations.get(i);

            canvas.drawText((i + 1) + ". " + (violation.getViolationName() != null ? violation.getViolationName() : "-"),
                    MARGIN + 20, yPosition, boldPaint);
            yPosition += lineSpacing;

            canvas.drawText("   Tanggal: " + (violation.getViolationDate() != null ? violation.getViolationDate() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Jenis: " + (violation.getViolationType() != null ? violation.getViolationType() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Sanksi: " + (violation.getSanction() != null ? violation.getSanction() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing;

            canvas.drawText("   Tanggal Sanksi: " + (violation.getSanctionDate() != null ? violation.getSanctionDate() : "-"),
                    MARGIN + 20, yPosition, paint);
            yPosition += lineSpacing + 10;
        }

        return new PageBreakResult(currentPage, yPosition + 15, pageNumber, false);
    }

    private String[] splitText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return new String[]{text};
        }

        int numLines = (int) Math.ceil((double) text.length() / maxLength);
        String[] lines = new String[numLines];

        for (int i = 0; i < numLines; i++) {
            int start = i * maxLength;
            int end = Math.min(start + maxLength, text.length());
            lines[i] = text.substring(start, end);
        }

        return lines;
    }
}