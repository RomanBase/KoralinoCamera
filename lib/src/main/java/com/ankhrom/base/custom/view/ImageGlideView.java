package com.ankhrom.base.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.ankhrom.base.R;
import com.ankhrom.base.animators.BaseAnim;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class ImageGlideView extends AppCompatImageView { // TODO: 27/04/16 transformations, thumbs

    protected boolean isCacheEnabled = true;
    protected int placeholder = R.drawable.placeholder;
    protected int errorPlaceholder = placeholder;

    public ImageGlideView(Context context) {
        super(context);
    }

    public ImageGlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageGlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageGlideView, 0, 0);
        try {
            placeholder = ta.getResourceId(R.styleable.ImageGlideView_placeholder, placeholder);
            errorPlaceholder = ta.getResourceId(R.styleable.ImageGlideView_placeholder_err, errorPlaceholder);
            isCacheEnabled = ta.getBoolean(R.styleable.ImageGlideView_cache, isCacheEnabled);
        } finally {
            ta.recycle();
        }

        if (isInEditMode()) {
            setImageResource(placeholder);
        }
    }

    @Override
    public void setImageURI(Uri uri) {

        loadGlide(Glide.with(getContext()).load(uri));
    }

    @Override
    public void setImageResource(int resId) {

        loadGlide(Glide.with(getContext()).load(resId));
    }

    public void setImageFile(File file) {

        loadGlide(Glide.with(getContext()).load(file));
    }

    public void setImageUrl(String url) {

        loadGlide(Glide.with(getContext()).load(url));
    }

    protected Target<?> loadGlide(DrawableTypeRequest glide) {

        if (!isCacheEnabled) {
            glide.skipMemoryCache(true);
        } else {
            glide.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        }

        return initDrawableRequest(glide).into(this);
    }

    protected DrawableRequestBuilder<?> initDrawableRequest(DrawableTypeRequest glide) {

        return glide
                .placeholder(placeholder)
                .error(errorPlaceholder)
                .crossFade(BaseAnim.DURATION);
    }
}
