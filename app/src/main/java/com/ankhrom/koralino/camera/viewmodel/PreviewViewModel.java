package com.ankhrom.koralino.camera.viewmodel;

import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.Image;
import android.view.View;

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

import java.io.IOException;

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

    private ImageProcessor imageProcessor;
    private Image image;

    @Override
    public void init(InitArgs args) {
        super.init(args);

        image = args.getArg(Image.class);
    }

    @Override
    public void onInit() {

        imageProcessor = new ImageProcessor(getContext(), image);

        image.close();
        image = null;

        imageProcessor.setBrightness(brightnessValue);
        imageProcessor.setContrast(contrastValue);

        brightness.setPercentage(brightnessValue);
        contrast.setPercentage(contrastValue);

        brightness.setOnPostValueChangedListener(onBrightnessChangedListener);
        contrast.setOnPostValueChangedListener(onContrastChangedListener);

        bitmap.set(imageProcessor.updateImage());
    }

    private final OnValueChangedListener<Integer> onBrightnessChangedListener = new OnValueChangedListener<Integer>() {
        @Override
        public void onValueChanged(Integer value) {

            imageProcessor.setBrightness(brightnessValue = brightness.getPercentage());
            bitmap.set(imageProcessor.updateImage());
        }
    };

    private final OnValueChangedListener<Integer> onContrastChangedListener = new OnValueChangedListener<Integer>() {
        @Override
        public void onValueChanged(Integer value) {

            imageProcessor.setContrast(contrastValue = contrast.getPercentage());
            bitmap.set(imageProcessor.updateImage());
        }
    };

    public void onSavePressed(View view) {

        saveImage();
        close();
    }

    public void onSaveVersionPressed(View view) {

        saveImage();
    }

    public void onAutoAdjustPressed(View view) {

        brightnessValue = 0.15f;
        contrastValue = 0.25f;

        brightness.setValue((int) (brightnessValue * 100.0f));
        contrast.setValue((int) (contrastValue * 100.0f));

        imageProcessor.setBrightness(brightnessValue);
        imageProcessor.setContrast(contrastValue);

        bitmap.set(imageProcessor.updateImage());
    }

    private void saveImage() {

        Bitmap bitmap = this.bitmap.get();

        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        int v = Integer.parseInt(version.get());
        version.set(String.valueOf(v + 1));

        new ImageFileThread(bitmap, v);
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

        public ImageFileThread(final Bitmap bitmap, final int version) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String file = Prefs.getFilePath(version);

                    FileHelper.sdWriteFile(file, BitmapHelper.getBitmapPNG(bitmap));

                    try {
                        ExifInterface metadata = new ExifInterface(FileHelper.sdFileUri(file).toString());
                        metadata.setAttribute("owner", "Koralino");
                        metadata.setAttribute("name", file);
                        metadata.saveAttributes();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
