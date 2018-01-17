package com.ankhrom.koralino.camera.image;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.ankhrom.base.common.statics.BitmapHelper;

import java.nio.ByteBuffer;

/**
 * Created by R' on 1/14/2018.
 */

public class ImageProcessor {

    private static final float CONTRAST_MAX = 7.5f;
    private static final float BRIGHTNESS_MAX = 0.675f;

    private RawImage rawImage;
    private RawImage tempImage;

    private float contrast;
    private float brightness;

    public ImageProcessor(Image image) {

        Bitmap bitmap = BitmapHelper.loadBitmap(getBytes(image));

        rawImage = new RawImage(bitmap);
        tempImage = new RawImage(rawImage);

        bitmap.recycle();
        image.close();
    }

    public void setContrast(float contrast) {
        this.contrast = CONTRAST_MAX * contrast;
    }

    public void setBrightness(float brightness) {
        this.brightness = BRIGHTNESS_MAX * brightness;
    }

    public Bitmap updateImage() {

        long timestamp = System.currentTimeMillis();

        float bound = (float) rawImage.width * 0.125f;

        for (int i = 0; i < rawImage.data.length; i++) {
            RawImage.RawColor rawColor = rawImage.data[i];
            RawImage.RawColor tempColor = tempImage.data[i];
/*
            int row = i % rawImage.width;
            int column = i - (row * rawImage.width);
            float brightnessProgress = 0.0f;

            if (row < bound) {
                brightnessProgress = 1.0f - (float) row / bound;
            }

            if (column < bound) {
                float progress = 1.0f - (float) column / bound;
                //brightnessProgress = Math.max(brightnessProgress, progress);
            }

            if (row > rawImage.width - bound) {
                float progress = bound / (float) (rawImage.width - row);
                brightnessProgress = Math.max(brightnessProgress, progress);
            }

            if (column > rawImage.height - bound) {
                float progress = bound / (float) (rawImage.height - column);
                //brightnessProgress = Math.max(brightnessProgress, progress);
            }
*/
            int brightnessBump = 0; //(int) (255.0f * brightnessProgress);

            tempColor.r = updateColor(rawColor.r, brightness, contrast) + brightnessBump;
            tempColor.g = updateColor(rawColor.g, brightness, contrast) + brightnessBump;
            tempColor.b = updateColor(rawColor.b, brightness, contrast) + brightnessBump;
        }

        Log.d("IMAGE", "Image process time: " + String.valueOf(System.currentTimeMillis() - timestamp));

        return getProcessedBitmap();
    }

    public Bitmap getProcessedBitmap() {

        return getBitmap(tempImage);
    }

    public Bitmap getOriginBitmap() {

        return getBitmap(rawImage);
    }

    private int updateColor(int color, float brightness, float contrast) {

        brightness *= 255.0f;
        contrast += 1.0f;

        float output = color;

        output = (output - 127.5f) * contrast + 127.5f;
        output += brightness;

        return (int) output;
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
