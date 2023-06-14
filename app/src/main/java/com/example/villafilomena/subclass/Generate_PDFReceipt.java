package com.example.villafilomena.subclass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.villafilomena.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Generate_PDFReceipt {
    String folder_name = "Invoice";
    Activity activity;
    String fullname, kidQty, adultQty, checkIn_date, checkIn_time, checkOut_date, checkOut_time, roomID, cottageID, total, payment_stat, GCashNum, refNumber;
    private OnUploadCompleteListener uploadCompleteListener;
    public Generate_PDFReceipt(Activity activity, String fullname, String kidQty, String adultQty, String checkIn_date, String checkIn_time, String checkOut_date, String checkOut_time, String roomID, String cottageID, String total, String payment_stat, String GCashNum, String refNumber) {
        this.activity = activity;
        this.fullname = fullname;
        this.kidQty = kidQty;
        this.adultQty = adultQty;
        this.checkIn_date = checkIn_date;
        this.checkIn_time = checkIn_time;
        this.checkOut_date = checkOut_date;
        this.checkOut_time = checkOut_time;
        this.roomID = roomID;
        this.cottageID = cottageID;
        this.total = total;
        this.payment_stat = payment_stat;
        this.GCashNum = GCashNum;
        this.refNumber = refNumber;
    }

    @SuppressLint("SimpleDateFormat")
    public void generatePDF(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            createFolder();
        }
        else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }

        //1 inch = 72 points so 1 * 72
        int width = 612;
        int height = 792;

        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo);

        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Matrix matrix = new Matrix();
        float scaleFactor = Math.min((float) 80 / bmp.getWidth(), (float) 80 / bmp.getHeight());
        bmp.getHeight();
        matrix.setScale(scaleFactor, scaleFactor);
        matrix.postTranslate(10, 25);

        canvas.drawBitmap(bmp, matrix, paint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(16f);
        canvas.drawText("Villa Filomena Natural Spring Resort", 130, 80, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(12f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Purok 2 Kaytimbog, Indang, Cavite", 130, 100, paint);
        canvas.drawText("09391136357", 130, 115, paint);

        Date InvoiceDate = new Date();
        DateFormat InvoiceDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(12f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Invoice Date: "+InvoiceDateFormat.format(InvoiceDate), width-20, 80, paint);
        canvas.drawText("Online Booking", width-20, 100, paint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(20f);
        canvas.drawText("Receipt", width/2, 160, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Guest Name: "+fullname, 50, 200, paint);
        canvas.drawText("No. of People: "+ adultQty + " Adult/s, "+ kidQty + "Kid/s", 50, 220, paint);
        canvas.drawText("Check-in: "+ checkIn_date +" - "+ checkIn_time, 50, 240, paint);
        canvas.drawText("Check-out: "+ checkOut_date +" - "+ checkOut_time, 50, 260, paint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(14f);
        canvas.drawText("Description", 50, 320, titlePaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(14f);
        canvas.drawText("Quantity", width/2, 320, titlePaint);

        titlePaint.setTextAlign(Paint.Align.RIGHT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(14f);
        canvas.drawText("Price", width-50, 320, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Room Details", 50, 340, paint);
        canvas.drawText("Cottage Details", 50, 360, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("0", width/2, 340, paint);
        canvas.drawText("0", width/2, 360, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("0", width-50, 340, paint);
        canvas.drawText("0", width-50, 360, paint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(16f);
        canvas.drawText("Total Amount: "+ total, 50, 440, titlePaint);
        canvas.drawText("Payment Status: "+ payment_stat, 50, 460, titlePaint);
        canvas.drawText("GCash Number: "+ GCashNum, 50, 480, titlePaint);
        canvas.drawText("Reference Number: "+ refNumber, 50, 500, titlePaint);
        canvas.drawText("Paid: ", 50, 520, titlePaint);
        canvas.drawText("Balance: ", 50, 540, titlePaint);

        document.finishPage(page);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

        String InvoiceName = "fullname"+"_"+dateFormat.format(date);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+folder_name;

        File file = new File(path, InvoiceName+".pdf");

        try {
            document.writeTo(Files.newOutputStream(file.toPath()));
            //Toast.makeText(activity, "Booking is confirmed", Toast.LENGTH_SHORT).show();

            Uri invoice_Name = Uri.fromFile(new File(path + "/" + file.getName()));
            uploadInvoiceFile(file, invoice_Name);

        } catch (IOException e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

  /*  public String getInvoiceUrl() {
        return invoiceUrl;
    }*/

    public void createFolder() {
        File file = new File(Environment.getExternalStorageDirectory(), folder_name);
        if (!file.exists()){
            file.mkdirs();
            if (file.isDirectory()){
                Toast.makeText(activity, "Folder Created", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(activity, "Folder not Created", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setUploadCompleteListener(OnUploadCompleteListener listener) {
        this.uploadCompleteListener = listener;
    }

    private void uploadInvoiceFile(File file, Uri invoiceUri) {
        // Perform the upload to Firebase Storage
        // ...

        // After the upload is successful, retrieve the download URL

        StorageReference invoiceReference = FirebaseStorage.getInstance().getReference("Receipts");

        StorageReference reference = invoiceReference.child(file.getName());
        reference.putFile(invoiceUri)
                .addOnSuccessListener(taskSnapshot -> {
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String invoiceUrl = uri.toString();
                        uploadCompleteListener.onUploadComplete(invoiceUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public interface OnUploadCompleteListener {
        void onUploadComplete(String invoiceUrl);
    }

}
