package com.ankhrom.koralino.camera.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.view.View;

import com.ankhrom.base.common.statics.AppsHelper;
import com.ankhrom.base.common.statics.BitmapHelper;
import com.ankhrom.base.common.statics.FileHelper;
import com.ankhrom.base.common.statics.FragmentHelper;
import com.ankhrom.base.custom.args.InitArgs;
import com.ankhrom.base.interfaces.OnValueChangedListener;
import com.ankhrom.base.model.Model;
import com.ankhrom.base.observable.ObservableString;
import com.ankhrom.base.observable.SeekBarObservable;
import com.ankhrom.base.viewmodel.BaseViewModel;
import com.ankhrom.koralino.camera.BR;
import com.ankhrom.koralino.camera.Prefs;
import com.ankhrom.koralino.camera.R;
import com.ankhrom.koralino.camera.databinding.ImagePreviewBinding;
import com.ankhrom.koralino.camera.image.ImageProcessor;

/**
 * Created by R' on 1/17/2018.
 */

public class PreviewViewModel extends BaseViewModel<ImagePreviewBinding, Model> {

    public static float brightnessValue = 0.15f;
    public static float contrastValue = 0.25f;

    public final SeekBarObservable brightness = new SeekBarObservable();
    public final SeekBarObservable contrast = new SeekBarObservable();
    public final ObservableField<Bitmap> bitmap = new ObservableField<>();
    public final ObservableString version = new ObservableString(String.valueOf(0));
    public final ObservableBoolean isMaskEnabled = new ObservableBoolean(true);

    private ImageProcessor imageProcessor;
    private ImageReader reader;

    @Override
    public void init(InitArgs args) {
        super.init(args);

        reader = args.getArg(ImageReader.class);
    }

    @Override
    public boolean onBackPressed() {

        if (isLoading.get()) {
            return true;
        }

        close();

        return true;
    }

    @Override
    public void onInit() {

        if (imageProcessor != null) {
            return;
        }

        isLoading.set(true);
        init();
        isLoading.set(false);
    }

    private final OnValueChangedListener<Integer> onBrightnessChangedListener = new OnValueChangedListener<Integer>() {
        @Override
        public void onValueChanged(Integer value) {

            imageProcessor.setBrightness(brightnessValue = brightness.getPercentage());
            updateImage();
        }
    };

    private final OnValueChangedListener<Integer> onContrastChangedListener = new OnValueChangedListener<Integer>() {
        @Override
        public void onValueChanged(Integer value) {

            imageProcessor.setContrast(contrastValue = contrast.getPercentage());
            updateImage();
        }
    };

    private void init() {

        Image image = reader.acquireLatestImage();
        imageProcessor = new ImageProcessor(getContext(), image);

        reader.close();
        image.close();
        reader = null;

        imageProcessor.setBrightness(brightnessValue);
        imageProcessor.setContrast(contrastValue);

        brightness.setPercentage(brightnessValue);
        contrast.setPercentage(contrastValue);

        brightness.setOnPostValueChangedListener(onBrightnessChangedListener);
        contrast.setOnPostValueChangedListener(onContrastChangedListener);

        updateImage();
    }

    private void updateImage() {

        Bitmap currentBitmap = bitmap.get();

        bitmap.set(imageProcessor.updateImage());

        if (currentBitmap != null && !currentBitmap.isRecycled()) {
            currentBitmap.recycle();
        }
    }

    public void onSavePressed(View view) {

        saveImage(true);
        close();
    }

    public void onSaveVersionPressed(View view) {

        saveImage(false);
    }

    public void onAutoAdjustPressed(View view) {

        brightnessValue = 0.15f;
        contrastValue = 0.25f;

        brightness.setValue((int) (brightnessValue * 100.0f));
        contrast.setValue((int) (contrastValue * 100.0f));

        imageProcessor.setBrightness(brightnessValue);
        imageProcessor.setContrast(contrastValue);

        updateImage();
    }

    private void saveImage(boolean recycle) {

        Bitmap bitmap = this.bitmap.get();

        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        int v = Integer.parseInt(version.get());
        version.set(String.valueOf(v + 1));

        new ImageFileThread(bitmap, v, recycle);
    }

    public void onFramePressed(View view) {

        isMaskEnabled.set(!isMaskEnabled.get());

        imageProcessor.setMaskOpacity(isMaskEnabled.get() ? 1.0f : 0.0f);

        updateImage();
    }

    public void onGalleryPressed(View view) {

        try {
            AppsHelper.startApp(getContext(), Prefs.GALERY_APP);
        } catch (Exception ex) {
            ex.printStackTrace();
            AppsHelper.showInGooglePlay(getContext(), Prefs.GALERY_APP);
        }
    }

    public void onClosePressed(View view) {

        close();
    }

    private void close() {

        if (imageProcessor != null) {
            imageProcessor.close();
            imageProcessor = null;
        }

        bitmap.set(null);

        getNavigation().setPreviousViewModel();
        FragmentHelper.removePage(getContext(), this);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.image_preview;
    }

    @Override
    public int getBindingResource() {
        return BR.VM;
    }

    public static class ImageFileThread {

        public ImageFileThread(final Bitmap bitmap, final int version, final boolean recycle) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String file = Prefs.getFilePath(version);
                    FileHelper.sdWriteFile(file, BitmapHelper.getBitmapPNG(bitmap));

                    if (recycle) {
                        bitmap.recycle();
                    }
                }
            }).start();
        }
    }
}
