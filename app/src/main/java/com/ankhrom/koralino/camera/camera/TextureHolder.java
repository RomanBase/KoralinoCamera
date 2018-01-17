package com.ankhrom.koralino.camera.camera;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;

import com.ankhrom.koralino.camera.MainActivity;

/**
 * Created by R' on 1/13/2018.
 */

public class TextureHolder implements TextureView.SurfaceTextureListener {

    private final CameraHolder camera;
    private final TextureView texture;

    private int width;
    private int height;

    public TextureHolder(CameraHolder camera, TextureView view) {
        this.camera = camera;
        this.texture = view;

        view.setSurfaceTextureListener(this);
    }

    private void setUpTransform() {

        int rotation = MainActivity.ref.getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, width, height);
        RectF bufferRect = new RectF(0, 0, camera.previewSize.getHeight(), camera.previewSize.getWidth());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);

            float scale = Math.max((float) height / camera.previewSize.getHeight(), (float) width / camera.previewSize.getWidth());

            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }

        texture.setTransform(matrix);
    }

    public void setUpPreview() {

        SurfaceTexture texture = this.texture.getSurfaceTexture();

        texture.setDefaultBufferSize(camera.previewSize.getWidth(), camera.previewSize.getHeight());
        Surface surface = new Surface(texture);

        camera.requestCapture(surface);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        this.width = width;
        this.height = height;

        setUpTransform();

        camera.openCamera(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public TextureView getTexture() {
        return texture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
