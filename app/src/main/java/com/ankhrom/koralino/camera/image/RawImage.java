package com.ankhrom.koralino.camera.image;

import android.graphics.Bitmap;

/**
 * Created by R' on 1/14/2018.
 */

public class RawImage {

    public RawColor[] data;
    public int width;
    public int height;

    public RawImage(Bitmap bitmap) {

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        int offset = height - width;

        height -= offset;
        offset /= 2;

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, offset, width, height);

        data = new RawColor[pixels.length];

        for (int i = 0; i < pixels.length; i++) {

            int pixel = pixels[i];
            RawColor color = new RawColor();

            color.r = (pixel >> 16) & 0xff;
            color.g = (pixel >> 8) & 0xff;
            color.b = pixel & 0xff;

            data[i] = color;
        }
    }

    public RawImage(RawImage image) {

        data = new RawColor[image.data.length];
        width = image.width;
        height = image.height;

        for (int i = 0; i < data.length; i++) {
            data[i] = new RawColor(image.data[i]);
        }
    }

    public byte[] getBytes() {

        int stride = 4;
        byte[] bytes = new byte[data.length * stride];

        for (int i = 0; i < data.length; i++) {

            RawColor color = data[i];
            color.normalize();

            bytes[stride * i] = (byte) color.r;
            bytes[stride * i + 1] = (byte) color.g;
            bytes[stride * i + 2] = (byte) color.b;
            bytes[stride * i + 3] = (byte) 255;
        }

        return bytes;
    }

    public static final class RawColor {

        public int r;
        public int g;
        public int b;

        public RawColor() {

        }

        public RawColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public RawColor(RawColor color) {
            r = color.r;
            g = color.g;
            b = color.b;
        }

        public void normalize() {

            r = Math.max(0, Math.min(r, 255));
            g = Math.max(0, Math.min(g, 255));
            b = Math.max(0, Math.min(b, 255));
        }
    }
}
