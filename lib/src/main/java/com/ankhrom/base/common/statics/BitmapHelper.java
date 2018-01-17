package com.ankhrom.base.common.statics;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap operations helper.
 * loading bitmaps from any resource
 * compresing bitmaps into png, etc.
 */
public final class BitmapHelper {

    /**
     * create bitmap from resource
     *
     * @param resourceId resource - eg. R.drawable.glid
     * @return a bitmap
     */
    public static Bitmap loadBitmap(Context context, int resourceId) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;    // No pre-scaling

        return BitmapFactory.decodeResource(context.getResources(), resourceId, options);
    }

    /**
     * create bitmap from assets file
     *
     * @param file assets file path
     * @return a bitmap
     */
    public static Bitmap loadBitmap(Context context, String file) {

        Bitmap bitmap = null;

        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;    // No pre-scaling

            InputStream is = context.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            bitmap = BitmapFactory.decodeStream(is, null, options);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * create bitmap from byte array
     *
     * @param bytes byte[] array of bitmap's data
     * @return a bitmap
     */
    public static Bitmap loadBitmap(byte[] bytes) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;    // No pre-scaling

        return BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, options); //close ByteArrayInputStream is not required
    }


    /**
     * @param bitmap Bitmap to compress
     * @return byte[] array of bitmap's data
     */
    public static byte[] getBitmapPNG(Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //close ByteArrayOutupStream is not required
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        return bytes.toByteArray();
    }

    /**
     * @param bitmap Bitmap to compress
     * @return byte[] array of bitmap's data
     */
    public static byte[] getBitmapJPG(Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //close ByteArrayOutupStream is not required
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);

        return bytes.toByteArray();
    }

    /**
     * @param bitmap  Bitmap to compress
     * @param quality 0 - 100 (0 - low quality, 100 - max quality) PNG(lossless) will ignore quality value
     * @return byte[] array of bitmap's data
     */
    public static byte[] getBitmapJPG(Bitmap bitmap, int quality) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //close ByteArrayOutupStream is not required
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes);

        return bytes.toByteArray();
    }

    /**
     * @param bitmap  Bitmap to compress
     * @param format  PNG, JPEG, WEBR
     * @param quality 0 - 100 (0 - low quality, 100 - max quality) PNG(lossless) will ignore quality value
     * @return byte[] array of bitmap's data
     */
    public static byte[] getBitmapBytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //close ByteArrayOutupStream is not required
        bitmap.compress(format, quality, bytes);

        return bytes.toByteArray();
    }

    /**
     * create resized bitmap
     *
     * @param bitmap    Bitmap to resize
     * @param newWidth  requested width of bitmap
     * @param newHeight requested height of bitmap
     * @return resized bitmap
     */
    public static Bitmap resize(Bitmap bitmap, float newWidth, float newHeight) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = newWidth / (float) width;
        float scaleHeight = newHeight / (float) height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        bitmap.recycle();

        return out;
    }

    /**
     * create resized bitmap
     *
     * @param bitmap    Bitmap to resize
     * @param newWidth  requested width of bitmap
     * @param newHeight requested height of bitmap
     * @param rotation  requested rotation in degrees (clockwise)
     * @return resized bitmap
     */
    public static Bitmap transform(Bitmap bitmap, float newWidth, float newHeight, float rotation) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = newWidth / (float) width;
        float scaleHeight = newHeight / (float) height;

        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        matrix.postScale(scaleWidth, scaleHeight);


        Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        bitmap.recycle();

        return out;
    }

}
