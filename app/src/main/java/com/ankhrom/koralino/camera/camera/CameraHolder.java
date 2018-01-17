package com.ankhrom.koralino.camera.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;

import java.util.Arrays;

/**
 * Created by R' on 1/13/2018.
 */

public class CameraHolder {

    private boolean isActive;

    private CameraDevice camera;
    private CameraManager manager;
    private TextureHolder texture;
    private String cameraId;

    private final Context context;

    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    protected CameraCaptureSession captureSession;
    protected Handler handler;

    public Size previewSize;
    public Size outputSize;

    public CameraHolder(@NonNull Context context) {
        this.context = context;

        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        if (manager == null) {
            throw new RuntimeException("NOT SUPPORTED");
        }

        try {
            String[] cameraIds = manager.getCameraIdList();
            cameraId = cameraIds[0];

            CameraCharacteristics prefs = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = prefs.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            assert map != null;

            previewSize = getPreviewSize(map.getOutputSizes(SurfaceTexture.class));
            outputSize = getOutputSize(map.getOutputSizes(ImageFormat.JPEG));

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void openCamera(TextureHolder texture) {

        this.texture = texture;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {

            manager.openCamera(cameraId, deviceStateListener, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void reOpenCamera() {

        if (texture != null) {
            texture.setUpPreview();
        }
    }

    public void closeCamera() {

        isActive = false;
        if (camera == null) {
            return;
        }

        camera.close();
        camera = null;
    }

    private Size getPreviewSize(Size[] sizes) {

        return sizes[9];
    }

    private Size getOutputSize(Size[] sizes) {

        return sizes[0];
    }

    public CaptureRequest.Builder requestCapture(Surface surface) {

        try {
            captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            camera.createCaptureSession(Arrays.asList(surface), captureStateListener, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return captureRequestBuilder;
    }

    protected void update() {

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, handler = new Handler(context.getMainLooper()));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback deviceStateListener = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            CameraHolder.this.camera = camera;
            isActive = true;

            if (texture != null) {
                texture.setUpPreview();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            closeCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            closeCamera();
        }
    };

    private final CameraCaptureSession.StateCallback captureStateListener = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            captureSession = session;
            update();
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            session.close();
        }
    };

    public void flash(boolean enable) {

        try {
            manager.setTorchMode(cameraId, enable);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public CameraDevice getCamera() {
        return camera;
    }

    public Handler getHandler() {
        return handler;
    }

    public boolean isActive() {
        return isActive;
    }
}
