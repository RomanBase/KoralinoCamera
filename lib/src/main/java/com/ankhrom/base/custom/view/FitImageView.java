package com.ankhrom.base.custom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FitImageView extends ImageView {

    public FitImageView(Context context) {
        super(context);
    }

    public FitImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FitImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Drawable img = getDrawable();
        if (img == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int imageWidth = img.getIntrinsicWidth();
        int imageHeight = img.getIntrinsicHeight();
        float ratio = ((float) imageWidth / (float) imageHeight);
        if (ratio == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthSpec;
        int heightSpec;
        if (widthSize > 0) {
            widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec((int) ((float) widthSize / ratio), MeasureSpec.EXACTLY);
        } else if (heightSize > 0) {
            widthSpec = MeasureSpec.makeMeasureSpec((int) ((float) heightSize * ratio), MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else {
            widthSpec = widthMeasureSpec;
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthSpec, heightSpec);
    }
}
