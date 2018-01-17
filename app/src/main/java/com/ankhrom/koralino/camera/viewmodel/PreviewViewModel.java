package com.ankhrom.koralino.camera.viewmodel;

import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.View;

import com.ankhrom.base.common.statics.BitmapHelper;
import com.ankhrom.base.common.statics.FileHelper;
import com.ankhrom.base.common.statics.FragmentHelper;
import com.ankhrom.base.custom.args.InitArgs;
import com.ankhrom.base.interfaces.OnValueChangedListener;
import com.ankhrom.base.model.Model;
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

    private ImageProcessor imageProcessor;

    @Override
    public void init(InitArgs args) {
        super.init(args);

        imageProcessor = new ImageProcessor(args.getArg(Image.class));
    }

    @Override
    public void onInit() {

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

        if (imageProcessor == null) {
            close();
            return;
        }

        Bitmap bitmap = this.bitmap.get();

        if (bitmap != null && !bitmap.isRecycled()) {
            FileHelper.sdWriteFile(Prefs.getFilePath(), BitmapHelper.getBitmapPNG(bitmap));
        }

        close();
    }

    public void onClosePressed(View view) {

        close();
    }

    private void close() {

        if (imageProcessor != null) {
            imageProcessor.close();
            imageProcessor = null;
        }

        Bitmap bitmap = this.bitmap.get();

        if (bitmap != null && !bitmap.isRecycled()) {
            this.bitmap.set(null);
            bitmap.recycle();
        }

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
}
