package com.ankhrom.base.custom.view;


import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ImageGlideRoundView extends ImageGlideView {

    public ImageGlideRoundView(Context context) {
        super(context);
    }

    public ImageGlideRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGlideRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected DrawableRequestBuilder<?> initDrawableRequest(DrawableTypeRequest glide) {
        return super.initDrawableRequest(glide).bitmapTransform(new CropCircleTransformation(getContext()));
    }
}
