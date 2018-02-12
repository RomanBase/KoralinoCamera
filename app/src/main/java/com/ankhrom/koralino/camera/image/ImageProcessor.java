package com.ankhrom.koralino.camera.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import com.ankhrom.base.Base;
import com.ankhrom.base.common.statics.BitmapHelper;

import java.nio.ByteBuffer;

/**
 * Created by R' on 1/14/2018.
 */

public class ImageProcessor {

    private static final float CONTRAST_MAX = 3.5f;
    private static final float BRIGHTNESS_MAX = 0.50f;

    private RawImage rawImage;
    private RawImage tempImage;
    private static RawImage mask;
    private RawImage.RawColor frameColor;

    private float contrast;
    private float brightness;

    private float maskOpacity = 1.0f;

    public ImageProcessor(Context context, Image image) {

        byte[] bytes = getBytes(image);
        image.close();

        Bitmap bitmap = BitmapHelper.loadBitmap(bytes);

        rawImage = new RawImage(bitmap);
        rawImage = new RawImage(bitmap = BitmapHelper.resize(getOriginBitmap(), 512, 512));

        tempImage = new RawImage(rawImage);
        frameColor = new RawImage.RawColor(255, 255, 255);

        bitmap.recycle();

        if (mask == null) {
            Bitmap frame = BitmapHelper.resize(BitmapHelper.loadBitmap(context, "image_frame.png"), rawImage.width, rawImage.height);
            mask = new RawImage(frame);
            frame.recycle();
        }
    }

    public void setContrast(float contrast) {
        this.contrast = CONTRAST_MAX * contrast;
    }

    public void setBrightness(float brightness) {
        this.brightness = BRIGHTNESS_MAX * brightness;
    }

    public void setMaskOpacity(float maskOpacity) {
        this.maskOpacity = maskOpacity;
    }

    public Bitmap updateImage() {

        long timestamp = System.currentTimeMillis();

        for (int i = 0; i < rawImage.data.length; i++) {
            RawImage.RawColor rawColor = rawImage.data[i];
            RawImage.RawColor tempColor = tempImage.data[i];
            RawImage.RawColor maskColor = mask.data[i];

            tempColor.r = updateColor(rawColor.r, brightness, contrast, frameColor.r, maskColor.r);
            tempColor.g = updateColor(rawColor.g, brightness, contrast, frameColor.g, maskColor.g);
            tempColor.b = updateColor(rawColor.b, brightness, contrast, frameColor.b, maskColor.b);
        }

        Base.logE("IMAGE", "Image process time: " + String.valueOf(System.currentTimeMillis() - timestamp));

        return getProcessedBitmap();
    }

    public Bitmap getProcessedBitmap() {

        return getBitmap(tempImage);
    }

    public Bitmap getOriginBitmap() {

        return getBitmap(rawImage);
    }

    private int updateColor(int color, float brightness, float contrast, float frameColor, float mask) {

        brightness *= 255.0f;
        contrast += 1.0f;

        float output = color;

        output = (output - 127.5f) * contrast + 127.5f;
        output += brightness;

        return interpolate(output, frameColor, (mask * maskOpacity) / 255.0f);
    }

    private int interpolate(float a, float b, float t) {

        return (int) (a + (b - a) * t);
    }

    private byte[] getBytes(Image image) {

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        return bytes;
    }

    private Bitmap getBitmap(RawImage image) {

        Bitmap bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(image.getBytes()));

        return bitmap;
    }

    public void close() {

        rawImage = null;
        tempImage = null;

        System.gc();
    }
}
