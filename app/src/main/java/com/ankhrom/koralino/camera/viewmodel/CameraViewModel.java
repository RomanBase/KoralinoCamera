package com.ankhrom.koralino.camera.viewmodel;

import android.media.ImageReader;
import android.view.View;

import com.ankhrom.base.model.Model;
import com.ankhrom.base.viewmodel.BaseViewModel;
import com.ankhrom.koralino.camera.BR;
import com.ankhrom.koralino.camera.R;
import com.ankhrom.koralino.camera.camera.CameraHolder;
import com.ankhrom.koralino.camera.camera.CameraPicture;
import com.ankhrom.koralino.camera.camera.OnImageCapturedListener;
import com.ankhrom.koralino.camera.camera.TextureHolder;
import com.ankhrom.koralino.camera.databinding.CameraMainBinding;

/**
 * Created by R' on 1/17/2018.
 */

public class CameraViewModel extends BaseViewModel<CameraMainBinding, Model> implements OnImageCapturedListener {

    private CameraHolder camera;
    private TextureHolder texture;

    @Override
    protected void onCreateViewBinding(CameraMainBinding binding) {

        camera = new CameraHolder(getContext());
        texture = new TextureHolder(camera, binding.cameraTexture);
    }

    public void onCapturePressed(View view) {

        new CameraPicture(getBaseActivity()).takePicture(camera, texture, this, false);
    }

    @Override
    public void onImageCaptured(ImageReader reader) {

        isLoading.set(true);

        addViewModel(PreviewViewModel.class, reader.acquireLatestImage());
        reader.close();

        isLoading.set(false);
    }

    @Override
    public void onViewStackChanged(boolean isBackStacked, boolean isVisible) {

        if (!isBackStacked && isVisible && camera != null) {
            camera.reOpenCamera();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.camera_main;
    }

    @Override
    public int getBindingResource() {
        return BR.VM;
    }
}
