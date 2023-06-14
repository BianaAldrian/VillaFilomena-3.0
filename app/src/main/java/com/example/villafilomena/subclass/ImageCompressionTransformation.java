package com.example.villafilomena.subclass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

public class ImageCompressionTransformation implements Transformation {

    private final int compressionQuality;

    public ImageCompressionTransformation(int compressionQuality) {
        this.compressionQuality = compressionQuality;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap compressedBitmap = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            source.compress(Bitmap.CompressFormat.JPEG, compressionQuality, outputStream);
            byte[] bitmapData = outputStream.toByteArray();
            compressedBitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        } catch (Exception e) {
            Log.e("ImageCompression", "Failed to compress image: " + e.getMessage());
        }

        if (compressedBitmap != null && compressedBitmap != source) {
            source.recycle();
        }

        return compressedBitmap;
    }

    @Override
    public String key() {
        return "imageCompression";
    }
}

